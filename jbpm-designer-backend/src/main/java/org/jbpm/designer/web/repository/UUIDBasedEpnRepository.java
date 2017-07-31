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

package org.jbpm.designer.web.repository;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.jbpm.designer.web.profile.IDiagramProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UUIDBasedEpnRepository implements IUUIDBasedRepository {

    private static final Logger _logger = LoggerFactory.getLogger(UUIDBasedEpnRepository.class);
    private final static String DEFAULTS_PATH = "defaults";

    private String _defaultsPath;

    public void configure(HttpServlet servlet) {
        _defaultsPath = servlet.getServletContext().getRealPath("/" + DEFAULTS_PATH);
    }

    public byte[] load(HttpServletRequest req,
                       String uuid,
                       IDiagramProfile profile,
                       ServletContext servletContext) throws Exception {
        // TODO hook up with Guvnor
        return new byte[0];
    }

    public void save(HttpServletRequest req,
                     String uuid,
                     String json,
                     String svg,
                     IDiagramProfile profile,
                     Boolean autosave) {
        //TODO hook up with Guvnor 
    }

    public String toXML(String json,
                        IDiagramProfile profile,
                        String preProcessingData) throws Exception {
        return profile.createMarshaller().parseModel(json,
                                                     preProcessingData);
    }
}
