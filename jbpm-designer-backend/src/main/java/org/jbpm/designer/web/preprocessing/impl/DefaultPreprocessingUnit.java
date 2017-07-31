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
package org.jbpm.designer.web.preprocessing.impl;

import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.repository.vfs.RepositoryDescriptor;
import org.jbpm.designer.web.preprocessing.IDiagramPreprocessingUnit;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.uberfire.io.IOService;

/**
 * @author Tihomir Surdilovic
 */
@Named("defaultPreprocessingUnit")
public class DefaultPreprocessingUnit implements IDiagramPreprocessingUnit {

    public void preprocess(HttpServletRequest request,
                           HttpServletResponse response,
                           IDiagramProfile profile,
                           ServletContext servletContext,
                           boolean readOnly,
                           boolean viewLocked,
                           IOService ioService,
                           RepositoryDescriptor descriptor) {
        // nothing to do
    }

    public String getOutData() {
        // TODO Auto-generated method stub
        return null;
    }
}
