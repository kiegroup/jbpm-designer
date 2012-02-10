package org.jbpm.designer.web.profile.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.JsonParseException;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.emf.common.util.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonMarshaller;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonUnmarshaller;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.web.plugin.IDiagramPlugin;
import org.jbpm.designer.web.plugin.impl.PluginServiceImpl;
import org.jbpm.designer.web.profile.IDiagramProfile;


/**
 * The implementation of the jBPM profile for Process Designer.
 * @author Tihomir Surdilovic
 */
public class JbpmProfileImpl implements IDiagramProfile {
    
    private static Logger _logger = LoggerFactory.getLogger(JbpmProfileImpl.class);
    
    private Map<String, IDiagramPlugin> _plugins = new LinkedHashMap<String, IDiagramPlugin>();

    private String _stencilSet;
    private String _externalLoadHost;
    private String _externalLoadProtocol;
    private String _externalLoadSubdomain;
    private String _usr;
    private String _pwd;
    private String _serviceRepositoryLocation;
    
    public JbpmProfileImpl(ServletContext servletContext) {
        this(servletContext, true);
    }
    
    public JbpmProfileImpl(ServletContext servletContext, boolean initializeLocalPlugins) {
        if (initializeLocalPlugins) {
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
                        append("/").append("profiles").append("/").append("jbpm.xml").toString());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(fileStream);
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
                    } else if ("externalloadurl".equals(reader.getLocalName())) {
                        for (int i = 0 ; i < reader.getAttributeCount() ; i++) {
                            if ("protocol".equals(reader.getAttributeLocalName(i))) {
                                String extProtocol = reader.getAttributeValue(i);
                                if(!isEmpty(extProtocol)) {
                                    _externalLoadProtocol = extProtocol;
                                } else {
                                    _logger.info("Invalid protocol specified");
                                }
                                _externalLoadProtocol = reader.getAttributeValue(i);
                            }
                            if ("host".equals(reader.getAttributeLocalName(i))) {
                                String exthost = reader.getAttributeValue(i);
                                if(!isEmpty(exthost)) {
                                    _externalLoadHost = exthost;
                                } else {
                                   _logger.info("Invalid host specified");
                                }
                            }
                            if ("subdomain".equals(reader.getAttributeLocalName(i))) {
                                String extsub = reader.getAttributeValue(i);
                                if(!isEmpty(extsub)) {
                                    if(extsub.startsWith("/")) {
                                        extsub = extsub.substring(1);
                                    } 
                                    if(extsub.endsWith("/")) {
                                        extsub = extsub.substring(0,extsub.length() - 1);
                                    }
                                    _externalLoadSubdomain = extsub;
                                } else {
                                    _logger.info("Invalid subdomain specified");
                                }
                            }
                            if ("usr".equals(reader.getAttributeLocalName(i))) {
                                String extUsr = reader.getAttributeValue(i);
                                if(!isEmpty(extUsr)) {
                                    _usr = extUsr;
                                } else {
                                    _logger.info("Invalid usr specified");
                                }
                            }
                            if ("pwd".equals(reader.getAttributeLocalName(i))) {
                                // allow any value for pwd
                                _pwd = reader.getAttributeValue(i);
                            }
                        }
                    } else if("servicerepository".equals(reader.getLocalName())) {
                    	for (int i = 0 ; i < reader.getAttributeCount() ; i++) {
                    		if ("location".equals(reader.getAttributeLocalName(i))) {
                    			String location = reader.getAttributeValue(i);
                    			if(!isEmpty(location)) {
                                    _serviceRepositoryLocation = location;
                                } else {
                                    _logger.info("Invalid service repo location specified");
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
      
    public String getExternalLoadURLProtocol() {
        return _externalLoadProtocol;
    }

    public String getExternalLoadURLHostname() {
        return _externalLoadHost;
    }

    public String getExternalLoadURLSubdomain() {
        return _externalLoadSubdomain;
    }

    public String getUsr() {
        return _usr;
    }

    public String getPwd() {
        return _pwd;
    }
    
    public String getServiceRepositoryLocation() {
    	return _serviceRepositoryLocation;
    }
    
    public IDiagramMarshaller createMarshaller() {
        return new IDiagramMarshaller() {
            public String parseModel(String jsonModel, String preProcessingData) {
                Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                JBPMBpmn2ResourceImpl res;
                try {
                    res = (JBPMBpmn2ResourceImpl) unmarshaller.unmarshall(jsonModel, preProcessingData);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    res.save(outputStream, new HashMap<Object, Object>());
                    return StringEscapeUtils.unescapeHtml(outputStream.toString());
                } catch (JsonParseException e) {
                    _logger.error(e.getMessage(), e);
                } catch (IOException e) {
                    _logger.error(e.getMessage(), e);
                }
                return "";
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
			
			public Resource getResource(String jsonModel, String preProcessingData) {
				try {
					Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
					return (JBPMBpmn2ResourceImpl) unmarshaller.unmarshall(jsonModel, preProcessingData);
				} catch (JsonParseException e) {
					_logger.error(e.getMessage(), e);
				} catch (IOException e) {
					_logger.error(e.getMessage(), e);
				}
				return null;
			}
        };
    }

    public IDiagramUnmarshaller createUnmarshaller() {
        return new IDiagramUnmarshaller() {
            public String parseModel(String xmlModel, IDiagramProfile profile, String preProcessingData) {
                Bpmn2JsonMarshaller marshaller = new Bpmn2JsonMarshaller();
                marshaller.setProfile(profile);
                try {
                    return marshaller.marshall(getDefinitions(xmlModel), preProcessingData);
                } catch (Exception e) {
                    _logger.error(e.getMessage(), e);
                }
                return "";
            }
        };
    }
    
    public Definitions getDefinitions(String xml) {
        try {
            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new JBPMBpmn2ResourceFactoryImpl());
            resourceSet.getPackageRegistry().put("http://www.omg.org/spec/BPMN/20100524/MODEL", Bpmn2Package.eINSTANCE);
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
                    System.out.println("Warning: "+diagnostic.getMessage());
                }
            }
            
            EList<Diagnostic> errors = resource.getErrors();
            if (errors != null && !errors.isEmpty()){
                for (Diagnostic diagnostic : errors) {
                    System.out.println("Error: "+diagnostic.getMessage());
                }
                throw new IllegalStateException("Error parsing process definition");
            }
            
            return ((DocumentRoot) resource.getContents().get(0)).getDefinitions();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public String getStencilSetURL() {
        return "/designer/stencilsets/bpmn2.0jbpm/bpmn2.0jbpm.json";
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

