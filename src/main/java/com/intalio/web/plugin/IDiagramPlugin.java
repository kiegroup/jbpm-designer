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
package com.intalio.web.plugin;

import java.io.InputStream;
import java.util.Map;

/**
 * The interface defining a plugin.
 * 
 * @author Antoine Toulme
 */
public interface IDiagramPlugin {

    /**
     * @return the name of the plugin.
     * The name of the plugin should be unique amongst all plugins, so you should make sure to qualify it.
     * 
     */
    public String getName();
    
    /**
     * @return the contents of a plugin.
     * The contents of the plugin file.
     * 
     * The object returned by this method MUST be closed explicitely.
     */
    public InputStream getContents();
    
    /**
     * @return true if the plugin should be considered a core plugin and loaded for all profiles.
     */
    public boolean isCore();
    
    /**
     * @return the properties of the plugin
     */
    public Map<String, Object> getProperties();

    /**
     * @return true if the contents of the plugin can be compressed.
     */
    public boolean isCompressable();
}
