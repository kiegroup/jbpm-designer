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

package org.jbpm.designer.epn.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.jbpm.designer.epn.EpnMarshallerHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * an unmarshaller to transform JSON into EPN elements.
 */
public class EpnJsonUnmarshaller {

    private Map<Object, String> _objMap = new HashMap<Object, String>();
    private Map<String, Object> _idMap = new HashMap<String, Object>();

    private List<EpnMarshallerHelper> _helpers;

    public EpnJsonUnmarshaller() {
        _helpers = new ArrayList<EpnMarshallerHelper>();
        // load the helpers to place them in field
        if (getClass().getClassLoader() instanceof BundleReference) {
            BundleContext context = ((BundleReference) getClass().getClassLoader()).
                    getBundle().getBundleContext();
            try {
                ServiceReference[] refs = context.getAllServiceReferences(
                        EpnMarshallerHelper.class.getName(),
                        null);
                for (ServiceReference ref : refs) {
                    EpnMarshallerHelper helper = (EpnMarshallerHelper) context.getService(ref);
                    _helpers.add(helper);
                }
            } catch (InvalidSyntaxException e) {
            }
        }
    }

    public Object unmarshall(String json) throws JsonParseException, IOException {
        return ""; //TODO empty for now until we finish the epn ecore model
    }
}
