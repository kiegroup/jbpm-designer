/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import javax.enterprise.event.Event;

import org.apache.commons.io.IOUtils;
import org.drools.core.util.ConfFileUtils;
import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jbpm.designer.notification.DesignerWorkitemInstalledEvent;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.workbench.events.NotificationEvent;

public class ServiceRepoUtils {

    private static final Logger _logger = LoggerFactory
            .getLogger(ServiceRepoUtils.class);

    public static void installWorkItem(Map<String, WorkDefinitionImpl> workitemsFromRepo,
                                       String key,
                                       String uuid,
                                       Repository repository,
                                       VFSService vfsServices,
                                       Event<DesignerWorkitemInstalledEvent> workitemInstalledEventEvent,
                                       Event<NotificationEvent> notification,
                                       POMService pomService,
                                       ProjectService projectService,
                                       MetadataService metadataService) throws IOException, FileAlreadyExistsException {

        String workitemDefinitionURL = workitemsFromRepo.get(key).getPath() + "/" + workitemsFromRepo.get(key).getName() + ".wid";
        String iconFileURL = workitemsFromRepo.get(key).getPath() + "/" + workitemsFromRepo.get(key).getIcon();
        String workItemDefinitionContent = ConfFileUtils.URLContentsToString(new URL(workitemDefinitionURL));
        String iconName = workitemsFromRepo.get(key).getIcon();
        String widName = workitemsFromRepo.get(key).getName();
        byte[] iconContent = null;
        try {
            iconContent = getImageBytes(new URL(iconFileURL)
                                                .openStream());
        } catch (Exception e1) {
            _logger.error("Could not read icon image: " + e1.getMessage());
        }
        // install wid and icon
        repository.deleteAssetFromPath(getRepositoryDir(uuid) + "/" + widName + ".wid");

        AssetBuilder widAssetBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        widAssetBuilder.name(widName)
                .location(getRepositoryDir(uuid))
                .type("wid")
                .content(workItemDefinitionContent);

        repository.createAsset(widAssetBuilder.getAsset());

        if (iconName != null && !iconName.isEmpty()) {
            AssetBuilder iconAssetBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
            String iconExtension = iconName.substring(iconName.lastIndexOf(".") + 1);
            String iconFileName = iconName.substring(0,
                                                     iconName.lastIndexOf("."));

            repository.deleteAssetFromPath(getRepositoryDir(uuid) + "/" + iconFileName + "." + iconExtension);

            iconAssetBuilder.name(iconFileName)
                    .location(getRepositoryDir(uuid))
                    .type(iconExtension)
                    .content(iconContent);

            repository.createAsset(iconAssetBuilder.getAsset());
        }

        if (vfsServices != null) {
            Path assetPath = vfsServices.get(uuid.replaceAll("\\s", "%20"));

            if (canUpdateConfigForWorkitem(workitemsFromRepo.get(key))) {
                workitemInstalledEventEvent.fire(new DesignerWorkitemInstalledEvent(
                        assetPath,
                        "",
                        workitemsFromRepo.get(key).getDefaultHandler(),
                        workitemsFromRepo.get(key).getName(),
                        ""
                ));
            } else {
                notification.fire(new NotificationEvent("Installed workitem cannot be registered in project configuration.",
                                                        NotificationEvent.NotificationType.WARNING));
            }

            if (canUpdateProjectPomForWorkitem(workitemsFromRepo.get(key))) {
                Project assetProject = projectService.resolveProject(assetPath);
                POM projectPOM = pomService.load(assetProject.getPomXMLPath());
                if (projectPOM != null) {
                    Dependencies projectDepends = projectPOM.getDependencies();
                    Dependencies validDependsFromWorkitem = getValidDependenciesForWorkitem(projectDepends,
                                                                                            workitemsFromRepo.get(key));
                    if (validDependsFromWorkitem != null && validDependsFromWorkitem.size() > 0) {
                        for (Dependency workitemDependency : validDependsFromWorkitem) {
                            projectPOM.getDependencies().add(workitemDependency);
                        }

                        pomService.save(assetProject.getPomXMLPath(),
                                        projectPOM,
                                        metadataService.getMetadata(assetProject.getPomXMLPath()),
                                        "System updated dependencies from workitem configuration.",
                                        false);
                    }
                }
            }
        }
    }

    private static Dependencies getValidDependenciesForWorkitem(Dependencies projectDepends,
                                                                WorkDefinitionImpl workitem) {
        Dependencies validDepends = new Dependencies();

        Dependencies workItemDepends = getWorkItemDepends(workitem);
        for (Dependency depends : workItemDepends) {
            if (!projectDepends.containsDependency(depends)) {
                validDepends.add(depends);
            }
        }

        return validDepends;
    }

    private static Dependencies getWorkItemDepends(WorkDefinitionImpl workitem) {
        Dependencies workItemDepends = new Dependencies();
        for (String mavenDepends : workitem.getMavenDependencies()) {
            String[] dependsParts = mavenDepends.split("\\s*:\\s*");
            Dependency newDepend = new Dependency();
            if (dependsParts.length == 3) {
                newDepend.setGroupId(dependsParts[0]);
                newDepend.setArtifactId(dependsParts[1]);
                newDepend.setVersion(dependsParts[2]);
                workItemDepends.add(newDepend);
            } else if (dependsParts.length == 4) {
                newDepend.setGroupId(dependsParts[0]);
                newDepend.setArtifactId(dependsParts[1]);
                newDepend.setVersion(dependsParts[2]);
                newDepend.setScope(dependsParts[3]);
                workItemDepends.add(newDepend);
            }
        }
        return workItemDepends;
    }

    private static byte[] getImageBytes(InputStream is) throws Exception {
        try {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new Exception("Error creating image byte array.");
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static String getRepositoryDir(String uuid) {
        int iStart = uuid.indexOf("//");
        iStart = uuid.indexOf('/',
                              iStart + 2);
        int iEnd = uuid.lastIndexOf('/');
        return uuid.substring(iStart,
                              iEnd);
    }

    public static boolean canUpdateProjectPomForWorkitem(WorkDefinitionImpl workitem) {
        return workitem != null &&
                workitem.getName() != null &&
                workitem.getName().trim().length() > 0 &&
                workitem.getMavenDependencies() != null &&
                workitem.getMavenDependencies().length > 0;
    }

    private static boolean canUpdateConfigForWorkitem(WorkDefinitionImpl workitem) {
        return workitem != null &&
                workitem.getName() != null &&
                workitem.getName().trim().length() > 0 &&
                workitem.getDefaultHandler() != null &&
                workitem.getDefaultHandler().trim().length() > 0;
    }
}
