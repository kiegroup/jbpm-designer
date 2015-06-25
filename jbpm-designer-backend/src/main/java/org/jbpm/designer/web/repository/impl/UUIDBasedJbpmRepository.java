/*
 * Copyright 2015 JBoss Inc
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

package org.jbpm.designer.web.repository.impl;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import bpsim.impl.BpsimPackageImpl;
import org.jbpm.designer.server.service.DefaultDesignerAssetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.drools.impl.DroolsPackageImpl;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.repository.IUUIDBasedRepository;


public class UUIDBasedJbpmRepository implements IUUIDBasedRepository {

    private static final Logger _logger = LoggerFactory.getLogger(UUIDBasedJbpmRepository.class);
    private final static String DEFAULTS_PATH = "defaults";
    
    private String _defaultsPath;

    public void configure(HttpServlet servlet) {
        _defaultsPath = servlet.getServletContext().getRealPath("/" + DEFAULTS_PATH);
    }

    public byte[] load(HttpServletRequest req, String uuid, IDiagramProfile profile, ServletContext servletContext) throws Exception {
        Repository repository = profile.getRepository();
        String preProcessingParam = req.getParameter("pp");
        String processxml = ((Asset<String>)repository.loadAsset(uuid)).getAssetContent();

        if(processxml != null && processxml.length() > 0) {
        	try {
                DroolsPackageImpl.init();
                BpsimPackageImpl.init();
                String processjson = profile.createUnmarshaller().parseModel(processxml, profile, preProcessingParam);
                return processjson.getBytes("UTF-8");
            } catch(Exception e) {
                return loadDefaultProcess(profile, preProcessingParam);
            }
        } else {
            return loadDefaultProcess(profile, preProcessingParam);
        }
    }

    private byte[] loadDefaultProcess(IDiagramProfile profile, String preProcessingParam) {
        try {
            String defaultProcessContent = DefaultDesignerAssetService.PROCESS_STUB.replaceAll( "\\$\\{processid\\}", "defaultprocessid" );
            String processjson = profile.createUnmarshaller().parseModel(defaultProcessContent, profile, preProcessingParam);
            return processjson.getBytes("UTF-8");
        } catch(Exception e) {
            return new byte[0];
        }
    }

    public void save(HttpServletRequest req, String uuid, String json,
            String svg, IDiagramProfile profile, Boolean autosave) {
        // Guvnor is responsible for saving 
    }
    
    public String toXML(String json, IDiagramProfile profile, String preProcessingData) throws Exception {
        return profile.createMarshaller().parseModel(json, preProcessingData);
    }
}
