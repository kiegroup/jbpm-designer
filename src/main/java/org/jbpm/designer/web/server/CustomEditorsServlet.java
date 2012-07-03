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
		String retStr = "";
		String customEditorsURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + CUSTOMEDITORS_NAME;
    	
    	String customEditorsSourceURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets/" + CUSTOMEDITORS_NAME + "/source";
    	try {
			URL checkURL = new URL(customEditorsURL);
			HttpURLConnection checkConnection = (HttpURLConnection) checkURL
			        .openConnection();
			ServletUtil.applyAuth(profile, checkConnection);
			checkConnection.setRequestMethod("GET");
			checkConnection
			        .setRequestProperty("Accept", "application/atom+xml");
			checkConnection.connect();
			_logger.info("check connection response code: " + checkConnection.getResponseCode());
			if (checkConnection.getResponseCode() == 200) {
				retStr = ServletUtil.streamToString(ServletUtil.getInputStreamForURL(customEditorsSourceURL, "GET", profile));
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
