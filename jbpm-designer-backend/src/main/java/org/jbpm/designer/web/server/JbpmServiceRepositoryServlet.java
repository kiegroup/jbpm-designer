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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jbpm.designer.notification.DesignerWorkitemInstalledEvent;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.jbpm.process.workitem.WorkItemRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.workbench.events.NotificationEvent;

@WebServlet(displayName = "JbpmServiceRepository", name = "JbpmServiceRepositoryServlet",
        urlPatterns = "/jbpmservicerepo")
public class JbpmServiceRepositoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger _logger = LoggerFactory
            .getLogger(JbpmServiceRepositoryServlet.class);
    private static final String displayRepoContent = "display";
    private static final String installRepoContent = "install";

    private IDiagramProfile profile;

    // this is here just for unit testing purpose
    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }

    @Inject
    private IDiagramProfileService _profileService = null;

    @Inject
    private Event<DesignerWorkitemInstalledEvent> workitemInstalledEventEvent;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private VFSService vfsServices;

    @Inject
    private POMService pomService;

    @Inject
    private ProjectService<? extends Project> projectService;

    @Inject
    private MetadataService metadataService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {
        String uuid = Utils.getUUID(req);
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String action = req.getParameter("action");
        String assetsToInstall = req.getParameter("asset");
        String categoryToInstall = req.getParameter("category");
        String repoURL = req.getParameter("repourl");

        if (repoURL == null || repoURL.length() < 1) {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            resp.getWriter().write("false");
            return;
        }

        try {
            URL url = new URL(repoURL);
            if (!(repoURL.startsWith("file:"))) {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5 * 1000);
                conn.setConnectTimeout(5 * 1000);
                conn.connect();
                if (conn.getResponseCode() != 200) {
                    resp.setCharacterEncoding("UTF-8");
                    resp.setContentType("application/json");
                    resp.getWriter().write("false");
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            resp.getWriter().write("false||" + e.getMessage());
            return;
        }

        if (repoURL.endsWith("/")) {
            repoURL = repoURL.substring(0,
                                        repoURL.length() - 1);
        }

        if (profile == null) {
            profile = _profileService.findProfile(req,
                                                  profileName);
        }
        Repository repository = profile.getRepository();

        Map<String, WorkDefinitionImpl> workitemsFromRepo = WorkItemRepository.getWorkDefinitions(repoURL);
        if (action != null && action.equalsIgnoreCase(displayRepoContent)) {
            if (workitemsFromRepo != null && workitemsFromRepo.size() > 0) {
                Map<String, List<String>> retMap = new HashMap<String, List<String>>();
                for (String key : workitemsFromRepo.keySet()) {
                    WorkDefinitionImpl wd = workitemsFromRepo.get(key);
                    List<String> keyList = new ArrayList<String>();
                    keyList.add(wd.getName() == null ? "" : wd.getName());
                    keyList.add(wd.getDisplayName() == null ? "" : wd.getDisplayName());

                    if (wd.getIcon() != null && wd.getIcon().trim().length() > 0) {
                        if (repoURL.startsWith("file:")) {
                            keyList.add(getFileIconEncoded(repoURL + "/" + wd.getName() + "/" + wd.getIcon()));
                        } else {
                            keyList.add(repoURL + "/" + wd.getName() + "/" + wd.getIcon());
                        }
                    } else {
                        _logger.warn("No icon specified. Showing default");
                        keyList.add(getFileIconEncoded(""));
                    }

                    keyList.add(wd.getCategory() == null ? "" : wd.getCategory());
                    keyList.add(wd.getExplanationText() == null ? "" : wd.getExplanationText());
                    keyList.add(repoURL + "/" + wd.getName() + "/" + wd.getDocumentation());
                    StringBuffer bn = new StringBuffer();
                    if (wd.getParameterNames() != null) {
                        String delim = "";
                        for (String name : wd.getParameterNames()) {
                            bn.append(delim).append(name);
                            delim = ",";
                        }
                    }
                    keyList.add(bn.toString());
                    StringBuffer br = new StringBuffer();
                    if (wd.getResultNames() != null) {
                        String delim = "";
                        for (String resName : wd.getResultNames()) {
                            br.append(delim).append(resName);
                            delim = ",";
                        }
                    }
                    keyList.add(br.toString());

                    keyList.add(wd.getDefaultHandler() == null ? "" : wd.getDefaultHandler());
                    retMap.put(key,
                               keyList);
                }
                JSONObject jsonObject = new JSONObject();
                for (Entry<String, List<String>> retMapKey : retMap.entrySet()) {
                    try {
                        if (retMapKey != null && retMapKey.getKey() != null) {
                            jsonObject.put(retMapKey.getKey(),
                                           retMapKey.getValue());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json");
                resp.getWriter().write(jsonObject.toString());
            } else {
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json");
                resp.getWriter().write("false");
                return;
            }
        } else if (action != null && action.equalsIgnoreCase(installRepoContent)) {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            if (workitemsFromRepo != null && workitemsFromRepo.size() > 0) {
                for (String key : workitemsFromRepo.keySet()) {
                    if (key != null &&
                            key.equals(assetsToInstall) &&
                            categoryToInstall.equals(workitemsFromRepo.get(key).getCategory())) {

                        try {
                            ServiceRepoUtils.installWorkItem(workitemsFromRepo,
                                                             key,
                                                             uuid,
                                                             repository,
                                                             vfsServices,
                                                             workitemInstalledEventEvent,
                                                             notification,
                                                             pomService,
                                                             projectService,
                                                             metadataService);
                        } catch (FileAlreadyExistsException e) {
                            _logger.warn("Workitem already installed.");
                            resp.setCharacterEncoding("UTF-8");
                            resp.setContentType("application/json");
                            resp.getWriter().write("alreadyinstalled");
                            return;
                        }
                    }
                }
            } else {
                _logger.error("Invalid or empty service repository.");
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("application/json");
                resp.getWriter().write("false");
                return;
            }
        }
    }

    private String getFileIconEncoded(String fileIconPath) {
        try {
            return javax.xml.bind.DatatypeConverter.printBase64Binary(
                    IOUtils.toByteArray(new FileInputStream(new File(fileIconPath.substring(5))))
            );
        } catch (Exception e) {
            try {
                // return default service icon
                String defaultServiceNodeIcon = getServletContext().getRealPath(ConfigurationProvider.getInstance().getDesignerContext() + "/defaults/defaultservicenodeicon.png");
                return javax.xml.bind.DatatypeConverter.printBase64Binary(
                        IOUtils.toByteArray(new FileInputStream(new File(defaultServiceNodeIcon)))
                );
            } catch (Exception ee) {
                _logger.error("Unable to load workitem icon: " + ee.getMessage());
                return "";
            }
        }
    }
}
