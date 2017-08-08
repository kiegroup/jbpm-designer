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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(displayName = "FileStore", name = "FileStoreServlet",
        urlPatterns = "/filestore")
public class FileStoreServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger _logger = LoggerFactory.getLogger(FileStoreServlet.class);
    private String retData;

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
        req.setCharacterEncoding("UTF-8");
        String fname = req.getParameter("fname");
        String fext = req.getParameter("fext");
        String data = req.getParameter("data");
        String dataEncoded = req.getParameter("data_encoded");

        String storeInRepo = req.getParameter("storeinrepo");
        String profileName = Utils.getDefaultProfileName(req.getParameter("profile"));
        String uuid = Utils.getUUID(req);
        String processid = req.getParameter("processid");

        if (profile == null) {
            profile = _profileService.findProfile(req,
                                                  profileName);
        }
        Repository repository = profile.getRepository();

        retData = "";
        if (dataEncoded != null && dataEncoded.length() > 0) {
            retData = new String(Base64.decodeBase64(dataEncoded));
        } else {
            retData = StringEscapeUtils.ESCAPE_XML11.translate(data);
        }

        if (fext != null && (fext.equals("bpmn2") || fext.equals("svg"))) {
            try {
                if (fext.equals("bpmn2")) {
                    resp.setContentType("application/xml; charset=UTF-8");
                } else if (fext.equals("svg")) {
                    resp.setContentType("image/svg+xml; charset=UTF-8");
                }

                if (processid != null) {
                    resp.setHeader("Content-Disposition",
                                   "attachment; filename=\"" + processid + "." + fext + "\"");
                } else if (uuid != null) {
                    resp.setHeader("Content-Disposition",
                                   "attachment; filename=\"" + uuid + "." + fext + "\"");
                } else {
                    resp.setHeader("Content-Disposition",
                                   "attachment; filename=\"" + fname + "." + fext + "\"");
                }
                resp.getWriter().write(retData);
            } catch (Exception e) {
                resp.sendError(500,
                               e.getMessage());
            }

            if (storeInRepo != null && storeInRepo.equals("true")) {
                storeInRepository(uuid,
                                  retData,
                                  fext,
                                  processid,
                                  repository);
            }
        }
    }

    private void storeInRepository(String uuid,
                                   String retData,
                                   String fext,
                                   String processid,
                                   Repository repository) {
        try {
            if (processid != null) {
                Asset<byte[]> processAsset = repository.loadAsset(uuid);

                if (processid.startsWith(".")) {
                    processid = processid.substring(1,
                                                    processid.length());
                }
                String assetFullName = processid + "-" + fext + "." + fext;

                repository.deleteAssetFromPath(processAsset.getAssetLocation() + assetFullName);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                OutputStreamWriter outStreamWriter = new OutputStreamWriter(outputStream);
                outStreamWriter.write(retData);
                outStreamWriter.close();

                AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);

                builder.name(processid + "-" + fext)
                        .type(fext)
                        .location(processAsset.getAssetLocation())
                        .version(processAsset.getVersion())
                        .content(outputStream.toByteArray());

                Asset<byte[]> resourceAsset = builder.getAsset();

                repository.createAsset(resourceAsset);
            }
        } catch (Exception e) {
            _logger.error(e.getMessage());
        }
    }

    public String getRetData() {
        return this.retData;
    }
}
