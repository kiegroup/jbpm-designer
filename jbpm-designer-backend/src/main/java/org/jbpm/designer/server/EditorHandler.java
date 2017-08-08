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
package org.jbpm.designer.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.WeakHashMap;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpsim.impl.BpsimFactoryImpl;
import org.apache.commons.io.IOUtils;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.repository.vfs.RepositoryDescriptor;
import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.plugin.IDiagramPlugin;
import org.jbpm.designer.web.plugin.IDiagramPluginService;
import org.jbpm.designer.web.plugin.impl.PluginServiceImpl;
import org.jbpm.designer.web.preprocessing.IDiagramPreprocessingService;
import org.jbpm.designer.web.preprocessing.IDiagramPreprocessingUnit;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.RepositoryInfo;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.io.IOService;

@WebServlet(displayName = "Oryx Editor Handler", name = "EditorHandler",
        urlPatterns = {"/editor", "/editor/*"},
        initParams = {
                @WebInitParam(name = "designer.dev", value = "true"),
                @WebInitParam(name = "designer.preprocess", value = "true"),
                @WebInitParam(name = "designer.locale", value = "en"),
                @WebInitParam(name = "designer.skin", value = "default")})
public class EditorHandler extends HttpServlet {

    private static final long serialVersionUID = -7439613152623067053L;

    private static final Logger logger =
            LoggerFactory.getLogger(EditorHandler.class);

    /**
     * The base path under which the application will be made available at runtime.
     * This constant should be used throughout the application.
     */
    public static final String DESIGNER_PATH = ConfigurationProvider.getInstance().getDesignerContext();

    /**
     * The designer DEV flag looked up from system properties.
     */
    public static final String DEV = "designer.dev";

    /**
     * The designer USEOLDDATAASSIGNMENTS flag looked up from system properties.
     */
    public static final String USEOLDDATAASSIGNMENTS = "designer.useolddataassignments";

    public static final String SHOW_PDF_DOC = "designer.showpdfdoc";

    public static final String PRESET_PERSPECTIVE = "org.jbpm.designer.perspective";

    public static final String BPSIM_DISPLAY = "org.jbpm.designer.bpsimdisplay";

    public static final String FORMS_TYPE = "org.jbpm.designer.formstype";

    /**
     * The designer PREPROCESS flag looked up from system properties.
     */
    public static final String PREPROCESS = "designer.preprocess";

    /**
     * The designer skin param.
     */
    public static final String SKIN = "designer.skin";

    public static final String SERVICE_REPO = "org.jbpm.service.repository";

    public static final String SERVICE_REPO_TASKS = "org.jbpm.service.servicetasknames";

    /**
     * The designer bundle version looked up from the manifest.
     */
    public static final String BUNDLE_VERSION = "Bundle-Version";

    /**
     * Used to enable/disable storing of SVG when process is saved.
     */
    public static final String STORE_SVG_ON_SAVE = "org.jbpm.designer.storesvgonsave";

    /**
     * The designer dev mode setting.
     */
    private boolean devMode;

    /**
     * The designer use old data assignments setting.
     */
    private boolean useOldDataAssignments;

    /**
     * Show / Hide PDF Documentation display option
     */
    private boolean showPDFDoc;

    /**
     * The designer preprocess mode setting.
     */
    private boolean preProcess;

    /**
     * The designer skin setting.
     */
    private String skin;

    /**
     * The designer version setting.
     */
    private String designerVersion;

    private String doc;

    private IDiagramProfile profile;

    private String serviceRepo;
    private String serviceRepoTasks;

    /**
     * The profile service, a global registry to get the
     * profiles.
     */
    @Inject
    private IDiagramProfileService profileService = null;

    @Inject
    private VFSService vfsServices;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private RepositoryDescriptor descriptor;

    /**
     * The pre-processing service, a global registry to get
     * the pre-processing units.
     */
    @Inject
    private IDiagramPreprocessingService preProcessingService;

    /**
     * The plugin service, a global registry for all plugins.
     */
    private IDiagramPluginService pluginservice = null;

    private List<String> envFiles = new ArrayList<String>();

    private Map<String, List<IDiagramPlugin>> pluginFiles =
            new HashMap<String, List<IDiagramPlugin>>();

    private Map<String, List<IDiagramPlugin>> uncompressedPlugins =
            new WeakHashMap<String, List<IDiagramPlugin>>();

    private ST editorTemplate;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();
        profileService.init(config.getServletContext());
        pluginservice = PluginServiceImpl.getInstance(
                config.getServletContext());
        preProcessingService.init(config.getServletContext(),
                                  vfsServices);

        devMode = Boolean.parseBoolean(System.getProperty(DEV) == null ? config.getInitParameter(DEV) : System.getProperty(DEV));
        useOldDataAssignments = Boolean.parseBoolean(System.getProperty(USEOLDDATAASSIGNMENTS) == null ? config.getInitParameter(USEOLDDATAASSIGNMENTS) : System.getProperty(USEOLDDATAASSIGNMENTS));
        preProcess = Boolean.parseBoolean(System.getProperty(PREPROCESS) == null ? config.getInitParameter(PREPROCESS) : System.getProperty(PREPROCESS));
        skin = System.getProperty(SKIN) == null ? config.getInitParameter(SKIN) : System.getProperty(SKIN);
        designerVersion = readDesignerVersion(config.getServletContext());
        showPDFDoc = doShowPDFDoc(config);
        serviceRepo = System.getProperty(SERVICE_REPO) == null ? config.getInitParameter(SERVICE_REPO) : System.getProperty(SERVICE_REPO);
        serviceRepoTasks = System.getProperty(SERVICE_REPO_TASKS) == null ? config.getInitParameter(SERVICE_REPO_TASKS) : System.getProperty(SERVICE_REPO_TASKS);

        String editor_file = config.
                getServletContext().getRealPath(DESIGNER_PATH + "editor.st");
        try {
            doc = readFile(editor_file);
        } catch (Exception e) {
            throw new ServletException(
                    "Error while parsing editor.st",
                    e);
        }
        if (doc == null) {
            logger.error("Invalid editor.st, " +
                                 "could not be read as a document.");
            throw new ServletException("Invalid editor.st, " +
                                               "could not be read as a document.");
        }
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        doGet(request,
              response);
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        String profileName = Utils.getDefaultProfileName(request.getParameter("profile"));
        String uuid = Utils.getUUID(request);

        String editorID = request.getParameter("editorid");

        // passed through via JSNI now
//        String encodedActiveNodes = request.getParameter("activenodes");
//        byte[] activeNodesByteArray = Base64.decodeBase64(encodedActiveNodes);
//        String activeNodes = new String(activeNodesByteArray, "UTF-8");
//
//        String encodedCompletedNodes = request.getParameter("completednodes");
//        byte[] completedNodesByteArray = Base64.decodeBase64(encodedCompletedNodes);
//        String completedNodes = new String(completedNodesByteArray, "UTF-8");

        // this is set in the persenter now
//        String encodedProcessSource = request.getParameter("processsource");
//        if (encodedProcessSource == null) {
//            encodedProcessSource = "";
//        }

        String readOnly = request.getParameter("readonly");
        String viewLocked = request.getParameter("viewlocked");
        String sessionId = request.getParameter("sessionId");

        if (viewLocked == null || viewLocked.length() < 1) {
            viewLocked = "false";
        }

        if (profile == null) {
            profile = profileService.findProfile(request,
                                                 profileName);
        }

        if (profile == null) {
            logger.error("No profile with the name " + profileName
                                 + " was registered");
            throw new IllegalArgumentException(
                    "No profile with the name " + profileName +
                            " was registered");
        }

        IDiagramPreprocessingUnit preprocessingUnit = null;
        if (preProcess) {
            if (logger.isInfoEnabled()) {
                logger.info(
                        "Performing diagram information pre-processing steps. ");
            }
            preprocessingUnit = preProcessingService.findPreprocessingUnit(request,
                                                                           profile);
            preprocessingUnit.preprocess(request,
                                         response,
                                         profile,
                                         getServletContext(),
                                         Boolean.parseBoolean(readOnly),
                                         Boolean.parseBoolean(viewLocked),
                                         ioService,
                                         descriptor);
        }

        //output env javascript files
        JSONArray scriptsArray;
        if (devMode) {
            scriptsArray = new JSONArray();
            for (String nextScript : envFiles) {
                scriptsArray.put(DESIGNER_PATH + nextScript);
            }
        } else {
            scriptsArray = new JSONArray();
            scriptsArray.put(DESIGNER_PATH + "jsc/env_combined.js");
        }

        // generate script tags for plugins.
        // they are located after the initialization script.

        if (pluginFiles.get(profileName) == null) {
            List<IDiagramPlugin> compressed = new ArrayList<IDiagramPlugin>();
            List<IDiagramPlugin> uncompressed = new ArrayList<IDiagramPlugin>();
            pluginFiles.put(profileName,
                            compressed);
            uncompressedPlugins.put(profileName,
                                    uncompressed);
            for (String pluginName : profile.getPlugins()) {
                IDiagramPlugin plugin = pluginservice.findPlugin(request,
                                                                 pluginName);
                if (plugin == null) {
                    logger.warn("Could not find the plugin " + pluginName +
                                        " requested by the profile " + profile.getName());
                    continue;
                }
                if (plugin.isCompressable()) {
                    compressed.add(plugin);
                } else {
                    uncompressed.add(plugin);
                }
            }
        }

        JSONArray pluginsArray = new JSONArray();
        if (devMode) {
            for (IDiagramPlugin jsFile : pluginFiles.get(profileName)) {
                pluginsArray.put("/plugin/" + jsFile.getName() + ".js");
            }
        } else {
            pluginsArray.put(DESIGNER_PATH + "jsc/plugins_" + profileName + ".js");
        }

        for (IDiagramPlugin uncompressed :
                uncompressedPlugins.get(profileName)) {
            pluginsArray.put(DESIGNER_PATH + "plugin/" + uncompressed.getName() + ".js");
        }

        editorTemplate = new ST(doc,
                                '$',
                                '$');
        editorTemplate.add("bopen",
                           "{");
        editorTemplate.add("bclose",
                           "}");
        editorTemplate.add("editorprofile",
                           profileName);
        editorTemplate.add("editoruuid",
                           uuid);
        editorTemplate.add("editorid",
                           editorID);
        editorTemplate.add("sessionid",
                           sessionId);
        editorTemplate.add("instanceviewmode",
                           getInstanceViewMode(request));
        //editorTemplate.add("activenodes", activeNodes);
        //editorTemplate.add("completednodes", completedNodes);
        //editorTemplate.add("processsource", encodedProcessSource);
        editorTemplate.add("readonly",
                           readOnly);
        editorTemplate.add("viewlocked",
                           viewLocked);
        editorTemplate.add("allscripts",
                           scriptsArray.toString());
        editorTemplate.add("allplugins",
                           pluginsArray.toString());
        editorTemplate.add("title",
                           profile.getTitle());
        editorTemplate.add("stencilset",
                           profile.getStencilSet());
        editorTemplate.add("debug",
                           devMode);
        editorTemplate.add("useolddataassignments",
                           useOldDataAssignments);
        editorTemplate.add("preprocessing",
                           preprocessingUnit == null ? "" : preprocessingUnit.getOutData());
        editorTemplate.add("externalprotocol",
                           RepositoryInfo.getRepositoryProtocol(profile) == null ? "" : RepositoryInfo.getRepositoryProtocol(profile));
        editorTemplate.add("externalhost",
                           RepositoryInfo.getRepositoryHost(profile));
        editorTemplate.add("externalsubdomain",
                           RepositoryInfo.getRepositorySubdomain(profile) != null ? RepositoryInfo.getRepositorySubdomain(profile).substring(0,
                                                                                                                                             RepositoryInfo.getRepositorySubdomain(profile).indexOf("/")) : "");
        editorTemplate.add("repositoryid",
                           "designer");
        editorTemplate.add("localhistoryenabled",
                           profile.getLocalHistoryEnabled());
        editorTemplate.add("localhistorytimeout",
                           profile.getLocalHistoryTimeout());
        editorTemplate.add("designerversion",
                           designerVersion);
        editorTemplate.add("showpdfdoc",
                           showPDFDoc);
        editorTemplate.add("storesvgonsave",
                           getCheckedStoreSVGOnSaveOption(profile));
        editorTemplate.add("defaultSkin",
                           DESIGNER_PATH + "css/theme-default.css");
        editorTemplate.add("presetperspective",
                           System.getProperty(PRESET_PERSPECTIVE) == null ? "" : System.getProperty(PRESET_PERSPECTIVE));
        editorTemplate.add("bpsimdisplay",
                           profile.getBpsimDisplay());
        editorTemplate.add("formstype",
                           profile.getFormsType());

        String overlaySkin = "";
        if (skin != null && !skin.equals("default")) {
            overlaySkin = DESIGNER_PATH + "css/theme-" + skin + ".css";
        }
        editorTemplate.add("overlaySkin",
                           overlaySkin);

        StringBuilder plugins = new StringBuilder();
        boolean commaNeeded = false;
        for (String ext : profile.getPlugins()) {
            if (commaNeeded) {
                plugins.append(",");
            } else {
                commaNeeded = true;
            }
            plugins.append("\"").append(ext).append("\"");
        }
        editorTemplate.add("profileplugins",
                           plugins.toString());

        StringBuilder ssexts = new StringBuilder();
        commaNeeded = false;
        for (String ext : profile.getStencilSetExtensions()) {
            if (commaNeeded) {
                ssexts.append(",");
            } else {
                commaNeeded = true;
            }
            ssexts.append("\"").append(ext).append("\"");
        }
        editorTemplate.add("ssextensions",
                           ssexts.toString());

        editorTemplate.add("contextroot",
                           request.getContextPath());

        editorTemplate.add("servicerepo",
                           serviceRepo == null ? "" : serviceRepo);
        editorTemplate.add("servicerepotasks",
                           serviceRepoTasks == null ? "" : Arrays.asList(serviceRepoTasks.split(",")));

        response.setContentType("text/javascript; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(editorTemplate.render());
    }

    /**
     * Returns the designer version from the manifest.
     * @param context
     * @return version
     */
    public static String readDesignerVersion(ServletContext context) {
        String retStr = "";
        BufferedReader br = null;
        try {
            InputStream inputStream = context.getResourceAsStream("/META-INF/MANIFEST.MF");
            br = new BufferedReader(new InputStreamReader(inputStream,
                                                          "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(BUNDLE_VERSION)) {
                    retStr = line.substring(BUNDLE_VERSION.length() + 1);
                    retStr = retStr.trim();
                }
            }
            inputStream.close();
        } catch (Exception e) {
            logger.error(e.getMessage(),
                         e);
        } finally {
            if (br != null) {
                IOUtils.closeQuietly(br);
            }
        }
        return retStr;
    }

    public String getInstanceViewMode(HttpServletRequest request) {
        String instanceViewMode = request.getParameter("instanceviewmode");
        return (instanceViewMode != null && instanceViewMode.equals("true")) ? "true" : "false";
    }

    private String readFile(String pathname) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new File(pathname),
                                      "UTF-8");
        String lineSeparator = System.getProperty("line.separator");
        try {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

    public boolean doShowPDFDoc(ServletConfig config) {
        return Boolean.parseBoolean(System.getProperty(SHOW_PDF_DOC) == null ? config.getInitParameter(SHOW_PDF_DOC) : System.getProperty(SHOW_PDF_DOC));
    }

    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }

    public IDiagramProfile getProfile() {
        return profile;
    }

    public ST getEditorTemplate() {
        return editorTemplate;
    }

    private String getCheckedStoreSVGOnSaveOption(IDiagramProfile profile) {
        String sysPropOption = System.getProperty(STORE_SVG_ON_SAVE);
        if (sysPropOption != null) {
            if ("true".equalsIgnoreCase(sysPropOption) || "false".equalsIgnoreCase(sysPropOption)) {
                return String.valueOf(Boolean.parseBoolean(sysPropOption));
            }
        }
        return profile.getStoreSVGonSaveOption();
    }
}
