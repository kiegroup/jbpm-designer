package org.oryxeditor.server;

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

import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class Repository {

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
	 * Obtains the base URL from a HttpServletRequest
	 * 
	 * @param req HTTP request containing a request URL
	 * @return
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
	
	public String getModel(String path) {
		return getModel(path, "self");
	}

	public String getModel(String path, String representationType) {
		String result = "";
		try {
		    HttpClient client = new HttpClient();
		    GetMethod method = new GetMethod( baseUrl + path + "/" + representationType);
		    int statusCode = client.executeMethod( method );
			if( statusCode != -1 ) {
				result = method.getResponseBodyAsString();
			} else {
				 // TODO handle error
			}
		} catch( Exception e ) {
			// TODO handle exception
		}
		return result;
	}

	/**
	 * 
	 * @param modelId
	 * @param modelData
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
	 * @return
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
		
		String url = baseUrl + "backend/poem/repository/new?stencilset=" + stencilset;
		try {
		    HttpClient client = new HttpClient();
		    PostMethod method = new PostMethod( url );

			// Configure the form parameters
			method.addParameter("data", newModel);
			method.addParameter("title", name);
			method.addParameter("summary", summary);
			method.addParameter("type", type);
			method.addParameter("svg", svg);
			 // Execute the POST method
			 int statusCode = client.executeMethod( method );
			 if( statusCode != -1 ) {
		    	Header header = method.getResponseHeader("location");
		    	result = header.getValue();
		    	
		    	//hack for reverse proxies:
				result = result.substring(result.lastIndexOf("http://"));
		    	
		    	if (result.startsWith(baseUrl)){
		    		result = result.substring(baseUrl.length());
		    	}
			 } else {
				 // TODO handle error
			 }
		} catch( Exception e ) {
			// TODO handle exception
		}
		return result;
	}
}
