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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ComplexGateway;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.Conversation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.Expression;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.GlobalBusinessRuleTask;
import org.eclipse.bpmn2.GlobalChoreographyTask;
import org.eclipse.bpmn2.GlobalManualTask;
import org.eclipse.bpmn2.GlobalScriptTask;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.GlobalUserTask;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.Resource;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;

import com.intalio.web.profile.IDiagramProfile;

import de.hpi.bpmn.sese.Join;
import de.hpi.bpmn.sese.Split;

/**
 * @author Antoine Toulme
 * 
 * a marshaller to transform BPMN 2.0 elements into JSON format.
 *
 */
public class Bpmn2JsonMarshaller {
	
	private Map<String, DiagramElement> _diagramElements = new HashMap<String, DiagramElement>();
	
	private IDiagramProfile profile;
	
	public void setProfile(IDiagramProfile profile) {
	    this.profile = profile;
	}

    public String marshall(Definitions def) throws IOException {
        StringWriter writer = new StringWriter();
        JsonFactory f = new JsonFactory();
        JsonGenerator generator = f.createJsonGenerator(writer);
        marshallDefinitions(def, generator);
        generator.close();
        return writer.toString();
    }
    
    private void linkSequenceFlows(List<FlowElement> flowElements) {
    	Map<String, FlowNode> nodes = new HashMap<String, FlowNode>();
    	for (FlowElement flowElement: flowElements) {
    		if (flowElement instanceof FlowNode) {
    			nodes.put(flowElement.getId(), (FlowNode) flowElement);
    			if (flowElement instanceof SubProcess) {
    				linkSequenceFlows(((SubProcess) flowElement).getFlowElements());
    			}
    		}
    	}
    	for (FlowElement flowElement: flowElements) {
    		if (flowElement instanceof SequenceFlow) {
    			SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
    			if (sequenceFlow.getSourceRef() == null && sequenceFlow.getTargetRef() == null) {
    				String id = sequenceFlow.getId();
    				try {
    					String[] subids = id.split("-_");
	    				String id1 = subids[0];
	    				String id2 = "_" + subids[1];
    					FlowNode source = nodes.get(id1);
    					if (source != null) {
    						sequenceFlow.setSourceRef(source);
    					}
    					FlowNode target = nodes.get(id2);
    					if (target != null) {
    						sequenceFlow.setTargetRef(target);
    					}
    				} catch (Throwable t) {
    					// Do nothing
    				}
    			}
    		}
    	}
    }

    private void marshallDefinitions(Definitions def, JsonGenerator generator) throws JsonGenerationException, IOException {
        try{
        	generator.writeStartObject();
	        generator.writeObjectField("resourceId", def.getId());
	        /**
	         * "properties":{"name":"",
	         * "documentation":"",
	         * "auditing":"",
	         * "monitoring":"",
	         * "executable":"true",
	         * "package":"com.sample",
	         * "id":"",
	         * "version":"",
	         * "author":"",
	         * "language":"",
	         * "namespaces":"",
	         * "targetnamespace":"",
	         * "expressionlanguage":"",
	         * "typelanguage":"",
	         * "creationdate":"",
	         * "modificationdate":""
	         * }
	         */
	        Map<String, Object> props = new LinkedHashMap<String, Object>();
	        props.put("namespaces", "");
	        props.put("targetnamespace", def.getTargetNamespace());
	        props.put("typelanguage", def.getTypeLanguage());
	        props.put("name",def.getName());
	        props.put("id", def.getId());
	        props.put("expressionlanguage", def.getExpressionLanguage());
	        if( def.getDocumentation() != null && def.getDocumentation().size() > 0 ) {
	            props.put("documentation", def.getDocumentation().get(0).getText());
	        }
	        
	        for (RootElement rootElement : def.getRootElements()) {
	            if (rootElement instanceof Process) {
	                // have to wait for process node to finish properties and stencil marshalling
	                props.put("executable", ((Process) rootElement).isIsExecutable() + "");
	                props.put("id", ((Process) rootElement).getId());
	                
	                // packageName and version are jbpm-specific extension attribute
	                Iterator<FeatureMap.Entry> iter = ((Process) rootElement).getAnyAttribute().iterator();
	                while(iter.hasNext()) {
	                    FeatureMap.Entry entry = iter.next();
	                    if(entry.getEStructuralFeature().getName().equals("packageName")) {
	                        props.put("package", entry.getValue());
	                    }
	                    
	                    if(entry.getEStructuralFeature().getName().equals("version")) {
                            props.put("version", entry.getValue());
                        }
	                }
	                
	                marshallProperties(props, generator);
	                marshallStencil("BPMNDiagram", generator);
	            	linkSequenceFlows(((Process) rootElement).getFlowElements());
	                marshallProcess((Process) rootElement, def, generator);
	            } else if (rootElement instanceof Interface) {
	                // TODO
	            } else if (rootElement instanceof ItemDefinition) {
	                // TODO
	            } else if (rootElement instanceof Resource) {
	                // TODO
	            } else if (rootElement instanceof Error) {
	                // TODO
	            } else if (rootElement instanceof Message) {
	                // TODO
	            } else {
	                throw new UnsupportedOperationException("Unknown root element " + rootElement); //TODO!
	            }
	        }
	        
	        generator.writeObjectFieldStart("stencilset");
	        generator.writeObjectField("url", this.profile.getStencilSetURL());
	        generator.writeObjectField("namespace", this.profile.getStencilSetNamespaceURL());
	        generator.writeEndObject();
	        generator.writeArrayFieldStart("ssextensions");
	        generator.writeObject(this.profile.getStencilSetExtensionURL());
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
        	marshallFlowElement(flowElement, plane, generator, 0, 0);
        }
        generator.writeEndArray();
    }
    
    private void marshallFlowElement(FlowElement flowElement, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	generator.writeStartObject();
    	generator.writeObjectField("resourceId", flowElement.getId());
    	if (flowElement instanceof StartEvent) {
    		marshallStartEvent((StartEvent) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof EndEvent) {
    		marshallEndEvent((EndEvent) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof IntermediateThrowEvent) {
    		marshallIntermediateThrowEvent((IntermediateThrowEvent) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof IntermediateCatchEvent) {
    		marshallIntermediateCatchEvent((IntermediateCatchEvent) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof BoundaryEvent) {
    		marshallBoundaryEvent((BoundaryEvent) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof Task) {
    		marshallTask((Task) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof SequenceFlow) {
    		marshallSequenceFlow((SequenceFlow) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof ParallelGateway) {
    		marshallParallelGateway((ParallelGateway) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof ExclusiveGateway) {
    		marshallExclusiveGateway((ExclusiveGateway) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof InclusiveGateway) {
    		marshallInclusiveGateway((InclusiveGateway) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof EventBasedGateway) {
    		marshallEventBasedGateway((EventBasedGateway) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof ComplexGateway) {
    		marshallComplexGateway((ComplexGateway) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof CallActivity) {
    		marshallCallActivity((CallActivity) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof SubProcess) {
    		marshallSubProcess((SubProcess) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof DataObject) {
    		marshallDataObject((DataObject) flowElement, plane, generator, xOffset, yOffset);
    	} else {
    		throw new UnsupportedOperationException("Unknown flow element " + flowElement);
    	}
    	generator.writeEndObject();
    }
    
    private void marshallStartEvent(StartEvent startEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	List<EventDefinition> eventDefinitions = startEvent.getEventDefinitions();
    	if (eventDefinitions == null || eventDefinitions.size() == 0) {
    		marshallNode(startEvent, "StartNoneEvent", plane, generator, xOffset, yOffset);
    	} else if (eventDefinitions.size() == 1) {
    		EventDefinition eventDefinition = eventDefinitions.get(0);
    		if (eventDefinition instanceof ConditionalEventDefinition) {
    			marshallNode(startEvent, "StartConditionalEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof SignalEventDefinition) {
    			marshallNode(startEvent, "StartSignalEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof MessageEventDefinition) {
    			marshallNode(startEvent, "StartMessageEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof TimerEventDefinition) {
    			marshallNode(startEvent, "StartTimerEvent", plane, generator, xOffset, yOffset);
    		} else {
    			throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
    		}
    	} else {
    		throw new UnsupportedOperationException("Multiple event definitions not supported for start event");
    	}
    }
    
    private void marshallEndEvent(EndEvent endEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	List<EventDefinition> eventDefinitions = endEvent.getEventDefinitions();
    	if (eventDefinitions == null || eventDefinitions.size() == 0) {
    		marshallNode(endEvent, "EndNoneEvent", plane, generator, xOffset, yOffset);
    	} else if (eventDefinitions.size() == 1) {
    		EventDefinition eventDefinition = eventDefinitions.get(0);
    		if (eventDefinition instanceof TerminateEventDefinition) {
    			marshallNode(endEvent, "EndTerminateEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof SignalEventDefinition) {
    			marshallNode(endEvent, "EndSignalEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof MessageEventDefinition) {
    			marshallNode(endEvent, "EndMessageEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof ErrorEventDefinition) {
    			marshallNode(endEvent, "EndErrorEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof EscalationEventDefinition) {
    			marshallNode(endEvent, "EndEscalationEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof CompensateEventDefinition) {
    			marshallNode(endEvent, "EndCompensationEvent", plane, generator, xOffset, yOffset);
    		} else {
    			throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
    		}
    	} else {
    		throw new UnsupportedOperationException("Multiple event definitions not supported for end event");
    	}
    }
    
    private void marshallIntermediateCatchEvent(IntermediateCatchEvent catchEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	List<EventDefinition> eventDefinitions = catchEvent.getEventDefinitions();
    	if (eventDefinitions.size() == 1) {
    		EventDefinition eventDefinition = eventDefinitions.get(0);
    		if (eventDefinition instanceof SignalEventDefinition) {
    			marshallNode(catchEvent, "IntermediateSignalEventCatching", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof MessageEventDefinition) {
    			marshallNode(catchEvent, "IntermediateMessageEventCatching", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof TimerEventDefinition) {
    			marshallNode(catchEvent, "IntermediateTimerEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof ConditionalEventDefinition) {
    			marshallNode(catchEvent, "IntermediateConditionalEvent", plane, generator, xOffset, yOffset);
    		} else {
    			throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
    		}
    	} else {
    		throw new UnsupportedOperationException("None or multiple event definitions not supported for intermediate catch event");
    	}
    }
    
    private void marshallBoundaryEvent(BoundaryEvent boundaryEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	List<EventDefinition> eventDefinitions = boundaryEvent.getEventDefinitions();
    	if (eventDefinitions.size() == 1) {
    		EventDefinition eventDefinition = eventDefinitions.get(0);
    		if (eventDefinition instanceof EscalationEventDefinition) {
    			marshallNode(boundaryEvent, "IntermediateEscalationEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof ErrorEventDefinition) {
    			marshallNode(boundaryEvent, "IntermediateErrorEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof TimerEventDefinition) {
    			marshallNode(boundaryEvent, "IntermediateTimerEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof CompensateEventDefinition) {
    			marshallNode(boundaryEvent, "IntermediateCompensationEventCatching", plane, generator, xOffset, yOffset);
    		} else {
    			throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
    		}
    	} else {
    		throw new UnsupportedOperationException("None or multiple event definitions not supported for boundary event");
    	}
    }
    
    private void marshallIntermediateThrowEvent(IntermediateThrowEvent throwEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	List<EventDefinition> eventDefinitions = throwEvent.getEventDefinitions();
    	if (eventDefinitions.size() == 0) {
			marshallNode(throwEvent, "IntermediateEventThrowing", plane, generator, xOffset, yOffset);
    	} else if (eventDefinitions.size() == 1) {
    		EventDefinition eventDefinition = eventDefinitions.get(0);
    		if (eventDefinition instanceof SignalEventDefinition) {
    			marshallNode(throwEvent, "IntermediateSignalEventThrowing", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof MessageEventDefinition) {
    			marshallNode(throwEvent, "IntermediateMessageEventThrowing", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof EscalationEventDefinition) {
    			marshallNode(throwEvent, "IntermediateEscalationEventThrowing", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof CompensateEventDefinition) {
    			marshallNode(throwEvent, "IntermediateCompensationEventThrowing", plane, generator, xOffset, yOffset);
    		} else {
    			throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
    		}
    	} else {
    		throw new UnsupportedOperationException("None or multiple event definitions not supported for intermediate throw event");
    	}
    }
    
    private void marshallTask(Task task, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
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
    	// get out the droolsjbpm-specific attributes "ruleflowGroup" and "taskName"
    	Iterator<FeatureMap.Entry> iter = task.getAnyAttribute().iterator();
        while(iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if(entry.getEStructuralFeature().getName().equals("ruleFlowGroup")) {
                properties.put("ruleflowgroup", entry.getValue());
            }
            if(entry.getEStructuralFeature().getName().equals("taskName")) {
                properties.put("taskname", entry.getValue());
            }
        }
        
    	marshallNode(task, properties, "Task", plane, generator, xOffset, yOffset);
    }
    
    private void marshallParallelGateway(ParallelGateway gateway, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	marshallNode(gateway, "ParallelGateway", plane, generator, xOffset, yOffset);
    }
    
    private void marshallExclusiveGateway(ExclusiveGateway gateway, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	marshallNode(gateway, "Exclusive_Databased_Gateway", plane, generator, xOffset, yOffset);
    }
    
    private void marshallInclusiveGateway(InclusiveGateway gateway, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	marshallNode(gateway, "InclusiveGateway", plane, generator, xOffset, yOffset);
    }
    
    private void marshallEventBasedGateway(EventBasedGateway gateway, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	marshallNode(gateway, "EventbasedGateway", plane, generator, xOffset, yOffset);
    }
    
    private void marshallComplexGateway(ComplexGateway gateway, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	marshallNode(gateway, "ComplexGateway", plane, generator, xOffset, yOffset);
    }
    
    private void marshallCallActivity(CallActivity callActivity, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	marshallNode(callActivity, "CollapsedSubprocess", plane, generator, xOffset, yOffset);
    }
    
    private void marshallNode(FlowNode node, String stencil, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	marshallNode(node, null, stencil, plane, generator, xOffset, yOffset);
    }
    
    private void marshallNode(FlowNode node, Map<String, Object> properties, String stencil, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	if (properties == null) {
    		properties = new LinkedHashMap<String, Object>();
    	}
        if(node.getDocumentation() != null && node.getDocumentation().size() > 0) {
            properties.put("documentation", node.getDocumentation().get(0).getText());
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
        
        BPMNShape shape = (BPMNShape) findDiagramElement(plane, node);
        Bounds bounds = shape.getBounds();
        correctEventNodeSize(shape);
        generator.writeObjectFieldStart("bounds");
        generator.writeObjectFieldStart("lowerRight");
        generator.writeObjectField("x", bounds.getX() + bounds.getWidth() - xOffset);
        generator.writeObjectField("y", bounds.getY() + bounds.getHeight() - yOffset);
        generator.writeEndObject();
        generator.writeObjectFieldStart("upperLeft");
        generator.writeObjectField("x", bounds.getX() - xOffset);
        generator.writeObjectField("y", bounds.getY() - yOffset);
        generator.writeEndObject();
        generator.writeEndObject();
    }
    
    private void correctEventNodeSize(BPMNShape shape) {
    	BaseElement element = shape.getBpmnElement();
		if (element instanceof Event) {
			Bounds bounds = shape.getBounds();
			float width = bounds.getWidth();
			float height = bounds.getHeight();
			if (width != 30 || height != 30) {
				bounds.setWidth(30);
				bounds.setHeight(30);
				float x = bounds.getX();
				float y = bounds.getY();
    			x = x - ((30 - width)/2);
    			y = y - ((30 - height)/2);
				bounds.setX(x);
				bounds.setY(y);
			}
		} else if (element instanceof Gateway) {
			Bounds bounds = shape.getBounds();
			float width = bounds.getWidth();
			float height = bounds.getHeight();
			if (width != 40 || height != 40) {
				bounds.setWidth(40);
				bounds.setHeight(40);
				float x = bounds.getX();
				float y = bounds.getY();
    			x = x - ((40 - width)/2);
    			y = y - ((40 - height)/2);
				bounds.setX(x);
				bounds.setY(y);
			}
    	}
    }
    
    private void marshallDataObject(DataObject dataObject, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	Map<String, Object> properties = new LinkedHashMap<String, Object>();
		properties.put("name", dataObject.getName());
	    marshallProperties(properties, generator);
	    generator.writeObjectFieldStart("stencil");
	    generator.writeObjectField("id", "DataObject");
	    generator.writeEndObject();
	    generator.writeArrayFieldStart("childShapes");
	    generator.writeEndArray();
	    generator.writeArrayFieldStart("outgoing");
	    generator.writeEndArray();
	    
	    Bounds bounds = ((BPMNShape) findDiagramElement(plane, dataObject)).getBounds();
	    generator.writeObjectFieldStart("bounds");
	    generator.writeObjectFieldStart("lowerRight");
	    generator.writeObjectField("x", bounds.getX() + bounds.getWidth() - xOffset);
	    generator.writeObjectField("y", bounds.getY() + bounds.getHeight() - yOffset);
	    generator.writeEndObject();
	    generator.writeObjectFieldStart("upperLeft");
	    generator.writeObjectField("x", bounds.getX() - xOffset);
	    generator.writeObjectField("y", bounds.getY() - yOffset);
	    generator.writeEndObject();
	    generator.writeEndObject();
	}
    
    private void marshallSubProcess(SubProcess subProcess, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	Map<String, Object> properties = new LinkedHashMap<String, Object>();
		properties.put("name", subProcess.getName());
	    marshallProperties(properties, generator);
	    generator.writeObjectFieldStart("stencil");
	    generator.writeObjectField("id", "Subprocess");
	    generator.writeEndObject();
	    generator.writeArrayFieldStart("childShapes");
	    Bounds bounds = ((BPMNShape) findDiagramElement(plane, subProcess)).getBounds();
	    for (FlowElement flowElement: subProcess.getFlowElements()) {
	    	marshallFlowElement(flowElement, plane, generator, (int) (xOffset + bounds.getX()), (int) (yOffset + bounds.getY()));
	    }
	    generator.writeEndArray();
	    generator.writeArrayFieldStart("outgoing");
	    for (BoundaryEvent boundaryEvent: subProcess.getBoundaryEventRefs()) {
        	generator.writeStartObject();
        	generator.writeObjectField("resourceId", boundaryEvent.getId());
        	generator.writeEndObject();
        }
	    for (SequenceFlow outgoing: subProcess.getOutgoing()) {
        	generator.writeStartObject();
        	generator.writeObjectField("resourceId", outgoing.getId());
        	generator.writeEndObject();
        }
	    generator.writeEndArray();
	    
	    generator.writeObjectFieldStart("bounds");
	    generator.writeObjectFieldStart("lowerRight");
	    generator.writeObjectField("x", bounds.getX() + bounds.getWidth() - xOffset);
	    generator.writeObjectField("y", bounds.getY() + bounds.getHeight() - yOffset);
	    generator.writeEndObject();
	    generator.writeObjectFieldStart("upperLeft");
	    generator.writeObjectField("x", bounds.getX() - xOffset);
	    generator.writeObjectField("y", bounds.getY() - yOffset);
	    generator.writeEndObject();
	    generator.writeEndObject();
	}
    
    private void marshallSequenceFlow(SequenceFlow sequenceFlow, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	Map<String, Object> properties = new LinkedHashMap<String, Object>();
    	properties.put("name", sequenceFlow.getName());
    	Expression conditionExpression = sequenceFlow.getConditionExpression();
    	if (conditionExpression instanceof FormalExpression) {
    		properties.put("conditionexpression", ((FormalExpression) conditionExpression).getBody());
    		properties.put("conditionexpression_language", ((FormalExpression) conditionExpression).getLanguage());
    	}
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
        List<Point> waypoints = ((BPMNEdge) findDiagramElement(plane, sequenceFlow)).getWaypoint();
        for (int i = 1; i < waypoints.size() - 1; i++) {
        	Point waypoint = waypoints.get(i);
            generator.writeStartObject();
            generator.writeObjectField("x", waypoint.getX());
            generator.writeObjectField("y", waypoint.getY());
            generator.writeEndObject();
        }
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