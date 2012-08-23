package org.jbpm.designer.web.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * Queries Guvnor for process version info.
 * 
 * @author Tihomir Surdilovic
 */
public class ProcessDiffServiceServlet extends HttpServlet {
	private static final Logger _logger = Logger
			.getLogger(ProcessDiffServiceServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uuid = req.getParameter("uuid");
		String profileName = req.getParameter("profile");
		String action = req.getParameter("action");
		String versionNum = req.getParameter("version");

		IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
		String[] packageAssetInfo = ServletUtil.findPackageAndAssetInfo(uuid, profile);
        String packageName = packageAssetInfo[0];
        String assetName = packageAssetInfo[1];
        if(action != null && action.equals("getversion") && versionNum != null) {
        	resp.setCharacterEncoding("UTF-8");
			resp.setContentType("text/xml");
			try {
				resp.getWriter().write(getAssetVerionSource(packageName, assetName, versionNum, profile));
			} catch (Throwable t) {
				resp.getWriter().write("");
			}
        } else {
	        List<String> versionList;
			try {
				versionList = getAssetVersions(packageName, assetName, uuid, profile);
			} catch (Throwable t) {
				versionList = new ArrayList<String>();
			}
	        JSONObject jsonObject = new JSONObject();
			if(versionList != null && versionList.size() > 0) {
				for(String version : versionList) {
					try {
						jsonObject.put(version, version);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json");
			resp.getWriter().write(jsonObject.toString());
        }
	}

	private String getAssetVerionSource(String packageName, String assetName, String versionNum, IDiagramProfile profile) {
		try {
			String versionURL = ExternalInfo.getExternalProtocol(profile)
					+ "://"
	                + ExternalInfo.getExternalHost(profile)
	                + "/"
	                + profile.getExternalLoadURLSubdomain().substring(0,
	                        profile.getExternalLoadURLSubdomain().indexOf("/"))
	                + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + assetName 
	                + "/versions/" + versionNum + "/source/";
			
			return IOUtils.toString(ServletUtil.getInputStreamForURL(versionURL, "GET", profile), "UTF-8");
		} catch (Exception e) {
			return "";
		}
	}
	
	private List<String> getAssetVersions(String packageName, String assetName, String uuid, IDiagramProfile profile) {
		try {
			String assetVersionURL = ExternalInfo.getExternalProtocol(profile)
					+ "://"
	                + ExternalInfo.getExternalHost(profile)
	                + "/"
	                + profile.getExternalLoadURLSubdomain().substring(0,
	                        profile.getExternalLoadURLSubdomain().indexOf("/"))
	                + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + assetName 
	                + "/versions/";
			List<String> versionList = new ArrayList<String>();
			
			XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory
                    .createXMLStreamReader(ServletUtil.getInputStreamForURL(
                    		assetVersionURL, "GET", profile), "UTF-8");
            boolean isFirstTitle = true;
            String title = "";
            while (reader.hasNext()) {
                int next = reader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(reader.getLocalName())) {
                    	if(isFirstTitle) {
                    		isFirstTitle = false;
                    	} else {
                    		versionList.add(reader.getElementText());
                    	}
                    }
                }
            }
            return versionList;
		} catch (Exception e) {
            _logger.error(e.getMessage());
            return null;
        }
	}
}
