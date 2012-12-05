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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.designer.web.profile.IDiagramProfile;


/**
 * A service to perform pre-processing of data.
 * 
 * @author Tihomir Surdilovic
 *
 */
public interface IDiagramPreprocessingUnit {
    public void preprocess(HttpServletRequest request, HttpServletResponse response, IDiagramProfile profile, ServletContext serlvetContext);
    public String getOutData();
}
