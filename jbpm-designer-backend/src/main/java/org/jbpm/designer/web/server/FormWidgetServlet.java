package org.jbpm.designer.web.server;

import org.apache.log4j.Logger;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FormWidgetServlet extends HttpServlet {
	private static final Logger _logger = Logger.getLogger(FormWidgetServlet.class);

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
	    req.setCharacterEncoding("UTF-8");
		String profileName = req.getParameter("profile");
		String action = req.getParameter("action");
		String widgetName = req.getParameter("widgetname");

        if (profile == null) {
            profile = _profileService.findProfile(req, profileName);
        }
        Repository repository = profile.getRepository();
		if(action != null && action.equals("getwidgets")) {
			List<String> widgetList;
			try {
				widgetList = ServletUtil.getFormWidgetList(profile, repository);
			} catch (Throwable t) {
				widgetList = new ArrayList<String>();
			}
			JSONObject jsonObject = new JSONObject();
			if(widgetList != null && widgetList.size() > 0) {
				for(String widget : widgetList) {
					try {
						jsonObject.put(widget, widget);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json");
			resp.getWriter().write(jsonObject.toString());
		} else if(action != null && action.equals("getwidgetsource")) {
            try {
                Asset<String> widgetAsset = repository.loadAssetFromPath(profile.getRepositoryGlobalDir() + "/" + widgetName+".fw");

                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("text/plain");

				resp.getWriter().write(widgetAsset.getAssetContent());
			} catch (Exception e) {
				resp.getWriter().write("");
			}
		}
	}
}
