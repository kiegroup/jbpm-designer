/***************************************
 * Copyright (c) 2008
 * Philipp Berger 2009
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

package org.oryxeditor.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;

public class EditorHandler extends HttpServlet {

	/**
	 * 
	 */
	private static final String oryx_path = "/oryx/";
	private static final String defaultSS="stencilsets/bpmn1.1/bpmn1.1.json";
	private static final long serialVersionUID = 1L;
	private Collection<String> availableProfiles;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		availableProfiles=getAvailableProfileNames();
		String[] urlSplitted=request.getRequestURI().split(";");
		ArrayList<String> profiles= new ArrayList<String>();
		if (urlSplitted.length>1){
			for(int i=1;i<urlSplitted.length;i++){
				profiles.add(urlSplitted[i]);
			}
		}else{
			profiles.add("default");
		}
		if(!availableProfiles.containsAll(profiles)){
			//Some profiles not available
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Profile not found!");
			profiles.retainAll(availableProfiles);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		String sset=null;
		try {
			sset=getNamedConf("stencilset", profiles.get(0));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(sset==null)
			sset=defaultSS;
		String content = 
	        "<script type='text/javascript'>" +
	        "if(!ORYX) var ORYX = {};" +
	        "if(!ORYX.CONFIG) ORYX.CONFIG = {};" +
	        "ORYX.CONFIG.PLUGINS_CONFIG  =			ORYX.CONFIG.PROFILE_PATH + '"+profiles.get(0)+".xml';" +
//	        "function getStencilSetFromHash(){" +
//	        "var para=location.hash.split('=')[1];" +
//	        "return para};" +

	        "function onOryxResourcesLoaded(){" +
                "if (location.hash.slice(1).length == 0 || location.hash.slice(1).indexOf('new')!=-1){" +
                "var stencilset=ORYX.Utils.getParamFromUrl('stencilset')?ORYX.Utils.getParamFromUrl('stencilset'):'"+sset+"';"+
                "new ORYX.Editor({"+
                  "id: 'oryx-canvas123',"+
                  "stencilset: {"+
                  	"url: '"+oryx_path+"'+stencilset" +
                  "}" +
          		"})}"+
                "else{" +
                "ORYX.Editor.createByUrl('" + getRelativeServerPath(request) + "'+location.hash.slice(1)+'/json', {"+
                  "id: 'oryx-canvas123'" +
          		"});" +
          	  "};" +
          	  "}" +
          	"</script>";
		response.setContentType("application/xhtml+xml");
		
		response.getWriter().println(this.getOryxModel("Oryx-Editor", 
				content, this.getLanguageCode(request), 
				this.getCountryCode(request), profiles));
		response.setStatus(200);
	}
	protected String getOryxModel(String title, String content, 
    		String languageCode, String countryCode, ArrayList<String> profiles) {
    	
    	return getOryxModel(title, content, languageCode, countryCode, "", profiles);
    }
    
    protected String getOryxModel(String title, String content, 
    		String languageCode, String countryCode, String headExtentions, ArrayList<String> profiles) {
    	
    	String languageFiles = "";
    	String profileFiles="";
    	
    	if (new File(this.getOryxRootDirectory() + "/oryx/i18n/translation_"+languageCode+".js").exists()) {
    		languageFiles += "<script src=\"" + oryx_path 
    		+ "i18n/translation_"+languageCode+".js\" type=\"text/javascript\" />\n";
    	}
    	
    	if (new File(this.getOryxRootDirectory() + "/oryx/i18n/translation_" + languageCode+"_" + countryCode + ".js").exists()) {
    		languageFiles += "<script src=\"" + oryx_path 
    		+ "i18n/translation_" + languageCode+"_" + countryCode 
    		+ ".js\" type=\"text/javascript\" />\n";
    	}
    	for(String profile: profiles){
      	  	profileFiles=profileFiles+ "<script src=\"" + oryx_path+"profiles/" + profile+".js\" type=\"text/javascript\" />\n";

    	}
      	return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
      	    + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
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
      	  	// EN_US is default an base language
      	  	+ "<!-- language files -->\n"
      	  	+ "<script src=\"" + oryx_path + "i18n/translation_en_us.js\" type=\"text/javascript\" />\n"      	  	
      	  	+ languageFiles
//TODO Handle different profiles
      	  	+ "<script src=\"" + oryx_path + "profiles/oryx.core.js\" type=\"text/javascript\" />\n"
      	  	+ profileFiles
      	  	+ headExtentions
      	  	
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
    protected String getOryxRootDirectory() {
    	String realPath = this.getServletContext().getRealPath("");
    	File backendDir = new File(realPath);
    	return backendDir.getParent();
    }
    protected String getCountryCode(HttpServletRequest req) {
    	return (String) req.getSession().getAttribute("countrycode");
    }
    protected String getLanguageCode(HttpServletRequest req) {
    	return (String) req.getSession().getAttribute("languagecode");
    }
	protected String getRelativeServerPath(HttpServletRequest req){
		return "/backend/poem"; //+ req.getServletPath();
	}
	public Collection<String> getAvailableProfileNames() {
		File handlerDir = new File(this.getServletContext().
				getRealPath("/profiles"));
		Collection<String> profilNames = new ArrayList<String>();
		
		for (File source : handlerDir.listFiles()) {
			if (source.getName().endsWith(".js")) {
				profilNames.add(source.getName().substring(0, source.getName().lastIndexOf(".")));
			}
		}
		return profilNames;
	}
	public String getNamedConf(String name, String profilename) throws FileNotFoundException, IOException {
		String conf=FileCopyUtils.copyToString(new FileReader(this.getServletContext().
				getRealPath("/profiles") + File.separator + profilename
				+ ".conf"));
		String[] confs=conf.split("##");
		for(String attr:confs){
			String[] pair=attr.split("::");
			if(pair[0].equals(name))
				return pair[1];
		}
		return null;
	}
}
