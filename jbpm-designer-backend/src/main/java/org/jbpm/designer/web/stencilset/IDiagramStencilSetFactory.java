/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * A factory to plug and add stencilsets.
 * @author Antoine Toulme
 */
public interface IDiagramStencilSetFactory {

    /**
     * @param request the request as the context of the stencilset factory.
     * @return the stencilsets created.
     */
    public Set<IDiagramStencilSet> getStencilSets(HttpServletRequest request);
}
