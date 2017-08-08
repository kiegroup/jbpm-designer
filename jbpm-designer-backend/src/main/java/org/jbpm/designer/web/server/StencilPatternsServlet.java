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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(displayName = "StencilPatterns", name = "StencilPatternsServlet",
        urlPatterns = "/stencilpatterns")
public class StencilPatternsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger _logger = LoggerFactory.getLogger(StencilPatternsServlet.class);
    public static final String PATTERNS_NAME = "patterns";

    protected IDiagramProfile profile;

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
        // for now just return the patterns data json
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String uuid = Utils.getUUID(req);

        if (profile == null) {
            profile = _profileService.findProfile(req,
                                                  profileName);
        }
        String patternsJSON = getWorkflowPatternJSON(profile,
                                                     uuid);
        PrintWriter pw = resp.getWriter();
        resp.setContentType("text/json");
        resp.setCharacterEncoding("UTF-8");
        pw.write(patternsJSON);
    }

    private String getWorkflowPatternJSON(IDiagramProfile profile,
                                          String uuid) {

        String retStr = "";
        Repository repository = profile.getRepository();
        try {
            Asset<String> patternAsset = repository.loadAssetFromPath(profile.getRepositoryGlobalDir(uuid) + "/" + PATTERNS_NAME + ".json");

            retStr = patternAsset.getAssetContent();
        } catch (Exception e) {
            _logger.error("Error retriving patterns info: " + e.getMessage());
        }

        return retStr;
    }
}
