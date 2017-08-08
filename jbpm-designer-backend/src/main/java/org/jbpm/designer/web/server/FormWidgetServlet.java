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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(displayName = "FormWidget", name = "FormWidgetServlet",
        urlPatterns = "/formwidget")
public class FormWidgetServlet extends HttpServlet {

    private static final Logger _logger = LoggerFactory.getLogger(FormWidgetServlet.class);

    private IDiagramProfile profile;

    // this is here just for unit testing purpose
    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }

    @Inject
    private IDiagramProfileService _profileService = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String action = req.getParameter("action");
        String widgetName = req.getParameter("widgetname");
        String uuid = Utils.getUUID(req);

        if (profile == null) {
            profile = _profileService.findProfile(req,
                                                  profileName);
        }
        Repository repository = profile.getRepository();
        if (action != null && action.equals("getwidgets")) {
            List<String> widgetList;
            try {
                widgetList = ServletUtil.getFormWidgetList(profile,
                                                           repository,
                                                           uuid);
            } catch (Throwable t) {
                widgetList = new ArrayList<String>();
            }
            JSONObject jsonObject = new JSONObject();
            if (widgetList != null && widgetList.size() > 0) {
                for (String widget : widgetList) {
                    try {
                        jsonObject.put(widget,
                                       widget);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            resp.getWriter().write(jsonObject.toString());
        } else if (action != null && action.equals("getwidgetsource")) {
            try {
                Asset<String> widgetAsset = repository.loadAssetFromPath(profile.getRepositoryGlobalDir(uuid) + "/" + widgetName + ".fw");

                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("text/plain");

                resp.getWriter().write(widgetAsset.getAssetContent());
            } catch (Exception e) {
                resp.getWriter().write("");
            }
        }
    }
}
