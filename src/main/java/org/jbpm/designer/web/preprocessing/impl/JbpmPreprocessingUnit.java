package org.jbpm.designer.web.preprocessing.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.abdera.i18n.text.Sanitizer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.designer.web.preprocessing.IDiagramPreprocessingUnit;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.jbpm.designer.web.server.ServletUtil;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.json.JSONObject;
import org.junit.runner.Request;
import org.drools.process.core.datatype.DataType;
import org.mvel2.MVEL;

import org.apache.commons.codec.binary.Base64;

import sun.misc.BASE64Encoder;

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
    private String themeInfo;
    private String formWidgetsDir;
    private String customEditorsInfo;
    
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
        setupCustomEditors(profile, req);
        // check with guvnor to see what packages exist
        List<String> packageNames = findPackages(uuid, profile);
        String[] info = ServletUtil.findPackageAndAssetInfo(uuid, profile);
        
        // set up form widgets
        setupFormWidgets(profile, req);
        // set up default icons
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
        			evaluateWorkDefinitions(workDefinitions, workitemConfigInfo, entry.getValue(), profile);
        		}
        	}
        	// set the out parameter
        	for(Map.Entry<String, WorkDefinitionImpl> definition : workDefinitions.entrySet()) {
        		outData += definition.getValue().getName() + ",";
        	}
        	// parse the profile json to include config data
        	// parse the orig stencil data with workitem definitions
        	StringTemplate workItemTemplate = new StringTemplate(readFile(origStencilFilePath));
        	workItemTemplate.setAttribute("workitemDefs", workDefinitions);
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
    private void createAndParseViewSVG(Map<String, WorkDefinitionImpl> workDefinitions, IDiagramProfile profile) {
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
                InputStream iconStream = getImageInstream(widIcon, "GET", profile);
                BASE64Encoder enc = new sun.misc.BASE64Encoder();
                String iconEncoded = "data:image/png;base64," + enc.encode(IOUtils.toByteArray(iconStream));
                workItemTemplate.setAttribute("nodeicon", iconEncoded);
                String fileToWrite = workitemSVGFilePath + definition.getValue().getName() + ".svg";
                createAndWriteToFile(fileToWrite, workItemTemplate.toString());
            }
        } catch (Exception e) {
            _logger.error("Failed to setup workitem svg images : " + e.getMessage());
        } 
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void evaluateWorkDefinitions(Map<String, WorkDefinitionImpl> workDefinitions, Map<String, List<String>> configInfo, String content, IDiagramProfile profile) throws Exception {
    	List<Map<String, Object>> workDefinitionsMaps = (List<Map<String, Object>>) MVEL.eval(content, new HashMap());
        
        for (Map<String, Object> workDefinitionMap : workDefinitionsMaps) {
            if (workDefinitionMap != null) {
                WorkDefinitionImpl workDefinition = new WorkDefinitionImpl();
                workDefinition.setName(((String) workDefinitionMap.get("name")).replaceAll("\\s",""));
                workDefinition.setDisplayName((String) workDefinitionMap.get("displayName"));
                String icon = (String) workDefinitionMap.get("icon");
                if(icon.length() < 1) {
                	String packageName = "";
                	for(Map.Entry<String, List<String>> entry : configInfo.entrySet()) {
                        packageName = entry.getKey();
                	}
                	icon = ExternalInfo.getExternalProtocol(profile) + "://" + ExternalInfo.getExternalHost(profile) +
                            "/" + profile.getExternalLoadURLSubdomain().substring(0, profile.getExternalLoadURLSubdomain().indexOf("/")) +
                            "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/defaultservicenodeicon/binary/";
                }
                workDefinition.setIcon(icon);
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
    
    private Map<String, String> getWorkitemConfigContent(Map<String, List<String>> configInfo, IDiagramProfile profile) {
        Map<String, String> resultsMap = new HashMap<String, String>();
        if(configInfo.size() > 0) {
            for(Map.Entry<String, List<String>> entry : configInfo.entrySet()) {
                String packageName = entry.getKey();
                List<String> configNames = entry.getValue();
                if(configNames != null) {
                    for(String configName : configNames) {
                    	try {
	                    	String configURL = ExternalInfo.getExternalProtocol(profile) + "://" + ExternalInfo.getExternalHost(profile) +
	                        "/" + profile.getExternalLoadURLSubdomain().substring(0, profile.getExternalLoadURLSubdomain().indexOf("/")) +
	                        "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + configName + "/source/";
                
                            InputStream in =  ServletUtil.getInputStreamForURL(configURL, "GET", profile);
                            StringWriter writer = new StringWriter();
                            IOUtils.copy(in, writer, "UTF-8");
                            resultsMap.put(configName, writer.toString());
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
    
    private void setupFormWidgets(IDiagramProfile profile, HttpServletRequest req) {
    	String formWidgetsPackageURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/";
    	File[] allFormWidgets = new File(formWidgetsDir).listFiles();
    	for(File formWidget : allFormWidgets) {
    		int extPosition = formWidget.getName().lastIndexOf(".");
    		String widgetNameOnly = formWidget.getName().substring(0,extPosition);
    		String widgetURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/globalArea/assets/" + widgetNameOnly;
    		try {
	    		URL checkURL = new URL(widgetURL);
		        HttpURLConnection checkConnection = (HttpURLConnection) checkURL.openConnection();
		        ServletUtil.applyAuth(profile, checkConnection);
		        checkConnection.setRequestMethod("GET");
		        checkConnection.setRequestProperty("charset", "UTF-8");
		        checkConnection.connect();
		        _logger.info("check connection response code: " + checkConnection.getResponseCode());
		        if (checkConnection.getResponseCode() != 200) {
		        	URL createURL = new URL(formWidgetsPackageURL);
		            HttpURLConnection createConnection = (HttpURLConnection) createURL
		                    .openConnection();
		            ServletUtil.applyAuth(profile, createConnection);
		            createConnection.setRequestMethod("POST");
		            createConnection.setRequestProperty("Content-Type",
		                    "application/octet-stream");
		            createConnection.setRequestProperty("Accept",
		                    "application/atom+xml");
		            createConnection.setRequestProperty("charset", "UTF-8");
		            createConnection.setRequestProperty("Slug", formWidget.getName());
		            createConnection.setDoOutput(true);
		            createConnection.getOutputStream().write(getBytesFromFile(formWidget));
		            createConnection.connect();
		            _logger.info("create form widget connection response code: " + createConnection.getResponseCode());
		        }
    		} catch (Exception e) {
                // we dont want to barf..just log that error happened
                _logger.error("Error setting up form widgets: " + e.getMessage());
            } 
    	}
    }
    
    private void setupCustomEditors(IDiagramProfile profile, HttpServletRequest req) {
    	String customEditorsURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + CUSTOMEDITORS_NAME;
		String customEditorsAssetsURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/";
		
		try {
	        URL checkURL = new URL(customEditorsURL);
	        HttpURLConnection checkConnection = (HttpURLConnection) checkURL
	                .openConnection();
	        ServletUtil.applyAuth(profile, checkConnection);
	        checkConnection.setRequestMethod("GET");
	        checkConnection.setRequestProperty("charset", "UTF-8");
	        checkConnection
	                .setRequestProperty("Accept", "application/atom+xml");
	        checkConnection.connect();
	        _logger.info("check connection response code: " + checkConnection.getResponseCode());
	        if (checkConnection.getResponseCode() != 200) {
	        	URL createURL = new URL(customEditorsAssetsURL);
	            HttpURLConnection createConnection = (HttpURLConnection) createURL
	                    .openConnection();
	            ServletUtil.applyAuth(profile, createConnection);
	            createConnection.setRequestMethod("POST");
	            createConnection.setRequestProperty("Content-Type",
	                    "application/octet-stream");
	            createConnection.setRequestProperty("Accept",
	                    "application/atom+xml");
	            createConnection.setRequestProperty("charset", "UTF-8");
	            createConnection.setRequestProperty("Slug", CUSTOMEDITORS_NAME  + CUSTOMEDITORS_EXT);
	            createConnection.setDoOutput(true);
	            createConnection.getOutputStream().write(getBytesFromFile(new File(customEditorsInfo)));
	            createConnection.connect();
	            _logger.info("create custom editors connection response code: " + createConnection.getResponseCode());
	        }
		} catch (Exception e) {
            _logger.error(e.getMessage());
        }
    }
    
    private Map<String, ThemeInfo> setupThemes(IDiagramProfile profile, HttpServletRequest req) {
    	Map<String, ThemeInfo> themeData = new HashMap<String, JbpmPreprocessingUnit.ThemeInfo>();
    	String themesURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + THEME_NAME;
    	
    	String themesSourceURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + THEME_NAME + "/source";
		
		String themesAssetsURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/";
		try {
	        URL checkURL = new URL(themesURL);
	        HttpURLConnection checkConnection = (HttpURLConnection) checkURL
	                .openConnection();
	        ServletUtil.applyAuth(profile, checkConnection);
	        checkConnection.setRequestMethod("GET");
	        checkConnection
	                .setRequestProperty("Accept", "application/atom+xml");
	        checkConnection.setRequestProperty("charset", "UTF-8");
	        checkConnection.connect();
	        System.out.println("check connection response code: " + checkConnection.getResponseCode());
	        if(checkConnection.getResponseCode() == 404) {
	        	checkConnection.disconnect();
	        } 	
	        if (checkConnection.getResponseCode() != 200) {
	        	URL createURL = new URL(themesAssetsURL);
	            HttpURLConnection createConnection = (HttpURLConnection) createURL
	                    .openConnection();
	            ServletUtil.applyAuth(profile, createConnection);
	            createConnection.setRequestMethod("POST");
	            createConnection.setRequestProperty("Content-Type",
	                    "application/octet-stream");
	            createConnection.setRequestProperty("Accept",
	                    "application/atom+xml");
	            createConnection.setRequestProperty("Slug", THEME_NAME + THEME_EXT);
	            checkConnection.setRequestProperty("charset", "UTF-8");
	            createConnection.setDoOutput(true);
	            createConnection.getOutputStream().write(getBytesFromFile(new File(themeInfo)));
	            createConnection.connect();
	            System.out.println("create themes connection response code: " + createConnection.getResponseCode());
	        }
	        
	        String themesStr;
			try {
				themesStr = ServletUtil.streamToString(ServletUtil.getInputStreamForURL(themesSourceURL, "GET", profile));
			} catch (Exception e) {
				themesStr = readFile(themeInfo);
			}
	        
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
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
            return themeData;
        }
    }
    
    private void setupDefaultIcons(String[] info, IDiagramProfile profile) {
    	if(info != null && info.length == 2) {
    		try {
	    		String pkg = URLEncoder.encode(info[0], "UTF-8");
	    		
	    		String emailIconURL = ExternalInfo.getExternalProtocol(profile)
	                    + "://"
	                    + ExternalInfo.getExternalHost(profile)
	                    + "/"
	                    + profile.getExternalLoadURLSubdomain().substring(0,
	                            profile.getExternalLoadURLSubdomain().indexOf("/"))
	                    + "/rest/packages/" + pkg + "/assets/" + "defaultemailicon"
	                    + ".gif";
	    		String logIconURL = ExternalInfo.getExternalProtocol(profile)
	                    + "://"
	                    + ExternalInfo.getExternalHost(profile)
	                    + "/"
	                    + profile.getExternalLoadURLSubdomain().substring(0,
	                            profile.getExternalLoadURLSubdomain().indexOf("/"))
	                    + "/rest/packages/" + pkg + "/assets/" + "defaultlogicon"
	                    + ".gif";
	    		
	    		String serviceNodeIconURL = ExternalInfo.getExternalProtocol(profile)
	                    + "://"
	                    + ExternalInfo.getExternalHost(profile)
	                    + "/"
	                    + profile.getExternalLoadURLSubdomain().substring(0,
	                            profile.getExternalLoadURLSubdomain().indexOf("/"))
	                    + "/rest/packages/" + pkg + "/assets/" + "defaultservicenodeicon"
	                    + ".png";
	    		
	    		String packageAssetsURL = ExternalInfo.getExternalProtocol(profile)
	                    + "://"
	                    + ExternalInfo.getExternalHost(profile)
	                    + "/"
	                    + profile.getExternalLoadURLSubdomain().substring(0,
	                            profile.getExternalLoadURLSubdomain().indexOf("/"))
	                    + "/rest/packages/" + pkg + "/assets/";
	    		
				// check if the images already exists
				URL checkEmailIconURL = new URL(emailIconURL);
				HttpURLConnection checkEmailIconConnection = (HttpURLConnection) checkEmailIconURL
				        .openConnection();
				applyAuth(profile, checkEmailIconConnection);
				checkEmailIconConnection.setRequestMethod("GET");
				checkEmailIconConnection.setRequestProperty("charset", "UTF-8");
				checkEmailIconConnection
				        .setRequestProperty("Accept", "application/atom+xml");
				checkEmailIconConnection.connect();
				System.out.println("check email icon connection response code: " + checkEmailIconConnection.getResponseCode());
				if (checkEmailIconConnection.getResponseCode() != 200) {
					URL createEmailIconURL = new URL(packageAssetsURL);
		            HttpURLConnection createEmailIconConnection = (HttpURLConnection) createEmailIconURL
		                    .openConnection();
		            applyAuth(profile, createEmailIconConnection);
		            createEmailIconConnection.setRequestMethod("POST");
		            createEmailIconConnection.setRequestProperty("Content-Type",
		                    "application/octet-stream");
		            createEmailIconConnection.setRequestProperty("Accept",
		                    "application/atom+xml");
		            createEmailIconConnection.setRequestProperty("Slug", "defaultemailicon.gif");
		            createEmailIconConnection.setRequestProperty("charset", "UTF-8");
		            createEmailIconConnection.setDoOutput(true);
		            createEmailIconConnection.getOutputStream().write(getBytesFromFile(new File(default_emailicon)));
		            createEmailIconConnection.connect();
		            System.out.println("created email icon: " + createEmailIconConnection.getResponseCode());
				}
				
				URL checkLogIconURL = new URL(logIconURL);
				HttpURLConnection checkLogIconConnection = (HttpURLConnection) checkLogIconURL
				        .openConnection();
				applyAuth(profile, checkLogIconConnection);
				checkLogIconConnection.setRequestMethod("GET");
				checkLogIconConnection
				        .setRequestProperty("Accept", "application/atom+xml");
				checkLogIconConnection.connect();
				System.out.println("check log icon connection response code: " + checkLogIconConnection.getResponseCode());
				if (checkLogIconConnection.getResponseCode() != 200) {
		            URL createLogIconURL = new URL(packageAssetsURL);
		            HttpURLConnection createLogIconConnection = (HttpURLConnection) createLogIconURL
		                    .openConnection();
		            applyAuth(profile, createLogIconConnection);
		            createLogIconConnection.setRequestMethod("POST");
		            createLogIconConnection.setRequestProperty("Content-Type",
		                    "application/octet-stream");
		            createLogIconConnection.setRequestProperty("Accept",
		                    "application/atom+xml");
		            createLogIconConnection.setRequestProperty("Slug", "defaultlogicon.gif");
		            createLogIconConnection.setRequestProperty("charset", "UTF-8");
		            createLogIconConnection.setDoOutput(true);
		            createLogIconConnection.getOutputStream().write(getBytesFromFile(new File(default_logicon)));
		            createLogIconConnection.connect();
		            System.out.println("created log icon: " + createLogIconConnection.getResponseCode());
				}
				
				URL checkServiceNodeIconURL = new URL(serviceNodeIconURL);
				HttpURLConnection checkServiceNodeIconConnection = (HttpURLConnection) checkServiceNodeIconURL
				        .openConnection();
				applyAuth(profile, checkServiceNodeIconConnection);
				checkServiceNodeIconConnection.setRequestMethod("GET");
				checkServiceNodeIconConnection
				        .setRequestProperty("Accept", "application/atom+xml");
				checkServiceNodeIconConnection.connect();
				System.out.println("check service node icon connection response code: " + checkServiceNodeIconConnection.getResponseCode());
				if (checkServiceNodeIconConnection.getResponseCode() != 200) {
		            URL createServiceNodeIconURL = new URL(packageAssetsURL);
		            HttpURLConnection createServiceNodeIconConnection = (HttpURLConnection) createServiceNodeIconURL
		                    .openConnection();
		            applyAuth(profile, createServiceNodeIconConnection);
		            createServiceNodeIconConnection.setRequestMethod("POST");
		            createServiceNodeIconConnection.setRequestProperty("Content-Type",
		                    "application/octet-stream");
		            createServiceNodeIconConnection.setRequestProperty("Accept",
		                    "application/atom+xml");
		            createServiceNodeIconConnection.setRequestProperty("Slug", "defaultservicenodeicon.png");
		            createServiceNodeIconConnection.setRequestProperty("charset", "UTF-8");
		            createServiceNodeIconConnection.setDoOutput(true);
		            createServiceNodeIconConnection.getOutputStream().write(getBytesFromFile(new File(default_servicenodeicon)));
		            createServiceNodeIconConnection.connect();
		            System.out.println("created service node icon: " + createServiceNodeIconConnection.getResponseCode());
				}
			} catch (Exception e) {
                e.printStackTrace();
			}
    	} else {
    		System.out.println("Unable to set up default icons.");
    	}
    }
    
    private void setupDefaultWorkitemConfigs(String uuid, List<String> packageNames, IDiagramProfile profile) {
    	boolean gotPackage = false;
    	String pkg = "";
    	for(String nextPackage : packageNames) {
    		try {	
	    		String packageAssetURL = ExternalInfo.getExternalProtocol(profile) + "://" + ExternalInfo.getExternalHost(profile) +
	            "/" + profile.getExternalLoadURLSubdomain().substring(0, profile.getExternalLoadURLSubdomain().indexOf("/")) +
	            "/rest/packages/" + URLEncoder.encode(nextPackage, "UTF-8") + "/assets/";
            
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader = factory.createXMLStreamReader(ServletUtil.getInputStreamForURL(packageAssetURL, "GET", profile), "UTF-8");
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
    		String packageAssetsURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/" + pkg + "/assets/";
    		try {
				// push default configuration wid
	            StringTemplate widConfigTemplate = new StringTemplate(readFile(default_widconfigtemplate));
	            widConfigTemplate.setAttribute("protocol", ExternalInfo.getExternalProtocol(profile));
	            widConfigTemplate.setAttribute("host", ExternalInfo.getExternalHost(profile));
	            widConfigTemplate.setAttribute("subdomain", profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/")));
	            widConfigTemplate.setAttribute("pkgName", pkg);
	            
	            URL createWidURL = new URL(packageAssetsURL);
	            HttpURLConnection createWidConnection = (HttpURLConnection) createWidURL
	                    .openConnection();
	            applyAuth(profile, createWidConnection);
	            createWidConnection.setRequestMethod("POST");
	            createWidConnection.setRequestProperty("Content-Type",
	                    "application/octet-stream");
	            createWidConnection.setRequestProperty("Accept",
	                    "application/atom+xml");
	            createWidConnection.setRequestProperty("Slug", "WorkDefinitions.wid");
	            createWidConnection.setRequestProperty("charset", "UTF-8");
	            createWidConnection.setDoOutput(true);
	            createWidConnection.getOutputStream().write(widConfigTemplate.toString().getBytes("UTF-8"));
	            createWidConnection.connect();
	            System.out.println("created default wid: " + createWidConnection.getResponseCode());
			} catch (Exception e) {
                e.printStackTrace();
			}
    		
    	}
    }
    
    private Map<String, List<String>> findWorkitemInfoForUUID(String uuid, List<String> packageNames, IDiagramProfile profile) {
        boolean gotPackage = false;
        String pkg = "";
        Map<String, List<String>> packageConfigs = new HashMap<String, List<String>>();
        for(String nextPackage : packageNames) {
        	try {
	        	String packageAssetURL = ExternalInfo.getExternalProtocol(profile) + "://" + ExternalInfo.getExternalHost(profile) +
	            "/" + profile.getExternalLoadURLSubdomain().substring(0, profile.getExternalLoadURLSubdomain().indexOf("/")) +
	            "/rest/packages/" + URLEncoder.encode(nextPackage, "UTF-8") + "/assets/";
	            packageConfigs.put(nextPackage, new ArrayList<String>());
	            
            
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader = factory.createXMLStreamReader(ServletUtil.getInputStreamForURL(packageAssetURL, "GET", profile), "UTF-8");

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
    
    private List<String> findPackages(String uuid, IDiagramProfile profile) {
        List<String> packages = new ArrayList<String>();
        String packagesURL = ExternalInfo.getExternalProtocol(profile) + "://" + ExternalInfo.getExternalHost(profile) +
        "/" + profile.getExternalLoadURLSubdomain().substring(0, profile.getExternalLoadURLSubdomain().indexOf("/")) +
        "/rest/packages/";
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(ServletUtil.getInputStreamForURL(packagesURL, "GET", profile), "UTF-8");
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(reader.getLocalName())) {
                        packages.add(reader.getElementText());
                    }
                }
            }
        } catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        } 
        
        
        return packages;
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
    
    private void applyAuth(IDiagramProfile profile, HttpURLConnection connection) {
        if (profile.getUsr() != null && profile.getUsr().trim().length() > 0
                && profile.getPwd() != null
                && profile.getPwd().trim().length() > 0) {
            String userpassword = profile.getUsr() + ":" + profile.getPwd();
            String encodedAuthorization = Base64.encodeBase64String(userpassword.getBytes());
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedAuthorization);
        }
    }
    public static byte[] getBytesFromFile(File file) throws IOException {
    	InputStream is = null;
    	is = new FileInputStream(file);
    	long length = file.length();

    	if (length > Integer.MAX_VALUE) {
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
    		throw new IOException("Could not completely read file " + file.getName());
    	}
    	is.close();
    	return bytes;
    }
    
    public static InputStream getImageInstream(String urlLocation,
            String requestMethod, IDiagramProfile profile) throws Exception {
        URL url = new URL(urlLocation);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml,application/json,application/octet-stream,text/json,text/plain;q=0.9,*/*;q=0.8");

        connection.setRequestProperty("charset", "UTF-8");
        connection.setReadTimeout(5 * 1000);

        ServletUtil.applyAuth(profile, connection);
        connection.connect();
        return connection.getInputStream();
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
}
