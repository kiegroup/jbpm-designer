/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class RDF2JSONServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(HttpServletRequest req,
                       HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setStatus(200);

        try {
            URL serverUrl = new URL(req.getScheme(),
                                    req.getServerName(),
                                    req.getServerPort(),
                                    "");

            PrintWriter out = res.getWriter();
            String rdfRepresentation = req.getParameter("rdf");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document rdfDoc = builder.parse(new ByteArrayInputStream(rdfRepresentation.getBytes("UTF-8")));

            String jsonRepresentation = RdfJsonTransformation.toJson(rdfDoc,
                                                                     serverUrl.toString()).toString();

            out.write(jsonRepresentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
