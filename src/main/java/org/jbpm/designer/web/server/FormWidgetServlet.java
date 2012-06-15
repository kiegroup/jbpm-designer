package org.jbpm.designer.web.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.json.JSONObject;

public class FormWidgetServlet extends HttpServlet {
	private static final Logger _logger = Logger
			.getLogger(FormWidgetServlet.class);
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String profileName = req.getParameter("profile");
		String action = req.getParameter("action");
		String widgetName = req.getParameter("widgetname");
		
		IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
		
		if(action != null && action.equals("getwidgets")) {
			List<String> widgetList;
			try {
				widgetList = ServletUtil.getFormWidgetList(profile);
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
			String widgetSourceURL = ExternalInfo.getExternalProtocol(profile)
					+ "://"
	                + ExternalInfo.getExternalHost(profile)
	                + "/"
	                + profile.getExternalLoadURLSubdomain().substring(0,
	                        profile.getExternalLoadURLSubdomain().indexOf("/"))
	                + "/rest/packages/globalArea/assets/" + widgetName 
	                + "/source/";
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("text/plain");
			try {
				resp.getWriter().write(IOUtils.toString(ServletUtil.getInputStreamForURL(widgetSourceURL, "GET", profile), "UTF-8"));
			} catch (Exception e) {
				resp.getWriter().write("");
			}
		}
	}
}
