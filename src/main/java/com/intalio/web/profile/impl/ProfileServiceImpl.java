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
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

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

    private Map<String, IDiagramProfile> _registry = 
        new HashMap<String, IDiagramProfile>();
    private Set<IDiagramProfileFactory> _factories = 
        new HashSet<IDiagramProfileFactory>();
    
    /**
     * Initialize the service with a context
     * @param context the servlet context to initialize the profile.
     */
    public void init(ServletContext context) {
        _registry.put("default", new DefaultProfileImpl(context));
        
    }
    
    private Map<String, IDiagramProfile> assembleProfiles(HttpServletRequest request) {
        Map<String, IDiagramProfile> profiles = new HashMap<String, IDiagramProfile>(_registry);
        if (request != null) {
            for (IDiagramProfileFactory factory : _factories) {
                for (IDiagramProfile  p : factory.getProfiles(request)) {
                    profiles.put(p.getName(), p);
                }
            }
        }
        return profiles;
    }
    
    public IDiagramProfile findProfile(HttpServletRequest request, String name) {
        return assembleProfiles(request).get(name);
    }

    public Collection<IDiagramProfile> getProfiles(HttpServletRequest request) {
        return assembleProfiles(request).values();
    }

    public Set<IDiagramProfileFactory> getFactories() {
    	return _factories;
    }

    
}
