package org.jbpm.designer.web.server;

import java.io.IOException;
import java.io.InputStream;
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
import org.jbpm.designer.web.profile.impl.ExternalInfo;

/** 
 * 
 * Interaction with task forms for inline editor.
 * 
 * @author Tihomir Surdilovic
 */
public class TaskFormsEditorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger _logger = Logger.getLogger(TaskFormsEditorServlet.class);
	private static final String TASKFORM_FILE_EXTENSION = ".flt";
	private static final String TASKFORM_NAME_EXTENSION = "-taskform";
	private static final String ACTION_LOAD = "load";
	private static final String ACTION_SAVE = "save";
	
	 @Override
	 public void init(ServletConfig config) throws ServletException {
		 super.init(config);
	 }
	 
	 @Override
	 protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		 String action = req.getParameter("action");
	     String uuid = req.getParameter("uuid");
	     String profileName = req.getParameter("profile");
	     String taskName = req.getParameter("taskname");
	     String taskFormValue = req.getParameter("tfvalue");
	     
	     IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
	     String[] packageAssetInfo = ServletUtil.findPackageAndAssetInfo(uuid, profile);
	     String packageName = packageAssetInfo[0];
	     //String assetName = packageAssetInfo[1];
	     
	     if(action != null && action.equals(ACTION_LOAD)) {
	    	 resp.setContentType("text/html");
	    	 resp.setCharacterEncoding("UTF-8");
	    	 PrintWriter pw = resp.getWriter();
	    	 pw.write(getTaskFormFromGuvnor(taskName, packageName, profile));
	     } else if(action != null && action.equals(ACTION_SAVE)) {
	    	 resp.setContentType("text/plain");
	    	 resp.setCharacterEncoding("UTF-8");
	    	 PrintWriter pw = resp.getWriter();
	    	 try {
				pw.write(storeTaskFormToGuvnor(taskName, packageName, profile, taskFormValue));
			} catch (Exception e) {
				pw.write("error: " + e.getMessage());
			}
	     }
	 }
	 
	 private String storeTaskFormToGuvnor(String taskName, String packageName, IDiagramProfile profile, String formValue) throws Exception{
		 String taskFormURL = ExternalInfo.getExternalProtocol(profile)
	                + "://"
	                + ExternalInfo.getExternalHost(profile)
	                + "/"
	                + profile.getExternalLoadURLSubdomain().substring(0,
	                        profile.getExternalLoadURLSubdomain().indexOf("/"))
	                + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + taskName + TASKFORM_NAME_EXTENSION;

		String createNewURL = ExternalInfo.getExternalProtocol(profile)
				+ "://"
				+ ExternalInfo.getExternalHost(profile)
				+ "/"
				+ profile.getExternalLoadURLSubdomain().substring(0,
						profile.getExternalLoadURLSubdomain().indexOf("/"))
				+ "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/";
		
        URL checkURL = new URL(taskFormURL);
        HttpURLConnection checkConnection = (HttpURLConnection) checkURL
                .openConnection();
        ServletUtil.applyAuth(profile, checkConnection);
        checkConnection.setRequestMethod("GET");
        checkConnection
                .setRequestProperty("Accept", "application/atom+xml");
        checkConnection.connect();
        _logger.info("check connection response code: " + checkConnection.getResponseCode());
        if (checkConnection.getResponseCode() == 200) {
            // delete the asset
            URL deleteAssetURL = new URL(taskFormURL);
            HttpURLConnection deleteConnection = (HttpURLConnection) deleteAssetURL
                    .openConnection();
            ServletUtil.applyAuth(profile, deleteConnection);
            deleteConnection.setRequestMethod("DELETE");
            deleteConnection.connect();
            _logger.info("delete connection response code: " + deleteConnection.getResponseCode());
        }
        URL createURL = new URL(createNewURL);
        HttpURLConnection createConnection = (HttpURLConnection) createURL
                .openConnection();
        ServletUtil.applyAuth(profile, createConnection);
        createConnection.setRequestMethod("POST");
        createConnection.setRequestProperty("Content-Type",
                "application/octet-stream");
        createConnection.setRequestProperty("Accept",
                "application/atom+xml");
        createConnection.setRequestProperty("Slug", taskName + TASKFORM_NAME_EXTENSION + TASKFORM_FILE_EXTENSION);
        createConnection.setDoOutput(true);
        
        createConnection.getOutputStream ().write(formValue.getBytes("UTF-8"));
        
        createConnection.connect();
        _logger.info("create connection response code: " + createConnection.getResponseCode());
		return "ok";
	 }
	 
	 private String getTaskFormFromGuvnor(String taskName, String packageName, IDiagramProfile profile) {
		 try {
			 	String taskFormURL = ExternalInfo.getExternalProtocol(profile)
		                + "://"
		                + ExternalInfo.getExternalHost(profile)
		                + "/"
		                + profile.getExternalLoadURLSubdomain().substring(0,
		                        profile.getExternalLoadURLSubdomain().indexOf("/"))
		                + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + taskName + TASKFORM_NAME_EXTENSION;
				
				String taskFormSourceURL = ExternalInfo.getExternalProtocol(profile)
		                + "://"
		                + ExternalInfo.getExternalHost(profile)
		                + "/"
		                + profile.getExternalLoadURLSubdomain().substring(0,
		                        profile.getExternalLoadURLSubdomain().indexOf("/"))
		                + "/rest/packages/"+ URLEncoder.encode(packageName, "UTF-8") + "/assets/" + taskName + TASKFORM_NAME_EXTENSION
		                + "/source/";
				
			
				URL checkURL = new URL(taskFormURL);
		        HttpURLConnection checkConnection = (HttpURLConnection) checkURL
		                .openConnection();
		        ServletUtil.applyAuth(profile, checkConnection);
		        checkConnection.setRequestMethod("GET");
		        checkConnection
		                .setRequestProperty("Accept", "application/atom+xml");
		        checkConnection.setConnectTimeout(2000);
		        checkConnection.connect();
		        _logger.info("check connection response code: " + checkConnection.getResponseCode());
		        if (checkConnection.getResponseCode() == 200) {
		        	InputStream in = ServletUtil.getInputStreamForURL(taskFormSourceURL, "GET", profile);
		            StringWriter writer = new StringWriter();
		            IOUtils.copy(in, writer);
		            return writer.toString();
		        }
			} catch (Exception e) {
	            _logger.error(e.getMessage());
	        } 
			return "false";
	 }
}
