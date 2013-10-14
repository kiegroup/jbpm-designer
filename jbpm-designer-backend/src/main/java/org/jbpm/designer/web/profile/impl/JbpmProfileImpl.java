package org.jbpm.designer.web.profile.impl;

import bpsim.impl.BpsimFactoryImpl;
import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.JsonParseException;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonMarshaller;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonUnmarshaller;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.web.plugin.IDiagramPlugin;
import org.jbpm.designer.web.plugin.impl.PluginServiceImpl;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.*;


/**
 * The implementation of the jBPM profile for Process Designer.
 * @author Tihomir Surdilovic
 */
@ApplicationScoped
public class JbpmProfileImpl implements IDiagramProfile {

    private static Logger _logger = LoggerFactory.getLogger(JbpmProfileImpl.class);

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

    private Map<String, IDiagramPlugin> _plugins = new LinkedHashMap<String, IDiagramPlugin>();

    private String _stencilSet;
    private String _localHistoryEnabled;
    private String _localHistoryTimeout;
    private boolean initializeLocalPlugins;
    private String _storeSVGonSaveOption;

    @Inject
    private Repository repository;

    @Inject
    private VFSService vfsServices;

    public JbpmProfileImpl(ServletContext servletContext) {
        this(servletContext, true, false);
    }

    public JbpmProfileImpl() {
        this(null, false, false);
    }

    public JbpmProfileImpl(ServletContext servletContext, boolean initializeLocalPlugins, boolean initializeRepository) {
        if(initializeLocalPlugins) {
            initializeLocalPlugins(servletContext);
        }
    }

    public String getTitle() {
        return "jBPM Process Designer";
    }

    public String getStencilSet() {
        return _stencilSet;
    }

    public Collection<String> getStencilSetExtensions() {
        return Collections.emptyList();
    }

    public Collection<String> getPlugins() {
        return Collections.unmodifiableCollection(_plugins.keySet());
    }

    private void initializeLocalPlugins(ServletContext context) {
        Map<String, IDiagramPlugin> registry = PluginServiceImpl.getLocalPluginsRegistry(context);
        FileInputStream fileStream = null;
        try {
            try {
                fileStream = new FileInputStream(new StringBuilder(context.getRealPath("/")).append("/").
                        append(ConfigurationProvider.getInstance().getDesignerContext()).append("profiles").append("/").append("jbpm.xml").toString());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(fileStream, "UTF-8");
            while(reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if ("profile".equals(reader.getLocalName())) {
                        for (int i = 0 ; i < reader.getAttributeCount() ; i++) {
                            if ("stencilset".equals(reader.getAttributeLocalName(i))) {
                                _stencilSet = reader.getAttributeValue(i);
                            }
                        }
                    } else if ("plugin".equals(reader.getLocalName())) {
                        String name = null;
                        for (int i = 0 ; i < reader.getAttributeCount() ; i++) {
                            if ("name".equals(reader.getAttributeLocalName(i))) {
                                name = reader.getAttributeValue(i);
                            }
                        }
                        _plugins.put(name, registry.get(name));
                    }  else if("localhistory".equals(reader.getLocalName())) {
                        for (int i = 0 ; i < reader.getAttributeCount() ; i++) {
                            if ("enabled".equals(reader.getAttributeLocalName(i))) {
                                String localhistoryenabled = reader.getAttributeValue(i);
                                if(!isEmpty(localhistoryenabled)) {
                                    _localHistoryEnabled = localhistoryenabled;
                                } else {
                                    _logger.info("Invalid local history enabled");
                                }
                            }
                            if ("timeout".equals(reader.getAttributeLocalName(i))) {
                                String localhistorytimeout = reader.getAttributeValue(i);
                                if(!isEmpty(localhistorytimeout)) {
                                    _localHistoryTimeout = localhistorytimeout;
                                } else {
                                    _logger.info("Invalid local history timeout");
                                }
                            }
                        }
                    } else if("storesvgonsave".equals(reader.getLocalName())) {
                        for (int i = 0 ; i < reader.getAttributeCount() ; i++) {
                            if ("enabled".equals(reader.getAttributeLocalName(i))) {
                                String storesvgonsaveenabled = reader.getAttributeValue(i);
                                if(!isEmpty(storesvgonsaveenabled)) {
                                    _storeSVGonSaveOption = storesvgonsaveenabled;
                                } else {
                                    _logger.info("Invalid store svg on save enabled");
                                }
                            }
                        }
                    }
                }
            }
        } catch (XMLStreamException e) {
            _logger.error(e.getMessage(), e);
            throw new RuntimeException(e); // stop initialization
        } finally {
            if (fileStream != null) { try { fileStream.close(); } catch(IOException e) {}};
        }
    }


    public String getName() {
        return "jbpm";
    }

    public String getSerializedModelExtension() {
        return "bpmn";
    }


    public String getLocalHistoryEnabled() {
        return _localHistoryEnabled;
    }

    public String getLocalHistoryTimeout() {
        return _localHistoryTimeout;
    }

    @Override
    public String getStoreSVGonSaveOption() {
        return _storeSVGonSaveOption;
    }

    public Repository getRepository() {
        return repository;
    }

    @Override
    public void init(ServletContext context) {
        if(!initializeLocalPlugins) {
            initializeLocalPlugins(context);
            initializeLocalPlugins = true;
        }
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public IDiagramMarshaller createMarshaller() {
        return new IDiagramMarshaller() {
            public String parseModel(String jsonModel, String preProcessingData) throws Exception {
                DroolsFactoryImpl.init();
                BpsimFactoryImpl.init();
                Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                JBPMBpmn2ResourceImpl res;
                res = (JBPMBpmn2ResourceImpl) unmarshaller.unmarshall(jsonModel, preProcessingData);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                res.save(outputStream, new HashMap<Object, Object>());
                return StringEscapeUtils.unescapeHtml(outputStream.toString("UTF-8"));
            }

            public Definitions getDefinitions(String jsonModel,
                                              String preProcessingData) {
                try {
                    Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                    JBPMBpmn2ResourceImpl res = (JBPMBpmn2ResourceImpl) unmarshaller.unmarshall(jsonModel, preProcessingData);
                    return (Definitions) res.getContents().get(0);
                } catch (JsonParseException e) {
                    _logger.error(e.getMessage(), e);
                } catch (IOException e) {
                    _logger.error(e.getMessage(), e);
                }
                return null;
            }

            public Resource getResource(String jsonModel, String preProcessingData) throws Exception {
                Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                return (JBPMBpmn2ResourceImpl) unmarshaller.unmarshall(jsonModel, preProcessingData);
            }
        };
    }

    public IDiagramUnmarshaller createUnmarshaller() {
        return new IDiagramUnmarshaller() {
            public String parseModel(String xmlModel, IDiagramProfile profile, String preProcessingData) throws Exception {
                DroolsFactoryImpl.init();
                BpsimFactoryImpl.init();

                Bpmn2JsonMarshaller marshaller = new Bpmn2JsonMarshaller();
                marshaller.setProfile(profile);
                return marshaller.marshall(getDefinitions(xmlModel), preProcessingData);
            }
        };
    }

    @Override
    public String getRepositoryGlobalDir() {
        return "/global";
    }

    @Override
    public String getRepositoryGlobalDir(String uuid) {
        if(uuid != null) {
            Path uuidPath = vfsServices.get(UriUtils.encode(uuid) );
            String pathURI = uuidPath.toURI();

            if(pathURI != "/") {
                String[] pathParts = pathURI.split("/");
                try {
                    String pathProjectName = pathParts[3];
                    if(pathProjectName.length() < 1) {
                        return "/global";
                    } else {
                        return "/" + pathProjectName + "/global";
                    }
                } catch(Exception e) {
                    return "/global";
                }
            }

            return "/global";
        }
        return "/global";
    }

    public Definitions getDefinitions(String xml) throws Exception {
        try {
            DroolsFactoryImpl.init();
            BpsimFactoryImpl.init();

            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                    .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new JBPMBpmn2ResourceFactoryImpl());
            resourceSet.getPackageRegistry().put("http://www.omg.org/spec/BPMN/20100524/MODEL", Bpmn2Package.eINSTANCE);
            resourceSet.getPackageRegistry().put("http://www.jboss.org/drools", DroolsPackage.eINSTANCE);

            JBPMBpmn2ResourceImpl resource = (JBPMBpmn2ResourceImpl) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
            resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_ENCODING, "UTF-8");
            resource.setEncoding("UTF-8");
            Map<String, Object> options = new HashMap<String, Object>();
            options.put( JBPMBpmn2ResourceImpl.OPTION_ENCODING, "UTF-8" );
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            resource.load(is, options);

            EList<Diagnostic> warnings = resource.getWarnings();

            if (warnings != null && !warnings.isEmpty()){
                for (Diagnostic diagnostic : warnings) {
                    _logger.info("Warning: "+diagnostic.getMessage());
                }
            }

            EList<Diagnostic> errors = resource.getErrors();
            if (errors != null && !errors.isEmpty()){
                for (Diagnostic diagnostic : errors) {
                   _logger.info("Error: "+diagnostic.getMessage());
                }
                throw new Exception("Error parsing process definition");
            }

            return ((DocumentRoot) resource.getContents().get(0)).getDefinitions();
        } catch(Exception e) {
            throw new Exception(e);
        }
    }

    public String getStencilSetURL() {
        return ConfigurationProvider.getInstance().getDesignerContext() + "stencilsets/bpmn2.0jbpm/bpmn2.0jbpm.json";
    }

    public String getStencilSetNamespaceURL() {
        return "http://b3mn.org/stencilset/bpmn2.0#";
    }

    public String getStencilSetExtensionURL() {
        return "http://oryx-editor.org/stencilsets/extensions/bpmncosts-2.0#";
    }

    private boolean isEmpty(final CharSequence str) {
        if ( str == null || str.length() == 0 ) {
            return true;
        }
        for ( int i = 0, length = str.length(); i < length; i++ ){
            if ( str.charAt( i ) != ' ' ) {
                return false;
            }
        }
        return true;
    }
}
