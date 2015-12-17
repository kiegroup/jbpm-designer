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

package org.jbpm.designer.web.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.designer.util.ConfigurationProvider;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * Deals with stencil patterns data.
 *
 * @author Tihomir Surdilovic
 */
public class StencilPatternsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger _logger = LoggerFactory.getLogger(StencilPatternsServlet.class);
    private static final String designer_path = ConfigurationProvider.getInstance().getDesignerContext();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // for now just return the patterns data json
        try {
            String patternsDataPath = getServletContext().getRealPath(designer_path + "defaults/patterns.json");

            PrintWriter pw = resp.getWriter();
            resp.setContentType("text/json");
            resp.setCharacterEncoding("UTF-8");
            pw.write(readFile(patternsDataPath));
        } catch (IOException e) {
            _logger.error(e.getMessage());
            PrintWriter pw = resp.getWriter();
            resp.setContentType("text/json");
            resp.setCharacterEncoding("UTF-8");
            pw.write("");
        }
    }

    private String readFile(String pathname) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new File(pathname), "UTF-8");
        String lineSeparator = System.getProperty("line.separator");
        try {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }
}
