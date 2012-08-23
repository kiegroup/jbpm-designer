package org.jbpm.designer.web.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;
import org.apache.commons.codec.binary.Base64;

/**
 * Utility class for web servlets.
 * 
 * @author Tihomir Surdilovic
 *
 */
public class ServletUtil {
	private static final Logger _logger = Logger.getLogger(ServletUtil.class);
	public static final String EXT_BPMN = "bpmn";
    public static final String EXT_BPMN2 = "bpmn2";
	private ServletUtil(){}
	
	public static IDiagramProfile getProfile(HttpServletRequest req,
            String profileName, ServletContext context) {
        IDiagramProfile profile = null;

        IDiagramProfileService service = new ProfileServiceImpl();
        service.init(context);
        profile = service.findProfile(req, profileName);
        if (profile == null) {
            throw new IllegalArgumentException(
                    "Cannot determine the profile to use for interpreting models");
        }
        return profile;
    }
	
	public static List<String> getFormWidgetList(IDiagramProfile profile) {
		List<String> widgets = new ArrayList<String>();
		String globalAreaURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/globalArea/assets";
		try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory
                    .createXMLStreamReader(ServletUtil.getInputStreamForURL(
                    		globalAreaURL, "GET", profile), "UTF-8");
            String title = "";
            String format = "";
            while (reader.hasNext()) {
                int next = reader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(reader.getLocalName())) {
                        title = reader.getElementText();
                    }
                    if ("format".equals(reader.getLocalName())) {
                    	format = reader.getElementText();
                    }
                }
                if (next == XMLStreamReader.END_ELEMENT) {
                	if ("asset".equals(reader.getLocalName())) {
                		if(title.length() > 0 && format.length() > 0 && format.equals("fw")) {
                			widgets.add(title);
                			title = "";
                			format = "";
                		}
                	}
                }
            }
        } catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        }
		return widgets;
	}
	
	public static String[] findPackageAndAssetInfo(String uuid,
            IDiagramProfile profile) {
        List<String> packages = new ArrayList<String>();
        String packagesURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
                        profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/";
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory
                    .createXMLStreamReader(ServletUtil.getInputStreamForURL(packagesURL,
                            "GET", profile), "UTF-8");
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(reader.getLocalName())) {
                        packages.add(reader.getElementText());
                    }
                }
            }
        } catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        }
        
        boolean gotPackage = false;
        String[] pkgassetinfo = new String[2];
        for (String nextPackage : packages) {
        	try {
	        	String packageAssetURL = ExternalInfo.getExternalProtocol(profile)
	                    + "://"
	                    + ExternalInfo.getExternalHost(profile)
	                    + "/"
	                    + profile.getExternalLoadURLSubdomain().substring(0,
	                            profile.getExternalLoadURLSubdomain().indexOf("/"))
	                    + "/rest/packages/" + URLEncoder.encode(nextPackage, "UTF-8") + "/assets/";
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader = factory
                        .createXMLStreamReader(ServletUtil.getInputStreamForURL(
                                packageAssetURL, "GET", profile), "UTF-8");
                String title = "";
                String readuuid = "";
                while (reader.hasNext()) {
                    int next = reader.next();
                    if (next == XMLStreamReader.START_ELEMENT) {
                        if ("title".equals(reader.getLocalName())) {
                            title = reader.getElementText();
                        }
                        if ("uuid".equals(reader.getLocalName())) {
                        	readuuid = reader.getElementText();
                        }
                    }
                    if (next == XMLStreamReader.END_ELEMENT) {
                    	if ("asset".equals(reader.getLocalName())) {
                    		if(title.length() > 0 && readuuid.length() > 0 && uuid.equals(readuuid)) {
                    			pkgassetinfo[0] = nextPackage;
                                pkgassetinfo[1] = title;
                                gotPackage = true;
                    		}
                    	}
                    }
                }
            } catch (Exception e) {
                // we dont want to barf..just log that error happened
                _logger.error(e.getMessage());
            }
            if (gotPackage) {
                // noo need to loop through rest of packages
                break;
            }
        }
        return pkgassetinfo;
    }
	
	public static InputStream getInputStreamForURL(String urlLocation,
            String requestMethod, IDiagramProfile profile) throws Exception {
        URL url = new URL(urlLocation);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml,application/json,application/octet-stream,text/json,text/plain;q=0.9,*/*;q=0.8");

        connection.setRequestProperty("charset", "UTF-8");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setReadTimeout(5 * 1000);

        ServletUtil.applyAuth(profile, connection);

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
	
	public static void applyAuth(IDiagramProfile profile, HttpURLConnection connection) {
		if (profile.getUsr() != null && profile.getUsr().trim().length() > 0
				&& profile.getPwd() != null
				&& profile.getPwd().trim().length() > 0) {
			String auth = profile.getUsr() + ":" + profile.getPwd();
	        connection.setRequestProperty("Authorization", "Basic "
	                + Base64.encodeBase64String(auth.getBytes()));
		}
    }
	
	public static boolean assetExistsInGuvnor(String packageName, String assetName, IDiagramProfile profile) {
    	try {	
    		String formURL = ExternalInfo.getExternalProtocol(profile)
    	        + "://"
    	        + ExternalInfo.getExternalHost(profile)
    	        + "/"
    	        + profile.getExternalLoadURLSubdomain().substring(0,
    	                profile.getExternalLoadURLSubdomain().indexOf("/"))
    	        + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + URLEncoder.encode(assetName, "UTF-8");
    	
    	
			URL checkURL = new URL(formURL);
			HttpURLConnection checkConnection = (HttpURLConnection) checkURL
			        .openConnection();
			ServletUtil.applyAuth(profile, checkConnection);
			checkConnection.setRequestMethod("GET");
			checkConnection
			        .setRequestProperty("Accept", "application/atom+xml");
			checkConnection.connect();
			_logger.info("check connection response code: " + checkConnection.getResponseCode());
			if (checkConnection.getResponseCode() == 200) {
				return true;
			}
		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
        return false;
    }
	
	public static boolean existsProcessImageInGuvnor(String assetURL, IDiagramProfile profile) {
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
	
	public static List<String> getPackageNamesFromGuvnor(IDiagramProfile profile) {
        List<String> packages = new ArrayList<String>();
        String packagesURL = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
    	                profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/";
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory
                    .createXMLStreamReader(ServletUtil.getInputStreamForURL(packagesURL, "GET", profile), "UTF-8");
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(reader.getLocalName())) {
                    	String pname = reader.getElementText();
                    	if(!pname.equalsIgnoreCase("Packages")) {
                    		 packages.add(pname);
                    	}
                    }
                }
            }
        } catch (Exception e) {
            _logger.error("Error retriving packages from guvnor: " + e.getMessage());
        }
        return packages;
    }
	
	public static List<String> getAllProcessesInPackage(String pkgName, IDiagramProfile profile) {
        List<String> processes = new ArrayList<String>();
        try {
	        String assetsURL = ExternalInfo.getExternalProtocol(profile)
	                + "://"
	                + ExternalInfo.getExternalHost(profile)
	                + "/"
	                + profile.getExternalLoadURLSubdomain().substring(0,
	    	                profile.getExternalLoadURLSubdomain().indexOf("/"))
	                + "/rest/packages/"
	                + URLEncoder.encode(pkgName, "UTF-8")
	                + "/assets/";
	        
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(ServletUtil.getInputStreamForURL(assetsURL, "GET", profile), "UTF-8");

            String format = "";
            String title = ""; 
            while (reader.hasNext()) {
                int next = reader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if ("format".equals(reader.getLocalName())) {
                        format = reader.getElementText();
                    } 
                    if ("title".equals(reader.getLocalName())) {
                        title = reader.getElementText();
                    }
                    if ("asset".equals(reader.getLocalName())) {
                        if(format.equals(EXT_BPMN) || format.equals(EXT_BPMN2)) {
                            processes.add(title);
                            title = "";
                            format = "";
                        }
                    }
                }
            }
            // last one
            if(format.equals(EXT_BPMN) || format.equals(EXT_BPMN2)) {
                processes.add(title);
            }
        } catch (Exception e) {
        	_logger.error("Error finding processes in package: " + e.getMessage());
        } 
        return processes;
    }
	
	public static String getProcessImagePath(String packageName, String processid, IDiagramProfile profile) {
		try {
			return ExternalInfo.getExternalProtocol(profile)
			        + "://"
			        + ExternalInfo.getExternalHost(profile)
			        + "/"
			        + profile.getExternalLoadURLSubdomain().substring(0,
			                profile.getExternalLoadURLSubdomain().indexOf("/"))
			        + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + processid + "-image"
			        + "/binary/";
		} catch (UnsupportedEncodingException e) {
			_logger.error(e.getMessage());
			return "";
		}
	}
	
	public static String getProcessImageSourcePath(String packageName, String processid, IDiagramProfile profile) {
		return ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0,
    	                profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/" + packageName + "/assets/" + processid + "-image"
                + "/source/";
	}
	
	public static String getProcessSourceContent(String packageName, String assetName, IDiagramProfile profile) {
		try {	
			String assetSourceURL = ExternalInfo.getExternalProtocol(profile)
	                + "://"
	                + ExternalInfo.getExternalHost(profile)
	                + "/"
	                + profile.getExternalLoadURLSubdomain().substring(0,
	    	                profile.getExternalLoadURLSubdomain().indexOf("/"))
	                + "/rest/packages/" + URLEncoder.encode(packageName, "UTF-8") + "/assets/" + assetName
	                + "/source/";

        
            InputStream in = ServletUtil.getInputStreamForURL(assetSourceURL, "GET", profile);
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer);
            return writer.toString();
        } catch (Exception e) {
        	_logger.error("Error retrieving asset content: " + e.getMessage());
            return "";
        }
    }
	
	public static String streamToString(InputStream is) {
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			return sb.toString();
		} catch (Exception e) {
			_logger.error("Error converting input stream to string: "
					+ e.getMessage());
			return "";
		}
	}
}
