package org.jbpm.designer.web.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.json.JSONObject;

/**
 * Sevlet for color themes.
 * 
 * @author Tihomir Surdilovic
 */
public class ThemeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String ACTION_GETTHEMENAMES = "getThemeNames";
	private static final String ACTION_GETTHEMEJSON = "getThemeJSON";
	private static final String THEME_NAME = "themes";
	private static final String DEFAULT_THEME = "jBPM";
	private static final Logger _logger = Logger.getLogger(ThemeServlet.class);
	private ServletConfig config;
	private String themeInfo;
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;
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
		String retStr = "";
		String themesURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + THEME_NAME;
    	
    	String themesSourceURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + THEME_NAME + "/source";
    	
    	
    	try {
			URL checkURL = new URL(themesURL);
			HttpURLConnection checkConnection = (HttpURLConnection) checkURL
			        .openConnection();
			ServletUtil.applyAuth(profile, checkConnection);
			checkConnection.setRequestMethod("GET");
			checkConnection
			        .setRequestProperty("Accept", "application/atom+xml");
			checkConnection.connect();
			_logger.info("check connection response code: " + checkConnection.getResponseCode());
			if (checkConnection.getResponseCode() == 200) {
				retStr = ServletUtil.streamToString(ServletUtil.getInputStreamForURL(themesSourceURL, "GET", profile));
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
		String themesURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + THEME_NAME;
    	
    	String themesSourceURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + THEME_NAME + "/source";
    	
        try {
			URL checkURL = new URL(themesURL);
			HttpURLConnection checkConnection = (HttpURLConnection) checkURL
			        .openConnection();
			ServletUtil.applyAuth(profile, checkConnection);
			checkConnection.setRequestMethod("GET");
			checkConnection
			        .setRequestProperty("Accept", "application/atom+xml");
			checkConnection.connect();
			_logger.info("check connection response code: " + checkConnection.getResponseCode());
			if (checkConnection.getResponseCode() != 200) {
				themesStr = DEFAULT_THEME;
			} else {
				JSONObject themesObject =  new JSONObject(ServletUtil.streamToString(ServletUtil.getInputStreamForURL(themesSourceURL, "GET", profile)));
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
