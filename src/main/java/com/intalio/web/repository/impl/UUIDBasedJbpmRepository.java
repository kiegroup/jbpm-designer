package com.intalio.web.repository.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.intalio.web.profile.IDiagramProfile;
import com.intalio.web.repository.IUUIDBasedRepository;

public class UUIDBasedJbpmRepository implements IUUIDBasedRepository {

    private static final Logger _logger = Logger.getLogger(UUIDBasedJbpmRepository.class);
    private static final String EXTERNAL_PROTOCOL = "oryx.external.protocol";
    private static final String EXTERNAL_HOST = "oryx.external.host";
    
    private final static String DEFAULTS_PATH = "defaults";
    
    private String _defaultsPath;
    
    public void configure(HttpServlet servlet) {
        _defaultsPath = servlet.getServletContext().getRealPath("/" + DEFAULTS_PATH);
    }

    public byte[] load(HttpServletRequest req, String uuid, IDiagramProfile profile) {
        String processjson = "";
        
        try {
            // check with Guvnor to see what it has for this uuid for us
            String processxml = doHttpUrlConnectionAction(buildExternalLoadURL(profile, uuid));
            if(processxml != null && processxml.length() > 0) {
                processjson = profile.createUnmarshaller().parseModel(processxml, profile);
                return processjson.getBytes("UTF-8");
            } else {
                //return displayDefaultProcess();
                return new byte[0];
            }
        } catch (Exception e) {
            _logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void save(HttpServletRequest req, String uuid, String json,
            String svg, IDiagramProfile profile, Boolean autosave) {
        // Guvnor is responsible for saving 
    }
    
    private String buildExternalLoadURL(IDiagramProfile profile, String uuid) {
        StringBuffer buff = new StringBuffer();
        //check override system prop
        if(!isEmpty(System.getProperty(EXTERNAL_PROTOCOL))) {
            buff.append(System.getProperty(EXTERNAL_PROTOCOL));
        } else {
            buff.append(profile.getExternalLoadURLProtocol());
        }
        
        buff.append("://");
        
        String externalHostSysProp = System.getProperty(EXTERNAL_HOST);
        if(!isEmpty(externalHostSysProp)) {
            if(externalHostSysProp.startsWith("/")){
                externalHostSysProp = externalHostSysProp.substring(1);
            }
            if(externalHostSysProp.endsWith("/")) {
                externalHostSysProp = externalHostSysProp.substring(0,externalHostSysProp.length() - 1);
            }
            buff.append(externalHostSysProp);
        } else {
            buff.append(profile.getExternalLoadURLHostname());
        }
        
        buff.append("/");
        buff.append(profile.getExternalLoadURLSubdomain());
        buff.append("?uuid=").append(uuid);
        buff.append("&usr=").append(profile.getUsr());
        buff.append("&pwd=").append(profile.getPwd());
        
        return buff.toString();
    }
    
    public String toXML(String json, IDiagramProfile profile) {
        return profile.createMarshaller().parseModel(json);
    }

    private String doHttpUrlConnectionAction(String desiredUrl) throws Exception {
      URL url = null;
      BufferedReader reader = null;
      StringBuilder stringBuilder;

      try {
        url = new URL(desiredUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/xml"); 
        connection.setRequestProperty("charset", "UTF-8");
        connection.setReadTimeout(5*1000);
        connection.connect();

        reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        stringBuilder = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null) {
          stringBuilder.append(line + "\n");
        }
        return stringBuilder.toString();
      } catch (Exception e) {
          _logger.error("Unable to connect to Gunvor. Is it running? [" + e.getMessage() + "]");
          // don't blow up, we will just show the default process
          return "";
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException ioe) {
              _logger.error("Unable to read from Gunvor. [" + ioe.getMessage() + "]");
              // don't blow up, we will just show the default process
              return "";
          }
        }
      }
    }

    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the String.
     * That functionality is available in isBlank().</p>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    private boolean isEmpty(final CharSequence str) {
        if ( str == null || str.length() == 0 ) {
            return true;
        }
        
        for ( int i = 0, length = str.length(); i < length; i++ ){
            if ( str.charAt( i ) != ' ' )  {
                return false;
            }
        }
        
        return true;
    }
}
