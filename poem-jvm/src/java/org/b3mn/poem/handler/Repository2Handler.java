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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.util.HandlerWithoutModelContext;

@HandlerWithoutModelContext(uri="/repository")
public class Repository2Handler extends  HandlerBase {


	
	// Return the HTML code for the repository
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {

		String[] java_script_includes = {"ext_templates", 
				"core/clazz", 
				"config", 
				"core/extExtention", 
				"core/eventHandler", 
				"core/dataCache", 
				"core/repository", 
				"core/plugin", 
				"core/viewPlugin", 
				"core/contextFreePlugin", 
				"core/contextPlugin",
				};

		String[] stylesheet_links 	= {"openid", "repository", "model_properties"};
		String stylesheet_ie_fix 	= "repository_ie_fixes";
		
		String backend_path = "/backend";
		String ext_path = backend_path + "/ext-2.0.2/";

    	response.setStatus(200);
    	response.setContentType("text/html");
    	PrintWriter out = response.getWriter();

    	String languageFiles = "";
    	
    	String languageCode = this.getLanguageCode(request);
    	String countryCode = this.getCountryCode(request);
    	
    	// Add language file with language code only if it exists  
    	if (new File(this.getBackendRootDirectory() + "/i18n/translation_"+languageCode+".js").exists()) {
    		languageFiles += "<script src=\"" + backend_path 
    		+ "/i18n/translation_"+languageCode+".js\" type=\"text/javascript\" ></script>\n";
    	}
    	// Add language file for country and language code if it exists
    	if (new File(this.getBackendRootDirectory() + "/i18n/translation_" + languageCode+"_" + countryCode + ".js").exists()) {
    		languageFiles += "<script src=\"" + backend_path 
    		+ "/i18n/translation_" + languageCode+"_" + countryCode 
    		+ ".js\" type=\"text/javascript\" ></script>\n";
    	}
    	
    	
    	out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
    	out.println("<html>");
    	out.println("<head>");
    	out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
    	// This prevents no caching of the repository site for IE
    	out.println("<meta http-equiv=\"Pragma\" content=\"no-cache\">");
    	out.println("<meta http-equiv=\"Expires\" content=\"-1\">");
    	
    	out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + ext_path + "resources/css/ext-all.css\">");
    	out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + ext_path + "resources/css/xtheme-gray.css\">");     	  	
    	/*out.println("<script type=\"text/javascript\" src=\"" + backend_path + "/repository2/prototype.js\"></script>");
    	out.println("<script type=\"text/javascript\" src=\"" + ext_path + "adapter/ext/ext-base.js\"></script>");
    	//out.println("<script type=\"text/javascript\" src=\"" + ext_path + "ext-all.js\"></script>");
    	out.println("<script type=\"text/javascript\" src=\"" + ext_path + "ext-all-debug.js\"></script>");
    	for (String include : java_script_includes) {	
    		out.println("<script type=\"text/javascript\" src=\"" + backend_path + "/repository2/" + include + ".js\"></script>");
    	}*/
    	out.println("<script type=\"text/javascript\">if(!Repository) var Repository = {}; Repository.currentUser='" + subject.getUri() + "';</script>");  
    	out.println("<script type=\"text/javascript\" src=\"" + backend_path + "/repository2/repository2.js\"></script>");
    	out.println("<script src=\"" + backend_path + "/i18n/translation_en_us.js\" type=\"text/javascript\" ></script>\n"); 
    	out.println(languageFiles);
    	for (String stylesheet : stylesheet_links) {
    		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + backend_path + "/css/" + stylesheet + ".css\">");
    	}

    	// Added a css-fix for IE
    	out.println("<!--[if IE]>");
    	out.println("<style type=\"text/css\">");
    	out.println("@import url(" + backend_path + "/css/" + stylesheet_ie_fix + ".css);");
    	out.println("</style>");
    	out.println("<![endif]-->");

    	//out.println("<script type=\"text/javascript\">Ext.onReady(function(){new Repository.Core.Repository(\"" + subject.getUri() + "\");});</script>");  
    	out.println("<title>ORYX - Repository 2.0 (BETA)</title>");
    	out.println("</head>");
    	out.println("<body>");
    	
    	String analytics = getServletContext().getInitParameter("ANALYTICS_SNIPPET");
    	if (null != analytics) {
    		out.println(analytics);
    	}
    	
    	out.println("</body>");
    	out.println("</html>");
	}
}
