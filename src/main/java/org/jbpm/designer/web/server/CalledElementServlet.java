package org.jbpm.designer.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.server.ServletUtil.UrlType;
import org.json.JSONObject;

/**
 * Sevlet for resolving called elements.
 * 
 * @author Tihomir Surdilovic
 */
public class CalledElementServlet extends HttpServlet {
    
	private static final long serialVersionUID = 1L;
	private static final Logger _logger = Logger.getLogger(CalledElementServlet.class);
	
	private static Pattern processIdPattern = Pattern.compile("<\\S*process[^\"]+id=\"([^_\"]+)\"", Pattern.MULTILINE);
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String profileName = req.getParameter("profile");
        String processPackage = req.getParameter("ppackage");
        String processId = req.getParameter("pid");
        String action = req.getParameter("action");
        
        IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
        if(action != null && action.equals("imageview")) {
        	String processPath = findProcessImagePath(profile, processId);

        	resp.setCharacterEncoding("UTF-8");
	        resp.setContentType("text/plain");
	        resp.getWriter().write(processPath);
        } else {
	        String jsonProcessPathsString = findProcessImagePathInPackage(profile, processPackage, processId);

			resp.setCharacterEncoding("UTF-8");
	        resp.setContentType("application/json");
	        resp.getWriter().write(jsonProcessPathsString);
        }
	}
	
	public static String findProcessImagePath(IDiagramProfile profile, String processId) { 
        String retValue = "";
        List<String> allPackageNames = ServletUtil.getPackageNames(profile);
        if(allPackageNames != null && allPackageNames.size() > 0) {
            for(String packageName : allPackageNames) {
                List<String> allProcessesInPackage = ServletUtil.getAllProcessesInPackage(packageName, profile);
                if(allProcessesInPackage != null && allProcessesInPackage.size() > 0) {
                    for(String p : allProcessesInPackage) {
                        String processContent = ServletUtil.getProcessSourceContent(packageName, p, profile);
                        Matcher idMatcher = processIdPattern.matcher(processContent);
                        if(idMatcher.find()) {
                            String pid = idMatcher.group(1);
                            String pidpath = getProcessImagePath(packageName, pid, profile);
                            if(pid != null && pid.equals(processId)) {
                                retValue = existsProcessImageInGuvnor(pidpath, profile) ? pidpath : "";
                                break;
                            }
                        }
                    }
                }
            }
        }
        return retValue;
	}
	
	public static String findProcessImagePathInPackage(IDiagramProfile profile, String processPackage, String processId) {
	    String retValue = "false";
        List<String> allPackageNames = ServletUtil.getPackageNames(profile);
        Map<String, String> processInfo = new HashMap<String, String>();
        if(allPackageNames != null && allPackageNames.size() > 0) {
            for(String packageName : allPackageNames) {
                List<String> allProcessesInPackage = ServletUtil.getAllProcessesInPackage(packageName, profile);
                if(allProcessesInPackage != null && allProcessesInPackage.size() > 0) {
                    for(String p : allProcessesInPackage) {
                        String processContent = ServletUtil.getProcessSourceContent(packageName, p, profile);
                        Matcher idMatcher = processIdPattern.matcher(processContent);
                        if(idMatcher.find()) {
                            String pid = idMatcher.group(1);
                            String pidpath = getProcessImagePath(packageName, pid, profile);
                            // if 
                            // 1. we have a pid
                            // 2. AND the asset is not the one we've been given [NOT (thisPkg && thisPid)]
                            // then grab the info
                            if(pid != null && !(packageName.equals(processPackage) && pid.equals(processId))) {
                                String possiblePidPath = existsProcessImageInGuvnor(pidpath, profile) ? pidpath : "";
                                processInfo.put(pid+"|"+packageName, possiblePidPath);
                            }
                        }
                    }
                }
            }
        }
        if( processInfo.size() > 0 ) { 
            retValue = getProcessInfoAsJSON(processInfo).toString();
        }
        
        return retValue;
	}
	
	public static JSONObject getProcessInfoAsJSON(Map<String, String> processInfoPathMap) {
		JSONObject jsonObject = new JSONObject();
		for (Entry<String,String> procesInfo: processInfoPathMap.entrySet()) {
			try {
				jsonObject.put(procesInfo.getKey(), procesInfo.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}
	
	private static boolean existsProcessImageInGuvnor(String assetURL, IDiagramProfile profile) {
	    try {   
	        URL checkURL = new URL(assetURL);
	        HttpURLConnection checkConnection = (HttpURLConnection) checkURL
	                .openConnection();
	        ServletUtil.applyAuth(profile, checkConnection);
	        checkConnection.setRequestMethod("GET");
	        //checkConnection
	        //        .setRequestProperty("Accept", "application/binary");
	        checkConnection.connect();
	        _logger.info("check connection response code: " + checkConnection.getResponseCode());
	        InputStream is = checkConnection.getInputStream();
	        while(is.read() != -1) {
	            // read all response data
	        }
	        if (checkConnection.getResponseCode() == 200) {
	            return true;
	        }
	    } catch (Exception e) {
	        _logger.error(e.getMessage());
	    }
	    return false;
	}
	
	private static String getProcessImagePath(String packageName, String processid, IDiagramProfile profile) {
	    // GUVNOR CalledElementServlet
	    return ServletUtil.getUrl(profile, packageName, processid + "-image", UrlType.Binary);
	}
}
