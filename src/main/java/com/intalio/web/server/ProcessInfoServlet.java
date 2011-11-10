package com.intalio.web.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

import com.intalio.web.profile.IDiagramProfile;
import com.intalio.web.profile.IDiagramProfileService;
import com.intalio.web.profile.impl.ExternalInfo;
import com.intalio.web.profile.impl.ProfileServiceImpl;

/** 
 * 
 * Queries repository to get the process information.
 * 
 * @author Tihomir Surdilovic
 */
public class ProcessInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger _logger = Logger.getLogger(ProcessInfoServlet.class);
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		String uuid = req.getParameter("uuid");
        String profileName = req.getParameter("profile");
        
        IDiagramProfile profile = getProfile(req, profileName);

        try {
        	// find out what package the uuid belongs to
        	String[] packageAssetInfo = findPackageAndAssetInfo(uuid, profile);
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
	
	private Map<String, String> getProcessInfo(String packageName, String assetName, String uuid, IDiagramProfile profile) throws Exception {
		Map<String, String> infoMap = new LinkedHashMap<String, String>();
		infoMap.put("Name", assetName);
		infoMap.put("Format", "");
		infoMap.put("Package", packageName);
		infoMap.put("Created", "");
		infoMap.put("Created By", "");
		infoMap.put("Last Modified", "");
		infoMap.put("Comment", "");
		infoMap.put("Version", "");
		
		String assetInfoURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/" + packageName + "/assets/" + assetName;
		XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory
               .createXMLStreamReader(getInputStreamForURL(assetInfoURL,
                       "GET", profile));
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
	
	private IDiagramProfile getProfile(HttpServletRequest req,
            String profileName) {
        IDiagramProfile profile = null;

        IDiagramProfileService service = new ProfileServiceImpl();
        service.init(getServletContext());
        profile = service.findProfile(req, profileName);
        if (profile == null) {
            throw new IllegalArgumentException(
                    "Cannot determine the profile to use for interpreting models");
        }
        return profile;
    }
	
	private String[] findPackageAndAssetInfo(String uuid,
            IDiagramProfile profile) throws Exception {
        List<String> packages = new ArrayList<String>();
        String packagesURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/";
         XMLInputFactory factory = XMLInputFactory.newInstance();
         XMLStreamReader reader = factory
                .createXMLStreamReader(getInputStreamForURL(packagesURL,
                        "GET", profile));
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamReader.START_ELEMENT) {
                if ("title".equals(reader.getLocalName())) {
                    packages.add(reader.getElementText());
                }
            }
        }
        boolean gotPackage = false;
        String[] pkgassetinfo = new String[2];
        for (String nextPackage : packages) {
            String packageAssetURL = ExternalInfo.getExternalProtocol(profile)
                    + "://"
                    + ExternalInfo.getExternalHost(profile)
                    + "/"
                    + profile.getExternalLoadURLSubdomain().substring(0,
                            profile.getExternalLoadURLSubdomain().indexOf("/"))
                    + "/rest/packages/" + nextPackage + "/assets/";
            XMLInputFactory pfactory = XMLInputFactory.newInstance();
            XMLStreamReader preader = pfactory
                   .createXMLStreamReader(getInputStreamForURL(
                            packageAssetURL, "GET", profile));
            String title = "";
            while (preader.hasNext()) {
                int next = preader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(preader.getLocalName())) {
                        title = preader.getElementText();
                    }
                    if ("uuid".equals(preader.getLocalName())) {
                        String eleText = preader.getElementText();
                        if (uuid.equals(eleText)) {
                            pkgassetinfo[0] = nextPackage;
                            pkgassetinfo[1] = title;
                            gotPackage = true;
                        }
                    }
                }
            }
            if (gotPackage) {
                // noo need to loop through rest of packages
                break;
            }
        }
        return pkgassetinfo;
    }
	
	private InputStream getInputStreamForURL(String urlLocation,
            String requestMethod, IDiagramProfile profile) throws Exception {
        URL url = new URL(urlLocation);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(requestMethod);
        connection
                .setRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.16) Gecko/20110319 Firefox/3.6.16");
        connection
                .setRequestProperty("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en-us,en;q=0.5");
        connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
        connection.setRequestProperty("charset", "UTF-8");
        connection.setConnectTimeout(5 * 1000);
        connection.setReadTimeout(5 * 1000);

        applyAuth(profile, connection);

        connection.connect();

        BufferedReader sreader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        while ((line = sreader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }

        return new ByteArrayInputStream(stringBuilder.toString().getBytes(
                "UTF-8"));
    }
	
	private void applyAuth(IDiagramProfile profile, HttpURLConnection connection) {
        if (profile.getUsr() != null && profile.getUsr().trim().length() > 0
                && profile.getPwd() != null
                && profile.getPwd().trim().length() > 0) {
            BASE64Encoder enc = new sun.misc.BASE64Encoder();
            String userpassword = profile.getUsr() + ":" + profile.getPwd();
            String encodedAuthorization = enc.encode(userpassword.getBytes());
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedAuthorization);
        }
    }
}
