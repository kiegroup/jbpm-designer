package org.jbpm.designer.web.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.ServletUtil.UrlType;

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

        if (action != null && action.equals(ACTION_LOAD)) {
            resp.setContentType("text/html");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter pw = resp.getWriter();
            pw.write(getTaskFormFromGuvnor(taskName, packageName, profile));
        } else if (action != null && action.equals(ACTION_SAVE)) {
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

    private String storeTaskFormToGuvnor(String taskName, String packageName, IDiagramProfile profile, String formValue)
            throws Exception {
        // GUVNOR TaskFormsEditorServlet
        String taskFormURL = ServletUtil.getUrl(profile, packageName, taskName + TASKFORM_NAME_EXTENSION, UrlType.Normal);
        String createNewURL = ServletUtil.getUrl(profile, packageName, "", UrlType.Normal);

        // Check if task form asset exists
        URL checkURL = new URL(taskFormURL);
        HttpURLConnection checkConnection = (HttpURLConnection) checkURL.openConnection();
        ServletUtil.applyAuth(profile, checkConnection);
        checkConnection.setRequestMethod("GET");
        checkConnection.setRequestProperty("Accept", "application/atom+xml");
        checkConnection.connect();
        _logger.info("check connection response code: " + checkConnection.getResponseCode());
        if (checkConnection.getResponseCode() == 200) {
            // delete the asset
            URL deleteAssetURL = new URL(taskFormURL);
            HttpURLConnection deleteConnection = (HttpURLConnection) deleteAssetURL.openConnection();
            ServletUtil.applyAuth(profile, deleteConnection);
            deleteConnection.setRequestMethod("DELETE");
            deleteConnection.connect();
            _logger.info("delete connection response code: " + deleteConnection.getResponseCode());
        }

        // Create the asset
        URL createURL = new URL(createNewURL);
        HttpURLConnection createConnection = (HttpURLConnection) createURL.openConnection();
        ServletUtil.applyAuth(profile, createConnection);
        createConnection.setRequestMethod("POST");
        createConnection.setRequestProperty("Content-Type", "application/octet-stream");
        createConnection.setRequestProperty("Accept", "application/atom+xml");
        createConnection.setRequestProperty("Slug", URLEncoder.encode(taskName, "UTF-8") + TASKFORM_NAME_EXTENSION
                + TASKFORM_FILE_EXTENSION);
        createConnection.setDoOutput(true);

        createConnection.getOutputStream().write(formValue.getBytes("UTF-8"));

        createConnection.connect();
        _logger.info("create connection response code: " + createConnection.getResponseCode());
        return "ok";
    }

    private String getTaskFormFromGuvnor(String taskName, String packageName, IDiagramProfile profile) {
        // GUVNOR TaskFormsEditorServlet
        try {
            String taskFormURL = ServletUtil.getUrl(profile, packageName, taskName + TASKFORM_NAME_EXTENSION, UrlType.Normal);
            String taskFormSourceURL = ServletUtil.getUrl(profile, packageName, taskName + TASKFORM_NAME_EXTENSION, UrlType.Source);

            URL checkURL = new URL(taskFormURL);
            HttpURLConnection checkConnection = (HttpURLConnection) checkURL.openConnection();
            ServletUtil.applyAuth(profile, checkConnection);
            checkConnection.setRequestMethod("GET");
            checkConnection.setRequestProperty("Accept", "application/atom+xml");
            checkConnection.setConnectTimeout(2000);
            checkConnection.connect();
            _logger.info("check connection response code: " + checkConnection.getResponseCode());
            if (checkConnection.getResponseCode() == 200) {
                return ServletUtil.getStringContentFromUrl(taskFormSourceURL, "GET", profile);
            }
        } catch (Exception e) {
            _logger.error(e.getMessage());
        }
        return "false";
    }
}
