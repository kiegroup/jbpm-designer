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
package org.oryxeditor.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.hpi.bpmn2_0.ExportValidationEventCollector;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;

/**
 * @author Antoine Toulme
 * a file based repository that uses the UUID element to save files in individual spots on the file system.
 *
 */
public class UUIDBasedRepositoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private static final Logger _logger = Logger.getLogger(UUIDBasedRepositoryServlet.class);
    
    /**
     * the path to the repository inside the servlet.
     */
    private final static String REPOSITORY_PATH = "repository";
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuid = req.getParameter("uuid");
        if (uuid == null) {
            throw new ServletException("uuid parameter required");
        }
        String filename = this.getServletContext().getRealPath("/" + REPOSITORY_PATH + "/" + uuid + ".json");
        if (!new File(filename).exists()) {
           return; // then return nothing. 
        }
        InputStream input = null;
        try {
            input = new FileInputStream(filename);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = input.read(buffer)) != -1) {
                resp.getOutputStream().write(buffer, 0, read);
            }
        } finally {
            if (input != null) { try { input.close();} catch(Exception e) {} }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringWriter reqWriter = new StringWriter();
        char[] buffer = new char[4096];
        int read;
        while ((read = reader.read(buffer)) != -1) {
            reqWriter.write(buffer, 0, read);
        }
        String data = reqWriter.toString();
        try {
            JSONObject jsonObject = new JSONObject(data);
            

            String json = (String) jsonObject.get("data");
            String svg = (String) jsonObject.get("svg");
            String uuid = (String) jsonObject.get("uuid");
            BufferedWriter writer = null;

            try {
                try {
                    StringWriter bpmnWriter = performTransformationToDi(json, true, AbstractBpmnFactory.getFactories());
                    writer = new BufferedWriter(new FileWriter(this.getServletContext().getRealPath("/" + REPOSITORY_PATH + "/" + uuid + ".bpmn")));
                    writer.write(bpmnWriter.toString());
                } catch (JSONException e2) {
                    throw new ServletException(e2);
                } catch (BpmnConverterException e) {
                    throw new ServletException(e);
                } catch (JAXBException e) {
                    throw new ServletException(e);
                }
            } catch (Exception e) {
                // whatever was thrown, for now, we catch it and log it.
                _logger.error(e.getMessage(), e);
            } finally {
                if (writer != null) { try { writer.close();} catch(Exception e) {} }
            }


            try {
                writer = new BufferedWriter(new FileWriter(this.getServletContext().getRealPath("/" + REPOSITORY_PATH + "/" + uuid + ".json")));
                writer.write(json);
            } finally {
                if (writer != null) { try { writer.close();} catch(Exception e) {} }
            }
            try {
                writer = new BufferedWriter(new FileWriter(this.getServletContext().getRealPath("/" + REPOSITORY_PATH + "/" + uuid + ".svg")));
                writer.write(svg);
            } finally {
                if (writer != null) { try { writer.close();} catch(Exception e) {} }
            }
        } catch (JSONException e1) {
            throw new ServletException(e1);
        }
    }
    
    /**
     * Copied from the Bpmn2_0Servlet class.
     * 
     * Triggers the transformation from Diagram to BPMN model and writes the 
     * resulting BPMN XML on success.
     * 
     * @param json
     *      The diagram in JSON format
     * @param writer
     *      The HTTP-response writer
     * @throws Exception
     *      Exception occurred while processing
     */
    protected StringWriter performTransformationToDi(String json, boolean asXML, List<Class<? extends AbstractBpmnFactory>> factoryClasses) throws Exception {
        StringWriter writer = new StringWriter();
        JSONObject result = new JSONObject();
        
        /* Retrieve diagram model from JSON */
    
        Diagram diagram = DiagramBuilder.parseJson(json);
            
        /* Build up BPMN 2.0 model */
        Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, factoryClasses);
        Definitions bpmnDefinitions = converter.getDefinitionsFromDiagram();
        
        /* Perform XML creation */
        JAXBContext context = JAXBContext.newInstance(Definitions.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        
        NamespacePrefixMapper nsp = new BPMNPrefixMapper();
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", nsp);
        
        /* Set Schema validation properties */
        SchemaFactory sf = SchemaFactory
                .newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        
        String xsdPath = this.getServletContext().getRealPath("/WEB-INF/lib/bpmn20/BPMN20.xsd");
        
        Schema schema = sf.newSchema(new File(xsdPath));
        marshaller.setSchema(schema);
        
        ExportValidationEventCollector vec = new ExportValidationEventCollector();
        marshaller.setEventHandler(vec);
        
        /* Marshal BPMN 2.0 XML */
        marshaller.marshal(bpmnDefinitions, writer);
        
        if(asXML) {
            return writer;
        }
        
        result.put("xml", writer.toString());
        
        /* Append XML Schema validation results */
        if(vec.hasEvents()) {
            ValidationEvent[] events = vec.getEvents();
            StringBuilder builder = new StringBuilder();
            builder.append("Validation Errors: <br /><br />");
            
            for(ValidationEvent event : Arrays.asList(events)) {
                
                builder.append("Line: ");
                builder.append(event.getLocator().getLineNumber());
                builder.append(" Column: ");
                builder.append(event.getLocator().getColumnNumber());
                
                builder.append("<br />Error: ");
                builder.append(event.getMessage());
                builder.append("<br /><br />");
            }
            result.put("validationEvents", builder.toString());
        }
        
        /* Prepare output */
        writer = new StringWriter();
        writer.write(result.toString());
        
        return writer;      
    }
}
