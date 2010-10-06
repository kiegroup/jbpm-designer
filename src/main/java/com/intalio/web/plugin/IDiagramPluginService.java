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

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

/**
 * A service to access the registered plugins and find plugins.
 * 
 * @author Antoine Toulme
 *
 */
public interface IDiagramPluginService {

    /**
     * @param request the context in which the plugins are requested.
     * @return a unmodifiable collection of the registered plugins.
     */
    public Collection<IDiagramPlugin> getRegisteredPlugins(HttpServletRequest request);
    
    /**
     * @param request the context in which the plugin is requested
     * @param name the name of the plugin to find
     * @return the plugin object or null
     */
    public IDiagramPlugin findPlugin(HttpServletRequest request, String name);
}
