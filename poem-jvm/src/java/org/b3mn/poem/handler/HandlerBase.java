/***************************************
 * Copyright (c) 2008
 * Bjoern Wagner
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

package org.b3mn.poem.handler;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Access;
import org.b3mn.poem.Dispatcher;
import org.b3mn.poem.Identity;
import org.b3mn.poem.Plugin;
import org.b3mn.poem.Representation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class HandlerBase {
	private ServletContext context;
	private static Dispatcher dispatcher;
	
	public static Dispatcher getDispatcher() {
		return dispatcher;
	}
	
	public static void setDispatcher(Dispatcher d) {
		dispatcher = d;
	}
	
	public static String getPublicUser() {
		return dispatcher.getPublicUser();
	}
	
	public ServletContext getServletContext() {
		return this.context;
	}
	
	public void setServletContext(ServletContext context) {
		this.context = context;
	}
	
	
	// Returns a JSONObject that contain all keys given in the String array and their values
	@Deprecated
	protected JSONObject toJSON(Object o, String[] keys) {
		Class c = o.getClass();
		JSONObject json = new JSONObject();
		for (String method : keys) {
			try {
				// Invoke the getter method of the key with some reflection magic ;)
				json.put(method.toLowerCase(), c.getMethod("get" + method, null).invoke(o));
			} catch (Exception e) {
				return null;
			}
		}
		return json;
	}
	
	protected JSONObject getModelMetaData(Identity subject, Identity model, HttpServletRequest req) {
		JSONArray uris = new JSONArray();
		for (Plugin plugin : subject.getServlets()) {
			// If the plugin supports export functionality
			if (plugin.isExport()) {
				JSONObject jsonPlugin = new JSONObject();
				try {
					jsonPlugin.put("href", this.getServerPath(req) + model.getUri() + plugin.getRel());
					jsonPlugin.put("title", plugin.getTitle());
					uris.put(jsonPlugin);
				} catch (JSONException e) {e.printStackTrace();}
			}
		}
		JSONObject info = this.getModelInfo(model, this.getServerPath(req));
		
		JSONArray accessRights = new JSONArray();
		for (Access right : model.getAccess()) {
			try {
				String[] keys = {"Subject", "Predicate"};
				JSONObject item = this.toJSON(right, keys);
				item.put("uri", this.getServerPath(req) + right.getUri());
				accessRights.put(item);
			} catch (JSONException e) {e.printStackTrace();}
		}
		try {
			JSONObject access = new JSONObject();
			access.put("access_rights", accessRights);
			access.put("edit_uri", this.getServerPath(req) + model.getUri() + "/access");
			JSONObject output = new JSONObject();
			output.put("uris", uris);
			output.put("info", info);
			output.put("access", access);
			return output;
		} catch (JSONException e) {e.printStackTrace(); return null;}
		
	}
	
	// Read the model properties including URIs from the database and returns them as JSONObject
	protected JSONObject getModelInfo(Identity model, String hostname) {
		Representation representation = model.read();
		String[] keys =  {"Title", "Summary", "Updated", "Created", "Type"};
		JSONObject output = this.toJSON(representation, keys);
	    try {
			output.put("edit_uri", hostname + model.getUri() + "/info");
			output.put("self_uri", hostname + model.getUri() + "/self");
		    output.put("meta_uri", hostname + model.getUri() + "/info-access");
		    output.put("icon_url",  "/oryx/stencilsets/bpmn/bpmn.png");
		    return output;
		    // TODO: load JSON file
		    //output['icon_url'] = @@model_types[representation.getType][:icon_url]
		} catch (JSONException e) {
			return null;
		}
	}
	
	// Returns the complete server path including the application e.g. 'http://localhost:8080/backend'
	protected String getServerPath(HttpServletRequest req) {
		return "http://" + req.getServerName() + ":" + String.valueOf(req.getServerPort()) + "/backend" + req.getServletPath();
	}
	
	// This method is called by the dispatcher before the first request is passed 
	public void init() {};
	
	// This method is called before the handler is unloaded
	public void destroy() {};
	
    public void doGet(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object) throws Exception {
  		res.setStatus(403);
	   	PrintWriter out = res.getWriter();
	   	out.write("Forbidden!");
	}
	
    public void doPost(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object) throws Exception {
  		res.setStatus(403);
	   	PrintWriter out = res.getWriter();
	   	out.write("Forbidden!");
	}
    public void doPut(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object) throws Exception {
  		res.setStatus(403);
	   	PrintWriter out = res.getWriter();
	   	out.write("Forbidden!");
	}
    public void doDelete(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object) throws Exception {
  		res.setStatus(403);
	   	PrintWriter out = res.getWriter();
	   	out.write("Forbidden!");
	}
    
    // Checks whether the given file exists
    protected boolean fileExists(String path) {
    	String realPath = this.getServletContext().getRealPath(path);
    	File file = new File(realPath);
    	return file.exists();
    }
    
    // Returns the absolute path to the root directory of the oryx webapp
    protected String getOryxRootDirectory() {
    	String realPath = this.getServletContext().getRealPath("");
    	File backendDir = new File(realPath);
    	return backendDir.getParent();
    }
    
    // Returns the absolute path to the root directory of the backend webapp
    protected String getBackendRootDirectory() {
    	return this.getServletContext().getRealPath("");
    }
    
    // Convenience method to get the language data from the session
    protected String getLanguageCode(HttpServletRequest req) {
    	return (String) req.getSession().getAttribute("languagecode");
    }
    
    // Convenience method to get the language data from the session
    protected String getCountryCode(HttpServletRequest req) {
    	return (String) req.getSession().getAttribute("countrycode");
    }
    
    // Removes spaces in the beginning and in the end of the string
    protected static String removeSpaces(String str) {
    	
    	if (str.length() < 1) return str;
    	int start = 0;
    	int end= str.length() - 1;
    	for (; str.charAt(start) == ' ' && start < str.length(); start++); // Count spaces in the beginning
    	for (; str.charAt(end) == ' ' && end >= 0; end--); // Count spaces in the end
    	return str.substring(start, end+1);
    }
    
    protected Map<String, String> getLanguageFiles(String absoluteLanguageDir) {
    	Map<String, String> files = new HashMap<String, String>();
    	File dir = new File(absoluteLanguageDir);
    	for (String fileName : dir.list()) {
			Pattern pattern = Pattern.compile("translation_([a-z]{2,2})(_([a-z]{2,2}))?\\.js");
			Matcher matcher = pattern.matcher(new StringBuffer(fileName));
			matcher.find();			
			if (matcher.group(3) == null) {
				files.put(matcher.group(1), fileName);
			} else {
				files.put(matcher.group(1) + "_" +  matcher.group(3), fileName);
    		}
    	}
    	return files;
    }
    
    
    protected String getOryxModel(String title, String content, 
    		String languageCode, String countryCode) {
    	
    	String oryx_path = "/oryx/";
    	String languageFiles = "";
    	
    	if (new File(this.getOryxRootDirectory() + "/i18n/translation_"+languageCode+".js").exists()) {
    		languageFiles += "<script src=\"" + oryx_path 
    		+ "i18n/translation_"+languageCode+".js\" type=\"text/javascript\" />\n";
    	}
    	
    	if (new File(this.getOryxRootDirectory() + "/i18n/translation_" + languageCode+"_" + countryCode + ".js").exists()) {
    		languageFiles += "<script src=\"" + oryx_path 
    		+ "i18n/translation_" + languageCode+"_" + countryCode 
    		+ ".js\" type=\"text/javascript\" />\n";
    	}
    	
      	return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
      	  	+ "<html xmlns=\"http://www.w3.org/1999/xhtml\"\n"
      	  	+ "xmlns:b3mn=\"http://b3mn.org/2007/b3mn\"\n"
      	  	+ "xmlns:ext=\"http://b3mn.org/2007/ext\"\n"
      	  	+ "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
      	  	+ "xmlns:atom=\"http://b3mn.org/2007/atom+xhtml\">\n"
      	  	+ "<head profile=\"http://purl.org/NET/erdf/profile\">\n"
      	  	+ "<title>" + title + " - Oryx</title>\n"
      	  	+ "<!-- libraries -->\n"
      	  	+ "<script src=\"" + oryx_path + "lib/prototype-1.5.1.js\" type=\"text/javascript\" />\n"
      	  	+ "<script src=\"" + oryx_path + "lib/path_parser.js\" type=\"text/javascript\" />\n"
      	  	+ "<script src=\"" + oryx_path + "lib/ext-2.0.2/adapter/ext/ext-base.js\" type=\"text/javascript\" />\n"
      	  	+ "<script src=\"" + oryx_path + "lib/ext-2.0.2/ext-all.js\" type=\"text/javascript\" />\n"
      	  	+ "<script src=\"" + oryx_path + "lib/ext-2.0.2/color-field.js\" type=\"text/javascript\" />\n"
      	  	+ "<style media=\"screen\" type=\"text/css\">\n"
      	  	+ "@import url(\"" + oryx_path + "lib/ext-2.0.2/resources/css/ext-all.css\");\n"
      	  	+ "@import url(\"" + oryx_path + "lib/ext-2.0.2/resources/css/xtheme-gray.css\");\n"
      	  	+ "</style>\n"

      	  	+ "<!-- oryx editor -->\n"
      	  	+ "<script src=\"" + oryx_path + "oryx.js\" type=\"text/javascript\" />\n"
      	  	// EN_US is default an base language
      	  	+ "<!-- language files -->\n"
      	  	+ "<script src=\"" + oryx_path + "i18n/translation_en_us.js\" type=\"text/javascript\" />\n"      	  	
      	  	+ languageFiles
      	  	+ "<link rel=\"Stylesheet\" media=\"screen\" href=\"" + oryx_path + "css/theme_norm.css\" type=\"text/css\" />\n"

      	  	+ "<!-- erdf schemas -->\n"
      	  	+ "<link rel=\"schema.dc\" href=\"http://purl.org/dc/elements/1.1/\" />\n"
      	  	+ "<link rel=\"schema.dcTerms\" href=\"http://purl.org/dc/terms/\" />\n"
      	  	+ "<link rel=\"schema.b3mn\" href=\"http://b3mn.org\" />\n"
      	  	+ "<link rel=\"schema.oryx\" href=\"http://oryx-editor.org/\" />\n"
      	  	+ "<link rel=\"schema.raziel\" href=\"http://raziel.org/\" />\n"
      	  	+ "</head>\n"
      	  	
      	  	+ "<body style=\"overflow:hidden;\"><div class='processdata' style='display:none'>\n"
      	  	+ content
      	  	+ "\n"
      	  	+ "</div>\n"
      	  	+ "</body>\n"
      	  	+ "</html>";
    }
}
