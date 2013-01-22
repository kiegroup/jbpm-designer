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
package org.jbpm.designer.web.stencilset;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

/**
 * A service to access registered StencilSets.
 * 
 * @author Antoine Toulme
 *
 */
public interface IDiagramStencilSetService {
    
    /**
     * @param request the request as context.
     * @return the registered stencilsets.
     */
    public Collection<IDiagramStencilSet> getRegisteredStencilSets(HttpServletRequest request);
    
    /**
     * Finds a stencilset by name in the context of the request.
     * @param request the request as context.
     * @param name the name of a stencilset
     * @return the stencilset
     */
    public IDiagramStencilSet findStencilSet(HttpServletRequest request, String name);

}
