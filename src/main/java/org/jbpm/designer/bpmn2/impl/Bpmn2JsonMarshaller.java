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
package org.jbpm.designer.bpmn2.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.AdHocOrdering;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Artifact;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.AssociationDirection;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.Collaboration;
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
import org.eclipse.bpmn2.ExtensionAttributeValue;
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
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.LoopCharacteristics;
import org.eclipse.bpmn2.ManualTask;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.Operation;
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
import org.eclipse.bpmn2.TextAnnotation;
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
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.GlobalType;
import org.jboss.drools.ImportType;
import org.jboss.drools.MetadataType;
import org.jboss.drools.MetaentryType;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.jbpm.designer.web.profile.IDiagramProfile;

/**
 * @author Antoine Toulme
 * @author Tihomir Surdilovic
 * 
 * a marshaller to transform BPMN 2.0 elements into JSON format.
 *
 */
public class Bpmn2JsonMarshaller {
	public static final String defaultBgColor_Activities = "#fafad2";
	public static final String defaultBgColor_Events = "#f5deb3";
	public static final String defaultBrColor = "#000000";
	public static final String defaultFontColor = "#000000";
	public static final String defaultSequenceflowColor = "#000000";
	
	private Map<String, DiagramElement> _diagramElements = new HashMap<String, DiagramElement>();
	private Map<String,Association> _diagramAssociations = new HashMap<String, Association>();
	private static final Logger _logger = Logger.getLogger(Bpmn2JsonMarshaller.class);
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

    protected void marshallDefinitions(Definitions def, JsonGenerator generator, String preProcessingData) throws JsonGenerationException, IOException {
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
	        props.put("name",unescapeXML(def.getName()));
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
	                props.put("name", unescapeXML(((Process) rootElement).getName()));
	                
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
	                
	                // packageName and version and adHoc are jbpm-specific extension attribute
	                Iterator<FeatureMap.Entry> iter = ((Process) rootElement).getAnyAttribute().iterator();
	                while(iter.hasNext()) {
	                    FeatureMap.Entry entry = iter.next();
	                    if(entry.getEStructuralFeature().getName().equals("packageName")) {
	                        props.put("package", entry.getValue());
	                    }
	                    
	                    if(entry.getEStructuralFeature().getName().equals("version")) {
                            props.put("version", entry.getValue());
                        }
	                    
	                    if(entry.getEStructuralFeature().getName().equals("adHoc")) {
	                    	props.put("adhocprocess", entry.getValue());
	                    }
	                }
	                
	                // process imports and globals extension elements
	                if(((Process) rootElement).getExtensionValues() != null && ((Process) rootElement).getExtensionValues().size() > 0) {
	                    String importsStr = "";
	                    String globalsStr = "";
	                    for(ExtensionAttributeValue extattrval : ((Process) rootElement).getExtensionValues()) {
	                        FeatureMap extensionElements = extattrval.getValue();
	                
	                        @SuppressWarnings("unchecked")
	                        List<ImportType> importExtensions = (List<ImportType>) extensionElements
	                                                             .get(DroolsPackage.Literals.DOCUMENT_ROOT__IMPORT, true);
	                
	                        @SuppressWarnings("unchecked")
	                        List<GlobalType> globalExtensions = (List<GlobalType>) extensionElements
	                                                          .get(DroolsPackage.Literals.DOCUMENT_ROOT__GLOBAL, true);
	                    
	                        for(ImportType importType : importExtensions) {
	                            importsStr += importType.getName();
	                            importsStr += ",";
	                        }
	                        
	                        for(GlobalType globalType : globalExtensions) {
	                            globalsStr += (globalType.getIdentifier() + ":" + globalType.getType());
	                            globalsStr += ",";
	                        }
	                    }
	                    if(importsStr.length() > 0) {
	                        if(importsStr.endsWith(",")) {
	                            importsStr = importsStr.substring(0, importsStr.length() - 1);
	                        }
	                        props.put("imports", importsStr);
	                    }
	                    if(globalsStr.length() > 0) {
	                        if(globalsStr.endsWith(",")) {
	                            globalsStr = globalsStr.substring(0, globalsStr.length() - 1);
	                        }
	                        props.put("globals", globalsStr);
	                    }
	                }
	                
	                marshallProperties(props, generator);
	                marshallStencil("BPMNDiagram", generator);
	            	linkSequenceFlows(((Process) rootElement).getFlowElements());
	                marshallProcess((Process) rootElement, def, generator, preProcessingData);
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
                } else if (rootElement instanceof Collaboration) { 
                	
                } else {
                	_logger.warn("Unknown root element " + rootElement + ". This element will not be parsed.");
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
    
    
    /** protected void marshallMessage(Message message, Definitions def, JsonGenerator generator) throws JsonGenerationException, IOException {
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

    protected void marshallCallableElement(CallableElement callableElement, Definitions def, JsonGenerator generator) throws JsonGenerationException, IOException {
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
            marshallProcess((Process) callableElement, def, generator, "");
        } else {
            throw new UnsupportedOperationException("TODO"); //TODO!
        }
        generator.writeEndObject();
    }

    protected void marshallProcess(Process process, Definitions def, JsonGenerator generator, String preProcessingData) throws JsonGenerationException, IOException {
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
        
        List<String> laneFlowElementsIds = new ArrayList<String>();
        for(LaneSet laneSet : process.getLaneSets()) {
        	for(Lane lane : laneSet.getLanes()) {
        		// we only want to marshall lanes if we have the bpmndi info for them!
        		if(findDiagramElement(plane, lane) != null) {
        			laneFlowElementsIds.addAll( marshallLanes(lane, plane, generator, 0, 0, preProcessingData, def) );
        		}
        	}
        }
        for (FlowElement flowElement: process.getFlowElements()) {
        	if( !laneFlowElementsIds.contains(flowElement.getId()) ) {
        		marshallFlowElement(flowElement, plane, generator, 0, 0, preProcessingData, def);
        	}
        }
        
        for (Artifact artifact: process.getArtifacts()) {
        	marshallArtifact(artifact, plane, generator, 0, 0, preProcessingData, def);
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
//                if(ted.getTimeDate() != null) {
//                    properties.put("timedate", ((FormalExpression) ted.getTimeDate()).getBody());
//                }
                if(ted.getTimeDuration() != null) {
                    properties.put("timeduration", ((FormalExpression) ted.getTimeDuration()).getBody());
                }
                if(ted.getTimeCycle() != null) {
                    properties.put("timecycle", ((FormalExpression) ted.getTimeCycle()).getBody());
                    if(((FormalExpression) ted.getTimeCycle()).getLanguage() != null) {
                    	properties.put("timecyclelanguage", ((FormalExpression) ted.getTimeCycle()).getLanguage());
                    }
                }
            } else if( ed instanceof SignalEventDefinition) {
                if(((SignalEventDefinition) ed).getSignalRef() != null) {
                    properties.put("signalref", ((SignalEventDefinition) ed).getSignalRef());
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
//                if(ted.getTimeDate() != null) {
//                    properties.put("timedate", ((FormalExpression) ted.getTimeDate()).getBody());
//                }
                if(ted.getTimeDuration() != null) {
                    properties.put("timeduration", ((FormalExpression) ted.getTimeDuration()).getBody());
                }
                if(ted.getTimeCycle() != null) {
                    properties.put("timecycle", ((FormalExpression) ted.getTimeCycle()).getBody());
                    if(((FormalExpression) ted.getTimeCycle()).getLanguage() != null) {
                    	properties.put("timecyclelanguage", ((FormalExpression) ted.getTimeCycle()).getLanguage());
                    }
                }
            } else if( ed instanceof SignalEventDefinition) {
                if(((SignalEventDefinition) ed).getSignalRef() != null) {
                    properties.put("signalref", ((SignalEventDefinition) ed).getSignalRef());
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
    
    private List<String> marshallLanes(Lane lane, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, String preProcessingData, Definitions def) throws JsonGenerationException, IOException {
    	Bounds bounds = ((BPMNShape) findDiagramElement(plane, lane)).getBounds();
    	List<String> nodeRefIds = new ArrayList<String>();
    	if(bounds != null) {
	    	generator.writeStartObject();
	    	generator.writeObjectField("resourceId", lane.getId());
	    	Map<String, Object> laneProperties = new LinkedHashMap<String, Object>();
	    	if(lane.getName() != null) {
	    		laneProperties.put("name", unescapeXML(lane.getName()));
	    	} else {
	    		laneProperties.put("name", "");
	    	}
	    	
	    	Iterator<FeatureMap.Entry> iter = lane.getAnyAttribute().iterator();
	    	boolean foundBgColor = false;
	    	boolean foundBrColor = false;
	    	boolean foundFontColor = false;
	    	boolean foundSelectable = false;
	        while(iter.hasNext()) {
	            FeatureMap.Entry entry = iter.next();
	            if(entry.getEStructuralFeature().getName().equals("bgcolor")) {
	            	laneProperties.put("bgcolor", entry.getValue());
	            	foundBgColor = true;
	            }
	            if(entry.getEStructuralFeature().getName().equals("bordercolor")) {
	            	laneProperties.put("bordercolor", entry.getValue());
	            	foundBrColor = true;
	            }
	            if(entry.getEStructuralFeature().getName().equals("fontsize")) {
	            	laneProperties.put("fontsize", entry.getValue());
	            	foundBrColor = true;
	            }
	            if(entry.getEStructuralFeature().getName().equals("fontcolor")) {
	            	laneProperties.put("fontcolor", entry.getValue());
	            	foundFontColor = true;
	            }
	            if(entry.getEStructuralFeature().getName().equals("selectable")) {
	            	laneProperties.put("isselectable", entry.getValue());
	            	foundSelectable = true;
	            }
	        }
	        if(!foundBgColor) {
	        	laneProperties.put("bgcolor", defaultBgColor_Events);
	        }
	        
	        if(!foundBrColor) {
	        	laneProperties.put("bordercolor", defaultBrColor);
	        }
	        
	        if(!foundFontColor) {
	        	laneProperties.put("fontcolor", defaultFontColor);
	        }
	        
	        if(!foundSelectable) {
	        	laneProperties.put("isselectable", "true");
	        }
	    	
	    	marshallProperties(laneProperties, generator);
	    	generator.writeObjectFieldStart("stencil");
	    	generator.writeObjectField("id", "Lane");
		    generator.writeEndObject();
		    generator.writeArrayFieldStart("childShapes");
		    for (FlowElement flowElement: lane.getFlowNodeRefs()) {
		    	nodeRefIds.add(flowElement.getId());
		    	// we dont want an offset here!
		    	marshallFlowElement(flowElement, plane, generator, 0, 0, preProcessingData, def);
		    }
		    generator.writeEndArray();
		    generator.writeArrayFieldStart("outgoing");
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
	    	generator.writeEndObject();
    	} else {
    		// dont marshall the lane unless it has BPMNDI info (eclipse editor does not generate it for lanes currently.
    		for (FlowElement flowElement: lane.getFlowNodeRefs()) {
		    	nodeRefIds.add(flowElement.getId());
		    	// we dont want an offset here!
		    	marshallFlowElement(flowElement, plane, generator, 0, 0, preProcessingData, def);
		    }
    	}
    	
    	return nodeRefIds;
    }
    
    protected void marshallFlowElement(FlowElement flowElement, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, String preProcessingData, Definitions def) throws JsonGenerationException, IOException {
    	generator.writeStartObject();
    	generator.writeObjectField("resourceId", flowElement.getId());
    	
    	Map<String, Object> flowElementProperties = new LinkedHashMap<String, Object>();
    	Iterator<FeatureMap.Entry> iter = flowElement.getAnyAttribute().iterator();
    	boolean foundBgColor = false;
    	boolean foundBrColor = false;
    	boolean foundFontColor = false;
    	boolean foundSelectable = false;
        while(iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if(entry.getEStructuralFeature().getName().equals("bgcolor")) {
            	flowElementProperties.put("bgcolor", entry.getValue());
            	foundBgColor = true;
            }
            if(entry.getEStructuralFeature().getName().equals("bordercolor")) {
            	flowElementProperties.put("bordercolor", entry.getValue());
            	foundBrColor = true;
            }
            if(entry.getEStructuralFeature().getName().equals("fontsize")) {
            	flowElementProperties.put("fontsize", entry.getValue());
            	foundBrColor = true;
            }
            if(entry.getEStructuralFeature().getName().equals("fontcolor")) {
            	flowElementProperties.put("fontcolor", entry.getValue());
            	foundFontColor = true;
            }
            if(entry.getEStructuralFeature().getName().equals("selectable")) {
            	flowElementProperties.put("isselectable", entry.getValue());
            	foundSelectable = true;
            }
        }
        if(!foundBgColor) {
        	if(flowElement instanceof Activity || flowElement instanceof SubProcess ) {
        		flowElementProperties.put("bgcolor", defaultBgColor_Activities);
        	} else {
        		flowElementProperties.put("bgcolor", defaultBgColor_Events);
        	}
        }
        
        if(!foundBrColor) {
        	flowElementProperties.put("bordercolor", defaultBrColor);
        }
        
        if(!foundFontColor) {
        	flowElementProperties.put("fontcolor", defaultFontColor);
        }
        
        if(!foundSelectable) {
        	flowElementProperties.put("isselectable", "true");
        }

        Map<String, Object> catchEventProperties = new LinkedHashMap<String, Object>(flowElementProperties);
    	Map<String, Object> throwEventProperties = new LinkedHashMap<String, Object>(flowElementProperties);
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
    		marshallBoundaryEvent((BoundaryEvent) flowElement, plane, generator, xOffset, yOffset, catchEventProperties);
    	} else if (flowElement instanceof Task) {
    		marshallTask((Task) flowElement, plane, generator, xOffset, yOffset, preProcessingData, def, flowElementProperties);
    	} else if (flowElement instanceof SequenceFlow) {
    		marshallSequenceFlow((SequenceFlow) flowElement, plane, generator, xOffset, yOffset);
    	} else if (flowElement instanceof ParallelGateway) {
    		marshallParallelGateway((ParallelGateway) flowElement, plane, generator, xOffset, yOffset, flowElementProperties);
    	} else if (flowElement instanceof ExclusiveGateway) {
    		marshallExclusiveGateway((ExclusiveGateway) flowElement, plane, generator, xOffset, yOffset, flowElementProperties);
    	} else if (flowElement instanceof InclusiveGateway) {
    		marshallInclusiveGateway((InclusiveGateway) flowElement, plane, generator, xOffset, yOffset, flowElementProperties);
    	} else if (flowElement instanceof EventBasedGateway) {
    		marshallEventBasedGateway((EventBasedGateway) flowElement, plane, generator, xOffset, yOffset, flowElementProperties);
    	} else if (flowElement instanceof ComplexGateway) {
    		marshallComplexGateway((ComplexGateway) flowElement, plane, generator, xOffset, yOffset, flowElementProperties);
    	} else if (flowElement instanceof CallActivity) {
    		marshallCallActivity((CallActivity) flowElement, plane, generator, xOffset, yOffset, flowElementProperties);
    	} else if (flowElement instanceof SubProcess) {
    	    if(flowElement instanceof AdHocSubProcess) {
    	        marshallSubProcess((AdHocSubProcess) flowElement, plane, generator, xOffset, yOffset, preProcessingData, def, flowElementProperties);
    	    } else {
    	        marshallSubProcess((SubProcess) flowElement, plane, generator, xOffset, yOffset, preProcessingData, def, flowElementProperties);
    	    }
    	} else if (flowElement instanceof DataObject) {
    		// only marshall if we can find DI info for it - BZ 800346
    		if(findDiagramElement(plane, (DataObject) flowElement) != null) {
    			marshallDataObject((DataObject) flowElement, plane, generator, xOffset, yOffset, flowElementProperties);
    		} else {
    			_logger.info("Could not marshall Data Object " + (DataObject) flowElement + " because no DI information could be found.");
    		}
    	} else {
    		throw new UnsupportedOperationException("Unknown flow element " + flowElement);
    	}
    	generator.writeEndObject();
    }
    
    protected void marshallStartEvent(StartEvent startEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> properties) throws JsonGenerationException, IOException {
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
    
    protected void marshallEndEvent(EndEvent endEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> properties) throws JsonGenerationException, IOException {
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
    
    protected void marshallIntermediateCatchEvent(IntermediateCatchEvent catchEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> properties) throws JsonGenerationException, IOException {
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
    
    protected void marshallBoundaryEvent(BoundaryEvent boundaryEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> catchEventProperties) throws JsonGenerationException, IOException {
    	List<EventDefinition> eventDefinitions = boundaryEvent.getEventDefinitions();
    	if(boundaryEvent.isCancelActivity()) {
    		catchEventProperties.put("boundarycancelactivity", "true");
    	} else {
    		catchEventProperties.put("boundarycancelactivity", "false");
    	}
    	if (eventDefinitions.size() == 1) {
    		EventDefinition eventDefinition = eventDefinitions.get(0);
    		if (eventDefinition instanceof SignalEventDefinition) {
    			marshallNode(boundaryEvent, catchEventProperties, "IntermediateSignalEventCatching", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof EscalationEventDefinition) {
    			marshallNode(boundaryEvent, catchEventProperties, "IntermediateEscalationEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof ErrorEventDefinition) {
    			marshallNode(boundaryEvent, catchEventProperties, "IntermediateErrorEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof TimerEventDefinition) {
    			marshallNode(boundaryEvent, catchEventProperties, "IntermediateTimerEvent", plane, generator, xOffset, yOffset);
    		} else if (eventDefinition instanceof CompensateEventDefinition) {
    			marshallNode(boundaryEvent, catchEventProperties, "IntermediateCompensationEventCatching", plane, generator, xOffset, yOffset);
    		} else if(eventDefinition instanceof ConditionalEventDefinition) {
    		    marshallNode(boundaryEvent, catchEventProperties, "IntermediateConditionalEvent", plane, generator, xOffset, yOffset);
    		} else if(eventDefinition instanceof MessageEventDefinition) {
    		    marshallNode(boundaryEvent, catchEventProperties, "IntermediateMessageEventCatching", plane, generator, xOffset, yOffset);
    		}else {
    			throw new UnsupportedOperationException("Event definition not supported: " + eventDefinition);
    		}
    	} else {
    		throw new UnsupportedOperationException("None or multiple event definitions not supported for boundary event");
    	}
    }
    
    protected void marshallIntermediateThrowEvent(IntermediateThrowEvent throwEvent, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> properties) throws JsonGenerationException, IOException {
    	List<EventDefinition> eventDefinitions = throwEvent.getEventDefinitions();
    	if (eventDefinitions.size() == 0) {
			marshallNode(throwEvent, properties, "IntermediateEvent", plane, generator, xOffset, yOffset);
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
    
    protected void marshallCallActivity(CallActivity callActivity, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
    	Map<String, Object> properties = new LinkedHashMap<String, Object>(flowElementProperties);
    	
    	Iterator<FeatureMap.Entry> iter = callActivity.getAnyAttribute().iterator();
        while(iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if(entry.getEStructuralFeature().getName().equals("independent")) {
                properties.put("independent", entry.getValue());
            }
            
            if(entry.getEStructuralFeature().getName().equals("waitForCompletion")) {
                properties.put("waitforcompletion", entry.getValue());
            }
        }
        
        if(callActivity.getCalledElement() != null && callActivity.getCalledElement().length() > 0) {
        	properties.put("calledelement", callActivity.getCalledElement());
        }
        
        // data inputs
        if(callActivity.getIoSpecification() != null) {
            List<InputSet> inputSetList = callActivity.getIoSpecification().getInputSets();
            StringBuilder dataInBuffer = new StringBuilder();
            for(InputSet inset : inputSetList) {
                List<DataInput> dataInputList =  inset.getDataInputRefs();
                for(DataInput dataIn : dataInputList) {
                    if(dataIn.getName() != null) {
                        dataInBuffer.append(dataIn.getName());
                        if(dataIn.getItemSubjectRef() != null && dataIn.getItemSubjectRef().getStructureRef() != null && dataIn.getItemSubjectRef().getStructureRef().length() > 0) {
                        	dataInBuffer.append(":").append(dataIn.getItemSubjectRef().getStructureRef());
                        }
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
        if(callActivity.getIoSpecification() != null) {
            List<OutputSet> outputSetList = callActivity.getIoSpecification().getOutputSets();
            StringBuilder dataOutBuffer = new StringBuilder();
            for(OutputSet outset : outputSetList) {
                List<DataOutput> dataOutputList =  outset.getDataOutputRefs();
                for(DataOutput dataOut : dataOutputList) {
                    dataOutBuffer.append(dataOut.getName());
                    if(dataOut.getItemSubjectRef() != null && dataOut.getItemSubjectRef().getStructureRef() != null && dataOut.getItemSubjectRef().getStructureRef().length() > 0) {
                    	dataOutBuffer.append(":").append(dataOut.getItemSubjectRef().getStructureRef());
                    }
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
        List<DataInputAssociation> inputAssociations = callActivity.getDataInputAssociations();
        List<DataOutputAssociation> outputAssociations = callActivity.getDataOutputAssociations();
        List<String> uniDirectionalAssociations = new ArrayList<String>();
        //List<String> biDirectionalAssociations = new ArrayList<String>();
        
        for(DataInputAssociation datain : inputAssociations) {
            String lhsAssociation = "";
            if(datain.getSourceRef() != null && datain.getSourceRef().size() > 0) {
            	if(datain.getTransformation() != null && datain.getTransformation().getBody() != null) {
            		lhsAssociation = datain.getTransformation().getBody();
            	} else {
            		lhsAssociation = datain.getSourceRef().get(0).getId();
            	}
            }
            
            String rhsAssociation = "";
            if(datain.getTargetRef() != null) {
                rhsAssociation = ((DataInput) datain.getTargetRef()).getName();
            }
            
            //boolean isBiDirectional = false;
            boolean isAssignment = false;
            
            if(datain.getAssignment() != null && datain.getAssignment().size() > 0) {
                isAssignment = true;
            } 
//            else {
//                // check if this is a bi-directional association
//                for(DataOutputAssociation dataout : outputAssociations) {
//                    if(dataout.getTargetRef().getId().equals(lhsAssociation) && 
//                       ((DataOutput) dataout.getSourceRef().get(0)).getName().equals(rhsAssociation)) {
//                        isBiDirectional = true;
//                        break;
//                    }
//                }
//            }
            
            if(isAssignment) {
            	// only know how to deal with formal expressions
            	if( datain.getAssignment().get(0).getFrom() instanceof FormalExpression) {
            		String associationValue = ((FormalExpression) datain.getAssignment().get(0).getFrom()).getBody();
            		if(associationValue == null) {
            			associationValue = "";
            		}
            		String replacer = associationValue.replaceAll(",", "##");
            		associationBuff.append(rhsAssociation).append("=").append(replacer);
                	associationBuff.append(",");
            	}
            } 
//            else if(isBiDirectional) {
//                associationBuff.append(lhsAssociation).append("<->").append(rhsAssociation);
//                associationBuff.append(",");
//                biDirectionalAssociations.add(lhsAssociation + "," + rhsAssociation);
//            } 
            else {
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
//                for(String bda : biDirectionalAssociations) {
//                    String[] dbaparts = bda.split( ",\\s*" );
//                    if(dbaparts[0].equals(rhsAssociation) && dbaparts[1].equals(lhsAssociation)) {
//                        wasBiDirectional = true;
//                        break;
//                    }
//                }
                
                if(dataout.getTransformation() != null && dataout.getTransformation().getBody() != null) {
                	rhsAssociation = dataout.getTransformation().getBody();
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
        
        // on-entry and on-exit actions
        if(callActivity.getExtensionValues() != null && callActivity.getExtensionValues().size() > 0) {
            
            String onEntryStr = "";
            String onExitStr = "";
            for(ExtensionAttributeValue extattrval : callActivity.getExtensionValues()) {
            
                FeatureMap extensionElements = extattrval.getValue();
        
                @SuppressWarnings("unchecked")
                List<OnEntryScriptType> onEntryExtensions = (List<OnEntryScriptType>) extensionElements
                                                     .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, true);
        
                @SuppressWarnings("unchecked")
                List<OnExitScriptType> onExitExtensions = (List<OnExitScriptType>) extensionElements
                                                  .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, true);
            
                for(OnEntryScriptType onEntryScript : onEntryExtensions) {
                    onEntryStr += onEntryScript.getScript();
                    onEntryStr += "|";
                
                    if(onEntryScript.getScriptFormat() != null) {
                        String format = onEntryScript.getScriptFormat();
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
                
                for(OnExitScriptType onExitScript : onExitExtensions) {
                    onExitStr += onExitScript.getScript();
                    onExitStr += "|";
                    
                    if(onExitScript.getScriptFormat() != null) {
                        String format = onExitScript.getScriptFormat();
                        String formatToWrite = "";
                        if(format.equals("http://www.java.com/java")) {
                            formatToWrite = "java";
                        } else if(format.equals("http://www.mvel.org/2.0")) {
                            formatToWrite = "mvel";
                        } else {
                            formatToWrite = "java";
                        }
                        if(properties.get("script_language") != null) {
                            properties.put("script_language", formatToWrite);
                        }
                    }
                }
            }
            if(onEntryStr.length() > 0) {
                if(onEntryStr.endsWith("|")) {
                    onEntryStr = onEntryStr.substring(0, onEntryStr.length() - 1);
                }
                properties.put("onentryactions", onEntryStr);
            }
            if(onExitStr.length() > 0) {
                if(onExitStr.endsWith("|")) {
                    onExitStr = onExitStr.substring(0, onExitStr.length() - 1);
                }
                properties.put("onexitactions", onExitStr);
            }
        }
    	
        marshallNode(callActivity, properties, "ReusableSubprocess", plane, generator, xOffset, yOffset);
    }
    
    protected void marshallTask(Task task, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, String preProcessingData, Definitions def, Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
        Map<String, Object> properties = new LinkedHashMap<String, Object>(flowElementProperties);
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
    		properties.put("script", scriptTask.getScript() != null ? scriptTask.getScript() : "");
    		String format = scriptTask.getScriptFormat();
    		if(format != null && format.length() > 0) {
    			String formatToWrite = "";
                if(format.equals("http://www.java.com/java")) {
                    formatToWrite = "java";
                } else if(format.equals("http://www.mvel.org/2.0")) {
                    formatToWrite = "mvel";
                } else {
                	// default to java
                    formatToWrite = "java";
                }
                properties.put("script_language", formatToWrite);
    		}
    		taskType = "Script";
    	} else if (task instanceof ServiceTask) {
    		taskType = "Service";
    		ServiceTask serviceTask = (ServiceTask) task;
    		if(serviceTask.getOperationRef() != null) {
    		    Operation oper = serviceTask.getOperationRef();
    		    if(oper.getName() != null) {
    		        properties.put("operation", oper.getName());
    		    }
    		    if(def != null) {
    		        List<RootElement> roots = def.getRootElements();
    		        for(RootElement root : roots) {
    		            if(root instanceof Interface) {
    		                Interface inter = (Interface) root;
    		                List<Operation> interOperations = inter.getOperations();
    		                for(Operation interOper : interOperations) {
    		                    if(interOper.getId().equals(oper.getId())) {
    		                        properties.put("interface", inter.getName());
    		                    }
    		                }
    		            }
    		        }
    		    }
    		}
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
    		// simulation properties
            if(task.getExtensionValues() != null && task.getExtensionValues().size() > 0) {
    	        for(ExtensionAttributeValue extattrval : task.getExtensionValues()) {
    	        	FeatureMap extensionElements = extattrval.getValue();
    	            
                    @SuppressWarnings("unchecked")
                    List<MetadataType> metadataTypeExtensions = (List<MetadataType>) extensionElements
                                                         .get(DroolsPackage.Literals.DOCUMENT_ROOT__METADATA, true);
                    if(metadataTypeExtensions != null && metadataTypeExtensions.size() > 0) {
                    	MetadataType metaType = metadataTypeExtensions.get(0);
                    	for(Object metaEntryObj : metaType.getMetaentry()) {
                    		MetaentryType entry = (MetaentryType) metaEntryObj;
                    		if(entry.getName() != null && entry.getName().equals("staffavailability")) {
                    			properties.put("staffavailability", entry.getValue());
                    		}
                    		if(entry.getName() != null && entry.getName().equals("workinghours")) {
                    			properties.put("workinghours", entry.getValue());
                    		}
                    		if(entry.getName() != null && entry.getName().equals("costpertimeunit")) {
                    			properties.put("costpertimeunit", entry.getValue());
                    		}
                    	}
                    }
                    
    	        }
            }
    	} else if (task instanceof SendTask) {
    		taskType = "Send";
    		SendTask st = (SendTask) task;
    		if(st.getMessageRef() != null) {
    			properties.put("messageref", st.getMessageRef().getId());
    		}
    	} else if (task instanceof ReceiveTask) {
    		taskType = "Receive";
    		ReceiveTask rt = (ReceiveTask) task;
    		if(rt.getMessageRef() != null) {
    			properties.put("messageref", rt.getMessageRef().getId());
    		}
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
        
        // data inputs
        DataInput groupDataInput = null;
        DataInput skippableDataInput = null;
        DataInput commentDataInput = null;
        DataInput contentDataInput = null;
        DataInput priorityDataInput = null;
        if(task.getIoSpecification() != null) {
            List<InputSet> inputSetList = task.getIoSpecification().getInputSets();
            StringBuilder dataInBuffer = new StringBuilder();
            for(InputSet inset : inputSetList) {
                List<DataInput> dataInputList =  inset.getDataInputRefs();
                for(DataInput dataIn : dataInputList) {
                    // dont add "TaskName" as that is added manually
                    if(dataIn.getName() != null && !dataIn.getName().equals("TaskName")) {
                        dataInBuffer.append(dataIn.getName());
                        if(dataIn.getItemSubjectRef() != null && dataIn.getItemSubjectRef().getStructureRef() != null && dataIn.getItemSubjectRef().getStructureRef().length() > 0) {
                        	dataInBuffer.append(":").append(dataIn.getItemSubjectRef().getStructureRef());
                        }
                        dataInBuffer.append(",");
                    }
                    if(dataIn.getName() != null && dataIn.getName().equals("GroupId")) {
                    	groupDataInput = dataIn;
                    }
                    if(dataIn.getName() != null && dataIn.getName().equals("Skippable")) {
                    	skippableDataInput = dataIn;
                    }
                    if(dataIn.getName() != null && dataIn.getName().equals("Comment")) {
                    	commentDataInput = dataIn;
                    }
                    if(dataIn.getName() != null && dataIn.getName().equals("Content")) {
                    	contentDataInput = dataIn;
                    }
                    if(dataIn.getName() != null && dataIn.getName().equals("Priority")) {
                    	priorityDataInput = dataIn;
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
                    if(dataOut.getItemSubjectRef() != null && dataOut.getItemSubjectRef().getStructureRef() != null && dataOut.getItemSubjectRef().getStructureRef().length() > 0) {
                    	dataOutBuffer.append(":").append(dataOut.getItemSubjectRef().getStructureRef());
                    }
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
        //List<String> biDirectionalAssociations = new ArrayList<String>();
        
        for(DataInputAssociation datain : inputAssociations) {
            String lhsAssociation = "";
            if(datain.getSourceRef() != null && datain.getSourceRef().size() > 0) {
            	if(datain.getTransformation() != null && datain.getTransformation().getBody() != null) {
            		lhsAssociation = datain.getTransformation().getBody();
            	} else {
            		lhsAssociation = datain.getSourceRef().get(0).getId();
            	}
            }
            
            String rhsAssociation = "";
            if(datain.getTargetRef() != null) {
                rhsAssociation = ((DataInput) datain.getTargetRef()).getName();
            }
            
            //boolean isBiDirectional = false;
            boolean isAssignment = false;
            
            if(datain.getAssignment() != null && datain.getAssignment().size() > 0) {
                isAssignment = true;
            } 
//            else {
//                // check if this is a bi-directional association
//                for(DataOutputAssociation dataout : outputAssociations) {
//                    if(dataout.getTargetRef().getId().equals(lhsAssociation) && 
//                       ((DataOutput) dataout.getSourceRef().get(0)).getName().equals(rhsAssociation)) {
//                        isBiDirectional = true;
//                        break;
//                    }
//                }
//            }
            
            if(isAssignment) {
            	// only know how to deal with formal expressions
            	if( datain.getAssignment().get(0).getFrom() instanceof FormalExpression) {
            		String associationValue = ((FormalExpression) datain.getAssignment().get(0).getFrom()).getBody();
            		if(associationValue == null) {
            			associationValue = "";
            		}
            		
            		// don't include properties that have their independent input editors:
            		if(!(rhsAssociation.equals("GroupId") || 
            		   rhsAssociation.equals("Skippable") ||
            		   rhsAssociation.equals("Comment") || 
            		   rhsAssociation.equals("Priority") ||
            		   rhsAssociation.equals("Content") ||
            		   rhsAssociation.equals("TaskName")
            		   )) {
            			String replacer = associationValue.replaceAll(",", "##");
            			associationBuff.append(rhsAssociation).append("=").append(replacer);
            			associationBuff.append(",");
            		}
            		
            		if(rhsAssociation.equalsIgnoreCase("TaskName")) {
            			properties.put("taskname", associationValue);
            		}
            		
            		if (groupDataInput != null && datain.getAssignment().get(0).getTo() != null &&
            				((FormalExpression) datain.getAssignment().get(0).getTo()).getBody() != null &&
            						((FormalExpression) datain.getAssignment().get(0).getTo()).getBody().equals(groupDataInput.getId())) {
            			properties.put("groupid", ((FormalExpression) datain.getAssignment().get(0).getFrom()).getBody());
            		}
            		if (skippableDataInput != null && datain.getAssignment().get(0).getTo() != null &&
            				((FormalExpression) datain.getAssignment().get(0).getTo()).getBody() != null &&
            						((FormalExpression) datain.getAssignment().get(0).getTo()).getBody().equals(skippableDataInput.getId())) {
            			properties.put("skippable", ((FormalExpression) datain.getAssignment().get(0).getFrom()).getBody());
            		}
            		if (commentDataInput != null && datain.getAssignment().get(0).getTo() != null &&
            				((FormalExpression) datain.getAssignment().get(0).getTo()).getBody() != null &&
            						((FormalExpression) datain.getAssignment().get(0).getTo()).getBody().equals(commentDataInput.getId())) {
            			properties.put("comment", ((FormalExpression) datain.getAssignment().get(0).getFrom()).getBody());
            		}
            		if (priorityDataInput != null && datain.getAssignment().get(0).getTo() != null &&
            				((FormalExpression) datain.getAssignment().get(0).getTo()).getBody() != null &&
            						((FormalExpression) datain.getAssignment().get(0).getTo()).getBody().equals(priorityDataInput.getId())) {
            			properties.put("priority", ((FormalExpression) datain.getAssignment().get(0).getFrom()).getBody());
            		}
            	}
            } 
//            else if(isBiDirectional) {
//                associationBuff.append(lhsAssociation).append("<->").append(rhsAssociation);
//                associationBuff.append(",");
//                biDirectionalAssociations.add(lhsAssociation + "," + rhsAssociation);
//            } 
            else {
                associationBuff.append(lhsAssociation).append("->").append(rhsAssociation);
                associationBuff.append(",");
                uniDirectionalAssociations.add(lhsAssociation + "," + rhsAssociation);
                
                if(contentDataInput != null) {
                	if(rhsAssociation.equals(contentDataInput.getName())) {
                		properties.put("content", lhsAssociation);
                	}
                }
            }
        }
        
        for(DataOutputAssociation dataout : outputAssociations) {
            if(dataout.getSourceRef().size() > 0) { 
                String lhsAssociation = ((DataOutput) dataout.getSourceRef().get(0)).getName();
                String rhsAssociation = dataout.getTargetRef().getId();
                
                boolean wasBiDirectional = false;
                // check if we already addressed this association as bidirectional
//                for(String bda : biDirectionalAssociations) {
//                    String[] dbaparts = bda.split( ",\\s*" );
//                    if(dbaparts[0].equals(rhsAssociation) && dbaparts[1].equals(lhsAssociation)) {
//                        wasBiDirectional = true;
//                        break;
//                    }
//                }
                
                if(dataout.getTransformation() != null && dataout.getTransformation().getBody() != null) {
                	rhsAssociation = dataout.getTransformation().getBody();
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
        
        // on-entry and on-exit actions
        if(task.getExtensionValues() != null && task.getExtensionValues().size() > 0) {
            
            String onEntryStr = "";
            String onExitStr = "";
            for(ExtensionAttributeValue extattrval : task.getExtensionValues()) {
            
                FeatureMap extensionElements = extattrval.getValue();
        
                @SuppressWarnings("unchecked")
                List<OnEntryScriptType> onEntryExtensions = (List<OnEntryScriptType>) extensionElements
                                                     .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, true);
        
                @SuppressWarnings("unchecked")
                List<OnExitScriptType> onExitExtensions = (List<OnExitScriptType>) extensionElements
                                                  .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, true);
            
                for(OnEntryScriptType onEntryScript : onEntryExtensions) {
                    onEntryStr += onEntryScript.getScript();
                    onEntryStr += "|";
                
                    if(onEntryScript.getScriptFormat() != null) {
                        String format = onEntryScript.getScriptFormat();
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
                
                for(OnExitScriptType onExitScript : onExitExtensions) {
                    onExitStr += onExitScript.getScript();
                    onExitStr += "|";
                    
                    if(onExitScript.getScriptFormat() != null) {
                        String format = onExitScript.getScriptFormat();
                        String formatToWrite = "";
                        if(format.equals("http://www.java.com/java")) {
                            formatToWrite = "java";
                        } else if(format.equals("http://www.mvel.org/2.0")) {
                            formatToWrite = "mvel";
                        } else {
                            formatToWrite = "java";
                        }
                        if(properties.get("script_language") != null) {
                            properties.put("script_language", formatToWrite);
                        }
                    }
                }
            }
            if(onEntryStr.length() > 0) {
                if(onEntryStr.endsWith("|")) {
                    onEntryStr = onEntryStr.substring(0, onEntryStr.length() - 1);
                }
                properties.put("onentryactions", onEntryStr);
            }
            if(onExitStr.length() > 0) {
                if(onExitStr.endsWith("|")) {
                    onExitStr = onExitStr.substring(0, onExitStr.length() - 1);
                }
                properties.put("onexitactions", onExitStr);
            }
        }
        
        // simulation properties
        if(task.getExtensionValues() != null && task.getExtensionValues().size() > 0) {
	        for(ExtensionAttributeValue extattrval : task.getExtensionValues()) {
	        	FeatureMap extensionElements = extattrval.getValue();
	            
                @SuppressWarnings("unchecked")
                List<MetadataType> metadataTypeExtensions = (List<MetadataType>) extensionElements
                                                     .get(DroolsPackage.Literals.DOCUMENT_ROOT__METADATA, true);
                if(metadataTypeExtensions != null && metadataTypeExtensions.size() > 0) {
                	MetadataType metaType = metadataTypeExtensions.get(0);
                	for(Object metaEntryObj : metaType.getMetaentry()) {
                		MetaentryType entry = (MetaentryType) metaEntryObj;
                		if(entry.getName() != null && entry.getName().equals("duration")) {
                			properties.put("duration", entry.getValue());
                		}
                		if(entry.getName() != null && entry.getName().equals("timeunit")) {
                			properties.put("timeunit", entry.getValue());
                		}
                		if(entry.getName() != null && entry.getName().equals("range")) {
                			properties.put("range", entry.getValue());
                		}
                		if(entry.getName() != null && entry.getName().equals("standarddeviation")) {
                			properties.put("standarddeviation", entry.getValue());
                		}
                		if(entry.getName() != null && entry.getName().equals("distributiontype")) {
                			properties.put("distributiontype", entry.getValue());
                		}
                	}
                }
                
	        }
        }
        
        // marshall the node out
        if(isCustomElement((String) properties.get("taskname"), preProcessingData)) {
            marshallNode(task, properties, (String) properties.get("taskname"), plane, generator, xOffset, yOffset);
        } else {
            marshallNode(task, properties, "Task", plane, generator, xOffset, yOffset);
        }
    }
    
    protected void marshallParallelGateway(ParallelGateway gateway, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
    	marshallNode(gateway, flowElementProperties, "ParallelGateway", plane, generator, xOffset, yOffset);
    }
    
    protected void marshallExclusiveGateway(ExclusiveGateway gateway, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
    	if(gateway.getDefault() != null) {
    		flowElementProperties.put("defaultgate", gateway.getDefault().getId());
    	}
    	marshallNode(gateway, flowElementProperties, "Exclusive_Databased_Gateway", plane, generator, xOffset, yOffset);
    }
    
    protected void marshallInclusiveGateway(InclusiveGateway gateway, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
    	if(gateway.getDefault() != null) {
    		flowElementProperties.put("defaultgate", gateway.getDefault().getId());
    	}
    	marshallNode(gateway, flowElementProperties, "InclusiveGateway", plane, generator, xOffset, yOffset);
    }
    
    protected void marshallEventBasedGateway(EventBasedGateway gateway, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
    	marshallNode(gateway, flowElementProperties, "EventbasedGateway", plane, generator, xOffset, yOffset);
    }
    
    protected void marshallComplexGateway(ComplexGateway gateway, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
    	marshallNode(gateway, flowElementProperties, "ComplexGateway", plane, generator, xOffset, yOffset);
    }
    
    protected void marshallNode(FlowNode node, Map<String, Object> properties, String stencil, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	if (properties == null) {
    		properties = new LinkedHashMap<String, Object>();
    	}
        if(node.getDocumentation() != null && node.getDocumentation().size() > 0) {
            properties.put("documentation", node.getDocumentation().get(0).getText());
        }
        if(node.getName() != null) {
        	properties.put("name", unescapeXML(node.getName()));
        } else {
        	properties.put("name", "");
        }
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
        // we need to also add associations as outgoing elements
        Process process = (Process) plane.getBpmnElement();
        for (Artifact artifact : process.getArtifacts()) {
        	if (artifact instanceof Association){
                Association association = (Association) artifact;
                if (association.getSourceRef().getId().equals(node.getId())) {
                	generator.writeStartObject();
                	generator.writeObjectField("resourceId", association.getId());
                	generator.writeEndObject();
                }
        	}
        }
        // and boundary events for activities
        for(FlowElement fe : process.getFlowElements()) {
        	if(fe instanceof BoundaryEvent) {
        		if(((BoundaryEvent) fe).getAttachedToRef().getId().equals(node.getId())) {
        			generator.writeStartObject();
                    generator.writeObjectField("resourceId", fe.getId());
                    generator.writeEndObject();
        		}
        	}
        }
        generator.writeEndArray();
        
        // boundary events have a docker
        if(node instanceof BoundaryEvent) {
        	// find the edge associated with this boundary event
        	for (DiagramElement element: plane.getPlaneElement()) {
        		if(element instanceof BPMNEdge && ((BPMNEdge) element).getBpmnElement() == node) {
        			List<Point> waypoints = ((BPMNEdge) element).getWaypoint();
        			if(waypoints != null && waypoints.size() > 0) {
                		// one per boundary event
                		Point p = waypoints.get(0);
                		if(p != null) {
                			generator.writeArrayFieldStart("dockers");
                            generator.writeStartObject();
                            generator.writeObjectField("x", p.getX());
                            generator.writeObjectField("y", p.getY());
                            generator.writeEndObject();
                            generator.writeEndArray();
                		}
                	}
        		}
        	}
        }
        
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
    
    protected void marshallDataObject(DataObject dataObject, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
    	Map<String, Object> properties = new LinkedHashMap<String, Object>(flowElementProperties);
    	if(dataObject.getName() != null) {
    		properties.put("name", unescapeXML(dataObject.getName()));
    	} else {
    		properties.put("name", "");
    	}
		if(dataObject.getItemSubjectRef().getStructureRef() != null && dataObject.getItemSubjectRef().getStructureRef().length() > 0) {
			properties.put("type", dataObject.getItemSubjectRef().getStructureRef());
		}
		
		if(findOutgoingAssociation(plane, dataObject) != null) {
			properties.put("input_output", "Input");
		} else {
			properties.put("input_output", "Output");
		}
	    
		marshallProperties(properties, generator);
	    
		generator.writeObjectFieldStart("stencil");
	    generator.writeObjectField("id", "DataObject");
	    generator.writeEndObject();
	    generator.writeArrayFieldStart("childShapes");
	    generator.writeEndArray();
	    generator.writeArrayFieldStart("outgoing");
    	if(findOutgoingAssociation(plane, dataObject) != null) {
    		generator.writeStartObject();
    		generator.writeObjectField("resourceId", findOutgoingAssociation(plane, dataObject).getId());
    		generator.writeEndObject();
    	}
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
    
    protected void marshallSubProcess(SubProcess subProcess, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, String preProcessingData, Definitions def, Map<String, Object> flowElementProperties) throws JsonGenerationException, IOException {
    	Map<String, Object> properties = new LinkedHashMap<String, Object>(flowElementProperties);
		if(subProcess.getName() != null) {
			properties.put("name", unescapeXML(subProcess.getName()));
		} else {
			properties.put("name", "");
		}
		if(subProcess instanceof AdHocSubProcess) {
			AdHocSubProcess ahsp = (AdHocSubProcess) subProcess;
			if(ahsp.getOrdering().equals(AdHocOrdering.PARALLEL)) {
				properties.put("adhocordering", "Parallel");
			} else if(ahsp.getOrdering().equals(AdHocOrdering.SEQUENTIAL)) {
				properties.put("adhocordering", "Sequential");
			} else {
				// default to parallel
				properties.put("adhocordering", "Parallel");
			}
			if(ahsp.getCompletionCondition() != null) {
				properties.put("adhoccompletioncondition", ((FormalExpression) ahsp.getCompletionCondition()).getBody());
			}
		}
		
		// data inputs
        if(subProcess.getIoSpecification() != null) {
            List<InputSet> inputSetList = subProcess.getIoSpecification().getInputSets();
            StringBuilder dataInBuffer = new StringBuilder();
            for(InputSet inset : inputSetList) {
                List<DataInput> dataInputList =  inset.getDataInputRefs();
                for(DataInput dataIn : dataInputList) {
                	if(dataIn.getName() != null) {
                        dataInBuffer.append(dataIn.getName());
                        if(dataIn.getItemSubjectRef() != null && dataIn.getItemSubjectRef().getStructureRef() != null && dataIn.getItemSubjectRef().getStructureRef().length() > 0) {
                        	dataInBuffer.append(":").append(dataIn.getItemSubjectRef().getStructureRef());
                        }
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
        if(subProcess.getIoSpecification() != null) {
            List<OutputSet> outputSetList = subProcess.getIoSpecification().getOutputSets();
            StringBuilder dataOutBuffer = new StringBuilder();
            for(OutputSet outset : outputSetList) {
                List<DataOutput> dataOutputList =  outset.getDataOutputRefs();
                for(DataOutput dataOut : dataOutputList) {
                    dataOutBuffer.append(dataOut.getName());
                    if(dataOut.getItemSubjectRef() != null && dataOut.getItemSubjectRef().getStructureRef() != null && dataOut.getItemSubjectRef().getStructureRef().length() > 0) {
                    	dataOutBuffer.append(":").append(dataOut.getItemSubjectRef().getStructureRef());
                    }
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
        List<DataInputAssociation> inputAssociations = subProcess.getDataInputAssociations();
        List<DataOutputAssociation> outputAssociations = subProcess.getDataOutputAssociations();
        List<String> uniDirectionalAssociations = new ArrayList<String>();
        //List<String> biDirectionalAssociations = new ArrayList<String>();
        
        for(DataInputAssociation datain : inputAssociations) {
            String lhsAssociation = "";
            if(datain.getSourceRef() != null && datain.getSourceRef().size() > 0) {
            	if(datain.getTransformation() != null && datain.getTransformation().getBody() != null) {
            		lhsAssociation = datain.getTransformation().getBody();
            	} else {
            		lhsAssociation = datain.getSourceRef().get(0).getId();
            	}
            }
            
            String rhsAssociation = "";
            if(datain.getTargetRef() != null) {
                rhsAssociation = ((DataInput) datain.getTargetRef()).getName();
            }
            
            //boolean isBiDirectional = false;
            boolean isAssignment = false;
            
            if(datain.getAssignment() != null && datain.getAssignment().size() > 0) {
                isAssignment = true;
            } 
//            else {
//                // check if this is a bi-directional association
//                for(DataOutputAssociation dataout : outputAssociations) {
//                    if(dataout.getTargetRef().getId().equals(lhsAssociation) && 
//                       ((DataOutput) dataout.getSourceRef().get(0)).getName().equals(rhsAssociation)) {
//                        isBiDirectional = true;
//                        break;
//                    }
//                }
//            }
            
            if(isAssignment) {
            	// only know how to deal with formal expressions
            	if( datain.getAssignment().get(0).getFrom() instanceof FormalExpression) {
            		String associationValue = ((FormalExpression) datain.getAssignment().get(0).getFrom()).getBody();
            		if(associationValue == null) {
            			associationValue = "";
            		}
            		String replacer = associationValue.replaceAll(",", "##");
            		associationBuff.append(rhsAssociation).append("=").append(replacer);
                	associationBuff.append(",");
            	}
            } 
//            else if(isBiDirectional) {
//                associationBuff.append(lhsAssociation).append("<->").append(rhsAssociation);
//                associationBuff.append(",");
//                biDirectionalAssociations.add(lhsAssociation + "," + rhsAssociation);
//            } 
            else {
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
//                for(String bda : biDirectionalAssociations) {
//                    String[] dbaparts = bda.split( ",\\s*" );
//                    if(dbaparts[0].equals(rhsAssociation) && dbaparts[1].equals(lhsAssociation)) {
//                        wasBiDirectional = true;
//                        break;
//                    }
//                }
                
                if(dataout.getTransformation() != null && dataout.getTransformation().getBody() != null) {
                	rhsAssociation = dataout.getTransformation().getBody();
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
        
        // on-entry and on-exit actions
        if(subProcess.getExtensionValues() != null && subProcess.getExtensionValues().size() > 0) {
            
            String onEntryStr = "";
            String onExitStr = "";
            for(ExtensionAttributeValue extattrval : subProcess.getExtensionValues()) {
            
                FeatureMap extensionElements = extattrval.getValue();
        
                @SuppressWarnings("unchecked")
                List<OnEntryScriptType> onEntryExtensions = (List<OnEntryScriptType>) extensionElements
                                                     .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, true);
        
                @SuppressWarnings("unchecked")
                List<OnExitScriptType> onExitExtensions = (List<OnExitScriptType>) extensionElements
                                                  .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, true);
            
                for(OnEntryScriptType onEntryScript : onEntryExtensions) {
                    onEntryStr += onEntryScript.getScript();
                    onEntryStr += "|";
                
                    if(onEntryScript.getScriptFormat() != null) {
                        String format = onEntryScript.getScriptFormat();
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
                
                for(OnExitScriptType onExitScript : onExitExtensions) {
                    onExitStr += onExitScript.getScript();
                    onExitStr += "|";
                    
                    if(onExitScript.getScriptFormat() != null) {
                        String format = onExitScript.getScriptFormat();
                        String formatToWrite = "";
                        if(format.equals("http://www.java.com/java")) {
                            formatToWrite = "java";
                        } else if(format.equals("http://www.mvel.org/2.0")) {
                            formatToWrite = "mvel";
                        } else {
                            formatToWrite = "java";
                        }
                        if(properties.get("script_language") != null) {
                            properties.put("script_language", formatToWrite);
                        }
                    }
                }
            }
            if(onEntryStr.length() > 0) {
                if(onEntryStr.endsWith("|")) {
                    onEntryStr = onEntryStr.substring(0, onEntryStr.length() - 1);
                }
                properties.put("onentryactions", onEntryStr);
            }
            if(onExitStr.length() > 0) {
                if(onExitStr.endsWith("|")) {
                    onExitStr = onExitStr.substring(0, onExitStr.length() - 1);
                }
                properties.put("onexitactions", onExitStr);
            }
        }
        
        // loop characteristics
        boolean haveValidLoopCharacteristics = false;
        if(subProcess.getLoopCharacteristics() != null && subProcess.getLoopCharacteristics() instanceof MultiInstanceLoopCharacteristics) {
        	MultiInstanceLoopCharacteristics lc = (MultiInstanceLoopCharacteristics) subProcess.getLoopCharacteristics();
        	if(lc.getLoopDataInputRef() != null &&  lc.getInputDataItem() != null) {
        		properties.put("variablename", lc.getInputDataItem().getId());
        		if(subProcess.getDataInputAssociations() != null && 
        				subProcess.getDataInputAssociations().size() == 1) {
        			DataInputAssociation dia = subProcess.getDataInputAssociations().get(0);
        			if(dia.getSourceRef() != null) {
        				properties.put("collectionexpression", dia.getSourceRef().get(0).getId());
        				haveValidLoopCharacteristics = true;
        			}
        		}
        	}
        }
        
        // properties
        List<Property> processProperties = subProcess.getProperties();
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
            properties.put("vardefs", propVal);
        }
		
	    marshallProperties(properties, generator);
	    generator.writeObjectFieldStart("stencil");
	    if(subProcess instanceof AdHocSubProcess) {
	        generator.writeObjectField("id", "AdHocSubprocess");
	    } else {
	    	if(haveValidLoopCharacteristics) {
	    		generator.writeObjectField("id", "MultipleInstanceSubprocess");
	    	} else {
	    		generator.writeObjectField("id", "Subprocess");
	    	}
	    }
	    generator.writeEndObject();
	    generator.writeArrayFieldStart("childShapes");
	    Bounds bounds = ((BPMNShape) findDiagramElement(plane, subProcess)).getBounds();
	    for (FlowElement flowElement: subProcess.getFlowElements()) {
	    	// dont want to set the offset
	    	marshallFlowElement(flowElement, plane, generator, 0, 0, preProcessingData, def);
	    }
	    for (Artifact artifact: subProcess.getArtifacts()) {
	    	marshallArtifact(artifact, plane, generator, 0, 0, preProcessingData, def);
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
	    // subprocess boundary events
	    Process process = (Process) plane.getBpmnElement();
        for(FlowElement fe : process.getFlowElements()) {
        	if(fe instanceof BoundaryEvent) {
        		if(((BoundaryEvent) fe).getAttachedToRef().getId().equals(subProcess.getId())) {
        			generator.writeStartObject();
                    generator.writeObjectField("resourceId", fe.getId());
                    generator.writeEndObject();
        		}
        	}
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
    
    protected void marshallSequenceFlow(SequenceFlow sequenceFlow, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset) throws JsonGenerationException, IOException {
    	Map<String, Object> properties = new LinkedHashMap<String, Object>();
    	// check null for sequence flow name
    	if(sequenceFlow.getName() != null && !"".equals(sequenceFlow.getName())) {
    	    properties.put("name", unescapeXML(sequenceFlow.getName()));
    	} else {
    	    properties.put("name", "");
    	}
    	if(sequenceFlow.getDocumentation() != null && sequenceFlow.getDocumentation().size() > 0) {
            properties.put("documentation", sequenceFlow.getDocumentation().get(0).getText());
        }
    	
    	Expression conditionExpression = sequenceFlow.getConditionExpression();
    	if (conditionExpression instanceof FormalExpression) {
    	    if(((FormalExpression) conditionExpression).getBody() != null) {
    	        properties.put("conditionexpression", ((FormalExpression) conditionExpression).getBody());
    	    }
    	    if(((FormalExpression) conditionExpression).getLanguage() != null) {
    	        String cd = ((FormalExpression) conditionExpression).getLanguage();
    	        String cdStr = "";
    	        if(cd.equalsIgnoreCase("http://www.java.com/java")) {
    	            cdStr = "java";
    	        } else if(cd.equalsIgnoreCase("http://www.jboss.org/drools/rule")) {
    	            cdStr = "drools";
    	        } else if(cd.equalsIgnoreCase("http://www.mvel.org/2.0")) {
    	            cdStr = "mvel";
    	        } else {
    	            // default to mvel
    	            cdStr = "mvel";
    	        }
    	        properties.put("conditionexpressionlanguage", cdStr);
    	    } 
    	}

        boolean foundBgColor = false;
    	boolean foundBrColor = false;
    	boolean foundFontColor = false;
    	boolean foundSelectable = false;
    	Iterator<FeatureMap.Entry> iter = sequenceFlow.getAnyAttribute().iterator();
        while(iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if(entry.getEStructuralFeature().getName().equals("priority")) {
                String priorityStr = String.valueOf(entry.getValue());
                if(priorityStr != null) {
                    try {
                        Integer priorityInt = Integer.parseInt(priorityStr);
                        if(priorityInt >= 1) {
                            properties.put("priority", entry.getValue());
                        } else {
                            _logger.error("Priority must be equal or greater than 1.");
                        }
                    } catch (NumberFormatException e) {
                        _logger.error("Priority must be a number.");
                    }
                }
            }
            if(entry.getEStructuralFeature().getName().equals("bgcolor")) {
            	properties.put("bgcolor", entry.getValue());
            	foundBgColor = true;
            }
            if(entry.getEStructuralFeature().getName().equals("bordercolor")) {
            	properties.put("bordercolor", entry.getValue());
            	foundBrColor = true;
            }
            if(entry.getEStructuralFeature().getName().equals("fontsize")) {
            	properties.put("fontsize", entry.getValue());
            	foundBrColor = true;
            }
            if(entry.getEStructuralFeature().getName().equals("fontcolor")) {
            	properties.put("fontcolor", entry.getValue());
            	foundFontColor = true;
            }
            if(entry.getEStructuralFeature().getName().equals("selectable")) {
            	properties.put("isselectable", entry.getValue());
            	foundSelectable = true;
            }
        }
        if(!foundBgColor) {
        	properties.put("bgcolor", defaultSequenceflowColor);
        }
        
        if(!foundBrColor) {
        	properties.put("bordercolor", defaultSequenceflowColor);
        }
        
        if(!foundFontColor) {
        	properties.put("fontcolor", defaultSequenceflowColor);
        }
        
        if(!foundSelectable) {
        	properties.put("isselectable", "true");
        }
        
        // simulation properties
        if(sequenceFlow.getExtensionValues() != null && sequenceFlow.getExtensionValues().size() > 0) {
	        for(ExtensionAttributeValue extattrval : sequenceFlow.getExtensionValues()) {
	        	FeatureMap extensionElements = extattrval.getValue();
	            
                @SuppressWarnings("unchecked")
                List<MetadataType> metadataTypeExtensions = (List<MetadataType>) extensionElements
                                                     .get(DroolsPackage.Literals.DOCUMENT_ROOT__METADATA, true);
                if(metadataTypeExtensions != null && metadataTypeExtensions.size() > 0) {
                	MetadataType metaType = metadataTypeExtensions.get(0);
                	for(Object metaEntryObj : metaType.getMetaentry()) {
                		MetaentryType entry = (MetaentryType) metaEntryObj;
                		if(entry.getName() != null && entry.getName().equals("probability")) {
                			properties.put("probability", entry.getValue());
                		}
                	}
                }
                
	        }
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
    
    private DiagramElement findDiagramElement(BPMNPlane plane, BaseElement baseElement) {
    	DiagramElement result = _diagramElements.get(baseElement.getId());
    	if (result != null) {
    		return result;
    	}
    	for (DiagramElement element: plane.getPlaneElement()) {
        	if ((element instanceof BPMNEdge && ((BPMNEdge) element).getBpmnElement() == baseElement) ||
    			(element instanceof BPMNShape && ((BPMNShape) element).getBpmnElement() == baseElement)) {
        		_diagramElements.put(baseElement.getId(), element);
        		return element;
        	}
        }
    	_logger.info("Could not find BPMNDI information for " + baseElement);
    	return null;
    }
    
    protected void marshallGlobalTask(GlobalTask globalTask, JsonGenerator generator) {
        if (globalTask instanceof GlobalBusinessRuleTask) {
            
        } else if (globalTask instanceof GlobalManualTask) {
            
        } else if (globalTask instanceof GlobalScriptTask) {
            
        } else if (globalTask instanceof GlobalUserTask) {
            
        } else {
            
        }
    }

    protected void marshallGlobalChoreographyTask(GlobalChoreographyTask callableElement, JsonGenerator generator) {
        throw new UnsupportedOperationException("TODO"); //TODO!
    }

    protected void marshallConversation(Conversation callableElement, JsonGenerator generator) {
        throw new UnsupportedOperationException("TODO"); //TODO!
    }

    protected void marshallChoreography(Choreography callableElement, JsonGenerator generator) {
        throw new UnsupportedOperationException("TODO"); //TODO!
    }
    
    protected void marshallProperties(Map<String, Object> properties, JsonGenerator generator) throws JsonGenerationException, IOException {
        generator.writeObjectFieldStart("properties");
        for (Entry<String, Object> entry : properties.entrySet()) {
            generator.writeObjectField(entry.getKey(), String.valueOf(entry.getValue()));
        }
        generator.writeEndObject();
    }
    
    protected void marshallArtifact(Artifact artifact, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, String preProcessingData, Definitions def) throws IOException {
    	generator.writeStartObject();
    	generator.writeObjectField("resourceId", artifact.getId());
    	if (artifact instanceof Association) {
    		marshallAssociation((Association)artifact, plane, generator, xOffset, yOffset, preProcessingData, def);
    	} else if (artifact instanceof TextAnnotation) {
    		marshallTextAnnotation((TextAnnotation) artifact, plane, generator, xOffset, yOffset, preProcessingData, def);
    	} else if (artifact instanceof Group) {
    		marshallGroup((Group) artifact, plane, generator, xOffset, yOffset, preProcessingData, def);
    	}
    	generator.writeEndObject();
    }
    
    protected void marshallAssociation(Association association, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, String preProcessingData, Definitions def) throws JsonGenerationException, IOException {
    	Map<String, Object> properties = new LinkedHashMap<String, Object>();
        Iterator<FeatureMap.Entry> iter = association.getAnyAttribute().iterator();
        boolean foundBrColor = false;
        while(iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if(entry.getEStructuralFeature().getName().equals("type")) {
            	properties.put("type", entry.getValue());
            }
            if(entry.getEStructuralFeature().getName().equals("bordercolor")) {
            	properties.put("bordercolor", entry.getValue());
            	foundBrColor = true;
            }
        }
        if(!foundBrColor) {
        	properties.put("bordercolor", defaultSequenceflowColor);
        }
        
        marshallProperties(properties, generator);
        generator.writeObjectFieldStart("stencil");
        if(association.getAssociationDirection().equals(AssociationDirection.ONE)) {
        	generator.writeObjectField("id", "Association_Unidirectional");
        } else if(association.getAssociationDirection().equals(AssociationDirection.BOTH)) {
        	generator.writeObjectField("id", "Association_Bidirectional");
        } else {
        	generator.writeObjectField("id", "Association_Undirected");
        }
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();
        generator.writeArrayFieldStart("outgoing");
        generator.writeStartObject();
        generator.writeObjectField("resourceId", association.getTargetRef().getId());
        generator.writeEndObject();
        generator.writeEndArray();
        
        Bounds sourceBounds = ((BPMNShape) findDiagramElement(plane, association.getSourceRef())).getBounds();
        Bounds targetBounds = ((BPMNShape) findDiagramElement(plane, association.getTargetRef())).getBounds();
        generator.writeArrayFieldStart("dockers");
        generator.writeStartObject();
        generator.writeObjectField("x", sourceBounds.getWidth() / 2);
        generator.writeObjectField("y", sourceBounds.getHeight() / 2);
        generator.writeEndObject();
        List<Point> waypoints = ((BPMNEdge) findDiagramElement(plane, association)).getWaypoint();
        for (int i = 1; i < waypoints.size() - 1; i++) {
        	Point waypoint = waypoints.get(i);
            generator.writeStartObject();
            generator.writeObjectField("x", waypoint.getX());
            generator.writeObjectField("y", waypoint.getY());
            generator.writeEndObject();
        }
        generator.writeStartObject();
        generator.writeObjectField("x", 0); 
        if(sourceBounds.getY() < targetBounds.getY()) {
        	generator.writeObjectField("y", 0);
        } else {
        	generator.writeObjectField("y", targetBounds.getHeight());
        }
        generator.writeEndObject();
        generator.writeEndArray();
    }

    protected void marshallTextAnnotation(TextAnnotation textAnnotation, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, String preProcessingData, Definitions def)  throws JsonGenerationException, IOException{
    	Map<String, Object> properties = new LinkedHashMap<String, Object>();
    	properties.put("name", textAnnotation.getText());
    	properties.put("artifacttype", "Annotation");
    	
    	Iterator<FeatureMap.Entry> iter = textAnnotation.getAnyAttribute().iterator();
    	boolean foundBrColor = false;
    	boolean foundFontColor = false;
        while(iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if(entry.getEStructuralFeature().getName().equals("bordercolor")) {
            	properties.put("bordercolor", entry.getValue());
            	foundBrColor = true;
            }
            if(entry.getEStructuralFeature().getName().equals("fontsize")) {
            	properties.put("fontsize", entry.getValue());
            	foundBrColor = true;
            }
            if(entry.getEStructuralFeature().getName().equals("fontcolor")) {
            	properties.put("fontcolor", entry.getValue());
            	foundFontColor = true;
            }
        }
        
        if(!foundBrColor) {
        	properties.put("bordercolor", defaultBrColor);
        }
        
        if(!foundFontColor) {
        	properties.put("fontcolor", defaultFontColor);
        }
    	
	    marshallProperties(properties, generator);
        
        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id", "TextAnnotation");
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();
    
    	generator.writeArrayFieldStart("outgoing");
    	if(findOutgoingAssociation(plane, textAnnotation) != null) {
    		generator.writeStartObject();
    		generator.writeObjectField("resourceId", findOutgoingAssociation(plane, textAnnotation).getId());
    		generator.writeEndObject();
    	}
    	generator.writeEndArray();
    
    	Bounds bounds = ((BPMNShape) findDiagramElement(plane, textAnnotation)).getBounds();
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
    
    protected void marshallGroup(Group group, BPMNPlane plane, JsonGenerator generator, int xOffset, int yOffset, String preProcessingData, Definitions def)  throws JsonGenerationException, IOException{
    	Map<String, Object> properties = new LinkedHashMap<String, Object>();
    	if(group.getCategoryValueRef() != null && group.getCategoryValueRef().getValue() != null) {
    		properties.put("name", unescapeXML(group.getCategoryValueRef().getValue()));
    	}
    	
	    marshallProperties(properties, generator);
        
        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id", "Group");
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();
    
    	generator.writeArrayFieldStart("outgoing");
    	if(findOutgoingAssociation(plane, group) != null) {
    		generator.writeStartObject();
    		generator.writeObjectField("resourceId", findOutgoingAssociation(plane, group).getId());
    		generator.writeEndObject();
    	}
    	generator.writeEndArray();
    
    	Bounds bounds = ((BPMNShape) findDiagramElement(plane, group)).getBounds();
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
    
    protected Association findOutgoingAssociation(BPMNPlane plane, BaseElement baseElement) {
    	Association result = _diagramAssociations.get(baseElement.getId());
    	if (result != null) {
    		return result;
    	}
        if (!(plane.getBpmnElement() instanceof Process)){
            throw new IllegalArgumentException("Don't know how to get associations from a non-Process Diagram");
        }
        
        Process process = (Process) plane.getBpmnElement();
        for (Artifact artifact : process.getArtifacts()) {
            if (artifact instanceof Association){
                Association association = (Association) artifact;
                if (association.getSourceRef() == baseElement){
                    _diagramAssociations.put(baseElement.getId(), association);
                    return association;
                }
            }
        }
        return null;
    }
    
    protected void marshallStencil(String stencilId, JsonGenerator generator) throws JsonGenerationException, IOException {
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
    
	private static String unescapeXML(String str) {
		if (str == null || str.length() == 0)
			return "";

		StringBuffer buf = new StringBuffer();
		int len = str.length();
		for (int i = 0; i < len; ++i) {
			char c = str.charAt(i);
			if (c == '&') {
				int pos = str.indexOf(";", i);
				if (pos == -1) { // Really evil
					buf.append('&');
				} else if (str.charAt(i + 1) == '#') {
					int val = Integer.parseInt(str.substring(i + 2, pos), 16);
					buf.append((char) val);
					i = pos;
				} else {
					String substr = str.substring(i, pos + 1);
					if (substr.equals("&amp;"))
						buf.append('&');
					else if (substr.equals("&lt;"))
						buf.append('<');
					else if (substr.equals("&gt;"))
						buf.append('>');
					else if (substr.equals("&quot;"))
						buf.append('"');
					else if (substr.equals("&apos;"))
						buf.append('\'');
					else
						// ????
						buf.append(substr);
					i = pos;
				}
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}
}
