/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.bpmn2.validation;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bpsim.*;
import bpsim.impl.BpsimFactoryImpl;
import org.drools.core.xml.SemanticModules;
import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BPMN2SyntaxChecker implements SyntaxChecker {
    public static final String BPMN2_TYPE = "BPMN2";
    public static final String SIMULATION_TYPE = "Simulation";
    public static final String PROCESS_TYPE = "Process";
    private static final Logger _logger = LoggerFactory.getLogger(BPMN2SyntaxChecker.class);

	protected Map<String, List<ValidationSyntaxError>> errors = new HashMap<String, List<ValidationSyntaxError>>();
	private String json;
	private String preprocessingData;
	private IDiagramProfile profile;
	private String defaultResourceId = "processerrors";
	
	public BPMN2SyntaxChecker(String json, String preprocessingData, IDiagramProfile profile) {
		this.json = json;
		this.preprocessingData = preprocessingData;
		this.profile = profile;
	}
	
	public void checkSyntax() {
		DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();

        try {
            Definitions def = profile.createMarshaller().getDefinitions(json, preprocessingData);
            List<RootElement> rootElements =  def.getRootElements();
            Scenario defaultScenario = getDefaultScenario(def);

            for(RootElement root : rootElements) {
                if(root instanceof Process) {
                    Process process = (Process) root;
                    if(process.getFlowElements() != null && process.getFlowElements().size() > 0) {
                        defaultResourceId = process.getFlowElements().get(0).getId();
                    }

                    if(isEmpty(process.getId())) {
                        addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE, "Process has no id."));
                    } else {
                        if(!SyntaxCheckerUtils.isNCName(process.getId())) {
                            addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE,  "Invalid process id. See http://www.w3.org/TR/REC-xml-names/#NT-NCName for more info."));
                        }
                    }

                    String pname;
                    Iterator<FeatureMap.Entry> iter = process.getAnyAttribute().iterator();
                    boolean foundPackageName = false;
                    while(iter.hasNext()) {
                        FeatureMap.Entry entry = iter.next();
                        if(entry.getEStructuralFeature().getName().equals("packageName")) {
                            foundPackageName = true;
                            pname = (String) entry.getValue();
                            if(isEmpty(pname)) {
                                addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE,  "Process has no package name."));
                            }
                            if(!isValidPackageName(pname)) {
                                addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE,  "Package name contains invalid characters."));
                            }
                        }
                    }
                    if(!foundPackageName) {
                        addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE,  "Process has no package name."));
                    }

                    if(isEmpty(process.getName())) {
                        addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE,  "Process has no name."));
                    }

                    List<Property> processProperties = process.getProperties();
                    if(processProperties != null && processProperties.size() > 0) {
                        for(Property prop : processProperties) {
                            String propId = prop.getId();
                            Pattern pattern = Pattern.compile("\\s");
                            Matcher matcher = pattern.matcher(propId);
                            if(matcher.find()) {
                                addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE,  "Process variable \"" + propId + "\" contains white spaces."));
                            }
                        }
                    }

                    boolean foundStartEvent = false;
                    boolean foundEndEvent = false;
                    List<FlowElement> flowElements =  process.getFlowElements();
                    for(FlowElement fe : flowElements) {
                        if(fe instanceof StartEvent) {
                            foundStartEvent = true;
                        }
                        if(fe instanceof EndEvent) {
                            foundEndEvent = true;
                        }
                    }
                    if(!foundStartEvent && !isAdHocProcess(process)) {
                        addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE,  "Process has no start node."));
                    }
                    if(!foundEndEvent && !isAdHocProcess(process)) {
                        addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE, "Process has no end node."));
                    }

                    checkFlowElements(process, process, defaultScenario);
                }
            }
        } catch(Exception e) {
            addError(defaultResourceId, new ValidationSyntaxError(null, PROCESS_TYPE, "Could not parse BPMN2 process."));
        }

        // if there are no suggestions add RuleFlowProcessValidator process errors
        if(this.errors.size() < 1) {
            try {
                SemanticModules modules = new SemanticModules();
                modules.addSemanticModule(new BPMNSemanticModule());
                modules.addSemanticModule(new BPMNDISemanticModule());
                XmlProcessReader xmlReader = new XmlProcessReader(modules, getClass().getClassLoader());
                List<org.kie.api.definition.process.Process> processes = xmlReader.read(new StringReader(profile.createMarshaller().parseModel(json, preprocessingData)));
                if(processes != null) {
                    ProcessValidationError[] errors = RuleFlowProcessValidator.getInstance().validateProcess((org.jbpm.ruleflow.core.RuleFlowProcess) processes.get(0));
                    for(ProcessValidationError er : errors) {
                        addError(defaultResourceId, new ValidationSyntaxError(null, PROCESS_TYPE, er.getMessage()));
                    }
                }
            } catch(Exception e) {
                _logger.warn("Could not parse to RuleFlowProcess.");
                addError(defaultResourceId, new ValidationSyntaxError(null, PROCESS_TYPE, "Could not parse BPMN2 to RuleFlowProcess."));
            }
        }

    }
	
	private void checkFlowElements(FlowElementsContainer container, Process process, Scenario defaultScenario) {
		
		for(FlowElement fe : container.getFlowElements()) {
			if(fe instanceof StartEvent) {
				StartEvent se = (StartEvent) fe;
				if(se.getOutgoing() == null || se.getOutgoing().size() < 1) {
                    if(container instanceof Process) {
                        if(!isAdHocProcess(process)) {
                            addError(se, new ValidationSyntaxError(se, BPMN2_TYPE,  "Start node has no outgoing connections"));
                        }
                    } else if(container instanceof SubProcess) {
                        if(!(container instanceof AdHocSubProcess)) {
                            addError(se, new ValidationSyntaxError(se, BPMN2_TYPE,  "Start node has no outgoing connections"));
                        }
                    } else {
                        addError(se, new ValidationSyntaxError(se, BPMN2_TYPE,  "Start node has no outgoing connections"));
                    }
				}
			} else if (fe instanceof EndEvent) {
				EndEvent ee = (EndEvent) fe;
				if(ee.getIncoming() == null || ee.getIncoming().size() < 1) {
                    if(container instanceof Process) {
                        if(!isAdHocProcess(process)) {
                            addError(ee, new ValidationSyntaxError(ee, BPMN2_TYPE,  "End node has no incoming connections"));
                        }
                    } else if(container instanceof SubProcess) {
                        if(!(container instanceof AdHocSubProcess)) {
                            addError(ee, new ValidationSyntaxError(ee, BPMN2_TYPE,  "End node has no incoming connections"));
                        }
                    } else {
                        addError(ee, new ValidationSyntaxError(ee, BPMN2_TYPE,  "End node has no incoming connections"));
                    }
				}
			} else {
				if(fe instanceof FlowNode) {
					FlowNode fn = (FlowNode) fe;
					if((fn.getOutgoing() == null || fn.getOutgoing().size() < 1) && !isAdHocProcess(process) && !(fn instanceof BoundaryEvent) && !(fn instanceof EventSubprocess)) {
                        if(container instanceof Process) {
                            if(!isAdHocProcess(process)) {
                                if(!isCompensatingFlowNodeInProcess(fn, (Process) container)) {
                                    addError(fn, new ValidationSyntaxError(fn, BPMN2_TYPE, "Node has no outgoing connections"));
                                }
                            }
                        } else if(container instanceof SubProcess) {
                            if(!(container instanceof AdHocSubProcess)) {
                                if(!isCompensatingFlowNodeInSubprocess(fn, (SubProcess) container)) {
                                    addError(fn, new ValidationSyntaxError(fn, BPMN2_TYPE, "Node has no outgoing connections"));
                                }
                            }
                        } else {
                            addError(fn, new ValidationSyntaxError(fn, BPMN2_TYPE, "Node has no outgoing connections"));
                        }
    				}
                    if(!(fn instanceof BoundaryEvent)) {
                        if((fn.getIncoming() == null || fn.getIncoming().size() < 1) && !isAdHocProcess(process)) {
                            if(container instanceof Process) {
                                if(!isAdHocProcess(process) && !(fn instanceof EventSubprocess)) {
                                    if(!isCompensatingFlowNodeInProcess(fn, (Process) container)) {
                                        addError(fn, new ValidationSyntaxError(fn, BPMN2_TYPE, "Node has no incoming connections"));
                                    }
                                }
                            } else if(container instanceof SubProcess) {
                                if(!(container instanceof AdHocSubProcess)) {
                                    if(!isCompensatingFlowNodeInSubprocess(fn, (SubProcess) container)) {
                                        addError(fn, new ValidationSyntaxError(fn, BPMN2_TYPE, "Node has no incoming connections"));
                                    }
                                }
                            } else {
                                addError(fn, new ValidationSyntaxError(fn, BPMN2_TYPE, "Node has no incoming connections"));
                            }
                        }
                    }
				}
			}
			
			if(fe instanceof BusinessRuleTask) {
				BusinessRuleTask bt = (BusinessRuleTask) fe;
				Iterator<FeatureMap.Entry> biter = bt.getAnyAttribute().iterator();
				boolean foundRuleflowGroup = false;
	            while(biter.hasNext()) {
	                FeatureMap.Entry entry = biter.next();
	                if(entry.getEStructuralFeature().getName().equals("ruleFlowGroup")) {
	                	foundRuleflowGroup = true;
	                	String ruleflowGroup = (String) entry.getValue();
	                	if(isEmpty(ruleflowGroup)) {
	                		addError(bt, new ValidationSyntaxError(bt, BPMN2_TYPE, "Business Rule Task has no ruleflow-group."));
	                	}
	                }
	            }
	            if(!foundRuleflowGroup) {
	            	addError(bt, new ValidationSyntaxError(bt, BPMN2_TYPE, "Business Rule Task has no ruleflow-group."));
	            }
			}
			
			if(fe instanceof ScriptTask) {
				ScriptTask st = (ScriptTask) fe;
				if(isEmpty(st.getScript())) {
					addError(st, new ValidationSyntaxError(st, BPMN2_TYPE, "Script Task has no script."));
				}
				if(isEmpty(st.getScriptFormat())) {
					addError(st, new ValidationSyntaxError(st, BPMN2_TYPE, "Script Task has no script format."));
				}
			}
			
			if(fe instanceof SendTask) {
				SendTask st = (SendTask) fe;
				if(st.getMessageRef() == null) {
					addError(st, new ValidationSyntaxError(st, BPMN2_TYPE, "Send Task has no message."));
				}
			}

            if(fe instanceof ServiceTask) {
                ServiceTask st = (ServiceTask) fe;
                if(st.getOperationRef() == null) {
                    addError(st, new ValidationSyntaxError(st, BPMN2_TYPE, "Service Task has no operation."));
                }
            }
			
			if(fe instanceof UserTask) {
				UserTask ut = (UserTask) fe;
				String taskName = null;
				Iterator<FeatureMap.Entry> utiter = ut.getAnyAttribute().iterator();
				boolean foundTaskName = false;


                if(ut.getIoSpecification() != null && ut.getIoSpecification().getDataInputs() != null) {
                    List<DataInput> taskDataInputs = ut.getIoSpecification().getDataInputs();
                    for(DataInput din : taskDataInputs) {
                        if(din.getName() != null && din.getName().equals("TaskName")) {
                            List<DataInputAssociation> taskDataInputAssociations = ut.getDataInputAssociations();
                            for(DataInputAssociation dia : taskDataInputAssociations) {
                                if(dia.getTargetRef().getId() != null && (dia.getTargetRef().getId().equals(din.getId()))) {
                                    foundTaskName = true;
                                    taskName = ((FormalExpression) dia.getAssignment().get(0).getFrom()).getBody();
                                    if(isEmpty(taskName)) {
                                        addError(ut, new ValidationSyntaxError(ut, BPMN2_TYPE, "User Task has no task name."));
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
		        if(!foundTaskName) {
		        	addError(ut, new ValidationSyntaxError(ut, BPMN2_TYPE, "User Task has no task name."));
		        }
		        
		        // simulation validation
		        if(defaultScenario != null && defaultScenario.getElementParameters() != null) {
		        	for(ElementParameters eleType : defaultScenario.getElementParameters()) {
		        		if(eleType.getElementRef().equals(ut.getId())) {
		        			if(eleType.getResourceParameters() != null) {
	        					ResourceParameters resourceParams = eleType.getResourceParameters();
	        					if(resourceParams.getQuantity() != null) {
	        						FloatingParameterType quantityVal = (FloatingParameterType) resourceParams.getQuantity().getParameterValue().get(0);
	        						double val = quantityVal.getValue();
	        						if(val < 0) {
	        							addError(ut, new ValidationSyntaxError(ut, SIMULATION_TYPE, "Staff Availability value must be positive."));
	        						}
	        					}
	        				}
		        		}
		        	}
		        }
		    }
			
			if(fe instanceof Task) {
				Task ta = (Task) fe;
				
				// simulation validation
				if(defaultScenario != null && defaultScenario.getElementParameters() != null) {
					for(ElementParameters eleType : defaultScenario.getElementParameters()) {
						if(eleType.getElementRef().equals(ta.getId())) {
	        				if(eleType.getCostParameters() != null) {
	        					CostParameters costParams = eleType.getCostParameters();
	        					if(costParams.getUnitCost() != null) {
	        						FloatingParameterType unitCostVal = (FloatingParameterType) costParams.getUnitCost().getParameterValue().get(0);
	        						Double val = unitCostVal.getValue();
	        						if(val.doubleValue() < 0) {
	        							addError(ta, new ValidationSyntaxError(ta, SIMULATION_TYPE, "Cost per Time Unit value must be positive."));
	        						}
	        					}
	        				}
	        				if(eleType.getResourceParameters() != null) {
	        					ResourceParameters resourceParams = eleType.getResourceParameters();
	        					if(resourceParams.getQuantity() != null) {
	        						FloatingParameterType workingHoursVal = (FloatingParameterType) resourceParams.getQuantity().getParameterValue().get(0);
	        						if(workingHoursVal.getValue() < 0) {
	        							addError(ta, new ValidationSyntaxError(ta, SIMULATION_TYPE, "Working Hours value must be positive."));
	        						}
	        					}
	        				}
	        			}
					}
				}
			}
			
			if(fe instanceof CatchEvent) {
				CatchEvent event = (CatchEvent) fe;
				List<EventDefinition> eventdefs = event.getEventDefinitions();
				for(EventDefinition ed : eventdefs) {
    				if(ed instanceof TimerEventDefinition) {
    	                TimerEventDefinition ted = (TimerEventDefinition) ed;
                        if(ted.getTimeDate() != null && ted.getTimeDuration() != null && ted.getTimeCycle() != null) {
                            addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Even has timeDate and timeDuration and timeCycle defined."));
                        } else if(ted.getTimeDate() != null && ted.getTimeDuration() != null) {
                            addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Even has both timeDate and timeDuration defined."));
                        } else if(ted.getTimeDate() != null && ted.getTimeCycle() != null) {
                            addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Even has both timeDate and timeCycle defined."));
                        } else if(ted.getTimeDuration() != null && ted.getTimeCycle() != null) {
                            addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Even has both timeduration and timecycle defined."));
                        }

                        if(ted.getTimeDate() == null && ted.getTimeDuration() == null && ted.getTimeCycle() == null) {
                            addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Even has no timeDate or timeDuration or timeCycle defined."));
                        }
    	            } else if( ed instanceof SignalEventDefinition) {
    	                if(((SignalEventDefinition) ed).getSignalRef() == null) {
    	                	addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Event has no signalref."));
    	                }
    	            } else if( ed instanceof ErrorEventDefinition) {
    	                if(((ErrorEventDefinition) ed).getErrorRef() == null || ((ErrorEventDefinition) ed).getErrorRef().getErrorCode() == null) {
    	                	addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Event has no errorref."));
    	                }
    	            } else if( ed instanceof ConditionalEventDefinition ) {
    	                FormalExpression conditionalExp = (FormalExpression) ((ConditionalEventDefinition) ed).getCondition();
    	                if(conditionalExp.getBody() == null) {
    	                	addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Event has no conditionexpression."));
    	                }
    	            } else if( ed instanceof EscalationEventDefinition ) {
    	                if(((EscalationEventDefinition) ed).getEscalationRef() == null) {
    	                	addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Event has no escalationref."));
    	                }
    	            } else if( ed instanceof MessageEventDefinition) {
    	                if(((MessageEventDefinition) ed).getMessageRef() == null) {
    	                    addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Event has no messageref."));
    	                }
    	            }
				}
			}
			
			if(fe instanceof ThrowEvent) {
				ThrowEvent event = (ThrowEvent) fe;
				List<EventDefinition> eventdefs = event.getEventDefinitions();
		        for(EventDefinition ed : eventdefs) {
		            if(ed instanceof TimerEventDefinition) {
                        TimerEventDefinition ted = (TimerEventDefinition) ed;
                        if(ted.getTimeDate() != null && ted.getTimeDuration() != null && ted.getTimeCycle() != null) {
                            addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Even has timeDate and timeDuration and timeCycle defined."));
                        } else if(ted.getTimeDate() != null && ted.getTimeDuration() != null) {
                            addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Even has both timeDate and timeDuration defined."));
                        } else if(ted.getTimeDate() != null && ted.getTimeCycle() != null) {
                            addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Even has both timeDate and timeCycle defined."));
                        } else if(ted.getTimeDuration() != null && ted.getTimeCycle() != null) {
                            addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Even has both timeduration and timecycle defined."));
                        }

                        if(ted.getTimeDate() == null && ted.getTimeDuration() == null && ted.getTimeCycle() == null) {
                            addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Catch Even has no timeDate or timeDuration or timeCycle defined."));
                        }
		            } else if( ed instanceof SignalEventDefinition) {
		                if(((SignalEventDefinition) ed).getSignalRef() == null) {
		                	addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Throw Event has no signalref."));
		                }
		            } else if( ed instanceof ErrorEventDefinition) {
		                if(((ErrorEventDefinition) ed).getErrorRef() == null || ((ErrorEventDefinition) ed).getErrorRef().getErrorCode() == null) {
		                	addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Throw Event has no errorref."));
		                }
		            } else if( ed instanceof ConditionalEventDefinition ) {
		                FormalExpression conditionalExp = (FormalExpression) ((ConditionalEventDefinition) ed).getCondition();
		                if(conditionalExp.getBody() == null) {
		                	addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Throw Event has no conditional expression."));
		                }
		            } else if( ed instanceof EscalationEventDefinition ) {
		                if(((EscalationEventDefinition) ed).getEscalationRef() == null) {
		                	addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Throw Event has no conditional escalationref."));
		                }
		            } else if( ed instanceof MessageEventDefinition) {
		                if(((MessageEventDefinition) ed).getMessageRef() == null) {
		                	addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Throw Event has no conditional messageref."));
		                }
		            }  else if( ed instanceof CompensateEventDefinition) {
		                if(((CompensateEventDefinition) ed).getActivityRef() == null) {
		                	addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, "Throw Event has no conditional activityref."));
		                }
		            }  
		        }
			}
			
			if(fe instanceof SequenceFlow) {
				SequenceFlow sf = (SequenceFlow) fe;
				if(sf.getSourceRef() == null) {
					addError((SequenceFlow) fe, new ValidationSyntaxError(fe, BPMN2_TYPE, "An Edge must have a source node."));
				}
				if(sf.getTargetRef() == null) {
					addError((SequenceFlow) fe, new ValidationSyntaxError(fe, BPMN2_TYPE, "An Edge must have a target node."));
				}
			}
			
			if(fe instanceof Gateway) {
				Gateway gw = (Gateway) fe;
				if(gw.getGatewayDirection() == null || gw.getGatewayDirection().getValue() == GatewayDirection.UNSPECIFIED.getValue()) {
					addError((Gateway) fe, new ValidationSyntaxError(fe, BPMN2_TYPE, "Gateway does not specify a valid direction."));
				}
				if(gw instanceof ExclusiveGateway) {
					if(gw.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue() && gw.getGatewayDirection().getValue() != GatewayDirection.CONVERGING.getValue()) {
						addError(fe, new ValidationSyntaxError(fe, BPMN2_TYPE, "Invalid Gateway direction for Exclusing Gateway. It should be 'Converging' or 'Diverging'."));
					}
				}
				if(gw instanceof EventBasedGateway) {
					if(gw.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue()) {
						addError(fe, new ValidationSyntaxError(fe, BPMN2_TYPE, "Invalid Gateway direction for EventBased Gateway. It should be 'Diverging'."));
					}
				}
				if(gw instanceof ParallelGateway) {
					if(gw.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue() && gw.getGatewayDirection().getValue() != GatewayDirection.CONVERGING.getValue()) {
						addError(fe, new ValidationSyntaxError(fe, BPMN2_TYPE, "Invalid Gateway direction for Parallel Gateway. It should be 'Converging' or 'Diverging'."));
					}
				}
//				if(gw instanceof InclusiveGateway) {
//					if(gw.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue()) {
//						addError(fe, new ValidationSyntaxError(fe, BPMN2_TYPE, "Invalid Gateway direction for Inclusive Gateway. It should be 'Diverging'."));
//					}
//				}
				if(gw instanceof ComplexGateway) {
					if(gw.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue() && gw.getGatewayDirection().getValue() != GatewayDirection.CONVERGING.getValue()) {
						addError(fe, new ValidationSyntaxError(fe, BPMN2_TYPE, "Invalid Gateway direction for Complex Gateway. It should be 'Converging' or 'Diverging'."));
					}
				}

                if( (gw instanceof ExclusiveGateway || gw instanceof InclusiveGateway) && (gw.getGatewayDirection().getValue() == GatewayDirection.DIVERGING.getValue())) {
                    List<SequenceFlow> outgoingFlows = gw.getOutgoing();
                    if(outgoingFlows != null && outgoingFlows.size() > 0) {
                        for(SequenceFlow flow : outgoingFlows) {
                            if(flow.getConditionExpression() == null) {
                                addError(flow, new ValidationSyntaxError(flow, BPMN2_TYPE, "Sequence flow has no condition expression defined."));
                            } else {
                                if(flow.getConditionExpression() instanceof FormalExpression) {
                                    FormalExpression formalExp = (FormalExpression) flow.getConditionExpression();
                                    if(formalExp.getBody() == null && formalExp.getBody().length() < 1) {
                                        addError(flow, new ValidationSyntaxError(flow, BPMN2_TYPE, "Sequence flow has no condition expression defined."));
                                    }
                                } else {
                                    addError(flow, new ValidationSyntaxError(flow, BPMN2_TYPE, "Invalid condition expression on sequence flow."));
                                }
                            }
                        }
                    }
                }

				// simulation validation
				if(!(gw instanceof ParallelGateway)) {
					List<SequenceFlow> outgoingGwSequenceFlows = gw.getOutgoing();
					if(outgoingGwSequenceFlows != null && outgoingGwSequenceFlows.size() > 0) {
						double sum = 0;
						for(SequenceFlow sf : outgoingGwSequenceFlows) {
					        	if(defaultScenario.getElementParameters() != null) {
					        		for(ElementParameters eleType : defaultScenario.getElementParameters()) {
					        			if(eleType.getElementRef().equals(sf.getId())) {
					        				if(eleType.getControlParameters() != null && eleType.getControlParameters().getProbability() != null) {
					        					FloatingParameterType valType = (FloatingParameterType) eleType.getControlParameters().getProbability().getParameterValue().get(0);
				                    			if(valType.getValue() < 0) {
				                    				addError(sf, new ValidationSyntaxError(sf, SIMULATION_TYPE, "Probability value must be positive."));
				                    			} else {
				                    				sum += valType.getValue();
				                    			}
					        				} else {
					        					addError(sf, new ValidationSyntaxError(sf, SIMULATION_TYPE, "Sequence Flow has no probability defined."));
					        				}
					        			}
					        		}
					        	}
						}
						if(sum != 100) {
							addError(gw, new ValidationSyntaxError(gw, SIMULATION_TYPE, "The sum of probability values of all outgoing Sequence Flows must be equal 100."));
						}
					}
				}
			}
			
			if(fe instanceof CallActivity) {
				CallActivity ca = (CallActivity) fe;
				if(ca.getCalledElement() == null || ca.getCalledElement().length() < 1) {
					addError((CallActivity) fe, new ValidationSyntaxError(fe, BPMN2_TYPE, "Reusable Subprocess has no called element specified."));
				}
			}
			
			if(fe instanceof DataObject) {
				DataObject dao = (DataObject) fe;
				if(dao.getName() == null || dao.getName().length() < 1) {
					addError((DataObject) fe, new ValidationSyntaxError(fe, BPMN2_TYPE, "Data Object has no name defined."));
				} else {
					if(containsWhiteSpace(dao.getName())) {
						addError((DataObject) fe, new ValidationSyntaxError(fe, BPMN2_TYPE, "Data Object name contains white spaces."));
					}
				}
			}
			
			if(fe instanceof SubProcess) {
				checkFlowElements((SubProcess) fe, process, defaultScenario);
			}
		}
	}

	public Map<String, List<ValidationSyntaxError>> getErrors() {
		return errors;
	}

	public JSONObject getErrorsAsJson() {
		JSONObject jsonObject = new JSONObject();
		for (Entry<String, List<ValidationSyntaxError>> error: this.getErrors().entrySet()) {
			try {
                JSONArray errorsArray = new JSONArray();
                for(ValidationSyntaxError se : error.getValue()) {
                    errorsArray.put(se.toJSON());
                }
				jsonObject.put(error.getKey(), errorsArray);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return jsonObject;
	}

	public boolean errorsFound() {
		return errors.size() > 0;
	}

	public void clearErrors() {
		errors.clear();
	}
	
	private void addError(BaseElement element, ValidationSyntaxError error) {
		addError(element.getId(), error);
	}
	
	private void addError(String resourceId, ValidationSyntaxError error) {
		if(errors.containsKey(resourceId) && errors.get(resourceId) != null) {
			errors.get(resourceId).add(error);
		} else {
			List<ValidationSyntaxError> value = new ArrayList<ValidationSyntaxError>();
			value.add(error);
			errors.put(resourceId, value);
		}
	}
	
	private static boolean isEmpty(final CharSequence str) {
		if ( str == null || str.length() == 0 ) {
			return true;
	    }
	    for ( int i = 0, length = str.length(); i < length; i++ ) {
	    	if ( str.charAt( i ) != ' ' ) {
	    		return false;
	        }
	    }
	    return true;
	}
	
    
    private boolean isAdHocProcess(Process process) {
        Iterator<FeatureMap.Entry> iter = process.getAnyAttribute().iterator();
        while(iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if(entry.getEStructuralFeature().getName().equals("adHoc")) {
            	return Boolean.parseBoolean(((String)entry.getValue()).trim());
            }
        }
        return false;
    }

    public boolean isCompensatingFlowNodeInSubprocess(FlowNode node, SubProcess subProcess ) {
		//text annotations are flow elements now not artifacts so omit them
		if(node instanceof TextAnnotation) {
			return true;
		}

        for(Artifact artifact : subProcess.getArtifacts()) {
            if (artifact instanceof Association){
                Association association = (Association) artifact;
                if (association.getTargetRef().getId().equals(node.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

	public boolean isCompensatingFlowNodeInProcess(FlowNode node, Process process ) {
        for(Artifact artifact : process.getArtifacts()) {
            if (artifact instanceof Association){
                Association association = (Association) artifact;
                if (association.getTargetRef().getId().equals(node.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean containsWhiteSpace(String testString){
        if(testString != null){
            for(int i = 0; i < testString.length(); i++){
                if(Character.isWhitespace(testString.charAt(i))){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isValidPackageName(String pkg) {
        Pattern p = Pattern.compile("^[a-zA-Z_\\$][\\w\\$]*(?:\\.[a-zA-Z_\\$][\\w\\$]*)*$");
        return p.matcher(pkg).matches();
    }
    
    private Scenario getDefaultScenario(Definitions def) {
    	if(def.getRelationships() != null && def.getRelationships().size() > 0) {
        	// current support for single relationship
        	Relationship relationship = def.getRelationships().get(0);
        	for(ExtensionAttributeValue extattrval : relationship.getExtensionValues()) {
                FeatureMap extensionElements = extattrval.getValue();
                @SuppressWarnings("unchecked")
                List<BPSimDataType> bpsimExtensions = (List<BPSimDataType>) extensionElements.get(BpsimPackage.Literals.DOCUMENT_ROOT__BP_SIM_DATA, true);
                if(bpsimExtensions != null && bpsimExtensions.size() > 0) {
                    BPSimDataType processAnalysis = bpsimExtensions.get(0);
                	if(processAnalysis.getScenario() != null && processAnalysis.getScenario().size() > 0) {
                		return processAnalysis.getScenario().get(0);
                	}
                }
        	}
        }
    	return null;
    }

    public class ValidationSyntaxError {
        private BaseElement element;
        private String error;
        private String type;

        public ValidationSyntaxError(BaseElement element, String type, String error) {
            this.element = element;
            this.error = error;
            this.type = type;
        }

        public JSONObject toJSON() throws JSONException {
            JSONObject errorJSON = new JSONObject();
            errorJSON.put("id", this.element == null ? "" : this.element.getId());
            errorJSON.put("type", type);
            errorJSON.put("error", this.error);

            return errorJSON;
        }

        public BaseElement getElement() {
            return this.element;
        }

        public String getError() {
            return this.error;
        }
    }
}
