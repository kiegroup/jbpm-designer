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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import de.hpi.util.reflection.ClassFinder;

/**
 * @author Antoine Toulme
 * a file based repository that uses the UUID element to save files in individual spots on the file system.
 *
 */
public class UUIDBasedRepositoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
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
        String uuid = req.getParameter("uuid");
        if (uuid == null) {
            throw new ServletException("uuid parameter required");
        }
        String json = req.getParameter("data");
        String svg = req.getParameter("svg");
        // this is using JAXB and JSON to recreate the model and save.
        try {
        try {
            Diagram diagram = DiagramBuilder.parseJson(json);
            List<Class<? extends AbstractBpmnFactory>> factoryClasses = ClassFinder.getClassesByPackageName(AbstractBpmnFactory.class,
                "de.hpi.bpmn2_0.factory", this.getServletContext());
            Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, factoryClasses);
            Definitions def = converter.getDefinitionsFromDiagram();
            JAXBContext context = JAXBContext.newInstance(Definitions.class);
            Marshaller m = context.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            NamespacePrefixMapper nsp = new BPMNPrefixMapper();
            m.setProperty("com.sun.xml.bind.namespacePrefixMapper", nsp);
            m.marshal(def, new File(this.getServletContext().getRealPath("/" + REPOSITORY_PATH + "/" + uuid + ".bpmn")));
        } catch (ClassNotFoundException e1) {
            throw new ServletException(e1);
        } catch (JSONException e2) {
            throw new ServletException(e2);
        } catch (BpmnConverterException e) {
            throw new ServletException(e);
        } catch (JAXBException e) {
            throw new ServletException(e);
        }
        } catch (Exception e) {
        }
        
        BufferedWriter writer = null;
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
    }
}
