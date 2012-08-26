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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.i18n.text.Sanitizer;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;

import org.apache.commons.codec.binary.Base64;

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
	private ServletConfig config;

	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        String uuid = req.getParameter("uuid");
        String profileName = req.getParameter("profile");
        String dvalue = req.getParameter("dvalue");
        
        IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
        if(action != null && action.equals(ACTION_SAVE)) {
        	storeToGuvnor(uuid, profile, dvalue);
        	PrintWriter pw = resp.getWriter();
    		resp.setContentType("text/plain");
    		resp.setCharacterEncoding("UTF-8");
    		pw.write("saved");
        } else if(action != null && action.equals(ACTION_LOAD)) {
        	PrintWriter pw = resp.getWriter();
    		resp.setContentType("text/json");
    		resp.setCharacterEncoding("UTF-8");
    		pw.write(getFromGuvnor(uuid, profile));
        }
	}
	
	private String getFromGuvnor(String uuid, IDiagramProfile profile) {
		String dictionaryURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + DICTIONARY_FNAME;
		
		String dictionarySourceURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + DICTIONARY_FNAME
                + "/source/";
		try {
			URL checkURL = new URL(dictionaryURL);
	        HttpURLConnection checkConnection = (HttpURLConnection) checkURL
	                .openConnection();
	        ServletUtil.applyAuth(profile, checkConnection);
	        checkConnection.setRequestMethod("GET");
	        checkConnection
	                .setRequestProperty("Accept", "application/atom+xml");
	        checkConnection.setConnectTimeout(3000);
	        checkConnection.connect();
	        _logger.info("check connection response code: " + checkConnection.getResponseCode());
	        if (checkConnection.getResponseCode() == 200) {
	        	InputStream in = ServletUtil.getInputStreamForURL(dictionarySourceURL, "GET", profile);
	            StringWriter writer = new StringWriter();
	            IOUtils.copy(in, writer);
	            return writer.toString();
	        }
		} catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        } 
		return "false";
	}
	
	private void storeToGuvnor(String uuid, IDiagramProfile profile, String dvalue) {
		String dictionaryURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + DICTIONARY_FNAME;
		
		String dictionaryDeleteURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + DICTIONARY_FNAME;
		
		String dictionaryAssetsURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/";
		try {
			// check if the dictionary already exists
	        URL checkURL = new URL(dictionaryURL);
	        HttpURLConnection checkConnection = (HttpURLConnection) checkURL
	                .openConnection();
	        ServletUtil.applyAuth(profile, checkConnection);
	        checkConnection.setRequestMethod("GET");
	        checkConnection
	                .setRequestProperty("Accept", "application/atom+xml");
	        checkConnection.connect();
	        _logger.info("check connection response code: " + checkConnection.getResponseCode());
	        if (checkConnection.getResponseCode() == 200) {
	            URL deleteAssetURL = new URL(dictionaryDeleteURL);
	            HttpURLConnection deleteConnection = (HttpURLConnection) deleteAssetURL
	                    .openConnection();
	            ServletUtil.applyAuth(profile, deleteConnection);
	            deleteConnection.setRequestMethod("DELETE");
	            deleteConnection.connect();
	            _logger.info("delete connection response code: " + deleteConnection.getResponseCode());
	        }
	        
	        URL createURL = new URL(dictionaryAssetsURL);
            HttpURLConnection createConnection = (HttpURLConnection) createURL
                    .openConnection();
            ServletUtil.applyAuth(profile, createConnection);
            createConnection.setRequestMethod("POST");
            createConnection.setRequestProperty("Content-Type",
                    "application/octet-stream");
            createConnection.setRequestProperty("Accept",
                    "application/atom+xml");
            createConnection.setRequestProperty("Slug", DICTIONARY_FNAME + DICTIONARY_FEXT);
            createConnection.setDoOutput(true);
            createConnection.getOutputStream().write(dvalue.getBytes("UTF-8"));
            createConnection.connect();
            _logger.info("create connection response code: " + createConnection.getResponseCode());
		} catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        }
	}
	 
}
