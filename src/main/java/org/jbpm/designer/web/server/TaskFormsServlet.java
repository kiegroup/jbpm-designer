package org.jbpm.designer.web.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.log4j.Logger;
import org.eclipse.bpmn2.Definitions;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonUnmarshaller;
import org.jbpm.designer.taskforms.TaskFormInfo;
import org.jbpm.designer.taskforms.TaskFormTemplateManager;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.jbpm.designer.web.server.GuvnorUtil.UrlType;

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
        req.setCharacterEncoding("UTF-8");
        String json = req.getParameter("json");
        String uuid = req.getParameter("uuid");
        String profileName = req.getParameter("profile");
        String preprocessingData = req.getParameter("ppdata");
        
        IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
        
        // find out what package the uuid belongs to
        String[] packageAssetInfo = ServletUtil.findPackageAndAssetInfo(uuid, profile);
        String packageName = packageAssetInfo[0];
        String assetName = packageAssetInfo[1];
        
        DroolsFactoryImpl.init();
        Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
        Definitions def = ((Definitions) unmarshaller.unmarshall(json, preprocessingData).getContents().get(0));
        
        TaskFormTemplateManager templateManager = new TaskFormTemplateManager(packageName, assetName, getServletContext().getRealPath("/" + TASKFORMS_PATH), def );
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
            outstr.write(resultsForm.toString().getBytes("UTF-8"));
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
        // GUVNOR TaskFormsServlet
        try {
			// check if the task form already exists
			String formURL = GuvnorUtil.getUrl(profile, taskForm.getPkgName(), taskForm.getId(), UrlType.Normal);
			if( GuvnorUtil.readCheckAssetExists(formURL, profile) ) { 
			    // delete the asset
			    GuvnorUtil.deleteAsset(formURL, profile);
			}
			
			// create new 
			String createNewURL = GuvnorUtil.getUrl(profile, taskForm.getPkgName(), "", UrlType.Normal);
			GuvnorUtil.createAsset(createNewURL, taskForm.getId(), FORMTEMPLATE_FILE_EXTENSION, taskForm.getOutput().getBytes("UTF-8"), profile);
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
    }
}
