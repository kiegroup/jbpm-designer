package org.jbpm.designer.server.service;

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.service.DesignerAssetService;
import org.uberfire.backend.vfs.Path;

/**
 * [manstis] Default implementation
 */
@Service
@ApplicationScoped
public class DefaultDesignerAssetService implements DesignerAssetService {
    @Inject
    private Repository repository;

    @Override
    public String loadEditorBody( final Path path, final String editorID, String hostInfo) {
        // TODO dont hard-code url here
        String editorURL = hostInfo + "/editor/?uuid=" + path.toURI() + "&profile=jbpm&pp=&editorid=" + editorID;
        return getEditorResponse(editorURL);
    }

    @Override
    public String getEditorID() {
        // TODO - fix this so it is not always "Definition"
        return "Definition";
        //return UUID.randomUUID().toString().replaceAll("-", "");
    }


    private String getEditorResponse(String urlpath) {
        HttpClient httpclient = new HttpClient();

        PostMethod authMethod = new PostMethod(urlpath);
        NameValuePair[] data = { new NameValuePair("j_username", "admin"),
                new NameValuePair("j_password", "admin") };
        authMethod.setRequestBody(data);
        try {
            httpclient.executeMethod(authMethod);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            authMethod.releaseConnection();
        }

        HttpMethod theMethod = new GetMethod(urlpath);
        StringBuffer sb = new StringBuffer();
        try {
            httpclient.executeMethod(theMethod);
            sb.append(theMethod.getResponseBodyAsString());
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            theMethod.releaseConnection();
        }
    }

}
