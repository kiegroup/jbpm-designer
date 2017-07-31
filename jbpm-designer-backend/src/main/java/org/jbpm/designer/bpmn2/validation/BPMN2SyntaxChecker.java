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

import bpsim.BPSimDataType;
import bpsim.BpsimPackage;
import bpsim.CostParameters;
import bpsim.ElementParameters;
import bpsim.FloatingParameterType;
import bpsim.ResourceParameters;
import bpsim.Scenario;
import bpsim.impl.BpsimFactoryImpl;
import org.drools.core.xml.SemanticModules;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Artifact;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ComplexGateway;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventBasedGateway;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.EventSubprocess;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.GatewayDirection;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.impl.DroolsFactoryImpl;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.designer.bpmn2.BpmnMarshallerHelper;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.process.core.validation.ProcessValidationError;
import org.jbpm.ruleflow.core.validation.RuleFlowProcessValidator;
import org.jbpm.workflow.core.node.RuleSetNode;
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

    public BPMN2SyntaxChecker(String json,
                              String preprocessingData,
                              IDiagramProfile profile) {
        this.json = json;
        this.preprocessingData = preprocessingData;
        this.profile = profile;
    }

    public void checkSyntax() {
        DroolsFactoryImpl.init();
        BpsimFactoryImpl.init();

        Process process = null;
        try {
            Definitions def = profile.createMarshaller().getDefinitions(json,
                                                                        preprocessingData);
            List<RootElement> rootElements = def.getRootElements();
            Scenario defaultScenario = getDefaultScenario(def);

            for (RootElement root : rootElements) {
                if (root instanceof Process) {
                    process = (Process) root;

                    if (isEmpty(process.getId())) {
                        addError(defaultResourceId,
                                 new ValidationSyntaxError(process,
                                                           BPMN2_TYPE,
                                                           "Process has no id."));
                    } else if (!SyntaxCheckerUtils.isNCName(process.getId())) {
                        addError(defaultResourceId,
                                 new ValidationSyntaxError(process,
                                                           BPMN2_TYPE,
                                                           "Invalid process id. See http://www.w3.org/TR/REC-xml-names/#NT-NCName for more info."));
                    } else {
                        defaultResourceId = process.getId();
                    }

                    String pname;
                    Iterator<FeatureMap.Entry> iter = process.getAnyAttribute().iterator();
                    while (iter.hasNext()) {
                        FeatureMap.Entry entry = iter.next();
                        if (entry.getEStructuralFeature().getName().equals("packageName")) {
                            pname = (String) entry.getValue();
                            if (!isEmpty(pname) && !isValidPackageName(pname)) {
                                addError(defaultResourceId,
                                         new ValidationSyntaxError(process,
                                                                   BPMN2_TYPE,
                                                                   SyntaxCheckerErrors.PACKAGE_NAME_CONTAINS_INVALID_CHARACTERS));
                            }
                        }
                    }

                    if (isEmpty(process.getName())) {
                        addError(defaultResourceId,
                                 new ValidationSyntaxError(process,
                                                           BPMN2_TYPE,
                                                           "Process has no name."));
                    }

                    List<Property> processProperties = process.getProperties();
                    if (processProperties != null && processProperties.size() > 0) {
                        for (Property prop : processProperties) {
                            String propId = prop.getId();
                            Pattern pattern = Pattern.compile("\\s");
                            Matcher matcher = pattern.matcher(propId);
                            if (matcher.find()) {
                                addError(defaultResourceId,
                                         new ValidationSyntaxError(process,
                                                                   BPMN2_TYPE,
                                                                   "Process variable \"" + propId + "\" contains white spaces."));
                            }
                        }
                    }

                    boolean foundStartEvent = checkEventsCount(StartEvent.class,
                                                               process.getFlowElements()) > 0;
                    boolean foundEndEvent = checkEventsCount(EndEvent.class,
                                                             process.getFlowElements()) > 0;
                    if (!foundStartEvent && !isAdHocProcess(process)) {
                        addError(defaultResourceId,
                                 new ValidationSyntaxError(process,
                                                           BPMN2_TYPE,
                                                           "Process has no start node."));
                    }
                    if (!foundEndEvent && !isAdHocProcess(process)) {
                        addError(defaultResourceId,
                                 new ValidationSyntaxError(process,
                                                           BPMN2_TYPE,
                                                           "Process has no end node."));
                    }

                    checkFlowElements(process,
                                      process,
                                      defaultScenario);
                }
            }
        } catch (Exception e) {
            addError(defaultResourceId,
                     new ValidationSyntaxError(process,
                                               PROCESS_TYPE,
                                               "Could not parse BPMN2 process."));
        }

        // if there are no suggestions add RuleFlowProcessValidator process errors
        if (this.errors.size() < 1) {
            try {
                SemanticModules modules = new SemanticModules();
                modules.addSemanticModule(new BPMNSemanticModule());
                modules.addSemanticModule(new BPMNDISemanticModule());
                XmlProcessReader xmlReader = new XmlProcessReader(modules,
                                                                  getClass().getClassLoader());
                List<org.kie.api.definition.process.Process> processes = xmlReader.read(new StringReader(profile.createMarshaller().parseModel(json,
                                                                                                                                               preprocessingData)));
                if (processes != null) {
                    ProcessValidationError[] errors = RuleFlowProcessValidator.getInstance().validateProcess((org.jbpm.ruleflow.core.RuleFlowProcess) processes.get(0));
                    for (ProcessValidationError er : errors) {
                        addError(defaultResourceId,
                                 new ValidationSyntaxError(process,
                                                           PROCESS_TYPE,
                                                           er.getMessage()));
                    }
                }
            } catch (Exception e) {
                _logger.warn("Could not parse to RuleFlowProcess.");
                addError(defaultResourceId,
                         new ValidationSyntaxError(process,
                                                   PROCESS_TYPE,
                                                   "Could not parse BPMN2 to RuleFlowProcess."));
            }
        }
    }

    private void checkFlowElements(FlowElementsContainer container,
                                   Process process,
                                   Scenario defaultScenario) {

        for (FlowElement fe : container.getFlowElements()) {
            if (fe instanceof StartEvent) {
                checkStartEvent((StartEvent) fe,
                                container,
                                process);
            } else if (fe instanceof EndEvent) {
                checkEndEvent((EndEvent) fe,
                              container,
                              process);
            } else {
                if (fe instanceof FlowNode) {
                    checkFlowNode((FlowNode) fe,
                                  container,
                                  process);
                }
            }

            if (fe instanceof BusinessRuleTask) {
                checkBusinessRuleTask((BusinessRuleTask) fe);
            }

            if (fe instanceof ScriptTask) {
                checkScriptTask((ScriptTask) fe);
            }

            if (fe instanceof ReceiveTask) {
                checkReceiveTask((ReceiveTask) fe);
            }

            if (fe instanceof SendTask) {
                checkSendTask((SendTask) fe);
            }

            if (fe instanceof ServiceTask) {
                checkServiceTask((ServiceTask) fe);
            }

            if (fe instanceof UserTask) {
                checkUserTask((UserTask) fe,
                              defaultScenario);
            }

            if (fe instanceof Task) {
                checkTask((Task) fe,
                          defaultScenario);
            }

            if (fe instanceof CatchEvent) {
                checkCatchEvent((CatchEvent) fe);
            }

            if (fe instanceof ThrowEvent) {
                checkThrowEvent((ThrowEvent) fe);
            }

            if (fe instanceof SequenceFlow) {
                checkSequenceFlow((SequenceFlow) fe);
            }

            if (fe instanceof Gateway) {
                checkGateway((Gateway) fe,
                             defaultScenario);
            }

            if (fe instanceof CallActivity) {
                checkCallActivity((CallActivity) fe);
            }

            if (fe instanceof DataObject) {
                checkDataObject((DataObject) fe);
            }

            if (fe instanceof SubProcess) {
                if (checkEventsCount(StartEvent.class,
                                     ((SubProcess) fe).getFlowElements()) > 1) {
                    addError(fe,
                             new ValidationSyntaxError(fe,
                                                       BPMN2_TYPE,
                                                       SyntaxCheckerErrors.MULTIPLE_START_EVENTS));
                }
                checkFlowElements((SubProcess) fe,
                                  process,
                                  defaultScenario);
            }
        }
    }

    private int checkEventsCount(Class<? extends Event> event,
                                 List<FlowElement> flowElements) {
        int occurrence = 0;

        if (flowElements != null) {
            for (FlowElement element : flowElements) {
                if (event.isInstance(element)) {
                    occurrence++;
                }
            }
        }

        return occurrence;
    }

    private void checkStartEvent(StartEvent startEvent,
                                 FlowElementsContainer container,
                                 Process process) {

        if (startEvent.getOutgoing() == null || startEvent.getOutgoing().isEmpty()) {
            if (container instanceof Process) {
                if (!isAdHocProcess(process)) {
                    addError(startEvent,
                             new ValidationSyntaxError(startEvent,
                                                       BPMN2_TYPE,
                                                       SyntaxCheckerErrors.START_NODE_NO_OUTGOING_CONNECTIONS));
                }
            } else if (container instanceof SubProcess) {
                if (!(container instanceof AdHocSubProcess)) {
                    addError(startEvent,
                             new ValidationSyntaxError(startEvent,
                                                       BPMN2_TYPE,
                                                       SyntaxCheckerErrors.START_NODE_NO_OUTGOING_CONNECTIONS));
                }
            } else {
                addError(startEvent,
                         new ValidationSyntaxError(startEvent,
                                                   BPMN2_TYPE,
                                                   SyntaxCheckerErrors.START_NODE_NO_OUTGOING_CONNECTIONS));
            }
        }
    }

    private void checkEndEvent(EndEvent endEvent,
                               FlowElementsContainer container,
                               Process process) {

        if (endEvent.getIncoming() == null || endEvent.getIncoming().isEmpty()) {
            if (container instanceof Process) {
                if (!isAdHocProcess(process)) {
                    addError(endEvent,
                             new ValidationSyntaxError(endEvent,
                                                       BPMN2_TYPE,
                                                       SyntaxCheckerErrors.END_NODE_NO_INCOMING_CONNECTIONS));
                }
            } else if (container instanceof SubProcess) {
                if (!(container instanceof AdHocSubProcess)) {
                    addError(endEvent,
                             new ValidationSyntaxError(endEvent,
                                                       BPMN2_TYPE,
                                                       SyntaxCheckerErrors.END_NODE_NO_INCOMING_CONNECTIONS));
                }
            } else {
                addError(endEvent,
                         new ValidationSyntaxError(endEvent,
                                                   BPMN2_TYPE,
                                                   SyntaxCheckerErrors.END_NODE_NO_INCOMING_CONNECTIONS));
            }
        }
    }

    private void checkFlowNode(FlowNode flowNode,
                               FlowElementsContainer container,
                               Process process) {
        if ((flowNode.getOutgoing() == null || flowNode.getOutgoing().isEmpty()) && !isAdHocProcess(process) && !(flowNode instanceof BoundaryEvent) && !(flowNode instanceof EventSubprocess)) {
            if (container instanceof Process) {
                if (!isAdHocProcess(process)) {
                    if (!isCompensatingFlowNodeInProcess(flowNode,
                                                         (Process) container)) {
                        addError(flowNode,
                                 new ValidationSyntaxError(flowNode,
                                                           BPMN2_TYPE,
                                                           SyntaxCheckerErrors.NODE_NO_OUTGOING_CONNECTIONS));
                    }
                }
            } else if (container instanceof SubProcess) {
                if (!(container instanceof AdHocSubProcess)) {
                    if (!isCompensatingFlowNodeInSubprocess(flowNode,
                                                            (SubProcess) container)) {
                        addError(flowNode,
                                 new ValidationSyntaxError(flowNode,
                                                           BPMN2_TYPE,
                                                           SyntaxCheckerErrors.NODE_NO_OUTGOING_CONNECTIONS));
                    }
                }
            } else {
                addError(flowNode,
                         new ValidationSyntaxError(flowNode,
                                                   BPMN2_TYPE,
                                                   SyntaxCheckerErrors.NODE_NO_OUTGOING_CONNECTIONS));
            }
        }
        if (!(flowNode instanceof BoundaryEvent)) {
            if ((flowNode.getIncoming() == null || flowNode.getIncoming().isEmpty()) && !isAdHocProcess(process)) {
                if (container instanceof Process) {
                    if (!isAdHocProcess(process) && !(flowNode instanceof EventSubprocess)) {
                        if (!isCompensatingFlowNodeInProcess(flowNode,
                                                             (Process) container)) {
                            addError(flowNode,
                                     new ValidationSyntaxError(flowNode,
                                                               BPMN2_TYPE,
                                                               SyntaxCheckerErrors.NODE_NO_INCOMING_CONNECTIONS));
                        }
                    }
                } else if (container instanceof SubProcess) {
                    if (!(container instanceof AdHocSubProcess)) {
                        if (!isCompensatingFlowNodeInSubprocess(flowNode,
                                                                (SubProcess) container)) {
                            addError(flowNode,
                                     new ValidationSyntaxError(flowNode,
                                                               BPMN2_TYPE,
                                                               SyntaxCheckerErrors.NODE_NO_INCOMING_CONNECTIONS));
                        }
                    }
                } else {
                    addError(flowNode,
                             new ValidationSyntaxError(flowNode,
                                                       BPMN2_TYPE,
                                                       SyntaxCheckerErrors.NODE_NO_INCOMING_CONNECTIONS));
                }
            }
        }
    }

    private void checkBusinessRuleTask(BusinessRuleTask businessRuleTask) {
        String ruleLanguage = businessRuleTask.getImplementation();
        if (RuleSetNode.DMN_LANG.equals(ruleLanguage)) {
            ruleLanguage = BpmnMarshallerHelper.RULE_LANG_DMN;
        } else {
            ruleLanguage = BpmnMarshallerHelper.RULE_LANG_DRL;
        }
        if (ruleLanguage.equals(BpmnMarshallerHelper.RULE_LANG_DRL)) {
            Iterator<FeatureMap.Entry> biter = businessRuleTask.getAnyAttribute().iterator();
            boolean foundRuleflowGroup = false;
            while (biter.hasNext()) {
                FeatureMap.Entry entry = biter.next();
                if (entry.getEStructuralFeature().getName().equals("ruleFlowGroup")) {
                    foundRuleflowGroup = true;
                    String ruleflowGroup = (String) entry.getValue();
                    if (isEmpty(ruleflowGroup)) {
                        addError(businessRuleTask,
                                 new ValidationSyntaxError(businessRuleTask,
                                                           BPMN2_TYPE,
                                                           SyntaxCheckerErrors.BUSINESS_RULE_TASK_NO_RULEFLOW_GROUP));
                    }
                }
            }
            if (!foundRuleflowGroup) {
                addError(businessRuleTask,
                         new ValidationSyntaxError(businessRuleTask,
                                                   BPMN2_TYPE,
                                                   SyntaxCheckerErrors.BUSINESS_RULE_TASK_NO_RULEFLOW_GROUP));
            }
        } else if (ruleLanguage.equals(BpmnMarshallerHelper.RULE_LANG_DMN)) {
            boolean missingNamespace = true;
            boolean missingModel = true;
            if (businessRuleTask.getIoSpecification() != null) {
                List<DataInput> dataInputs = businessRuleTask.getIoSpecification().getDataInputs();

                if (dataInputs != null) {
                    for (DataInput di : dataInputs) {
                        if ("namespace".equals(di.getName())) {
                            missingNamespace = false;
                        } else if ("model".equals(di.getName())) {
                            missingModel = false;
                        }
                    }
                }
            }
            if (missingNamespace) {
                addError(businessRuleTask,
                         new ValidationSyntaxError(businessRuleTask,
                                                   BPMN2_TYPE,
                                                   SyntaxCheckerErrors.DMN_BUSINESS_RULE_TASK_NO_NAMESPACE));
            }
            if (missingModel) {
                addError(businessRuleTask,
                         new ValidationSyntaxError(businessRuleTask,
                                                   BPMN2_TYPE,
                                                   SyntaxCheckerErrors.DMN_BUSINESS_RULE_TASK_NO_MODEL));
            }
        }
    }

    private void checkScriptTask(ScriptTask scriptTask) {
        if (isEmpty(scriptTask.getScript())) {
            addError(scriptTask,
                     new ValidationSyntaxError(scriptTask,
                                               BPMN2_TYPE,
                                               SyntaxCheckerErrors.SCRIPT_TASK_NO_SCRIPT));
        }
        if (isEmpty(scriptTask.getScriptFormat())) {
            addError(scriptTask,
                     new ValidationSyntaxError(scriptTask,
                                               BPMN2_TYPE,
                                               SyntaxCheckerErrors.SCRIPT_TASK_NO_SCRIPT_FORMAT));
        }
    }

    private void checkReceiveTask(ReceiveTask receiveTask) {
        checkMessageRefOfTask(receiveTask,
                              receiveTask.getMessageRef());
    }

    private void checkSendTask(SendTask sendTask) {
        checkMessageRefOfTask(sendTask,
                              sendTask.getMessageRef());
    }

    private void checkMessageRefOfTask(Task task,
                                       Message message) {
        if (message == null) {
            addError(task,
                     new ValidationSyntaxError(task,
                                               BPMN2_TYPE,
                                               SyntaxCheckerErrors.TASK_NO_MESSAGE));
        }
    }

    private void checkServiceTask(ServiceTask serviceTask) {

        if (serviceTask.getOperationRef() == null) {
            addError(serviceTask,
                     new ValidationSyntaxError(serviceTask,
                                               BPMN2_TYPE,
                                               SyntaxCheckerErrors.SERVICE_TASK_NO_OPERATION));
        }
    }

    private void checkUserTask(UserTask userTask,
                               Scenario defaultScenario) {

        String taskName = null;
        boolean foundTaskName = false;

        if (userTask.getIoSpecification() != null && userTask.getIoSpecification().getDataInputs() != null) {
            List<DataInput> taskDataInputs = userTask.getIoSpecification().getDataInputs();
            for (DataInput din : taskDataInputs) {
                if (din.getName() != null && din.getName().equals("TaskName")) {
                    List<DataInputAssociation> taskDataInputAssociations = userTask.getDataInputAssociations();
                    for (DataInputAssociation dia : taskDataInputAssociations) {
                        if (dia.getTargetRef().getId() != null && (dia.getTargetRef().getId().equals(din.getId()))) {
                            foundTaskName = true;
                            taskName = ((FormalExpression) dia.getAssignment().get(0).getFrom()).getBody();
                            if (isEmpty(taskName)) {
                                addError(userTask,
                                         new ValidationSyntaxError(userTask,
                                                                   BPMN2_TYPE,
                                                                   SyntaxCheckerErrors.USER_TASK_HAS_NO_TASK_NAME));
                            }
                        }
                    }
                    break;
                }
            }
        }
        if (!foundTaskName) {
            addError(userTask,
                     new ValidationSyntaxError(userTask,
                                               BPMN2_TYPE,
                                               SyntaxCheckerErrors.USER_TASK_HAS_NO_TASK_NAME));
        }

        // simulation validation
        for (ElementParameters eleType : getElementParameters(defaultScenario,
                                                              userTask)) {
            if (eleType.getResourceParameters() != null) {
                ResourceParameters resourceParams = eleType.getResourceParameters();
                if (resourceParams.getQuantity() != null) {
                    FloatingParameterType quantityVal = (FloatingParameterType) resourceParams.getQuantity().getParameterValue().get(0);
                    double val = quantityVal.getValue();
                    if (val < 0) {
                        addError(userTask,
                                 new ValidationSyntaxError(userTask,
                                                           SIMULATION_TYPE,
                                                           SyntaxCheckerErrors.STAFF_AVAILABILITY_MUST_BE_POSITIVE));
                    }
                }
            }
        }
    }

    private void checkTask(Task task,
                           Scenario defaultScenario) {
        // simulation validation
        for (ElementParameters eleType : getElementParameters(defaultScenario,
                                                              task)) {
            if (eleType.getCostParameters() != null) {
                CostParameters costParams = eleType.getCostParameters();
                if (costParams.getUnitCost() != null) {
                    FloatingParameterType unitCostVal = (FloatingParameterType) costParams.getUnitCost().getParameterValue().get(0);
                    Double val = unitCostVal.getValue();
                    if (val.doubleValue() < 0) {
                        addError(task,
                                 new ValidationSyntaxError(task,
                                                           SIMULATION_TYPE,
                                                           SyntaxCheckerErrors.COST_PER_TIME_UNIT_MUST_BE_POSITIVE));
                    }
                }
            }
            if (eleType.getResourceParameters() != null) {
                ResourceParameters resourceParams = eleType.getResourceParameters();
                if (resourceParams.getQuantity() != null) {
                    FloatingParameterType workingHoursVal = (FloatingParameterType) resourceParams.getQuantity().getParameterValue().get(0);
                    if (workingHoursVal.getValue() < 0) {
                        addError(task,
                                 new ValidationSyntaxError(task,
                                                           SIMULATION_TYPE,
                                                           SyntaxCheckerErrors.WORKING_HOURS_MUST_BE_POSITIVE));
                    }
                }
            }
        }
    }

    private void checkCatchEvent(CatchEvent catchEvent) {

        List<EventDefinition> eventdefs = catchEvent.getEventDefinitions();
        for (EventDefinition ed : eventdefs) {
            checkEventDefinition(catchEvent,
                                 ed,
                                 "Catch");
        }
    }

    private void checkThrowEvent(ThrowEvent throwEvent) {

        List<EventDefinition> eventdefs = throwEvent.getEventDefinitions();
        for (EventDefinition ed : eventdefs) {
            checkEventDefinition(throwEvent,
                                 ed,
                                 "Throw");
        }
    }

    private void checkEventDefinition(Event event,
                                      EventDefinition eventDefinition,
                                      String errorPrefix) {
        if (eventDefinition instanceof TimerEventDefinition) {
            TimerEventDefinition ted = (TimerEventDefinition) eventDefinition;
            if (ted.getTimeDate() != null && ted.getTimeDuration() != null && ted.getTimeCycle() != null) {
                addError(event,
                         new ValidationSyntaxError(event,
                                                   BPMN2_TYPE,
                                                   errorPrefix + " Event has timeDate and timeDuration and timeCycle defined."));
            } else if (ted.getTimeDate() != null && ted.getTimeDuration() != null) {
                addError(event,
                         new ValidationSyntaxError(event,
                                                   BPMN2_TYPE,
                                                   errorPrefix + " Event has both timeDate and timeDuration defined."));
            } else if (ted.getTimeDate() != null && ted.getTimeCycle() != null) {
                addError(event,
                         new ValidationSyntaxError(event,
                                                   BPMN2_TYPE,
                                                   errorPrefix + " Event has both timeDate and timeCycle defined."));
            } else if (ted.getTimeDuration() != null && ted.getTimeCycle() != null) {
                addError(event,
                         new ValidationSyntaxError(event,
                                                   BPMN2_TYPE,
                                                   errorPrefix + " Event has both timeduration and timecycle defined."));
            }

            if (ted.getTimeDate() == null && ted.getTimeDuration() == null && ted.getTimeCycle() == null) {
                addError(event,
                         new ValidationSyntaxError(event,
                                                   BPMN2_TYPE,
                                                   errorPrefix + " Event has no timeDate or timeDuration or timeCycle defined."));
            }
        } else if (eventDefinition instanceof SignalEventDefinition) {
            if (((SignalEventDefinition) eventDefinition).getSignalRef() == null) {
                addError(event,
                         new ValidationSyntaxError(event,
                                                   BPMN2_TYPE,
                                                   errorPrefix + " Event has no signalref."));
            }
        } else if (eventDefinition instanceof ErrorEventDefinition) {
            if (((ErrorEventDefinition) eventDefinition).getErrorRef() == null || ((ErrorEventDefinition) eventDefinition).getErrorRef().getErrorCode() == null) {
                addError(event,
                         new ValidationSyntaxError(event,
                                                   BPMN2_TYPE,
                                                   errorPrefix + SyntaxCheckerErrors.EVENT_HAS_NO_ERROR_REF));
            }
        } else if (eventDefinition instanceof ConditionalEventDefinition) {
            FormalExpression conditionalExp = (FormalExpression) ((ConditionalEventDefinition) eventDefinition).getCondition();
            if (conditionalExp.getBody() == null) {
                addError(event,
                         new ValidationSyntaxError(event,
                                                   BPMN2_TYPE,
                                                   errorPrefix + " Event has no condition expression."));
            }
        } else if (eventDefinition instanceof EscalationEventDefinition) {
            if (((EscalationEventDefinition) eventDefinition).getEscalationRef() == null) {
                addError(event,
                         new ValidationSyntaxError(event,
                                                   BPMN2_TYPE,
                                                   errorPrefix + " Event has no escalationref."));
            }
        } else if (eventDefinition instanceof MessageEventDefinition) {
            if (((MessageEventDefinition) eventDefinition).getMessageRef() == null) {
                addError(event,
                         new ValidationSyntaxError(event,
                                                   BPMN2_TYPE,
                                                   errorPrefix + " Event has no messageref."));
            }
        } else if (eventDefinition instanceof CompensateEventDefinition) {
            if (((CompensateEventDefinition) eventDefinition).getActivityRef() == null) {
                addError(event,
                         new ValidationSyntaxError(event,
                                                   BPMN2_TYPE,
                                                   errorPrefix + " Event has no activityref."));
            }
        }
    }

    private void checkGateway(Gateway gateway,
                              Scenario defaultScenario) {

        if (gateway.getGatewayDirection() == null || gateway.getGatewayDirection().getValue() == GatewayDirection.UNSPECIFIED.getValue()) {
            addError(gateway,
                     new ValidationSyntaxError(gateway,
                                               BPMN2_TYPE,
                                               "Gateway does not specify a valid direction."));
        }
        if (gateway instanceof ExclusiveGateway) {
            if (gateway.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue() && gateway.getGatewayDirection().getValue() != GatewayDirection.CONVERGING.getValue()) {
                addError(gateway,
                         new ValidationSyntaxError(gateway,
                                                   BPMN2_TYPE,
                                                   "Invalid Gateway direction for Exclusing Gateway. It should be 'Converging' or 'Diverging'."));
            }
            checkDefaultGate(gateway,
                             ((ExclusiveGateway) gateway).getDefault());
        }
        if (gateway instanceof EventBasedGateway) {
            if (gateway.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue()) {
                addError(gateway,
                         new ValidationSyntaxError(gateway,
                                                   BPMN2_TYPE,
                                                   "Invalid Gateway direction for EventBased Gateway. It should be 'Diverging'."));
            }
        }
        if (gateway instanceof ParallelGateway) {
            if (gateway.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue() && gateway.getGatewayDirection().getValue() != GatewayDirection.CONVERGING.getValue()) {
                addError(gateway,
                         new ValidationSyntaxError(gateway,
                                                   BPMN2_TYPE,
                                                   "Invalid Gateway direction for Parallel Gateway. It should be 'Converging' or 'Diverging'."));
            }
        }
        if (gateway instanceof InclusiveGateway) {
            if (gateway.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue()) {
                addError(gateway,
                         new ValidationSyntaxError(gateway,
                                                   BPMN2_TYPE,
                                                   "Invalid Gateway direction for Inclusive Gateway. It should be 'Diverging'."));
            }
            checkDefaultGate(gateway,
                             ((InclusiveGateway) gateway).getDefault());
        }
        if (gateway instanceof ComplexGateway) {
            if (gateway.getGatewayDirection().getValue() != GatewayDirection.DIVERGING.getValue() && gateway.getGatewayDirection().getValue() != GatewayDirection.CONVERGING.getValue()) {
                addError(gateway,
                         new ValidationSyntaxError(gateway,
                                                   BPMN2_TYPE,
                                                   "Invalid Gateway direction for Complex Gateway. It should be 'Converging' or 'Diverging'."));
            }
        }

        if ((gateway instanceof ExclusiveGateway || gateway instanceof InclusiveGateway) && (gateway.getGatewayDirection().getValue() == GatewayDirection.DIVERGING.getValue())) {
            List<SequenceFlow> outgoingFlows = gateway.getOutgoing();
            if (outgoingFlows != null && outgoingFlows.size() > 0) {
                for (SequenceFlow flow : outgoingFlows) {
                    if (flow.getConditionExpression() == null) {
                        addError(flow,
                                 new ValidationSyntaxError(flow,
                                                           BPMN2_TYPE,
                                                           SyntaxCheckerErrors.SEQUENCE_FLOW_NO_CONDITION_EXPRESSION_DEFINED));
                    } else {
                        if (flow.getConditionExpression() instanceof FormalExpression) {
                            FormalExpression formalExp = (FormalExpression) flow.getConditionExpression();
                            if (formalExp.getBody() == null && formalExp.getBody().isEmpty()) {
                                addError(flow,
                                         new ValidationSyntaxError(flow,
                                                                   BPMN2_TYPE,
                                                                   SyntaxCheckerErrors.SEQUENCE_FLOW_NO_CONDITION_EXPRESSION_DEFINED));
                            }
                        } else {
                            addError(flow,
                                     new ValidationSyntaxError(flow,
                                                               BPMN2_TYPE,
                                                               "Invalid condition expression on sequence flow."));
                        }
                    }
                }
            }
        }

        // simulation validation
        List<SequenceFlow> outgoingGwSequenceFlows = gateway.getOutgoing();
        if (outgoingGwSequenceFlows != null && outgoingGwSequenceFlows.size() > 0) {
            double probabilitySum = 0;
            boolean defaultSimulationOutgoingFlow = false;
            for (SequenceFlow sf : outgoingGwSequenceFlows) {
                for (ElementParameters eleType : getElementParameters(defaultScenario,
                                                                      sf)) {
                    if (eleType.getControlParameters() != null && eleType.getControlParameters().getProbability() != null) {
                        FloatingParameterType valType = (FloatingParameterType) eleType.getControlParameters().getProbability().getParameterValue().get(0);
                        if (valType.getValue() < 0) {
                            addError(sf,
                                     new ValidationSyntaxError(sf,
                                                               SIMULATION_TYPE,
                                                               SyntaxCheckerErrors.PROBABILITY_MUST_BE_POSITIVE));
                        } else {
                            if (valType.getValue() == 100) {
                                defaultSimulationOutgoingFlow = true;
                            }
                            probabilitySum += valType.getValue();
                        }
                    } else {
                        addError(sf,
                                 new ValidationSyntaxError(sf,
                                                           SIMULATION_TYPE,
                                                           SyntaxCheckerErrors.SEQUENCE_FLOW_NO_PROBABILITY_DEFINED));
                    }
                }
            }
            if (!(gateway instanceof ParallelGateway)) {
                if (gateway instanceof InclusiveGateway) {
                    if (!defaultSimulationOutgoingFlow) {
                        addError(gateway,
                                 new ValidationSyntaxError(gateway,
                                                           SIMULATION_TYPE,
                                                           SyntaxCheckerErrors.AT_LEAST_ONE_OUTGOING_PROBABILITY_VALUE_100));
                    }
                } else {
                    if (probabilitySum != 100) {
                        addError(gateway,
                                 new ValidationSyntaxError(gateway,
                                                           SIMULATION_TYPE,
                                                           SyntaxCheckerErrors.THE_SUM_OF_PROBABILITIES_MUST_BE_EQUAL_100));
                    }
                }
            }
        }
    }

    private void checkSequenceFlow(SequenceFlow sequenceFlow) {
        if (sequenceFlow.getSourceRef() == null) {
            addError(sequenceFlow,
                     new ValidationSyntaxError(sequenceFlow,
                                               BPMN2_TYPE,
                                               "An Edge must have a source node."));
        }
        if (sequenceFlow.getTargetRef() == null) {
            addError(sequenceFlow,
                     new ValidationSyntaxError(sequenceFlow,
                                               BPMN2_TYPE,
                                               "An Edge must have a target node."));
        }
    }

    private void checkCallActivity(CallActivity callActivity) {

        if (callActivity.getCalledElement() == null || callActivity.getCalledElement().isEmpty()) {
            addError(callActivity,
                     new ValidationSyntaxError(callActivity,
                                               BPMN2_TYPE,
                                               SyntaxCheckerErrors.NO_CALLED_ELEMENT_SPECIFIED));
        }
    }

    private void checkDataObject(DataObject dataObject) {
        if (dataObject.getName() == null || dataObject.getName().isEmpty()) {
            addError(dataObject,
                     new ValidationSyntaxError(dataObject,
                                               BPMN2_TYPE,
                                               "Data Object has no name defined."));
        } else {
            if (containsWhiteSpace(dataObject.getName())) {
                addError(dataObject,
                         new ValidationSyntaxError(dataObject,
                                                   BPMN2_TYPE,
                                                   "Data Object name contains white spaces."));
            }
        }
    }

    private void checkDefaultGate(Gateway gateway,
                                  SequenceFlow defaultSequenceFlow) {
        if (defaultSequenceFlow != null && (gateway.getOutgoing() != null && !gateway.getOutgoing().contains(defaultSequenceFlow))) {
            addError(gateway,
                     new ValidationSyntaxError(gateway,
                                               BPMN2_TYPE,
                                               SyntaxCheckerErrors.NOT_VALID_DEFAULT_GATE));
        }
    }

    public Map<String, List<ValidationSyntaxError>> getErrors() {
        return errors;
    }

    public JSONObject getErrorsAsJson() {
        JSONObject jsonObject = new JSONObject();
        for (Entry<String, List<ValidationSyntaxError>> error : this.getErrors().entrySet()) {
            try {
                JSONArray errorsArray = new JSONArray();
                for (ValidationSyntaxError se : error.getValue()) {
                    errorsArray.put(se.toJSON());
                }
                jsonObject.put(error.getKey(),
                               errorsArray);
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

    private void addError(BaseElement element,
                          ValidationSyntaxError error) {
        addError(element.getId(),
                 error);
    }

    private void addError(String resourceId,
                          ValidationSyntaxError error) {
        if (errors.containsKey(resourceId) && errors.get(resourceId) != null) {
            errors.get(resourceId).add(error);
        } else {
            List<ValidationSyntaxError> value = new ArrayList<ValidationSyntaxError>();
            value.add(error);
            errors.put(resourceId,
                       value);
        }
    }

    private static boolean isEmpty(final CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        for (int i = 0, length = str.length(); i < length; i++) {
            if (str.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    private boolean isAdHocProcess(Process process) {
        Iterator<FeatureMap.Entry> iter = process.getAnyAttribute().iterator();
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (entry.getEStructuralFeature().getName().equals("adHoc")) {
                return Boolean.parseBoolean(((String) entry.getValue()).trim());
            }
        }
        return false;
    }

    public boolean isCompensatingFlowNodeInSubprocess(FlowNode node,
                                                      SubProcess subProcess) {
        //text annotations are flow elements now not artifacts so omit them
        if (node instanceof TextAnnotation) {
            return true;
        }

        for (Artifact artifact : subProcess.getArtifacts()) {
            if (artifact instanceof Association) {
                Association association = (Association) artifact;
                if (association.getTargetRef().getId().equals(node.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCompensatingFlowNodeInProcess(FlowNode node,
                                                   Process process) {
        for (Artifact artifact : process.getArtifacts()) {
            if (artifact instanceof Association) {
                Association association = (Association) artifact;
                if (association.getTargetRef().getId().equals(node.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean containsWhiteSpace(String testString) {
        if (testString != null) {
            for (int i = 0; i < testString.length(); i++) {
                if (Character.isWhitespace(testString.charAt(i))) {
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
        if (def.getRelationships() != null && def.getRelationships().size() > 0) {
            // current support for single relationship
            Relationship relationship = def.getRelationships().get(0);
            for (ExtensionAttributeValue extattrval : relationship.getExtensionValues()) {
                FeatureMap extensionElements = extattrval.getValue();
                @SuppressWarnings("unchecked")
                List<BPSimDataType> bpsimExtensions = (List<BPSimDataType>) extensionElements.get(BpsimPackage.Literals.DOCUMENT_ROOT__BP_SIM_DATA,
                                                                                                  true);
                if (bpsimExtensions != null && bpsimExtensions.size() > 0) {
                    BPSimDataType processAnalysis = bpsimExtensions.get(0);
                    if (processAnalysis.getScenario() != null && processAnalysis.getScenario().size() > 0) {
                        return processAnalysis.getScenario().get(0);
                    }
                }
            }
        }
        return null;
    }

    private List<ElementParameters> getElementParameters(Scenario scenario,
                                                         BaseElement element) {
        List<ElementParameters> elementParameters = new ArrayList<>();
        if (scenario != null && scenario.getElementParameters() != null) {
            for (ElementParameters eleType : scenario.getElementParameters()) {
                if (eleType.getElementRef().equals(element.getId())) {
                    elementParameters.add(eleType);
                }
            }
        }
        return elementParameters;
    }

    public class ValidationSyntaxError {

        private BaseElement element;
        private String error;
        private String type;

        public ValidationSyntaxError(BaseElement element,
                                     String type,
                                     String error) {
            this.element = element;
            this.error = error;
            this.type = type;
        }

        public JSONObject toJSON() throws JSONException {
            JSONObject errorJSON = new JSONObject();
            errorJSON.put("id",
                          this.element == null ? "" : this.element.getId());
            errorJSON.put("type",
                          type);
            errorJSON.put("error",
                          this.error);

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
