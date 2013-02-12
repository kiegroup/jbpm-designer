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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jbpm.designer.util.ConfigurationProvider;
import org.jbpm.designer.web.stencilset.IDiagramStencilSet;
import org.jbpm.designer.web.stencilset.IDiagramStencilSetFactory;
import org.jbpm.designer.web.stencilset.IDiagramStencilSetService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A service to serve stencilsets.
 * 
 * @author Antoine Toulme
 *
 */
public class StencilSetServiceImpl implements IDiagramStencilSetService {

    private static Logger _logger = LoggerFactory.getLogger(StencilSetServiceImpl.class);
    public static final String designer_path = ConfigurationProvider.getInstance().getDesignerContext();


    private Map<String, IDiagramStencilSet> _registry = new HashMap<String, IDiagramStencilSet>();
    private Set<IDiagramStencilSetFactory> _factories = new HashSet<IDiagramStencilSetFactory>();

    public StencilSetServiceImpl(ServletContext context) {
        initializeLocalStencilSets(context);
        
        // if we are in the OSGi world:
        if (getClass().getClassLoader() instanceof BundleReference) {
            final BundleContext bundleContext = ((BundleReference) getClass().getClassLoader()).getBundle().getBundleContext();
            ServiceReference[] sRefs = null;
            try {
                sRefs = bundleContext.getServiceReferences(IDiagramStencilSet.class.getName(), null);
            } catch (InvalidSyntaxException e) {
            }
            if (sRefs != null) {
                for (ServiceReference sRef : sRefs) {
                    IDiagramStencilSetFactory service = (IDiagramStencilSetFactory) bundleContext.getService(sRef);
                    _factories.add(service);
                }
            }
            ServiceTrackerCustomizer cust = new ServiceTrackerCustomizer() {

                public void removedService(ServiceReference reference, Object service) {
                }

                public void modifiedService(ServiceReference reference, Object service) {
                }

                public Object addingService(ServiceReference reference) {
                    IDiagramStencilSetFactory service = (IDiagramStencilSetFactory) bundleContext.getService(reference);
                    _factories.add(service);
                    return service;
                }
            };
            ServiceTracker tracker = new ServiceTracker(bundleContext,
                    IDiagramStencilSetFactory.class.getName(), cust);
            tracker.open();
            //also register yourself to allow the construction of profiles by remote services:
            bundleContext.registerService(IDiagramStencilSetService.class.getName(), this, new Hashtable());
        }
    }

    private void initializeLocalStencilSets(ServletContext context) {
        File ssDir = new File(context.getRealPath( designer_path + "stencilsets"));
        for (File dir : ssDir.listFiles()) {
            if (dir.isDirectory()) {
                _registry.put(dir.getName(), new LocalStencilSetImpl(dir.getName(), dir.getAbsolutePath()));
            }
        }
    }
    
    private Map<String, IDiagramStencilSet> assembleStencilSets(HttpServletRequest request) {
        Map<String, IDiagramStencilSet> stencilsets = new HashMap<String, IDiagramStencilSet>(_registry);
        for (IDiagramStencilSetFactory factory : _factories) {
            for (IDiagramStencilSet  p : factory.getStencilSets(request)) {
                stencilsets.put(p.getName(), p);
            }
        }
        return stencilsets;
    }
    
    
    public Collection<IDiagramStencilSet> getRegisteredStencilSets(HttpServletRequest request) {
        return assembleStencilSets(request).values();
    }
    
    public IDiagramStencilSet findStencilSet(HttpServletRequest request, String name) {
        return assembleStencilSets(request).get(name);
    }
}
