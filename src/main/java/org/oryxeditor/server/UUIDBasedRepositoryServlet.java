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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.json.JSONException;
import org.json.JSONObject;

import com.intalio.bpmn2.Bpmn2JsonUnmarshaller;
import com.intalio.web.repository.IUUIDBasedRepository;
import com.intalio.web.repository.impl.UUIDBasedFileRepository;


/**
 * @author Antoine Toulme
 * a file based repository that uses the UUID element to save files in individual spots on the file system.
 *
 */
public class UUIDBasedRepositoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private static final Logger _logger = Logger.getLogger(UUIDBasedRepositoryServlet.class);
    
    public static Class<? extends IUUIDBasedRepository> _repositoryClass;
    
    static {
        _repositoryClass = UUIDBasedFileRepository.class;
    }
    
    private IUUIDBasedRepository _repository;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            _repository = _repositoryClass.newInstance();
            _repository.configure(this);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uuid = req.getParameter("uuid");
        if (uuid == null) {
            throw new ServletException("uuid parameter required");
        }
        
        ByteArrayInputStream input = new ByteArrayInputStream(_repository.load(uuid));
        byte[] buffer = new byte[4096];
        int read;

        while ((read = input.read(buffer)) != -1) {
            resp.getOutputStream().write(buffer, 0, read);
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
            String bpmn = "";
            
            try {
                Bpmn2JsonUnmarshaller unmarshaller = new Bpmn2JsonUnmarshaller();
                Definitions def = unmarshaller.unmarshall(json);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                def.eResource().save(outputStream, Collections.singletonMap(XMLResource.OPTION_ENCODING, "UTF-8"));
                bpmn = outputStream.toString();
                
            } catch (Exception e) {
                // whatever was thrown, for now, we catch it and log it.
                _logger.error(e.getMessage(), e);
            }
            
            _repository.save(uuid, json, svg, bpmn);

        } catch (JSONException e1) {
            throw new ServletException(e1);
        }
    }
}
