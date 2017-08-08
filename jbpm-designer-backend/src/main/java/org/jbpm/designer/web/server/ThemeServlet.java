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
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
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

@WebServlet(displayName = "Themes", name = "ThemesServlet",
        urlPatterns = "/themes")
public class ThemeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String ACTION_GETTHEMENAMES = "getThemeNames";
    private static final String ACTION_GETTHEMEJSON = "getThemeJSON";
    private static final String THEME_NAME = "themes";
    public static final String THEME_EXT = ".json";
    private static final String DEFAULT_THEME = "jBPM";
    private static final Logger _logger = LoggerFactory.getLogger(ThemeServlet.class);

    protected IDiagramProfile profile;

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
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String action = req.getParameter("action");
        String uuid = Utils.getUUID(req);

        if (profile == null) {
            profile = _profileService.findProfile(req,
                                                  profileName);
        }

        if (action != null && action.equals(ACTION_GETTHEMENAMES)) {
            String themeStr = getThemeNames(profile,
                                            getServletContext(),
                                            uuid);
            PrintWriter pw = resp.getWriter();
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("UTF-8");
            pw.write(themeStr);
        } else if (action != null && action.equals(ACTION_GETTHEMEJSON)) {
            String themeJSON = getThemeJson(profile,
                                            getServletContext(),
                                            uuid);
            PrintWriter pw = resp.getWriter();
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("UTF-8");
            pw.write(themeJSON);
        }
    }

    private String getThemeJson(IDiagramProfile profile,
                                ServletContext servletContext,
                                String uuid) {
        Repository repository = profile.getRepository();

        String retStr = "";

        try {
            Asset<String> themeAsset = repository.loadAssetFromPath(profile.getRepositoryGlobalDir(uuid) + "/" + THEME_NAME + THEME_EXT);

            retStr = themeAsset.getAssetContent();
        } catch (Exception e) {
            _logger.error("Error retriving color theme info: " + e.getMessage());
        }
        return retStr;
    }

    private String getThemeNames(IDiagramProfile profile,
                                 ServletContext servletContext,
                                 String uuid) {
        Repository repository = profile.getRepository();

        String themesStr = "";

        try {

            Asset<String> themeAsset = repository.loadAssetFromPath(profile.getRepositoryGlobalDir(uuid) + "/" + THEME_NAME + THEME_EXT);

            JSONObject themesObject = new JSONObject(themeAsset.getAssetContent());
            JSONObject themes = (JSONObject) themesObject.get("themes");
            for (int i = 0; i < themes.names().length(); i++) {
                themesStr += themes.names().getString(i) + ",";
            }
            if (themesStr.endsWith(",")) {
                themesStr = themesStr.substring(0,
                                                themesStr.length() - 1);
            }
        } catch (Exception e) {
            themesStr = DEFAULT_THEME;
        }
        return themesStr;
    }
}
