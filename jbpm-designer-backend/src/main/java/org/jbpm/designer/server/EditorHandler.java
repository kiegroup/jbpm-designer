/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.designer.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.*;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpsim.impl.BpsimFactoryImpl;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jboss.errai.ioc.client.api.Caller;
import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.web.plugin.IDiagramPlugin;
import org.jbpm.designer.web.plugin.IDiagramPluginService;
import org.jbpm.designer.web.plugin.impl.PluginServiceImpl;
import org.jbpm.designer.web.preprocessing.IDiagramPreprocessingService;
import org.jbpm.designer.web.preprocessing.IDiagramPreprocessingUnit;
import org.jbpm.designer.web.preprocessing.impl.PreprocessingServiceImpl;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.RepositoryInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.EvaluatorException;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

/**
 * Servlet to load plugin and Oryx stencilset
 */
public class EditorHandler extends HttpServlet {

    private static final long serialVersionUID = -7439613152623067053L;

    private static final Logger _logger =
            Logger.getLogger(EditorHandler.class);

    /**
     * The base path under which the application will be made available at runtime.
     * This constant should be used throughout the application.
     */
    public static final String designer_path = ConfigurationProvider.getInstance().getDesignerContext();

    /**
     * The designer DEV flag looked up from system properties.
     */
    public static final String DEV = "designer.dev";

    /**
     * The designer PREPROCESS flag looked up from system properties.
     */
    public static final String PREPROCESS = "designer.preprocess";

    /**
     * The designer skin param.
     */
    public static final String SKIN = "designer.skin";

    /**
     * The designer bundle version looked up from the manifest.
     */
    public static final String BUNDLE_VERSION = "Bundle-Version";

    /**
     * The designer dev mode setting.
     */
    private boolean _devMode;

    /**
     * The designer preprocess mode setting.
     */
    private boolean _preProcess;

    /**
     * The designer skin setting.
     */
    private String _skin;

    /**
     * The designer version setting.
     */
    private String _designerVersion;

    private String _doc;

    /**
     * The profile service, a global registry to get the
     * profiles.
     */
    @Inject
    private IDiagramProfileService _profileService = null;

    @Inject
    private VFSService vfsServices;

    @Inject
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    /**
     * The pre-processing service, a global registry to get
     * the pre-processing units.
     */
    private IDiagramPreprocessingService _preProcessingService = null;

    /**
     * The plugin service, a global registry for all plugins.
     */
    private IDiagramPluginService _pluginService = null;

    private List<String> _envFiles = new ArrayList<String>();

    private Map<String, List<IDiagramPlugin>> _pluginfiles =
            new HashMap<String, List<IDiagramPlugin>>();

    private Map<String, List<IDiagramPlugin>> _uncompressedPlugins =
            new WeakHashMap<String, List<IDiagramPlugin>>();

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();
        _profileService.init(config.getServletContext());
        _pluginService = PluginServiceImpl.getInstance(
                config.getServletContext());
        _preProcessingService = PreprocessingServiceImpl.INSTANCE;
        _preProcessingService.init(config.getServletContext(), vfsServices, resourceUpdatedEvent, resourceAddedEvent);

        _devMode = Boolean.parseBoolean(System.getProperty(DEV) == null ? config.getInitParameter(DEV) : System.getProperty(DEV));
        _preProcess = Boolean.parseBoolean(System.getProperty(PREPROCESS) == null ? config.getInitParameter(PREPROCESS) : System.getProperty(PREPROCESS));
        _skin = System.getProperty(SKIN) == null ? config.getInitParameter(SKIN) : System.getProperty(SKIN);
        _designerVersion = readDesignerVersion(config.getServletContext());


        String editor_file = config.
                getServletContext().getRealPath(designer_path + "editor.st");
        try {
            _doc = readFile(editor_file);
        } catch (Exception e) {
            throw new ServletException(
                    "Error while parsing editor.st", e);
        }
        if (_doc == null) {
            _logger.error("Invalid editor.st, " +
                    "could not be read as a document.");
            throw new ServletException("Invalid editor.st, " +
                    "could not be read as a document.");
        }

        try {
            initEnvFiles(getServletContext(), config);
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Initiate the compression of the environment.
     *
     * @param context
     * @throws java.io.IOException
     */
    private void initEnvFiles(ServletContext context, ServletConfig config) throws IOException {
        // only do it the first time the servlet starts
        try {
            JSONObject obj = new JSONObject(readEnvFiles(context));

            JSONArray array = obj.getJSONArray("files");
            for (int i = 0; i < array.length(); i++) {
                _envFiles.add(array.getString(i));
            }
        } catch (JSONException e) {
            _logger.error("invalid js_files.json");
            _logger.error(e.getMessage(), e);
            throw new RuntimeException("Error initializing the " +
                    "environment of the editor");
        }

        // generate script to setup the languages
        if (!_devMode) {
            if (_logger.isInfoEnabled()) {
                _logger.info(
                        "The diagram editor is running in production mode. " +
                                "Javascript will be served compressed");
            }
            StringWriter sw = new StringWriter();
            List<InputStream> codes = new ArrayList<InputStream>();
            for (String file : _envFiles) {
                codes.add(new FileInputStream(new File(getServletContext().getRealPath(designer_path + file))));
            }

            try {
                sw.append(compileJS(_envFiles, codes));
                sw.append("\n");
            } catch (EvaluatorException e) {
                _logger.error(e.getMessage(), e);
            } catch (IOException e) {
                _logger.error(e.getMessage(), e);
            } finally {
                try {
                    for (InputStream inputStream : codes) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                }
            }


            try {
                FileWriter w = new FileWriter(
                        context.getRealPath(designer_path + "jsc/env_combined.js"));
                w.write(sw.toString());
                w.close();
            } catch (IOException e) {
                _logger.error(e.getMessage(), e);
            }
        } else {
            if (_logger.isInfoEnabled()) {
                _logger.info(
                        "The diagram editor is running in development mode. " +
                                "Javascript will be served uncompressed");
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        String profileName = request.getParameter("profile");
        String uuid = request.getParameter("uuid");

        String editorID = request.getParameter("editorid");

        String encodedActiveNodes = request.getParameter("activenodes");
        byte[] activeNodesByteArray = Base64.decodeBase64(encodedActiveNodes);
        String activeNodes = new String(activeNodesByteArray, "UTF-8");

        String encodedCompletedNodes = request.getParameter("completednodes");
        byte[] completedNodesByteArray = Base64.decodeBase64(encodedCompletedNodes);
        String completedNodes = new String(completedNodesByteArray, "UTF-8");

        // this is set in the persenter now
//        String encodedProcessSource = request.getParameter("processsource");
//        if (encodedProcessSource == null) {
//            encodedProcessSource = "";
//        }

        String readOnly = request.getParameter("readonly");


        if (profileName == null || profileName.length() < 1) {
            profileName = "jbpm";
        }
        IDiagramProfile profile = _profileService.findProfile(
                request, profileName);
        if (profile == null) {
            _logger.error("No profile with the name " + profileName
                    + " was registered");
            throw new IllegalArgumentException(
                    "No profile with the name " + profileName +
                            " was registered");
        }

        IDiagramPreprocessingUnit preprocessingUnit = null;
        if (_preProcess) {
            if (_logger.isInfoEnabled()) {
                _logger.info(
                        "Performing diagram information pre-processing steps. ");
            }
            preprocessingUnit = _preProcessingService.findPreprocessingUnit(request, profile);
            preprocessingUnit.preprocess(request, response, profile, getServletContext());
        }

        //output env javascript files
        JSONArray scriptsArray;
        if (_devMode) {
            scriptsArray = new JSONArray();
            for (String nextScript : _envFiles) {
                scriptsArray.put(designer_path + nextScript);
            }

        } else {
            scriptsArray = new JSONArray();
            scriptsArray.put(designer_path + "jsc/env_combined.js");
        }

        // generate script tags for plugins.
        // they are located after the initialization script.

        if (_pluginfiles.get(profileName) == null) {
            List<IDiagramPlugin> compressed = new ArrayList<IDiagramPlugin>();
            List<IDiagramPlugin> uncompressed = new ArrayList<IDiagramPlugin>();
            _pluginfiles.put(profileName, compressed);
            _uncompressedPlugins.put(profileName, uncompressed);
            for (String pluginName : profile.getPlugins()) {
                IDiagramPlugin plugin = _pluginService.findPlugin(request,
                        pluginName);
                if (plugin == null) {
                    _logger.warn("Could not find the plugin " + pluginName +
                            " requested by the profile " + profile.getName());
                    continue;
                }
                if (plugin.isCompressable()) {
                    compressed.add(plugin);
                } else {
                    uncompressed.add(plugin);
                }
            }

            if (!_devMode) {
                // let's call the compression routine
                String rs = compressJS(_pluginfiles.get(profileName),
                        getServletContext());
                try {
                    FileWriter w = new FileWriter(getServletContext().
                            getRealPath(designer_path + "jsc/plugins_" + profileName
                                    + ".js"));
                    w.write(rs.toString());
                    w.close();
                } catch (Exception e) {
                    _logger.error(e.getMessage(), e);
                }
            }
        }

        JSONArray pluginsArray = new JSONArray();
        if (_devMode) {
            for (IDiagramPlugin jsFile : _pluginfiles.get(profileName)) {
                pluginsArray.put("/plugin/" + jsFile.getName() + ".js");
            }
        } else {
            pluginsArray.put(designer_path + "jsc/plugins_" + profileName + ".js");
        }

        for (IDiagramPlugin uncompressed :
                _uncompressedPlugins.get(profileName)) {
            pluginsArray.put(designer_path + "plugin/" + uncompressed.getName() + ".js");
        }

        StringTemplate editorTemplate = new StringTemplate(_doc);
        editorTemplate.setAttribute("editorprofile", profileName);
        editorTemplate.setAttribute("editoruuid", uuid);
        editorTemplate.setAttribute("editorid", editorID);
        editorTemplate.setAttribute("activenodes", activeNodes);
        editorTemplate.setAttribute("completednodes", completedNodes);
        //editorTemplate.setAttribute("processsource", encodedProcessSource);
        editorTemplate.setAttribute("readonly", readOnly);
        editorTemplate.setAttribute("allscripts", scriptsArray.toString());
        editorTemplate.setAttribute("allplugins", pluginsArray.toString());
        editorTemplate.setAttribute("title", profile.getTitle());
        editorTemplate.setAttribute("stencilset", profile.getStencilSet());
        editorTemplate.setAttribute("debug", _devMode);
        editorTemplate.setAttribute("preprocessing", preprocessingUnit == null ? "" : preprocessingUnit.getOutData());
        editorTemplate.setAttribute("externalprotocol", RepositoryInfo.getRepositoryProtocol(profile) == null ? "" : RepositoryInfo.getRepositoryProtocol(profile));
        editorTemplate.setAttribute("externalhost", RepositoryInfo.getRepositoryHost(profile));
        editorTemplate.setAttribute("externalsubdomain", RepositoryInfo.getRepositorySubdomain(profile) != null ? RepositoryInfo.getRepositorySubdomain(profile).substring(0,
                RepositoryInfo.getRepositorySubdomain(profile).indexOf("/")) : "");
        editorTemplate.setAttribute("localhistoryenabled", profile.getLocalHistoryEnabled());
        editorTemplate.setAttribute("localhistorytimeout", profile.getLocalHistoryTimeout());
        editorTemplate.setAttribute("designerversion", _designerVersion);
        editorTemplate.setAttribute("storesvgonsave", profile.getStoreSVGonSaveOption());
        editorTemplate.setAttribute("defaultSkin", designer_path + "css/theme-default.css");

        String overlaySkin = "";
        if (_skin != null && !_skin.equals("default")) {
            overlaySkin = designer_path + "css/theme-" + _skin + ".css";
        }
        editorTemplate.setAttribute("overlaySkin", overlaySkin);

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
        editorTemplate.setAttribute("profileplugins", plugins.toString());

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
        editorTemplate.setAttribute("ssextensions", ssexts.toString());

        editorTemplate.setAttribute("contextroot", request.getContextPath());

        response.setContentType("text/javascript; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(editorTemplate.toString());
    }

    /**
     * @return read the files to be placed as core scripts
     *         from a configuration file in a json file.
     * @throws java.io.IOException
     */
    private static String readEnvFiles(ServletContext context) throws IOException {
        FileInputStream core_scripts = new FileInputStream(
                context.getRealPath(designer_path + "js/js_files.json"));
        try {
            ByteArrayOutputStream stream =
                    new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = core_scripts.read(buffer)) != -1) {
                stream.write(buffer, 0, read);
            }
            return stream.toString();
        } finally {
            try {
                core_scripts.close();
            } catch (IOException e) {
                _logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Compress a list of js files into one combined string
     *
     * @param plugins list of js files
     * @return a string that contains all the compressed data
     * @throws org.mozilla.javascript.EvaluatorException
     *
     * @throws java.io.IOException
     */
    private static String compressJS(Collection<IDiagramPlugin> plugins,
                                     ServletContext context) {
        StringWriter sw = new StringWriter();
        for (IDiagramPlugin plugin : plugins) {
            sw.append("/* ").append(plugin.getName()).append(" */\n");
            InputStream input = plugin.getContents();
            try {
                sw.append(compileJS(plugin.getName(), input));
            } catch (EvaluatorException e) {
                _logger.error(e.getMessage(), e);
            } catch (IOException e) {
                _logger.error(e.getMessage(), e);
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }

            sw.append("\n");
        }
        return sw.toString();
    }


    /**
     * Determine whether the browser is IE
     *
     * @param request
     * @return true: IE browser false: others browsers
     */
    private static boolean isIE(HttpServletRequest request) {
        return request.getHeader("USER-AGENT").
                toLowerCase().indexOf("msie") > 0;
    }

    /**
     * Returns the designer version from the manifest.
     *
     * @param context
     * @return version
     */
    private static String readDesignerVersion(ServletContext context) {
        String retStr = "";
        try {
            InputStream inputStream = context.getResourceAsStream("/META-INF/MANIFEST.MF");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(BUNDLE_VERSION)) {
                    retStr = line.substring(BUNDLE_VERSION.length() + 1);
                    retStr = retStr.trim();
                }
            }
            inputStream.close();
        } catch (Exception e) {
            _logger.error(e.getMessage(), e);
        }
        return retStr;
    }

    public static String compileJS(String filename, InputStream code) throws IOException {
        List<String> filenames = new ArrayList<String>();
        List<InputStream> codes = new ArrayList<InputStream>();

        filenames.add(filename);
        codes.add(code);

        return compileJS(filenames, codes);
    }

    public static String compileJS(List<String> filenames, List<InputStream> codes) throws IOException {
        com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler();

        CompilerOptions options = new CompilerOptions();
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(
                options);

        // To get the complete set of externs, the logic in
        // CompilerRunner.getDefaultExterns() should be used here.
        JSSourceFile extern = JSSourceFile.fromCode("externs.js",
                "function alert(x) {}");

        // The dummy input name "input.js" is used here so that any warnings or
        // errors will cite line numbers in terms of input.js.
        List<JSSourceFile> inputs = new ArrayList<JSSourceFile>();
        for (int i = 0; i < filenames.size(); i++) {
            inputs.add(JSSourceFile.fromInputStream(filenames.get(i), codes.get(i)));
        }

        // compile() returns a Result, but it is not needed here.
        Result results = compiler.compile(extern, inputs.toArray(new JSSourceFile[inputs.size()]), options);

        // The compiler is responsible for generating the compiled code; it is not
        // accessible via the Result.
        return compiler.toSource();
    }

    private String readFile(String pathname) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new File(pathname), "UTF-8");
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
}