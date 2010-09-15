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
        localPath.append("js").append("/").append("Plugins").append("/").append(path);
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
