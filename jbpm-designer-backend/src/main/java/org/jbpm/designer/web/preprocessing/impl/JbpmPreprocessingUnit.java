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

package org.jbpm.designer.web.preprocessing.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.jbpm.designer.notification.DesignerWorkitemInstalledEvent;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.repository.filters.FilterByExtension;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.RepositoryDescriptor;
import org.jbpm.designer.server.EditorHandler;
import org.jbpm.designer.server.service.DefaultDesignerAssetService;
import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.preprocessing.IDiagramPreprocessingUnit;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.ServiceRepoUtils;
import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.process.core.datatype.impl.type.EnumDataType;
import org.jbpm.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.jbpm.process.workitem.WorkItemRepository;
import org.jbpm.util.WidMVELEvaluator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.backend.builder.core.LRUBuilderCache;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * JbpmPreprocessingUnit - preprocessing unit for the jbpm profile
 * @author Tihomir Surdilovic
 */
@Named("jbpmPreprocessingUnit")
public class JbpmPreprocessingUnit implements IDiagramPreprocessingUnit {

    private static final Logger _logger =
            LoggerFactory.getLogger(JbpmPreprocessingUnit.class);
    public static final String STENCILSET_PATH = "stencilsets";
    public static final String WORKITEM_DEFINITION_EXT = "wid";
    public static final String THEME_NAME = "themes";
    public static final String THEME_EXT = ".json";
    public static final String PATTERNS_NAME = "patterns";
    public static final String PATTERNS_EXT = ".json";
    public static final String DEFAULT_THEME_NAME = "jBPM";
    public static final String CUSTOMEDITORS_NAME = "customeditors";
    public static final String PROCESSDICTIONARY_NAME = "processdictionary";
    public static final String CUSTOMEDITORS_EXT = ".json";
    public static final String THEME_COOKIE_NAME = "designercolortheme";
    public static final String DEFAULT_CATEGORY_NAME = "Service Tasks";
    public static final String INCLUDE_DATA_OBJECT = "designerdataobjects";
    public static final Pattern UNICODE_WORDS_PATTERN = Pattern.compile("\\p{L}+",
                                                                        Pattern.UNICODE_CHARACTER_CLASS);

    private String designer_path;
    private String stencilPath;
    private String origStencilFilePath;
    private String stencilFilePath;
    private String outData = "";
    private String workitemSVGFilePath;
    private String origWorkitemSVGFile;
    private String defaultEmailIcon;
    private String defaultMilestoneIcon;
    private String defaultLogIcon;
    private String defaultServiceNodeIcon;
    private String defaultBusinessRulesIcon;
    private String defaultDecisionIcon;
    private String defaultWidConfigTemplate;
    private String defaultClasspathWid = "META-INF/WorkDefinitions.wid";
    private String themeInfo;
    private String formWidgetsDir;
    private String customEditorsInfo;
    private String patternsData;
    private String sampleBpmn2;
    private String globalDir;
    private VFSService vfsService;
    private boolean includeDataObjects;

    @Inject
    protected Event<DesignerWorkitemInstalledEvent> workitemInstalledEventEvent;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    private POMService pomService;

    @Inject
    private ProjectService<? extends Project> projectService;

    @Inject
    private MetadataService metadataService;

    @Inject
    private DefaultDesignerAssetService defaultDesignerAssetService;

    @Inject
    KieProjectService kieProjectService;

    @Inject
    private LRUBuilderCache builderCache;

    public JbpmPreprocessingUnit() {
    }

    public void init(ServletContext servletContext,
                     VFSService vfsService) {
        init(servletContext,
             ConfigurationProvider.getInstance().getDesignerContext(),
             vfsService);
    }

    public void init(ServletContext servletContext,
                     String designerPath,
                     VFSService vfsService) {
        this.designer_path = designerPath.substring(0,
                                                    designerPath.length() - 1);
        this.vfsService = vfsService;
        stencilPath = servletContext.getRealPath(designer_path + "/" + STENCILSET_PATH);
        origStencilFilePath = stencilPath + "/bpmn2.0jbpm/stencildata/" + "bpmn2.0jbpm.orig";
        stencilFilePath = stencilPath + "/bpmn2.0jbpm/" + "bpmn2.0jbpm.json";
        workitemSVGFilePath = stencilPath + "/bpmn2.0jbpm/view/activity/workitems/";
        origWorkitemSVGFile = workitemSVGFilePath + "workitem.orig";
        defaultEmailIcon = servletContext.getRealPath(designer_path + "/defaults/defaultemailicon.gif");
        defaultMilestoneIcon = servletContext.getRealPath(designer_path + "/defaults/defaultmilestoneicon.png");
        defaultLogIcon = servletContext.getRealPath(designer_path + "/defaults/defaultlogicon.gif");
        defaultServiceNodeIcon = servletContext.getRealPath(designer_path + "/defaults/defaultservicenodeicon.png");
        defaultBusinessRulesIcon = servletContext.getRealPath(designer_path + "/defaults/defaultbusinessrulesicon.png");
        defaultDecisionIcon = servletContext.getRealPath(designer_path + "/defaults/defaultdecisionicon.png");
        defaultWidConfigTemplate = servletContext.getRealPath(designer_path + "/defaults/WorkDefinitions.wid.st");
        themeInfo = servletContext.getRealPath(designer_path + "/defaults/themes.json");
        formWidgetsDir = servletContext.getRealPath(designer_path + "/defaults/formwidgets");
        customEditorsInfo = servletContext.getRealPath(designer_path + "/defaults/customeditors.json");
        patternsData = servletContext.getRealPath(designer_path + "/defaults/patterns.json");
        sampleBpmn2 = servletContext.getRealPath(designer_path + "/defaults/SampleProcess.bpmn2");
        includeDataObjects = Boolean.parseBoolean(System.getProperty(INCLUDE_DATA_OBJECT) == null ? "true" : System.getProperty(INCLUDE_DATA_OBJECT));
    }

    public String getOutData() {
        if (outData != null && outData.length() > 0) {
            if (outData.endsWith(",")) {
                outData = outData.substring(0,
                                            outData.length() - 1);
            }
        }
        return outData;
    }

    public void preprocess(HttpServletRequest req,
                           HttpServletResponse res,
                           IDiagramProfile profile,
                           ServletContext serlvetContext,
                           boolean readOnly,
                           boolean viewLocked,
                           IOService ioService,
                           RepositoryDescriptor descriptor) {
        try {

            if (readOnly) {
                _logger.info("Performing preprocessing steps in readonly mode.");
                ST workItemTemplate = new ST(readFile(origStencilFilePath),
                                             '$',
                                             '$');
                workItemTemplate.add("bopen",
                                     "{");
                workItemTemplate.add("bclose",
                                     "}");
                workItemTemplate.add("patternData",
                                     new HashMap<String, PatternInfo>());
                workItemTemplate.add("packageName",
                                     "org.jbpm");
                workItemTemplate.add("processn",
                                     "");
                workItemTemplate.add("processid",
                                     "");
                workItemTemplate.add("pversion",
                                     "1.0");

                String readOnlyIconEncoded = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABEAAAARCAYAAAA7bUf6AAAC7mlDQ1BJQ0MgUHJvZmlsZQAAeAGFVM9rE0EU/jZuqdAiCFprDrJ4kCJJWatoRdQ2/RFiawzbH7ZFkGQzSdZuNuvuJrWliOTi0SreRe2hB/+AHnrwZC9KhVpFKN6rKGKhFy3xzW5MtqXqwM5+8943731vdt8ADXLSNPWABOQNx1KiEWlsfEJq/IgAjqIJQTQlVdvsTiQGQYNz+Xvn2HoPgVtWw3v7d7J3rZrStpoHhP1A4Eea2Sqw7xdxClkSAog836Epx3QI3+PY8uyPOU55eMG1Dys9xFkifEA1Lc5/TbhTzSXTQINIOJT1cVI+nNeLlNcdB2luZsbIEL1PkKa7zO6rYqGcTvYOkL2d9H5Os94+wiHCCxmtP0a4jZ71jNU/4mHhpObEhj0cGDX0+GAVtxqp+DXCFF8QTSeiVHHZLg3xmK79VvJKgnCQOMpkYYBzWkhP10xu+LqHBX0m1xOv4ndWUeF5jxNn3tTd70XaAq8wDh0MGgyaDUhQEEUEYZiwUECGPBoxNLJyPyOrBhuTezJ1JGq7dGJEsUF7Ntw9t1Gk3Tz+KCJxlEO1CJL8Qf4qr8lP5Xn5y1yw2Fb3lK2bmrry4DvF5Zm5Gh7X08jjc01efJXUdpNXR5aseXq8muwaP+xXlzHmgjWPxHOw+/EtX5XMlymMFMXjVfPqS4R1WjE3359sfzs94i7PLrXWc62JizdWm5dn/WpI++6qvJPmVflPXvXx/GfNxGPiKTEmdornIYmXxS7xkthLqwviYG3HCJ2VhinSbZH6JNVgYJq89S9dP1t4vUZ/DPVRlBnM0lSJ93/CKmQ0nbkOb/qP28f8F+T3iuefKAIvbODImbptU3HvEKFlpW5zrgIXv9F98LZua6N+OPwEWDyrFq1SNZ8gvAEcdod6HugpmNOWls05Uocsn5O66cpiUsxQ20NSUtcl12VLFrOZVWLpdtiZ0x1uHKE5QvfEp0plk/qv8RGw/bBS+fmsUtl+ThrWgZf6b8C8/UXAeIuJAAAACXBIWXMAAAsTAAALEwEAmpwYAAADrUlEQVQ4EYVUfUxTVxT/3de+wqNUJDgU+ZRiC+goKyIZy5wSSBB04WMMZ1yiy5Y4kpksWxwxRCdmxLk/JCQzS8yYMVsym6HDwDAoYHHgClhb+QhlRUQYwVrBFUof7ePdvdfMfbCYndx7cz/y+52T3zn3gFKK500TNSnSys4Yn/f+7J7BKvuw8FaMvf/hFnhdBsepjSYqkubakz1mgf8tz9oz8erhI23pqyAgMtuftqZpsu9o+7WpkuvNzsAE6yfwhOnALHLxRCOuj/I55qGiudEpyr05ic0VB4xnJJxbxirlhRDCfvfNwDFeraoSXESzTrMRx8uKkJbGw3aHImAEw4yHpl34vhV0CVgMYaquTFhQuinnaBAvR1L1Y2tWoMd/yfcEWhKqpq/vjiX5uQFMzjzC6JCI+BcJNvti0e0jaL0zTYX5JcLF0DGmK6z0K9PukaAmBwtTImyzTr8z1oM9RfFkh/YpZmdL4Oh6E+o1DfDerAB1v4WClwgKs1LJyJAHA233hbc/To2SIwmSGJdDIc77cbC8GPk5AhYHD2P4bhIqd9Rjb/ExxOU2wB7pha/7a+wMDcM7BwrBcX7ySoaK/Yukrn76hFvF6fl+Hva2GSyFD+GNffvBL+iBwDLS+TTEoRL3xs/Cqb6LFa0A9gmnb//8wQmZRGkorzeOiUIy7/EynCSzixOxNLMHOksmBiiPl3tlZxKRsgy/csOYGxERsgiY6QJjsd1O1pX3GpmafbH240ey+zK1ap9Pymp0QIEktCBEdRk5lAMU8uAwEnIZ4UIL4gJKBESCjGS1T8bVKJLswWr102kDimptv9jcdO7xPdpxUU8t56Lo6O1qKtYP0NHeanpDOptHo6l12kbPn3fTrZW1Nirh5OwGhWVvUZrOqPDlpVZc71EicVsjNuQp0Gk9jeGnp9A5eBqbNC8gXrwC6wCLCx0/weVXUXvXihDURF46m8d+3xmnVz2KWMFVxyRlkUC2b2vHukgX5jcIiFyuQnJBTNBBt/Sekh1GtGM61nR1cs6wK+Hvsv+20fpZeDTzQe/YuOZGnxfv5RdLlcxjmaFQSP4ys0NxTooUa8NRsEW7EP5YbKg4ZKyRg/jX33E2Wj9p6n9YWu1y+LeyfhANpxu87+UMERrRHvA5NrMqvLY+RbVre8IP+w9l1Ul4j0zynzZQ9lFH4mD3VIYsWl3nzabUki+mTn76szmw8CDPYnbmvft+S5YEY5+1geAH/udh9d5kMilQdvZ/+8kfxh8EsHymFKsAAAAASUVORK5CYII=";
                Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();
                Map<String, Map<String, List<String>>> customParams = new HashMap<>();
                WorkDefinitionImpl readOnlyImpl = new WorkDefinitionImpl();
                readOnlyImpl.setName("ReadOnlyService");
                readOnlyImpl.setDisplayName("ReadOnlyService");
                readOnlyImpl.setCategory("Service Tasks");
                readOnlyImpl.setIcon("/global/defaultservicenodeicon.png");
                readOnlyImpl.setIconEncoded(URLEncoder.encode(readOnlyIconEncoded,
                                                              "UTF-8"));
                readOnlyImpl.setCustomEditor("");
                workDefinitions.put("ReadOnlyService",
                                    readOnlyImpl);
                workItemTemplate.add("workitemDefs",
                                     workDefinitions);
                workItemTemplate.add("customParams",
                                     customParams);
                Map<String, ThemeInfo> themeData = setupThemesForReadOnly(req);
                workItemTemplate.add("colortheme",
                                     themeData);
                workItemTemplate.add("caseproject",
                                     false);
                deletefile(stencilFilePath);
                createAndWriteToFile(stencilFilePath,
                                     workItemTemplate.render());
                createAndParseViewSVGForReadOnly(workDefinitions,
                                                 readOnlyIconEncoded);

                return;
            }

            if (ioService != null) {
                ioService.startBatch(new FileSystem[]{descriptor.getFileSystem()});
            }

            Repository repository = profile.getRepository();

            String uuid = Utils.getUUID(req);
            //createAssetIfNotExisting(repository, "/defaultPackage", "BPMN2-SampleProcess", "bpmn2", getBytesFromFile(new File(sampleBpmn2)));

            installDefaultRepositoryWorkitems(uuid,
                                              repository,
                                              vfsService);

            Asset<String> asset = repository.loadAsset(uuid);
            this.globalDir = profile.getRepositoryGlobalDir(uuid);
            outData = "";
            Map<String, ThemeInfo> themeData = setupThemes(req,
                                                           repository,
                                                           profile);
            setupCustomEditors(repository,
                               profile);
            setupDefaultIcons(globalDir,
                              repository);
            setupDefaultWorkflowPatterns(globalDir,
                                         repository);

            // figure out which package our uuid belongs in and get back the list of configs
            Collection<Asset> workitemConfigInfo = findWorkitemInfoForUUID(asset.getAssetLocation(),
                                                                           repository);

            // also get all from globals package
            Collection<Asset> globalWorkitemConfigInfo = findWorkitemInfoForUUID(globalDir,
                                                                                 repository);

            if (workitemConfigInfo != null) {
                if (globalWorkitemConfigInfo != null) {
                    workitemConfigInfo.addAll(globalWorkitemConfigInfo);
                }
            } else {
                workitemConfigInfo = globalWorkitemConfigInfo;
            }

            if (workitemConfigInfo == null || workitemConfigInfo.size() < 1) {
                setupDefaultWorkitemConfigs(asset.getAssetLocation(),
                                            repository);
                workitemConfigInfo = findWorkitemInfoForUUID(asset.getAssetLocation(),
                                                             repository);
            }

            // get the contents of each of the configs
            Collection<Asset> workItemsContent = getWorkitemConfigContent(workitemConfigInfo,
                                                                          repository);

            // evaluate all configs
            KieProject kieProject = kieProjectService.resolveProject(vfsService.get(uuid.replaceAll("\\s",
                                                                                                    "%20")));
            Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();
            for (Asset entry : workItemsContent) {

                try {
                    evaluateWorkDefinitions(workDefinitions,
                                            entry,
                                            asset.getAssetLocation(),
                                            repository,
                                            kieProject);
                } catch (Exception e) {
                    _logger.error("Unable to parse a workitem definition: " + e.getMessage());
                }
            }

            Map<String, Map<String, List<String>>> customParams = new HashMap<>();
            for (Map.Entry<String, WorkDefinitionImpl> widEntry : workDefinitions.entrySet()) {
                WorkDefinitionImpl widImpl = widEntry.getValue();
                if (widImpl.getParameterValues() != null) {
                    Map<String, Object> widImplParamValues = widImpl.getParameterValues();

                    Map<String, List<String>> customParamsValueMap = new HashMap<>();
                    for (Map.Entry<String, Object> widParamValueEntry : widImplParamValues.entrySet()) {
                        if (widParamValueEntry.getValue() != null && widParamValueEntry.getValue() instanceof String) {
                            customParamsValueMap.put(widParamValueEntry.getKey(),
                                                     Arrays.asList(((String) widParamValueEntry.getValue()).split(",\\s*")));
                        }
                    }
                    customParams.put(widEntry.getKey(),
                                     customParamsValueMap);
                }
            }

            // sort against display name
            WorkItemDisplayNameComparator wiComparator = new WorkItemDisplayNameComparator(workDefinitions);
            Map<String, WorkDefinitionImpl> workDefinitionsTree = new TreeMap<String, WorkDefinitionImpl>(wiComparator);
            workDefinitionsTree.putAll(workDefinitions);

            // set the out parameter
            for (Map.Entry<String, WorkDefinitionImpl> definition : workDefinitionsTree.entrySet()) {
                outData += definition.getValue().getName() + ",";
            }
            // parse the profile json to include config data
            // parse patterns data
            JSONArray patternsArray = new JSONArray(findWorkflowPatterns(repository).getAssetContent());
            Map<String, PatternInfo> patternInfoMap = new HashMap<String, PatternInfo>();
            for (int i = 0; i < patternsArray.length(); i++) {
                JSONObject patternObj = patternsArray.getJSONObject(i);
                PatternInfo pi = new PatternInfo(patternObj.getString("id"),
                                                 patternObj.getString("name"),
                                                 patternObj.getString("description"));
                patternInfoMap.put(patternObj.getString("id"),
                                   pi);
            }

            // parse the orig stencil data with workitem definitions
            ST workItemTemplate = new ST(readFile(origStencilFilePath),
                                         '$',
                                         '$');
            workItemTemplate.add("bopen",
                                 "{");
            workItemTemplate.add("bclose",
                                 "}");
            workItemTemplate.add("workitemDefs",
                                 workDefinitionsTree);
            workItemTemplate.add("customParams",
                                 customParams);
            workItemTemplate.add("patternData",
                                 patternInfoMap);
            workItemTemplate.add("includedo",
                                 includeDataObjects);

            if (kieProject != null && kieProject.getRootPath() != null && defaultDesignerAssetService.isCaseProject(kieProject.getRootPath())) {
                workItemTemplate.add("caseproject",
                                     true);
            } else {
                workItemTemplate.add("caseproject",
                                     false);
            }

            String processPackage = asset.getAssetLocation();
            if (processPackage.startsWith("/")) {
                processPackage = processPackage.substring(1,
                                                          processPackage.length());
            }
            processPackage = processPackage.replaceAll("/",
                                                       ".");
            // final check in odd cases
            if (processPackage.startsWith(".")) {
                processPackage = processPackage.substring(1,
                                                          processPackage.length());
            }

            // set package to org.jbpm
            workItemTemplate.add("packageName",
                                 "org.jbpm");

            String processName = asset.getName();
            workItemTemplate.add("processn",
                                 processName);

            String packageNameStr = (processPackage.length() > 0) ? processPackage + "." : "";
            if (packageNameStr.length() > 0) {
                String[] packageNameParts = packageNameStr.split("\\.");
                packageNameStr = packageNameParts[0] + ".";
            }

            // default the process id to packagename.processName
            String processIdString = packageNameStr + processName;
            if (processIdString.startsWith(".")) {
                processIdString = processIdString.substring(1,
                                                            processIdString.length());
            }

            workItemTemplate.add("processid",
                                 processIdString);

            // default version to 1.0
            workItemTemplate.add("pversion",
                                 "1.0");
            // color theme attribute
            workItemTemplate.add("colortheme",
                                 themeData);

            // delete stencil data json if exists
            deletefile(stencilFilePath);
            // copy our results as the stencil json data
            createAndWriteToFile(stencilFilePath,
                                 workItemTemplate.render());
            // create and parse the view svg to include config data
            createAndParseViewSVG(workDefinitionsTree,
                                  repository);
        } catch (Exception e) {
            _logger.error(e.getMessage());
        } catch (Throwable e) {
            _logger.error(e.getMessage());
        } finally {
            try {
                if (!readOnly && ioService != null) {
                    ioService.endBatch();
                }
            } catch (Exception ee) {
                _logger.debug("Error ending batch: " + ee.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void createAndParseViewSVGForReadOnly(Map<String, WorkDefinitionImpl> workDefinitions,
                                                  String iconBase64) {
        try {
            for (Map.Entry<String, WorkDefinitionImpl> definition : workDefinitions.entrySet()) {
                ST workItemTemplate = new ST(readFile(origWorkitemSVGFile),
                                             '$',
                                             '$');
                workItemTemplate.add("workitemDef",
                                     definition.getValue());
                String widIcon = definition.getValue().getIcon();

                String iconEncoded = iconBase64;
                workItemTemplate.add("nodeicon",
                                     iconEncoded);
                String fileToWrite = workitemSVGFilePath + definition.getValue().getName() + ".svg";
                createAndWriteToFile(fileToWrite,
                                     workItemTemplate.render());
            }
        } catch (Exception e) {
            _logger.error("Failed to setup workitem svg images: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void createAndParseViewSVG(Map<String, WorkDefinitionImpl> workDefinitions,
                                       Repository repository) {
        // first delete all existing workitem svgs
        Collection<File> workitemsvgs = FileUtils.listFiles(new File(workitemSVGFilePath),
                                                            new String[]{"svg"},
                                                            true);
        if (workitemsvgs != null) {
            for (File wisvg : workitemsvgs) {
                deletefile(wisvg);
            }
        }
        try {
            for (Map.Entry<String, WorkDefinitionImpl> definition : workDefinitions.entrySet()) {
                ST workItemTemplate = new ST(readFile(origWorkitemSVGFile),
                                             '$',
                                             '$');
                workItemTemplate.add("workitemDef",
                                     definition.getValue());
                String widIcon = definition.getValue().getIcon();

                Asset<byte[]> iconAsset = repository.loadAssetFromPath(widIcon);
                String iconEncoded = "data:image/png;base64," + javax.xml.bind.DatatypeConverter.printBase64Binary(iconAsset.getAssetContent());
                workItemTemplate.add("nodeicon",
                                     iconEncoded);
                String fileToWrite = workitemSVGFilePath + definition.getValue().getName() + ".svg";
                createAndWriteToFile(fileToWrite,
                                     workItemTemplate.render());
            }
        } catch (Exception e) {
            _logger.error("Failed to setup workitem svg images: " + e.getMessage());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void evaluateWorkDefinitions(Map<String, WorkDefinitionImpl> workDefinitions,
                                        Asset<String> widAsset,
                                        String assetLocation,
                                        Repository repository,
                                        KieProject kieProject) throws Exception {
        List<Map<String, Object>> workDefinitionsMaps;

        try {
            workDefinitionsMaps = (List<Map<String, Object>>) WidMVELEvaluator.eval(widAsset.getAssetContent());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        for (Map<String, Object> workDefinitionMap : workDefinitionsMaps) {
            if (workDefinitionMap != null) {
                WorkDefinitionImpl workDefinition = new WorkDefinitionImpl();

                String origWidName = ((String) workDefinitionMap.get("name")).replaceAll("\\s",
                                                                                         "");
                Matcher widNameMatcher = UNICODE_WORDS_PATTERN.matcher(origWidName);
                if(widNameMatcher.matches()) {
                    workDefinition.setName(widNameMatcher.group());

                    workDefinition.setDisplayName((String) workDefinitionMap.get("displayName"));

                    String category = (String) workDefinitionMap.get("category");
                    if (category == null || category.length() < 1) {
                        category = DEFAULT_CATEGORY_NAME;
                    }
                    workDefinition.setCategory(category);

                    String icon = (String) workDefinitionMap.get("icon");
                    if (icon == null || icon.trim().length() < 1) {
                        icon = this.globalDir + "/defaultservicenodeicon.png";
                    }
                    Asset<byte[]> iconAsset;
                    boolean iconFound = false;
                    // Look for icon located relative to the asset
                    String absoluteIcon = createAbsoluteIconPath(assetLocation,
                                                                 icon).replaceAll("\\s",
                                                                                  "%20");
                    if (repository.assetExists(absoluteIcon)) {
                        icon = absoluteIcon;
                        iconFound = true;
                    }
                    // Icon not found relative to asset, look for it relative to globalDir
                    if (!iconFound) {
                        if (!icon.startsWith(this.globalDir)) {
                            if (icon.startsWith("/")) {
                                icon = this.globalDir + icon;
                            } else {
                                icon = this.globalDir + "/" + icon;
                            }
                        }
                    }

                    try {
                        if (!repository.assetExists(icon)) {
                            icon = this.globalDir + "/defaultservicenodeicon.png";
                        }
                    } catch (Exception e) {
                        _logger.error(e.getMessage());
                        icon = this.globalDir + "/defaultservicenodeicon.png";
                    }
                    iconAsset = repository.loadAssetFromPath(icon);
                    workDefinition.setIcon(icon);

                    String iconEncoded = "data:image/png;base64," + javax.xml.bind.DatatypeConverter.printBase64Binary(iconAsset.getAssetContent());
                    workDefinition.setIconEncoded(URLEncoder.encode(iconEncoded,
                                                                    "UTF-8"));

                    if (workDefinitionMap.get("customEditor") != null
                            && ((String) workDefinitionMap.get("customEditor")).length() > 0) {
                        workDefinition.setCustomEditor((String) workDefinitionMap.get("customEditor"));
                    } else {
                        workDefinition.setCustomEditor(null);
                    }

                    Set<ParameterDefinition> parameters = new HashSet<ParameterDefinition>();
                    if (workDefinitionMap.get("parameters") != null) {
                        Map<String, DataType> parameterMap = (Map<String, DataType>) workDefinitionMap.get("parameters");
                        if (parameterMap != null) {
                            for (Map.Entry<String, DataType> entry : parameterMap.entrySet()) {
                                parameters.add(new ParameterDefinitionImpl(entry.getKey(),
                                                                           entry.getValue()));
                            }
                        }
                    }
                    workDefinition.setParameters(parameters);

                    Set<ParameterDefinition> results = new HashSet<ParameterDefinition>();
                    if (workDefinitionMap.get("results") != null) {
                        Map<String, DataType> resultMap = (Map<String, DataType>) workDefinitionMap.get("results");
                        if (resultMap != null) {
                            for (Map.Entry<String, DataType> entry : resultMap.entrySet()) {
                                results.add(new ParameterDefinitionImpl(entry.getKey(),
                                                                        entry.getValue()));
                            }
                        }
                    }
                    workDefinition.setResults(results);

                    Map<String, Object> parameterValues = new HashMap<>();
                    if (workDefinitionMap.get("parameterValues") != null) {
                        try {
                            Map<String, Object> parameterValuesMap = (Map<String, Object>) workDefinitionMap.get("parameterValues");
                            if (parameterValuesMap != null) {
                                for (Map.Entry<String, Object> entry : parameterValuesMap.entrySet()) {

                                    Object paramValueObj = entry.getValue();
                                    if (paramValueObj != null) {
                                        if (paramValueObj instanceof String
                                                && ((String) paramValueObj).trim().length() > 0) {
                                            parameterValues.put(entry.getKey(),
                                                                entry.getValue());
                                        } else if (paramValueObj instanceof EnumDataType) {
                                            Builder builder = builderCache.getBuilder(kieProject);
                                            EnumDataType enumdt = (EnumDataType) entry.getValue();
                                            if (enumdt != null) {
                                                try {
                                                    List<String> enumValuesList = Arrays.asList(enumdt.getValueNames(builder.getKieContainer().getClassLoader()));
                                                    String enumValuesStr = enumValuesList.stream().filter(StringUtils::isNotBlank)
                                                            .collect(Collectors.joining(","));

                                                    parameterValues.put(entry.getKey(),
                                                                        enumValuesStr);
                                                } catch (Throwable t) {
                                                    _logger.error("Error retrieving enum: " + t.getMessage());
                                                }
                                            }
                                        } else {
                                            _logger.warn("parameter value type not supported");
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            _logger.error("Error parsing parameter values: " + e.getMessage());
                        }
                    }
                    workDefinition.setParameterValues(parameterValues);

                    if (workDefinitionMap.get("defaultHandler") != null) {
                        workDefinition.setDefaultHandler((String) workDefinitionMap.get("defaultHandler"));
                    } else {
                        workDefinition.setDefaultHandler("");
                    }

                    if (workDefinitionMap.get("dependencies") != null) {
                        workDefinition.setDependencies(((List<String>) workDefinitionMap.get("dependencies")).toArray(new String[0]));
                    } else {
                        workDefinition.setDependencies(new String[]{});
                    }

                    if (workDefinitionMap.get("documentation") != null) {
                        workDefinition.setDocumentation((String) workDefinitionMap.get("documentation"));
                    } else {
                        workDefinition.setDocumentation("");
                    }

                    if (workDefinitionMap.get("defaultHandler") != null) {
                        workDefinition.setDefaultHandler((String) workDefinitionMap.get("defaultHandler"));
                    } else {
                        workDefinition.setDefaultHandler("");
                    }

                    if (workDefinitionMap.get("version") != null) {
                        workDefinition.setVersion((String) workDefinitionMap.get("version"));
                    } else {
                        workDefinition.setVersion("");
                    }

                    if (workDefinitionMap.get("description") != null) {
                        workDefinition.setDescription((String) workDefinitionMap.get("description"));
                    } else {
                        workDefinition.setDescription("");
                    }

                    if (workDefinitionMap.get("mavenDependencies") != null) {
                        workDefinition.setMavenDependencies(((List<String>) workDefinitionMap.get("mavenDependencies")).toArray(new String[0]));
                    } else {
                        workDefinition.setMavenDependencies(new String[]{});
                    }

                    workDefinitions.put(workDefinition.getName(),
                                        workDefinition);
                } else {
                    _logger.error("Workitem has invalid name: " + workDefinitionMap.get("name") + " and will not be added. Name must contain words only");
                }
            }
        }
    }

    private Collection<Asset> getWorkitemConfigContent(Collection<Asset> widAssets,
                                                       Repository repository) {
        List<Asset> loadedAssets = new ArrayList<Asset>();
        for (Asset widAsset : widAssets) {
            try {
                Asset assetWithContent = repository.loadAsset(widAsset.getUniqueId());
                loadedAssets.add(assetWithContent);
            } catch (NoSuchFileException e) {
                _logger.error("Asset " + widAsset.getName() + " not found");
            }
        }

        return loadedAssets;
    }

    private void setupFormWidgets(Repository repository,
                                  IDiagramProfile profile) {

        File[] allFormWidgets = new File(formWidgetsDir).listFiles();
        for (File formWidget : allFormWidgets) {
            try {
                int extPosition = formWidget.getName().lastIndexOf(".");
                String extension = formWidget.getName().substring(extPosition + 1);
                String name = formWidget.getName().substring(0,
                                                             extPosition);
                createAssetIfNotExisting(repository,
                                         this.globalDir,
                                         name,
                                         extension,
                                         getBytesFromFile(formWidget));
            } catch (Exception e) {
                _logger.error("Error setting up form widgets: " + e.getMessage());
            }
        }
    }

    private void setupCustomEditors(Repository repository,
                                    IDiagramProfile profile) {

        try {
            createAssetIfNotExisting(repository,
                                     this.globalDir,
                                     CUSTOMEDITORS_NAME,
                                     "json",
                                     getBytesFromFile(new File(customEditorsInfo)));
        } catch (Exception e) {
            _logger.error(e.getMessage());
        }
    }

    private Map<String, ThemeInfo> setupThemesForReadOnly(HttpServletRequest req) {
        Map<String, ThemeInfo> themeData = new HashMap<String, ThemeInfo>();
        try {
            String themesStr = readFile(themeInfo);
            JSONObject themesObject = new JSONObject(themesStr);

            // extract theme info from json
            JSONObject themes = (JSONObject) themesObject.get("themes");
            JSONObject selectedTheme = (JSONObject) themes.get("jBPM");
            for (String key : JSONObject.getNames(selectedTheme)) {
                String val = (String) selectedTheme.get(key);
                String[] valParts = val.split("\\|\\s*");
                ThemeInfo ti;
                if (valParts.length == 3) {
                    ti = new ThemeInfo(valParts[0],
                                       valParts[1],
                                       valParts[2]);
                } else {
                    ti = new ThemeInfo("#000000",
                                       "#000000",
                                       "#000000");
                }
                themeData.put(key,
                              ti);
            }
            return themeData;
        } catch (Exception e) {
            e.printStackTrace();
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
            return themeData;
        }
    }

    private Map<String, ThemeInfo> setupThemes(HttpServletRequest req,
                                               Repository repository,
                                               IDiagramProfile profile) {
        Map<String, ThemeInfo> themeData = new HashMap<String, ThemeInfo>();
        Asset<String> themeAsset = null;
        try {
            boolean themeExists = repository.assetExists(this.globalDir + "/" + THEME_NAME + THEME_EXT);
            if (!themeExists) {
                // create theme asset
                AssetBuilder assetBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
                assetBuilder.content(new String(getBytesFromFile(new File(themeInfo)),
                                                "UTF-8"))
                        .location(this.globalDir)
                        .name(THEME_NAME)
                        .type("json")
                        .version("1.0");

                themeAsset = assetBuilder.getAsset();

                repository.createAsset(themeAsset);
            } else {
                themeAsset = repository.loadAssetFromPath(this.globalDir + "/" + THEME_NAME + THEME_EXT);
            }

            String themesStr = themeAsset.getAssetContent();

            JSONObject themesObject = new JSONObject(themesStr);

            // get the theme name from cookie if exists or default
            String themeName = DEFAULT_THEME_NAME;
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie ck : cookies) {
                    if (ck.getName().equals(THEME_COOKIE_NAME)) {
                        themeName = ck.getValue();
                    }
                }
            }

            // extract theme info from json
            JSONObject themes = (JSONObject) themesObject.get("themes");
            JSONObject selectedTheme = (JSONObject) themes.get(themeName);
            for (String key : JSONObject.getNames(selectedTheme)) {
                String val = (String) selectedTheme.get(key);
                String[] valParts = val.split("\\|\\s*");
                ThemeInfo ti;
                if (valParts.length == 3) {
                    ti = new ThemeInfo(valParts[0],
                                       valParts[1],
                                       valParts[2]);
                } else {
                    ti = new ThemeInfo("#000000",
                                       "#000000",
                                       "#000000");
                }
                themeData.put(key,
                              ti);
            }
            return themeData;
        } catch (Exception e) {
            e.printStackTrace();
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
            return themeData;
        }
    }

    private void setupDefaultIcons(String location,
                                   Repository repository) {

        try {

            createAssetIfNotExisting(repository,
                                     location,
                                     "defaultemailicon",
                                     "gif",
                                     getBytesFromFile(new File(defaultEmailIcon)));

            createAssetIfNotExisting(repository,
                                     location,
                                     "defaultmilestoneicon",
                                     "png",
                                     getBytesFromFile(new File(defaultMilestoneIcon)));

            createAssetIfNotExisting(repository,
                                     location,
                                     "defaultlogicon",
                                     "gif",
                                     getBytesFromFile(new File(defaultLogIcon)));

            createAssetIfNotExisting(repository,
                                     location,
                                     "defaultservicenodeicon",
                                     "png",
                                     getBytesFromFile(new File(defaultServiceNodeIcon)));

            createAssetIfNotExisting(repository,
                                     location,
                                     "defaultbusinessrulesicon",
                                     "png",
                                     getBytesFromFile(new File(defaultBusinessRulesIcon)));

            createAssetIfNotExisting(repository,
                                     location,
                                     "defaultdecisionicon",
                                     "png",
                                     getBytesFromFile(new File(defaultDecisionIcon)));
        } catch (Exception e) {
            _logger.error(e.getMessage());
        }
    }

    private void installDefaultRepositoryWorkitems(String uuid,
                                                   Repository repository,
                                                   VFSService vfsService) throws IOException {
        String defaultServiceRepo = System.getProperty(EditorHandler.SERVICE_REPO) == null ? null : System.getProperty(EditorHandler.SERVICE_REPO);
        String defaultServiceRepoTasks = System.getProperty(EditorHandler.SERVICE_REPO_TASKS) == null ? null : System.getProperty(EditorHandler.SERVICE_REPO_TASKS);

        if (defaultServiceRepo != null &&
                defaultServiceRepo.trim().length() > 0 &&
                defaultServiceRepoTasks != null &&
                defaultServiceRepoTasks.trim().length() > 0 &&
                workitemInstalledEventEvent != null &&
                notification != null &&
                projectService != null &&
                pomService != null &&
                metadataService != null) {
            Map<String, WorkDefinitionImpl> workitemsFromRepo = WorkItemRepository.getWorkDefinitions(defaultServiceRepo);
            if (workitemsFromRepo != null && workitemsFromRepo.size() > 0) {
                List<String> toInstallTasks = Arrays.asList(defaultServiceRepoTasks.split("\\s*,\\s*"));
                for (String installTask : toInstallTasks) {
                    if (workitemsFromRepo.containsKey(installTask)) {
                        try {
                            ServiceRepoUtils.installWorkItem(workitemsFromRepo,
                                                             installTask,
                                                             uuid,
                                                             repository,
                                                             vfsService,
                                                             workitemInstalledEventEvent,
                                                             notification,
                                                             pomService,
                                                             projectService,
                                                             metadataService);
                        } catch (Exception e) {
                            _logger.error("Work Item '" + installTask + "' was not installed correctly: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    private void setupDefaultWorkflowPatterns(String location,
                                              Repository repository) {
        try {
            createAssetIfNotExisting(repository,
                                     location,
                                     "patterns",
                                     "json",
                                     getBytesFromFile(new File(patternsData)));
        } catch (Exception e) {
            _logger.error(e.getMessage());
        }
    }

    private void setupDefaultWorkitemConfigs(String location,
                                             Repository repository) {
        try {
            location = UriUtils.encode(location);
            // push default configuration wid
            // check classpath first
            InputStream widIn = this.getClass().getClassLoader().getResourceAsStream(defaultClasspathWid);
            String createdUUID;
            if (widIn != null) {
                createdUUID = createAssetIfNotExisting(repository,
                                                       location,
                                                       "WorkDefinitions",
                                                       "wid",
                                                       IOUtils.toByteArray(widIn));
            } else {
                ST widConfigTemplate = new ST(readFile(defaultWidConfigTemplate),
                                              '$',
                                              '$');
                createdUUID = createAssetIfNotExisting(repository,
                                                       location,
                                                       "WorkDefinitions",
                                                       "wid",
                                                       widConfigTemplate.render().getBytes("UTF-8"));
            }
            if (Base64.isBase64(createdUUID)) {
                byte[] decoded = Base64.decodeBase64(createdUUID);
                try {
                    createdUUID = new String(decoded,
                                             "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (vfsService != null && createdUUID != null) {
                Path newWidAssetPath = vfsService.get(createdUUID.replaceAll("\\s",
                                                                             "%20"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Collection<Asset> findWorkitemInfoForUUID(String location,
                                                      Repository repository) {
        Collection<Asset> widAssets = repository.listAssets(location,
                                                            new FilterByExtension(WORKITEM_DEFINITION_EXT) {

                                                                @Override
                                                                public boolean accept(org.uberfire.java.nio.file.Path path) {
                                                                    boolean accept = super.accept(path);
                                                                    return accept && !path.getFileName().toString().startsWith("."); // JBPM-4215 fix: filter out hidden files of the same suffix but different format
                                                                }
                                                            });
        return widAssets;
    }

    private Asset<String> findWorkflowPatterns(Repository repository) {
        return repository.loadAssetFromPath(this.globalDir + "/" + PATTERNS_NAME + PATTERNS_EXT);
    }

    protected static String readFile(String pathname) throws IOException {
        if (pathname == null) {
            return null;
        }

        StringBuilder fileContents = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(pathname),
                                  "UTF-8");
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            IOUtils.closeQuietly(scanner);
        }
    }

    private void deletefile(String file) {
        File f = new File(file);
        boolean success = f.delete();
        if (!success) {
            _logger.info("Unable to delete file: " + file);
        } else {
            _logger.info("Successfully deleted file: " + file);
        }
    }

    private void deletefile(File f) {
        String fname = f.getAbsolutePath();
        boolean success = f.delete();
        if (!success) {
            _logger.info("Unable to delete file: " + fname);
        } else {
            _logger.info("Successfully deleted file: " + fname);
        }
    }

    private void createAndWriteToFile(String file,
                                      String content) throws Exception {
        Writer output = null;
        try {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
                                                               "UTF-8"));
            output.write(content);
            _logger.info("Created file: " + file);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    protected static byte[] getBytesFromFile(File file) throws IOException {
        if (file == null || file.length() > Integer.MAX_VALUE) {
            return null; // File is null or too large
        }

        long length = file.length();
        byte[] bytes;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            bytes = new byte[(int) length];

            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes,
                                          offset,
                                          bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
        return bytes;
    }

    private String createAssetIfNotExisting(Repository repository,
                                            String location,
                                            String name,
                                            String type,
                                            byte[] content) {
        try {
            boolean assetExists = repository.assetExists(location + "/" + name + "." + type);
            if (!assetExists) {
                // create theme asset
                AssetBuilder assetBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
                assetBuilder.content(content)
                        .location(location)
                        .name(name)
                        .type(type)
                        .version("1.0");

                Asset<byte[]> customEditorsAsset = assetBuilder.getAsset();

                return repository.createAsset(customEditorsAsset);
            }
        } catch (Exception e) {
            _logger.error(e.getMessage());
        }

        return null;
    }

    public static String createAbsoluteIconPath(String assetPath,
                                                String iconPath) {
        if (assetPath == null || assetPath.length() < 1) {
            return iconPath;
        }
        if (iconPath == null || iconPath.length() < 1) {
            return assetPath;
        }

        // Handle cases where iconPath doesn't start with ".."
        if (iconPath.startsWith("/")) {
            return iconPath;
        } else if (!iconPath.startsWith("..")) {
            return assetPath + "/" + iconPath;
        }

        // Handle ".." once or more at start of iconPath
        String separator = "/";
        String[] assetFolders = assetPath.split(separator);
        String[] iconFolders = iconPath.split(separator);

        int toRemoveFromIconFolders = 0;
        int toIncludeInAssetFolders = assetFolders.length;
        for (int i = 0; i < iconFolders.length; i++) {
            if ("..".equals(iconFolders[i])) {
                toRemoveFromIconFolders++;
                toIncludeInAssetFolders--;
            } else {
                break;
            }
        }

        StringBuilder sb = new StringBuilder(assetPath.length() + iconPath.length() + 1);
        sb.append(separator);

        for (int i = 1; i < toIncludeInAssetFolders; i++) {
            sb.append(assetFolders[i]).append(separator);
        }
        for (int i = toRemoveFromIconFolders; i < iconFolders.length; i++) {
            sb.append(iconFolders[i]).append(separator);
        }
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }

    private class ThemeInfo {

        private String bgColor;
        private String borderColor;
        private String fontColor;

        public ThemeInfo(String bgColor,
                         String borderColor,
                         String fontColor) {
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            this.fontColor = fontColor;
        }

        public String getBgColor() {
            return bgColor;
        }

        public void setBgColor(String bgColor) {
            this.bgColor = bgColor;
        }

        public String getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(String borderColor) {
            this.borderColor = borderColor;
        }

        public String getFontColor() {
            return fontColor;
        }

        public void setFontColor(String fontColor) {
            this.fontColor = fontColor;
        }
    }

    private class PatternInfo {

        private String id;
        private String name;
        private String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public PatternInfo(String id,
                           String name,
                           String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public class WorkItemDisplayNameComparator implements Comparator<String> {

        private Map<String, WorkDefinitionImpl> workMap;

        public WorkItemDisplayNameComparator(Map<String, WorkDefinitionImpl> workMap) {
            this.workMap = workMap;
        }

        public int compare(String a,
                           String b) {
            try {
                return workMap.get(a).getDisplayName().compareTo(workMap.get(b).getDisplayName());
            } catch (Exception e) {
                return a.compareTo(b);
            }
        }
    }

    public void setGlobalDir(String globalDir) {
        this.globalDir = globalDir;
    }

    public void setBuilderCache(LRUBuilderCache builderCache) {
        this.builderCache = builderCache;
    }
}
