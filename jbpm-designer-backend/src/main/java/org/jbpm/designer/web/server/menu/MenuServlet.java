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

package org.jbpm.designer.web.server.menu;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.util.Utils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(displayName = "Menu Servlet", name = "MenuServlet",
        urlPatterns = "/menu/*")
public class MenuServlet extends HttpServlet {

    private Document _doc = null;

    protected IDiagramProfile profile;

    @Inject
    private IDiagramProfileService _profileService = null;
    private static final Logger _logger = LoggerFactory.getLogger(MenuServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        _profileService.init(config.getServletContext());
        String menufile = config.getServletContext().getRealPath("/menu.html");
        try {
            _doc = readDocument(menufile);
        } catch (Exception e) {
            throw new ServletException(
                    "Error while parsing menu.html",
                    e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String profileName = Utils.getDefaultProfileName(request.getParameter("profile"));
        if (profile == null) {
            profile = _profileService.findProfile(request,
                                                  profileName);
        }
        if (profile == null) {
            _logger.error("No profile with the name " + profileName
                                  + " was registered");
            throw new IllegalArgumentException(
                    "No profile with the name " + profileName +
                            " was registered");
        }
        Document doc = (Document) _doc.clone();
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setExpandEmptyElements(true);
        outputter.setFormat(format);
        String html = outputter.outputString(doc);
        StringTokenizer tokenizer = new StringTokenizer(html,
                                                        "@",
                                                        true);
        StringBuilder resultHtml = new StringBuilder();
        boolean tokenFound = false;
        boolean replacementMade = false;

        while (tokenizer.hasMoreTokens()) {
            String elt = tokenizer.nextToken();
            if ("rootid".equals(elt)) {
                resultHtml.append("vfs");
                replacementMade = true;
            } else if ("rootname".equals(elt)) {
                resultHtml.append("Designer Repository");
                replacementMade = true;
            } else if ("language".equals(elt)) {
                resultHtml.append("en");
                replacementMade = true;
            } else if ("@".equals(elt)) {
                if (replacementMade) {
                    tokenFound = false;
                    replacementMade = false;
                } else {
                    tokenFound = true;
                }
            } else {
                if (tokenFound) {
                    tokenFound = false;
                    resultHtml.append("@");
                }
                resultHtml.append(elt);
            }
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(resultHtml.toString());
    }

    private static Document readDocument(String path) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder(false);
        builder.setValidation(false);
        builder.setFeature("http://xml.org/sax/features/validation",
                           false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
                           false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
                           false);

        Document anotherDocument = builder.build(new File(path));
        return anotherDocument;
    }
}