package org.oryxeditor.server;

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
	 * @param modelData
	 * @return
	 */
	public String generateERDF(String modelId, String modelData){
		return generateERDF(modelId, modelData, DEFAULT_STENCILSET, DEFAULT_MODEL_TYPE);
	}

	/**
	 * 
	 * @param modelData
	 * @param stencilset Relative path to stencilset, e.g., /stencilsets/bpmn1.1/bpmn1.1.json
	 * @return
	 */
	public String generateERDF(String modelId, String modelData, String stencilset){
		return generateERDF(modelId, modelData, stencilset, DEFAULT_MODEL_TYPE);
	}

	/**
	 * 
	 * @param modelData
	 * @param stencilset Relative path to stencilset, e.g., /stencilsets/bpmn1.1/bpmn1.1.json
	 * @param modelType
	 * @return
	 */
	public String generateERDF(String modelId, String modelData, String stencilset, String modelType){
		String stencilsetLocation = baseUrl + "oryx" + stencilset;
		return "<div id=\"oryx-canvas123\" class=\"-oryx-canvas\"><span class=\"oryx-type\">"+modelType+"</span><span class=\"oryx-id\">"+modelId+"</span><span class=\"oryx-name\"></span><span class=\"oryx-version\"></span><span class=\"oryx-author\"></span><span class=\"oryx-language\">English</span><span class=\"oryx-expressionlanguage\"></span><span class=\"oryx-querylanguage\"></span><span class=\"oryx-creationdate\"></span><span class=\"oryx-modificationdate\"></span><span class=\"oryx-pools\"></span><span class=\"oryx-documentation\"></span><span class=\"oryx-mode\">writable</span><span class=\"oryx-mode\">fullscreen</span><a rel=\"oryx-stencilset\" href=\""+stencilsetLocation+"\"/>"+modelData+"</div>";
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
