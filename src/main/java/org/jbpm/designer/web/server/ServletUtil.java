package org.jbpm.designer.web.server;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ExternalInfo;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;

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
	
	public static String[] findPackageAndAssetInfo(String uuid,
            IDiagramProfile profile) {
        // GUVNOR ServletUtil
        List<String> packages = getPackageNames(profile);
        
        boolean gotPackage = false;
        String[] pkgassetinfo = new String[2];
        for (String nextPackage : packages) {
        	try {
	        	String packageAssetURL = getUrl(profile, nextPackage, "", UrlType.Normal);

                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLStreamReader reader = factory.createXMLStreamReader(ServletUtil.getOutputReaderFromUrl(packageAssetURL, "GET", profile));
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
	
	public static String getStringContentFromUrl(String urlLocation,
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

	    BufferedReader sreader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
	    StringBuilder stringBuilder = new StringBuilder();

	    String line = null;
	    while ((line = sreader.readLine()) != null) {
	        stringBuilder.append(line + "\n");
	    }

	    return stringBuilder.toString();
	}
	
	public static Reader getOutputReaderFromUrl(String urlLocation,
	        String requestMethod, IDiagramProfile profile) throws Exception {
	    String output = getStringContentFromUrl(urlLocation, requestMethod, profile);
	    return new StringReader(output);
	}
	
	public static void applyAuth(IDiagramProfile profile, HttpURLConnection connection) {
		if (profile.getUsr() != null && profile.getUsr().trim().length() > 0
				&& profile.getPwd() != null && profile.getPwd().trim().length() > 0) {
			String auth = profile.getUsr() + ":" + profile.getPwd();
	        connection.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String(auth.getBytes()));
		}
    }
	
	public static boolean assetExistsInGuvnor(String packageName, String assetName, IDiagramProfile profile) {
    	try {	
            // GUVNOR ServletUtil
    		String formURL = getUrl(profile, packageName, assetName, UrlType.Normal);
    	
			URL checkURL = new URL(formURL);
			HttpURLConnection checkConnection = (HttpURLConnection) checkURL.openConnection();
			ServletUtil.applyAuth(profile, checkConnection);
			checkConnection.setRequestMethod("GET");
			checkConnection.setRequestProperty("Accept", "application/atom+xml");
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
	
	public static List<String> getPackageNames(IDiagramProfile profile) {
        // GUVNOR ServletUtil
        List<String> packages = new ArrayList<String>();
        String packagesURL = getUrl(profile, "", null, UrlType.Normal);

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(ServletUtil.getOutputReaderFromUrl(packagesURL, "GET", profile));
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
        // GUVNOR ServletUtil
        try {
	        String assetsURL = getUrl(profile, pkgName, "", UrlType.Normal);
            
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(ServletUtil.getOutputReaderFromUrl(assetsURL, "GET", profile));

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
	
	public static String getProcessSourceContent(String packageName, String assetName, IDiagramProfile profile) {
        // GUVNOR ServletUtil
		try {	
			String assetSourceURL = getUrl(profile, packageName, assetName, UrlType.Source);
            return getStringContentFromUrl(assetSourceURL, "GET", profile);
        } catch (Exception e) {
        	_logger.error("Error retrieving asset content: " + e.getMessage());
            return "";
        }
    }

	public enum UrlType { 
	    Normal, Source, Binary;
	}
	
	public static String getUrl(IDiagramProfile profile, String packageName, String assetName, UrlType urlType) { 
	    return getUrl(profile, packageName, assetName, null, urlType);
	}
	
	public static String getUrl(IDiagramProfile profile, String packageName, String assetName, String version, UrlType urlType) { 
	    // Encode version, asset and package names
	    String urlVersion = version;
	    if( urlVersion != null ) { 
	        try { 
	            urlVersion = URLEncoder.encode(version, "UTF-8");
	        } catch(UnsupportedEncodingException uee ) { 
	            _logger.error( "Unable to encode [" + version + "] into application/x-www-form-urlencoded format.", uee);
	        }
	    }
	    String urlAssetName = assetName;
	    if( urlAssetName != null ) { 
	        try { 
	            urlAssetName = URLEncoder.encode(assetName, "UTF-8");
	        } catch(UnsupportedEncodingException uee ) { 
	            _logger.error( "Unable to encode [" + assetName + "] into application/x-www-form-urlencoded format.", uee);
	        }
	    }
	    String urlPackageName = packageName;
	    try { 
	        urlPackageName = URLEncoder.encode(packageName, "UTF-8");
	    } catch(UnsupportedEncodingException uee ) { 
	        _logger.error( "Unable to encode [" + packageName + "] into application/x-www-form-urlencoded format.", uee);
	    }
	   
	    // Generate URL
	    String url = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0, profile.getExternalLoadURLSubdomain().indexOf("/"))
                + "/rest/packages/" + urlPackageName;
	    if( urlAssetName != null ) { 
	        url += "/assets/" + urlAssetName;
	        if( urlVersion != null ) { 
	            url +=  "/versions/" + urlVersion;
	        }
	    }
	    
        switch( urlType ) { 
        case Normal:
            break;
        case Source:
            url += "/source/";	 
            break;
        case Binary:
            url += "/binary/";
            break;
        default: 
            throw new RuntimeException( "Unknown url type : " + urlType.toString() );
        }
        
        return url;
	}
	
	@Deprecated // a string turned to stream is being passed to this method!!
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
