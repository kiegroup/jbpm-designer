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
                        addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE, SyntaxCheckerErrors.PROCESS_HAS_NO_ID));
                    } else {
                        if(!SyntaxCheckerUtils.isNCName(process.getId())) {
                            addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE, SyntaxCheckerErrors.INVALID_PROCESS_ID));
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
                                addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE, SyntaxCheckerErrors.PROCESS_HAS_NO_PACKAGE_NAME));
                            }
                            if(!isValidPackageName(pname)) {
                                addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE, SyntaxCheckerErrors.PACKAGE_NAME_CONTAINS_INVALID_CHARACTERS));
                            }
                        }
                    }
                    if(!foundPackageName) {
                        addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE, SyntaxCheckerErrors.PROCESS_HAS_NO_PACKAGE_NAME));
                    }

                    if(isEmpty(process.getName())) {
                        addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE, SyntaxCheckerErrors.PROCESS_HAS_NO_NAME));
                    }

                    List<Property> processProperties = process.getProperties();
                    if(processProperties != null && processProperties.size() > 0) {
                        for(Property prop : processProperties) {
                            String propId = prop.getId();
                            Pattern pattern = Pattern.compile("\\s");
                            Matcher matcher = pattern.matcher(propId);
                            if(matcher.find()) {
                                addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE, SyntaxCheckerErrors.processVariableContainsWhiteSpaces(propId)));
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
                        addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE, SyntaxCheckerErrors.PROCESS_HAS_NO_START_NODE));
                    }
                    if(!foundEndEvent && !isAdHocProcess(process)) {
                        addError(defaultResourceId, new ValidationSyntaxError(process, BPMN2_TYPE, SyntaxCheckerErrors.PROCESS_HAS_NO_END_NODE));
                    }

                    checkFlowElements(process, process, defaultScenario);
                }
            }
        } catch(Exception e) {
            addError(defaultResourceId, new ValidationSyntaxError(null, PROCESS_TYPE, SyntaxCheckerErrors.COULD_NOT_PARSE_BPMN2_PROCESS));
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
                addError(defaultResourceId, new ValidationSyntaxError(null, PROCESS_TYPE, SyntaxCheckerErrors.COULD_NOT_PARSE_BPMN2_TO_RULE_FLOW_PROCESS));
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
                            addError(se, new ValidationSyntaxError(se, BPMN2_TYPE, SyntaxCheckerErrors.START_NODE_NO_OUTGOING_CONNECTIONS));
                        }
                    } else if(container instanceof SubProcess) {
                        if(!(container instanceof AdHocSubProcess)) {
                            addError(se, new ValidationSyntaxError(se, BPMN2_TYPE, SyntaxCheckerErrors.START_NODE_NO_OUTGOING_CONNECTIONS));
                        }
                    } else {
                        addError(se, new ValidationSyntaxError(se, BPMN2_TYPE, SyntaxCheckerErrors.START_NODE_NO_OUTGOING_CONNECTIONS));
                    }
				}
			} else if (fe instanceof EndEvent) {
				EndEvent ee = (EndEvent) fe;
				if(ee.getIncoming() == null || ee.getIncoming().size() < 1) {
                    if(container instanceof Process) {
                        if(!isAdHocProcess(process)) {
                            addError(ee, new ValidationSyntaxError(ee, BPMN2_TYPE, SyntaxCheckerErrors.END_NODE_NO_INCOMING_CONNECTIONS));
                        }
                    } else if(container instanceof SubProcess) {
                        if(!(container instanceof AdHocSubProcess)) {
                            addError(ee, new ValidationSyntaxError(ee, BPMN2_TYPE, SyntaxCheckerErrors.END_NODE_NO_INCOMING_CONNECTIONS));
                        }
                    } else {
                        addError(ee, new ValidationSyntaxError(ee, BPMN2_TYPE, SyntaxCheckerErrors.END_NODE_NO_INCOMING_CONNECTIONS));
                    }
				}
			} else {
				if(fe instanceof FlowNode) {
					FlowNode fn = (FlowNode) fe;
					if((fn.getOutgoing() == null || fn.getOutgoing().size() < 1) && !isAdHocProcess(process) && !(fn instanceof BoundaryEvent) && !(fn instanceof EventSubprocess)) {
                        if(container instanceof Process) {
                            if(!isAdHocProcess(process)) {
                                if(!isCompensatingFlowNodeInProcess(fn, (Process) container)) {
                                    addError(fn, new ValidationSyntaxError(fn, BPMN2_TYPE, SyntaxCheckerErrors.NODE_NO_OUTGOING_CONNECTIONS));
                                }
                            }
                        } else if(container instanceof SubProcess) {
                            if(!(container instanceof AdHocSubProcess)) {
                                if(!isCompensatingFlowNodeInSubprocess(fn, (SubProcess) container)) {
                                    addError(fn, new ValidationSyntaxError(fn, BPMN2_TYPE, SyntaxCheckerErrors.NODE_NO_OUTGOING_CONNECTIONS));
                                }
                            }
                        } else {
                            addError(fn, new ValidationSyntaxError(fn, BPMN2_TYPE, SyntaxCheckerErrors.NODE_NO_OUTGOING_CONNECTIONS));
                        }
    				}
                    if(!(fn instanceof BoundaryEvent)) {
                        if((fn.getIncoming() == null || fn.getIncoming().size() < 1) && !isAdHocProcess(process)) {
                            if(container instanceof Process) {
                                if(!isAdHocProcess(process) && !(fn instanceof EventSubprocess)) {
                                    if(!isCompensatingFlowNodeInProcess(fn, (Process) container)) {
                                        addError(fn, new ValidationSyntaxError(fn, BPMN2_TYPE, SyntaxCheckerErrors.NODE_NO_INCOMING_CONNECTIONS));
                                    }
                                }
                            } else if(container instanceof SubProcess) {
                                if(!(container instanceof AdHocSubProcess)) {
                                    if(!isCompensatingFlowNodeInSubprocess(fn, (SubProcess) container)) {
                                        addError(fn, new ValidationSyntaxError(fn, BPMN2_TYPE, SyntaxCheckerErrors.NODE_NO_INCOMING_CONNECTIONS));
                                    }
                                }
                            } else {
                                addError(fn, new ValidationSyntaxError(fn, BPMN2_TYPE, SyntaxCheckerErrors.NODE_NO_INCOMING_CONNECTIONS));
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
	                		addError(bt, new ValidationSyntaxError(bt, BPMN2_TYPE, SyntaxCheckerErrors.BUSINESS_RULE_TASK_NO_RULEFLOW_GROUP));
	                	}
	                }
	            }
	            if(!foundRuleflowGroup) {
	            	addError(bt, new ValidationSyntaxError(bt, BPMN2_TYPE, SyntaxCheckerErrors.BUSINESS_RULE_TASK_NO_RULEFLOW_GROUP));
	            }
			}
			
			if(fe instanceof ScriptTask) {
				ScriptTask st = (ScriptTask) fe;
				if(isEmpty(st.getScript())) {
					addError(st, new ValidationSyntaxError(st, BPMN2_TYPE, SyntaxCheckerErrors.SCRIPT_TASK_HAS_NO_SCRIPT));
				}
				if(isEmpty(st.getScriptFormat())) {
					addError(st, new ValidationSyntaxError(st, BPMN2_TYPE, SyntaxCheckerErrors.SCRIPT_TASK_HAS_NO_SCRIPT_FORMAT));
				}
			}
			
			if(fe instanceof SendTask) {
				SendTask st = (SendTask) fe;
				if(st.getMessageRef() == null) {
					addError(st, new ValidationSyntaxError(st, BPMN2_TYPE, SyntaxCheckerErrors.SEND_TASK_HAS_NO_MESSAGE));
				}
			}

            if(fe instanceof ServiceTask) {
                ServiceTask st = (ServiceTask) fe;
                if(st.getOperationRef() == null) {
                    addError(st, new ValidationSyntaxError(st, BPMN2_TYPE, SyntaxCheckerErrors.SERVICE_TASK_HAS_NO_OPERATION));
                }
            }
			
			if(fe instanceof UserTask) {
				UserTask ut = (UserTask) fe;
				String taskName = null;
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
                                        addError(ut, new ValidationSyntaxError(ut, BPMN2_TYPE, SyntaxCheckerErrors.USER_TASK_HAS_NO_TASK_NAME));
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
		        if(!foundTaskName) {
		        	addError(ut, new ValidationSyntaxError(ut, BPMN2_TYPE, SyntaxCheckerErrors.USER_TASK_HAS_NO_TASK_NAME));
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
	        							addError(ut, new ValidationSyntaxError(ut, SIMULATION_TYPE, SyntaxCheckerErrors.STAFF_AVAILABILITY_VALUE_MUST_BE_POSITIVE));
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
	        							addError(ta, new ValidationSyntaxError(ta, SIMULATION_TYPE, SyntaxCheckerErrors.COST_PER_TIME_UNIT_VALUE_MUST_BE_POSITIVE));
	        						}
	        					}
	        				}
	        				if(eleType.getResourceParameters() != null) {
	        					ResourceParameters resourceParams = eleType.getResourceParameters();
	        					if(resourceParams.getQuantity() != null) {
	        						FloatingParameterType workingHoursVal = (FloatingParameterType) resourceParams.getQuantity().getParameterValue().get(0);
	        						if(workingHoursVal.getValue() < 0) {
	        							addError(ta, new ValidationSyntaxError(ta, SIMULATION_TYPE, SyntaxCheckerErrors.WORKING_HOURS_VALUE_MUST_BE_POSITIVE));
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
    				checkEventDefinition(event, ed, false);
				}
			}
			
			if(fe instanceof ThrowEvent) {
				ThrowEvent event = (ThrowEvent) fe;
				List<EventDefinition> eventdefs = event.getEventDefinitions();
		        for(EventDefinition ed : eventdefs) {
		            checkEventDefinition(event, ed, true);
				}
			}
			
			if(fe instanceof SequenceFlow) {
				SequenceFlow sf = (SequenceFlow) fe;
				if(sf.getSourceRef() == null) {
					addError((SequenceFlow) fe, new ValidationSyntaxError(fe, BPMN2_TYPE, SyntaxCheckerErrors.AN_EDGE_MUST_HAVE_A_SOURCE_NODE));
				}
				if(sf.getTargetRef() == null) {
					addError((SequenceFlow) fe, new ValidationSyntaxError(fe, BPMN2_TYPE, SyntaxCheckerErrors.AN_EDGE_MUST_HAVE_A_TARGET_NODE));
				}
			}
			
			if(fe instanceof Gateway) {
				Gateway gw = (Gateway) fe;
				if(gw.getGatewayDirection() == null || gw.getGatewayDirection().getValue() == GatewayDirection.UNSPECIFIED.getValue()) {
					addError((Gateway) fe, new ValidationSyntaxError(fe, BPMN2_TYPE, SyntaxCheckerErrors.GATEWAY_DOES_NOT_SPECIFY_A_VALID_DIRECTION));
				}
				if(gw instanceof ExclusiveGateway) {
					if(gw.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue() && gw.getGatewayDirection().getValue() != GatewayDirection.CONVERGING.getValue()) {
						addError(fe, new ValidationSyntaxError(fe, BPMN2_TYPE,
								SyntaxCheckerErrors.INVALID_GATEWAY_DIRECTION_FOR +
								SyntaxCheckerErrors.EXCLUSING_GATEWAY +
								SyntaxCheckerErrors.IT_SHOULD_BE_CONVERGING_OR_DIVERGING));
					}
				}
				if(gw instanceof EventBasedGateway) {
					if(gw.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue()) {
						addError(fe, new ValidationSyntaxError(fe, BPMN2_TYPE,
								SyntaxCheckerErrors.INVALID_GATEWAY_DIRECTION_FOR +
								SyntaxCheckerErrors.EVENT_BASED_GATEWAY +
								SyntaxCheckerErrors.IT_SHOULD_BE_DIVERGING));
					}
				}
				if(gw instanceof ParallelGateway) {
					if(gw.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue() && gw.getGatewayDirection().getValue() != GatewayDirection.CONVERGING.getValue()) {
						addError(fe, new ValidationSyntaxError(fe, BPMN2_TYPE,
								SyntaxCheckerErrors.INVALID_GATEWAY_DIRECTION_FOR +
								SyntaxCheckerErrors.PARALLEL_GATEWAY +
								SyntaxCheckerErrors.IT_SHOULD_BE_CONVERGING_OR_DIVERGING));
					}
				}
//				if(gw instanceof InclusiveGateway) {
//					if(gw.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue()) {
//						addError(fe, new ValidationSyntaxError(fe, BPMN2_TYPE, "Invalid Gateway direction for Inclusive Gateway. It should be 'Diverging'."));
//					}
//				}
				if(gw instanceof ComplexGateway) {
					if(gw.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue() && gw.getGatewayDirection().getValue() != GatewayDirection.CONVERGING.getValue()) {
						addError(fe, new ValidationSyntaxError(fe, BPMN2_TYPE,
								SyntaxCheckerErrors.INVALID_GATEWAY_DIRECTION_FOR +
								SyntaxCheckerErrors.COMPLEX_GATEWAY +
								SyntaxCheckerErrors.IT_SHOULD_BE_CONVERGING_OR_DIVERGING));
					}
				}

                if( (gw instanceof ExclusiveGateway || gw instanceof InclusiveGateway) && (gw.getGatewayDirection().getValue() == GatewayDirection.DIVERGING.getValue())) {
                    List<SequenceFlow> outgoingFlows = gw.getOutgoing();
                    if(outgoingFlows != null && outgoingFlows.size() > 0) {
                        for(SequenceFlow flow : outgoingFlows) {
                            if(flow.getConditionExpression() == null) {
                                addError(flow, new ValidationSyntaxError(flow, BPMN2_TYPE, SyntaxCheckerErrors.SEQUENCE_FLOW_NO_CONDITION_EXPRESSION_DEFINED));
                            } else {
                                if(flow.getConditionExpression() instanceof FormalExpression) {
                                    FormalExpression formalExp = (FormalExpression) flow.getConditionExpression();
                                    if(formalExp.getBody() == null && formalExp.getBody().length() < 1) {
                                        addError(flow, new ValidationSyntaxError(flow, BPMN2_TYPE, SyntaxCheckerErrors.SEQUENCE_FLOW_NO_CONDITION_EXPRESSION_DEFINED));
                                    }
                                } else {
                                    addError(flow, new ValidationSyntaxError(flow, BPMN2_TYPE, SyntaxCheckerErrors.INVALID_CONDITION_EXPRESSION_ON_SEQUENCE_FLOW));
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
				                    				addError(sf, new ValidationSyntaxError(sf, SIMULATION_TYPE, SyntaxCheckerErrors.PROBABILITY_VALUE_MUST_BE_POSITIVE));
				                    			} else {
				                    				sum += valType.getValue();
				                    			}
					        				} else {
					        					addError(sf, new ValidationSyntaxError(sf, SIMULATION_TYPE, SyntaxCheckerErrors.SEQUENCE_FLOW_HAS_NO_PROBABILITY_DEFINED));
					        				}
					        			}
					        		}
					        	}
						}
						if(sum != 100) {
							addError(gw, new ValidationSyntaxError(gw, SIMULATION_TYPE, SyntaxCheckerErrors.THE_SUM_OF_PROBABILITY_VALUES_OF_ALL_OUTGOING_SEQUENCE_FLOWS_MUST_BE_EQUAL_100));
						}
					}
				}
			}
			
			if(fe instanceof CallActivity) {
				CallActivity ca = (CallActivity) fe;
				if(ca.getCalledElement() == null || ca.getCalledElement().length() < 1) {
					addError((CallActivity) fe, new ValidationSyntaxError(fe, BPMN2_TYPE, SyntaxCheckerErrors.REUSABLE_SUBPROCESS_HAS_NO_CALLED_ELEMENT_SPECIFIED));
				}
			}
			
			if(fe instanceof DataObject) {
				DataObject dao = (DataObject) fe;
				if(dao.getName() == null || dao.getName().length() < 1) {
					addError((DataObject) fe, new ValidationSyntaxError(fe, BPMN2_TYPE, SyntaxCheckerErrors.DATA_OBJECT_HAS_NO_NAME_DEFINED));
				} else {
					if(containsWhiteSpace(dao.getName())) {
						addError((DataObject) fe, new ValidationSyntaxError(fe, BPMN2_TYPE, SyntaxCheckerErrors.DATA_OBJECT_NAME_CONTAINS_WHITE_SPACES));
					}
				}
			}
			
			if(fe instanceof SubProcess) {
				checkFlowElements((SubProcess) fe, process, defaultScenario);
			}
		}
	}

	private void checkEventDefinition(Event event, EventDefinition ed, boolean isThrowingEvent) {
		String prefix = isThrowingEvent ? SyntaxCheckerErrors.THROW_EVENT : SyntaxCheckerErrors.CATCH_EVENT;
		if(ed instanceof TimerEventDefinition) {
			TimerEventDefinition ted = (TimerEventDefinition) ed;
			if(ted.getTimeDate() != null && ted.getTimeDuration() != null && ted.getTimeCycle() != null) {
				addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, prefix + SyntaxCheckerErrors.HAS_TIME_DATE_AND_TIME_DURATION_AND_TIME_CYCLE_DEFINED));
			} else if(ted.getTimeDate() != null && ted.getTimeDuration() != null) {
				addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, prefix + SyntaxCheckerErrors.HAS_BOTH_TIME_DATE_AND_TIME_DURATION_DEFINED));
			} else if(ted.getTimeDate() != null && ted.getTimeCycle() != null) {
				addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, prefix + SyntaxCheckerErrors.HAS_BOTH_TIME_DATE_AND_TIME_CYCLE_DEFINED));
			} else if(ted.getTimeDuration() != null && ted.getTimeCycle() != null) {
				addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, prefix + SyntaxCheckerErrors.HAS_BOTH_TIMEDURATION_AND_TIMECYCLE_DEFINED));
			}

			if(ted.getTimeDate() == null && ted.getTimeDuration() == null && ted.getTimeCycle() == null) {
				addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, prefix + SyntaxCheckerErrors.HAS_NO_TIME_DATE_OR_TIME_DURATION_OR_TIME_CYCLE_DEFINED));
			}
		} else if( ed instanceof SignalEventDefinition) {
			if(((SignalEventDefinition) ed).getSignalRef() == null) {
				addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, prefix + SyntaxCheckerErrors.HAS_NO_SIGNALREF));
			}
		} else if( ed instanceof ErrorEventDefinition) {
			if(((ErrorEventDefinition) ed).getErrorRef() == null || ((ErrorEventDefinition) ed).getErrorRef().getErrorCode() == null) {
				addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, prefix + SyntaxCheckerErrors.HAS_NO_ERRORREF));
			}
		} else if( ed instanceof ConditionalEventDefinition ) {
			FormalExpression conditionalExp = (FormalExpression) ((ConditionalEventDefinition) ed).getCondition();
			if(conditionalExp.getBody() == null) {
				addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, prefix + SyntaxCheckerErrors.HAS_NO_CONDITIONAL_EXPRESSION));
			}
		} else if( ed instanceof EscalationEventDefinition ) {
			if(((EscalationEventDefinition) ed).getEscalationRef() == null) {
				addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, prefix + SyntaxCheckerErrors.HAS_NO_ESCALATIONREF));
			}
		} else if( ed instanceof MessageEventDefinition) {
			if(((MessageEventDefinition) ed).getMessageRef() == null) {
				addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, prefix + SyntaxCheckerErrors.HAS_NO_MESSAGEREF));
			}
		} else if( ed instanceof CompensateEventDefinition) {
			if(((CompensateEventDefinition) ed).getActivityRef() == null) {
				addError(event, new ValidationSyntaxError(event, BPMN2_TYPE, prefix + SyntaxCheckerErrors.HAS_NO_ACTIVITYREF));
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
