package org.jbpm.designer.server.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.service.DesignerAssetService;
import org.json.JSONArray;
import org.uberfire.backend.vfs.Path;
import org.uberfire.shared.mvp.PlaceRequest;

@Service
@ApplicationScoped
public class DefaultDesignerAssetService implements DesignerAssetService {
    @Inject
    private Repository repository;
    private static final Object PROCESS_STUB = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
            "<definitions id=\"Definition\"\n" +
            "             targetNamespace=\"http://www.jboss.org/drools\"\n" +
            "             typeLanguage=\"http://www.java.com/javaTypes\"\n" +
            "             expressionLanguage=\"http://www.mvel.org/2.0\"\n" +
            "             xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n" +
            "             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "             xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\"\n" +
            "             xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n" +
            "             xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n" +
            "             xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n" +
            "             xmlns:tns=\"http://www.jboss.org/drools\">\n" +
            "  <process processType=\"Private\" isExecutable=\"true\" id=\"\" name=\"\"  >\n" +
            "  </process>\n" +
            "  <bpmndi:BPMNDiagram>\n" +
            "    <bpmndi:BPMNPlane bpmnElement=\"\" >\n" +
            "    </bpmndi:BPMNPlane>\n" +
            "  </bpmndi:BPMNDiagram>" +
            "</definitions>";

    @Override
    public String loadEditorBody( final Path path, final String editorID, String hostInfo, PlaceRequest place) {
        List<String> activeNodesList = new ArrayList<String>();
        String activeNodesParam = place.getParameter("activeNodes", null);
        if(activeNodesParam != null) {
            activeNodesList = Arrays.asList(activeNodesParam.split(","));
        }

        JSONArray activeNodesArray = new JSONArray(activeNodesList);
        String encodedActiveNodesParam;
        try {
            encodedActiveNodesParam = Base64.encodeBase64URLSafeString(activeNodesArray.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            encodedActiveNodesParam = "";
        }

        String editorURL = hostInfo + "/editor/?uuid=" + path.toURI() + "&profile=jbpm&pp=&editorid=" + editorID + "&activenodes=" + encodedActiveNodesParam;
        return getEditorResponse(editorURL);
    }

    @Override
    public String getEditorID() {
        // TODO - fix this so it is not always "Definition"
        return "Definition";
        //return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public void createProcess(Path path) {

        String fileName = path.getFileName();
        String location = path.toURI().replaceFirst("/" + fileName, "");
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(path.getFileName());
        builder.location(location).content(PROCESS_STUB);
        Asset<String> processAsset = builder.getAsset();

        repository.createAsset(processAsset);
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
