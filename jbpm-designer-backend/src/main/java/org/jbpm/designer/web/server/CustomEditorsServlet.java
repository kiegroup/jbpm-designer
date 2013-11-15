package org.jbpm.designer.web.server;

import org.jbpm.designer.expressioneditor.server.ExpressionEditorProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Sevlet for custom editors.
 * 
 * @author Tihomir Surdilovic
 */
public class CustomEditorsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger _logger = LoggerFactory.getLogger(CustomEditorsServlet.class);
	public static final String CUSTOMEDITORS_NAME = "customeditors";

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		String profileName = req.getParameter("profile");
        String uuid = req.getParameter("uuid");

        if (isExpressionEditorRequest(req)) {
            //do the processing for the expression editor.
            ExpressionEditorProcessor expressionEditorProcessor = new ExpressionEditorProcessor();
            expressionEditorProcessor.doProcess(req, resp);

        } else {
            //do the normal processing of this servlet.
            if (profile == null) {
                profile = _profileService.findProfile(req, profileName);
            }
		    String customEditorsJSON = getCustomEditorsJSON(profile, getServletContext(), uuid);
		    PrintWriter pw = resp.getWriter();
		    resp.setContentType("text/plain");
		    resp.setCharacterEncoding("UTF-8");
		    pw.write(customEditorsJSON);
        }
	}

    private String getCustomEditorsJSON(IDiagramProfile profile, ServletContext servletContext, String uuid) {

        String retStr = "";
        Repository repository = profile.getRepository();
        try {
            Asset<String> customEditorAsset = repository.loadAssetFromPath(profile.getRepositoryGlobalDir( uuid ) + "/" + CUSTOMEDITORS_NAME + ".json");

            retStr = customEditorAsset.getAssetContent();

		} catch (Exception e) {
			_logger.error("Error retriving custom editors info: " + e.getMessage());
		}
    	
        return retStr;
	}

    private boolean isExpressionEditorRequest(HttpServletRequest req) {
        return req.getParameter(ExpressionEditorProcessor.COMMAND_PARAM) != null && req.getParameter(ExpressionEditorProcessor.MESSAGE_PARAM) != null;
    }

}