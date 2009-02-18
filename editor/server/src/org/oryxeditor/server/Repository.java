package org.oryxeditor.server;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class Repository {

	public static final String NEW_MODEL_SVG_STRING = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:oryx=\"http://oryx-editor.org\" id=\"oryx_98F1C176-75F8-4C0A-899E-0F5E352A5F58\" width=\"10\" height=\"10\" xlink=\"http://www.w3.org/1999/xlink\" svg=\"http://www.w3.org/2000/svg\"><defs/><g stroke=\"none\" font-family=\"Verdana\" font-size=\"12\"><g class=\"stencils\" transform=\"translate(0)\"><g class=\"me\"/><g class=\"children\"/><g class=\"edge\"/></g></g></svg>";
	public static final String DEFAULT_STENCILSET = "/stencilsets/bpmn1.1/bpmn1.1.json";
	public static final String DEFAULT_TYPE = "http://b3mn.org/stencilset/bpmn1.1#";
	
	public static String getModel(String completePath) {
		String result = "";
		try {
		    HttpClient client = new HttpClient();
		    GetMethod method = new GetMethod( completePath );
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

	public static String saveNewModel(String baseUrl, String newModel, String name){
		return saveNewModel(baseUrl, newModel, name, "", DEFAULT_TYPE, DEFAULT_STENCILSET, NEW_MODEL_SVG_STRING);
	}

	public static String saveNewModel(String baseUrl, String newModel, String name, String summary){
		return saveNewModel(baseUrl, newModel, name, summary, DEFAULT_TYPE, DEFAULT_STENCILSET, NEW_MODEL_SVG_STRING);
	}

	public static String saveNewModel(String baseUrl, String newModel, String name, String summary, String type){
		return saveNewModel(baseUrl, newModel, name, summary, type, DEFAULT_STENCILSET, NEW_MODEL_SVG_STRING);
	}

	public static String saveNewModel(String baseUrl, String newModel, String name, String summary, String type, String stencilset){
		return saveNewModel(baseUrl, newModel, name, summary, type, stencilset,
				NEW_MODEL_SVG_STRING);
	}

	public static String saveNewModel(String baseUrl, String newModel, String name, String summary, String type, String stencilset, String svg){
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
