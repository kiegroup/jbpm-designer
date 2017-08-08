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
package org.jbpm.designer.web.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.repository.IUUIDBasedRepository;
import org.jbpm.designer.web.repository.IUUIDBasedRepositoryService;
import org.jbpm.designer.web.repository.UUIDBasedEpnRepository;
import org.jbpm.designer.web.repository.impl.UUIDBasedFileRepository;
import org.jbpm.designer.web.repository.impl.UUIDBasedJbpmRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(displayName = "UUID Based Repository", name = "UUIDBasedRepositoryServlet",
        urlPatterns = "/uuidRepository",
        initParams = {
                @WebInitParam(name = "factoryName", value = "jbpm")})
public class UUIDBasedRepositoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger _logger = LoggerFactory.getLogger(UUIDBasedRepositoryServlet.class);

    private IDiagramProfile profile;

    @Inject
    private IDiagramProfileService _profileService = null;

    public static IUUIDBasedRepositoryService _factory = new IUUIDBasedRepositoryService() {

        private Map<String, IUUIDBasedRepository> factories = new HashMap<String, IUUIDBasedRepository>();
        private boolean _init = false;

        public void init() {
            factories.put("default",
                          new UUIDBasedFileRepository());
            factories.put("jbpm",
                          new UUIDBasedJbpmRepository());
            factories.put("epn",
                          new UUIDBasedEpnRepository());
            _init = true;
        }

        public IUUIDBasedRepository createRepository(ServletConfig config) {
            if (!_init) {
                init();
            }
            return lookupRepository(config.getInitParameter("factoryName"));
        }

        public IUUIDBasedRepository createRepository() {
            return new UUIDBasedFileRepository();
        }

        public IUUIDBasedRepository lookupRepository(String name) {
            if (name == null || !factories.containsKey(name)) {
                IUUIDBasedRepository repo = factories.get("default");
                return repo;
            } else {
                IUUIDBasedRepository repo = factories.get(name);
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
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        String uuid = Utils.getUUID(req);
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        if (uuid == null) {
            throw new ServletException("uuid parameter required");
        }
        if (profile == null) {
            profile = _profileService.findProfile(req,
                                                  profileName);
        }
        try {
            String response = new String(_repository.load(req,
                                                          uuid,
                                                          profile,
                                                          getServletContext()),
                                         Charset.forName("UTF-8"));
            resp.getWriter().write(response);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("error: " + ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String actionParam = req.getParameter("action");
        String preProcessingParam = req.getParameter("pp");
        if (preProcessingParam == null) {
            preProcessingParam = "ReadOnlyService";
        }
        if (actionParam != null && actionParam.equals("toXML")) {
            if (profile == null) {
                profile = _profileService.findProfile(req,
                                                      profileName);
            }
            String json = req.getParameter("data");
            String xml = "";
            try {
                xml = _repository.toXML(json,
                                        profile,
                                        preProcessingParam);
            } catch (Exception e) {
                _logger.error("Error transforming to XML: " + e.getMessage());
            }
            StringWriter output = new StringWriter();
            output.write(xml);
            resp.setContentType("application/xml");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(200);
            resp.getWriter().print(output.toString());
        } else if (actionParam != null && actionParam.equals("checkErrors")) {
            String retValue = "false";
            if (profile == null) {
                profile = _profileService.findProfile(req,
                                                      profileName);
            }
            String json = req.getParameter("data");
            try {
                String xmlOut = profile.createMarshaller().parseModel(json,
                                                                      preProcessingParam);
                String jsonIn = profile.createUnmarshaller().parseModel(xmlOut,
                                                                        profile,
                                                                        preProcessingParam);
                if (jsonIn == null || jsonIn.length() < 1) {
                    retValue = "true";
                }
            } catch (Throwable t) {
                retValue = "true";
                _logger.error("Exception parsing process: " + t.getMessage());
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
                reqWriter.write(buffer,
                                0,
                                read);
            }

            String data = reqWriter.toString();
            try {
                JSONObject jsonObject = new JSONObject(data);

                String json = (String) jsonObject.get("data");
                String svg = (String) jsonObject.get("svg");
                String uuid = (String) jsonObject.get("uuid");
                boolean autosave = jsonObject.getBoolean("savetype");

                if (profile == null) {
                    profile = _profileService.findProfile(req,
                                                          profileName);
                }

                _repository.save(req,
                                 uuid,
                                 json,
                                 svg,
                                 profile,
                                 autosave);
            } catch (JSONException e1) {
                throw new ServletException(e1);
            }
        }
    }
}
