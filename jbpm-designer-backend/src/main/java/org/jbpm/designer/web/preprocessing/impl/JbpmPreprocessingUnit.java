package org.jbpm.designer.web.preprocessing.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.drools.core.process.core.ParameterDefinition;
import org.drools.core.process.core.datatype.DataType;
import org.drools.core.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.AssetNotFoundException;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.filters.FilterByExtension;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.util.Base64Backport;
import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.web.preprocessing.IDiagramPreprocessingUnit;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mvel2.MVEL;
import org.stringtemplate.v4.ST;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;
import sun.misc.BASE64Encoder;
import org.apache.commons.io.IOUtils;

import javax.enterprise.event.Event;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * JbpmPreprocessingUnit - preprocessing unit for the jbpm profile
 *
 * @author Tihomir Surdilovic
 */
public class JbpmPreprocessingUnit implements IDiagramPreprocessingUnit {
    private static final Logger _logger =
            LoggerFactory.getLogger(JbpmPreprocessingUnit.class);
    public static final String STENCILSET_PATH = "stencilsets";
    public static final String WORKITEM_DEFINITION_EXT = "wid";
    public static final String THEME_NAME = "themes";
    public static final String THEME_EXT = ".json";
    public static final String DEFAULT_THEME_NAME = "jBPM";
    public static final String CUSTOMEDITORS_NAME = "customeditors";
    public static final String PROCESSDICTIONARY_NAME = "processdictionary";
    public static final String CUSTOMEDITORS_EXT = ".json";
    public static final String THEME_COOKIE_NAME = "designercolortheme";
    public static final String DEFAULT_CATEGORY_NAME = "Service Tasks";

    private String designer_path;
    private String stencilPath;
    private String origStencilFilePath;
    private String stencilFilePath;
    private String outData = "";
    private String workitemSVGFilePath;
    private String origWorkitemSVGFile;
    private String default_emailicon;
    private String default_logicon;
    private String default_servicenodeicon;
    private String default_widconfigtemplate;
    private String defaultClasspathWid = "META-INF/WorkDefinitions.wid";
    private String themeInfo;
    private String formWidgetsDir;
    private String customEditorsInfo;
    private String patternsData;
    private String sampleBpmn2;
    private String globalDir;
    private VFSService vfsService;
    private Event<ResourceUpdatedEvent> resourceUpdatedEvent;
    private Event<ResourceAddedEvent> resourceAddedEvent;

    public JbpmPreprocessingUnit(ServletContext servletContext, VFSService vfsService, Event<ResourceUpdatedEvent> resourceUpdatedEvent, Event<ResourceAddedEvent> resourceAddedEvent) {
        this(servletContext, ConfigurationProvider.getInstance().getDesignerContext(), vfsService, resourceUpdatedEvent, resourceAddedEvent);
    }

    public JbpmPreprocessingUnit(ServletContext servletContext, String designerPath, VFSService vfsService, Event<ResourceUpdatedEvent> resourceUpdatedEvent, Event<ResourceAddedEvent> resourceAddedEvent) {
        this.designer_path = designerPath.substring(0, designerPath.length()-1);
        this.vfsService = vfsService;
        this.resourceAddedEvent = resourceAddedEvent;
        this.resourceUpdatedEvent = resourceUpdatedEvent;
        stencilPath = servletContext.getRealPath(designer_path + "/" + STENCILSET_PATH);
        origStencilFilePath = stencilPath + "/bpmn2.0jbpm/stencildata/" + "bpmn2.0jbpm.orig";
        stencilFilePath = stencilPath + "/bpmn2.0jbpm/" + "bpmn2.0jbpm.json";
        workitemSVGFilePath = stencilPath  + "/bpmn2.0jbpm/view/activity/workitems/";
        origWorkitemSVGFile = workitemSVGFilePath + "workitem.orig";
        default_emailicon = servletContext.getRealPath(designer_path + "/defaults/defaultemailicon.gif");
        default_logicon = servletContext.getRealPath(designer_path + "/defaults/defaultlogicon.gif");
        default_servicenodeicon = servletContext.getRealPath(designer_path + "/defaults/defaultservicenodeicon.png");
        default_widconfigtemplate = servletContext.getRealPath(designer_path + "/defaults/WorkDefinitions.wid.st");
        themeInfo = servletContext.getRealPath(designer_path + "/defaults/themes.json");
        formWidgetsDir = servletContext.getRealPath(designer_path + "/defaults/formwidgets");
        customEditorsInfo = servletContext.getRealPath(designer_path + "/defaults/customeditors.json");
        patternsData = servletContext.getRealPath(designer_path + "/defaults/patterns.json");
        sampleBpmn2 = servletContext.getRealPath(designer_path + "/defaults/SampleProcess.bpmn2");
    }

    public String getOutData() {
        if(outData != null && outData.length() > 0) {
            if(outData.endsWith(",")) {
                outData = outData.substring(0, outData.length()-1);
            }
        }
        return outData;
    }

    public void preprocess(HttpServletRequest req, HttpServletResponse res, IDiagramProfile profile, ServletContext serlvetContext) {

        Repository repository = profile.getRepository();

        String uuid = req.getParameter("uuid");
        try {
            //createAssetIfNotExisting(repository, "/defaultPackage", "BPMN2-SampleProcess", "bpmn2", getBytesFromFile(new File(sampleBpmn2)));

            Asset<String> asset = repository.loadAsset(uuid);
            this.globalDir = profile.getRepositoryGlobalDir( uuid );
            outData = "";
            Map<String, ThemeInfo> themeData = setupThemes(req, repository, profile);
            setupCustomEditors(repository, profile);
            setupFormWidgets(repository, profile);
            setupDefaultIcons(globalDir, repository);

            // figure out which package our uuid belongs in and get back the list of configs
            Collection<Asset> workitemConfigInfo = findWorkitemInfoForUUID(asset.getAssetLocation(), repository);
            // also get all from globals package
            Collection<Asset> globalWorkitemConfigInfo = findWorkitemInfoForUUID(globalDir, repository);

            if(workitemConfigInfo != null) {
                if(globalWorkitemConfigInfo != null) {
                    workitemConfigInfo.addAll(globalWorkitemConfigInfo);
                }
            } else {
                workitemConfigInfo = globalWorkitemConfigInfo;
            }

            if(workitemConfigInfo != null) {
                setupDefaultWorkitemConfigs(asset.getAssetLocation(), repository);
                workitemConfigInfo = findWorkitemInfoForUUID(asset.getAssetLocation(), repository);
            }

            // get the contents of each of the configs
            Collection<Asset> workItemsContent = getWorkitemConfigContent(workitemConfigInfo, repository);

            // evaluate all configs
            Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();
            for(Asset entry : workItemsContent) {

                try {
                    evaluateWorkDefinitions(workDefinitions, entry, repository, profile);
                } catch(Exception e) {
                    e.printStackTrace();
                    // log and continue
                    _logger.error("Unable to parse a workitem definition: " + e.getMessage());
                }

            }
            // set the out parameter
            for(Map.Entry<String, WorkDefinitionImpl> definition : workDefinitions.entrySet()) {
                outData += definition.getValue().getName() + ",";
            }
            // parse the profile json to include config data
            // parse patterns data
            JSONArray patternsArray = new JSONArray(readFile(patternsData));
            Map<String, PatternInfo> patternInfoMap = new HashMap<String, PatternInfo>();
            for(int i=0; i < patternsArray.length(); i++) {
                JSONObject patternObj = patternsArray.getJSONObject(i);
                PatternInfo pi = new PatternInfo(patternObj.getString("id"), patternObj.getString("name"), patternObj.getString("description"));
                patternInfoMap.put(patternObj.getString("id"), pi);
            }

            // parse the orig stencil data with workitem definitions
            ST workItemTemplate = new ST(readFile(origStencilFilePath), '$', '$');
            workItemTemplate.add("bopen", "{");
            workItemTemplate.add("bclose", "}");
            workItemTemplate.add("workitemDefs", workDefinitions);
            workItemTemplate.add("patternData", patternInfoMap);

            String processPackage = asset.getAssetLocation();
            if(processPackage.startsWith("/")) {
                processPackage = processPackage.substring(1, processPackage.length());
            }
            processPackage = processPackage.replaceAll("/", ".");
            // final check in odd cases
            if(processPackage.startsWith(".")) {
                processPackage = processPackage.substring(1, processPackage.length());
            }

            // set package to org.jbpm
            workItemTemplate.add("packageName", "org.jbpm");

            String processName = asset.getName();
            workItemTemplate.add("processn", processName);

            String packageNameStr = (processPackage.length() > 0) ? processPackage + "." : "";
            if(packageNameStr.length() > 0) {
                String[] packageNameParts = packageNameStr.split("\\.");
                packageNameStr = packageNameParts[0] + ".";
            }

            // default the process id to packagename.processName
            String processIdString = packageNameStr + processName;
            if(processIdString.startsWith("."));
            if(processIdString.startsWith(".")) {
                processIdString = processIdString.substring(1, processIdString.length());
            }

            workItemTemplate.add("processid", processIdString);

            // default version to 1.0
            workItemTemplate.add("pversion", "1.0");
            // color theme attribute
            workItemTemplate.add("colortheme", themeData);

            // delete stencil data json if exists
            deletefile(stencilFilePath);
            // copy our results as the stencil json data
            createAndWriteToFile(stencilFilePath, workItemTemplate.render());
            // create and parse the view svg to include config data
            createAndParseViewSVG(workDefinitions, repository);
        } catch( Exception e ) {
            _logger.error("Failed to setup workitems : " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void createAndParseViewSVG(Map<String, WorkDefinitionImpl> workDefinitions, Repository repository) {
        // first delete all existing workitem svgs
        Collection<File> workitemsvgs = FileUtils.listFiles(new File(workitemSVGFilePath), new String[] { "svg" }, true);
        if(workitemsvgs != null) {
            for(File wisvg : workitemsvgs) {
                deletefile(wisvg);
            }
        }
        try {
            for(Map.Entry<String, WorkDefinitionImpl> definition : workDefinitions.entrySet()) {
                ST workItemTemplate = new ST(readFile(origWorkitemSVGFile), '$', '$');
                workItemTemplate.add("workitemDef", definition.getValue());
                String widIcon = definition.getValue().getIcon();

                Asset<byte[]> iconAsset = repository.loadAssetFromPath(widIcon);

                BASE64Encoder enc = new BASE64Encoder();
                String iconEncoded = "data:image/png;base64," + enc.encode(iconAsset.getAssetContent());
                workItemTemplate.add("nodeicon", iconEncoded);
                String fileToWrite = workitemSVGFilePath + definition.getValue().getName() + ".svg";
                createAndWriteToFile(fileToWrite, workItemTemplate.render());
            }
        } catch (Exception e) {
            _logger.error("Failed to setup workitem svg images : " + e.getMessage());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void evaluateWorkDefinitions(Map<String, WorkDefinitionImpl> workDefinitions, Asset<String> widAsset, Repository repository, IDiagramProfile profile) throws Exception {
        List<Map<String, Object>> workDefinitionsMaps;

        try {
            workDefinitionsMaps = (List<Map<String, Object>>) MVEL.eval(widAsset.getAssetContent(), new HashMap());
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }

        for (Map<String, Object> workDefinitionMap : workDefinitionsMaps) {
            if (workDefinitionMap != null) {
                WorkDefinitionImpl workDefinition = new WorkDefinitionImpl();
                workDefinition.setName(((String) workDefinitionMap.get("name")).replaceAll("\\s",""));
                workDefinition.setDisplayName((String) workDefinitionMap.get("displayName"));
                String category = (String) workDefinitionMap.get("category");
                if(category == null || category.length() < 1) {
                    category = DEFAULT_CATEGORY_NAME;
                }
                workDefinition.setCategory(category);

                String icon = (String) workDefinitionMap.get("icon");


                Asset<byte[]> iconAsset = null;

                if(!icon.startsWith(this.globalDir)) {
                    if(icon.startsWith("/")) {
                        icon = this.globalDir + icon;
                    } else {
                        icon = this.globalDir + "/" + icon;
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

                BASE64Encoder enc = new BASE64Encoder();
                String iconEncoded = "data:image/png;base64," + enc.encode(iconAsset.getAssetContent());
                workDefinition.setIconEncoded(URLEncoder.encode(iconEncoded, "UTF-8"));
                workDefinition.setCustomEditor((String) workDefinitionMap.get("customEditor"));
                Set<ParameterDefinition> parameters = new HashSet<ParameterDefinition>();
                if(workDefinitionMap.get("parameters") != null) {
                    Map<String, DataType> parameterMap = (Map<String, DataType>) workDefinitionMap.get("parameters");
                    if (parameterMap != null) {
                        for (Map.Entry<String, DataType> entry : parameterMap.entrySet()) {
                            parameters.add(new ParameterDefinitionImpl(entry.getKey(), entry.getValue()));
                        }
                    }
                    workDefinition.setParameters(parameters);
                }

                if(workDefinitionMap.get("results") != null) {
                    Set<ParameterDefinition> results = new HashSet<ParameterDefinition>();
                    Map<String, DataType> resultMap = (Map<String, DataType>) workDefinitionMap.get("results");
                    if (resultMap != null) {
                        for (Map.Entry<String, DataType> entry : resultMap.entrySet()) {
                            results.add(new ParameterDefinitionImpl(entry.getKey(), entry.getValue()));
                        }
                    }
                    workDefinition.setResults(results);
                }
                if(workDefinitionMap.get("defaultHandler") != null) {
                    workDefinition.setDefaultHandler((String) workDefinitionMap.get("defaultHandler"));
                }
                if(workDefinitionMap.get("dependencies") != null) {
                    workDefinition.setDependencies(((List<String>) workDefinitionMap.get("dependencies")).toArray(new String[0]));
                }
                workDefinitions.put(workDefinition.getName(), workDefinition);
            }
        }
    }

    private Collection<Asset> getWorkitemConfigContent(Collection<Asset> widAssets, Repository repository) {
        List<Asset> loadedAssets = new ArrayList<Asset>();
        for (Asset widAsset : widAssets) {
            try {
                Asset assetWithContent = repository.loadAsset(widAsset.getUniqueId());
                loadedAssets.add(assetWithContent);
            } catch (AssetNotFoundException e) {
                _logger.error("Asset " + widAsset.getName() + " not found");
            }
        }

        return loadedAssets;
    }

    private void setupFormWidgets(Repository repository, IDiagramProfile profile) {

        File[] allFormWidgets = new File(formWidgetsDir).listFiles();
        for(File formWidget : allFormWidgets) {
            try {
                int extPosition = formWidget.getName().lastIndexOf(".");
                String extension = formWidget.getName().substring(extPosition + 1);
                String name = formWidget.getName().substring(0, extPosition);
                createAssetIfNotExisting(repository, this.globalDir, name, extension,
                        getBytesFromFile(formWidget));


            } catch (Exception e) {
                _logger.error("Error setting up form widgets: " + e.getMessage());
            }
        }
    }

    private void setupCustomEditors(Repository repository, IDiagramProfile profile) {

        try {
            createAssetIfNotExisting(repository, this.globalDir, CUSTOMEDITORS_NAME, "json",
                    getBytesFromFile(new File(customEditorsInfo)));

        } catch (Exception e) {
            _logger.error(e.getMessage());
        }
    }

    private Map<String, ThemeInfo> setupThemes(HttpServletRequest req, Repository repository, IDiagramProfile profile) {
        Map<String, ThemeInfo> themeData = new HashMap<String, ThemeInfo>();
        Asset<String> themeAsset = null;
        try {
            boolean themeExists = repository.assetExists(this.globalDir + "/" + THEME_NAME + THEME_EXT);
            if (!themeExists) {
                // create theme asset
                AssetBuilder assetBuilder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
                assetBuilder.content(new String(getBytesFromFile(new File(themeInfo)), "UTF-8"))
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


            JSONObject themesObject =  new JSONObject(themesStr);

            // get the theme name from cookie if exists or default
            String themeName = DEFAULT_THEME_NAME;
            Cookie[] cookies = req.getCookies();
            if(cookies != null) {
                for(Cookie ck : cookies) {
                    if(ck.getName().equals(THEME_COOKIE_NAME)) {
                        themeName = ck.getValue();
                    }
                }
            }

            // extract theme info from json
            JSONObject themes = (JSONObject) themesObject.get("themes");
            JSONObject selectedTheme = (JSONObject) themes.get(themeName);
            for(String key : JSONObject.getNames(selectedTheme)) {
                String val = (String) selectedTheme.get(key);
                String[] valParts = val.split( "\\|\\s*" );
                ThemeInfo ti;
                if(valParts.length == 3) {
                    ti = new ThemeInfo(valParts[0], valParts[1], valParts[2]);
                } else {
                    ti = new ThemeInfo("#000000", "#000000", "#000000");
                }
                themeData.put(key, ti);
            }
            return themeData;
        } catch (Exception e) {
            e.printStackTrace();
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
            return themeData;
        }
    }

    private void setupDefaultIcons(String location, Repository repository) {

        try {

            createAssetIfNotExisting(repository, location, "defaultemailicon", "gif",
                    getBytesFromFile(new File(default_emailicon)));

            createAssetIfNotExisting(repository, location, "defaultlogicon", "gif",
                    getBytesFromFile(new File(default_logicon)));

            createAssetIfNotExisting(repository, location, "defaultservicenodeicon", "png",
                    getBytesFromFile(new File(default_servicenodeicon)));

        } catch (Exception e) {
            _logger.error(e.getMessage());
        }

    }

    private void setupDefaultWorkitemConfigs(String location, Repository repository) {
        try {
            // push default configuration wid
            // check classpath first
            InputStream widIn = this.getClass().getClassLoader().getResourceAsStream(defaultClasspathWid);
            String createdUUID;
            if(widIn != null) {
                createdUUID = createAssetIfNotExisting(repository, location, "WorkDefinitions", "wid", IOUtils.toByteArray(widIn));
            } else {
                ST widConfigTemplate = new ST(readFile(default_widconfigtemplate), '$', '$');
                createdUUID = createAssetIfNotExisting(repository, location, "WorkDefinitions", "wid",
                        widConfigTemplate.render().getBytes("UTF-8"));


            }
            if (Base64Backport.isBase64(createdUUID)) {
                byte[] decoded = Base64.decodeBase64(createdUUID);
                try {
                    createdUUID =  new String(decoded, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if(vfsService != null && createdUUID != null) {
                Path newWidAssetPath = vfsService.get(createdUUID);
                resourceAddedEvent.fire(new ResourceAddedEvent( newWidAssetPath ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Collection<Asset> findWorkitemInfoForUUID(String location, Repository repository) {
        Collection<Asset> widAssets = repository.listAssets(location, new FilterByExtension(WORKITEM_DEFINITION_EXT));
        return widAssets;
    }

    private String readFile(String pathname) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new File(pathname), "UTF-8");
        String lineSeparator = System.getProperty("line.separator");
        try {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

    private void deletefile(String file) {
        File f = new File(file);
        boolean success = f.delete();
        if (!success){
            _logger.info("Unable to delete file :" + file);
        } else {
            _logger.info("Successfully deleted file :" + file);
        }
    }

    private void deletefile(File f) {
        String fname = f.getAbsolutePath();
        boolean success = f.delete();
        if (!success){
            _logger.info("Unable to delete file :" + fname);
        } else {
            _logger.info("Successfully deleted file :" + fname);
        }
    }

    private void createAndWriteToFile(String file, String content) throws Exception {
        Writer output = null;
        output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        output.write(content);
        output.close();
        _logger.info("Created file:" + file);
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = null;
        is = new FileInputStream(file);
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            is.close();
            return null; // File is too large
        }

        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

    private String createAssetIfNotExisting(Repository repository, String location, String name, String type, byte[] content) {
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

    private class ThemeInfo {
        private String bgColor;
        private String borderColor;
        private String fontColor;

        public ThemeInfo(String bgColor, String borderColor, String fontColor) {
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

        public PatternInfo(String id, String name, String description) {
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
}
