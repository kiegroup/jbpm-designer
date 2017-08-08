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
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.function.BiConsumer;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpsim.impl.BpsimFactoryImpl;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.bpmn2.Definitions;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonUnmarshaller;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.taskforms.BPMNFormBuilderManager;
import org.jbpm.designer.taskforms.TaskFormInfo;
import org.jbpm.designer.taskforms.TaskFormTemplateManager;
import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;

@WebServlet(displayName = "Taskforms", name = "TaskformsServlet",
        urlPatterns = "/taskforms")
public class TaskFormsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger _logger = LoggerFactory
            .getLogger(TaskFormsServlet.class);
    private static final String TASKFORMS_PATH = "taskforms";
    private static final String FORMMODELER_FILE_EXTENSION = "frm";
    private static final String FORMMODELER_PREVIEW_FILE_EXTENSION = "form";
    public static final String DESIGNER_PATH = ConfigurationProvider.getInstance().getDesignerContext();

    private IDiagramProfile profile;

    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }

    @Inject
    private IDiagramProfileService _profileService = null;

    @Inject
    protected BPMNFormBuilderManager formBuilderManager;

    @Inject
    private VFSService vfsServices;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String json = req.getParameter("json");
        String uuid = Utils.getUUID(req);
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String preprocessingData = req.getParameter("ppdata");
        String taskId = req.getParameter("taskid");
        String formType = req.getParameter("formtype");

        if (profile == null) {
            profile = _profileService.findProfile(req,
                                                  profileName);
        }
        Repository repository = profile.getRepository();

        Asset<String> processAsset = null;

        try {
            processAsset = repository.loadAsset(uuid);

            DroolsFactoryImpl.init();
            BpsimFactoryImpl.init();

            Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
            Definitions def = ((Definitions) unmarshaller.unmarshall(json,
                                                                     preprocessingData).getContents().get(0));

            Path myPath = vfsServices.get(uuid.replaceAll("\\s",
                                                          "%20"));

            TaskFormTemplateManager templateManager = new TaskFormTemplateManager(myPath,
                                                                                  formBuilderManager,
                                                                                  profile,
                                                                                  processAsset,
                                                                                  getServletContext().getRealPath(DESIGNER_PATH + TASKFORMS_PATH),
                                                                                  def,
                                                                                  taskId,
                                                                                  formType);
            templateManager.processTemplates();

            //storeInRepository(templateManager, processAsset.getAssetLocation(), repository);
            //displayResponse( templateManager, resp, profile );
            resp.setContentType("application/json");
            resp.getWriter().write(storeInRepository(templateManager,
                                                     processAsset.getAssetLocation(),
                                                     repository).toString());
        } catch (Exception e) {
            _logger.error(e.getMessage());
            //displayErrorResponse(resp, e.getMessage());
            resp.setContentType("text/plain");
            resp.getWriter().write("fail");
        }
    }

    public JSONArray storeInRepository(TaskFormTemplateManager templateManager,
                                       String location,
                                       Repository repository) throws Exception {
        JSONArray retArray = new JSONArray();
        List<TaskFormInfo> taskForms = templateManager.getTaskFormInformationList();
        for (TaskFormInfo taskForm : taskForms) {
            retArray.put(storeTaskForm(taskForm,
                                       location,
                                       repository));
        }

        return retArray;
    }

    public JSONObject storeTaskForm(TaskFormInfo taskForm,
                                    String location,
                                    Repository repository) throws Exception {
        try {
            JSONObject retObj = new JSONObject();

            // create the modeler form assets

            taskForm.getModelerOutputs().forEach(new BiConsumer<String, String>() {
                @Override
                public void accept(String extension,
                                   String content) {
                    try {
                        repository.deleteAssetFromPath(taskForm.getPkgName() + "/" + taskForm.getId() + "." + extension);
                        AssetBuilder modelerBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
                        modelerBuilder.name(taskForm.getId())
                                .location(location)
                                .type(extension)
                                .content(content.getBytes("UTF-8"));

                        repository.createAsset(modelerBuilder.getAsset());

                        Asset newModelerFormAsset = repository.loadAssetFromPath(taskForm.getPkgName() + "/" + taskForm.getId() + "." + extension);

                        String modelerUniqueId = newModelerFormAsset.getUniqueId();
                        if (Base64.isBase64(modelerUniqueId)) {
                            byte[] decoded = Base64.decodeBase64(modelerUniqueId);
                            try {
                                modelerUniqueId = new String(decoded,
                                                             "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        if (extension.equals(FORMMODELER_FILE_EXTENSION) || extension.equals(FORMMODELER_PREVIEW_FILE_EXTENSION)) {
                            retObj.put("formuri",
                                       modelerUniqueId);
                        }
                    } catch (Exception ex) {
                        _logger.error("Error creating form for: " + taskForm.getId() + "." + extension);
                    }
                }
            });

            return retObj;
        } catch (Exception e) {
            _logger.error(e.getMessage());
            return new JSONObject();
        }
    }
}
