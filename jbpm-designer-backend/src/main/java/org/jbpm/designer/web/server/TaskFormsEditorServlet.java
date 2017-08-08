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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.taskforms.BPMNFormBuilderManager;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.json.JSONObject;
import org.kie.workbench.common.forms.bpmn.BPMNFormBuilderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.rpc.SessionInfo;

@WebServlet(displayName = "TaskformsEditor", name = "TaskFormsEditorServlet",
        urlPatterns = "/taskformseditor")
public class TaskFormsEditorServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger _logger = LoggerFactory.getLogger(TaskFormsEditorServlet.class);
    private static final String FORMMODELER_FILE_EXTENSION = "form";
    private static final String FORMMODELER_PREVIEW_FILE_EXTENSION = "frm";
    private static final String TASKFORM_NAME_EXTENSION = "-taskform";
    protected static final String ACTION_LOAD = "load";
    protected static final String ACTION_SAVE = "save";

    private IDiagramProfile profile;

    BPMNFormBuilderService<Definitions> formBuilder;

    // this is here just for unit testing purpose
    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }

    @Inject
    private IDiagramProfileService _profileService = null;

    @Inject
    private VFSService vfsServices;

    @Inject
    private BPMNFormBuilderManager formBuilderManager;

    @Inject
    private SessionInfo sessionInfo;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String uuid = Utils.getUUID(req);
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String taskName = UriUtils.decode(Utils.getEncodedParam(req,
                                                                "taskname"));
        String taskFormValue = req.getParameter("tfvalue");
        String formType = req.getParameter("formtype");
        String json = req.getParameter("json");
        String preprocessingData = req.getParameter("ppdata");
        String taskId = req.getParameter("taskid");

        if (profile == null) {
            profile = _profileService.findProfile(req,
                                                  profileName);
        }
        Repository repository = profile.getRepository();

        Asset<String> processAsset = null;
        try {
            processAsset = repository.loadAsset(uuid);

            if (action != null && action.equals(ACTION_LOAD)) {
                resp.setContentType("text/html");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter pw = resp.getWriter();
                String taskResponse = getTaskFormFromRepository(formType,
                                                                taskName,
                                                                processAsset.getAssetLocation(),
                                                                repository,
                                                                json,
                                                                preprocessingData,
                                                                uuid,
                                                                taskId);
                pw.write(taskResponse);
            } else if (action != null && action.equals(ACTION_SAVE)) {
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter pw = resp.getWriter();
                try {
                    pw.write(storeTaskFormInRepository(formType,
                                                       taskName,
                                                       processAsset.getAssetLocation(),
                                                       taskFormValue,
                                                       repository).toString());
                } catch (Exception e) {
                    _logger.error("Exception during saving form: " + e.getMessage());
                    pw.write(new JSONObject().toString());
                }
            }
        } catch (Exception e) {
            PrintWriter pw = resp.getWriter();
            pw.write("error: " + e.getMessage());
        }
    }

    private JSONObject storeTaskFormInRepository(String formType,
                                                 String taskName,
                                                 String packageName,
                                                 String formValue,
                                                 Repository repository) throws Exception {
        if (formType.equals(FORMMODELER_FILE_EXTENSION) || formType.equals(FORMMODELER_PREVIEW_FILE_EXTENSION)) {
            repository.deleteAssetFromPath(packageName + "/" + taskName + TASKFORM_NAME_EXTENSION + "." + formType);

            AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
            builder.location(packageName)
                    .name(taskName + TASKFORM_NAME_EXTENSION)
                    .type(formType)
                    .content(formValue.getBytes("UTF-8"));

            repository.createAsset(builder.getAsset());

            Asset newFormAsset = repository.loadAssetFromPath(packageName + "/" + taskName + TASKFORM_NAME_EXTENSION + "." + formType);

            JSONObject retObj = new JSONObject();
            try {
                retObj.put("formid",
                           getDecodedId(newFormAsset.getUniqueId()));
            } catch (UnsupportedEncodingException e) {
                retObj.put("formid",
                           "false");
            }

            return retObj;
        } else {
            return new JSONObject();
        }
    }

    private String getTaskFormFromRepository(String formType,
                                             String taskName,
                                             String packageName,
                                             Repository repository,
                                             String json,
                                             String preprocessingData,
                                             String uuid,
                                             String taskId) {
        try {
            Asset<String> formAsset = repository.loadAssetFromPath(packageName + "/" + taskName + TASKFORM_NAME_EXTENSION + "." + formType);
            if (formType.equals(FORMMODELER_FILE_EXTENSION) || formType.equals(FORMMODELER_PREVIEW_FILE_EXTENSION)) {
                try {
                    return getAssetInfo(formAsset);
                } catch (UnsupportedEncodingException e) {
                    _logger.error("Error loading form: " + e.getMessage());
                }
            } else {
                _logger.error("Cannot load existing form for invalid form type: " + formType);
            }
        } catch (NoSuchFileException anfe) {
            try {
                String formValue = "";
                if (formType.equals(FORMMODELER_FILE_EXTENSION) || formType.equals(FORMMODELER_PREVIEW_FILE_EXTENSION)) {
                    formBuilder = getFormBuilder(formType);
                    if (formBuilder != null) {
                        DroolsFactoryImpl.init();
                        BpsimFactoryImpl.init();
                        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                        Definitions def = ((Definitions) unmarshaller.unmarshall(json,
                                                                                 preprocessingData).getContents().get(0));

                        Path myPath = vfsServices.get(uuid.replaceAll("\\s",
                                                                      "%20"));

                        org.uberfire.java.nio.file.Path kiePath = Paths.convert(myPath);
                        Path formPath = Paths.convert(kiePath.getParent().resolve(taskName + TASKFORM_NAME_EXTENSION + "." + formType));

                        formValue = formBuilder.buildFormContent(formPath,
                                                                 def,
                                                                 taskId);
                    } else {
                        _logger.warn("Unable to find form builder for form type: " + formType);
                    }

                    AssetBuilder assetBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
                    assetBuilder.location(packageName)
                            .name(taskName + TASKFORM_NAME_EXTENSION)
                            .type(formType)
                            .content(formValue.getBytes("UTF-8"));
                    repository.createAsset(assetBuilder.getAsset());

                    Asset<String> newFormAsset = repository.loadAssetFromPath(packageName + "/" + taskName + TASKFORM_NAME_EXTENSION + "." + formType);

                    return getAssetInfo(newFormAsset);
                } else {
                    _logger.error("Cannot create new form for invalid form type: " + formType);
                }
            } catch (Exception e) {
                _logger.error(e.getMessage());
            }
        }
        return "false";
    }

    public BPMNFormBuilderService<Definitions> getFormBuilder() {
        return formBuilder;
    }

    public BPMNFormBuilderService<Definitions> getFormBuilder(String formType) {
        return formBuilderManager.getBuilderByFormType(formType);
    }

    private String getAssetInfo(Asset<String> asset) throws UnsupportedEncodingException {
        return asset.getName() + "." + asset.getAssetType() + "|" + getDecodedId(asset.getUniqueId());
    }

    private String getDecodedId(String id) throws UnsupportedEncodingException {
        if (Base64.isBase64(id)) {
            byte[] decodedId = Base64.decodeBase64(id);
            return new String(decodedId,
                              "UTF-8");
        } else {
            return id;
        }
    }

    public void setFormBuilder(BPMNFormBuilderService<Definitions> formBuilder) {
        this.formBuilder = formBuilder;
    }
}
