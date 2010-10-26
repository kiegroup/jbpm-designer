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
package com.intalio.web.profile.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.intalio.web.profile.IDiagramProfile;
import com.intalio.web.profile.IDiagramProfileFactory;
import com.intalio.web.profile.IDiagramProfileService;

/**
 * a service to register profiles.
 * 
 * @author Antoine Toulme
 * 
 */
public class ProfileServiceImpl implements IDiagramProfileService {
	
	public static ProfileServiceImpl INSTANCE = new ProfileServiceImpl();

    private Map<String, IDiagramProfile> _registry = new HashMap<String, IDiagramProfile>();
    private Set<IDiagramProfileFactory> _factories = new HashSet<IDiagramProfileFactory>();
    
    private ProfileServiceImpl() {
    }
    
    public void setServletContext(ServletContext context) {
        _registry.put("default", new DefaultProfileImpl(context));
    }
    
    public Set<IDiagramProfileFactory> getFactories() {
    	return _factories;
    }
        
    private Map<String, IDiagramProfile> assemblePlugins(HttpServletRequest request) {
        Map<String, IDiagramProfile> plugins = new HashMap<String, IDiagramProfile>(_registry);
        if (request != null) {
            for (IDiagramProfileFactory factory : _factories) {
                for (IDiagramProfile  p : factory.getProfiles(request)) {
                    plugins.put(p.getName(), p);
                }
            }
        }
        System.err.println(plugins.keySet());
        return plugins;
    }
    
    public IDiagramProfile findProfile(HttpServletRequest request, String name) {
        return assemblePlugins(request).get(name);
    }

    public Collection<IDiagramProfile> getProfiles(HttpServletRequest request) {
        return assemblePlugins(request).values();
    }

    
}
