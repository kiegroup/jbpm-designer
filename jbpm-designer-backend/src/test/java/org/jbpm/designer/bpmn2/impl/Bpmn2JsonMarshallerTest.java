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

package org.jbpm.designer.bpmn2.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jbpm.designer.bpmn2.BpmnMarshallerHelper;
import org.jbpm.designer.bpmn2.impl.helpers.SimpleEdge;
import org.jbpm.designer.bpmn2.utils.Bpmn2Loader;
import org.jbpm.designer.bpmn2.validation.BPMN2SyntaxCheckerTest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.jbpm.designer.bpmn2.impl.helpers.SimpleEdge.createEdge;
import static org.jbpm.designer.bpmn2.utils.Bpmn2Loader.getChildByName;
import static org.junit.Assert.*;

public class Bpmn2JsonMarshallerTest {

    private static final String COST_PER_TIME_UNIT = "unitcost";
    private static final String PROCESSING_TIME_MAX = "max";
    private static final String PROCESSING_TIME_MIN = "min";
    private static final String PROCESSING_TIME_MEAN = "mean";
    private static final String PROBABILITY = "probability";
    private static final String WORKING_HOURS = "workinghours";
    private static final String QUANTITY = "quantity";
    private static final String STANDARD_DEVIATION = "standarddeviation";
    private static final String DISTRIBUTION_TYPE = "distributiontype";

    private Bpmn2Loader loader = new Bpmn2Loader(Bpmn2JsonMarshallerTest.class);

    @Test
    public void testGroupMarshalling() throws Exception {
        JSONObject process = loader.loadProcessFromXml("group.bpmn2");
        JSONObject group = getChildByName(process,
                                          "group");

        assertNotNull("Group with name 'group' not found in process.",
                      group);
        assertEquals("Group has wrong documentation.",
                     "group documentation",
                     loader.getDocumentationFor(group));
    }

    @Test
    public void testBoundaryEventDocumentation() throws Exception {
        JSONObject process = loader.loadProcessFromXml("boundaryEventsDocumentation.bpmn2");
        JSONObject boundaryEvent = getChildByName(process,
                                                  "CancelOnTimer");

        assertNotNull("BoundaryEvent with name 'CancelOnTimer' not found in process.",
                      boundaryEvent);
        assertEquals("BoundaryEvent has wrong documentation.",
                     "Cancel task on timeout.",
                     loader.getDocumentationFor(boundaryEvent));
    }

    @Test
    public void testSwimlaneDocumentation() throws Exception {
        JSONObject process = loader.loadProcessFromXml("swimlane.bpmn2");
        JSONObject swimlane = getChildByName(process,
                                             "Documented Swimlane");

        assertNotNull("Swimlane with name 'Documented Swimlane' not found in process.",
                      swimlane);
        assertEquals("Swimlane has wrong documentation.",
                     "Some documentation for swimlane.",
                     loader.getDocumentationFor(swimlane));
    }

    @Test
    public void testConstraint() throws Exception {
        JSONObject process = loader.loadProcessFromXml("constraint.bpmn2");
        JSONObject condition1 = getChildByName(process,
                                               "Condition1");
        JSONObject condition2 = getChildByName(process,
                                               "Condition2");
        JSONObject condition1Properties = condition1.getJSONObject("properties");
        JSONObject condition2Properties = condition2.getJSONObject("properties");
        assertEquals("return  KieFunctions.endsWith(customVar, \"sample\");",
                     condition1Properties.getString("conditionexpression"));
        assertEquals("return  !KieFunctions.isNull(intVar);",
                     condition2Properties.getString("conditionexpression"));
    }

    @Test
    public void testMultiLineNames() throws Exception {
        JSONObject process = loader.loadProcessFromXml("multiLineNames.bpmn2");

        // Lane elements
        JSONObject lane = Bpmn2Loader.getChildByName(process,
                                                     "my\nlane");
        assertNotNull(lane);
        assertNotNull(Bpmn2Loader.getChildByName(lane,
                                                 "my\nuser\ntask"));
        assertNotNull(Bpmn2Loader.getChildByName(lane,
                                                 "my\nmessage"));
        JSONObject adhocSubprocess = Bpmn2Loader.getChildByName(lane,
                                                                "my\nadhoc\nsubprocess");

        assertNotNull(adhocSubprocess);
        assertNotNull(Bpmn2Loader.getChildByName(adhocSubprocess,
                                                 "my\ntask\nin\nadhoc"));
        assertNotNull(Bpmn2Loader.getChildByName(adhocSubprocess,
                                                 "my\nmessage\nin\nsubprocess\nin\nlane"));

        // Other elements in process
        assertNotNull(Bpmn2Loader.getChildByName(process,
                                                 "my\nstart"));
        assertNotNull(Bpmn2Loader.getChildByName(process,
                                                 "my\nflow\nin\nlane"));
        assertNotNull(Bpmn2Loader.getChildByName(process,
                                                 "my\ngate"));
        assertNotNull(Bpmn2Loader.getChildByName(process,
                                                 "my\nterminate\nend"));
        assertNotNull(Bpmn2Loader.getChildByName(process,
                                                 "my\nthrowing\nmessage"));
        assertNotNull(Bpmn2Loader.getChildByName(process,
                                                 "my\nend"));
        assertNotNull(Bpmn2Loader.getChildByName(process,
                                                 "my\nflow"));

        // Embedded subprocess elements
        JSONObject embeddedSubprocess = Bpmn2Loader.getChildByName(process,
                                                                   "my\nsubprocess");
        assertNotNull(embeddedSubprocess);
        assertNotNull(Bpmn2Loader.getChildByName(embeddedSubprocess,
                                                 "my\nmessage\nstart"));
        assertNotNull(Bpmn2Loader.getChildByName(embeddedSubprocess,
                                                 "my\ninner\nend"));
        assertNotNull(Bpmn2Loader.getChildByName(embeddedSubprocess,
                                                 "my\nflow\nin\nsubprocess"));
        assertNotNull(Bpmn2Loader.getChildByName(embeddedSubprocess,
                                                 "my\nmanual\ntask"));
        assertNotNull(Bpmn2Loader.getChildByName(embeddedSubprocess,
                                                 "my\nescalation\nevent"));
    }

    @Test
    public void testTextAnnotationEdges() throws Exception {
        List<SimpleEdge> expectedEdges = new ArrayList<SimpleEdge>();
        expectedEdges.add(createEdge("Start\nAnnotation")
                                  .addPoint(15,
                                            15)
                                  .addPoint(1,
                                            25)
        );
        expectedEdges.add(createEdge("Task\nIn\nLane\nAnnotation")
                                  .addPoint(50,
                                            40)
                                  .addPoint(380,
                                            184)
                                  .addPoint(331,
                                            184)
                                  .addPoint(1,
                                            25)
        );
        expectedEdges.add(createEdge("WID\nTask\nannotation")
                                  .addPoint(50,
                                            40)
                                  .addPoint(690,
                                            507)
                                  .addPoint(1,
                                            25)
        );
        expectedEdges.add(createEdge("User\nTask\nAnnotation")
                                  .addPoint(50,
                                            40)
                                  .addPoint(196,
                                            646)
                                  .addPoint(1,
                                            25)
        );
        expectedEdges.add(createEdge("Gateway\nin\nlane\nannotation")
                                  .addPoint(20,
                                            20)
                                  .addPoint(1,
                                            25)
        );
        expectedEdges.add(createEdge("End\nIn\nSwimlane")
                                  .addPoint(14,
                                            14)
                                  .addPoint(915,
                                            231)
                                  .addPoint(1,
                                            25)
        );
        expectedEdges.add(createEdge("Subprocess's\nAnnotation")
                                  .addPoint(100,
                                            80)
                                  .addPoint(495,
                                            847)
                                  .addPoint(1,
                                            25)
        );
        expectedEdges.add(createEdge("Swimlane's\nAnnotation")
                                  .addPoint(300,
                                            125)
                                  .addPoint(1066,
                                            320)
                                  .addPoint(1,
                                            25)
        );

        JSONObject process = loader.loadProcessFromXml("textAnnotation.bpmn2");
        List<JSONObject> edges = Bpmn2Loader.getChildByTypeName(process,
                                                                "Association_Undirected");
        List<JSONObject> annotations = Bpmn2Loader.getChildByTypeName(process,
                                                                      "TextAnnotation");
        annotations.addAll(Bpmn2Loader.getChildByTypeName(getChildByName(process,
                                                                         "swimlane"),
                                                          "TextAnnotation"));
        List<SimpleEdge> actualEdges = new ArrayList<SimpleEdge>();
        for (JSONObject edge : edges) {
            SimpleEdge current = null;

            String associatedWith = ((JSONObject) edge.getJSONArray("outgoing").get(0)).getString("resourceId");
            for (JSONObject annotation : annotations) {
                if (Bpmn2Loader.getResourceId(annotation).equals(associatedWith)) {
                    current = createEdge(Bpmn2Loader.getNameFor(annotation));
                }
            }

            JSONArray dockers = edge.getJSONArray("dockers");
            for (int i = 0; i < dockers.length(); i++) {
                JSONObject dockerPoint = (JSONObject) dockers.get(i);
                current.addPoint(Float.parseFloat(dockerPoint.getString("x")),
                                 Float.parseFloat(dockerPoint.getString("y")));
            }
            actualEdges.add(current);
        }

        assertEquals(expectedEdges,
                     actualEdges);
    }

    @Test
    public void testTextAnnotationLink() throws Exception {
        JSONObject process = loader.loadProcessFromXml("textAnnotation.bpmn2");
        assertEquals(2,
                     getChildByName(process,
                                    "Start").getJSONArray("outgoing").length());
        assertEquals(2,
                     getChildByName(process,
                                    "ServiceTask").getJSONArray("outgoing").length());
        assertEquals(0,
                     getChildByName(process,
                                    "End2").getJSONArray("outgoing").length());

        JSONObject swimlane = getChildByName(process,
                                             "swimlane");
        assertEquals(1,
                     swimlane.getJSONArray("outgoing").length());
        assertEquals(2,
                     getChildByName(swimlane,
                                    "ManualTask").getJSONArray("outgoing").length());
        assertEquals(3,
                     getChildByName(swimlane,
                                    "Gateway").getJSONArray("outgoing").length());
        assertEquals(1,
                     getChildByName(swimlane,
                                    "End1").getJSONArray("outgoing").length());

        JSONObject subprocess = getChildByName(process,
                                               "Subprocess");
        assertEquals(2,
                     subprocess.getJSONArray("outgoing").length());
        assertEquals(1,
                     getChildByName(subprocess,
                                    "Task_1").getJSONArray("outgoing").length());
    }

    @Test
    public void testSendTaskDataInputs() throws Exception {
        String[] variableNames = {"Comment", "Content", "CreatedBy", "GroupId", "Locale",
                "NotCompletedNotify", "NotCompletedReassign", "NotStartedNotify", "NotStartedReassign",
                "Priority", "Skippable", "TaskName", "MyDataInput1", "MyDataInput2"};

        JSONObject process = loader.loadProcessFromXml("nonusertaskdatainputs.bpmn2");
        JSONObject sendtask = getChildByName(process,
                                             "MySendTask");
        JSONObject properties = sendtask.getJSONObject("properties");
        String datainputset = properties.getString("datainputset");
        for (String variableName : variableNames) {
            String dataInput = variableName + ":String";
            assertTrue("Variable \"" + variableName + "\" not found in datainputset",
                       datainputset.contains(dataInput));
        }

        String assignments = properties.getString("assignments");
        for (String variableName : variableNames) {
            String assignment = "[din]" + variableName + "=a" + variableName;
            assertTrue("Assignment \"" + assignment + "\" not found in assignments",
                       assignments.contains(assignment));
        }
    }

    @Test
    public void testCustomTaskTaskName() throws Exception {
        String[] existingVariableNames = {"Message", "DataInput1"};

        String[] nonExistingVariableNames = {"TaskName"};

        JSONObject process = loader.loadProcessFromXml("customtasktaskname.bpmn2");
        JSONObject logtask = getChildByName(process,
                                            "Log");
        JSONObject properties = logtask.getJSONObject("properties");
        String datainputset = properties.getString("datainputset");
        String assignments = properties.getString("assignments");

        for (String variableName : existingVariableNames) {
            String dataInput = variableName + ":String";
            assertTrue("Variable \"" + variableName + "\" not found in datainputset",
                       datainputset.contains(dataInput));
        }
        for (String variableName : existingVariableNames) {
            String assignment = "[din]" + variableName + "=";
            assertTrue("Assignment \"" + assignment + "\" not found in assignments",
                       assignments.contains(assignment));
        }

        for (String variableName : nonExistingVariableNames) {
            String dataInput = variableName + ":String";
            assertFalse("Variable \"" + variableName + "\" found in datainputset",
                        datainputset.contains(dataInput));
        }
        for (String variableName : nonExistingVariableNames) {
            String assignment = "[din]" + variableName + "=";
            assertFalse("Assignment \"" + assignment + "\" found in assignments",
                        assignments.contains(assignment));
        }
    }

    @Test
    public void testSimulationStartEvent() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject startEvent = getChildByName(process,
                                               "StartEvent");
        JSONObject properties = startEvent.getJSONObject("properties");
        assertEquals(99,
                     properties.getInt(PROBABILITY));
        assertEquals(5,
                     properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(4,
                     properties.getInt(PROCESSING_TIME_MIN));
        assertEquals("uniform",
                     properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationUserTask() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject userTask = getChildByName(process,
                                             "UserTask");
        JSONObject properties = userTask.getJSONObject("properties");
        assertEquals(8,
                     properties.getInt(COST_PER_TIME_UNIT));
        assertEquals(7,
                     properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(3,
                     properties.getInt(PROCESSING_TIME_MIN));
        assertEquals(5,
                     properties.getInt(WORKING_HOURS));
        assertEquals(2,
                     properties.getInt(QUANTITY));
        assertEquals("uniform",
                     properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationGatewayProbability() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject lowSequence = getChildByName(process,
                                                "LowProbability");
        JSONObject highSequence = getChildByName(process,
                                                 "HighProbability");
        JSONObject lowProperties = lowSequence.getJSONObject("properties");
        assertEquals(30,
                     lowProperties.getInt("probability"));
        JSONObject highProperties = highSequence.getJSONObject("properties");
        assertEquals(70,
                     highProperties.getInt("probability"));
    }

    @Test
    public void testSimulationThrowEvent() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject throwEvent = getChildByName(process,
                                               "ThrowEvent");
        JSONObject properties = throwEvent.getJSONObject("properties");
        assertEquals(3,
                     properties.getInt(PROCESSING_TIME_MEAN));
        assertEquals("poisson",
                     properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationCallActivity() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject callActivity = getChildByName(process,
                                                 "CallActivity");
        JSONObject properties = callActivity.getJSONObject("properties");
        assertEquals(3,
                     properties.getInt(COST_PER_TIME_UNIT));
        assertEquals(2,
                     properties.getInt(PROCESSING_TIME_MEAN));
        assertEquals(1,
                     properties.getInt(STANDARD_DEVIATION));
        assertEquals("normal",
                     properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationEndEvent() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject endEvent = getChildByName(process,
                                             "EndEvent");
        JSONObject properties = endEvent.getJSONObject("properties");
        assertEquals(9,
                     properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(8,
                     properties.getInt(PROCESSING_TIME_MIN));
        assertEquals("uniform",
                     properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationSubProcess() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject subProcess = getChildByName(process,
                                               "SubProcess");
        JSONObject properties = subProcess.getJSONObject("properties");
        assertEquals(12,
                     properties.getInt(COST_PER_TIME_UNIT));
        assertEquals(6,
                     properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(2,
                     properties.getInt(PROCESSING_TIME_MIN));
        assertEquals("uniform",
                     properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationSubService() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject serviceTask = getChildByName(getChildByName(process,
                                                               "SubProcess"),
                                                "SubService");
        JSONObject properties = serviceTask.getJSONObject("properties");
        assertEquals(14,
                     properties.getInt(COST_PER_TIME_UNIT));
        assertEquals(7,
                     properties.getInt(PROCESSING_TIME_MEAN));
        assertEquals("poisson",
                     properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationBoundaryEvent() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject boundaryEvent = getChildByName(process,
                                                  "BoundaryEvent");
        JSONObject properties = boundaryEvent.getJSONObject("properties");
        assertEquals(13,
                     properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(4,
                     properties.getInt(PROCESSING_TIME_MIN));
        assertEquals(85,
                     properties.getInt(PROBABILITY));
        assertEquals("uniform",
                     properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationCancelEvent() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject cancelEvent = getChildByName(process,
                                                "CancelEvent");
        JSONObject properties = cancelEvent.getJSONObject("properties");
        assertEquals(15,
                     properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(6,
                     properties.getInt(PROCESSING_TIME_MIN));
        assertEquals("uniform",
                     properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testUserTaskAndTaskName() throws Exception {
        JSONObject process = loader.loadProcessFromXml("userTaskWithTaskName.bpmn2",
                                                       BPMN2SyntaxCheckerTest.class,
                                                       null);
        JSONObject userTask = getChildByName(process,
                                             "User Task");
        JSONObject properties = userTask.getJSONObject("properties");
        assertEquals("taskForSomebody",
                     properties.getString("taskname"));
        assertEquals(true,
                     properties.getBoolean("isasync"));
    }

    @Test
    public void testCallActivity() throws Exception {
        JSONObject process = loader.loadProcessFromXml("callActivity.bpmn2",
                                                       BPMN2SyntaxCheckerTest.class,
                                                       null);
        JSONObject callActivity = getChildByName(process,
                                                 "callActivity");
        JSONObject properties = callActivity.getJSONObject("properties");
        assertEquals("abc.noCalledElementCallActivity",
                     properties.getString("calledelement"));
        assertEquals(true,
                     properties.getBoolean("isabortparent"));
    }

    @Test
    public void testNoCalledElementCallActivity() throws Exception {
        JSONObject process = loader.loadProcessFromXml("noCalledElementCallActivity.bpmn2",
                                                       BPMN2SyntaxCheckerTest.class,
                                                       null);
        JSONObject callActivity = getChildByName(process,
                                                 "callActivity");
        JSONObject properties = callActivity.getJSONObject("properties");
        assertFalse(properties.has("calledelement"));
    }

    @Test
    public void testErrorBoundaryEvent() throws Exception {
        JSONObject process = loader.loadProcessFromXml("errorBoundaryEvent.bpmn2",
                                                       BPMN2SyntaxCheckerTest.class,
                                                       null);
        JSONObject lane = getChildByName(process,
                                         "myLane");
        JSONObject error = getChildByName(lane,
                                          "MyError");
        JSONObject properties = error.getJSONObject("properties");
        assertEquals("errorReference",
                     properties.getString("errorref"));
    }

    @Test
    public void testErrorBoundaryEventMissingDefinition() throws Exception {
        JSONObject process = loader.loadProcessFromXml("errorBoundaryEventMissingDefinition.bpmn2",
                                                       BPMN2SyntaxCheckerTest.class,
                                                       null);
        JSONObject lane = getChildByName(process,
                                         "myLane");
        JSONObject error = getChildByName(lane,
                                          "MyError");
        JSONObject properties = error.getJSONObject("properties");
        assertEquals("",
                     properties.getString("errorref"));
    }

    @Test
    public void testServiceTaskInterfaceAndOperation() throws Exception {
        JSONObject process = loader.loadProcessFromXml("serviceTaskInterfaceAndOperation.bpmn2");
        JSONObject serviceTask = getChildByName(process,
                                                "Send PO");
        JSONObject properties = serviceTask.getJSONObject("properties");
        assertEquals("Java",
                     properties.getString("serviceimplementation"));
        assertEquals("sendInterface",
                     properties.getString("serviceinterface"));
        assertEquals("sendOperation",
                     properties.getString("serviceoperation"));
        assertEquals(true,
                     properties.getBoolean("isasync"));
    }

    @Test
    public void testServiceTaskNoInterfaceNoOperation() throws Exception {
        JSONObject process = loader.loadProcessFromXml("serviceTaskNoInterfaceNoOperation.bpmn2");
        JSONObject serviceTask = getChildByName(process,
                                                "Send PO");
        JSONObject properties = serviceTask.getJSONObject("properties");
        assertEquals("Java",
                     properties.getString("serviceimplementation"));
        assertEquals("",
                     properties.getString("serviceinterface"));
        assertEquals("",
                     properties.getString("serviceoperation"));
    }

    @Test
    public void testSubprocessTaskAssignments() throws Exception {
        JSONObject process = loader.loadProcessFromXml("subprocessTaskAssignments.bpmn2");
        JSONObject subprocess = getChildByName(process,
                                               "Embedded subprocess");
        JSONObject userTask = getChildByName(subprocess,
                                             "UserTask");
        JSONObject properties = userTask.getJSONObject("properties");
        assertTrue(properties.getString("datainputset").contains("sInput:String"));
        assertTrue(properties.getString("dataoutputset").contains("iOutput:Integer"));
        JSONObject subprocessProperties = subprocess.getJSONObject("properties");
        assertEquals(true,
                     subprocessProperties.getBoolean("isasync"));
    }

    @Test
    public void testEndEventsAssignments() throws Exception {
        JSONObject process = loader.loadProcessFromXml("subprocessTaskAssignments.bpmn2");

        JSONObject subprocess = getChildByName(process,
                                               "Embedded subprocess");
        JSONObject subEnd = getChildByName(subprocess,
                                           "SubEnd");
        JSONObject subProperties = subEnd.getJSONObject("properties");
        assertEquals("intSubInput:Integer",
                     subProperties.getString("datainput"));
        assertEquals("[din]intVar->intSubInput",
                     subProperties.getString("datainputassociations"));

        JSONObject endEvent = getChildByName(process,
                                             "End Event");
        JSONObject properties = endEvent.getJSONObject("properties");
        assertEquals("intInput:Integer",
                     properties.getString("datainput"));
        assertEquals("[din]intVar->intInput",
                     properties.getString("datainputassociations"));
    }

    @Test
    public void testBusinessRuleTask() throws Exception {
        JSONObject process = loader.loadProcessFromXml("businessRule.bpmn2",
                                                       BPMN2SyntaxCheckerTest.class,
                                                       null);
        JSONObject ruleTask = getChildByName(process,
                                             "businessRuleTask");
        JSONObject properties = ruleTask.getJSONObject("properties");
        assertEquals("simpleGroup",
                     properties.getString("ruleflowgroup"));
        assertEquals(true,
                     properties.getBoolean("isasync"));
        assertEquals(BpmnMarshallerHelper.RULE_LANG_DRL,
                     properties.getString("rulelanguage"));
    }

    @Test
    public void testReceiveTask() throws Exception {
        JSONObject process = loader.loadProcessFromXml("receiveTask.bpmn2");
        JSONObject receiveTask = getChildByName(process,
                                                "receiveTask");
        JSONObject properties = receiveTask.getJSONObject("properties");
        assertEquals("parcel",
                     properties.getString("messageref"));
        assertEquals(true,
                     properties.getBoolean("isasync"));
    }

    @Test
    public void testSendTask() throws Exception {
        JSONObject process = loader.loadProcessFromXml("sendTask.bpmn2");
        JSONObject sendTask = getChildByName(process,
                                             "sendTask");
        JSONObject properties = sendTask.getJSONObject("properties");
        assertEquals("parcel",
                     properties.getString("messageref"));
        assertEquals(true,
                     properties.getBoolean("isasync"));
    }

    @Test
    public void testRestTaskAssignments() throws Exception {
        String[] existingVariableNames = {"Content", "ContentType", "ResultClass", "Method", "Username", "Password",
                "ReadTimeout", "ConnectTimeout", "Url"};

        JSONObject process = loader.loadProcessFromXml("restTask.bpmn2");
        JSONObject restTask = getChildByName(process,
                                             "REST");
        JSONObject properties = restTask.getJSONObject("properties");
        String datainputset = properties.getString("datainputset");
        String dataoutputset = properties.getString("dataoutputset");
        String assignments = properties.getString("assignments");

        for (String variableName : existingVariableNames) {
            String dataInput = variableName + ":String";
            assertTrue("Variable \"" + variableName + "\" not found in datainputset",
                       datainputset.contains(dataInput));
        }
        for (String variableName : existingVariableNames) {
            String assignment = "[din]" + variableName + "=";
            assertTrue("Assignment \"" + assignment + "\" not found in assignments",
                       assignments.contains(assignment));
        }

        assertTrue(assignments.contains("[dout]Result->processVariable"));
        assertTrue(dataoutputset.contains("Result:java.lang.Object"));
    }

    @Test
    public void testCallActivityAssignments() throws Exception {
        JSONObject process = loader.loadProcessFromXml("callActivityInSubprocess.bpmn2");
        JSONObject subProcess = getChildByName(process,
                                               "SubProcess");
        JSONObject callActivity = getChildByName(subProcess,
                                                 "callActivity");
        JSONObject properties = callActivity.getJSONObject("properties");
        String datainputset = properties.getString("datainputset");
        String dataoutputset = properties.getString("dataoutputset");
        String assignments = properties.getString("assignments");

        assertTrue(assignments.contains("[dout]innerOutput->intVariable"));
        assertTrue(assignments.contains("[din]intVariable->innerInput"));
        assertTrue(assignments.contains("[din]innerConstant=stringConstant"));
        assertTrue(datainputset.contains("innerInput:Integer"));
        assertTrue(datainputset.contains("innerConstant:String"));
        assertTrue(dataoutputset.contains("innerOutput:Integer"));
    }

    @Test
    public void testProcessCustomProperties() throws Exception {
        JSONObject process = loader.loadProcessFromXml("customProperties.bpmn2");
        JSONObject properties = process.getJSONObject("properties");
        String caseIdPrefix = properties.getString("customcaseidprefix");
        String caseRoles = properties.getString("customcaseroles");

        assertEquals("HR",
                     caseIdPrefix);
        assertEquals("owner:1,participant:2",
                     caseRoles);
    }

    @Test
    public void testTaskCustomProperties() throws Exception {
        JSONObject process = loader.loadProcessFromXml("customPropertiesTask.bpmn2");
        JSONObject task = getChildByName(process,
                                         "Task_1");
        JSONObject properties = task.getJSONObject("properties");
        String autoStart = properties.getString("customautostart");

        assertEquals("true",
                     autoStart);
    }

    @Test
    public void testOnEntryOnExitActions() throws Exception {
        JSONObject process = loader.loadProcessFromXml("onEntryonExitActions.bpmn2");
        JSONObject bcTask = getChildByName(process,
                                           "bc");
        JSONObject properties = bcTask.getJSONObject("properties");

        assertEquals("onExit1;\nonExit2;\n",
                     properties.getString("onexitactions"));
        assertEquals("onEntry1;\nonEntry2;\n",
                     properties.getString("onentryactions"));
    }

    @Test
    public void testOnEntryOnExitActionsEscaping() throws Exception {
        JSONObject process = loader.loadProcessFromXml("onEntryonExitActionsEscaping.bpmn2");
        JSONObject task = getChildByName(process,
                                         "task");
        JSONObject properties = task.getJSONObject("properties");

        assertEquals("myVariable = \"\\\\\"text in quotes\\\\\"\";\\nSystem.out.println(myVariable);\\n",
                     properties.getString("onentryactions"));
        assertEquals("myVariable = \"\\\\\"another text in quotes\\\\\"\";\\nSystem.out.println(myVariable);\\n",
                     properties.getString("onexitactions"));
    }

    @Test
    public void testAdHocSubprocessDroolsCompletionCondition() throws Exception {
        JSONObject process = loader.loadProcessFromXml("adHocSubprocessCompletionCondition.bpmn2");
        JSONObject adHocSubprocess = getChildByName(process,
                                                    "abcde");
        JSONObject properties = adHocSubprocess.getJSONObject("properties");

        String completionCondition = properties.getString("adhoccompletioncondition");
        String scriptLanguage = properties.getString("script_language");

        assertEquals("org.kie.api.runtime.process.CaseData(data.get(\"claimReportDone\") == true)",
                     completionCondition);
        assertEquals("drools",
                     scriptLanguage);
    }

    @Test
    public void testDMNBusinessRuleTask() throws Exception {
        JSONObject process = loader.loadProcessFromXml("dmnBusinessRule.bpmn2");
        JSONObject ruleTask = getChildByName(process,
                                             "test");
        JSONObject properties = ruleTask.getJSONObject("properties");
        assertEquals(BpmnMarshallerHelper.RULE_LANG_DMN,
                     properties.getString("rulelanguage"));
    }

    @Test
    public void testCallActivityAbortParent() throws Exception {
        JSONObject process = loader.loadProcessFromXml("callActivityAbortParent.bpmn2");
        JSONObject callActivity = getChildByName(process,
                                                 "callActivity");
        JSONObject properties = callActivity.getJSONObject("properties");
        assertEquals(false,
                     properties.getBoolean("isabortparent"));
    }

    @Test
    public void testCustomWorkitemAssignments() throws Exception {
        List<String> testWorkItemNames = Arrays.asList("SampleUserWorkitem");
        JSONObject process = loader.loadProcessFromXml("customWorkitemAssignments.bpmn2",
                                                       testWorkItemNames);

        JSONObject workitem = getChildByName(process,
                                             "SampleUserWorkitem");
        JSONObject workItemPropertiesProperties = workitem.getJSONObject("properties");
        assertEquals("lastNameIn:String,firstNameIn:String",
                     workItemPropertiesProperties.getString("datainputset"));
        assertEquals("lastNameOut:String,firstNameOut:String",
                     workItemPropertiesProperties.getString("dataoutputset"));
        assertEquals("[din]lastName->lastNameIn,[din]firstName->firstNameIn,[dout]lastNameOut->lastName,[dout]firstNameOut->firstName",
                     workItemPropertiesProperties.getString("assignments"));
    }
}
