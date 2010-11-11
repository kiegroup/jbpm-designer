/***************************************
 * Copyright (c) Intalio, Inc 2010
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/
package com.intalio.web.plugin.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import com.intalio.web.plugin.IDiagramPlugin;

/**
 * @author Antoine Toulme
 * A default implementation of a plugin for plugins defined inside the Process Designer web application
 */
public class LocalPluginImpl extends LocalResource implements IDiagramPlugin {

    private boolean _core;
    private Map<String, Object> _properties = new HashMap<String, Object>();

    public LocalPluginImpl(String name, String path, ServletContext context, 
            boolean core, Map<String, Object> props) {
        super(name, path, context);
        StringBuilder localPath = new StringBuilder();
        localPath.append("js").append("/");
        localPath.append("Plugins").append("/").append(path);
        this._core = core;
        this._properties.putAll(props);
    }

    public boolean isCore() {
        return _core;
    }

    public Map<String, Object> getProperties() {
        return _properties;
    }

    public boolean isCompressable() {
        return true;
    }
}
