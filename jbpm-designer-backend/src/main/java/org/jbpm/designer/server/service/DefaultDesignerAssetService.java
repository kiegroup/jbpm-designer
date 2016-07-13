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

package org.jbpm.designer.server.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.RepositoryDescriptor;
import org.jbpm.designer.service.BPMN2DataServices;
import org.jbpm.designer.service.DesignerAssetService;
import org.jbpm.designer.service.DesignerContent;
import org.jbpm.designer.util.Utils;
import org.json.JSONArray;
import org.kie.workbench.common.services.backend.service.KieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class DefaultDesignerAssetService
        extends KieService<DesignerContent>
        implements DesignerAssetService {

    private static Logger logger = LoggerFactory.getLogger(DefaultDesignerAssetService.class);

    @Inject
    private Repository repository;
    
    @Inject
    private Instance<BPMN2DataServices> bpmn2DataServices;

    @Inject
    private SessionInfo sessionInfo;

    @Inject
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private RepositoryDescriptor descriptor;
   
    // socket buffer size in bytes: can be tuned for performance
    private final static int socketBufferSize = 8 * 1024;

    private static final Logger _logger =
            LoggerFactory.getLogger(DefaultDesignerAssetService.class);

    public static final String PROCESS_STUB = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
    "<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.omg.org/bpmn20\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:bpsim=\"http://www.bpsim.org/schemas/1.0\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:drools=\"http://www.jboss.org/drools\" \n" +
        "id=\"Definition\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd\" expressionLanguage=\"http://www.mvel.org/2.0\" targetNamespace=\"http://www.omg.org/bpmn20\" typeLanguage=\"http://www.java.com/javaTypes\"> \n" +
    "   <bpmn2:process id=\"${processid}\" drools:packageName=\"${packageName}\" drools:version=\"1.0\" name=\"\" isExecutable=\"true\"> \n" +
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

    @Override
    public DesignerContent loadContent(Path path) {
        return super.loadContent(path);
    }

    @Override
    public Map<String, String> getEditorParameters( final Path path,
                                  final String editorID,
                                  String hostInfo,
                                  PlaceRequest place ) {
        List<String> activeNodesList = new ArrayList<String>();
        String activeNodesParam = place.getParameter( "activeNodes", null );

        boolean readOnly = place.getParameter( "readOnly", null ) != null;

        if(!readOnly) {
            try {
                ioService.getFileSystem(URI.create(path.toURI()));
            } catch(Exception e) {
                logger.error("Unable to create file system: " + e.getMessage());
                throw new FileSystemNotFoundException(e.getMessage());
            }
        }


        String processId = place.getParameter( "processId", "" );
        String deploymentId = place.getParameter( "deploymentId", "" );
        String encodedProcessSource = "";
        try {
            encodedProcessSource = bpmn2DataServices.iterator().next().getProcessSources(deploymentId,  processId );
        } catch(Exception e) {
            encodedProcessSource = place.getParameter( "encodedProcessSource", "" );
        }
        
        if ( activeNodesParam != null ) {
            activeNodesList = Arrays.asList( activeNodesParam.split( "," ) );
        }

        List<String> completedNodesList = new ArrayList<String>();
        String completedNodesParam = place.getParameter( "completedNodes", null );

        if ( completedNodesParam != null ) {
            completedNodesList = Arrays.asList( completedNodesParam.split( "," ) );
        }

        JSONArray activeNodesArray = new JSONArray( activeNodesList );
//        String encodedActiveNodesParam;
//        try {
//            encodedActiveNodesParam = Base64.encodeBase64URLSafeString( activeNodesArray.toString().getBytes( "UTF-8" ) );
//        } catch ( UnsupportedEncodingException e ) {
//            encodedActiveNodesParam = "";
//        }

        JSONArray completedNodesArray = new JSONArray( completedNodesList );
//        String encodedCompletedNodesParam;
//        try {
//            encodedCompletedNodesParam = Base64.encodeBase64URLSafeString( completedNodesArray.toString().getBytes( "UTF-8" ) );
//        } catch ( UnsupportedEncodingException e ) {
//            encodedCompletedNodesParam = "";
//        }

        Map<String, String> editorParamsMap = new HashMap<String, String>();
        editorParamsMap.put("hostinfo", hostInfo);
        try {
            editorParamsMap.put("uuid", Base64.encodeBase64URLSafeString(UriUtils.decode(path.toURI()).getBytes("UTF-8")));
        } catch(UnsupportedEncodingException e) {

        }
        editorParamsMap.put("profile", "jbpm");
        editorParamsMap.put("pp", "");
        editorParamsMap.put("editorid", editorID);
        editorParamsMap.put("readonly", String.valueOf(readOnly));
        editorParamsMap.put("activenodes", activeNodesArray.toString());
        editorParamsMap.put("completednodes", completedNodesArray.toString());
        editorParamsMap.put("processsource", encodedProcessSource);

        //Signal opening to interested parties if we are not in readonly mode
        if(!readOnly) {
            resourceOpenedEvent.fire(new ResourceOpenedEvent( path, sessionInfo ));
        }

        return editorParamsMap;
    }

    @Override
    public String getEditorID() {
        return UUID.randomUUID().toString().replaceAll( "-", "" );
    }

    @Override
    public Path createProcess( final Path context,
                               final String fileName ) {

        final Path path = Paths.convert( Paths.convert( context ).resolve( fileName ) );
        String location = Paths.convert( path ).getParent().toString();
        String name = path.getFileName();
        String processId = buildProcessId( location, name );
        String packageName = buildPackageName(location, name);


        String processContent = PROCESS_STUB.replaceAll( "\\$\\{processid\\}", processId.replaceAll("\\s", "") )
                                            .replaceAll("\\$\\{packageName\\}", packageName.replaceAll("\\s", ""));

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder( name );
        builder.location( location ).content( processContent ).uniqueId( path.toURI() );
        Asset<String> processAsset = builder.getAsset();

        repository.createAsset( processAsset );
        return path;
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
        name = Utils.toBPMNIdentifier(name);
        return location + "." + name;
    }

    private String buildPackageName( String location, String name ) {
        // replace file name in case it exists
        String packageName = location.replaceFirst("/"+name, "")
        // replace project and resources structure
        .replaceFirst(".*/src/main/resources", "")
        // replace  with . to form package name
        .replaceAll("/", ".");
        // lastly if there is . at the beginning just remove it
        if (packageName.startsWith(".")) {
            packageName = packageName.substring(1);
        }

        return packageName;
    }

    @Override
    public void updateMetadata( final Path resource, final Metadata metadata ) {
        ioService.setAttributes( Paths.convert( resource ),
                metadataService.setUpAttributes( resource, metadata ) );
    }

    @Override
    protected DesignerContent constructContent(Path path, Overview overview) {
        return new DesignerContent(overview);
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
