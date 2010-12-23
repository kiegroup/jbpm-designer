package com.intalio.web.profile.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intalio.bpmn2.impl.Bpmn2JsonUnmarshaller;
import com.intalio.web.plugin.IDiagramPlugin;
import com.intalio.web.plugin.impl.PluginServiceImpl;
import com.intalio.web.profile.IDiagramProfile;

/**
 * The implementation of the drools profile for Process Designer.
 * @author Tihomir Surdilovic
 */
public class DroolsProfileImpl implements IDiagramProfile {
    
    private static Logger _logger = LoggerFactory.getLogger(DroolsProfileImpl.class);
    

    private Map<String, IDiagramPlugin> _plugins = new LinkedHashMap<String, IDiagramPlugin>();


    private String _stencilSet;
    
    public DroolsProfileImpl(ServletContext servletContext) {
        this(servletContext, true);
    }
    
    public DroolsProfileImpl(ServletContext servletContext, boolean initializeLocalPlugins) {
        if (initializeLocalPlugins) {
            initializeLocalPlugins(servletContext);
        }
    }

    public String getTitle() {
        return "Drools Process Designer";
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
                        append("/").append("profiles").append("/").append("drools.xml").toString());
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
        return "drools";
    }

    public String getSerializedModelExtension() {
        return "bpmn";
    }
    
    public IDiagramMarshaller createMarshaller() {
        return new IDiagramMarshaller() {
            public String parseModel(String jsonModel) {
                Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                Definitions def;
                try {
                    def = unmarshaller.unmarshall(jsonModel);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    def.eResource().save(outputStream, Collections.singletonMap(XMLResource.OPTION_ENCODING, "UTF-8"));
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
}

