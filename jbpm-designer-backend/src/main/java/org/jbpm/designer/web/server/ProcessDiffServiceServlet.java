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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.RepositoryInfo;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(displayName = "ProcessDiffService", name = "ProcessDiffServiceServlet",
        urlPatterns = "/processdiff")
public class ProcessDiffServiceServlet extends HttpServlet {

    private static final Logger _logger = LoggerFactory
            .getLogger(ProcessDiffServiceServlet.class);

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
        String uuid = Utils.getUUID(req);
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String action = req.getParameter("action");
        String versionNum = req.getParameter("version");

        if (profile == null) {
            profile = _profileService.findProfile(req,
                                                  profileName);
        }
        String[] packageAssetInfo = ServletUtil.findPackageAndAssetInfo(uuid,
                                                                        profile);
        String packageName = packageAssetInfo[0];
        String assetName = packageAssetInfo[1];
        if (action != null && action.equals("getversion") && versionNum != null) {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("text/xml");
            try {
                resp.getWriter().write(getAssetVerionSource(packageName,
                                                            assetName,
                                                            versionNum,
                                                            profile));
            } catch (Throwable t) {
                resp.getWriter().write("");
            }
        } else {
            List<String> versionList;
            try {
                versionList = getAssetVersions(packageName,
                                               assetName,
                                               uuid,
                                               profile);
            } catch (Throwable t) {
                versionList = new ArrayList<String>();
            }
            JSONObject jsonObject = new JSONObject();
            if (versionList != null && versionList.size() > 0) {
                for (String version : versionList) {
                    try {
                        jsonObject.put(version,
                                       version);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            resp.getWriter().write(jsonObject.toString());
        }
    }

    private String getAssetVerionSource(String packageName,
                                        String assetName,
                                        String versionNum,
                                        IDiagramProfile profile) {
        try {
            String versionURL = RepositoryInfo.getRepositoryProtocol(profile)
                    + "://"
                    + RepositoryInfo.getRepositoryHost(profile)
                    + "/"
                    + RepositoryInfo.getRepositorySubdomain(profile).substring(0,
                                                                               RepositoryInfo.getRepositorySubdomain(profile).indexOf("/"))
                    + "/rest/packages/" + URLEncoder.encode(packageName,
                                                            "UTF-8") + "/assets/" + assetName
                    + "/versions/" + versionNum + "/source/";

            return IOUtils.toString(ServletUtil.getInputStreamForURL(versionURL,
                                                                     "GET",
                                                                     profile),
                                    "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    private List<String> getAssetVersions(String packageName,
                                          String assetName,
                                          String uuid,
                                          IDiagramProfile profile) {
        try {
            String assetVersionURL = RepositoryInfo.getRepositoryProtocol(profile)
                    + "://"
                    + RepositoryInfo.getRepositoryHost(profile)
                    + "/"
                    + RepositoryInfo.getRepositorySubdomain(profile).substring(0,
                                                                               RepositoryInfo.getRepositorySubdomain(profile).indexOf("/"))
                    + "/rest/packages/" + URLEncoder.encode(packageName,
                                                            "UTF-8") + "/assets/" + assetName
                    + "/versions/";
            List<String> versionList = new ArrayList<String>();

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory
                    .createXMLStreamReader(ServletUtil.getInputStreamForURL(
                            assetVersionURL,
                            "GET",
                            profile),
                                           "UTF-8");
            boolean isFirstTitle = true;
            String title = "";
            while (reader.hasNext()) {
                int next = reader.next();
                if (next == XMLStreamReader.START_ELEMENT) {
                    if ("title".equals(reader.getLocalName())) {
                        if (isFirstTitle) {
                            isFirstTitle = false;
                        } else {
                            versionList.add(reader.getElementText());
                        }
                    }
                }
            }
            return versionList;
        } catch (Exception e) {
            _logger.error(e.getMessage());
            return null;
        }
    }
}
