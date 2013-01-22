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
package org.jbpm.designer.web.preprocessing;

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jbpm.designer.web.profile.IDiagramProfile;


/**
 * A service to find pre-processing units.
 * 
 * @author Tihomir Surdilovic
 *
 */
public interface IDiagramPreprocessingService {
    public Collection<IDiagramPreprocessingUnit> getRegisteredPreprocessingUnits(HttpServletRequest request);
    public IDiagramPreprocessingUnit findPreprocessingUnit(HttpServletRequest request, IDiagramProfile profile);
    public void init(ServletContext servletContext);
}
