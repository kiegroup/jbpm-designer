package org.jbpm.designer.web.server;

import java.io.*;
import java.util.Scanner;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.GuvnorUtil.UrlType;
import org.json.JSONObject;

/**
 * Sevlet for color themes.
 * 
 * @author Tihomir Surdilovic
 */
public class ThemeServlet extends HttpServlet {
    
	private static final Logger _logger = Logger.getLogger(ThemeServlet.class);
	private static final long serialVersionUID = 1L;
	
	private static final String ACTION_GETTHEMENAMES = "getThemeNames";
	private static final String ACTION_GETTHEMEJSON = "getThemeJSON";
	private static final String THEME_NAME = "themes";
	private static final String DEFAULT_THEME = "jBPM";
	
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		String profileName = req.getParameter("profile");
		String action = req.getParameter("action");
		
		IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
		
		if(action != null && action.equals(ACTION_GETTHEMENAMES)) {
			String themeStr = getThemeNames(profile);
			PrintWriter pw = resp.getWriter();
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			pw.write(themeStr);
		} else if(action != null && action.equals(ACTION_GETTHEMEJSON)) {
			String themeJSON = getThemeJson(profile, getServletContext());
			PrintWriter pw = resp.getWriter();
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			pw.write(themeJSON);
		}
	}
	
	private String getThemeJson(IDiagramProfile profile, ServletContext servletContext) {
        // GUVNOR ThemeServlet
		String retStr = "";
		
		String themesURL = GuvnorUtil.getUrl(profile, "globalArea", THEME_NAME, UrlType.Normal);
    	
    	try {
			if (GuvnorUtil.readCheckAssetExists(themesURL, profile)) { 
			    String themesSourceURL = GuvnorUtil.getUrl(profile, "globalArea", THEME_NAME, UrlType.Source);
				retStr = GuvnorUtil.readStringContentFromUrl(themesSourceURL, "GET", profile);
			} else {
				retStr = readFile(servletContext.getRealPath("/defaults/themes.json"));
			}
		} catch (Exception e) {
			_logger.error("Error retriving color theme info: " + e.getMessage());
		}
        return retStr;
	}
	
	private String getThemeNames(IDiagramProfile profile) {
		String themesStr = "";
		String themesURL = GuvnorUtil.getUrl(profile, "globalArea", THEME_NAME, UrlType.Normal);
    	String themesSourceURL = GuvnorUtil.getUrl(profile, "globalArea", THEME_NAME, UrlType.Source);
        
        try {
			if( !GuvnorUtil.readCheckAssetExists(themesURL, profile) ) { 
				themesStr = DEFAULT_THEME;
			} else {
			    String content = GuvnorUtil.readStringContentFromUrl(themesSourceURL, "GET", profile);
				JSONObject themesObject =  new JSONObject(content);
				JSONObject themes = (JSONObject) themesObject.get("themes");
				for (int i = 0; i < themes.names().length(); i++) {
					themesStr += themes.names().getString(i) + ",";
				}
				if(themesStr.endsWith(",")) {
					themesStr = themesStr.substring(0, themesStr.length() - 1);
                }
				
			}
		} catch (Exception e) {
			themesStr = DEFAULT_THEME;
		} 
        return themesStr;
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
