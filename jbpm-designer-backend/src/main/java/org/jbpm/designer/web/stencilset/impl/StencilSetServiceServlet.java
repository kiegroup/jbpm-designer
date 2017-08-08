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
package org.jbpm.designer.web.stencilset.impl;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.web.stencilset.IDiagramStencilSet;
import org.jbpm.designer.web.stencilset.IDiagramStencilSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(displayName = "StencilSet Service Servlet", name = "StencilSetServiceServlet",
        urlPatterns = "/stencilset/*",
        initParams = {
                @WebInitParam(name = "defaultName", value = "bpmn2.0jbpm")})
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

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI().replaceFirst(req.getContextPath(),
                                                             "");
        // urls should be of type: stencilset/bpmn2.0
        // stencilset/{name}/{resource}

        String[] segments = requestURI.split("/");
        if (segments.length < 2) {
            throw new IllegalArgumentException("No name provided");
        }

        String name = null;
        try {
            name = segments[2];
        } catch (Exception e) {
            name = segments[segments.length - 1];
        }

        if (name.length() < 1) {
            name = defaultName;
        }

        IDiagramStencilSet stencilset = _pluginService.findStencilSet(req,
                                                                      name);

        if (stencilset == null) {
            throw new IllegalArgumentException("No stencilset by the name of " + name);
        }
        String applicationContext = ConfigurationProvider.getInstance().getDesignerContext().replaceAll("/",
                                                                                                        "");
        InputStream input = null;
        if (segments.length > 4) {
            //looking for a resource under the stencilset.
            String path = requestURI.substring(requestURI.indexOf(segments[2]) + segments[2].length() + 1);
            if (path.indexOf(applicationContext + "/stencilset/org.jbpm.designer.jBPMDesigner/stencilsets/bpmn2.0jbpm/bpmn2.0jbpm.json") >= 0) {
                path = applicationContext + "/stencilsets/bpmn2.0jbpm/bpmn2.0jbpm.json";
            }

            if (path.indexOf(applicationContext + "/stencilset/bpmn2.0jbpm/stencilset/org.jbpm.designer.jBPMDesigner/stencilsets/bpmn2.0jbpm/bpmn2.0jbpm.json") >= 0) {
                path = applicationContext + "/stencilsets/bpmn2.0jbpm/bpmn2.0jbpm.json";
            }

            if (path.indexOf("stencilset//stencilsets/bpmn2.0jbpm/bpmn2.0jbpm.json") >= 0) {
                path = "/bpmn2.0jbpm.json";
            }

            if (path.indexOf(applicationContext + "/stencilset/" + applicationContext + "/stencilsets/bpmn2.0/") >= 0) {
                path = path.substring((applicationContext + "/stencilset/" + applicationContext + "/stencilsets/bpmn2.0/").length(),
                                      path.length());
            }
            if (path.indexOf("stencilset//" + applicationContext + "/stencilsets/bpmn2.0jbpm/") >= 0) {
                path = path.substring(("stencilset//" + applicationContext + "/stencilsets/bpmn2.0jbpm/").length(),
                                      path.length());
            }
            if (path.indexOf(applicationContext + "/stencilset/" + applicationContext + "/stencilsets/bpmn2.0jbpm/") >= 0) {
                path = path.substring((applicationContext + "/stencilset/" + applicationContext + "/stencilsets/bpmn2.0jbpm/").length(),
                                      path.length());
            }
            if (path.indexOf("bpmn2.0.json/") >= 0) {
                path = path.substring("bpmn2.0.json".length(),
                                      path.length());
            }
            if (path.indexOf("bpmn2.0jbpm.json/") >= 0) {
                path = path.substring("bpmn2.0jbpm.json".length(),
                                      path.length());
            }
            if (path.startsWith("/bpmn2.0jbpm/")) {
                path = path.substring("/bpmn2.0jbpm/".length(),
                                      path.length());
            }
            if (path.startsWith("stencilsets/bpmn2.0jbpm/")) {
                path = path.substring("stencilsets/bpmn2.0jbpm/".length(),
                                      path.length());
            }
            if (path.startsWith("2.0jbpm/bpmn2.0jbpm.json/")) {
                path = path.substring("2.0jbpm/bpmn2.0jbpm.json/".length(),
                                      path.length());
            }

            input = stencilset.getResourceContents(path);
            if (requestURI.endsWith(".svg")) {
                resp.setContentType("text/xml");
            } else if (requestURI.endsWith(".png")) {
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
                resp.getOutputStream().write(buffer,
                                             0,
                                             read);
            }
        } catch (IOException e) {
            _logger.error(e.getMessage(),
                          e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
            ;
        }
    }
}
