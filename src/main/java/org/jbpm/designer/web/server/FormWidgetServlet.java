package org.jbpm.designer.web.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.ServletUtil.UrlType;
import org.json.JSONObject;

public class FormWidgetServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger _logger = Logger.getLogger(FormWidgetServlet.class);
	
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
		
		IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
		
		if(action != null && action.equals("getwidgets")) {
			List<String> widgetList;
			try {
				widgetList = getFormWidgetList(profile);
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
	        // GUVNOR FormWidgetServlet
			String widgetSourceURL = ServletUtil.getUrl(profile, "globalArea", widgetName, UrlType.Source);
			
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("text/plain");
			try {
				resp.getWriter().write(ServletUtil.getStringContentFromUrl(widgetSourceURL, "GET", profile));
			} catch (Exception e) {
				resp.getWriter().write("");
			}
		}
	}
	
	public static List<String> getFormWidgetList(IDiagramProfile profile) {
	    // GUVNOR FormWidgetServlet
	    List<String> widgets = new ArrayList<String>();
	    String globalAreaURL = ServletUtil.getUrl(profile, "globalArea", "", UrlType.Normal);
	    
	    try {
	        XMLInputFactory factory = XMLInputFactory.newInstance();
	        XMLStreamReader reader = factory.createXMLStreamReader(ServletUtil.getOutputReaderFromUrl(globalAreaURL, "GET", profile));
	        String title = "";
	        String format = "";
	        while (reader.hasNext()) {
	            int next = reader.next();
	            if (next == XMLStreamReader.START_ELEMENT) {
	                if ("title".equals(reader.getLocalName())) {
	                    title = reader.getElementText();
	                }
	                if ("format".equals(reader.getLocalName())) {
	                    format = reader.getElementText();
	                }
	            }
	            if (next == XMLStreamReader.END_ELEMENT) {
	                if ("asset".equals(reader.getLocalName())) {
	                    if(title.length() > 0 && format.length() > 0 && format.equals("fw")) {
	                        widgets.add(title);
	                        title = "";
	                        format = "";
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	        // we dont want to barf..just log that error happened
	        _logger.error(e.getMessage());
	    }
	    return widgets;
	}
}
