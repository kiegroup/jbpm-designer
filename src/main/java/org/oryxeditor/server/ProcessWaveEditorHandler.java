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
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProcessWaveEditorHandler extends EditorHandler {

	/**
	 * 
	 */
	private static final String oryx_path = "/oryx/";
	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*
		 * stencilsets/bpmn1.1/bpmn1.1.json
		 */
		String sset=request.getParameter("stencilset");
		String json = request.getParameter("json");
		String extString=request.getParameter("exts");
		String content = 
	        "<script type='text/javascript'>" +
	        "if(!ORYX) var ORYX = {};" +
	        "if(!ORYX.CONFIG) ORYX.CONFIG = {};\n" +
	        "ORYX.CONFIG.SSET='" + sset +"';\n" +
	        "ORYX.CONFIG.SSEXTS=" + extString + ";\n"+
        	"window.onOryxResourcesLoaded = function() {\n" +
        	"var json=" +json+";\n" +
        	"if(json.stencilset){\n" +
        	"json.stencilset.url='" +
        	sset+
        	"';\n" +
        	"}"+
            "new ORYX.Editor(json);\n"+
	      	  "}" +
          	"</script>";
		response.setContentType("application/xhtml+xml");
		
		response.getWriter().println(this.getOryxModel("Oryx-Editor", 
				content, this.getLanguageCode(request), 
				this.getCountryCode(request)));
		response.setStatus(200);
		
	}
	protected String getOryxModel(String title, String content, 
    		String languageCode, String countryCode) {
    	
    	return getOryxModel(title, content, languageCode, countryCode, "");
    }
    
    protected String getOryxModel(String title, String content, 
    		String languageCode, String countryCode, String headExtentions) {
    	
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
      	  	profileFiles=profileFiles+ "<script src=\"" + oryx_path+"oryx.js\" type=\"text/javascript\" />\n";

    	
    	String analytics = getServletContext().getInitParameter("ANALYTICS_SNIPPET");
    	if (null == analytics) {
    		analytics = "";
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
      	  	// Handle different profiles
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
      	  	
      	    + content
      	  	
      	  	+ "</head>\n"
      	  	
      	  	+ "<body style=\"overflow:hidden;\"><div class='processdata' style='display:none'>\n"
      	  	
      	  	+ "\n"
      	  	+ "</div>\n"
      	  	
      	  	+ analytics

      	  	+ "</body>\n"
      	  	+ "</html>";
    }
}
