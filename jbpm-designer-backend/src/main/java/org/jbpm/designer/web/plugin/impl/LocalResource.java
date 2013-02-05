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
package org.jbpm.designer.web.plugin.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.ServletContext;

/**
 * @author Antoine Toulme
 * A default implementation of a plugin for plugins defined inside the Process Designer web application
 */
public class LocalResource {

    private String _path;
    private String _name;

    public LocalResource(String name, String path, ServletContext context) {
        this._name = name;
        StringBuilder localPath = new StringBuilder();
        localPath.append("org.jbpm.designer.jBPMDesigner").append("/").append("js").append("/").append("Plugins").append("/").append(path);
        this._path = context.getRealPath(localPath.toString());
    }
    
    public LocalResource(String name, String path) {
        this._name = name;
        this._path = path;
    }

    public String getName() {
        return _name;
    }

    public InputStream getContents() {
        try {
            return new FileInputStream(_path);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
