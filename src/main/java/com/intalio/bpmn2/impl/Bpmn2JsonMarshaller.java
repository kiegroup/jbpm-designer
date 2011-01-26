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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.ComplexGateway;
import org.eclipse.bpmn2.Conversation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.GlobalBusinessRuleTask;
import org.eclipse.bpmn2.GlobalChoreographyTask;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.GlobalScriptTask;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.GlobalUserTask;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.di.DiagramElement;

/**
 * @author Antoine Toulme
 * 
 * a marshaller to transform BPMN 2.0 elements into JSON format.
 *
 */
public class Bpmn2JsonMarshaller {
	
	private Map<String, DiagramElement> _diagramElements = new HashMap<String, DiagramElement>();

    public String marshall(Definitions def) throws IOException {
        StringWriter writer = new StringWriter();
        JsonFactory f = new JsonFactory();
        JsonGenerator generator = f.createJsonGenerator(writer);
        marshallDefinitions(def, generator);
        generator.close();
        return writer.toString();
    }

    private void marshallDefinitions(Definitions def, JsonGenerator generator) throws JsonGenerationException, IOException {
        try{
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
        } finally {
        	_diagramElements.clear();
        }
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
    	} else if (flowElement instanceof EndEvent) {
    		marshallEndEvent((EndEvent) flowElement, plane, generator);
    	} else if (flowElement instanceof Task) {
    		marshallTask((Task) flowElement, plane, generator);
    	} else if (flowElement instanceof SequenceFlow) {
    		marshallSequenceFlow((SequenceFlow) flowElement, plane, generator);
    	} else if (flowElement instanceof ParallelGateway) {
    		marshallParallelGateway((ParallelGateway) flowElement, plane, generator);
    	} else if (flowElement instanceof ExclusiveGateway) {
    		marshallExclusiveGateway((ExclusiveGateway) flowElement, plane, generator);
    	} else if (flowElement instanceof InclusiveGateway) {
    		marshallInclusiveGateway((InclusiveGateway) flowElement, plane, generator);
    	} else if (flowElement instanceof EventBasedGateway) {
    		marshallEventBasedGateway((EventBasedGateway) flowElement, plane, generator);
    	} else if (flowElement instanceof ComplexGateway) {
    		marshallComplexGateway((ComplexGateway) flowElement, plane, generator);
    	} else if (flowElement instanceof CallActivity) {
    		marshallCallActivity((CallActivity) flowElement, plane, generator);
    	}
    	generator.writeEndObject();
    }
    
    private void marshallStartEvent(StartEvent startEvent, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	marshallNode(startEvent, "StartNoneEvent", plane, generator);
    }
    
    private void marshallEndEvent(EndEvent endEvent, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	List<EventDefinition> eventDefinitions = endEvent.getEventDefinitions();
    	if (eventDefinitions == null || eventDefinitions.size() == 0) {
    		marshallNode(endEvent, "EndNoneEvent", plane, generator);
    	} else if (eventDefinitions.size() == 1) {
    		EventDefinition eventDefinition = eventDefinitions.get(0);
    		if (eventDefinition instanceof TerminateEventDefinition) {
    			marshallNode(endEvent, "EndTerminateEvent", plane, generator);
    		} else {
    			throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
    		}
    	} else {
    		throw new UnsupportedOperationException("Multiple event definitions not supported for end node");
    	}
    }
    
    private void marshallTask(Task task, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	Map<String, Object> properties = new LinkedHashMap<String, Object>();
    	String taskType = "None";
    	if (task instanceof BusinessRuleTask) {
    		taskType = "Business Rule";
    	} else if (task instanceof ScriptTask) {
    		ScriptTask scriptTask = (ScriptTask) task;
    		properties.put("script", scriptTask.getScript());
    		properties.put("script_language", scriptTask.getScriptFormat());
    		taskType = "Script";
    	} else if (task instanceof ServiceTask) {
    		taskType = "Service";
    	} else if (task instanceof ManualTask) {
    		taskType = "Manual";
    	} else if (task instanceof UserTask) {
    		taskType = "User";
    	} else if (task instanceof SendTask) {
    		taskType = "Send";
    	} else if (task instanceof ReceiveTask) {
    		taskType = "Receive";
    	}	
    	properties.put("tasktype", taskType);
    	marshallNode(task, properties, "Task", plane, generator);
    }
    
    private void marshallParallelGateway(ParallelGateway gateway, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	marshallNode(gateway, "ParallelGateway", plane, generator);
    }
    
    private void marshallExclusiveGateway(ExclusiveGateway gateway, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	marshallNode(gateway, "Exclusive_Databased_Gateway", plane, generator);
    }
    
    private void marshallInclusiveGateway(InclusiveGateway gateway, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	marshallNode(gateway, "InclusiveGateway", plane, generator);
    }
    
    private void marshallEventBasedGateway(EventBasedGateway gateway, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	marshallNode(gateway, "EventbasedGateway", plane, generator);
    }
    
    private void marshallComplexGateway(ComplexGateway gateway, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	marshallNode(gateway, "ComplexGateway", plane, generator);
    }
    
    private void marshallCallActivity(CallActivity callActivity, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	marshallNode(callActivity, "CollapsedSubprocess", plane, generator);
    }
    
    private void marshallNode(FlowNode node, String stencil, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	marshallNode(node, null, stencil, plane, generator);
    }
    
    private void marshallNode(FlowNode node, Map<String, Object> properties, String stencil, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	if (properties == null) {
    		properties = new LinkedHashMap<String, Object>();
    	}
    	properties.put("name", node.getName());
        marshallProperties(properties, generator);
        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id", stencil);
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();
        generator.writeArrayFieldStart("outgoing");
        for (SequenceFlow outgoing: node.getOutgoing()) {
        	generator.writeStartObject();
        	generator.writeObjectField("resourceId", outgoing.getId());
        	generator.writeEndObject();
        }
        generator.writeEndArray();
        
        Bounds bounds = ((BPMNShape) findDiagramElement(plane, node)).getBounds();
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

    private void marshallSequenceFlow(SequenceFlow sequenceFlow, BPMNPlane plane, JsonGenerator generator) throws JsonGenerationException, IOException {
    	Map<String, Object> properties = new LinkedHashMap<String, Object>();
    	properties.put("name", sequenceFlow.getName());
        marshallProperties(properties, generator);
        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id", "SequenceFlow");
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();
        generator.writeArrayFieldStart("outgoing");
        generator.writeStartObject();
        generator.writeObjectField("resourceId", sequenceFlow.getTargetRef().getId());
        generator.writeEndObject();
        generator.writeEndArray();
        
        Bounds sourceBounds = ((BPMNShape) findDiagramElement(plane, sequenceFlow.getSourceRef())).getBounds();
        Bounds targetBounds = ((BPMNShape) findDiagramElement(plane, sequenceFlow.getTargetRef())).getBounds();
        generator.writeArrayFieldStart("dockers");
        generator.writeStartObject();
        generator.writeObjectField("x", sourceBounds.getWidth() / 2);
        generator.writeObjectField("y", sourceBounds.getHeight() / 2);
        generator.writeEndObject();
        generator.writeStartObject();
        generator.writeObjectField("x", targetBounds.getWidth() / 2);
        generator.writeObjectField("y", targetBounds.getHeight() / 2);
        generator.writeEndObject();
        generator.writeEndArray();
    }
    
    private DiagramElement findDiagramElement(BPMNPlane plane, FlowElement flowElement) {
    	DiagramElement result = _diagramElements.get(flowElement.getId());
    	if (result != null) {
    		return result;
    	}
    	for (DiagramElement element: plane.getPlaneElement()) {
        	if ((element instanceof BPMNEdge && ((BPMNEdge) element).getBpmnElement() == flowElement) ||
    			(element instanceof BPMNShape && ((BPMNShape) element).getBpmnElement() == flowElement)) {
        		_diagramElements.put(flowElement.getId(), element);
        		return element;
        	}
        }
		throw new IllegalArgumentException(
			"Could not find BPMNDI information for " + flowElement.getId());
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