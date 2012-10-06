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

/**
 * 
 * Queries Guvnor for process version info.
 * 
 * @author Tihomir Surdilovic
 */
public class ProcessDiffServiceServlet extends HttpServlet {

    private static final Logger _logger = Logger.getLogger(ProcessDiffServiceServlet.class);
    
    private static final String GET_VERSION_ACTION = "getversion";

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
        if(action != null && action.equals(GET_VERSION_ACTION) && versionNum != null) {
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

	private static String getAssetVerionSource(String packageName, String assetName, String versionNum, IDiagramProfile profile) {
        // GUVNOR ProcessDiffServiceServlet
		try {
			String versionURL = ServletUtil.getUrl(profile, packageName, assetName, versionNum, UrlType.Source);
			return ServletUtil.getStringContentFromUrl(versionURL, "GET", profile);
		} catch (Exception e) {
			return "";
		}
	}
	
	private static List<String> getAssetVersions(String packageName, String assetName, String uuid, IDiagramProfile profile) {
        // GUVNOR ProcessDiffServiceServlet
		try {
			String assetVersionURL = ServletUtil.getUrl(profile, packageName, assetName, "", UrlType.Normal);
			List<String> versionList = new ArrayList<String>();
			
			XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(ServletUtil.getOutputReaderFromUrl(assetVersionURL, "GET", profile));
            boolean isFirstTitle = true;
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
