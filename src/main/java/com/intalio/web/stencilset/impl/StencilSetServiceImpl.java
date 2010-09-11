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
package com.intalio.web.stencilset.impl;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletContext;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intalio.web.stencilset.StencilSet;
import com.intalio.web.stencilset.StencilSetFactory;
import com.intalio.web.stencilset.StencilSetService;

/**
 * A service to serve stencilsets.
 * 
 * @author Antoine Toulme
 *
 */
public class StencilSetServiceImpl implements StencilSetService {

    private static Logger _logger = LoggerFactory.getLogger(StencilSetServiceImpl.class);
    
    private Map<String, StencilSet> _registry = new HashMap<String, StencilSet>();

    public StencilSetServiceImpl(ServletContext context) {
        initializeLocalStencilSets(context);
        
        // if we are in the OSGi world:
        if (getClass().getClassLoader() instanceof BundleReference) {
            final BundleContext bundleContext = ((BundleReference) getClass().getClassLoader()).getBundle().getBundleContext();
            ServiceReference[] sRefs = null;
            try {
                sRefs = bundleContext.getServiceReferences(StencilSet.class.getName(), null);
            } catch (InvalidSyntaxException e) {
            }
            if (sRefs != null) {
                for (ServiceReference sRef : sRefs) {
                    StencilSetFactory service = (StencilSetFactory) bundleContext.getService(sRef);
                    for (StencilSet p : service.createStencilSets()) {
                        _registry.put(p.getName(), p);
                    }
                }
            } else {
                ServiceTrackerCustomizer cust = new ServiceTrackerCustomizer() {

                    public void removedService(ServiceReference reference, Object service) {
                    }

                    public void modifiedService(ServiceReference reference, Object service) {
                    }

                    public Object addingService(ServiceReference reference) {
                        StencilSetFactory service = (StencilSetFactory) bundleContext.getService(reference);
                        for (StencilSet p : service.createStencilSets()) {
                            _registry.put(p.getName(), p);
                        }
                        return service;
                    }
                };
                ServiceTracker tracker = new ServiceTracker(bundleContext,
                        StencilSetFactory.class.getName(), cust);
                tracker.open();
            }
            //also register yourself to allow the construction of profiles by remote services:
            bundleContext.registerService(StencilSetService.class.getName(), this, new Hashtable());
        }
    }

    private void initializeLocalStencilSets(ServletContext context) {
        File ssDir = new File(context.getRealPath("stencilsets"));
        for (File dir : ssDir.listFiles()) {
            if (dir.isDirectory()) {
                _registry.put(dir.getName(), new LocalStencilSetImpl(dir.getName(), dir.getAbsolutePath()));
            }
        }
    }
    
    
    public Collection<StencilSet> getRegisteredStencilSets() {
        return Collections.unmodifiableCollection(_registry.values());
    }
    
    public StencilSet findStencilSet(String name) {
        return _registry.get(name);
    }
}
