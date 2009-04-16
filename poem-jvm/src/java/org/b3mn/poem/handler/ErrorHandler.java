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
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.business.Model;
import org.b3mn.poem.util.HandlerWithModelContext;

/* This class is deprecated and should be removed if the old repository isn't used anymore
 * 
 * 
 * */
@HandlerWithModelContext(uri="/error")
public class ErrorHandler extends  HandlerBase {

	
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException, Exception {

		Model model = new Model(object.getId());
		
		String backend_path = "/backend";
		String stylesheet	= "repository";
	

		/**
		 * GET THE COUNTRY CODE
		 */
    	String languageFiles 	= "";    	
    	String languageCode 	= this.getLanguageCode(request);
    	String countryCode 		= this.getCountryCode(request);
    	
    	// Add language file with language code only if it exists  
    	if (new File(this.getBackendRootDirectory() + "/i18n/translation_"+languageCode+".js").exists()) {
    		languageFiles += "<script src=\"" + backend_path 
    		+ "/i18n/translation_"+languageCode+".js\" type=\"text/javascript\" ></script>\n";
    	}
    	// Add language file for country and language code if it exists
    	if (new File(this.getBackendRootDirectory() + "/i18n/translation_" + languageCode+"_" + countryCode + ".js").exists()) {
    		languageFiles += "<script src=\"" + backend_path 
    		+ "i18n/translation_" + languageCode+"_" + countryCode 
    		+ ".js\" type=\"text/javascript\" ></script>\n";
    	}
    	
    	
    	response.setStatus(200);
    	response.setContentType("text/html");
    	PrintWriter out = response.getWriter();

    	/**
    	 * WRITE OUTPUT
    	 */
    	out.println("<html>");
    	out.println("<head>");
    	// Write Title
    	out.println("<title>Oryx</title>");
    	// Write CSS
    	out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + backend_path + "/css/" + stylesheet + ".css\">");
    	// Write Language Files
    	out.println("<script src=\"" + backend_path + "/i18n/translation_en_us.js\" type=\"text/javascript\" ></script>\n"); 
    	out.println(languageFiles); 
    	
    	out.println("</head>");
    	out.println("<body>");

    	out.println("<div id='header'>");
    	out.println("<a target='_blank' href='http://oryx-editor.org'>");
    	out.println("<img id='oryx_repository_logo' title='ORYX' alt='ORYX Logo' src='" + backend_path +"/images/style/oryx_small.png'/>");
    	out.println("</a>");
    	out.println("</div>"); 
    	
    	out.println("<div class='content'>");  
    	out.println("<script>document.write(Repository.I18N.Repository.errorText)</script>");  	
    	out.println("<br/><br/>");
    	out.println("<span class='label'><script>document.write(Repository.I18N.Repository.errorTitle)</script></span><span class='value'>" + model.getTitle() + "</span>");    	
    	out.println("<br/>");
    	out.println("<span class='label'><script>document.write(Repository.I18N.Repository.errorAuthor)</script></span><span class='value'>" + model.getAuthor() + "</span>");
    	out.println("<br/>");
    	
    	out.println("<img class='model' src='./png'>");
    	out.println("</div>");
    	
    	out.println("</body>");
    	out.println("</html>");
	}
}
