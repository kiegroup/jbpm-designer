package org.jbpm.designer.web.server;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.ExternalInfo;

/** 
 * 
 * Queries repository to get the process information.
 * 
 * @author Tihomir Surdilovic
 */
public class ProcessInfoServlet extends HttpServlet {
    
	private static final long serialVersionUID = 1L;
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		String uuid = req.getParameter("uuid");
        String profileName = req.getParameter("profile");
        
        IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());

        try {
        	// find out what package the uuid belongs to
        	String[] packageAssetInfo = ServletUtil.findPackageAndAssetInfo(uuid, profile);
        	String packageName = packageAssetInfo[0];
        	String assetName = packageAssetInfo[1];
        	Map<String, String> processInfo = getProcessInfo(packageName, assetName, uuid, profile);
        	resp.setCharacterEncoding("UTF-8");
        	resp.setContentType("text/html");
        	resp.getWriter().write(createHtmlTable(processInfo));
        } catch (Exception e) {
        	resp.setCharacterEncoding("UTF-8");
        	resp.setContentType("text/html");
        	resp.getWriter().write("<center><b>Unable to retrieve process information.</b></center>");
        }
	}
	
	private String createHtmlTable(Map<String, String> processInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("<table border=\"0\" width=\"100%\">");
		Iterator<String> keyIterator = processInfo.keySet().iterator();
		while(keyIterator.hasNext()) {
			String key = keyIterator.next();
			sb.append("<tr>");
			sb.append("<td><b>").append(key).append(":").append("</b></td>");
			sb.append("<td>").append(processInfo.get(key)).append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	public static Map<String, String> getProcessInfo(String packageName, String assetName, String uuid, IDiagramProfile profile) throws Exception {
		Map<String, String> infoMap = new LinkedHashMap<String, String>();
		infoMap.put("Name", assetName);
		infoMap.put("Package", packageName);
		
		String [] params = { "Format", "Created", "Created By", "Last Modified", "Comment", "Version" };
		for( String param : params ) { 
		    infoMap.put(param, "");
		}
		
        // GUVNOR ProcessInfoServlet
		String assetInfoURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + assetName;
		XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(ServletUtil.getOutputReaderFromUrl(assetInfoURL, "GET", profile));
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamReader.START_ELEMENT) {
                if ("format".equals(reader.getLocalName())) {
                    infoMap.put("Format", reader.getElementText());
                }
                if ("checkInComment".equals(reader.getLocalName())) {
                    infoMap.put("Comment", reader.getElementText());
                }
                if ("created".equals(reader.getLocalName())) {
                    infoMap.put("Created", reader.getElementText());
                }
                if ("createdBy".equals(reader.getLocalName())) {
                    infoMap.put("Created By", reader.getElementText());
                }
                if ("lastModified".equals(reader.getLocalName())) {
                    infoMap.put("Last Modified", reader.getElementText());
                }
                if ("version".equals(reader.getLocalName())) {
                    infoMap.put("Version", reader.getElementText());
                }
            }
        }
        return infoMap;
	}
}
