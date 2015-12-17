/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.web.batikprotocolhandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.apache.batik.util.ParsedURLData;
import org.jbpm.designer.web.profile.IDiagramProfile;

import org.apache.commons.codec.binary.Base64;
import org.jbpm.designer.web.profile.impl.RepositoryInfo;

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
        if (RepositoryInfo.getRepositoryUsr(profile) != null && RepositoryInfo.getRepositoryUsr(profile).trim().length() > 0
                && RepositoryInfo.getRepositoryPwd(profile) != null
                && RepositoryInfo.getRepositoryPwd(profile).trim().length() > 0) {
            String userpassword = RepositoryInfo.getRepositoryUsr(profile) + ":" + RepositoryInfo.getRepositoryPwd(profile);
            String encodedAuthorization = Base64.encodeBase64String(userpassword.getBytes());
            connection.setRequestProperty("Authorization", "Basic "
                    + encodedAuthorization);
        }
    }
}
