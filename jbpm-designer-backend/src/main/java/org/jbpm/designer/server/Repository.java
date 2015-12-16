/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

/**
 * Copyright (c) 2008
 * SAP Research
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
 * 
 * 
 * The initial version of the code in this file has been developed by
 * Stefan Krumnow and Falko Menge at SAP Research Brisbane and has been
 * contributed to the Oryx project in October 2008 under the terms of the
 * MIT License.
 * 
 * @author Stefan Krumnow
 * @author Falko Menge
 * @author Jan-Felix Schwarz 
 **/

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class Repository {

    private static final Logger logger = LoggerFactory.getLogger(Repository.class);
    
	public static final String NEW_MODEL_SVG_STRING = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:oryx=\"http://oryx-editor.org\" id=\"oryx_98F1C176-75F8-4C0A-899E-0F5E352A5F58\" width=\"10\" height=\"10\" xlink=\"http://www.w3.org/1999/xlink\" svg=\"http://www.w3.org/2000/svg\"><defs/><g stroke=\"none\" font-family=\"Verdana\" font-size=\"12\"><g class=\"stencils\" transform=\"translate(0)\"><g class=\"me\"/><g class=\"children\"/><g class=\"edge\"/></g></g></svg>";
	public static final String DEFAULT_STENCILSET = "/stencilsets/bpmn1.1/bpmn1.1.json";
	public static final String DEFAULT_TYPE = "http://b3mn.org/stencilset/bpmn1.1#";
	public static final String DEFAULT_MODEL_TYPE = "http://b3mn.org/stencilset/bpmn1.1#BPMNDiagram";
	public static final String DEFAULT_MODEL_NAME = "Generated Model";
	public static final String DEFAULT_MODEL_DESCRIPTION = "The initial version of this model has been generated automatically.";

	/**
	 * URL prefix for the backend, e.g., http://localhost:8180/
	 */
	protected String baseUrl;

	/**
	 * Constructor
	 * 
	 * @param baseUrl URL prefix for the backend, e.g., http://localhost:8180/
	 */
	public Repository(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	/**
	 * Constructor
	 */
	public Repository(HttpServletRequest request) {
		this.baseUrl = getBaseUrl(request);
	}
	
	/**
	 * Obtains the base URL from a HttpServletRequest
	 * 
	 * @param req HTTP request containing a request URL
	 */
	public static String getBaseUrl(HttpServletRequest req) {
		String baseUrl = req.getRequestURL().toString();
		
		//hack for reverse proxies:
		baseUrl = baseUrl.substring(baseUrl.lastIndexOf("http://"));
		
		return baseUrl.substring(0, baseUrl.length()-req.getRequestURI().length() +1);
	}
	
	public static String getOryxUrl(HttpServletRequest req) {
		return getBaseUrl(req) + "oryx/";
	}
	
	public String getOryxUrl() {
		return baseUrl + "oryx/";
	}
	
	public static String getOryxPath() {
		// TODO: make this more platform independant
		return System.getProperty("catalina.home") + "/webapps/oryx/";
	}
	
	public String getModel(String path) {
		return getModel(path, "self");
	}

	public String getModel(String path, String representationType) {
		String result = "";
		
		String urlString = baseUrl + path + "/" + representationType;
		try {
		    HttpClient client = HttpClientBuilder.create().build();
		    HttpGet get = new HttpGet( urlString);
		    HttpResponse response = client.execute( get );
		    int statusCode = response.getStatusLine().getStatusCode();
			if( statusCode != -1 ) { 
			    HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);
			} else {
				 logger.error("GET to '{}' failed with status {}", urlString, statusCode );
			}
		} catch( Exception e ) {
		    logger.error("GET to '{}' failed: " + e.getMessage(), e );
		}
		return result;
	}

	/**
	 * 
	 * @param modelId
	 * @param modelData
	 */
	public String generateERDF(
			String modelId,
			String modelData
	){
		return generateERDF(
				modelId,
				modelData,
				DEFAULT_STENCILSET,
				DEFAULT_MODEL_TYPE,
				null,
				DEFAULT_MODEL_NAME, DEFAULT_MODEL_DESCRIPTION
		);
	}

	/**
	 * 
	 * @param modelId
	 * @param modelData
	 * @param stencilset Relative path to stencilset, e.g., /stencilsets/bpmn1.1/bpmn1.1.json
	 */
	public String generateERDF(
			String modelId,
			String modelData,
			String stencilset
	){
		return generateERDF(
				modelId,
				modelData,
				stencilset,
				DEFAULT_MODEL_TYPE,
				null,
				DEFAULT_MODEL_NAME,
				DEFAULT_MODEL_DESCRIPTION
		);
	}

	/**
	 * 
	 * @param modelId
	 * @param modelData
	 * @param stencilset Relative path to stencilset, e.g., /stencilsets/bpmn1.1/bpmn1.1.json
	 * @param modelType
	 */
	public String generateERDF(
			String modelId,
			String modelData,
			String stencilset,
			String modelType
	){
		return generateERDF(
				modelId,
				modelData,
				stencilset,
				modelType,
				null,
				DEFAULT_MODEL_NAME,
				DEFAULT_MODEL_DESCRIPTION
		);
	}

	/**
	 * 
	 * @param modelId
	 * @param modelData
	 * @param stencilset Relative path to stencilset, e.g., /stencilsets/bpmn1.1/bpmn1.1.json
	 * @param modelType
	 * @param stencilSetExtensionUrls TODO
	 */
	public String generateERDF(
			String modelId,
			String modelData,
			String stencilset,
			String modelType,
			List<String> stencilSetExtensionUrls
	){
		return generateERDF(
				modelId,
				modelData,
				stencilset,
				modelType,
				stencilSetExtensionUrls,
				DEFAULT_MODEL_NAME,
				DEFAULT_MODEL_DESCRIPTION
		);
	}

	/**
	 * 
	 * @param modelId
	 * @param modelData
	 * @param stencilset Relative path to stencilset, e.g., /stencilsets/bpmn1.1/bpmn1.1.json
	 * @param modelType
	 * @param stencilSetExtensionUrls TODO
	 * @param modelName TODO
	 */
	public String generateERDF(
			String modelId,
			String modelData,
			String stencilset,
			String modelType,
			List<String> stencilSetExtensionUrls,
			String modelName
	){
		return generateERDF(
				modelId,
				modelData,
				stencilset,
				modelType,
				stencilSetExtensionUrls,
				modelName,
				DEFAULT_MODEL_DESCRIPTION
		);
	}

	/**
	 * 
	 * @param modelId
	 * @param modelData
	 * @param stencilset Relative path to stencilset, e.g., /stencilsets/bpmn1.1/bpmn1.1.json
	 * @param modelType
	 * @param stencilSetExtensionUrls TODO
	 * @param modelName TODO
	 * @param modelDescription TODO
	 */
	public String generateERDF(
			String modelId,
			String modelData,
			String stencilset,
			String modelType,
			List<String> stencilSetExtensionUrls,
			String modelName,
			String modelDescription
	){
		String stencilsetLocation = baseUrl + "oryx" + stencilset;
		//TODO: remove modelId, since it doesn't seem to be used any more
		String erdf = "<div id=\"oryx-canvas123\" class=\"-oryx-canvas\">"
			+ "<span class=\"oryx-type\">" + modelType + "</span>"
			+ "<span class=\"oryx-id\">" + modelId + "</span>"
			+ "<span class=\"oryx-name\">" + modelName + "</span>"
			+ "<span class=\"oryx-version\"></span>"
			+ "<span class=\"oryx-author\"></span>"
			+ "<span class=\"oryx-language\">English</span>"
			+ "<span class=\"oryx-expressionlanguage\"></span>"
			+ "<span class=\"oryx-querylanguage\"></span>"
			+ "<span class=\"oryx-creationdate\"></span>"
			+ "<span class=\"oryx-modificationdate\"></span>"
			+ "<span class=\"oryx-pools\"></span>"
			+ "<span class=\"oryx-documentation\">" + modelDescription + "</span>"
			+ "<span class=\"oryx-mode\">writable</span>"
			+ "<span class=\"oryx-mode\">fullscreen</span>"
			+ "<a rel=\"oryx-stencilset\" href=\"" + stencilsetLocation + "\"/>";
		if (stencilSetExtensionUrls != null) {
			ListIterator<String> iterator = stencilSetExtensionUrls.listIterator();
			while (iterator.hasNext()) {
				erdf += "<span class=\"oryx-ssextension\">" + iterator.next() + "</span>";
			}
		}
		erdf += modelData + "</div>";
		return erdf;
	}
		
	public String saveNewModel(String newModel, String name){
		return saveNewModel(newModel, name, "", DEFAULT_TYPE, DEFAULT_STENCILSET, NEW_MODEL_SVG_STRING);
	}

	public String saveNewModel(String newModel, String name, String summary){
		return saveNewModel(newModel, name, summary, DEFAULT_TYPE, DEFAULT_STENCILSET, NEW_MODEL_SVG_STRING);
	}

	public String saveNewModel(String newModel, String name, String summary, String type){
		return saveNewModel(newModel, name, summary, type, DEFAULT_STENCILSET, NEW_MODEL_SVG_STRING);
	}

	public String saveNewModel(String newModel, String name, String summary, String type, String stencilset){
		return saveNewModel(newModel, name, summary, type, stencilset,
				NEW_MODEL_SVG_STRING);
	}

	public String saveNewModel(String newModel, String name, String summary, String type, String stencilset, String svg){
	    String result = "";

	    // setup 
	    String url = baseUrl + "backend/poem/repository/new?stencilset=" + stencilset;
	    HttpClient client = HttpClientBuilder.create().build();

	    // configure the form parameters
	    List<NameValuePair> formParams = new ArrayList<NameValuePair>(2);
	    formParams.add(new BasicNameValuePair("data", newModel));
	    formParams.add(new BasicNameValuePair("title", name));
	    formParams.add(new BasicNameValuePair("summary", summary));
	    formParams.add(new BasicNameValuePair("type", type));
	    formParams.add(new BasicNameValuePair("svg", svg));
	    UrlEncodedFormEntity formEntity;

	    try {
	        formEntity = new UrlEncodedFormEntity(formParams);
	    } catch( UnsupportedEncodingException uee ) {
	        logger.error("Could not encode authentication parameters into request body", uee);
	        return result;
	    }

	    // create POST method and add form 
	    HttpPost post = new HttpPost( url);
	    post.setEntity(formEntity);
	    
	    try {
	        // execute the POST method
	        HttpResponse response = client.execute( post );
            int statusCode = response.getStatusLine().getStatusCode();
            if( statusCode != -1 ) { 
                Header [] headers = response.getHeaders("location");
				if( headers.length > 0 ) { 
				    result = headers[0].getValue();
				    // hack for reverse proxies:
				    result = result.substring(result.lastIndexOf("http://"));
				    if (result.startsWith(baseUrl)){
				        result = result.substring(baseUrl.length());
				    }
				} else {
				    logger.error("GET to '{}' failed with status {}", url, statusCode );
				}
            } 
            else { 
               logger.error( "POST to [{}] resulted in status code {} ", url, statusCode);
            }
		} catch( Exception e ) { 
		    logger.error("POST to [" + url + "] failed: " + e.getMessage(), e );
		} 
			
		return result;
	}
	
	public String saveNewModelErdf(String newModel, String name, ServletContext context){
		return saveNewModel(erdfToJson(newModel, context), name, "", DEFAULT_TYPE, DEFAULT_STENCILSET, NEW_MODEL_SVG_STRING);
	}

	public String saveNewModelErdf(String newModel, String name, String summary, ServletContext context){
		return saveNewModel(erdfToJson(newModel, context), name, summary, DEFAULT_TYPE, DEFAULT_STENCILSET, NEW_MODEL_SVG_STRING);
	}

	public String saveNewModelErdf(String newModel, String name, String summary, String type, ServletContext context){
		return saveNewModel(erdfToJson(newModel, context), name, summary, type, DEFAULT_STENCILSET, NEW_MODEL_SVG_STRING);
	}

	public String saveNewModelErdf(String newModel, String name, String summary, String type, String stencilset, ServletContext context){
		return saveNewModel(erdfToJson(newModel, context), name, summary, type, stencilset,
				NEW_MODEL_SVG_STRING);
	}

	public String saveNewModelErdf(String newModel, String name, String summary, String type, String stencilset, String svg, ServletContext context){
		return saveNewModel(erdfToJson(newModel, context), name, summary, type, stencilset, svg);
	}
	
	public void addTag(String modelUrl, String tagName) {
		if (modelUrl.endsWith("/self")) {
			modelUrl = modelUrl.substring(0, modelUrl.lastIndexOf("/self"));
		}
		String modelTagsUrl = modelUrl + "/tags";

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(modelTagsUrl);
	    
		// configure the form parameters
        List<NameValuePair> formParams = new ArrayList<NameValuePair>(2);
        formParams.add(new BasicNameValuePair("tag_name", tagName));
        UrlEncodedFormEntity formEntity;

        try {
            formEntity = new UrlEncodedFormEntity(formParams);
        } catch( UnsupportedEncodingException uee ) {
            logger.error("Could not encode authentication parameters into request body", uee);
            return;
        }

		// execute the POST method
		int statusCode;
		try {
			HttpResponse response = client.execute(post);
			statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != -1) {
				// TODO return result 
			} else {
			    logger.error( "POST to [" + modelTagsUrl + "] failed with status code " + statusCode );
			}
		} catch (Exception e) {
			logger.error( "POST to [" + modelTagsUrl + "] failed: " + e.getMessage(), e );
		}
	}
	
	protected String erdfToJson(String erdf, ServletContext context) {
		try {
			String rdf = erdfToRdf(erdf, context);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document rdfDoc = builder.parse(new ByteArrayInputStream(rdf.getBytes("UTF-8")));
			return RdfJsonTransformation.toJson(rdfDoc, "").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	protected String erdfToRdf(String erdf, ServletContext context) throws TransformerException{
		String serializedDOM = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<html xmlns=\"http://www.w3.org/1999/xhtml\" " +
		"xmlns:b3mn=\"http://b3mn.org/2007/b3mn\" " +
		"xmlns:ext=\"http://b3mn.org/2007/ext\" " +
		"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "  +
		"xmlns:atom=\"http://b3mn.org/2007/atom+xhtml\">" +
		"<head profile=\"http://purl.org/NET/erdf/profile\">" +
		"<link rel=\"schema.dc\" href=\"http://purl.org/dc/elements/1.1/\" />" +
		"<link rel=\"schema.dcTerms\" href=\"http://purl.org/dc/terms/ \" />" +
		"<link rel=\"schema.b3mn\" href=\"http://b3mn.org\" />" +
		"<link rel=\"schema.oryx\" href=\"http://oryx-editor.org/\" />" +
		"<link rel=\"schema.raziel\" href=\"http://raziel.org/\" />" +
		"</head><body>" + erdf + "</body></html>";
        
		InputStream xsltStream = context.getResourceAsStream("/WEB-INF/lib/extract-rdf.xsl");
        Source xsltSource = new StreamSource(xsltStream);
        Source erdfSource = new StreamSource(new StringReader(serializedDOM));

        TransformerFactory transFact =
                TransformerFactory.newInstance();
        Transformer trans = transFact.newTransformer(xsltSource);
        StringWriter output = new StringWriter();
        trans.transform(erdfSource, new StreamResult(output));
		return output.toString();
	}
}
