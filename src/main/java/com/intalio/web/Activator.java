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
package com.intalio.web;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.intalio.web.repository.IUUIDBasedRepositoryService;
import com.intalio.web.server.UUIDBasedRepositoryServlet;


/**
 * @author Antoine Toulme
 * 
 * This activator is called when the bundle starts to register services for the bundle.
 *
 */
public class Activator implements BundleActivator {

    public void start(final BundleContext context) throws Exception {
        ServiceReference sRef =
            context.getServiceReference(IUUIDBasedRepositoryService.class.getName());
        if (sRef != null) {
            IUUIDBasedRepositoryService service = (IUUIDBasedRepositoryService) context.getService(sRef);
            UUIDBasedRepositoryServlet._factory = service;
        } else {
            //use a service tracker to be called back when the IUUIDBasedRepositoryFactory is ready:
            ServiceTrackerCustomizer cust = new ServiceTrackerCustomizer() {

                public void removedService(ServiceReference reference, Object service) {
                    //special servlet shutdown
                }

                public void modifiedService(ServiceReference reference, Object service) {
                    //reload?
                }

                public Object addingService(ServiceReference reference) {
                    IUUIDBasedRepositoryService service = (IUUIDBasedRepositoryService) context.getService(reference);
                    UUIDBasedRepositoryServlet._factory = service;
                    return service;
                }
            };
            ServiceTracker tracker = new ServiceTracker(context,
                    IUUIDBasedRepositoryService.class.getName(), cust);
            tracker.open();

        }
    }

    public void stop(BundleContext bundleContext) throws Exception {

    }
}
