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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.TextAnnotation;
import org.jbpm.designer.bpmn2.impl.Bpmn2JsonMarshallerTest;
import org.jbpm.designer.bpmn2.utils.Bpmn2Loader;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BPMN2SyntaxCheckerTest {

    private Bpmn2Loader loader = new Bpmn2Loader(BPMN2SyntaxCheckerTest.class);

    private Map<String, List<BPMN2SyntaxChecker.ValidationSyntaxError>> errors;

    @Before
    public void setup() {
        loader.getProfile().setBpsimDisplay("true");
    }

    @Test
    public void testUserTaskWithTaskName() throws Exception {
        loader.loadProcessFromXml("userTaskWithTaskName.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testUserTaskWithoutTaskName() throws Exception {
        JSONObject process = loader.loadProcessFromXml("userTaskWithoutTaskName.bpmn2");
        JSONObject userTask = loader.getChildByName(process,
                                                    "User Task");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              userTask,
                              Arrays.asList(SyntaxCheckerErrors.USER_TASK_HAS_NO_TASK_NAME));
    }

    @Test
    public void testNoCalledElementCallActivity() throws Exception {
        JSONObject process = loader.loadProcessFromXml("noCalledElementCallActivity.bpmn2");
        JSONObject callActivity = loader.getChildByName(process,
                                                        "callActivity");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              callActivity,
                              Arrays.asList(SyntaxCheckerErrors.NO_CALLED_ELEMENT_SPECIFIED));
    }

    @Test
    public void testCallActivity() throws Exception {
        loader.loadProcessFromXml("callActivity.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testInclusiveGatewayWithDefaultSimulationPath() throws Exception {
        loader.loadProcessFromXml("inclusiveGatewayWithDefaultSimulationPath.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testInclusiveGatewayWithoutDefaultSimulationPath() throws Exception {
        JSONObject process = loader.loadProcessFromXml("inclusiveGatewayWithoutDefaultSimulationPath.bpmn2");
        JSONObject gateway = loader.getChildByName(process,
                                                   "inclusiveGateway");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              gateway,
                              Arrays.asList(SyntaxCheckerErrors.AT_LEAST_ONE_OUTGOING_PROBABILITY_VALUE_100));
    }

    @Test
    public void testInclusiveGatewayInvalidDefaultGate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("inclusiveGatewayInvalidDefaultGate.bpmn2");
        JSONObject gateway = loader.getChildByName(process,
                                                   "inclusiveGateway");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              gateway,
                              Arrays.asList(SyntaxCheckerErrors.NOT_VALID_DEFAULT_GATE));
    }

    @Test
    public void testEventGatewayProperProbabilities() throws Exception {
        loader.loadProcessFromXml("eventGatewayProperProbabilities.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testEventGatewayInproperProbabilities() throws Exception {
        JSONObject process = loader.loadProcessFromXml("eventGatewayInproperProbabilities.bpmn2");
        JSONObject gateway = loader.getChildByName(process,
                                                   "eventGateway");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              gateway,
                              Arrays.asList(SyntaxCheckerErrors.THE_SUM_OF_PROBABILITIES_MUST_BE_EQUAL_100));
    }

    @Test
    public void testExclusiveGatewayProperProbabilities() throws Exception {
        loader.loadProcessFromXml("exclusiveGatewayProperProbabilities.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testExclusiveGatewayInproperProbabilities() throws Exception {
        JSONObject process = loader.loadProcessFromXml("exclusiveGatewayInproperProbabilities.bpmn2");
        JSONObject gateway = loader.getChildByName(process,
                                                   "exclusiveGateway");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              gateway,
                              Arrays.asList(SyntaxCheckerErrors.THE_SUM_OF_PROBABILITIES_MUST_BE_EQUAL_100));
    }

    @Test
    public void testExclusiveGatewayInvalidDefaultGate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("exclusiveGatewayInvalidDefaultGate.bpmn2");
        JSONObject gateway = loader.getChildByName(process,
                                                   "exclusiveGateway");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              gateway,
                              Arrays.asList(SyntaxCheckerErrors.NOT_VALID_DEFAULT_GATE));
    }

    @Test
    public void testParallelGatewayProbabilities() throws Exception {
        loader.loadProcessFromXml("parallelGatewayProbabilities.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testScriptTaskNoScriptNoLanguage() throws Exception {
        JSONObject process = loader.loadProcessFromXml("scriptTaskNoScriptNoLanguage.bpmn2");
        JSONObject task = loader.getChildByName(process,
                                                "scriptTask");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              task,
                              Arrays.asList(SyntaxCheckerErrors.SCRIPT_TASK_NO_SCRIPT,
                                            SyntaxCheckerErrors.SCRIPT_TASK_NO_SCRIPT_FORMAT));
    }

    @Test
    public void testScriptTaskNoScript() throws Exception {
        JSONObject process = loader.loadProcessFromXml("scriptTaskNoScript.bpmn2");
        JSONObject task = loader.getChildByName(process,
                                                "scriptTask");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              task,
                              Arrays.asList(SyntaxCheckerErrors.SCRIPT_TASK_NO_SCRIPT));
    }

    @Test
    public void testScriptTaskNoLanguage() throws Exception {
        JSONObject process = loader.loadProcessFromXml("scriptTaskNoLanguage.bpmn2");
        JSONObject task = loader.getChildByName(process,
                                                "scriptTask");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              task,
                              Arrays.asList(SyntaxCheckerErrors.SCRIPT_TASK_NO_SCRIPT_FORMAT));
    }

    @Test
    public void testScriptTask() throws Exception {
        loader.loadProcessFromXml("scriptTask.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testErrorBoundaryEvent() throws Exception {
        loader.loadProcessFromXml("errorBoundaryEvent.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testErrorBoundaryEventMissingDefinition() throws Exception {
        JSONObject process = loader.loadProcessFromXml("errorBoundaryEventMissingDefinition.bpmn2");
        JSONObject lane = loader.getChildByName(process,
                                                "myLane");
        JSONObject error = loader.getChildByName(lane,
                                                 "MyError");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              error,
                              Arrays.asList("Catch" + SyntaxCheckerErrors.EVENT_HAS_NO_ERROR_REF));
    }

    @Test
    public void testServiceTaskInterfaceAndOperation() throws Exception {
        JSONObject process = loader.loadProcessFromXml("serviceTaskInterfaceAndOperation.bpmn2",
                                                       Bpmn2JsonMarshallerTest.class,
                                                       null);
        JSONObject serviceTask = loader.getChildByName(process,
                                                       "Send PO");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        syntaxChecker.checkSyntax();
        assertTrue(syntaxChecker.errorsFound());
        errors = syntaxChecker.getErrors();
        String serviceTaskId = serviceTask.getString("resourceId");
        assertFalse(errors.containsKey(serviceTaskId));
    }

    @Test
    public void testServiceTaskNoInterfaceNoOperation() throws Exception {
        JSONObject process = loader.loadProcessFromXml("serviceTaskNoInterfaceNoOperation.bpmn2",
                                                       Bpmn2JsonMarshallerTest.class,
                                                       null);
        JSONObject serviceTask = loader.getChildByName(process,
                                                       "Send PO");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              serviceTask,
                              Arrays.asList(SyntaxCheckerErrors.SERVICE_TASK_NO_OPERATION));
    }

    @Test
    public void testEmptyBusinessRule() throws Exception {
        JSONObject process = loader.loadProcessFromXml("emptyBusinessRule.bpmn2");
        JSONObject ruleTask = loader.getChildByName(process,
                                                    "businessRuleTask");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              ruleTask,
                              Arrays.asList(SyntaxCheckerErrors.BUSINESS_RULE_TASK_NO_RULEFLOW_GROUP));
    }

    @Test
    public void testBusinessRule() throws Exception {
        loader.loadProcessFromXml("businessRule.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testGenericTask() throws Exception {
        JSONObject process = loader.loadProcessFromXml("genericTask.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              process.getJSONObject("properties").getString("id"),
                              Arrays.asList("Node 'generic task' [2] Task has no task type."));
    }

    @Test
    public void testIsCompensatingFlowNodeInSubprocessForTextAnnotation() throws Exception {
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker("",
                                                                  "",
                                                                  null);

        SubProcess subprocess = Bpmn2Factory.eINSTANCE.createSubProcess();
        TextAnnotation textAnnotation = Bpmn2Factory.eINSTANCE.createTextAnnotation();

        subprocess.getFlowElements().add(textAnnotation);

        assertTrue(syntaxChecker.isCompensatingFlowNodeInSubprocess(textAnnotation,
                                                                    subprocess));
    }

    @Test
    public void testReceiveTask() throws Exception {
        loader.loadProcessFromXml("receiveTask.bpmn2",
                                  Bpmn2JsonMarshallerTest.class,
                                  null);
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testReceiveTaskMissingMessage() throws Exception {
        JSONObject process = loader.loadProcessFromXml("receiveTaskMissingMessage.bpmn2");
        JSONObject receiveTask = loader.getChildByName(process,
                                                       "receiveTask");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              receiveTask,
                              Arrays.asList(SyntaxCheckerErrors.TASK_NO_MESSAGE));
    }

    @Test
    public void testSendTask() throws Exception {
        loader.loadProcessFromXml("sendTask.bpmn2",
                                  Bpmn2JsonMarshallerTest.class,
                                  null);
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testSendTaskMissingMessage() throws Exception {
        JSONObject process = loader.loadProcessFromXml("sendTaskMissingMessage.bpmn2");
        JSONObject sendTask = loader.getChildByName(process,
                                                    "sendTask");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              sendTask,
                              Arrays.asList(SyntaxCheckerErrors.TASK_NO_MESSAGE));
    }

    @Test
    public void testMultipleSubprocessStartEvents() throws Exception {
        JSONObject process = loader.loadProcessFromXml("multipleSubprocessStartEvents.bpmn2");
        JSONObject subProcess = loader.getChildByName(process,
                                                      "SubProcess");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              subProcess,
                              Arrays.asList(SyntaxCheckerErrors.MULTIPLE_START_EVENTS));
    }

    @Test
    public void testEmptyDMNBusinessRule() throws Exception {
        JSONObject process = loader.loadProcessFromXml("emptyDmnBusinessRule.bpmn2",
                                                       BPMN2SyntaxCheckerTest.class,
                                                       null);
        JSONObject ruleTask = loader.getChildByName(process,
                                                    "test");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyErrorsOfElement(syntaxChecker,
                              ruleTask,
                              Arrays.asList(SyntaxCheckerErrors.DMN_BUSINESS_RULE_TASK_NO_NAMESPACE,
                                            SyntaxCheckerErrors.DMN_BUSINESS_RULE_TASK_NO_MODEL));
    }

    @Test
    public void testDMNBusinessRule() throws Exception {
        loader.loadProcessFromXml("dmnBusinessRule.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testEmptyPackageName() throws Exception {
        loader.loadProcessFromXml("emptyPackageName.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        verifyNoErrors(syntaxChecker);
    }

    @Test
    public void testInvalidPackageName() throws Exception {
        loader.loadProcessFromXml("invalidPackageName.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson,
                                                                  "",
                                                                  loader.getProfile());
        syntaxChecker.checkSyntax();
        Map<String, List<BPMN2SyntaxChecker.ValidationSyntaxError>> errors = syntaxChecker.getErrors();
        assertEquals(1,
                     errors.size());

        assertEquals(1,
                     errors.get("emptypackagename").size());
        BPMN2SyntaxChecker.ValidationSyntaxError packageError = errors.get("emptypackagename").get(0);

        assertEquals(SyntaxCheckerErrors.PACKAGE_NAME_CONTAINS_INVALID_CHARACTERS,
                     packageError.getError());
    }

    private void verifyNoErrors(SyntaxChecker syntaxChecker) throws Exception {
        syntaxChecker.checkSyntax();
        assertFalse(syntaxChecker.errorsFound());
        assertEquals(0,
                     syntaxChecker.getErrors().size());
    }

    private void verifyErrorsOfElement(SyntaxChecker syntaxChecker,
                                       JSONObject element,
                                       List<String> elementErrors) throws Exception {
        verifyErrorsOfElement(syntaxChecker,
                              element.getString("resourceId"),
                              elementErrors);
    }

    private void verifyErrorsOfElement(SyntaxChecker syntaxChecker,
                                       String elementId,
                                       List<String> elementErrors) throws Exception {
        syntaxChecker.checkSyntax();
        assertTrue(syntaxChecker.errorsFound());
        errors = syntaxChecker.getErrors();
        assertEquals(elementErrors.size(),
                     errors.get(elementId).size());
        int i = 0;
        for (String error : elementErrors) {
            assertEquals(error,
                         errors.get(elementId).get(i).getError());
            i++;
        }
    }
}
