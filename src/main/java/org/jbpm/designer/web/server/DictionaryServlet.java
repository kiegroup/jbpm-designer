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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;

import sun.misc.BASE64Encoder;

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
        
        IDiagramProfile profile = getProfile(req, profileName);
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
	        applyAuth(profile, checkConnection);
	        checkConnection.setRequestMethod("GET");
	        checkConnection
	                .setRequestProperty("Accept", "application/atom+xml");
	        checkConnection.connect();
	        _logger.info("check connection response code: " + checkConnection.getResponseCode());
	        if (checkConnection.getResponseCode() == 200) {
	        	InputStream in = getInputStreamForURL(dictionarySourceURL, "GET", profile);
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
	        applyAuth(profile, checkConnection);
	        checkConnection.setRequestMethod("GET");
	        checkConnection
	                .setRequestProperty("Accept", "application/atom+xml");
	        checkConnection.connect();
	        _logger.info("check connection response code: " + checkConnection.getResponseCode());
	        if (checkConnection.getResponseCode() == 200) {
	            URL deleteAssetURL = new URL(dictionaryDeleteURL);
	            HttpURLConnection deleteConnection = (HttpURLConnection) deleteAssetURL
	                    .openConnection();
	            applyAuth(profile, deleteConnection);
	            deleteConnection.setRequestMethod("DELETE");
	            deleteConnection.connect();
	            _logger.info("delete connection response code: " + deleteConnection.getResponseCode());
	        }
	        
	        URL createURL = new URL(dictionaryAssetsURL);
            HttpURLConnection createConnection = (HttpURLConnection) createURL
                    .openConnection();
            applyAuth(profile, createConnection);
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
	
	private IDiagramProfile getProfile(HttpServletRequest req,
            String profileName) {
        IDiagramProfile profile = null;

        IDiagramProfileService service = new ProfileServiceImpl();
        service.init(getServletContext());
        profile = service.findProfile(req, profileName);
        if (profile == null) {
            throw new IllegalArgumentException(
                    "Cannot determine the profile to use for interpreting models");
        }
        return profile;
    }
	
	private void applyAuth(IDiagramProfile profile, HttpURLConnection connection) {
        if (profile.getUsr() != null && profile.getUsr().trim().length() > 0
                && profile.getPwd() != null
                && profile.getPwd().trim().length() > 0) {
            BASE64Encoder enc = new sun.misc.BASE64Encoder();
            String userpassword = profile.getUsr() + ":" + profile.getPwd();
            String encodedAuthorization = enc.encode(userpassword.getBytes());
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedAuthorization);
        }
    }
	
	 private InputStream getInputStreamForURL(String urlLocation,
	            String requestMethod, IDiagramProfile profile) throws Exception {
	        URL url = new URL(urlLocation);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	        connection.setRequestMethod(requestMethod);
	        connection
	                .setRequestProperty(
	                        "User-Agent",
	                        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.16) Gecko/20110319 Firefox/3.6.16");
	        connection
	                .setRequestProperty("Accept",
	                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	        connection.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
	        connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
	        connection.setRequestProperty("charset", "UTF-8");
	        connection.setReadTimeout(5 * 1000);

	        applyAuth(profile, connection);

	        connection.connect();

	        BufferedReader sreader = new BufferedReader(new InputStreamReader(
	                connection.getInputStream(), "UTF-8"));
	        StringBuilder stringBuilder = new StringBuilder();

	        String line = null;
	        while ((line = sreader.readLine()) != null) {
	            stringBuilder.append(line + "\n");
	        }

	        return new ByteArrayInputStream(stringBuilder.toString().getBytes(
	                "UTF-8"));
	    }
}
