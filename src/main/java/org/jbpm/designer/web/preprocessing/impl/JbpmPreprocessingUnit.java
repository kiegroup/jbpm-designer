package org.jbpm.designer.web.preprocessing.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.impl.ParameterDefinitionImpl;
import org.jbpm.designer.web.preprocessing.IDiagramPreprocessingUnit;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.drools.process.core.datatype.DataType;
import org.mvel2.MVEL;

import sun.misc.BASE64Encoder;


/**
 * JbpmPreprocessingUnit - preprocessing unit for the jbpm profile
 * 
 * @author Tihomir Surdilovic
 */
public class JbpmPreprocessingUnit implements IDiagramPreprocessingUnit {
    private static final Logger _logger = 
        Logger.getLogger(JbpmPreprocessingUnit.class);
    public final static String STENCILSET_PATH = "stencilsets";
    public static final String WORKITEM_DEFINITION_EXT = "wid";
    
    private String stencilPath;
    private String origStencilFilePath;
    private String stencilFilePath;
    private String outData = "";
    private String workitemSVGFilePath;
    private String origWorkitemSVGFile;
    private String default_emailicon;
    private String default_logicon;
    private String default_widconfigtemplate;
    
    public JbpmPreprocessingUnit(ServletContext servletContext) {
        stencilPath = servletContext.getRealPath("/" + STENCILSET_PATH);
        origStencilFilePath = stencilPath + "/bpmn2.0jbpm/stencildata/" + "bpmn2.0jbpm.orig";
        stencilFilePath = stencilPath + "/bpmn2.0jbpm/" + "bpmn2.0jbpm.json";
        workitemSVGFilePath = stencilPath  + "/bpmn2.0jbpm/view/activity/workitems/";
        origWorkitemSVGFile = workitemSVGFilePath + "workitem.orig";
        default_emailicon = servletContext.getRealPath("/defaults/defaultemailicon.gif");
        default_logicon = servletContext.getRealPath(  "/defaults/defaultlogicon.gif");
        default_widconfigtemplate = servletContext.getRealPath("/defaults/WorkDefinitions.wid.st");
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
        // check with guvnor to see what packages exist
        List<String> packageNames = findPackages(uuid, profile);
        
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
        			evaluateWorkDefinitions(workDefinitions, entry.getValue());
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
        			workItemTemplate.setAttribute("packageName", key);
        		}
        	} else {
        		workItemTemplate.setAttribute("packageName", "");
        	}
        	String[] info = findPackageAndAssetInfo(uuid, profile);
        	if(info != null && info.length == 2 && info[1] != null) {
        		workItemTemplate.setAttribute("processName", info[1]);
        	} else {
        		workItemTemplate.setAttribute("processName", "");
        	}
        	
        	// default the process id to packagename.processName
        	workItemTemplate.setAttribute("processid", workItemTemplate.getAttribute("packageName") + "." + workItemTemplate.getAttribute("processName")); 
        	// delete stencil data json if exists
        	deletefile(stencilFilePath);
        	// copy our results as the stencil json data
        	createAndWriteToFile(stencilFilePath, workItemTemplate.toString());
        	// create and parse the view svg to include config data
            createAndParseViewSVG(workDefinitions);
        } catch( Exception e ) {
            _logger.error("Failed to setup workitems : " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void createAndParseViewSVG(Map<String, WorkDefinitionImpl> workDefinitions) {
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
                String fileToWrite = workitemSVGFilePath + definition.getValue().getName() + ".svg";
                createAndWriteToFile(fileToWrite, workItemTemplate.toString());
            }
        } catch (Exception e) {
            _logger.error("Failed to setup workitem svg images : " + e.getMessage());
        } 
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void evaluateWorkDefinitions(Map<String, WorkDefinitionImpl> workDefinitions, String content) throws Exception {
        List<Map<String, Object>> workDefinitionsMaps = (List<Map<String, Object>>) MVEL.eval(content, new HashMap());
        
        for (Map<String, Object> workDefinitionMap : workDefinitionsMaps) {
            if (workDefinitionMap != null) {
                WorkDefinitionImpl workDefinition = new WorkDefinitionImpl();
                workDefinition.setName((String) workDefinitionMap.get("name"));
                workDefinition.setDisplayName((String) workDefinitionMap.get("displayName"));
                workDefinition.setIcon((String) workDefinitionMap.get("icon"));
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
                        String configURL = ExternalInfo.getExternalProtocol(profile) + "://" + ExternalInfo.getExternalHost(profile) +
                        "/" + profile.getExternalLoadURLSubdomain().substring(0, profile.getExternalLoadURLSubdomain().indexOf("/")) +
                        "/rest/packages/" + packageName + "/assets/" + configName + "/source/";
                
                        try {
                            InputStream in = getInputStreamForURL(configURL, profile);
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
    
    private InputStream getInputStreamForURL(String urlLocation, IDiagramProfile profile) throws Exception{
        // pretend we are mozilla
        URL url = new URL(urlLocation);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection
                .setRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.16) Gecko/20110319 Firefox/3.6.16");
        connection
                .setRequestProperty("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
        connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
        connection.setRequestProperty("charset", "UTF-8");
        connection.setReadTimeout(5 * 1000);
        
        if(profile.getUsr() != null && profile.getUsr().trim().length() > 0 && profile.getPwd() != null && profile.getPwd().trim().length() > 0) {
            BASE64Encoder enc = new sun.misc.BASE64Encoder();
            String userpassword = profile.getUsr() + ":" + profile.getPwd();
            String encodedAuthorization = enc.encode( userpassword.getBytes() );
            connection.setRequestProperty("Authorization", "Basic "+ encodedAuthorization);
        }
        
        connection.connect();
        
        BufferedReader sreader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        while ((line = sreader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }
        
        return new ByteArrayInputStream(stringBuilder.toString()
                .getBytes("UTF-8"));
    }
    
    private void setupDefaultWorkitemConfigs(String uuid, List<String> packageNames, IDiagramProfile profile) {
    	boolean gotPackage = false;
    	String pkg = "";
    	for(String nextPackage : packageNames) {
            String packageAssetURL = ExternalInfo.getExternalProtocol(profile) + "://" + ExternalInfo.getExternalHost(profile) +
            "/" + profile.getExternalLoadURLSubdomain().substring(0, profile.getExternalLoadURLSubdomain().indexOf("/")) +
            "/rest/packages/" + nextPackage + "/assets/";
            
            try {
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader = factory.createXMLStreamReader(getInputStreamForURL(packageAssetURL, profile));
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
    		// push the default workitem config and icons to guvnor
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
    		
    		String packageAssetsURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/" + pkg + "/assets/";
    		try {
				// check if the images already exists
				URL checkEmailIconURL = new URL(emailIconURL);
				HttpURLConnection checkEmailIconConnection = (HttpURLConnection) checkEmailIconURL
				        .openConnection();
				applyAuth(profile, checkEmailIconConnection);
				checkEmailIconConnection.setRequestMethod("GET");
				checkEmailIconConnection
				        .setRequestProperty("Accept", "application/atom+xml");
				checkEmailIconConnection.connect();
				System.out.println("check email icon connection response code: " + checkEmailIconConnection.getResponseCode());
				if (checkEmailIconConnection.getResponseCode() == 200) {
				    URL deleteAssetURL = new URL(emailIconURL);
				    HttpURLConnection deleteConnection = (HttpURLConnection) deleteAssetURL
				            .openConnection();
				    applyAuth(profile, deleteConnection);
				    deleteConnection.setRequestMethod("DELETE");
				    deleteConnection.connect();
				    System.out.println("delete email icon response code: " + deleteConnection.getResponseCode());
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
				if (checkLogIconConnection.getResponseCode() == 200) {
				    URL deleteAssetURL = new URL(logIconURL);
				    HttpURLConnection deleteConnection = (HttpURLConnection) deleteAssetURL
				            .openConnection();
				    applyAuth(profile, deleteConnection);
				    deleteConnection.setRequestMethod("DELETE");
				    deleteConnection.connect();
				    System.out.println("delete log icon response code: " + deleteConnection.getResponseCode());
				}
				
				// now push all defaults 
				// email icon
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
	            createEmailIconConnection.setDoOutput(true);
	            createEmailIconConnection.getOutputStream().write(getBytesFromFile(new File(default_emailicon)));
	            createEmailIconConnection.connect();
	            System.out.println("created email icon: " + createEmailIconConnection.getResponseCode());
				
	            // log icon
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
	            createLogIconConnection.setDoOutput(true);
	            createLogIconConnection.getOutputStream().write(getBytesFromFile(new File(default_logicon)));
	            createLogIconConnection.connect();
	            System.out.println("created log icon: " + createLogIconConnection.getResponseCode());
	            
				// default configuration wid
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
            String packageAssetURL = ExternalInfo.getExternalProtocol(profile) + "://" + ExternalInfo.getExternalHost(profile) +
            "/" + profile.getExternalLoadURLSubdomain().substring(0, profile.getExternalLoadURLSubdomain().indexOf("/")) +
            "/rest/packages/" + nextPackage + "/assets/";
            packageConfigs.put(nextPackage, new ArrayList<String>());
            
            try {
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader = factory.createXMLStreamReader(getInputStreamForURL(packageAssetURL, profile));

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
            XMLStreamReader reader = factory.createXMLStreamReader(getInputStreamForURL(packagesURL, profile));
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
    
    private String[] findPackageAndAssetInfo(String uuid,
            IDiagramProfile profile) throws Exception {
        List<String> packages = new ArrayList<String>();
        String packagesURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/";
         XMLInputFactory factory = XMLInputFactory.newInstance();
         XMLStreamReader reader = factory
                .createXMLStreamReader(getInputStreamForURL(packagesURL, profile));
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamReader.START_ELEMENT) {
                if ("title".equals(reader.getLocalName())) {
                    packages.add(reader.getElementText());
                }
            }
        }
        boolean gotPackage = false;
        String[] pkgassetinfo = new String[2];
        for (String nextPackage : packages) {
            String packageAssetURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/" + nextPackage + "/assets/";
            XMLInputFactory pfactory = XMLInputFactory.newInstance();
            XMLStreamReader preader = pfactory
                   .createXMLStreamReader(getInputStreamForURL(
                            packageAssetURL, profile));
            String title = "";
            while (preader.hasNext()) {
                int next = preader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(preader.getLocalName())) {
                        title = preader.getElementText();
                    }
                    if ("uuid".equals(preader.getLocalName())) {
                        String eleText = preader.getElementText();
                        if (uuid.equals(eleText)) {
                            pkgassetinfo[0] = nextPackage;
                            pkgassetinfo[1] = title;
                            gotPackage = true;
                        }
                    }
                }
            }
            if (gotPackage) {
                // noo need to loop through rest of packages
                break;
            }
        }
        return pkgassetinfo;
    }
    
    private String readFile(String pathname) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new File(pathname));
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
        output = new BufferedWriter(new FileWriter(file));
        output.write(content);
        output.close();
        _logger.info("Created file:" + file);
    }
    
    private void applyAuth(IDiagramProfile profile, HttpURLConnection connection) {
        if (profile.getUsr() != null && profile.getUsr().trim().length() > 0
                && profile.getPwd() != null
                && profile.getPwd().trim().length() > 0) {
            BASE64Encoder enc = new sun.misc.BASE64Encoder();
            String userpassword = profile.getUsr() + ":" + profile.getPwd();
            String encodedAuthorization = enc.encode(userpassword.getBytes());
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
}
