/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.designer.web.stencilset.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.web.stencilset.IDiagramStencilSet;
import org.jbpm.designer.web.stencilset.IDiagramStencilSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A servlet to serve stencilsets from the StencilSetService.
 * @author Antoine Toulme
 *
 */
public class StencilSetServiceServlet extends HttpServlet {

private static final Logger _logger = LoggerFactory.getLogger(StencilSetServiceServlet.class);
    
    private static final long serialVersionUID = -2024110864538877629L;
    private String defaultName;
    
    private IDiagramStencilSetService _pluginService;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        defaultName = config.getInitParameter("defaultName");
        _pluginService = new StencilSetServiceImpl(config.getServletContext());
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        // urls should be of type: /org.jbpm.designer.jBPMDesigner/stencilset/bpmn2.0
        // /org.jbpm.designer.jBPMDesigner/stencilset/{name}/{resource}


        String[] segments = requestURI.split("/");
        if (segments.length < 2) {
            throw new IllegalArgumentException("No name provided");
        }

        String name = null;
        try {
            name = segments[5];
        } catch (Exception e) {
            name = segments[segments.length-1];
        }

        if(name.length() < 1) {
            name = defaultName;
        }

        IDiagramStencilSet stencilset = _pluginService.findStencilSet(req, name);
        
        if (stencilset == null) {
            throw new IllegalArgumentException("No stencilset by the name of " + name);
        }
        InputStream input = null;
        if (segments.length > 4) { 
            //looking for a resource under the stencilset.
            String path = requestURI.substring(requestURI.indexOf(segments[3]) + segments[3].length() + 1);
            // this is a bad temp hack..but gets stuff working for nows
            if(path.indexOf("org.jbpm.designer.jBPMDesigner/stencilset//org.jbpm.designer.jBPMDesigner/stencilsets/bpmn2.0/") >= 0) {
                path = path.substring("org.jbpm.designer.jBPMDesigner/stencilset//org.jbpm.designer.jBPMDesigner/stencilsets/bpmn2.0/".length(), path.length());
            }
            if(path.indexOf("org.jbpm.designer.jBPMDesigner/stencilset//org.jbpm.designer.jBPMDesigner/stencilsets/bpmn2.0jbpm/") >= 0) {
                path = path.substring("org.jbpm.designer.jBPMDesigner/stencilset//org.jbpm.designer.jBPMDesigner/stencilsets/bpmn2.0jbpm/".length(), path.length());
            }
            if(path.indexOf("bpmn2.0.json/") >= 0) {
                path = path.substring("bpmn2.0.json".length(), path.length());
            }
            if(path.indexOf("bpmn2.0jbpm.json/") >= 0) {
                path = path.substring("bpmn2.0jbpm.json".length(), path.length());
            }
            if(path.startsWith("/bpmn2.0jbpm/")) {
                path = path.substring("/bpmn2.0jbpm/".length(), path.length());
            }
            if(path.startsWith("stencilsets/bpmn2.0jbpm/")) {
                path = path.substring("stencilsets/bpmn2.0jbpm/".length(), path.length());
            }
            if(path.startsWith("2.0jbpm/bpmn2.0jbpm.json/")) {
                path = path.substring("2.0jbpm/bpmn2.0jbpm.json/".length(), path.length());
            }

            input = stencilset.getResourceContents(path);
            if(requestURI.endsWith(".svg")) {
                resp.setContentType("text/xml");
            } else if(requestURI.endsWith(".png")) {
                resp.setContentType("image/png");
            } else {
                //default to not setting
            }
        } else {
            input = stencilset.getContents();
            resp.setContentType("application/json");
        }
        
        try {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = input.read(buffer)) != -1) {
                resp.getOutputStream().write(buffer, 0, read);
            }
        } catch (IOException e) {
            _logger.error(e.getMessage(), e);
        } finally {
            if (input != null) { try { input.close(); } catch(IOException e) {}};
        }
        }
}
