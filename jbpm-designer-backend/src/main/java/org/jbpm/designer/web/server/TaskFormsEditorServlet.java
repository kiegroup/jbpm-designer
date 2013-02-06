package org.jbpm.designer.web.server;

import org.apache.log4j.Logger;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/** 
 * 
 * Interaction with task forms for inline editor.
 * 
 * @author Tihomir Surdilovic
 */
public class TaskFormsEditorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger _logger = Logger.getLogger(TaskFormsEditorServlet.class);
	private static final String TASKFORM_FILE_EXTENSION = "flt";
	private static final String TASKFORM_NAME_EXTENSION = "-taskform";
	private static final String ACTION_LOAD = "load";
	private static final String ACTION_SAVE = "save";

    private IDiagramProfile profile;
    // this is here just for unit testing purpose
    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }

    @Inject
    private IDiagramProfileService _profileService = null;

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

         if (profile == null) {
	        profile = _profileService.findProfile(req, profileName);
         }
         Repository repository = profile.getRepository();

         Asset<String> processAsset = null;
         try {
             processAsset = repository.loadAsset(uuid);


             if(action != null && action.equals(ACTION_LOAD)) {
                 resp.setContentType("text/html");
                 resp.setCharacterEncoding("UTF-8");
                 PrintWriter pw = resp.getWriter();
                 pw.write(getTaskFormFromRepository(taskName, processAsset.getAssetLocation(), repository));
             } else if(action != null && action.equals(ACTION_SAVE)) {
                 resp.setContentType("text/plain");
                 resp.setCharacterEncoding("UTF-8");
                 PrintWriter pw = resp.getWriter();
                 try {
                    pw.write(storeTaskFormInRepository(taskName, processAsset.getAssetLocation(), taskFormValue, repository));
                } catch (Exception e) {
                    pw.write("error: " + e.getMessage());
                }
             }
         } catch (Exception e) {
             PrintWriter pw = resp.getWriter();
             pw.write("error: " + e.getMessage());
         }
	 }
	 
	 private String storeTaskFormInRepository(String taskName, String packageName, String formValue, Repository repository) throws Exception{

        repository.deleteAssetFromPath(packageName + taskName + TASKFORM_NAME_EXTENSION + "." + TASKFORM_FILE_EXTENSION);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder.location(packageName)
                .name(taskName + TASKFORM_NAME_EXTENSION)
                .type(TASKFORM_FILE_EXTENSION)
                .content(formValue.getBytes("UTF-8"));

        repository.createAsset(builder.getAsset());
        return "ok";
	 }
	 
	 private String getTaskFormFromRepository(String taskName, String packageName, Repository repository) {
         try {
             Asset<String> formAsset = repository.loadAssetFromPath(packageName + "/" + taskName + TASKFORM_NAME_EXTENSION + "." + TASKFORM_FILE_EXTENSION);
             return formAsset.getAssetContent();
         } catch (Exception e) {
             e.printStackTrace();
             _logger.error(e.getMessage());
         }
         return "false";
     }
}
