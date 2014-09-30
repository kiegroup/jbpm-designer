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
package org.jbpm.designer.web.stencilset.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jbpm.designer.web.plugin.impl.LocalResource;
import org.jbpm.designer.web.stencilset.IDiagramStencilSet;


/**
 * A local implementation of the stencilset for stencilsets defined inside the platform.
 * @author Antoine Toulme
 *
 */
public class LocalStencilSetImpl extends LocalResource implements IDiagramStencilSet {

    private String _basePath;

    public LocalStencilSetImpl(String name, String path) {
        super(name, path  + "/" + name + ".json");
        _basePath = path;
    }

    public InputStream getResourceContents(String path) {
        File file = new File(_basePath + "/" + path);
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
}
