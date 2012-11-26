package org.jbpm.designer.web.preprocessing.impl;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.*;
import javax.servlet.http.Cookie;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.designer.Base64EncodingUtil;
import org.jbpm.designer.web.preprocessing.IDiagramPreprocessingUnit;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.jbpm.designer.web.server.*;
import org.jbpm.designer.web.server.GuvnorUtil.UrlType;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.json.*;
import org.mvel2.MVEL;

/**
 * JbpmPreprocessingUnit - preprocessing unit for the jbpm profile
 * 
 * @author Tihomir Surdilovic
 */
public class JbpmPreprocessingUnit implements IDiagramPreprocessingUnit {
    private static final Logger _logger = 
        Logger.getLogger(JbpmPreprocessingUnit.class);
    public static final String STENCILSET_PATH = "stencilsets";
    public static final String WORKITEM_DEFINITION_EXT = "wid";
    public static final String THEME_NAME = "themes";
    public static final String THEME_EXT = ".json";
    public static final String DEFAULT_THEME_NAME = "jBPM";
    public static final String CUSTOMEDITORS_NAME = "customeditors";
    public static final String CUSTOMEDITORS_EXT = ".json";
    public static final String THEME_COOKIE_NAME = "designercolortheme";
    public static final String DEFAULT_CATEGORY_NAME = "Service Tasks";
    
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
    String themeInfo;
    private String formWidgetsDir;
    private String customEditorsInfo;
    private String patternsData;
    
    public JbpmPreprocessingUnit(ServletContext servletContext) {
        stencilPath = servletContext.getRealPath("/" + STENCILSET_PATH);
        origStencilFilePath = stencilPath + "/bpmn2.0jbpm/stencildata/" + "bpmn2.0jbpm.orig";
        stencilFilePath = stencilPath + "/bpmn2.0jbpm/" + "bpmn2.0jbpm.json";
        workitemSVGFilePath = stencilPath  + "/bpmn2.0jbpm/view/activity/workitems/";
        origWorkitemSVGFile = workitemSVGFilePath + "workitem.orig";
        default_emailicon = servletContext.getRealPath("/defaults/defaultemailicon.gif");
        default_logicon = servletContext.getRealPath(  "/defaults/defaultlogicon.gif");
        default_servicenodeicon = servletContext.getRealPath(  "/defaults/defaultservicenodeicon.png");
        default_widconfigtemplate = servletContext.getRealPath("/defaults/WorkDefinitions.wid.st");
        themeInfo = servletContext.getRealPath("/defaults/themes.json");
        formWidgetsDir = servletContext.getRealPath("/defaults/formwidgets");
        customEditorsInfo = servletContext.getRealPath("/defaults/customeditors.json");
        patternsData = servletContext.getRealPath("/defaults/patterns.json");
    }
    
    public String getOutData() {
        if(outData != null && outData.length() > 0) {
            if(outData.endsWith(",")) {
                outData = outData.substring(0, outData.length()-1);
            }
        }
        return outData;
    }
    
    public void preprocess(HttpServletRequest req, HttpServletResponse res, IDiagramProfile profile) {
        String uuid = req.getParameter("uuid");
        outData = "";
        Map<String, ThemeInfo> themeData = setupThemes(profile, req);
        setupCustomEditors(profile);
        // check with guvnor to see what packages exist
        List<String> packageNames = ServletUtil.getPackageNames(profile);
        String[] info = ServletUtil.findPackageAndAssetInfo(uuid, packageNames, profile);
        
        setupFormWidgets(profile);
        setupDefaultIcons(info, profile);
        
        // figure out which package our uuid belongs in and get back the list of configs
        Map<String, List<String>> workitemConfigInfo = findWorkitemInfoForUUID(uuid, packageNames, profile);
        if(workitemConfigInfo != null) {
        	boolean gotConfigs = false;
        	Iterator<String> pkgIter = workitemConfigInfo.keySet().iterator();
        	while(pkgIter.hasNext()) {
        		String pkgName = pkgIter.next();
        		if(workitemConfigInfo.get(pkgName) != null && workitemConfigInfo.get(pkgName).size() > 0) {
        			gotConfigs = true;
        		}
        	}
        	if(!gotConfigs) {
        		System.out.println("Setting up default workitem configuration");
        		setupDefaultWorkitemConfigs(uuid, packageNames, profile);
        		System.out.println("End setting up default workitem configuration");
        		// re-load the workitem config info
        		workitemConfigInfo = findWorkitemInfoForUUID(uuid, packageNames, profile);
        	}
        }
        
        try {
        	// get the contents of each of the configs
        	Map<String, String> workItemsContent = getWorkitemConfigContent(workitemConfigInfo, profile);
        
        	// evaluate all configs
        	Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();
        	for(Map.Entry<String, String> entry : workItemsContent.entrySet()) {
        		if(entry.getValue().trim().length() > 0) {
        			try {
                        evaluateWorkDefinitions(workDefinitions, workitemConfigInfo, entry.getValue(), profile);
                    } catch(Exception e) {
                        // log and continue
                        _logger.error("Unable to parse a workitem definition: " + e.getMessage());
                    }
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
        	StringTemplate workItemTemplate = new StringTemplate(readFile(origStencilFilePath));
        	workItemTemplate.setAttribute("workitemDefs", workDefinitions);
            workItemTemplate.setAttribute("patternData", patternInfoMap);
        	if(workitemConfigInfo != null && workitemConfigInfo.keySet() != null && workitemConfigInfo.keySet().size() > 0) {
        		for(String key: workitemConfigInfo.keySet()) {
        			workItemTemplate.setAttribute("packageName", key.replaceAll("\\s",""));
        		}
        	} else {
        		workItemTemplate.setAttribute("packageName", "");
        	}
        	if(info != null && info.length == 2 && info[1] != null) {
        		workItemTemplate.setAttribute("processName", info[1].replaceAll("\\s",""));
        	} else {
        		workItemTemplate.setAttribute("processName", "");
        	}
        	
        	// default the process id to packagename.processName
        	workItemTemplate.setAttribute("processid", workItemTemplate.getAttribute("packageName") + "." + workItemTemplate.getAttribute("processName")); 
        	// color theme attribute
        	workItemTemplate.setAttribute("colortheme", themeData);
        	
        	// delete stencil data json if exists
        	deletefile(stencilFilePath);
        	// copy our results as the stencil json data
        	createAndWriteToFile(stencilFilePath, workItemTemplate.toString());
        	// create and parse the view svg to include config data
            createAndParseViewSVG(workDefinitions, profile);
        } catch( Exception e ) {
            _logger.error("Failed to setup workitems : " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    void createAndParseViewSVG(Map<String, WorkDefinitionImpl> workDefinitions, IDiagramProfile profile) {
        // first delete all existing workitem svgs
        Collection<File> workitemsvgs = FileUtils.listFiles(new File(workitemSVGFilePath), new String[] { "svg" }, true);
        if(workitemsvgs != null) {
            for(File wisvg : workitemsvgs) {
                deletefile(wisvg);
            }
        }
        try {
            for(Map.Entry<String, WorkDefinitionImpl> definition : workDefinitions.entrySet()) {
                StringTemplate workItemTemplate = new StringTemplate(readFile(origWorkitemSVGFile));
                workItemTemplate.setAttribute("workitemDef", definition.getValue());
                String widIcon = definition.getValue().getIcon();
                InputStream iconStream = GuvnorUtil.readStreamFromUrl(widIcon, "GET", profile);
                String iconEncoded = "data:image/png;base64," + Base64EncodingUtil.encode(IOUtils.toByteArray(iconStream));
                workItemTemplate.setAttribute("nodeicon", iconEncoded);
                String fileToWrite = workitemSVGFilePath + definition.getValue().getName() + ".svg";
                createAndWriteToFile(fileToWrite, workItemTemplate.toString());
            }
        } catch (Exception e) {
            _logger.error("Failed to setup workitem svg images : " + e.getMessage());
        } 
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void evaluateWorkDefinitions(Map<String, WorkDefinitionImpl> workDefinitions, Map<String, List<String>> configInfo, String content, IDiagramProfile profile) throws Exception {
    	List<Map<String, Object>> workDefinitionsMaps;

        try {
            workDefinitionsMaps = (List<Map<String, Object>>) MVEL.eval(content, new HashMap());
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
                String iconLoc = (String) workDefinitionMap.get("icon");
                if(iconLoc.length() < 1) {
                	String packageName = "";
                	for(Map.Entry<String, List<String>> entry : configInfo.entrySet()) {
                        packageName = entry.getKey();
                	}
                	// GUVNOR JbpmPreprocessingUnit
                	iconLoc = GuvnorUtil.getUrl(profile, packageName, "defaultservicenodeicon", GuvnorUtil.UrlType.Binary);
                }
                workDefinition.setIcon(iconLoc);
                InputStream iconStream = GuvnorUtil.readStreamFromUrl(iconLoc, "GET", profile);
                String iconEncoded = "data:image/png;base64," + Base64EncodingUtil.encode(IOUtils.toByteArray(iconStream));
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
    
    public Map<String, String> getWorkitemConfigContent(Map<String, List<String>> configInfo, IDiagramProfile profile) {
        Map<String, String> resultsMap = new HashMap<String, String>();
        if(configInfo.size() > 0) {
            for(Map.Entry<String, List<String>> entry : configInfo.entrySet()) {
                String packageName = entry.getKey();
                List<String> configNames = entry.getValue();
                if(configNames != null) {
                    for(String configName : configNames) {
                    	try {
                    	    //GUVNOR JbpmPreprocessingUnit
	                    	String configURL = GuvnorUtil.getUrl(profile, packageName, configName, UrlType.Source);
                            String resultString =  GuvnorUtil.readStringContentFromUrl(configURL, "GET", profile);
                            
                            resultsMap.put(configName, resultString);
                        } catch (Exception e) {
                            // we dont want to barf..just log that error happened
                            _logger.error(e.getMessage());
                        } 
                    }
                }
            }
        }
        return resultsMap;
    }
    
    public void setupFormWidgets(IDiagramProfile profile) {
        // GUVNOR JbpmPreprocessingUnit
    	String formWidgetsPackageURL = GuvnorUtil.getUrl(profile, "globalArea", "", UrlType.Normal);

    	File[] allFormWidgets = new File(formWidgetsDir).listFiles();
    	for(File formWidget : allFormWidgets) {
    		int extPosition = formWidget.getName().lastIndexOf(".");
    		String widgetNameOnly = formWidget.getName().substring(0,extPosition);
    		String widgetURL = GuvnorUtil.getUrl(profile, "globalArea", widgetNameOnly, UrlType.Normal);

    		try {
		        if(GuvnorUtil.readCheckAssetExists(widgetURL, profile)) { 
		            byte [] formWidgetContentBytes = getBytesFromFile(formWidget);
		            GuvnorUtil.createAsset(formWidgetsPackageURL, formWidget.getName(), "", formWidgetContentBytes, profile);
		        }
    		} catch (Exception e) {
                // we dont want to barf..just log that error happened
                _logger.error("Error setting up form widgets: " + e.getMessage());
            } 
    	}
    }
    
    public void setupCustomEditors(IDiagramProfile profile) {
        // GUVNOR JbpmPreprocessingUnit
        String customEditorsURL = GuvnorUtil.getUrl(profile, "globalArea", CUSTOMEDITORS_NAME, UrlType.Normal);
        String customEditorsAssetsURL = GuvnorUtil.getUrl(profile, "globalArea", "", UrlType.Normal);

        try {
            if(GuvnorUtil.readCheckAssetExists(customEditorsURL, profile)) { 
                byte [] customEditorsInfoBytes = getBytesFromFile(new File(customEditorsInfo));
                GuvnorUtil.createAsset(customEditorsAssetsURL, CUSTOMEDITORS_NAME, CUSTOMEDITORS_EXT, customEditorsInfoBytes, profile);
            }
        } catch (Exception e) {
            _logger.error(e.getMessage());
        }
    }
    
    public Map<String, ThemeInfo> setupThemes(IDiagramProfile profile, HttpServletRequest req) {
        // GUVNOR JbpmPreprocessingUnit
    	Map<String, ThemeInfo> themeData = null;
    	String themesURL = GuvnorUtil.getUrl(profile, "globalArea", THEME_NAME, UrlType.Normal);
    	String themesSourceURL = GuvnorUtil.getUrl(profile, "globalArea", THEME_NAME, UrlType.Source);
		String themesAssetsURL = GuvnorUtil.getUrl(profile, "globalArea", "", UrlType.Normal);

		try {
	        if (GuvnorUtil.readCheckAssetExists(themesURL, profile)) { 
	            byte [] themeInfoContentByes = getBytesFromFile(new File(themeInfo));
	            GuvnorUtil.createAsset(themesAssetsURL, THEME_NAME, THEME_EXT, themeInfoContentByes, profile);
	        }
	        
	        String themesStr;
			try {
				themesStr = GuvnorUtil.readStringContentFromUrl(themesSourceURL, "GET", profile);
			} catch (Exception e) {
				themesStr = readFile(themeInfo);
			}
	        
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
	        return convertJsonToThemeInfoMap(themesStr, themeName);
		} catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
            return themeData;
        }
    }
    
    /**
     * Extract theme info from json
     * @throws JSONException 
     */
    public HashMap<String, JbpmPreprocessingUnit.ThemeInfo> convertJsonToThemeInfoMap(String themesJsonStr, String themeName) throws JSONException { 
        HashMap<String, JbpmPreprocessingUnit.ThemeInfo> themeData = new HashMap<String, JbpmPreprocessingUnit.ThemeInfo>();
        JSONObject themes = (JSONObject) new JSONObject(themesJsonStr).get("themes");
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
    }
    
    public void setupDefaultIcons(String[] info, IDiagramProfile profile) {
        // GUVNOR JbpmPreprocessingUnit
    	if(info != null && info.length == 2) {
    		try {
	    		String pkg = URLEncoder.encode(info[0], "UTF-8");
	    		
	    		String emailIconURL = GuvnorUtil.getUrl(profile, pkg, "defaultemailicon", UrlType.Normal);
	    		String logIconURL = GuvnorUtil.getUrl(profile, pkg, "defaultlogicon", UrlType.Normal);
	    		String serviceNodeIconURL = GuvnorUtil.getUrl(profile, pkg, "defaultservicenodeicon", UrlType.Normal);
	    		String packageAssetsURL = GuvnorUtil.getUrl(profile, pkg, "", UrlType.Normal);
	    		
				// check if the images already exists
				if(GuvnorUtil.readCheckAssetExists(emailIconURL, profile)) { 
				    byte [] defaultEmailIconContentByte = getBytesFromFile(new File(default_emailicon));
				    GuvnorUtil.createAsset(packageAssetsURL, "defaultemailicon", ".gif", defaultEmailIconContentByte, profile);
				}
				
				if(GuvnorUtil.readCheckAssetExists(logIconURL, profile)) { 
				    byte [] defaultLogIconBytes = getBytesFromFile(new File(default_logicon));
				    GuvnorUtil.createAsset(packageAssetsURL, "defaultlogicon", ".gif", defaultLogIconBytes, profile);
				}

				if(GuvnorUtil.readCheckAssetExists(serviceNodeIconURL, profile)) { 
				    byte [] defaultServiceNodeIconContentBytes = getBytesFromFile(new File(default_servicenodeicon));
				    GuvnorUtil.createAsset(packageAssetsURL, "defaultservicenodeicon", ".png", defaultServiceNodeIconContentBytes, profile);
				}
			} catch (Exception e) {
                _logger.error(e.getMessage());
			}
    	} else {
    		System.out.println("Unable to set up default icons.");
    	}
    }
    
    public void setupDefaultWorkitemConfigs(String uuid, List<String> packageNames, IDiagramProfile profile) {
        // GUVNOR JbpmPreprocessingUnit
    	boolean gotPackage = false;
    	String pkg = "";
    	for(String nextPackage : packageNames) {
    		try {	
	    		String packageAssetURL = GuvnorUtil.getUrl(profile, nextPackage, "", UrlType.Normal);
	    		String content = GuvnorUtil.readStringContentFromUrl(packageAssetURL, "GET", profile);
            
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(content));
                while (reader.hasNext()) {
                    int next = reader.next();
                    if (next == XMLStreamReader.START_ELEMENT) {
                        if ("uuid".equals(reader.getLocalName())) {
                            String eleText = reader.getElementText();
                            if(uuid.equals(eleText)) {
                                pkg = nextPackage;
                                gotPackage = true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // we dont want to barf..just log that error happened
                _logger.error(e.getMessage());
            } 
            if(gotPackage) {
                // no need to loop through rest of packages really
                break;
            }
        }
    	
    	if(gotPackage) {
    		// push the default workitem config
    		String packageAssetsURL = GuvnorUtil.getUrl(profile, pkg, "", UrlType.Normal);

    		try {
				// push default configuration wid
	            StringTemplate widConfigTemplate = new StringTemplate(readFile(default_widconfigtemplate));
	            widConfigTemplate.setAttribute("protocol", ExternalInfo.getExternalProtocol(profile));
	            widConfigTemplate.setAttribute("host", ExternalInfo.getExternalHost(profile));
	            widConfigTemplate.setAttribute("subdomain", profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/")));
	            widConfigTemplate.setAttribute("pkgName", pkg);
	            
	            byte [] workDefinitionsContentBytes = widConfigTemplate.toString().getBytes("UTF-8");
	            GuvnorUtil.createAsset(packageAssetsURL, "WorkDefinitions", ".wid", workDefinitionsContentBytes, profile);
			} catch (Exception e) {
                e.printStackTrace();
			}
    		
    	}
    }
    
    public Map<String, List<String>> findWorkitemInfoForUUID(String uuid, List<String> packageNames, IDiagramProfile profile) {
        boolean gotPackage = false;
        String pkg = "";
        Map<String, List<String>> packageConfigs = new HashMap<String, List<String>>();
        for(String nextPackage : packageNames) {
        	try {
	        	String packageAssetURL = GuvnorUtil.getUrl(profile, nextPackage, "", UrlType.Normal);
	        	String content = GuvnorUtil.readStringContentFromUrl(packageAssetURL, "GET", profile);

	            packageConfigs.put(nextPackage, new ArrayList<String>());
            
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(content));

                String format = "";
                String title = "";  
                while (reader.hasNext()) {
                    int next = reader.next();
                    if (next == XMLStreamReader.START_ELEMENT) {
                        if ("format".equals(reader.getLocalName())) {
                            format = reader.getElementText();
                        } 
                        if ("title".equals(reader.getLocalName())) {
                            title = reader.getElementText();
                        }
                        if ("uuid".equals(reader.getLocalName())) {
                            String eleText = reader.getElementText();
                            if(uuid.equals(eleText)) {
                                pkg = nextPackage;
                                gotPackage = true;
                            }
                        }
                    }
                    if (next == XMLStreamReader.END_ELEMENT) {
                      if ("asset".equals(reader.getLocalName())) {
                    	  if(format.equals(WORKITEM_DEFINITION_EXT)) {
                    		  packageConfigs.get(nextPackage).add(title);
                    		  title = "";
                    		  format = "";
                    	  }
                      }
                    }
                }
                if(format.equals("wid")) {
                    packageConfigs.get(nextPackage).add(title);
                }
            } catch (Exception e) {
                // we dont want to barf..just log that error happened
                _logger.error(e.getMessage());
            } 
            if(gotPackage) {
                // noo need to loop through rest of packages really
                break;
            }
        }
        Map<String, List<String>> returnData = new HashMap<String, List<String>>();
        returnData.put(pkg, packageConfigs.get(pkg));
        return returnData;
    }
    
    private static String readFile(String pathname) throws IOException {
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
    
    private static void deletefile(File f) {
        String fname = f.getAbsolutePath();
        boolean success = f.delete();
        if (!success){
            _logger.info("Unable to delete file :" + fname);
        } else {
            _logger.info("Successfully deleted file :" + fname);
        }
    }
    
    private static void createAndWriteToFile(String file, String content) throws Exception {
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
    
    public class ThemeInfo {
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
		
		public String toString() { 
		   return "{ " + bgColor + ", " + borderColor + ", " + fontColor + "}"; 
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
