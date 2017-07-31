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
package org.jbpm.designer.web.stencilset;

import java.io.InputStream;

/**
 * An interface to define a stencilset in the editor
 * @author Antoine Toulme
 */
public interface IDiagramStencilSet {

    /**
     * @return the name of the stencilset
     */
    public String getName();

    /**
     * @return the contents of the stencilset file.
     */
    public InputStream getContents();

    /**
     * Finds and returns the contents of a resource located under the stencilset.
     * @param path the path of the resource
     * @return the contents of the resouce.
     */
    public InputStream getResourceContents(String path);
}
