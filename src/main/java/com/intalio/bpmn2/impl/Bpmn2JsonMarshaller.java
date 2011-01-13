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
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.GlobalBusinessRuleTask;
import org.eclipse.bpmn2.GlobalChoreographyTask;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.GlobalScriptTask;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.GlobalUserTask;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.dd.di.Shape;

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
        generator.close();
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
        props.put("namespaces", "");
        props.put("targetnamespace", def.getTargetNamespace());
        props.put("typelanguage", def.getTypeLanguage());
        marshallProperties(props, generator);
        marshallStencil("BPMNDiagram", generator);
        
        for (RootElement rootElement : def.getRootElements()) {
            if (rootElement instanceof Process) {
                marshallProcess((Process) rootElement, def, generator);
            } else {
                throw new UnsupportedOperationException("TODO"); //TODO!
            }
        }
        
        generator.writeObjectFieldStart("stencilset");
        generator.writeObjectField("url", "/designer/stencilsets/bpmn2.0/bpmn2.0.json");
        generator.writeObjectField("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        generator.writeEndObject();
        generator.writeArrayFieldStart("ssextensions");
        generator.writeObject("http://oryx-editor.org/stencilsets/extensions/bpmncosts-2.0#");
        generator.writeEndArray();
        
        generator.writeEndObject();
    }

    private void marshallCallableElement(CallableElement callableElement, Definitions def, JsonGenerator generator) throws JsonGenerationException, IOException {
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
            marshallProcess((Process) callableElement, def, generator);
        } else {
            throw new UnsupportedOperationException("TODO"); //TODO!
        }
        generator.writeEndObject();
    }

    private void marshallProcess(Process process, Definitions def, JsonGenerator generator) throws JsonGenerationException, IOException {
    	BPMNPlane plane = null;
    	for (BPMNDiagram d: def.getDiagrams()) {
    		if (d != null) {
    			BPMNPlane p = d.getPlane();
    			if (p != null) {
    				if (p.getBpmnElement() == process) {
    					plane = p;
    					break;
    				}
    			}
    		}
    	}
    	if (plane == null) {
    		throw new IllegalArgumentException("Could not find BPMNDI information");
    	}
        generator.writeArrayFieldStart("childShapes");
        for (FlowElement flowElement: process.getFlowElements()) {
        	marshallFlowElement(flowElement, plane, generator);
        }
        generator.writeEndArray();
    }
    
    private void marshallFlowElement(FlowElement flowElement, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	generator.writeStartObject();
    	generator.writeObjectField("resourceId", flowElement.getId());
    	if (flowElement instanceof StartEvent) {
    		marshallStartEvent((StartEvent) flowElement, plane, generator);
    	}
    	generator.writeEndObject();
    }
    
    private void marshallStartEvent(StartEvent startEvent, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	Map<String, Object> properties = new LinkedHashMap<String, Object>();
    	properties.put("name", startEvent.getName());
        marshallProperties(properties, generator);
        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id", "StartNoneEvent");
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();
        generator.writeArrayFieldStart("outgoing");
        generator.writeEndArray();
        
        Bounds bounds = null;
        for (DiagramElement element: plane.getPlaneElement()) {
        	if (element instanceof BPMNShape && ((BPMNShape) element).getBpmnElement() == startEvent) {
        		bounds = ((Shape) element).getBounds();
        		break;
        	}
        }
        if (bounds == null) {
    		throw new IllegalArgumentException(
				"Could not find BPMNDI information for " + startEvent.getId());
    	}
        generator.writeObjectFieldStart("bounds");
        generator.writeObjectFieldStart("lowerRight");
        generator.writeObjectField("x", bounds.getX() + bounds.getHeight());
        generator.writeObjectField("y", bounds.getY() + bounds.getWidth());
        generator.writeEndObject();
        generator.writeObjectFieldStart("upperLeft");
        generator.writeObjectField("x", bounds.getX());
        generator.writeObjectField("y", bounds.getY());
        generator.writeEndObject();
        generator.writeEndObject();
        
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
