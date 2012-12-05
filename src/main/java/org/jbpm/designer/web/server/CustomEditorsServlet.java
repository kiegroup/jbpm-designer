package org.jbpm.designer.web.server;

import org.apache.log4j.Logger;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;

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
	private static final Logger _logger = Logger.getLogger(CustomEditorsServlet.class);
	public static final String CUSTOMEDITORS_NAME = "customeditors";

    private IDiagramProfile profile;
    // this is here just for unit testing purpose
    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		String profileName = req.getParameter("profile");

        if (profile == null) {
            profile = ServletUtil.getProfile(req, profileName, getServletContext());
        }
		String customEditorsJSON = getCustomEditorsJSON(profile, getServletContext());
		PrintWriter pw = resp.getWriter();
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		pw.write(customEditorsJSON);
	}
	
	private String getCustomEditorsJSON(IDiagramProfile profile, ServletContext servletContext) {

        String retStr = "";
        Repository repository = profile.getRepository();
        try {
            Asset<String> customEditorAsset = repository.loadAssetFromPath(profile.getRepositoryGlobalDir() + "/" + CUSTOMEDITORS_NAME + ".json");

            retStr = customEditorAsset.getAssetContent();

		} catch (Exception e) {
			_logger.error("Error retriving custom editors info: " + e.getMessage());
		}
    	
        return retStr;
	}
}
