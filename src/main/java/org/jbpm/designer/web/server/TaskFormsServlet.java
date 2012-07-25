package org.jbpm.designer.web.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.log4j.Logger;
import org.eclipse.bpmn2.Definitions;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonUnmarshaller;
import org.jbpm.designer.taskforms.TaskFormInfo;
import org.jbpm.designer.taskforms.TaskFormTemplateManager;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;

import org.apache.commons.codec.binary.Base64;

/** 
 * 
 * Creates/updates task forms for a specific process.
 * 
 * @author Tihomir Surdilovic
 */
public class TaskFormsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger _logger = Logger
            .getLogger(TaskFormsServlet.class);
    private static final String TASKFORMS_PATH = "taskforms";
    private static final String FORMTEMPLATE_FILE_EXTENSION = ".flt";
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String json = req.getParameter("json");
        String uuid = req.getParameter("uuid");
        String profileName = req.getParameter("profile");
        String preprocessingData = req.getParameter("ppdata");
        
        IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
        
        // find out what package the uuid belongs to
        String[] packageAssetInfo = ServletUtil.findPackageAndAssetInfo(uuid, profile);
        String packageName = packageAssetInfo[0];
        String assetName = packageAssetInfo[1];

        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions def = ((Definitions) unmarshaller.unmarshall(json, preprocessingData).getContents().get(0));
        
        TaskFormTemplateManager templateManager = new TaskFormTemplateManager( profile, packageName, assetName, getServletContext().getRealPath("/" + TASKFORMS_PATH), def );
        templateManager.processTemplates();
        
        try {
            storeToGuvnor( templateManager, profile );
            displayResponse( templateManager, resp, profile );
        } catch (Exception e) {
            _logger.error(e.getMessage());
            displayErrorResponse(resp, e.getMessage());
        }
    }
    
    public void displayResponse(TaskFormTemplateManager templateManager, HttpServletResponse resp, IDiagramProfile profile) {
        try {
            StringTemplateGroup templates = new StringTemplateGroup("resultsgroup", templateManager.getTemplatesPath());
            StringTemplate resultsForm = templates.getInstanceOf("resultsform");
            resultsForm.setAttribute("manager", templateManager);
            resultsForm.setAttribute("profile", ExternalInfo.getExternalProtocol(profile));
            resultsForm.setAttribute("host", ExternalInfo.getExternalHost(profile));
            resultsForm.setAttribute("subdomain", profile.getExternalLoadURLSubdomain().substring(0,
                profile.getExternalLoadURLSubdomain().indexOf("/")));
            ServletOutputStream outstr = resp.getOutputStream();
            resp.setContentType("text/html");
            outstr.write(resultsForm.toString().getBytes("ASCII"));
            outstr.flush();
            outstr.close();
        } catch (IOException e) {
           _logger.error(e.getMessage());
        }
    }
    
    public void displayErrorResponse(HttpServletResponse resp, String exceptionStr) {
        try {
            ServletOutputStream outstr = resp.getOutputStream();
            resp.setContentType("text/html");
            outstr.write(exceptionStr.getBytes("ASCII"));
            outstr.flush();
            outstr.close();
        } catch (IOException e) {
           _logger.error(e.getMessage());
        }
    }
    
    public void storeToGuvnor(TaskFormTemplateManager templateManager, IDiagramProfile profile) throws Exception {
        List<TaskFormInfo> taskForms =  templateManager.getTaskFormInformationList();
        for(TaskFormInfo taskForm : taskForms) {
            storeTaskForm(taskForm, profile);
        }
    }
    
    public void storeTaskForm(TaskFormInfo taskForm, IDiagramProfile profile) throws Exception {
        String formURL = ExternalInfo.getExternalProtocol(profile)
        + "://"
        + ExternalInfo.getExternalHost(profile)
        + "/"
        + profile.getExternalLoadURLSubdomain().substring(0,
                profile.getExternalLoadURLSubdomain().indexOf("/"))
        + "/rest/packages/" + taskForm.getPkgName() + "/assets/" + URLEncoder.encode(taskForm.getId(), "UTF-8");
        
        String createNewURL = ExternalInfo.getExternalProtocol(profile)
        + "://"
        + ExternalInfo.getExternalHost(profile)
        + "/"
        + profile.getExternalLoadURLSubdomain().substring(0,
                profile.getExternalLoadURLSubdomain().indexOf("/"))
        + "/rest/packages/" + taskForm.getPkgName() + "/assets/";
        
        
        // check if the task form already exists
        URL checkURL = new URL(formURL);
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
            URL deleteAssetURL = new URL(formURL);
            HttpURLConnection deleteConnection = (HttpURLConnection) deleteAssetURL
                    .openConnection();
            ServletUtil.applyAuth(profile, deleteConnection);
            deleteConnection.setRequestMethod("DELETE");
            deleteConnection.connect();
            _logger.info("delete connection response code: " + deleteConnection.getResponseCode());
        }
        // create new 
        URL createURL = new URL(createNewURL);
        HttpURLConnection createConnection = (HttpURLConnection) createURL
                .openConnection();
        ServletUtil.applyAuth(profile, createConnection);
        createConnection.setRequestMethod("POST");
        createConnection.setRequestProperty("Content-Type",
                "application/octet-stream");
        createConnection.setRequestProperty("Accept",
                "application/atom+xml");
        createConnection.setRequestProperty("Slug", URLEncoder.encode(taskForm.getId(), "UTF-8") + FORMTEMPLATE_FILE_EXTENSION);
        createConnection.setDoOutput(true);
        
        createConnection.getOutputStream ().write(taskForm.getOutput().getBytes("UTF-8"));
        
        createConnection.connect();
        _logger.info("create connection response code: " + createConnection.getResponseCode());
    }
}
