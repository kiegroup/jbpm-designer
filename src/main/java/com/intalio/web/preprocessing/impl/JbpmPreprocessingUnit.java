/***************************************
 * Copyright (c) Intalio, Inc 2010
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
package com.intalio.web.preprocessing.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
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
import org.jbpm.process.workitem.WorkDefinitionImpl;
import org.drools.process.core.datatype.DataType;
import org.mvel2.MVEL;

import sun.misc.BASE64Encoder;

import com.intalio.web.preprocessing.IDiagramPreprocessingUnit;
import com.intalio.web.profile.IDiagramProfile;
import com.intalio.web.profile.impl.ExternalInfo;

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
    
    public JbpmPreprocessingUnit(ServletContext servletContext) {
        stencilPath = servletContext.getRealPath("/" + STENCILSET_PATH);
        origStencilFilePath = stencilPath + "/bpmn2.0jbpm/stencildata/" + "bpmn2.0jbpm.orig";
        stencilFilePath = stencilPath + "/bpmn2.0jbpm/" + "bpmn2.0jbpm.json";
        workitemSVGFilePath = stencilPath  + "/bpmn2.0jbpm/view/activity/workitems/";
        origWorkitemSVGFile = workitemSVGFilePath + "workitem.orig";
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
        try {
            // parse the orig stencil data with workitem definitions
            StringTemplate workItemTemplate = new StringTemplate(readFile(origStencilFilePath));
            workItemTemplate.setAttribute("workitemDefs", workDefinitions);
            // delete stencil data json if exists
            deletefile(stencilFilePath);
            // copy our results as the stencil json data
            createAndWriteToFile(stencilFilePath, workItemTemplate.toString());
        } catch( Exception e ) {
            _logger.error("Failed to setup workitems : " + e.getMessage());
        }
        // create and parse the view svg to include config data
        createAndParseViewSVG(workDefinitions);
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
    private void evaluateWorkDefinitions(Map<String, WorkDefinitionImpl> workDefinitions, String content) {
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
}
