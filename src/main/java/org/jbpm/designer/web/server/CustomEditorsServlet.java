package org.jbpm.designer.web.server;

import java.io.*;
import java.util.Scanner;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.GuvnorUtil.UrlType;

/**
 * Sevlet for custom editors.
 * 
 * @author Tihomir Surdilovic
 */
public class CustomEditorsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger _logger = Logger.getLogger(CustomEditorsServlet.class);
	public static final String CUSTOMEDITORS_NAME = "customeditors";
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		String profileName = req.getParameter("profile");
		IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
		
		String customEditorsJSON = getCustomEditorsJSON(profile, getServletContext());
		PrintWriter pw = resp.getWriter();
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		pw.write(customEditorsJSON);
	}
	
	private String getCustomEditorsJSON(IDiagramProfile profile, ServletContext servletContext) {
        // GUVNOR CustomEditorsServlet
		String retStr = "";
		String customEditorsURL = GuvnorUtil.getUrl(profile, "globalArea", CUSTOMEDITORS_NAME, UrlType.Normal);

    	try {
			if (GuvnorUtil.readCheckAssetExists(customEditorsURL, profile) ) { 
			    String customEditorsSourceURL = GuvnorUtil.getUrl(profile, "globalArea", CUSTOMEDITORS_NAME, UrlType.Source);
				retStr = GuvnorUtil.readStringContentFromUrl(customEditorsSourceURL, "GET", profile);
			} else {
				retStr = readFile(servletContext.getRealPath("/defaults/customeditors.json"));
			}
		} catch (Exception e) {
			_logger.error("Error retriving custom editors info: " + e.getMessage());
		}
    	
        return retStr;
	}
	
	private String readFile(String pathname) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new File(pathname), "UTF-8");
        String lineSeparator = System.getProperty("line.separator");
        try {
            while(scanner.hasNextLine()) {        
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }
}
