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

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.bpmn2.validation.BPMN2SyntaxChecker;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;


/** 
 * 
 * Check syntax of a BPMN2 process.
 * 
 * @author Tihomir Surdilovic
 */
public class SyntaxCheckerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    @Inject
    private IDiagramProfileService _profileService = null;

	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		String json = req.getParameter("data");
        String profileName = req.getParameter("profile");
        String preprocessingData = req.getParameter("pp");
        IDiagramProfile profile = _profileService.findProfile(req, profileName);

        BPMN2SyntaxChecker checker = new BPMN2SyntaxChecker(json, preprocessingData, profile);
		checker.checkSyntax();
		resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        if(checker.errorsFound()) {
			resp.getWriter().write(checker.getErrorsAsJson().toString());
		}
	}
}
