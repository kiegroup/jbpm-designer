/***************************************
 * Copyright (c) 2008-2010
 * Philipp Berger 2009
 * Intalio, Inc 2010
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/

package org.oryxeditor.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.EvaluatorException;

import com.intalio.web.plugin.IDiagramPlugin;
import com.intalio.web.plugin.IDiagramPluginService;
import com.intalio.web.plugin.impl.PluginServiceImpl;
import com.intalio.web.profile.IDiagramProfile;
import com.intalio.web.profile.IDiagramProfileService;
import com.intalio.web.profile.impl.ExternalInfo;
import com.intalio.web.profile.impl.ProfileServiceImpl;
import com.intalio.web.preference.IDiagramPreference;
import com.intalio.web.preference.IDiagramPreferenceService;
import com.intalio.web.preprocessing.IDiagramPreprocessingService;
import com.intalio.web.preprocessing.IDiagramPreprocessingUnit;
import com.intalio.web.preprocessing.impl.PreprocessingServiceImpl;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * Servlet to load plugin and Oryx stencilset
 */
public class EditorHandler extends HttpServlet {

    private static final long serialVersionUID = -7439613152623067053L;

    /**
     * da logger
     */
    private static final Logger _logger = 
        Logger.getLogger(EditorHandler.class);
    
    public static IDiagramPreferenceService PREFERENCE_FACTORY = new IDiagramPreferenceService() {

        public IDiagramPreference createPreference(HttpServletRequest req) {
            //later we could read the parameters from the URL as well.
            return new IDiagramPreference() {

                public boolean isAutoSaveEnabled() {
                    return true;
                }

                public int getAutosaveInterval() {
                    return 120000;
                }
            };
        }
    };
    
    /**
     * The base path under which the application will be made available at runtime.
     * This constant should be used throughout the application.
     */
    public static final String oryx_path = "/designer/";
    
    /**
     * The designer DEV flag looked up from system properties.
     */
    public static final String DEV = "designer.dev";
    
    /**
     * The designer PREPROCESS flag looked up from system properties.
     */
    public static final String PREPROCESS = "designer.preprocess";
    
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
     * The designer version setting.
     */
    private String _designerVersion;
    
    /**
     * The profile service, a global registry to get the
     * profiles.
     */
    private IDiagramProfileService _profileService = null;
    
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
    
    /**
     * editor.html document.
     */
    private Document _doc = null;  
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        _profileService = ProfileServiceImpl.INSTANCE;
        _profileService.init(config.getServletContext());
        _pluginService = PluginServiceImpl.getInstance(
                config.getServletContext());
        _preProcessingService = PreprocessingServiceImpl.INSTANCE;
        _preProcessingService.init(config.getServletContext());
        
        _devMode = Boolean.parseBoolean( System.getProperty(DEV) == null ? config.getInitParameter(DEV) : System.getProperty(DEV) );
        _preProcess = Boolean.parseBoolean( System.getProperty(PREPROCESS) == null ? config.getInitParameter(PREPROCESS) : System.getProperty(PREPROCESS) );
        _designerVersion = readDesignerVersion(config.getServletContext());
        
        String editor_file = config.
            getServletContext().getRealPath("/editor.html");
        try {
            _doc = readDocument(editor_file);
        } catch (Exception e) {
            throw new ServletException(
                    "Error while parsing editor.html", e);
        }
        if (_doc == null) {
            _logger.error("Invalid editor.html, " +
                    "could not be read as a document.");
            throw new ServletException("Invalid editor.html, " +
                    "could not be read as a document.");
        }
        
        Element root = _doc.getRootElement();
        Element head = root.getChild("head", root.getNamespace());
        if (head == null) {
            _logger.error("Invalid editor.html. No html or head tag");
            throw new ServletException("Invalid editor.html. " +
                    "No html or head tag");
        }
        
        
        try {
            initEnvFiles(getServletContext(), config);
        } catch (IOException e) {
            throw new ServletException(e);
        }            
    }

    /**
     * Initiate the compression of the environment.
     * @param context
     * @throws IOException
     */
    private void initEnvFiles(ServletContext context, ServletConfig config) throws IOException {
        // only do it the first time the servlet starts
        try {
            JSONObject obj = new JSONObject(readEnvFiles(context));
    
            JSONArray array = obj.getJSONArray("files");
            for (int i = 0 ; i < array.length() ; i++) {
                _envFiles.add(array.getString(i));
            }
        } catch (JSONException e) {
            _logger.error("invalid js_files.json");
            _logger.error(e.getMessage(), e);
            throw new RuntimeException("Error initializing the " +
                "environment of the editor");
        }
    
        // generate script to setup the languages
        _envFiles.add("i18n/translation_en_us.js");
        if (!_devMode) {
            StringWriter sw = new StringWriter();
            for (String file : _envFiles) {
                sw.append("/* ").append(file).append(" */\n");
                InputStream input = new FileInputStream(new File(getServletContext().getRealPath(file)));
                try {
                    JavaScriptCompressor compressor = new JavaScriptCompressor(
                            new InputStreamReader(input), null);
                    compressor.compress(sw, -1, false, false, false, false);
                } catch (EvaluatorException e) {
                    _logger.error(e.getMessage(), e);
                } catch (IOException e) {
                    _logger.error(e.getMessage(), e);
                } finally {
                    try { input.close(); } catch (IOException e) {}
                }

                sw.append("\n");
            }
            try {
                FileWriter w = new FileWriter(
                        context.getRealPath("jsc/env_combined.js"));
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

    protected void doGet(HttpServletRequest request, 
            HttpServletResponse response) 
            throws ServletException, IOException {
        Document doc = (Document) _doc.clone();
        String profileName = request.getParameter("profile");
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
        if(_preProcess) {
            if (_logger.isInfoEnabled()) {
                _logger.info(
                    "Performing diagram information pre-processing steps. ");
            }
            preprocessingUnit = _preProcessingService.findPreprocessingUnit(request, profile);
            preprocessingUnit.preprocess(request, response, profile);
        }

        //output env javascript files
        if (_devMode) {
            for (String jsFile : _envFiles) {
                addScript(doc, oryx_path + jsFile, true);
            }
        } else {
            addScript(doc, oryx_path + "jsc/env_combined.js", true);
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
                            getRealPath("jsc/plugins_" + profileName 
                                    + ".js"));
                    w.write(rs.toString());
                    w.close();
                } catch (Exception e) {
                    _logger.error(e.getMessage(), e);
                }
            }
        }
        
        if (_devMode) {
            for (IDiagramPlugin jsFile : _pluginfiles.get(profileName)) {
                addScript(doc, oryx_path + "plugin/" + jsFile.getName() 
                        + ".js", true);
            }
        } else {
            addScript(doc, 
                    oryx_path + "jsc/plugins_" + profileName + ".js", 
                    false);
        }
        
        for (IDiagramPlugin uncompressed : 
                _uncompressedPlugins.get(profileName)) {
            addScript(doc, oryx_path + "plugin/" + uncompressed.getName() 
                    + ".js", false);
        }
        
        // send the updated editor.html to client 
        if(!isIE(request)){
            response.setContentType("application/xhtml+xml");
        }
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setExpandEmptyElements(true);
        outputter.setFormat(format);
        String html = outputter.outputString(doc);
        StringTokenizer tokenizer = new StringTokenizer(
                html, "@", true);
        StringBuilder resultHtml = new StringBuilder();
        boolean tokenFound = false;
        boolean replacementMade = false;
      
        IDiagramPreference pref = PREFERENCE_FACTORY.createPreference(request);
        int autoSaveInt = pref.getAutosaveInterval();
        boolean autoSaveOn = pref.isAutoSaveEnabled();

        while(tokenizer.hasMoreTokens()) {
            String elt = tokenizer.nextToken();
            if ("title".equals(elt)) {
                resultHtml.append(profile.getTitle());
                replacementMade = true;
            } else if ("stencilset".equals(elt)) {
                resultHtml.append(profile.getStencilSet());
                replacementMade = true;
            } else if ("debug".equals(elt)) {
                resultHtml.append(_devMode);
                replacementMade = true;
            } else if ("autosaveinterval".equals(elt)) {
                resultHtml.append(autoSaveInt);
                replacementMade = true;
            } else if ("autosavedefault".equals(elt)) {
                resultHtml.append(autoSaveOn);
                replacementMade = true;    
            } else if ("preprocessing".equals(elt)) {
                resultHtml.append(preprocessingUnit == null ? "" : preprocessingUnit.getOutData());
                replacementMade = true;    
            } else if ("externalprotocol".equals(elt)) {
                resultHtml.append(ExternalInfo.getExternalProtocol(profile));
                replacementMade = true;    
            } else if ("externalhost".equals(elt)) {
                resultHtml.append(ExternalInfo.getExternalHost(profile));
                replacementMade = true;    
            } else if ("externalsubdomain".equals(elt)) {
                resultHtml.append(profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/")));
                replacementMade = true;    
            } else if ("designerversion".equals(elt)) { 
                resultHtml.append(_designerVersion);
                replacementMade = true;    
            } else if ("profileplugins".equals(elt)) {
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
                resultHtml.append(plugins.toString());
                replacementMade = true;
            } else if ("ssextensions".equals(elt)) {
                StringBuilder ssexts = new StringBuilder();
                boolean commaNeeded = false;
                for (String ext : profile.getStencilSetExtensions()) {
                    if (commaNeeded) {
                        ssexts.append(",");
                    } else {
                        commaNeeded = true;
                    }
                    ssexts.append("\"").append(ext).append("\"");
                }
                resultHtml.append(ssexts.toString());
                replacementMade = true;
            } else if ("@".equals(elt)) {
                if (replacementMade) {
                    tokenFound = false;
                    replacementMade = false;
                } else {
                    tokenFound = true;
                }
            } else {
                if (tokenFound) {
                    tokenFound = false;
                    resultHtml.append("@");
                }
                resultHtml.append(elt);
            }
        }
        
        response.getWriter().write(resultHtml.toString());
    }
    
    /**
     * Reads the document from the file at the given path
     * @param path the path to the file
     * @return a document
     * @throws JDOMException
     * @throws IOException
     */
    private static Document readDocument(String path) 
        throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder(false); 

        // no DTD validation
        builder.setValidation(false);
        builder.setFeature("http://xml.org/sax/features/validation", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        Document anotherDocument = builder.build(new File(path));
        return anotherDocument;
    }
    
    /**
     * Adds a script to the head.
     * @param doc the document to use.
     * @param src the location of the script
     */
    private void addScript(Document doc, String src, boolean isCore) {
        Namespace nm = doc.getRootElement().getNamespace();
        Element script = new Element("script", nm);
        // set the attributes
        script.setAttribute("src", src);
        script.setAttribute("type", "text/javascript");
        //add an empty text node in them
        script.addContent("");
        // put it to the right place
        Element head = doc.getRootElement().getChild("head", nm);
        
        if (isCore) {
            // then place it first.
          //insert before the last script tag.
            head.addContent(head.getContentSize() -2, script);
        } else {
            head.addContent(script);
        }
        
        return;
    }
    
    /**
     * @return read the files to be placed as core scripts
     * from a configuration file in a json file.
     * @throws IOException 
     */
    private static String readEnvFiles(ServletContext context) throws IOException {
        FileInputStream core_scripts = new FileInputStream(
                context.getRealPath("/js/js_files.json"));
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
     * @param a list of js files
     * @return a string that contains all the compressed data
     * @throws EvaluatorException
     * @throws IOException
     */
    private static String compressJS(Collection<IDiagramPlugin> plugins, 
            ServletContext context) {
        StringWriter sw = new StringWriter();
        for (IDiagramPlugin plugin : plugins) {
            sw.append("/* ").append(plugin.getName()).append(" */\n");
            InputStream input = plugin.getContents();
            try {
                JavaScriptCompressor compressor = new JavaScriptCompressor(
                        new InputStreamReader(input), null);
                compressor.compress(sw, -1, false, false, false, false);
            } catch (EvaluatorException e) {
                _logger.error(e.getMessage(), e);
            } catch (IOException e) {
                _logger.error(e.getMessage(), e);
            } finally {
                try { input.close(); } catch (IOException e) {}
            }

            sw.append("\n");
        }
        return sw.toString();
    }
    
    
    /**
     * Determine whether the browser is IE
     * @param request
     * @return true: IE browser false: others browsers
     */
    private static boolean isIE(HttpServletRequest request){
        return request.getHeader("USER-AGENT").
            toLowerCase().indexOf("msie") > 0;
    }
    
    /**
     * Returns the designer version from the manifest.
     * @param context 
     * @return version
     */
    private static String readDesignerVersion(ServletContext context) {
        String retStr = "";
        try {
            InputStream inputStream = context.getResourceAsStream("/META-INF/MANIFEST.MF");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null)   {
                if(line.startsWith(BUNDLE_VERSION)) {
                    retStr = line.substring(BUNDLE_VERSION.length() + 1);
                    retStr = retStr.trim();
                }
            }
            inputStream.close();
        } catch (IOException e) {
            _logger.error(e.getMessage(), e);
        }
        return retStr;
    }
}