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
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import bpsim.impl.BpsimFactoryImpl;
import org.apache.commons.lang3.StringEscapeUtils;
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
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonMarshaller;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonUnmarshaller;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceFactoryImpl;
import org.jbpm.designer.bpmn2.resource.JBPMBpmn2ResourceImpl;
import org.jbpm.designer.notification.DesignerNotificationEvent;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.server.EditorHandler;
import org.jbpm.designer.server.service.DefaultDesignerAssetService;
import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.web.plugin.IDiagramPlugin;
import org.jbpm.designer.web.plugin.impl.PluginServiceImpl;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * The implementation of the jBPM profile for Process Designer.
 * @author Tihomir Surdilovic
 */
@ApplicationScoped
public class JbpmProfileImpl implements IDiagramProfile {

    private static Logger _logger = LoggerFactory.getLogger(JbpmProfileImpl.class);

    private Map<String, IDiagramPlugin> _plugins = new LinkedHashMap<String, IDiagramPlugin>();

    private String stencilSet;
    private String localHistoryEnabled;
    private String localHistoryTimeout;
    private boolean initializeLocalPlugins;
    private String storeSVGonSaveOption;
    private String zOrder;
    private String bpsimDisplay;
    private String formsType;

    @Inject
    private Repository repository;

    @Inject
    private VFSService vfsServices;

    @Inject
    private Event<DesignerNotificationEvent> notification;

    @Inject
    private User user;

    public JbpmProfileImpl(ServletContext servletContext) {
        this(servletContext,
             true,
             false);
    }

    public JbpmProfileImpl() {
        this(null,
             false,
             false);
    }

    public JbpmProfileImpl(ServletContext servletContext,
                           boolean initializeLocalPlugins,
                           boolean initializeRepository) {
        if (initializeLocalPlugins) {
            initializeLocalPlugins(servletContext);
        }
    }

    public String getTitle() {
        return "jBPM Process Designer";
    }

    public String getStencilSet() {
        return stencilSet;
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
            XMLStreamReader reader = factory.createXMLStreamReader(fileStream,
                                                                   "UTF-8");
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamReader.START_ELEMENT) {
                    if ("profile".equals(reader.getLocalName())) {
                        stencilSet = readAttribute(reader,
                                                   "stencilset");
                    } else if ("plugin".equals(reader.getLocalName())) {
                        String name = readAttribute(reader,
                                                    "name");
                        _plugins.put(name,
                                     registry.get(name));
                    } else if ("localhistory".equals(reader.getLocalName())) {
                        String localHistoryEnabledVal = readAttribute(reader,
                                                                      "enabled");
                        if (!isEmpty(localHistoryEnabledVal)) {
                            this.localHistoryEnabled = localHistoryEnabledVal;
                        } else {
                            _logger.info("Invalid local history enabled");
                        }

                        String localHistoryTimeoutVal = readAttribute(reader,
                                                                      "timeout");
                        if (!isEmpty(localHistoryTimeoutVal)) {
                            localHistoryTimeout = localHistoryTimeoutVal;
                        } else {
                            _logger.info("Invalid local history timeout");
                        }
                    } else if ("storesvgonsave".equals(reader.getLocalName())) {
                        String storesvgonsaveenabled = readAttribute(reader,
                                                                     "enabled");
                        if (!isEmpty(storesvgonsaveenabled)) {
                            storeSVGonSaveOption = storesvgonsaveenabled;
                        } else {
                            _logger.info("Invalid store svg on save enabled");
                        }
                    } else if ("zorder".equals(reader.getLocalName())) {
                        String zOrderEnabled = readAttribute(reader,
                                                             "enabled");
                        if (!isEmpty(zOrderEnabled)) {
                            zOrder = zOrderEnabled;
                        } else {
                            _logger.warn("Invalid zorder enabled");
                        }
                    } else if ("bpsimdisplay".equals(reader.getLocalName())) {
                        String bpsimDisplayEnabled = readAttribute(reader,
                                                                   "enabled");
                        if (!isEmpty(bpsimDisplayEnabled)) {
                            bpsimDisplay = bpsimDisplayEnabled;
                        } else {
                            _logger.info("Invalid bpsim diaply enabled.");
                        }
                    } else if ("forms".equals(reader.getLocalName())) {
                        String formsTypeOption = readAttribute(reader,
                                                               "type");
                        formsType = formsTypeOption == null ? "" : formsTypeOption;
                    }
                }
            }
        } catch (XMLStreamException e) {
            _logger.error(e.getMessage(),
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

    private String readAttribute(XMLStreamReader reader,
                                 String attributeLocalName) {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (attributeLocalName.equals(reader.getAttributeLocalName(i))) {
                return reader.getAttributeValue(i);
            }
        }

        return null;
    }

    public String getName() {
        return "jbpm";
    }

    public String getSerializedModelExtension() {
        return "bpmn";
    }

    public String getLocalHistoryEnabled() {
        return localHistoryEnabled;
    }

    public String getLocalHistoryTimeout() {
        return localHistoryTimeout;
    }

    @Override
    public String getStoreSVGonSaveOption() {
        return storeSVGonSaveOption;
    }

    public Repository getRepository() {
        return repository;
    }

    @Override
    public void init(ServletContext context) {
        if (!initializeLocalPlugins) {
            initializeLocalPlugins(context);
            initializeLocalPlugins = true;
        }
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public IDiagramMarshaller createMarshaller() {
        return new IDiagramMarshaller() {
            public String parseModel(String jsonModel,
                                     String preProcessingData) throws Exception {
                DroolsFactoryImpl.init();
                BpsimFactoryImpl.init();
                Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                unmarshaller.setZOrderEnabled(zOrder != null && "true".equals(zOrder));
                unmarshaller.setBpsimDisplay(getBpsimDisplay() != null && "true".equals(getBpsimDisplay()));
                JBPMBpmn2ResourceImpl res;
                res = (JBPMBpmn2ResourceImpl) unmarshaller.unmarshall(jsonModel,
                                                                      preProcessingData);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                res.save(outputStream,
                         new HashMap<Object, Object>());
                return StringEscapeUtils.unescapeHtml4(outputStream.toString("UTF-8"));
            }

            public Definitions getDefinitions(String jsonModel,
                                              String preProcessingData) {
                try {
                    Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                    unmarshaller.setZOrderEnabled(zOrder != null && "true".equals(zOrder));
                    unmarshaller.setBpsimDisplay(getBpsimDisplay() != null && "true".equals(getBpsimDisplay()));
                    JBPMBpmn2ResourceImpl res = (JBPMBpmn2ResourceImpl) unmarshaller.unmarshall(jsonModel,
                                                                                                preProcessingData);
                    return (Definitions) res.getContents().get(0);
                } catch (JsonParseException e) {
                    return getDefaultDefinitions();
                } catch (IOException e) {
                    return getDefaultDefinitions();
                }
            }

            public Resource getResource(String jsonModel,
                                        String preProcessingData) throws Exception {
                Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                unmarshaller.setZOrderEnabled(zOrder != null && "true".equals(zOrder));
                unmarshaller.setBpsimDisplay(getBpsimDisplay() != null && "true".equals(getBpsimDisplay()));
                return (JBPMBpmn2ResourceImpl) unmarshaller.unmarshall(jsonModel,
                                                                       preProcessingData);
            }
        };
    }

    public IDiagramUnmarshaller createUnmarshaller() {
        return new IDiagramUnmarshaller() {
            public String parseModel(String xmlModel,
                                     IDiagramProfile profile,
                                     String preProcessingData) throws Exception {
                DroolsFactoryImpl.init();
                BpsimFactoryImpl.init();

                Bpmn2JsonMarshaller marshaller = new Bpmn2JsonMarshaller();
                marshaller.setProfile(profile);
                return marshaller.marshall(getDefinitions(xmlModel),
                                           preProcessingData);
            }
        };
    }

    @Override
    public String getRepositoryGlobalDir() {
        return "/global";
    }

    @Override
    public String getRepositoryGlobalDir(String uuid) {
        if (uuid != null) {
            Path uuidPath = vfsServices.get(uuid.replaceAll("\\s", "%20"));
            String pathURI = uuidPath.toURI();

            if (pathURI != "/") {
                String[] pathParts = pathURI.split("/");
                try {
                    String pathProjectName = pathParts[3];
                    if (pathProjectName.length() < 1) {
                        return "/global";
                    } else {
                        return "/" + pathProjectName + "/global";
                    }
                } catch (Exception e) {
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
                    .put(Resource.Factory.Registry.DEFAULT_EXTENSION,
                         new JBPMBpmn2ResourceFactoryImpl());
            resourceSet.getPackageRegistry().put("http://www.omg.org/spec/BPMN/20100524/MODEL",
                                                 Bpmn2Package.eINSTANCE);
            resourceSet.getPackageRegistry().put("http://www.jboss.org/drools",
                                                 DroolsPackage.eINSTANCE);

            JBPMBpmn2ResourceImpl resource = (JBPMBpmn2ResourceImpl) resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
            resource.getDefaultLoadOptions().put(JBPMBpmn2ResourceImpl.OPTION_ENCODING,
                                                 "UTF-8");
            resource.setEncoding("UTF-8");
            Map<String, Object> options = new HashMap<String, Object>();
            options.put(JBPMBpmn2ResourceImpl.OPTION_ENCODING,
                        "UTF-8");
            options.put(JBPMBpmn2ResourceImpl.OPTION_DEFER_IDREF_RESOLUTION,
                        true);
            options.put(JBPMBpmn2ResourceImpl.OPTION_DISABLE_NOTIFY,
                        true);
            options.put(JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF,
                        JBPMBpmn2ResourceImpl.OPTION_PROCESS_DANGLING_HREF_RECORD);
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            resource.load(is,
                          options);
            if (!resource.getErrors().isEmpty()) {
                String errorMessages = "";
                for (Resource.Diagnostic error : resource.getErrors()) {
                    errorMessages += error.getMessage() + "\n";
                }
                notification.fire(new DesignerNotificationEvent(errorMessages,
                                                                NotificationEvent.NotificationType.ERROR,
                                                                user.getIdentifier()));
            }

            if (!resource.getWarnings().isEmpty()) {
                String warningMessages = "";
                for (Resource.Diagnostic warning : resource.getWarnings()) {
                    warningMessages += warning.getMessage() + "\n";
                }
                notification.fire(new DesignerNotificationEvent(warningMessages,
                                                                NotificationEvent.NotificationType.WARNING,
                                                                user.getIdentifier()));
            }

            EList<Diagnostic> warnings = resource.getWarnings();

            if (warnings != null && !warnings.isEmpty()) {
                for (Diagnostic diagnostic : warnings) {
                    _logger.info("Warning: " + diagnostic.getMessage());
                }
            }

            return ((DocumentRoot) resource.getContents().get(0)).getDefinitions();
        } catch (Exception e) {
            return getDefaultDefinitions();
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

    @Override
    public String getBpsimDisplay() {
        return System.getProperty(EditorHandler.BPSIM_DISPLAY) == null ? bpsimDisplay : System.getProperty(EditorHandler.BPSIM_DISPLAY);
    }

    @Override
    public String getFormsType() {
        return System.getProperty(EditorHandler.FORMS_TYPE) == null ? formsType : System.getProperty(EditorHandler.FORMS_TYPE);
    }

    /**
     * For test purposes
     * @param zOrderEnabled
     */
    public void setZOrder(String zOrderEnabled) {
        zOrder = zOrderEnabled;
    }

    /**
     * For test purposes
     * @param bpsimDisplayEnabled
     */
    public void setBpsimDisplay(String bpsimDisplayEnabled) {
        bpsimDisplay = bpsimDisplayEnabled;
    }

    /**
     * For test purposes
     * @param formsTypeOption
     */
    public void setFormsType(String formsTypeOption) {
        formsType = formsTypeOption;
    }

    private boolean isEmpty(final CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        for (int i = 0, length = str.length(); i < length; i++) {
            if (str.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    private Definitions getDefaultDefinitions() {
        try {
            String defaultProcessContent = DefaultDesignerAssetService.PROCESS_STUB.replaceAll("\\$\\{processid\\}",
                                                                                               "defaultprocessid");
            String errorMessages = "openinxmleditor";
            notification.fire(new DesignerNotificationEvent(errorMessages,
                                                            NotificationEvent.NotificationType.ERROR,
                                                            user.getIdentifier()));
            return getDefinitions(defaultProcessContent);
        } catch (Exception e) {
            _logger.error(e.getMessage());
            return null;
        }
    }
}
