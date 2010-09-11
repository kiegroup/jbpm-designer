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
package com.intalio.bpmn2.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.Conversation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.GlobalBusinessRuleTask;
import org.eclipse.bpmn2.GlobalChoreographyTask;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.GlobalScriptTask;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.GlobalUserTask;
import org.eclipse.bpmn2.RootElement;

/**
 * @author Antoine Toulme
 * 
 * a marshaller to transform BPMN 2.0 elements into JSON format.
 *
 */
public class Bpmn2JsonMarshaller {

    public String marshall(Definitions def) throws IOException {
        StringWriter writer = new StringWriter();
        JsonFactory f = new JsonFactory();
        JsonGenerator generator = f.createJsonGenerator(writer);
        marshallDefinitions(def, generator);
        return writer.toString();
    }

    private void marshallDefinitions(Definitions def, JsonGenerator generator) throws JsonGenerationException, IOException {
        generator.writeStartObject();
        
        generator.writeObjectField("resourceId", def.getId());
        
        /*
         *  "properties":{"id":"",
               "name":"",
               "documentation":"",
               "auditing":"",
               "monitoring":"",
               "version":"",
               "author":"",
               "language":"English",
               "namespaces":"",
               "targetnamespace":"http://www.omg.org/bpmn20",
               "expressionlanguage":"http://www.w3.org/1999/XPath",
               "typelanguage":"http://www.w3.org/2001/XMLSchema",
               "creationdate":"",
               "modificationdate":""
               }
         */
        Map<String, Object> props = new LinkedHashMap<String, Object>();
        props.put("id", def.getId());
        props.put("namespaces", "");
        props.put("targetnamespace", def.getTargetNamespace());
        props.put("typelanguage", def.getTypeLanguage());
        marshallProperties(props, generator);
        marshallStencil("BPMNDiagram", generator);
        
        
        generator.writeArrayFieldStart("childShapes");
        for (RootElement rootElement : def.getRootElements()) {
            if (rootElement instanceof CallableElement) {
                marshallCallableElement((CallableElement) rootElement, generator);
            } else {
                throw new UnsupportedOperationException("TODO"); //TODO!
            }
        }
        generator.writeEndArray();
        
        
        generator.writeEndObject();
    }

    private void marshallCallableElement(CallableElement callableElement, JsonGenerator generator) throws JsonGenerationException, IOException {
        generator.writeStartObject();
        generator.writeObjectField("resourceId", callableElement.getId());
        
        if (callableElement instanceof Choreography) {
            marshallChoreography((Choreography) callableElement, generator);
        } else if (callableElement instanceof Conversation) {
            marshallConversation((Conversation) callableElement, generator);
        } else if (callableElement instanceof GlobalChoreographyTask) {
            marshallGlobalChoreographyTask((GlobalChoreographyTask) callableElement, generator);
        } else if (callableElement instanceof GlobalTask) {
            marshallGlobalTask((GlobalTask) callableElement, generator);
        } else if (callableElement instanceof Process) {
            marshallProcess((Process) callableElement, generator);
        } else {
            throw new UnsupportedOperationException("TODO"); //TODO!
        }
    }

    private void marshallProcess(Process process, JsonGenerator generator) {
        throw new UnsupportedOperationException("TODO"); //TODO!        
    }

    private void marshallGlobalTask(GlobalTask globalTask, JsonGenerator generator) {
        if (globalTask instanceof GlobalBusinessRuleTask) {
            
        } else if (globalTask instanceof GlobalManualTask) {
            
        } else if (globalTask instanceof GlobalScriptTask) {
            
        } else if (globalTask instanceof GlobalUserTask) {
            
        } else {
            
        }
    }

    private void marshallGlobalChoreographyTask(GlobalChoreographyTask callableElement, JsonGenerator generator) {
        throw new UnsupportedOperationException("TODO"); //TODO!
    }

    private void marshallConversation(Conversation callableElement, JsonGenerator generator) {
        throw new UnsupportedOperationException("TODO"); //TODO!
    }

    private void marshallChoreography(Choreography callableElement, JsonGenerator generator) {
        throw new UnsupportedOperationException("TODO"); //TODO!
    }
    
    private void marshallProperties(Map<String, Object> properties, JsonGenerator generator) throws JsonGenerationException, IOException {
        generator.writeObjectFieldStart("properties");
        for (Entry<String, Object> entry : properties.entrySet()) {
            generator.writeObjectField(entry.getKey(), String.valueOf(entry.getValue()));
        }
        generator.writeEndObject();
    }
    
    private void marshallStencil(String stencilId, JsonGenerator generator) throws JsonGenerationException, IOException {
        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id", stencilId);
        generator.writeEndObject();
    }
    
}
