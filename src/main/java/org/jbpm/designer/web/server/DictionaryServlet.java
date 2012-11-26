/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.designer.web.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.GuvnorUtil.UrlType;

/** 
 * Dictionary Servlet.
 * @author Tihomir Surdilovic
 */
public class DictionaryServlet extends HttpServlet {
    
	private static final long serialVersionUID = 1L;
	
	private static final String ACTION_LOAD = "load";
	private static final String ACTION_SAVE = "save";
	private static final String DICTIONARY_FNAME = "processdictionary";
	private static final String DICTIONARY_FEXT = ".json";
	private static final Logger _logger = Logger.getLogger(DictionaryServlet.class);


	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        String profileName = req.getParameter("profile");
        String dvalue = req.getParameter("dvalue");
        
        IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
        if(action != null && action.equals(ACTION_SAVE)) {
        	storeToGuvnor(profile, dvalue);
        	PrintWriter pw = resp.getWriter();
    		resp.setContentType("text/plain");
    		resp.setCharacterEncoding("UTF-8");
    		pw.write("saved");
        } else if(action != null && action.equals(ACTION_LOAD)) {
        	PrintWriter pw = resp.getWriter();
    		resp.setContentType("text/json");
    		resp.setCharacterEncoding("UTF-8");
    		pw.write(getFromGuvnor(profile));
        }
	}
	
	private String getFromGuvnor(IDiagramProfile profile) {
        // GUVNOR DictionaryServlet
		try {
		    String dictionaryURL = GuvnorUtil.getUrl(profile, "globalArea", DICTIONARY_FNAME, UrlType.Normal);
	        if (GuvnorUtil.readCheckAssetExists(dictionaryURL, 3000, profile) ) { 
	            String dictionarySourceURL = GuvnorUtil.getUrl(profile, "globalArea", DICTIONARY_FNAME, UrlType.Source);
	            return GuvnorUtil.readStringContentFromUrl(dictionarySourceURL, "GET", profile);
	        }
		} catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        } 
		return "false";
	}
	
	private void storeToGuvnor(IDiagramProfile profile, String dvalue) {

		try {
		    String dictionaryURL = GuvnorUtil.getUrl(profile, "globalArea", DICTIONARY_FNAME, UrlType.Normal);
		    
			// check if the dictionary already exists
	        if(GuvnorUtil.readCheckAssetExists(dictionaryURL, profile)) { 
	            GuvnorUtil.deleteAsset(dictionaryURL, profile);
	        }
	        
	        String dictionaryAssetsURL = GuvnorUtil.getUrl(profile, "globalArea", "", UrlType.Normal);
	        GuvnorUtil.createAsset(dictionaryAssetsURL, DICTIONARY_FNAME, DICTIONARY_FEXT, dvalue.getBytes("UTF-8"), profile);
		} catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        }
	}
	 
}
