/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.designer.web;

import java.util.Hashtable;
import javax.inject.Inject;

import org.jbpm.designer.web.filter.IFilterFactory;
import org.jbpm.designer.web.filter.impl.PluggableFilter;
import org.jbpm.designer.web.preference.IDiagramPreferenceService;
import org.jbpm.designer.web.profile.IDiagramProfileFactory;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;
import org.jbpm.designer.web.repository.IUUIDBasedRepositoryService;
import org.jbpm.designer.web.server.UUIDBasedRepositoryServlet;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Antoine Toulme
 *         <p>
 *         This activator is called when the bundle starts to register services for the bundle.
 */
public class Activator implements BundleActivator {

    @Inject
    private ProfileServiceImpl profile;

    @SuppressWarnings("rawtypes")
    public void start(final BundleContext context) throws Exception {
        {
            ServiceReference sRef =
                    context.getServiceReference(IUUIDBasedRepositoryService.class.getName());
            if (sRef != null) {
                IUUIDBasedRepositoryService service = (IUUIDBasedRepositoryService) context.getService(sRef);
                UUIDBasedRepositoryServlet._factory = service;
            } else {
                //use a service tracker to be called back when the IUUIDBasedRepositoryFactory is ready:
                ServiceTrackerCustomizer cust = new ServiceTrackerCustomizer() {

                    public void removedService(ServiceReference reference,
                                               Object service) {
                        //special servlet shutdown
                    }

                    public void modifiedService(ServiceReference reference,
                                                Object service) {
                        //reload?
                    }

                    public Object addingService(ServiceReference reference) {
                        IUUIDBasedRepositoryService service = (IUUIDBasedRepositoryService) context.getService(reference);
                        UUIDBasedRepositoryServlet._factory = service;
                        return service;
                    }
                };
                ServiceTracker tracker = new ServiceTracker(context,
                                                            IUUIDBasedRepositoryService.class.getName(),
                                                            cust);
                tracker.open();
            }
        }

        {
            ServiceReference sRef =
                    context.getServiceReference(IDiagramPreferenceService.class.getName());
            if (sRef != null) {
                IDiagramPreferenceService service = (IDiagramPreferenceService) context.getService(sRef);
            } else {
                //use a service tracker to be called back when the IUUIDBasedRepositoryFactory is ready:
                ServiceTrackerCustomizer cust = new ServiceTrackerCustomizer() {

                    public void removedService(ServiceReference reference,
                                               Object service) {
                        //special servlet shutdown
                    }

                    public void modifiedService(ServiceReference reference,
                                                Object service) {
                        //reload?
                    }

                    public Object addingService(ServiceReference reference) {
                        IDiagramPreferenceService service = (IDiagramPreferenceService) context.getService(reference);
                        return service;
                    }
                };
                ServiceTracker tracker = new ServiceTracker(context,
                                                            IDiagramPreferenceService.class.getName(),
                                                            cust);
                tracker.open();
            }
        }

        {
            ServiceReference sRef =
                    context.getServiceReference(IFilterFactory.class.getName());
            if (sRef != null) {
                IFilterFactory service = (IFilterFactory) context.getService(sRef);
                PluggableFilter.registerFilter(service);
            } else {
                //use a service tracker to be called back when the IFilterFactory is ready:
                ServiceTrackerCustomizer cust = new ServiceTrackerCustomizer() {

                    public void removedService(ServiceReference reference,
                                               Object service) {
                        //special servlet shutdown
                    }

                    public void modifiedService(ServiceReference reference,
                                                Object service) {
                        //reload?
                    }

                    public Object addingService(ServiceReference reference) {
                        IFilterFactory service = (IFilterFactory) context.getService(reference);
                        PluggableFilter.registerFilter(service);
                        return service;
                    }
                };
                ServiceTracker tracker = new ServiceTracker(context,
                                                            IFilterFactory.class.getName(),
                                                            cust);
                tracker.open();
            }
        }

        {
            ServiceReference[] sRefs = null;
            try {
                sRefs = context.getServiceReferences(IDiagramProfileFactory.class.getName(),
                                                     null);
            } catch (InvalidSyntaxException e) {
            }
            if (sRefs != null) {
                for (ServiceReference sRef : sRefs) {
                    IDiagramProfileFactory service = (IDiagramProfileFactory) context.getService(sRef);
                    profile.getFactories().add(service);
                }
            }
            ServiceTrackerCustomizer cust = new ServiceTrackerCustomizer() {

                public void removedService(ServiceReference reference,
                                           Object service) {
                }

                public void modifiedService(ServiceReference reference,
                                            Object service) {
                }

                public Object addingService(ServiceReference reference) {
                    IDiagramProfileFactory service = (IDiagramProfileFactory) context.getService(reference);
                    profile.getFactories().add(service);
                    return service;
                }
            };
            ServiceTracker tracker = new ServiceTracker(context,
                                                        IDiagramProfileFactory.class.getName(),
                                                        cust);
            tracker.open();
            // register self to make the default profile available to the world:
            context.registerService(IDiagramProfileService.class.getName(),
                                    profile,
                                    new Hashtable());
        }
    }

    public void stop(BundleContext bundleContext) throws Exception {

    }
}
