/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletContext;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import bpsim.impl.BpsimFactoryImpl;
import org.codehaus.jackson.JsonParseException;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonMarshaller;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonUnmarshaller;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.web.plugin.IDiagramPlugin;
import org.jbpm.designer.web.plugin.impl.PluginServiceImpl;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The implementation of the default profile for Process Designer.
 * @author Antoine Toulme
 */
@ApplicationScoped
public class DefaultProfileImpl implements IDiagramProfile {

    private static Logger logger = LoggerFactory.getLogger(DefaultProfileImpl.class);

    private Map<String, IDiagramPlugin> plugins = new LinkedHashMap<String, IDiagramPlugin>();

    private String stencilSet;
    private String localHistoryEnabled;
    private String localHistoryTimeout;
    private String repositoryId;
    private String repositoryRoot;
    private String repositoryName;
    private String repositoryHost;
    private String repositoryProtocol;
    private String repositorySubdomain;
    private String repositoryUsr;
    private String repositoryPwd;
    private String repositoryGlobalDir;
    private String bpsimDisplay;
    private String formsType;

    public DefaultProfileImpl() {

    }

    public DefaultProfileImpl(ServletContext servletContext) {
        this(servletContext,
             true);
    }

    public DefaultProfileImpl(ServletContext servletContext,
                              boolean initializeLocalPlugins) {
        if (initializeLocalPlugins) {
            initializeLocalPlugins(servletContext);
        }
    }

    public String getTitle() {
        return "Process Designer";
    }

    public String getStencilSet() {
        return stencilSet;
    }

    public Collection<String> getStencilSetExtensions() {
        return Collections.emptyList();
    }

    public Collection<String> getPlugins() {
        return Collections.unmodifiableCollection(plugins.keySet());
    }

    private void initializeLocalPlugins(ServletContext context) {
        Map<String, IDiagramPlugin> registry = PluginServiceImpl.getLocalPluginsRegistry(context);
        //we read the default.xml file and make sense of it.
        FileInputStream fileStream = null;
        try {
            try {
                fileStream = new FileInputStream(new StringBuilder(context.getRealPath("/")).append("/").
                        append(ConfigurationProvider.getInstance().getDesignerContext()).append("profiles").append("/").append("default.xml").toString());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(fileStream,
                                                                   "UTF-8");
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if ("profile".equals(reader.getLocalName())) {
                        for (int i = 0; i < reader.getAttributeCount(); i++) {
                            if ("stencilset".equals(reader.getAttributeLocalName(i))) {
                                stencilSet = reader.getAttributeValue(i);
                            }
                        }
                    } else if ("plugin".equals(reader.getLocalName())) {
                        String name = null;
                        for (int i = 0; i < reader.getAttributeCount(); i++) {
                            if ("name".equals(reader.getAttributeLocalName(i))) {
                                name = reader.getAttributeValue(i);
                            }
                        }
                        plugins.put(name,
                                    registry.get(name));
                    }
                }
            }
        } catch (XMLStreamException e) {
            logger.error(e.getMessage(),
                         e);
            throw new RuntimeException(e); // stop initialization
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                }
            }
            ;
        }
    }

    public String getName() {
        return "default";
    }

    public String getSerializedModelExtension() {
        return "bpmn";
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public String getRepositoryRoot() {
        return repositoryRoot;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public String getRepositoryHost() {
        return repositoryHost;
    }

    public String getRepositoryProtocol() {
        return repositoryProtocol;
    }

    public String getRepositorySubdomain() {
        return repositorySubdomain;
    }

    public String getRepositoryUsr() {
        return repositoryUsr;
    }

    public String getRepositoryPwd() {
        return repositoryPwd;
    }

    public String getRepositoryGlobalDir() {
        return repositoryGlobalDir;
    }

    public String getRepositoryGlobalDir(String uuid) {
        return repositoryGlobalDir;
    }

    public String getLocalHistoryEnabled() {
        return localHistoryEnabled;
    }

    public String getLocalHistoryTimeout() {
        return localHistoryTimeout;
    }

    @Override
    public String getStoreSVGonSaveOption() {
        return "false";
    }

    @Override
    public String getBpsimDisplay() {
        return bpsimDisplay;
    }

    @Override
    public String getFormsType() {
        return formsType;
    }

    public Repository getRepository() {
        return null;
    }

    @Override
    public void init(ServletContext context) {
    }

    public IDiagramMarshaller createMarshaller() {
        return new IDiagramMarshaller() {
            public String parseModel(String jsonModel,
                                     String preProcessingData) {
                Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                //Definitions def;
                Resource res;
                try {
                    res = unmarshaller.unmarshall(jsonModel,
                                                  preProcessingData);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    Map saveMap = new HashMap();
                    saveMap.put(XMLResource.OPTION_ENCODING,
                                "UTF-8");
                    saveMap.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION,
                                true);
                    saveMap.put(XMLResource.OPTION_DISABLE_NOTIFY,
                                true);
                    saveMap.put(XMLResource.OPTION_PROCESS_DANGLING_HREF,
                                XMLResource.OPTION_PROCESS_DANGLING_HREF_RECORD);
                    res.save(outputStream,
                             saveMap);
                    return outputStream.toString();
                } catch (JsonParseException e) {
                    logger.error(e.getMessage(),
                                 e);
                } catch (IOException e) {
                    logger.error(e.getMessage(),
                                 e);
                }

                return "";
            }

            public Definitions getDefinitions(String jsonModel,
                                              String preProcessingData) {
                try {
                    Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                    JBPMBpmn2ResourceImpl res = (JBPMBpmn2ResourceImpl) unmarshaller.unmarshall(jsonModel,
                                                                                                preProcessingData);
                    return (Definitions) res.getContents().get(0);
                } catch (JsonParseException e) {
                    logger.error(e.getMessage(),
                                 e);
                } catch (IOException e) {
                    logger.error(e.getMessage(),
                                 e);
                }
                return null;
            }

            public Resource getResource(String jsonModel,
                                        String preProcessingData) {
                try {
                    Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                    return (JBPMBpmn2ResourceImpl) unmarshaller.unmarshall(jsonModel,
                                                                           preProcessingData);
                } catch (JsonParseException e) {
                    logger.error(e.getMessage(),
                                 e);
                } catch (IOException e) {
                    logger.error(e.getMessage(),
                                 e);
                }
                return null;
            }
        };
    }

    public IDiagramUnmarshaller createUnmarshaller() {
        return new IDiagramUnmarshaller() {
            public String parseModel(String xmlModel,
                                     IDiagramProfile profile,
                                     String preProcessingData) {
                Bpmn2JsonMarshaller marshaller = new Bpmn2JsonMarshaller();
                marshaller.setProfile(profile);
                try {
                    return marshaller.marshall(getDefinitions(xmlModel),
                                               preProcessingData);
                } catch (Exception e) {
                    logger.error(e.getMessage(),
                                 e);
                }
                return "";
            }
        };
    }

    private Definitions getDefinitions(String xml) {
        try {
            DroolsFactoryImpl.init();
            BpsimFactoryImpl.init();

            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                    .put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                         new Bpmn2ResourceFactoryImpl());
            resourceSet.getPackageRegistry().put("http://www.omg.org/spec/BPMN/20100524/MODEL",
                                                 Bpmn2Package.eINSTANCE);
            Resource resource = resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            resource.load(is,
                          Collections.EMPTY_MAP);
            resource.load(Collections.EMPTY_MAP);
            return ((DocumentRoot) resource.getContents().get(0)).getDefinitions();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    public String getStencilSetURL() {
        return "/org.jbpm.designer.jBPMDesigner/stencilsets/bpmn2.0/bpmn2.0.json";
    }

    public String getStencilSetNamespaceURL() {
        return "http://b3mn.org/stencilset/bpmn2.0#";
    }

    public String getStencilSetExtensionURL() {
        return "http://oryx-editor.org/stencilsets/extensions/bpmncosts-2.0#";
    }
}
