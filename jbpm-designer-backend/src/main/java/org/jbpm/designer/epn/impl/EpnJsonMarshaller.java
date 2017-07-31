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
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.eclipse.dd.di.DiagramElement;
import org.jbpm.designer.web.profile.IDiagramProfile;

/**
 * a marshaller to transform EPN elements into JSON format.
 */
public class EpnJsonMarshaller {

    private Map<String, DiagramElement> _diagramElements = new HashMap<String, DiagramElement>();
    private IDiagramProfile profile;

    public void setProfile(IDiagramProfile profile) {
        this.profile = profile;
    }

    public String marshall(String definitions) throws IOException { //TODO fix this when we have the EPN ecore model
        StringWriter writer = new StringWriter();
        JsonFactory f = new JsonFactory();
        JsonGenerator generator = f.createJsonGenerator(writer);
        //TODO do the heavy lifting here passing in the writer and the json generator
        generator.close();
        return writer.toString();
    }
}
