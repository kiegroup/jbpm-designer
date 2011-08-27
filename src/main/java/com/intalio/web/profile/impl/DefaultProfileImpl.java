/***************************************
 * Copyright (c) Intalio, Inc 2010
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/
package com.intalio.web.profile.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.jackson.JsonParseException;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;

import com.intalio.bpmn2.impl.Bpmn2JsonMarshaller;
import com.intalio.bpmn2.impl.Bpmn2JsonUnmarshaller;
import com.intalio.web.plugin.IDiagramPlugin;
import com.intalio.web.plugin.impl.PluginServiceImpl;
import com.intalio.web.profile.IDiagramProfile;
import com.intalio.web.profile.IDiagramProfile.IDiagramUnmarshaller;


/**
 * The implementation of the default profile for Process Designer.
 * @author Antoine Toulme
 */
public class DefaultProfileImpl implements IDiagramProfile {
    
    private static Logger _logger = LoggerFactory.getLogger(DefaultProfileImpl.class);
    

    private Map<String, IDiagramPlugin> _plugins = new LinkedHashMap<String, IDiagramPlugin>();


    private String _stencilSet;
    private String _externalLoadHost;
    private String _externalLoadProtocol;
    private String _externalLoadSubdomain;
    private String _usr;
    private String _pwd;
    
    public DefaultProfileImpl(ServletContext servletContext) {
        this(servletContext, true);
    }
    
    public DefaultProfileImpl(ServletContext servletContext, boolean initializeLocalPlugins) {
        if (initializeLocalPlugins) {
            initializeLocalPlugins(servletContext);
        }
    }

    public String getTitle() {
        return "Process Designer";
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
        //we read the default.xml file and make sense of it.
        FileInputStream fileStream = null;
        try {
            try {
                fileStream = new FileInputStream(new StringBuilder(context.getRealPath("/")).append("/").
                        append("/").append("profiles").append("/").append("default.xml").toString());
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
        return "default";
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

    public IDiagramMarshaller createMarshaller() {
        return new IDiagramMarshaller() {
            public String parseModel(String jsonModel, String preProcessingData) {
                Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                //Definitions def;
                Resource res;
                try {
                    res = unmarshaller.unmarshall(jsonModel, preProcessingData);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    res.save(outputStream, Collections.singletonMap(XMLResource.OPTION_ENCODING, "UTF-8"));
                    return outputStream.toString();
                } catch (JsonParseException e) {
                    _logger.error(e.getMessage(), e);
                } catch (IOException e) {
                    _logger.error(e.getMessage(), e);
                }

                return "";
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
    
    private Definitions getDefinitions(String xml) {
        try {
            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new Bpmn2ResourceFactoryImpl());
            resourceSet.getPackageRegistry().put("http://www.omg.org/spec/BPMN/20100524/MODEL", Bpmn2Package.eINSTANCE);
            Resource resource = resourceSet.createResource(URI.createURI("inputStream://dummyUriWithValidSuffix.xml"));
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            resource.load(is, Collections.EMPTY_MAP);
            resource.load(Collections.EMPTY_MAP);
            return ((DocumentRoot) resource.getContents().get(0)).getDefinitions();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
    
    public String getStencilSetURL() {
        return "/designer/stencilsets/bpmn2.0/bpmn2.0.json";
    }

    public String getStencilSetNamespaceURL() {
        return "http://b3mn.org/stencilset/bpmn2.0#";
    }

    public String getStencilSetExtensionURL() {
        return "http://oryx-editor.org/stencilsets/extensions/bpmncosts-2.0#";
    }

}
