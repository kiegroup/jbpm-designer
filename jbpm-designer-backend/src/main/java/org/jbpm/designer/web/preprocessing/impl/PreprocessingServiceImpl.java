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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jbpm.designer.web.preprocessing.IDiagramPreprocessingService;
import org.jbpm.designer.web.preprocessing.IDiagramPreprocessingUnit;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.uberfire.backend.vfs.VFSService;

/**
 * @author Tihomir Surdilovic
 */
public class PreprocessingServiceImpl implements IDiagramPreprocessingService {

    @Inject
    @Named("defaultPreprocessingUnit")
    private IDiagramPreprocessingUnit defaultPreprocessingUnit;

    @Inject
    @Named("jbpmPreprocessingUnit")
    private IDiagramPreprocessingUnit jbpmPreprocessingUnit;

    public final static PreprocessingServiceImpl INSTANCE = new PreprocessingServiceImpl();
    private Map<String, IDiagramPreprocessingUnit> _registry = new HashMap<String, IDiagramPreprocessingUnit>();

    public Collection<IDiagramPreprocessingUnit> getRegisteredPreprocessingUnits(
            HttpServletRequest request) {
        Map<String, IDiagramPreprocessingUnit> preprocessingUnits = new HashMap<String, IDiagramPreprocessingUnit>(_registry);
        return new ArrayList<IDiagramPreprocessingUnit>(preprocessingUnits.values());
    }

    public IDiagramPreprocessingUnit findPreprocessingUnit(
            HttpServletRequest request,
            IDiagramProfile profile) {
        Map<String, IDiagramPreprocessingUnit> preprocessingUnits = new HashMap<String, IDiagramPreprocessingUnit>(_registry);
        return preprocessingUnits.get(profile.getName());
    }

    public void init(ServletContext context,
                     VFSService vfsService) {
        ((JbpmPreprocessingUnit) jbpmPreprocessingUnit).init(context,
                                                             vfsService);
        _registry.put("default",
                      defaultPreprocessingUnit);
        _registry.put("jbpm",
                      jbpmPreprocessingUnit);
    }
}
