package org.jbpm.designer.web.batikprotocolhandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.apache.batik.util.ParsedURLData;
import org.jbpm.designer.web.profile.IDiagramProfile;

import org.apache.commons.codec.binary.Base64;

public class GuvnorParsedURLData extends ParsedURLData {
    private IDiagramProfile profile;
    private String urlStr;
    
    public GuvnorParsedURLData() {}
    
    public GuvnorParsedURLData(IDiagramProfile profile, String urlStr) {
         this.profile = profile;
         this.urlStr = urlStr;
    }
    
    public InputStream openStream(String userAgent, Iterator mimeTypes) throws IOException {
        try {
            return getInputStreamForURL(urlStr, "GET");
        } catch (Exception e) {
            return null;
        } 
    }
    
    private InputStream getInputStreamForURL(String urlLocation,
            String requestMethod) throws Exception {
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
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setReadTimeout(5 * 1000);

        applyAuth(connection);
        connection.connect();
        return connection.getInputStream();
    }
    
    private void applyAuth(HttpURLConnection connection) {
        if (profile.getUsr() != null && profile.getUsr().trim().length() > 0
                && profile.getPwd() != null
                && profile.getPwd().trim().length() > 0) {
            String userpassword = profile.getUsr() + ":" + profile.getPwd();
            String encodedAuthorization = Base64.encodeBase64String(userpassword.getBytes());
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedAuthorization);
        }
    }
}
