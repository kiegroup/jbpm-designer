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
package org.jbpm.designer.web.plugin.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.server.EditorHandler;
import org.jbpm.designer.web.plugin.IDiagramPlugin;
import org.jbpm.designer.web.plugin.IDiagramPluginService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This servlet exposes the plugins registered against the platforms.
 * 
 * The servlet has this contract:
 * /plugins will return the information about the registered plugins in json format:
 *  [{"name":ORYX.Save, "core":false}]
 * /plugin?name=ORYX.Save
 * will return the contents of the ORYX.Save plugin. 
 * 
 * @author Antoine Toulme
 */
public class PluginServiceServlet extends HttpServlet {
    
    private static final Logger _logger = LoggerFactory.getLogger(
            PluginServiceServlet.class);
    
    private static final long serialVersionUID = -2024110864538877629L;
    
    private IDiagramPluginService _pluginService;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        _pluginService = PluginServiceImpl.getInstance(
                config.getServletContext());
    }
    
    protected void doGet(HttpServletRequest req, 
            HttpServletResponse resp) 
    throws ServletException, IOException {
        try {
        if ((EditorHandler.oryx_path +  "plugins").equals(req.getRequestURI())) {
            listAllPlugins(req, resp);
        } else {
            retrievePluginContents(req, resp);
        }
        } catch(JSONException e) {
            throw new ServletException(e);
        }
    }

    private void retrievePluginContents(HttpServletRequest req, 
            HttpServletResponse resp) {
        String name = req.getParameter("name");
        if (name == null) {
            name = req.getRequestURI().substring(
                    req.getRequestURI().lastIndexOf("/")+1);
            if (name.endsWith(".js")) {
                name = name.substring(0, name.length() - ".js".length());
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("No name provided");
        }
        IDiagramPlugin plugin = _pluginService.findPlugin(req, name);
        if (plugin == null) {
            throw new IllegalArgumentException(
                    "No plugin by the name of " + name);
        }
        InputStream input = plugin.getContents();
        if (input == null) {
            throw new IllegalArgumentException(
                    "Plugin contents could not be found");
        }
        try {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = input.read(buffer)) != -1) {
                resp.getOutputStream().write(buffer, 0, read);
            }
            resp.setContentType("application/x-javascript");
        } catch (IOException e) {
            _logger.error(e.getMessage(), e);
        } finally {
            if (input != null) { 
                try { input.close(); } catch(IOException e) {}
            }
        }
        
    }

    private void listAllPlugins(HttpServletRequest req, 
            HttpServletResponse resp) 
            throws IOException, JSONException {
        JSONArray plugins = new JSONArray();
        for (IDiagramPlugin p : 
                _pluginService.getRegisteredPlugins(req)) {
            JSONObject obj = new JSONObject();
            obj.put("name", p.getName());
            obj.put("core", p.isCore());
            JSONArray properties = new JSONArray();
            if (p.getProperties() != null) {
                for (Entry<String, Object> entry : 
                        p.getProperties().entrySet()) {
                    JSONObject propObj = new JSONObject();
                    propObj.put(entry.getKey(), entry.getValue());
                    properties.put(propObj);
                }
            }
            obj.put("properties", properties);
            plugins.put(obj);
        }
        resp.setContentType("application/json");
        resp.getWriter().append(plugins.toString());
    }

}
