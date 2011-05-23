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
package com.intalio.web.server;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.ServiceReference;

import com.intalio.web.profile.IDiagramProfile;
import com.intalio.web.profile.IDiagramProfileService;
import com.intalio.web.profile.impl.DefaultProfileImpl;
import com.intalio.web.profile.impl.ProfileServiceImpl;
import com.intalio.web.repository.IUUIDBasedRepository;
import com.intalio.web.repository.IUUIDBasedRepositoryService;
import com.intalio.web.repository.UUIDBasedEpnRepository;
import com.intalio.web.repository.impl.UUIDBasedJbpmRepository;
import com.intalio.web.repository.impl.UUIDBasedFileRepository;


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
        IDiagramProfile profile = getProfile(req, req.getParameter("profile"));
        String response = new String(_repository.load(req, uuid, profile), Charset.forName("UTF-8"));
        
        resp.getWriter().write(response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String actionParam = req.getParameter("action");
        String preProcessingParam = req.getParameter("pp");
        if(actionParam != null && actionParam.equals("toXML")) {
            IDiagramProfile profile = getProfile(req, req.getParameter("profile"));
            String json = req.getParameter("data");
            String xml = _repository.toXML(json, profile, preProcessingParam);
            StringWriter output = new StringWriter();
            output.write(xml);
            resp.setContentType("application/xml");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(200);
            resp.getWriter().print(output.toString());
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
            
                IDiagramProfile profile = getProfile(req, profileName);
            
                _repository.save(req, uuid, json, svg, profile, autosave);

            } catch (JSONException e1) {
                throw new ServletException(e1);
            }
        }
    }
    
    private IDiagramProfile getProfile(HttpServletRequest req, String profileName) {
        IDiagramProfile profile = null;
        
        IDiagramProfileService service = new ProfileServiceImpl();
        service.init(getServletContext());
        profile = service.findProfile(req, profileName);
        if(profile == null) {
            throw new IllegalArgumentException("Cannot determine the profile to use for interpreting models");
        }
        return profile;
    }
}
