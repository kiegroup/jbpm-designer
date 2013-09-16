package org.jbpm.designer.server.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.service.DesignerAssetService;
import org.jbpm.designer.util.OSProtocolSocketFactory;
import org.json.JSONArray;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.ResourceAddedEvent;

@Service
@ApplicationScoped
public class DefaultDesignerAssetService implements DesignerAssetService {

    @Inject
    private Paths paths;
    @Inject
    private Repository repository;
    @Inject
    private Event<ResourceAddedEvent> resourceAddedEvent;

    private static final String PROCESS_STUB = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
    "<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.omg.org/bpmn20\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:bpsim=\"http://www.bpsim.org/schemas/1.0\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:drools=\"http://www.jboss.org/drools\" \n" +
        "id=\"Definition\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd\" expressionLanguage=\"http://www.mvel.org/2.0\" targetNamespace=\"http://www.omg.org/bpmn20\" typeLanguage=\"http://www.java.com/javaTypes\"> \n" +
    "   <bpmn2:process id=\"${processid}\" drools:packageName=\"org.jbpm\" drools:version=\"1.0\" name=\"\" isExecutable=\"true\"> \n" +
    "      <bpmn2:startEvent id=\"processStartEvent\" drools:bgcolor=\"#9acd32\" drools:selectable=\"true\" name=\"\"/> \n" +
    "   </bpmn2:process> \n" +
    "   <bpmndi:BPMNDiagram> \n" +
    "      <bpmndi:BPMNPlane bpmnElement=\"${processid}\"> \n" +
    "         <bpmndi:BPMNShape bpmnElement=\"processStartEvent\"> \n" +
    "            <dc:Bounds height=\"30.0\" width=\"30.0\" x=\"120.0\" y=\"165.0\"/> \n" +
    "         </bpmndi:BPMNShape> \n" +
    "      </bpmndi:BPMNPlane> \n" +
    "   </bpmndi:BPMNDiagram> \n" +
    "</bpmn2:definitions>";


    @PostConstruct
    public void configure() {
        Protocol.registerProtocol("http", new Protocol("http", new OSProtocolSocketFactory(), 80));
    }

    @Override
    public Map<String, String> getEditorParameters( final Path path,
                                  final String editorID,
                                  String hostInfo,
                                  PlaceRequest place ) {
        List<String> activeNodesList = new ArrayList<String>();
        String activeNodesParam = place.getParameter( "activeNodes", null );

        String readOnly = place.getParameter( "readOnly", "false" );
        String encodedProcessSource = place.getParameter( "encodedProcessSource", "" );

        if ( activeNodesParam != null ) {
            activeNodesList = Arrays.asList( activeNodesParam.split( "," ) );
        }

        List<String> completedNodesList = new ArrayList<String>();
        String completedNodesParam = place.getParameter( "completedNodes", null );

        if ( completedNodesParam != null ) {
            completedNodesList = Arrays.asList( completedNodesParam.split( "," ) );
        }

        JSONArray activeNodesArray = new JSONArray( activeNodesList );
        String encodedActiveNodesParam;
        try {
            encodedActiveNodesParam = Base64.encodeBase64URLSafeString( activeNodesArray.toString().getBytes( "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            encodedActiveNodesParam = "";
        }

        JSONArray completedNodesArray = new JSONArray( completedNodesList );
        String encodedCompletedNodesParam;
        try {
            encodedCompletedNodesParam = Base64.encodeBase64URLSafeString( completedNodesArray.toString().getBytes( "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            encodedCompletedNodesParam = "";
        }

        Map<String, String> editorParamsMap = new HashMap<String, String>();
        editorParamsMap.put("hostinfo", hostInfo);
        editorParamsMap.put("uuid", path.toURI());
        editorParamsMap.put("profile", "jbpm");
        editorParamsMap.put("pp", "");
        editorParamsMap.put("editorid", editorID);
        editorParamsMap.put("readonly", readOnly);
        editorParamsMap.put("activenodes", encodedActiveNodesParam);
        editorParamsMap.put("completednodes", encodedCompletedNodesParam);
        editorParamsMap.put("processsource", encodedProcessSource);

        return editorParamsMap;
//        String editorURL = hostInfo + "/editor/?uuid=" + path.toURI() + "&profile=jbpm&pp=&editorid=" + editorID + "&readonly=" + readOnly +
//                "&activenodes=" + encodedActiveNodesParam + "&completednodes=" + encodedCompletedNodesParam;
//        return getEditorResponse( editorURL, encodedProcessSource );
    }

    @Override
    public String getEditorID() {
        return UUID.randomUUID().toString().replaceAll( "-", "" );
    }

    @Override
    public Path createProcess( final Path context,
                               final String fileName ) {
        final Path path = paths.convert( paths.convert( context ).resolve( fileName ), false );

        String location = paths.convert( path ).getParent().toString();
        String name = path.getFileName();
        String processId = buildProcessId( location, name );

        String processContent = PROCESS_STUB.replaceAll( "\\$\\{processid\\}", processId );

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder( name );
        builder.location( location ).content( processContent ).uniqueId( path.toURI() );
        Asset<String> processAsset = builder.getAsset();

        repository.createAsset( processAsset );

        resourceAddedEvent.fire( new ResourceAddedEvent( path ) );

        return path;
    }

    private String getEditorResponse( String urlpath,
                                      String encProcessSrc ) {
        HttpClient httpclient = new HttpClient();

        PostMethod authMethod = new PostMethod( urlpath );
        NameValuePair[] data = { new NameValuePair( "j_username", "admin" ),
                new NameValuePair( "j_password", "admin" ) };
        authMethod.setRequestBody( data );
        try {
            httpclient.executeMethod( authMethod );
        } catch ( IOException e ) {
            e.printStackTrace();
            return null;
        } finally {
            authMethod.releaseConnection();
        }

        PostMethod theMethod = new PostMethod( urlpath );
        theMethod.setParameter( "processsource", encProcessSrc );
        StringBuffer sb = new StringBuffer();
        try {
            httpclient.executeMethod( theMethod );
            sb.append( theMethod.getResponseBodyAsString() );
            return sb.toString();

        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        } finally {
            theMethod.releaseConnection();
        }
    }

    private String buildProcessId( String location,
                                   String name ) {
        if ( location.startsWith( "/" ) ) {
            location = location.replaceFirst( "/", "" );
        }
        location = location.replaceAll( "/", "." );

        if(location.length() > 0) {
            String[] locationParts = location.split("\\.");
            location = locationParts[0];
        }

        name = name.substring( 0, name.lastIndexOf( "." ) );

        return location + "." + name.replaceAll("\\s", "");
    }

}
