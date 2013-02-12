package org.jbpm.designer.web.profile.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletContext;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.jackson.JsonParseException;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.emf.ecore.resource.Resource;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonUnmarshaller;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.epn.impl.EpnJsonMarshaller;
import org.jbpm.designer.epn.impl.EpnJsonUnmarshaller;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.web.plugin.IDiagramPlugin;
import org.jbpm.designer.web.plugin.impl.PluginServiceImpl;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The implementation of the epn profile for Process Designer.
 * @author Tihomir Surdilovic
 */
@ApplicationScoped
public class EpnProfileImpl implements IDiagramProfile {

    private static Logger _logger = LoggerFactory.getLogger(EpnProfileImpl.class);
    private Map<String, IDiagramPlugin> _plugins = new LinkedHashMap<String, IDiagramPlugin>();

    private String _stencilSet;
    private String _localHistoryEnabled;
    private String _localHistoryTimeout;
    private String _repositoryId;
    private String _repositoryRoot;
    private String _repositoryName;
    private String _repositoryHost;
    private String _repositoryProtocol;
    private String _repositorySubdomain;
    private String _repositoryUsr;
    private String _repositoryPwd;
    private String _repositoryGlobalDir;

    public EpnProfileImpl() {

    }
    public EpnProfileImpl(ServletContext servletContext) {
        this(servletContext, true);
    }
    
    public EpnProfileImpl(ServletContext servletContext, boolean initializeLocalPlugins) {
        if (initializeLocalPlugins) {
            initializeLocalPlugins(servletContext);
        }
    }
    
    private void initializeLocalPlugins(ServletContext context) {
        Map<String, IDiagramPlugin> registry = PluginServiceImpl.getLocalPluginsRegistry(context);
        //we read the default.xml file and make sense of it.
        FileInputStream fileStream = null;
        try {
            try {
                fileStream = new FileInputStream(new StringBuilder(context.getRealPath("/")).append("/").
                        append(ConfigurationProvider.getInstance().getDesignerContext()).append("profiles").append("/").append("epn.xml").toString());
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
                    } else if ("repository".equals(reader.getLocalName())) {
                        for (int i = 0 ; i < reader.getAttributeCount() ; i++) {
                            if ("id".equals(reader.getAttributeLocalName(i))) {
                                String repositoryId = reader.getAttributeValue(i);
                                if(!isEmpty(repositoryId)) {
                                    _repositoryId = repositoryId;
                                } else {
                                    _logger.info("Invalid repository id specified");
                                }
                            }
                            if ("globaldir".equals(reader.getAttributeLocalName(i))) {
                                String repositoryGlobalDir = reader.getAttributeValue(i);
                                if(!isEmpty(repositoryGlobalDir)) {
                                    _repositoryGlobalDir = repositoryGlobalDir;
                                } else {
                                    _repositoryGlobalDir = "repository";
                                }
                            }
                            if ("root".equals(reader.getAttributeLocalName(i))) {
                                String repositoryRoot = reader.getAttributeValue(i);
                                if(!isEmpty(repositoryRoot)) {
                                    _repositoryRoot = repositoryRoot;
                                }
                            }
                            if ("name".equals(reader.getAttributeLocalName(i))) {
                                String repositoryName = reader.getAttributeValue(i);
                                if(!isEmpty(repositoryName)) {
                                    _repositoryName = repositoryName;
                                }
                            }
                            if ("protocol".equals(reader.getAttributeLocalName(i))) {
                                String repositoryProtocol = reader.getAttributeValue(i);
                                if(!isEmpty(repositoryProtocol)) {
                                    _repositoryProtocol = repositoryProtocol;
                                }
                            }
                            if ("host".equals(reader.getAttributeLocalName(i))) {
                                String repositoryHost = reader.getAttributeValue(i);
                                if(!isEmpty(repositoryHost)) {
                                    _repositoryHost = repositoryHost;
                                }
                            }
                            if ("subdomain".equals(reader.getAttributeLocalName(i))) {
                                String repositorySubdomain = reader.getAttributeValue(i);
                                if(!isEmpty(repositorySubdomain)) {
                                    if(repositorySubdomain.startsWith("/")) {
                                        repositorySubdomain = repositorySubdomain.substring(1);
                                    }
                                    if(repositorySubdomain.endsWith("/")) {
                                        repositorySubdomain = repositorySubdomain.substring(0, repositorySubdomain.length() - 1);
                                    }
                                    _repositorySubdomain = repositorySubdomain;
                                }
                            }
                            if ("usr".equals(reader.getAttributeLocalName(i))) {
                                String repositoryUsr = reader.getAttributeValue(i);
                                if(!isEmpty(repositoryUsr)) {
                                    _repositoryUsr = repositoryUsr;
                                }
                            }
                            if ("pwd".equals(reader.getAttributeLocalName(i))) {
                                // allow any value for pwd
                                _repositoryPwd = reader.getAttributeValue(i);
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
        return "epn";
    }

    public String getTitle() {
        return "EPN Designer";
    }

    public String getStencilSet() {
        return _stencilSet;
    }

    public Collection<String> getStencilSetExtensions() {
        return Collections.emptyList();
    }

    public String getSerializedModelExtension() {
        return "epn";
    }

    public String getStencilSetURL() {
        return "/designer/stencilsets/epn/epn.json";
    }

    public String getStencilSetNamespaceURL() {
        return "http://b3mn.org/stencilset/epn#";
    }

    public String getStencilSetExtensionURL() {
        return "http://oryx-editor.org/stencilsets/extensions/epn#";
    }

    public Collection<String> getPlugins() {
        return Collections.unmodifiableCollection(_plugins.keySet());
    }

    public IDiagramMarshaller createMarshaller() {
        return new IDiagramMarshaller() {
            public String parseModel(String jsonModel, String preProcessingData) {
                EpnJsonUnmarshaller unmarshaller = new EpnJsonUnmarshaller();
                Object def; //TODO will be replaced with the epn ecore model class (definitions)
                try {
                    def = unmarshaller.unmarshall(jsonModel);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    //TODO do something now with the model (save it!);
                    return outputStream.toString();
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
                EpnJsonMarshaller marshaller = new EpnJsonMarshaller();
                marshaller.setProfile(profile);
                try {
                    return marshaller.marshall(""); // TODO FIX THIS!
                } catch (Exception e) {
                    _logger.error(e.getMessage(), e);
                }
                return "";
            }
        };
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

    public String getRepositoryId() {
        return _repositoryId;
    }

    public String getRepositoryRoot() {
        return _repositoryRoot;
    }

    public String getRepositoryName() {
        return _repositoryName;
    }

    public String getRepositoryHost() {
        return _repositoryHost;
    }

    public String getRepositoryProtocol() {
        return _repositoryProtocol;
    }

    public String getRepositorySubdomain() {
        return _repositorySubdomain;
    }

    public String getRepositoryUsr() {
        return _repositoryUsr;
    }

    public String getRepositoryPwd() {
        return _repositoryPwd;
    }

    public String getRepositoryGlobalDir() {
        return _repositoryGlobalDir;
    }

    public String getLocalHistoryEnabled() {
        return _localHistoryEnabled;
    }

    public String getLocalHistoryTimeout() {
        return _localHistoryTimeout;
    }

    public Repository getRepository() {
        return null;
    }

    @Override
    public void init(ServletContext context) {
        initializeLocalPlugins(context);
    }
}
