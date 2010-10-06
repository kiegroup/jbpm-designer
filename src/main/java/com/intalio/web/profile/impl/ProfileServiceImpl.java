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

    private Map<String, IDiagramProfile> _registry = new HashMap<String, IDiagramProfile>();
    private Set<IDiagramProfileFactory> _factories = new HashSet<IDiagramProfileFactory>();
    
    public ProfileServiceImpl(ServletContext context) {
        _registry.put("default", new DefaultProfileImpl(context));
        
        // if we are in the OSGi world:
        if (getClass().getClassLoader() instanceof BundleReference) {
            final BundleContext bundleContext = ((BundleReference) getClass().getClassLoader()).getBundle().getBundleContext();
            ServiceReference[] sRefs = null;
            try {
                sRefs = bundleContext.getServiceReferences(IDiagramProfileFactory.class.getName(), null);
            } catch (InvalidSyntaxException e) {
            }
            if (sRefs != null) {
                for (ServiceReference sRef : sRefs) {
                    IDiagramProfileFactory service = (IDiagramProfileFactory) bundleContext.getService(sRef);
                    _factories.add(service);
                }
            }
            ServiceTrackerCustomizer cust = new ServiceTrackerCustomizer() {

                public void removedService(ServiceReference reference, Object service) {
                }

                public void modifiedService(ServiceReference reference, Object service) {
                }

                public Object addingService(ServiceReference reference) {
                    IDiagramProfileFactory service = (IDiagramProfileFactory) bundleContext.getService(reference);
                    _factories.add(service);
                    return service;
                }
            };
            ServiceTracker tracker = new ServiceTracker(bundleContext,
                    IDiagramProfileFactory.class.getName(), cust);
            tracker.open();
            // register self to make the default profile available to the world:
            bundleContext.registerService(IDiagramProfileService.class.getName(), this, new Hashtable());
        }
    }
    
    private Map<String, IDiagramProfile> assemblePlugins(HttpServletRequest request) {
        Map<String, IDiagramProfile> plugins = new HashMap<String, IDiagramProfile>(_registry);
        for (IDiagramProfileFactory factory : _factories) {
            for (IDiagramProfile  p : factory.getProfiles(request)) {
                plugins.put(p.getName(), p);
            }
        }
        return plugins;
    }
    
    public IDiagramProfile findProfile(HttpServletRequest request, String name) {
        return assemblePlugins(request).get(name);
    }

    public Collection<IDiagramProfile> getProfiles(HttpServletRequest request) {
        return assemblePlugins(request).values();
    }

    
}
