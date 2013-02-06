/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;

/** 
 * Dictionary Servlet.
 * @author Tihomir Surdilovic
 */
public class DictionaryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String ACTION_LOAD = "load";
	private static final String ACTION_SAVE = "save";
	private static final String DICTIONARY_FNAME = "processdictionary";
	private static final String DICTIONARY_FEXT = "json";
	private static final Logger _logger = Logger.getLogger(DictionaryServlet.class);
	private ServletConfig config;

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
        this.config = config;
    }
	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        String uuid = req.getParameter("uuid");
        String profileName = req.getParameter("profile");
        String dvalue = req.getParameter("dvalue");

        if (profile == null) {
            profile = _profileService.findProfile(req, profileName);
        }
        Repository repository = profile.getRepository();

        if(action != null && action.equals(ACTION_SAVE)) {
        	storeInRepository(uuid, profile, dvalue, repository);
        	PrintWriter pw = resp.getWriter();
    		resp.setContentType("text/plain");
    		resp.setCharacterEncoding("UTF-8");
    		pw.write("saved");
        } else if(action != null && action.equals(ACTION_LOAD)) {
        	PrintWriter pw = resp.getWriter();
    		resp.setContentType("text/json");
    		resp.setCharacterEncoding("UTF-8");
    		pw.write(getFromRepository(uuid, profile, repository));
        }
	}
	
	private String getFromRepository(String uuid, IDiagramProfile profile, Repository repository) {
        try {
            Asset<String> dictionaryAsset = repository.loadAssetFromPath(profile.getRepositoryGlobalDir()+"/"+DICTIONARY_FNAME+"."+DICTIONARY_FEXT);
            if(dictionaryAsset != null) {
                return dictionaryAsset.getAssetContent();
            } else {
                return "false";
            }
		} catch (Exception e) {
            e.printStackTrace();
            _logger.error(e.getMessage());
        } 
		return "false";
	}
	
	private void storeInRepository(String uuid, IDiagramProfile profile, String dvalue, Repository repository) {
        try {
            repository.deleteAssetFromPath(profile.getRepositoryGlobalDir()+"/" + DICTIONARY_FNAME+"."+DICTIONARY_FEXT);

            AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);

            builder.name(DICTIONARY_FNAME)
                   .type(DICTIONARY_FEXT)
                   .location(profile.getRepositoryGlobalDir())
                   .content(dvalue);

            repository.createAsset(builder.getAsset());

		} catch (Exception e) {
            // we dont want to barf..just log that error happened
            _logger.error(e.getMessage());
        }
	}
	 
}
