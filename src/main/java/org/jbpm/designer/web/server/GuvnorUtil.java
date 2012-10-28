package org.jbpm.designer.web.server;

import java.io.*;
import java.net.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.ExternalInfo;

public class GuvnorUtil {

    private static final Logger _logger = Logger.getLogger(GuvnorUtil.class);
    
    public enum UrlType { 
        Normal, Source, Binary;
    }

    public static String getUrl(IDiagramProfile profile, String location) { 
        // Generate URL
        String url = ExternalInfo.getExternalProtocol(profile)
                + "://"
                + ExternalInfo.getExternalHost(profile)
                + "/"
                + profile.getExternalLoadURLSubdomain().substring(0, profile.getExternalLoadURLSubdomain().indexOf("/"))
                + location;
        return url;
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
        String url = GuvnorUtil.getUrl(profile, "/rest/packages/" + urlPackageName );
                
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

    public static String getUrl(IDiagramProfile profile, String packageName, String assetName, UrlType urlType) { 
        return getUrl(profile, packageName, assetName, null, urlType);
    }

    /** 
     * Connection methods
     */
    
    private static final int READ_TIME_OUT = 5 * 1000;
    
    public static void applyAuth(IDiagramProfile profile, HttpURLConnection connection) {
        if (profile.getUsr() != null && profile.getUsr().trim().length() > 0
                && profile.getPwd() != null 
                && profile.getPwd().trim().length() > 0) {
            String auth = profile.getUsr() + ":" + profile.getPwd();
            String encodedAuthorization = Base64.encodeBase64String(auth.getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
        }
    }
    
    public static String readStringContentFromUrl(String urlLocation, String requestMethod, IDiagramProfile profile) throws Exception {
        return readStringContentFromUrl(urlLocation, requestMethod, profile, true);
    }
    
    public static String readStringContentFromUrl(String urlLocation, String requestMethod, IDiagramProfile profile, boolean addLineBreaks) throws Exception {
        InputStream connectionInputStream = readStreamFromUrl(urlLocation, requestMethod, profile);
        BufferedReader sreader = new BufferedReader(new InputStreamReader(connectionInputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        while ((line = sreader.readLine()) != null) {
            stringBuilder.append(line);
            if( addLineBreaks ) { 
                stringBuilder.append("\n");
            }
        }

        return stringBuilder.toString();
    }
    
    public static InputStream readStreamFromUrl(String urlLocation, String requestMethod, IDiagramProfile profile) throws Exception {
        URL url = new URL(urlLocation);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml,application/json,application/octet-stream,text/json,text/plain;q=0.9,*/*;q=0.8");

        connection.setRequestProperty("charset", "UTF-8");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setReadTimeout(READ_TIME_OUT);

        applyAuth(profile, connection);

        connection.connect();

        return connection.getInputStream();
    }
    
    
    public static boolean readCheckAssetExists(String url, IDiagramProfile profile) throws IOException {
        return readCheckAssetExists(url, null, profile);
    }
    
    public static boolean readCheckAssetExists(String urlString, Integer timeOut, IDiagramProfile profile) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        applyAuth(profile, conn);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("charset", "UTF-8");
        conn.setRequestProperty("Accept", "application/atom+xml");
        if( timeOut != null ) { 
            conn.setConnectTimeout(timeOut);
        }
        conn.connect();
        
        int respCode = conn.getResponseCode();
        _logger.debug("check connection response code: " + respCode);
        
        // Originally found in JbpmPreprocessingUnit.setupThemes(...)
        if(respCode == 404) {
            conn.disconnect();
        } 
        
        if(respCode == 200) {
            return true;
        } else { 
            return false;
        }
    }
    
    public static boolean deleteAsset(String urlString, IDiagramProfile profile) throws IOException { 
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        applyAuth(profile, conn);
        conn.setRequestMethod("DELETE");
        conn.connect();
        
        int respCode = conn.getResponseCode();
	    _logger.debug("delete connection response code: " + respCode );
        
	    //TODO : throw exception!
	    if( respCode < 300 && respCode >= 200 ) { 
	        return true;
	    }
	    return false;
    }
    
    public static boolean createAsset(String urlString, String assetName, String assetFormat, byte [] contentBytes, IDiagramProfile profile) throws IOException { 
        URL createURL = new URL(urlString);
        HttpURLConnection createConnection = (HttpURLConnection) createURL.openConnection();
        applyAuth(profile, createConnection);
        createConnection.setRequestMethod("POST");
        createConnection.setRequestProperty("Content-Type", "application/octet-stream");
        createConnection.setRequestProperty("Accept", "application/atom+xml");
        assetName = URLEncoder.encode(assetName, "UTF-8");
        if( ! assetFormat.isEmpty() && ! assetFormat.startsWith(".") ) { 
            assetFormat = "." + assetFormat;
        }
        createConnection.setRequestProperty("Slug", assetName + assetFormat);
        createConnection.setDoOutput(true);
        createConnection.getOutputStream().write(contentBytes);
        createConnection.connect();
        
        int respCode = createConnection.getResponseCode();
        _logger.debug("create connection response code: " + respCode ); 
        
        //TODO: throw exception!
        if( respCode == 200 ) { 
            return true;
        } else { 
            return false;
        }
        
    }
    
}
