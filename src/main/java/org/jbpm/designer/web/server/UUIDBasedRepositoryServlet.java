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
package org.jbpm.designer.web.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.DefaultProfileImpl;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;
import org.jbpm.designer.web.repository.IUUIDBasedRepository;
import org.jbpm.designer.web.repository.IUUIDBasedRepositoryService;
import org.jbpm.designer.web.repository.UUIDBasedEpnRepository;
import org.jbpm.designer.web.repository.impl.UUIDBasedFileRepository;
import org.jbpm.designer.web.repository.impl.UUIDBasedJbpmRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.ServiceReference;

/**
 * @author Antoine Toulme
 * a file based repository that uses the UUID element to save files in individual spots on the file system.
 *
 */
public class UUIDBasedRepositoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private static final Logger _logger = Logger.getLogger(UUIDBasedRepositoryServlet.class);
    
    public static IUUIDBasedRepositoryService _factory = new IUUIDBasedRepositoryService() {

        private Map<String, IUUIDBasedRepository> factories = new HashMap<String, IUUIDBasedRepository>();
        private boolean _init = false;
        
        public void init() {
            factories.put("default", new UUIDBasedFileRepository());
            factories.put("jbpm", new UUIDBasedJbpmRepository());
            factories.put("epn", new UUIDBasedEpnRepository());
            _init = true;
        }
        
        public IUUIDBasedRepository createRepository(ServletConfig config) {
            if(!_init) init();     
            return lookupRepository(config.getInitParameter("factoryName"));
        }
        
        public IUUIDBasedRepository createRepository() {
            return new UUIDBasedFileRepository();
        }
        
        public IUUIDBasedRepository lookupRepository(String name) {
            if(name == null || !factories.containsKey(name)) {
                IUUIDBasedRepository repo =  factories.get("default");
                return repo;
            } else {
                IUUIDBasedRepository repo =  factories.get(name);
                return repo;
            }
        }     
    };
    
    private IUUIDBasedRepository _repository;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            _repository = _factory.createRepository(config);
            _repository.configure(this);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        String uuid = req.getParameter("uuid");
        String preProcessingParam = req.getParameter("pp");
        if (uuid == null) {
            throw new ServletException("uuid parameter required");
        }
        IDiagramProfile profile = ServletUtil.getProfile(req, req.getParameter("profile"), getServletContext());
		try {
		    
			String response =  new String(_repository.load(req, uuid, profile), "UTF-8");
			resp.getWriter().write(response);
		} catch (Exception e) {
			throw new ServletException("Exception loading process: " + e.getMessage());
		}
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String actionParam = req.getParameter("action");
        String preProcessingParam = req.getParameter("pp");
        if(actionParam != null && actionParam.equals("toXML")) {
            IDiagramProfile profile = ServletUtil.getProfile(req, req.getParameter("profile"), getServletContext());
            String json = req.getParameter("data");
            String xml = _repository.toXML(json, profile, preProcessingParam);
            StringWriter output = new StringWriter();
            output.write(xml);
            resp.setContentType("application/xml");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(200);
            resp.getWriter().print(output.toString());
        } else if(actionParam != null && actionParam.equals("checkErrors")) { 
        	String retValue = "false";
        	IDiagramProfile profile = ServletUtil.getProfile(req, req.getParameter("profile"), getServletContext());
            String json = req.getParameter("data");
            try {
				String xmlOut = profile.createMarshaller().parseModel(json, preProcessingParam);
				String jsonIn = profile.createUnmarshaller().parseModel(xmlOut, profile, preProcessingParam);
				if(jsonIn == null || jsonIn.length() < 1) {
					retValue = "true";
				}
			} catch (Throwable t) {
				retValue = "true";
				System.out.println("Exception parsing process: " + t.getMessage());
			}
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(200);
            resp.getWriter().print(retValue);
        } else {
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
                String profileName = (String) jsonObject.get("profile");
                boolean autosave = jsonObject.getBoolean("savetype");
            
                IDiagramProfile profile = ServletUtil.getProfile(req, profileName, getServletContext());
            
                _repository.save(req, uuid, json, svg, profile, autosave);

            } catch (JSONException e1) {
                throw new ServletException(e1);
            }
        }
    }
}
