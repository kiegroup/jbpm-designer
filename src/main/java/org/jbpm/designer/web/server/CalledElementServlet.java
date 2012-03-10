package org.jbpm.designer.web.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.web.profile.IDiagramProfile;
import org.json.JSONObject;

/**
 * Sevlet for resolving called elements.
 * 
 * @author Tihomir Surdilovic
 */
public class CalledElementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletConfig config;
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String profileName = req.getParameter("profile");
        String uuid = req.getParameter("uuid");
        String processPackage = req.getParameter("ppackage");
        String processId = req.getParameter("pid");
        String action = req.getParameter("action");
        
        IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
        if(action != null && action.equals("imageview")) {
        	String retValue = "";
        	List<String> allPackageNames = ServletUtil.getPackageNamesFromGuvnor(profile);
        	if(allPackageNames != null && allPackageNames.size() > 0) {
        		for(String packageName : allPackageNames) {
        			List<String> allProcessesInPackage = ServletUtil.getAllProcessesInPackage(packageName, profile);
        			if(allProcessesInPackage != null && allProcessesInPackage.size() > 0) {
        				for(String p : allProcessesInPackage) {
                			String processContent = ServletUtil.getProcessSourceContent(packageName, p, profile);
                			Pattern idPattern = Pattern.compile("<\\S*process[^\"]+id=\"([^_\"]+)\"", Pattern.MULTILINE);
        		            Matcher idMatcher = idPattern.matcher(processContent);
        		            if(idMatcher.find()) {
        		            	String pid = idMatcher.group(1);
        		            	String pidpath = ServletUtil.getProcessImagePath(packageName, pid, profile);
        		            	if(pid != null && pid.equals(processId)) {
        		            		retValue = ServletUtil.existsProcessImageInGuvnor(pidpath, profile) ? pidpath : "";
        		            		break;
        		            	}
        		            }
                		}
        			}
        		}
        	}
        	resp.setCharacterEncoding("UTF-8");
	        resp.setContentType("text/plain");
	        resp.getWriter().write(retValue);
        } else {
	        String retValue = "false";
	        List<String> allPackageNames = ServletUtil.getPackageNamesFromGuvnor(profile);
	        Map<String, String> processInfo = new HashMap<String, String>();
	        if(allPackageNames != null && allPackageNames.size() > 0) {
	        	for(String packageName : allPackageNames) {
	        		List<String> allProcessesInPackage = ServletUtil.getAllProcessesInPackage(packageName, profile);
	        		if(allProcessesInPackage != null && allProcessesInPackage.size() > 0) {
	    				for(String p : allProcessesInPackage) {
	    					String processContent = ServletUtil.getProcessSourceContent(packageName, p, profile);
	    					Pattern idPattern = Pattern.compile("<\\S*process[^\"]+id=\"([^_\"]+)\"", Pattern.MULTILINE);
	    		            Matcher idMatcher = idPattern.matcher(processContent);
	    		            if(idMatcher.find()) {
	    		            	String pid = idMatcher.group(1);
	    		            	String pidpath = ServletUtil.getProcessImagePath(packageName, pid, profile);
	    		            	if(pid != null && !(packageName.equals(processPackage) && pid.equals(processId))) {
	    		            		processInfo.put(pid+"|"+packageName, ServletUtil.existsProcessImageInGuvnor(pidpath, profile) ? pidpath : "");
	    		            	}
	    		            }
	    				}
	    				retValue = getProcessInfoAsJSON(processInfo).toString();
	    			}
	        	}
	        }
			resp.setCharacterEncoding("UTF-8");
	        resp.setContentType("application/json");
	        resp.getWriter().write(retValue);
        }
	}
	
	public JSONObject getProcessInfoAsJSON(Map<String, String> processInfo) {
		JSONObject jsonObject = new JSONObject();
		for (Entry<String,String> error: processInfo.entrySet()) {
			try {
				jsonObject.put(error.getKey(), error.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}
}
