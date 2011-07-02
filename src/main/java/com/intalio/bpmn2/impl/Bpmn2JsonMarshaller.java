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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ComplexGateway;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.Conversation;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Escalation;
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
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.Resource;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TerminateEventDefinition;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.ecore.util.FeatureMap;

import com.intalio.web.profile.IDiagramProfile;

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

    public String marshall(Definitions def, String preProcessingData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonFactory f = new JsonFactory();
        JsonGenerator generator = f.createJsonGenerator(baos, JsonEncoding.UTF8);
        marshallDefinitions(def, generator, preProcessingData);
        generator.close();
        
        return baos.toString("UTF-8");
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

    private void marshallDefinitions(Definitions def, JsonGenerator generator, String preProcessingData) throws JsonGenerationException, IOException {
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
	         * "vardefs":"a,b,c,d",
	         * "lanes" : "a,b,c",
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
	        //props.put("targetnamespace", def.getTargetNamespace());
	        props.put("targetnamespace", "http://www.omg.org/bpmn20");
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
	                props.put("name", ((Process) rootElement).getName());
	                
	                List<Property> processProperties = ((Process) rootElement).getProperties();
	                if(processProperties != null && processProperties.size() > 0) {
	                    String propVal = "";
	                    for(int i=0; i<processProperties.size(); i++) {
	                        Property p = processProperties.get(i);
	                        propVal += p.getId();
	                        // check the structureRef value
	                        if(p.getItemSubjectRef() != null && p.getItemSubjectRef().getStructureRef() != null) {
	                            propVal += ":" + p.getItemSubjectRef().getStructureRef();
	                        }
	                        if(i != processProperties.size()-1) {
	                            propVal += ",";
	                        }
	                    }
	                    props.put("vardefs", propVal);
	                }
	                
	                Map<String, List<String>> lanesetInfo = new HashMap<String, List<String>>();
	                List<LaneSet> processLanesets = ((Process) rootElement).getLaneSets();
	                if(processLanesets != null && processLanesets.size() > 0) {
	                    // we support currently only a single laneset
	                    LaneSet ls = processLanesets.get(0);
	                    List<Lane> processLanes =  ls.getLanes();
	                    String lanesVal = "";
	                    if(processLanes != null && processLanes.size() > 0) {
	                        for(int i=0; i < processLanes.size(); i++) {
	                            Lane lane = processLanes.get(i);
	                            lanesVal += lane.getName();
	                            lanesetInfo.put(lane.getName(), new ArrayList<String>());
	                            if(i != processLanes.size() - 1) {
	                                lanesVal += ",";
	                            }
	                            List<FlowNode> laneFlowNodes = lane.getFlowNodeRefs();
	                            for(FlowNode fl : laneFlowNodes) {
	                                lanesetInfo.get(lane.getName()).add(fl.getId());
	                            }
	                        }
	                    }
	                    props.put("lanes", lanesVal);
	                }
	                
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
	                    
	                    if(entry.getEStructuralFeature().getName().equals("import")) {
                            props.put("imports", entry.getValue());
                        }
	                }
	                
	                marshallProperties(props, generator);
	                marshallStencil("BPMNDiagram", generator);
	            	linkSequenceFlows(((Process) rootElement).getFlowElements());
	                marshallProcess((Process) rootElement, def, generator, preProcessingData, lanesetInfo);
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
	            } else if (rootElement instanceof Signal) {
                    // TODO
                } else if (rootElement instanceof Escalation) {
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
    
    
    /** private void marshallMessage(Message message, Definitions def, JsonGenerator generator) throws JsonGenerationException, IOException {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        
        generator.writeStartObject();
        generator.writeObjectField("resourceId", message.getId());
        
        properties.put("name", message.getName());
        if(message.getDocumentation() != null && message.getDocumentation().size() > 0) {
            properties.put("documentation", message.getDocumentation().get(0).getText());
        }
        
        marshallProperties(properties, generator);
        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id", "Message");
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();
        generator.writeArrayFieldStart("outgoing");
        generator.writeEndArray();
        
        generator.writeEndObject();
    } **/

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
            marshallProcess((Process) callableElement, def, generator, "", null);
        } else {
            throw new UnsupportedOperationException("TODO"); //TODO!
        }
        generator.writeEndObject();
    }

    private void marshallProcess(Process process, Definitions def, JsonGenerator generator, String preProcessingData, Map<String, List<String>> lanesetInfo) throws JsonGenerationException, IOException {
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
        	marshallFlowElement(flowElement, plane, generator, 0, 0, preProcessingData, lanesetInfo);
        }
        generator.writeEndArray();
    }
    
    private void setCatchEventProperties(CatchEvent event, Map<String, Object> properties) {
        if(event.getOutputSet() != null) {
            List<DataOutput> dataOutputs = event.getOutputSet().getDataOutputRefs();
            StringBuffer doutbuff = new StringBuffer();
            for(DataOutput dout : dataOutputs) {
                doutbuff.append(dout.getName());
                doutbuff.append(",");
            }
            if(doutbuff.length() > 0) {
                doutbuff.setLength(doutbuff.length() - 1);
            }
            properties.put("dataoutput", doutbuff.toString());
        
            List<DataOutputAssociation> outputAssociations = event.getDataOutputAssociation();
            StringBuffer doutassociationbuff = new StringBuffer();
            for(DataOutputAssociation doa : outputAssociations) {
                doutassociationbuff.append(((DataOutput)doa.getSourceRef().get(0)).getName());
                doutassociationbuff.append("->");
                doutassociationbuff.append(doa.getTargetRef().getId());
                doutassociationbuff.append(",");
            }
            if(doutassociationbuff.length() > 0) {
                doutassociationbuff.setLength(doutassociationbuff.length() - 1);
            }
            properties.put("dataoutputassociations", doutassociationbuff.toString());
        }
        // event definitions
        List<EventDefinition> eventdefs = event.getEventDefinitions();
        for(EventDefinition ed : eventdefs) {
            if(ed instanceof TimerEventDefinition) {
                TimerEventDefinition ted = (TimerEventDefinition) ed;
                if(ted.getTimeDate() != null) {
                    properties.put("timedate", ((FormalExpression) ted.getTimeDate()).getBody());
                }
                if(ted.getTimeDuration() != null) {
                    properties.put("timeduration", ((FormalExpression) ted.getTimeDuration()).getBody());
                }
                if(ted.getTimeCycle() != null) {
                    properties.put("timecycle", ((FormalExpression) ted.getTimeCycle()).getBody());
                }
            } else if( ed instanceof SignalEventDefinition) {
                if(((SignalEventDefinition) ed).getSignalRef() != null && ((SignalEventDefinition) ed).getSignalRef().getName() != null) {
                    properties.put("signalref", ((SignalEventDefinition) ed).getSignalRef().getName());
                } else {
                    properties.put("signalref", "");
                }
            } else if( ed instanceof ErrorEventDefinition) {
                if(((ErrorEventDefinition) ed).getErrorRef() != null && ((ErrorEventDefinition) ed).getErrorRef().getErrorCode() != null) {
                    properties.put("errorref", ((ErrorEventDefinition) ed).getErrorRef().getErrorCode());
                } else {
                    properties.put("errorref", "");
                }
            } else if( ed instanceof ConditionalEventDefinition ) {
                FormalExpression conditionalExp = (FormalExpression) ((ConditionalEventDefinition) ed).getCondition();
                if(conditionalExp.getBody() != null) {
                    properties.put("conditionexpression", conditionalExp.getBody());
                }
                if(conditionalExp.getLanguage() != null) {
                    String languageVal = conditionalExp.getLanguage();
                    if(languageVal.equals("http://www.jboss.org/drools/rule")) {
                        properties.put("conditionlanguage", "drools");
                    } else if(languageVal.equals("http://www.mvel.org/2.0")) {
                        properties.put("conditionlanguage", "mvel");
                    } else {
                        // default to drools
                        properties.put("conditionlanguage", "drools");
                    }
                }
            } else if( ed instanceof EscalationEventDefinition ) {
                if(((EscalationEventDefinition) ed).getEscalationRef() != null) {
                    Escalation esc = ((EscalationEventDefinition) ed).getEscalationRef();
                    if(esc.getEscalationCode() != null && esc.getEscalationCode().length() > 0) {
                        properties.put("escalationcode", esc.getEscalationCode());
                    } else {
                        properties.put("escalationcode", "");
                    }
                }
            } else if( ed instanceof MessageEventDefinition) {
                if(((MessageEventDefinition) ed).getMessageRef() != null) {
                    Message msg = ((MessageEventDefinition) ed).getMessageRef();
                    properties.put("messageref", msg.getId());
                }
            }  else if( ed instanceof CompensateEventDefinition) {
                if(((CompensateEventDefinition) ed).getActivityRef() != null) {
                    Activity act = ((CompensateEventDefinition) ed).getActivityRef();
                    properties.put("activityref", act.getName());
                }
            }  
        }
    }
    
    private void setThrowEventProperties(ThrowEvent event, Map<String, Object> properties) {
        if(event.getInputSet() != null) {
            List<DataInput> dataInputs = event.getInputSet().getDataInputRefs();
            StringBuffer dinbuff = new StringBuffer();
            for(DataInput din : dataInputs) {
                dinbuff.append(din.getName());
                dinbuff.append(",");
            }
            if(dinbuff.length() > 0) {
                dinbuff.setLength(dinbuff.length() - 1);
            }
            properties.put("datainput", dinbuff.toString());
            
            List<DataInputAssociation> inputAssociations = event.getDataInputAssociation();
            StringBuffer dinassociationbuff = new StringBuffer();
            for(DataInputAssociation din : inputAssociations) {
                dinassociationbuff.append(din.getSourceRef().get(0).getId());
                dinassociationbuff.append("->");
                dinassociationbuff.append( ((DataInput)din.getTargetRef()).getName());
                dinassociationbuff.append(",");
            }
            if(dinassociationbuff.length() > 0) {
                dinassociationbuff.setLength(dinassociationbuff.length() - 1);
            }
            properties.put("datainputassociations", dinassociationbuff.toString());
        }
        // event definitions
        List<EventDefinition> eventdefs = event.getEventDefinitions();
        for(EventDefinition ed : eventdefs) {
            if(ed instanceof TimerEventDefinition) {
                TimerEventDefinition ted = (TimerEventDefinition) ed;
                if(ted.getTimeDate() != null) {
                    properties.put("timedate", ((FormalExpression) ted.getTimeDate()).getBody());
                }
                if(ted.getTimeDuration() != null) {
                    properties.put("timeduration", ((FormalExpression) ted.getTimeDuration()).getBody());
                }
                if(ted.getTimeCycle() != null) {
                    properties.put("timecycle", ((FormalExpression) ted.getTimeCycle()).getBody());
                }
            } else if( ed instanceof SignalEventDefinition) {
                if(((SignalEventDefinition) ed).getSignalRef() != null && ((SignalEventDefinition) ed).getSignalRef().getName() != null) {
                    properties.put("signalref", ((SignalEventDefinition) ed).getSignalRef().getName());
                } else {
                    properties.put("signalref", "");
                }
            } else if( ed instanceof ErrorEventDefinition) {
                if(((ErrorEventDefinition) ed).getErrorRef() != null && ((ErrorEventDefinition) ed).getErrorRef().getErrorCode() != null) {
                    properties.put("errorref", ((ErrorEventDefinition) ed).getErrorRef().getErrorCode());
                } else {
                    properties.put("errorref", "");
                }
            } else if( ed instanceof ConditionalEventDefinition ) {
                FormalExpression conditionalExp = (FormalExpression) ((ConditionalEventDefinition) ed).getCondition();
                if(conditionalExp.getBody() != null) {
                    properties.put("conditionexpression", conditionalExp.getBody());
                }
                if(conditionalExp.getLanguage() != null) {
                    String languageVal = conditionalExp.getLanguage();
                    if(languageVal.equals("http://www.jboss.org/drools/rule")) {
                        properties.put("conditionlanguage", "drools");
                    } else if(languageVal.equals("http://www.mvel.org/2.0")) {
                        properties.put("conditionlanguage", "mvel");
                    } else {
                        // default to drools
                        properties.put("conditionlanguage", "drools");
                    }
                }
            } else if( ed instanceof EscalationEventDefinition ) {
                if(((EscalationEventDefinition) ed).getEscalationRef() != null) {
                    Escalation esc = ((EscalationEventDefinition) ed).getEscalationRef();
                    if(esc.getEscalationCode() != null && esc.getEscalationCode().length() > 0) {
                        properties.put("escalationcode", esc.getEscalationCode());
                    } else {
                        properties.put("escalationcode", "");
                    }
                }
            } else if( ed instanceof MessageEventDefinition) {
                if(((MessageEventDefinition) ed).getMessageRef() != null) {
                    Message msg = ((MessageEventDefinition) ed).getMessageRef();
                    properties.put("messageref", msg.getId());
                }
            }  else if( ed instanceof CompensateEventDefinition) {
                if(((CompensateEventDefinition) ed).getActivityRef() != null) {
                    Activity act = ((CompensateEventDefinition) ed).getActivityRef();
                    properties.put("activityref", act.getName());
                }
            }  
        }
    }
    
    private void marshallFlowElement(FlowElement flowElement, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, String preProcessingData, Map<String, List<String>> lanesetInfo) throws JsonGenerationException, IOException {
    	generator.writeStartObject();
    	generator.writeObjectField("resourceId", flowElement.getId());
    	
    	Map<String, Object> catchEventProperties = new LinkedHashMap<String, Object>();
    	Map<String, Object> throwEventProperties = new LinkedHashMap<String, Object>();
    	if(flowElement instanceof CatchEvent) {
    	    setCatchEventProperties((CatchEvent) flowElement, catchEventProperties);
    	}
    	if(flowElement instanceof ThrowEvent) {
    	    setThrowEventProperties((ThrowEvent) flowElement, throwEventProperties);
    	}
    	if (flowElement instanceof StartEvent) {
    		marshallStartEvent((StartEvent) flowElement, plane, generator, xOffset, yOffset, catchEventProperties);
    	} else if (flowElement instanceof EndEvent) {
    		marshallEndEvent((EndEvent) flowElement, plane, generator, xOffset, yOffset, throwEventProperties);
    	} else if (flowElement instanceof IntermediateThrowEvent) {
    		marshallIntermediateThrowEvent((IntermediateThrowEvent) flowElement, plane, generator, xOffset, yOffset, throwEventProperties);
    	} else if (flowElement instanceof IntermediateCatchEvent) {
    		marshallIntermediateCatchEvent((IntermediateCatchEvent) flowElement, plane, generator, xOffset, yOffset, catchEventProperties);
    	} else if (flowElement instanceof BoundaryEvent) {
    		marshallBoundaryEvent((BoundaryEvent) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof Task) {
    		marshallTask((Task) flowElement, plane, generator, xOffset, yOffset, preProcessingData, lanesetInfo);
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
    	    if(flowElement instanceof AdHocSubProcess) {
    	        marshallSubProcess((AdHocSubProcess) flowElement, plane, generator, xOffset, yOffset, preProcessingData, lanesetInfo);
    	    } else {
    	        marshallSubProcess((SubProcess) flowElement, plane, generator, xOffset, yOffset, preProcessingData, lanesetInfo);
    	    }
    	} else if (flowElement instanceof DataObject) {
    		marshallDataObject((DataObject) flowElement, plane, generator, xOffset, yOffset);
    	} else {
    		throw new UnsupportedOperationException("Unknown flow element " + flowElement);
    	}
    	generator.writeEndObject();
    }
    
    private void marshallStartEvent(StartEvent startEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> properties) throws JsonGenerationException, IOException {
        List<EventDefinition> eventDefinitions = startEvent.getEventDefinitions();
    	if (eventDefinitions == null || eventDefinitions.size() == 0) {
    		marshallNode(startEvent, properties, "StartNoneEvent", plane, generator, xOffset, yOffset);
    	} else if (eventDefinitions.size() == 1) {
    		EventDefinition eventDefinition = eventDefinitions.get(0);
    		if (eventDefinition instanceof ConditionalEventDefinition) {
    			marshallNode(startEvent, properties, "StartConditionalEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof SignalEventDefinition) {
    			marshallNode(startEvent, properties, "StartSignalEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof MessageEventDefinition) {
    			marshallNode(startEvent, properties, "StartMessageEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof TimerEventDefinition) {
    			marshallNode(startEvent, properties, "StartTimerEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof ErrorEventDefinition) {
    		    marshallNode(startEvent, properties, "StartErrorEvent", plane, generator, xOffset, yOffset);
    		} else if(eventDefinition instanceof ConditionalEventDefinition) {
    		    marshallNode(startEvent, properties, "StartConditionalEvent", plane, generator, xOffset, yOffset);
    		} else if(eventDefinition instanceof EscalationEventDefinition) {
                marshallNode(startEvent, properties, "StartEscalationEvent", plane, generator, xOffset, yOffset);
            } else if(eventDefinition instanceof CompensateEventDefinition) {
                marshallNode(startEvent, properties, "StartCompensationEvent", plane, generator, xOffset, yOffset);
            }
    		else {
    			throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
    		}
    	} else {
    		throw new UnsupportedOperationException("Multiple event definitions not supported for start event");
    	}
    }
    
    private void marshallEndEvent(EndEvent endEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> properties) throws JsonGenerationException, IOException {
    	List<EventDefinition> eventDefinitions = endEvent.getEventDefinitions();
    	if (eventDefinitions == null || eventDefinitions.size() == 0) {
    		marshallNode(endEvent, properties, "EndNoneEvent", plane, generator, xOffset, yOffset);
    	} else if (eventDefinitions.size() == 1) {
    		EventDefinition eventDefinition = eventDefinitions.get(0);
    		if (eventDefinition instanceof TerminateEventDefinition) {
    			marshallNode(endEvent, properties, "EndTerminateEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof SignalEventDefinition) {
    			marshallNode(endEvent, properties, "EndSignalEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof MessageEventDefinition) {
    			marshallNode(endEvent, properties, "EndMessageEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof ErrorEventDefinition) {
    			marshallNode(endEvent, properties, "EndErrorEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof EscalationEventDefinition) {
    			marshallNode(endEvent, properties, "EndEscalationEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof CompensateEventDefinition) {
    			marshallNode(endEvent, properties, "EndCompensationEvent", plane, generator, xOffset, yOffset);
    		} else {
    			throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
    		}
    	} else {
    		throw new UnsupportedOperationException("Multiple event definitions not supported for end event");
    	}
    }
    
    private void marshallIntermediateCatchEvent(IntermediateCatchEvent catchEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> properties) throws JsonGenerationException, IOException {
    	List<EventDefinition> eventDefinitions = catchEvent.getEventDefinitions();
    	if (eventDefinitions.size() == 1) {
    		EventDefinition eventDefinition = eventDefinitions.get(0);
    		if (eventDefinition instanceof SignalEventDefinition) {
    			marshallNode(catchEvent, properties, "IntermediateSignalEventCatching", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof MessageEventDefinition) {
    			marshallNode(catchEvent, properties, "IntermediateMessageEventCatching", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof TimerEventDefinition) {
    			marshallNode(catchEvent, properties, "IntermediateTimerEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof ConditionalEventDefinition) {
    			marshallNode(catchEvent, properties, "IntermediateConditionalEvent", plane, generator, xOffset, yOffset);
    		} else if(eventDefinition instanceof ErrorEventDefinition) {
    		    marshallNode(catchEvent, properties, "IntermediateErrorEvent", plane, generator, xOffset, yOffset);
    		} else if(eventDefinition instanceof EscalationEventDefinition) {
                marshallNode(catchEvent, properties, "IntermediateEscalationEvent", plane, generator, xOffset, yOffset);
            } else if(eventDefinition instanceof CompensateEventDefinition) {
                marshallNode(catchEvent, properties, "IntermediateCompensationEventCatching", plane, generator, xOffset, yOffset);
            } 
    		else {
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
    		} else if(eventDefinition instanceof ConditionalEventDefinition) {
    		    marshallNode(boundaryEvent, "IntermediateConditionalEvent", plane, generator, xOffset, yOffset);
    		} else {
    			throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
    		}
    	} else {
    		throw new UnsupportedOperationException("None or multiple event definitions not supported for boundary event");
    	}
    }
    
    private void marshallIntermediateThrowEvent(IntermediateThrowEvent throwEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> properties) throws JsonGenerationException, IOException {
    	List<EventDefinition> eventDefinitions = throwEvent.getEventDefinitions();
    	if (eventDefinitions.size() == 0) {
			marshallNode(throwEvent, properties, "IntermediateEventThrowing", plane, generator, xOffset, yOffset);
    	} else if (eventDefinitions.size() == 1) {
    		EventDefinition eventDefinition = eventDefinitions.get(0);
    		if (eventDefinition instanceof SignalEventDefinition) {
    			marshallNode(throwEvent, properties, "IntermediateSignalEventThrowing", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof MessageEventDefinition) {
    			marshallNode(throwEvent, properties, "IntermediateMessageEventThrowing", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof EscalationEventDefinition) {
    			marshallNode(throwEvent, properties, "IntermediateEscalationEventThrowing", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof CompensateEventDefinition) {
    			marshallNode(throwEvent, properties, "IntermediateCompensationEventThrowing", plane, generator, xOffset, yOffset);
    		} else {
    			throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
    		}
    	} else {
    		throw new UnsupportedOperationException("None or multiple event definitions not supported for intermediate throw event");
    	}
    }
    
    private void marshallTask(Task task, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, String preProcessingData, Map<String, List<String>> lanesetInfo) throws JsonGenerationException, IOException {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
    	String taskType = "None";
    	if (task instanceof BusinessRuleTask) {
    		taskType = "Business Rule";
    		Iterator<FeatureMap.Entry> iter = task.getAnyAttribute().iterator();
            while(iter.hasNext()) {
                FeatureMap.Entry entry = iter.next();
                if(entry.getEStructuralFeature().getName().equals("ruleFlowGroup")) {
                    properties.put("ruleflowgroup", entry.getValue());
                }
            }
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
    		// get the user task actors
    		List<ResourceRole> roles = task.getResources();
    		StringBuilder sb = new StringBuilder();
    		for(ResourceRole role : roles) {
    		    if(role instanceof PotentialOwner) {
    		        FormalExpression fe = (FormalExpression) ( (PotentialOwner)role).getResourceAssignmentExpression().getExpression();
    		        if(fe.getBody() != null && fe.getBody().length() > 0) {
    		            sb.append(fe.getBody());
    		            sb.append(",");
    		        }
    		    }
    		}
    		if(sb.length() > 0) {
    		    sb.setLength(sb.length() - 1);
    		}
    		properties.put("actors", sb.toString());
    		
    		Iterator<FeatureMap.Entry> iter = task.getAnyAttribute().iterator();
            while(iter.hasNext()) {
                FeatureMap.Entry entry = iter.next();
                if(entry.getEStructuralFeature().getName().equals("onEntry-script")) {
                    properties.put("onentryactions", entry.getValue());
                } else if(entry.getEStructuralFeature().getName().equals("onExit-script")) {
                    properties.put("onexitactions", entry.getValue());
                } else if(entry.getEStructuralFeature().getName().equals("scriptFormat")) {
                    String format = (String) entry.getValue();
                    String formatToWrite = "";
                    if(format.equals("http://www.java.com/java")) {
                        formatToWrite = "java";
                    } else if(format.equals("http://www.mvel.org/2.0")) {
                        formatToWrite = "mvel";
                    } else {
                        formatToWrite = "java";
                    }
                    properties.put("script_language", formatToWrite);
                } 
            }
    	} else if (task instanceof SendTask) {
    		taskType = "Send";
    	} else if (task instanceof ReceiveTask) {
    		taskType = "Receive";
    	}
    	
    	// get out the droolsjbpm-specific attributes "ruleflowGroup" and "taskName"
    	Iterator<FeatureMap.Entry> iter = task.getAnyAttribute().iterator();
        while(iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if(entry.getEStructuralFeature().getName().equals("taskName")) {
                properties.put("taskname", entry.getValue());
            }
        }
        
        // check if we are dealing with a custom task
        if(isCustomElement((String) properties.get("taskname"), preProcessingData)) {
            properties.put("tasktype", properties.get("taskname"));
        } else {
            properties.put("tasktype", taskType);
        }
        
        // check if the task belongs to a laneset
        String taskLanes = "";
        if(lanesetInfo != null) {
            Iterator<String> lanesetIterator = lanesetInfo.keySet().iterator();
            while(lanesetIterator.hasNext()) {
                String laneSetName = lanesetIterator.next();
                List<String> laneNodeRefs = lanesetInfo.get(laneSetName);
                for(String nodeRef : laneNodeRefs) {
                    if(task.getId().equals(nodeRef)) {
                        taskLanes += laneSetName;
                        taskLanes += ",";
                    }
                }
            }
            if(taskLanes.endsWith(",")) {
                taskLanes = taskLanes.substring(0, taskLanes.length() - 1);
            }
            properties.put("lanes", taskLanes);
        }
        
        // data inputs
        if(task.getIoSpecification() != null) {
            List<InputSet> inputSetList = task.getIoSpecification().getInputSets();
            StringBuilder dataInBuffer = new StringBuilder();
            for(InputSet inset : inputSetList) {
                List<DataInput> dataInputList =  inset.getDataInputRefs();
                for(DataInput dataIn : dataInputList) {
                    // dont add "TaskName" as that is added manually
                    if(dataIn.getName() != null && !dataIn.getName().equals("TaskName")) {
                        dataInBuffer.append(dataIn.getName());
                        dataInBuffer.append(",");
                    }
                }
            }
            if(dataInBuffer.length() > 0) {
                dataInBuffer.setLength(dataInBuffer.length() - 1);
            }
            properties.put("datainputset", dataInBuffer.toString());
        }
        
        // data outputs
        if(task.getIoSpecification() != null) {
            List<OutputSet> outputSetList = task.getIoSpecification().getOutputSets();
            StringBuilder dataOutBuffer = new StringBuilder();
            for(OutputSet outset : outputSetList) {
                List<DataOutput> dataOutputList =  outset.getDataOutputRefs();
                for(DataOutput dataOut : dataOutputList) {
                    dataOutBuffer.append(dataOut.getName());
                    dataOutBuffer.append(",");
                }
            }
            if(dataOutBuffer.length() > 0) {
                dataOutBuffer.setLength(dataOutBuffer.length() - 1);
            }
            properties.put("dataoutputset", dataOutBuffer.toString());
        }
        
        // assignments
        StringBuilder associationBuff = new StringBuilder();
        List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
        List<DataOutputAssociation> outputAssociations = task.getDataOutputAssociations();
        List<String> uniDirectionalAssociations = new ArrayList<String>();
        List<String> biDirectionalAssociations = new ArrayList<String>();
        
        for(DataInputAssociation datain : inputAssociations) {
            String lhsAssociation = "";
            if(datain.getSourceRef() != null && datain.getSourceRef().size() > 0) {
                lhsAssociation = datain.getSourceRef().get(0).getId();
            }
            
            String rhsAssociation = "";
            if(datain.getTargetRef() != null) {
                rhsAssociation = ((DataInput) datain.getTargetRef()).getName();
            }
            
            boolean isBiDirectional = false;
            boolean isAssignment = false;
            
            if(datain.getAssignment() != null && datain.getAssignment().size() > 0) {
                isAssignment = true;
            } else {
                // check if this is a bi-directional association
                for(DataOutputAssociation dataout : outputAssociations) {
                    if(dataout.getTargetRef().getId().equals(lhsAssociation) && 
                       ((DataOutput) dataout.getSourceRef().get(0)).getName().equals(rhsAssociation)) {
                        isBiDirectional = true;
                        break;
                    }
                }
            }
            
            if(isAssignment) {
                String associationValue = ((FormalExpression) datain.getAssignment().get(0).getFrom()).getBody();
                if(associationValue == null) {
                    associationValue = "";
                }
                associationBuff.append(rhsAssociation).append("=").append(associationValue);
                associationBuff.append(",");
            } else if(isBiDirectional) {
                associationBuff.append(lhsAssociation).append("<->").append(rhsAssociation);
                associationBuff.append(",");
                biDirectionalAssociations.add(lhsAssociation + "," + rhsAssociation);
            } else {
                associationBuff.append(lhsAssociation).append("->").append(rhsAssociation);
                associationBuff.append(",");
                uniDirectionalAssociations.add(lhsAssociation + "," + rhsAssociation);
            }
        }
        
        for(DataOutputAssociation dataout : outputAssociations) {
            if(dataout.getSourceRef().size() > 0) { 
                String lhsAssociation = ((DataOutput) dataout.getSourceRef().get(0)).getName();
                String rhsAssociation = dataout.getTargetRef().getId();
                
                boolean wasBiDirectional = false;
                // check if we already addressed this association as bidirectional
                for(String bda : biDirectionalAssociations) {
                    String[] dbaparts = bda.split( ",\\s*" );
                    if(dbaparts[0].equals(rhsAssociation) && dbaparts[1].equals(lhsAssociation)) {
                        wasBiDirectional = true;
                        break;
                    }
                }
                
                if(!wasBiDirectional) {
                    associationBuff.append(lhsAssociation).append("->").append(rhsAssociation);
                    associationBuff.append(",");
                }
            }
        }
        
        String assignmentString = associationBuff.toString();
        if(assignmentString.endsWith(",")) {
            assignmentString = assignmentString.substring(0, assignmentString.length() - 1);
        }
        properties.put("assignments", assignmentString);
        
        
        // marshall the node out
        if(isCustomElement((String) properties.get("taskname"), preProcessingData)) {
            marshallNode(task, properties, (String) properties.get("taskname"), plane, generator, xOffset, yOffset);
        } else {
            marshallNode(task, properties, "Task", plane, generator, xOffset, yOffset);
        }
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
    
    private void marshallSubProcess(SubProcess subProcess, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, String preProcessingData, Map<String, List<String>> lanesetInfo) throws JsonGenerationException, IOException {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
		properties.put("name", subProcess.getName());
	    marshallProperties(properties, generator);
	    generator.writeObjectFieldStart("stencil");
	    if(subProcess instanceof AdHocSubProcess) {
	        generator.writeObjectField("id", "AdHocSubprocess");
	    } else {
	        generator.writeObjectField("id", "Subprocess");
	    }
	    generator.writeEndObject();
	    generator.writeArrayFieldStart("childShapes");
	    Bounds bounds = ((BPMNShape) findDiagramElement(plane, subProcess)).getBounds();
	    for (FlowElement flowElement: subProcess.getFlowElements()) {
	    	marshallFlowElement(flowElement, plane, generator, (int) (xOffset + bounds.getX()), (int) (yOffset + bounds.getY()), preProcessingData, lanesetInfo);
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
    	// check null for sequence flow name
    	if(sequenceFlow.getName() != null && !"".equals(sequenceFlow.getName())) {
    	    properties.put("name", sequenceFlow.getName());
    	} else {
    	    properties.put("name", "");
    	}
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
    
    private boolean isCustomElement(String taskType, String preProcessingData) {
        if(taskType != null && taskType.length() > 0 && preProcessingData != null && preProcessingData.length() > 0) {
            String[] preProcessingDataElements = preProcessingData.split( ",\\s*" );
            for(String preProcessingDataElement : preProcessingDataElements) {
                if(taskType.equals(preProcessingDataElement)) {
                    return true;
                }
            }
        }
        return false;
    }
    
}