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

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.TextAnnotation;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BPMN2SyntaxCheckerTest {

    @Test
    public void testNoCalledElementCallActivity() throws Exception {
        JSONObject process = loader.loadProcessFromXml("noCalledElementCallActivity.bpmn2");
        JSONObject callActivity = loader.getChildByName(process, "callActivity");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertTrue(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(1, errors.size());
        String callActivityId = callActivity.getString("resourceId");
        assertTrue(errors.keySet().contains(callActivityId));
        assertEquals(1, errors.get(callActivityId).size());
        assertEquals(SyntaxCheckerErrors.NO_CALLED_ELEMENT_SPECIFIED, errors.get(callActivityId).get(0).getError());
    }

    @Test
    public void testCallActivity() throws Exception {
        loader.loadProcessFromXml("callActivity.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertFalse(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void testInclusiveGatewayWithDefaultSimulationPath() throws Exception {
        loader.loadProcessFromXml("inclusiveGatewayWithDefaultSimulationPath.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertFalse(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void testInclusiveGatewayWithoutDefaultSimulationPath() throws Exception {
        JSONObject process = loader.loadProcessFromXml("inclusiveGatewayWithoutDefaultSimulationPath.bpmn2");
        JSONObject gateway = loader.getChildByName(process, "inclusiveGateway");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertTrue(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(1, errors.size());
        String gatewayId = gateway.getString("resourceId");
        assertEquals(1, errors.get(gatewayId).size());
        assertEquals(SyntaxCheckerErrors.AT_LEAST_ONE_OUTGOING_PROBABILITY_VALUE_100, errors.get(gatewayId).get(0).getError());
    }

    @Test
    public void testInclusiveGatewayInvalidDefaultGate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("inclusiveGatewayInvalidDefaultGate.bpmn2");
        JSONObject gateway = loader.getChildByName(process, "inclusiveGateway");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertTrue(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(1, errors.size());
        String gatewayId = gateway.getString("resourceId");
        assertEquals(1, errors.get(gatewayId).size());
        assertEquals(SyntaxCheckerErrors.NOT_VALID_DEFAULT_GATE, errors.get(gatewayId).get(0).getError());
    }

    @Test
    public void testEventGatewayProperProbabilities() throws Exception {
        loader.loadProcessFromXml("eventGatewayProperProbabilities.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertFalse(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void testEventGatewayInproperProbabilities() throws Exception {
        JSONObject process = loader.loadProcessFromXml("eventGatewayInproperProbabilities.bpmn2");
        JSONObject gateway = loader.getChildByName(process, "eventGateway");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertTrue(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(1, errors.size());
        String gatewayId = gateway.getString("resourceId");
        assertEquals(1, errors.get(gatewayId).size());
        assertEquals(SyntaxCheckerErrors.THE_SUM_OF_PROBABILITIES_MUST_BE_EQUAL_100, errors.get(gatewayId).get(0).getError());
    }

    @Test
    public void testExclusiveGatewayProperProbabilities() throws Exception {
        loader.loadProcessFromXml("exclusiveGatewayProperProbabilities.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertFalse(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void testExclusiveGatewayInproperProbabilities() throws Exception {
        JSONObject process = loader.loadProcessFromXml("exclusiveGatewayInproperProbabilities.bpmn2");
        JSONObject gateway = loader.getChildByName(process, "exclusiveGateway");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertTrue(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(1, errors.size());
        String gatewayId = gateway.getString("resourceId");
        assertEquals(1, errors.get(gatewayId).size());
        assertEquals(SyntaxCheckerErrors.THE_SUM_OF_PROBABILITIES_MUST_BE_EQUAL_100, errors.get(gatewayId).get(0).getError());
    }

    @Test
    public void testExclusiveGatewayInvalidDefaultGate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("exclusiveGatewayInvalidDefaultGate.bpmn2");
        JSONObject gateway = loader.getChildByName(process, "exclusiveGateway");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertTrue(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(1, errors.size());
        String gatewayId = gateway.getString("resourceId");
        assertEquals(1, errors.get(gatewayId).size());
        assertEquals(SyntaxCheckerErrors.NOT_VALID_DEFAULT_GATE, errors.get(gatewayId).get(0).getError());
    }

    @Test
    public void testParallelGatewayProbabilities() throws Exception {
        loader.loadProcessFromXml("parallelGatewayProbabilities.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertFalse(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void testScriptTaskNoScriptNoLanguage() throws Exception {
        JSONObject process = loader.loadProcessFromXml("scriptTaskNoScriptNoLanguage.bpmn2");
        JSONObject task = loader.getChildByName(process, "scriptTask");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertTrue(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(1, errors.size());
        String taskId = task.getString("resourceId");
        assertEquals(2, errors.get(taskId).size());
        assertEquals(SyntaxCheckerErrors.SCRIPT_TASK_NO_SCRIPT, errors.get(taskId).get(0).getError());
        assertEquals(SyntaxCheckerErrors.SCRIPT_TASK_NO_SCRIPT_FORMAT, errors.get(taskId).get(1).getError());
    }

    @Test
    public void testScriptTaskNoScript() throws Exception {
        JSONObject process = loader.loadProcessFromXml("scriptTaskNoScript.bpmn2");
        JSONObject task = loader.getChildByName(process, "scriptTask");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertTrue(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(1, errors.size());
        String taskId = task.getString("resourceId");
        assertEquals(1, errors.get(taskId).size());
        assertEquals(SyntaxCheckerErrors.SCRIPT_TASK_NO_SCRIPT, errors.get(taskId).get(0).getError());
    }

    @Test
    public void testScriptTaskNoLanguage() throws Exception {
        JSONObject process = loader.loadProcessFromXml("scriptTaskNoLanguage.bpmn2");
        JSONObject task = loader.getChildByName(process, "scriptTask");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertTrue(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(1, errors.size());
        String taskId = task.getString("resourceId");
        assertEquals(1, errors.get(taskId).size());
        assertEquals(SyntaxCheckerErrors.SCRIPT_TASK_NO_SCRIPT_FORMAT, errors.get(taskId).get(0).getError());
    }

    @Test
    public void testScriptTask() throws Exception {
        loader.loadProcessFromXml("scriptTask.bpmn2");
        String processJson = loader.getProcessJson();
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker(processJson, "", loader.getProfile());
        syntaxChecker.checkSyntax();
        assertFalse(syntaxChecker.errorsFound());
        errors  = syntaxChecker.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void testIsCompensatingFlowNodeInSubprocessForTextAnnotation() throws Exception {
        BPMN2SyntaxChecker syntaxChecker = new BPMN2SyntaxChecker("", "", null);

        SubProcess subprocess = Bpmn2Factory.eINSTANCE.createSubProcess();
        TextAnnotation textAnnotation = Bpmn2Factory.eINSTANCE.createTextAnnotation();

        subprocess.getFlowElements().add(textAnnotation);

        assertTrue(syntaxChecker.isCompensatingFlowNodeInSubprocess(textAnnotation, subprocess));
    }
}
