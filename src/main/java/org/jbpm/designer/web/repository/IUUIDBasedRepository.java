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
package org.jbpm.designer.web.repository;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.jbpm.designer.web.profile.IDiagramProfile;


/**
 * @author Antoine Toulme
 * 
 * This interface defines the way the models should be saved and loaded.
 *
 */
public interface IUUIDBasedRepository {
    
    /**
     * Configure the repository in the context of the servlet
     * @param servlet the servlet which will use this repository.
     */
    public void configure(HttpServlet servlet);
    
    /**
     * @param req the request from the user.
     * @param uuid the id of the model.
     * @param ext the file extension to apply to the model.
     * @param loadExt the external url to load from
     * @return the model as a set of bytes.
     */
    public byte[] load(HttpServletRequest req, String uuid, IDiagramProfile profile) throws Exception;
    
    /**
     * Saves the model inside the repository.
     * @param req the request from the user.
     * @param uuid the id of the model
     * @param json the json model
     * @param svg the svg representation of the model
     * @param profile the profile
     */
    public void save(HttpServletRequest req, String uuid, String json, String svg, IDiagramProfile profile, Boolean autosave);
    
    /**
     * Transforms given json to bpmn2 xml
     * @param json
     * @param profile
     * @param preProcessingData
     * @return bpmn2 xml
     */
    public String toXML(String json, IDiagramProfile profile, String preProcessingData);

}
