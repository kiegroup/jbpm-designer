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
import static org.jbpm.designer.bpmn2.utils.Bpmn2Loader.getChildByTypeName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    public void testSimulationMIUserTask() throws Exception {
        JSONObject process = loader.loadProcessFromXml("multipleInstanceTask.bpmn2");
        JSONObject userTask = getChildByName(process,
                                             "mitask");
        JSONObject properties = userTask.getJSONObject("properties");
        assertNotNull(properties.get("dataoutputset"));
        assertEquals("salidaSimple:Object",
                     properties.get("dataoutputset"));
        assertTrue(properties.getString("datainputset").contains("entradaSimple"));
        assertTrue(properties.getString("datainputset").contains(properties.getString("multipleinstancedatainput")));
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

    @Test
    public void testBusinessRuleTaskInputOutputSetWithSpacesAndUnderscores() throws Exception {
        JSONObject process = loader.loadProcessFromXml("businessRuleTaskWithDataInputsOutputsWithSpaces.bpmn2");
        JSONObject businessRuleTask = getChildByName(process,
                                                     "mytask");
        JSONObject businessRuleTaskProperties = businessRuleTask.getJSONObject("properties");
        assertEquals("[din]aaa->_first in,[din]bbb->_second in,[din]_third in=123,[dout]_first out->ccc,[dout]_second out->bbb",
                     businessRuleTaskProperties.getString("assignments"));

        assertEquals("_first in:String,_second in:Object,_third in:Integer",
                     businessRuleTaskProperties.getString("datainputset"));

        assertEquals("_first out:String,_second out:Integer",
                     businessRuleTaskProperties.getString("dataoutputset"));
    }

    @Test
    public void testStartEventOutputSetWithSpacesAndUnderscores() throws Exception {
        JSONObject process = loader.loadProcessFromXml("startEventWithDataInputWithSpaces.bpmn2");
        JSONObject startEvent = getChildByName(process,
                                               "abc");
        JSONObject startEventProperties = startEvent.getJSONObject("properties");

        assertEquals("_first out:String",
                     startEventProperties.getString("dataoutput"));

        assertEquals("[dout]_first out->aaa",
                     startEventProperties.getString("dataoutputassociations"));
    }

    @Test
    public void testEndEventOutputSetWithSpacesAndUnderscores() throws Exception {
        JSONObject process = loader.loadProcessFromXml("endEventWithDataInputWithSpaces.bpmn2");
        JSONObject endEvent = getChildByName(process,
                                             "abc");
        JSONObject endEventProperties = endEvent.getJSONObject("properties");

        assertEquals("_first in:String",
                     endEventProperties.getString("datainput"));

        assertEquals("[din]aaa->_first in",
                     endEventProperties.getString("datainputassociations"));
    }

    @Test
    public void testCallActivityInputsOutputSetWithSpacesAndUnderscores() throws Exception {
        JSONObject process = loader.loadProcessFromXml("callActivityWithDataInputOutputWithSpaces.bpmn2");
        JSONObject callActivity = getChildByName(process,
                                                 "abc");
        JSONObject callActivityProperties = callActivity.getJSONObject("properties");
        assertEquals("[din]aaa->_first in,[dout]_first out->aaa",
                     callActivityProperties.getString("assignments"));

        assertEquals("_first in:String",
                     callActivityProperties.getString("datainputset"));

        assertEquals("_first out:String",
                     callActivityProperties.getString("dataoutputset"));
    }

    @Test
    public void testUserTaskInputsOutputSetWithSpacesAndUnderscores() throws Exception {
        JSONObject process = loader.loadProcessFromXml("userTaskWithDataInputOutputWithSpaces.bpmn2");
        JSONObject callActivity = getChildByName(process,
                                                 "abc");
        JSONObject callActivityProperties = callActivity.getJSONObject("properties");
        assertEquals("[din]aaa->_first in,[dout]_first out->bbb",
                     callActivityProperties.getString("assignments"));

        assertEquals("_first in:String",
                     callActivityProperties.getString("datainputset"));

        assertEquals("_first out:String",
                     callActivityProperties.getString("dataoutputset"));
    }

    @Test
    public void testProcessAndUserTaskWithXMLEscapeChars() throws Exception {
        JSONObject process = loader.loadProcessFromXml("usertaskWithXMLEscapeChars.bpmn2");
        JSONObject processProperties = process.getJSONObject("properties");

        String processName = processProperties.getString("processn");
        assertEquals("\"'<>&Process",
                     processName);

        List<JSONObject> userTasks = getChildByTypeName(process,
                                                        "Task");
        assertNotNull(userTasks);
        assertEquals(1,
                     userTasks.size());
        JSONObject userTask = userTasks.get(0);
        assertNotNull(userTask);

        JSONObject userTaskProperties = userTask.getJSONObject("properties");
        String userTaskName = userTaskProperties.getString("name");
        assertEquals("\"'<>&Task",
                     userTaskName);
    }

    @Test
    public void testProcessWithEmptyOnEntryOnExitNode() throws Exception {
        JSONObject process = loader.loadProcessFromXml("usertaskWithEmptyOnEntryOnExistActions.bpmn2");
        JSONObject processProperties = process.getJSONObject("properties");

        String processName = processProperties.getString("processn");
        assertEquals("MyProcess",
                     processName);

        List<JSONObject> userTasks = getChildByTypeName(process,
                                                        "Task");
        assertNotNull(userTasks);
        assertEquals(1,
                     userTasks.size());
        JSONObject userTask = userTasks.get(0);
        assertNotNull(userTask);

        JSONObject userTaskProperties = userTask.getJSONObject("properties");
        String userTaskName = userTaskProperties.getString("name");
        assertEquals("MyTask",
                     userTaskName);
    }

    @Test
    public void testCatchEventDefinitionRef() throws Exception {
        JSONObject process = loader.loadProcessFromXml("catchEventDefinitionRef.bpmn2");
        JSONObject processProperties = process.getJSONObject("properties");

        String processName = processProperties.getString("processn");
        assertEquals("catchDefinitionRef",
                     processName);

        JSONObject catchEvent = getChildByName(process,
                                               "mytimer");
        assertNotNull(catchEvent);
    }

    @Test
    public void testThrowEventDefinitionRef() throws Exception {
        JSONObject process = loader.loadProcessFromXml("throwEventDefinitionRef.bpmn2");
        JSONObject processProperties = process.getJSONObject("properties");

        String processName = processProperties.getString("processn");
        assertEquals("throwDefinitionRef",
                     processName);

        JSONObject catchEvent = getChildByName(process,
                                               "mysignalEvent");
        assertNotNull(catchEvent);
    }

    @Test
    public void testStartEventDefinitionRef() throws Exception {
        JSONObject process = loader.loadProcessFromXml("startEventDefinitionRef.bpmn2");
        JSONObject processProperties = process.getJSONObject("properties");

        String processName = processProperties.getString("processn");
        assertEquals("startDefinitionRef",
                     processName);

        JSONObject startEvent = getChildByName(process,
                                               "mytimerstart");
        assertNotNull(startEvent);
    }

    @Test
    public void testEndEventDefinitionRef() throws Exception {
        JSONObject process = loader.loadProcessFromXml("endEventDefinitionRef.bpmn2");
        JSONObject processProperties = process.getJSONObject("properties");

        String processName = processProperties.getString("processn");
        assertEquals("endEventDefinitionRef",
                     processName);

        JSONObject endEvent = getChildByName(process,
                                             "mysignalendEvent");
        assertNotNull(endEvent);
    }

    @Test
    public void testBoundaryEventDefinitionRef() throws Exception {
        JSONObject process = loader.loadProcessFromXml("boundaryEventDefinitionRef.bpmn2");
        JSONObject processProperties = process.getJSONObject("properties");

        String processName = processProperties.getString("processn");
        assertEquals("boundaryDefinitionRef",
                     processName);

        JSONObject endEvent = getChildByName(process,
                                             "myboundarytimer");
        assertNotNull(endEvent);
    }

    @Test
    public void testMessageRefNodes() throws Exception {
        JSONObject process = loader.loadProcessFromXml("messageRefNodes.bpmn2");

        JSONObject startMessageEvent = getChildByName(process,
                                                      "start");
        JSONObject startMessageEventProperties = startMessageEvent.getJSONObject("properties");
        assertEquals("messageOne",
                     startMessageEventProperties.getString("messageref"));

        JSONObject sendTask = getChildByName(process,
                                             "send");
        JSONObject sendTaskProperties = sendTask.getJSONObject("properties");
        assertEquals("messagetwo",
                     sendTaskProperties.getString("messageref"));

        JSONObject receiveTask = getChildByName(process,
                                                "receive");
        JSONObject receiveTaskProperties = receiveTask.getJSONObject("properties");
        assertEquals("messagethree",
                     receiveTaskProperties.getString("messageref"));

        JSONObject endMessageEvent = getChildByName(process,
                                                    "end");
        JSONObject endMessageEventProperties = endMessageEvent.getJSONObject("properties");
        assertEquals("messagefour",
                     endMessageEventProperties.getString("messageref"));
    }

    @Test
    public void testProcessCustomSlaDueDate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("customProperties.bpmn2");
        JSONObject properties = process.getJSONObject("properties");
        String slaDueDate = properties.getString("customsladuedate");

        assertEquals("3m",
                     slaDueDate);
    }

    @Test
    public void testTaskCustomSlaDueDate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("customPropertiesTask.bpmn2");
        JSONObject task = getChildByName(process,
                                         "Task_1");
        JSONObject properties = task.getJSONObject("properties");
        String slaDueDate = properties.getString("customsladuedate");

        assertEquals("3m",
                     slaDueDate);
    }

    @Test
    public void testAdHocSubprocessActivationCondition() throws Exception {
        JSONObject process = loader.loadProcessFromXml("adHocSubprocessActivationCondition.bpmn2");
        JSONObject adHocSubprocess = getChildByName(process,
                                                    "abcde");
        JSONObject properties = adHocSubprocess.getJSONObject("properties");

        String completionCondition = properties.getString("adhocactivationcondition");
        String scriptLanguage = properties.getString("script_language");

        assertEquals("org.kie.api.runtime.process.CaseData(data.get(\"Done\") == true)",
                     completionCondition);
        assertEquals("drools",
                     scriptLanguage);
    }

    @Test
    public void testSequenceFlowFeel() throws Exception {
        JSONObject process = loader.loadProcessFromXml("sequenceFlowFeel.bpmn2");
        JSONObject sequenceFlow = getChildByName(process,
                                                 "seqFlow");
        JSONObject properties = sequenceFlow.getJSONObject("properties");

        String conditionExpressionLanguage = properties.getString("conditionexpressionlanguage");
        String conditionExpression = properties.getString("conditionexpression");

        assertEquals("FEEL",
                     conditionExpressionLanguage);
        assertEquals("x = \"Entry\"",
                     conditionExpression);
    }

    @Test
    public void testDefinitionsExporterAndVersion() throws Exception {
        JSONObject process = loader.loadProcessFromXml("exporterAndVersion.bpmn2");
        JSONObject properties = process.getJSONObject("properties");

        String exporter = properties.getString("exporter");
        String exporterVersion = properties.getString("exporterversion");

        assertEquals("My Custom Exporter",
                     exporter);
        assertEquals("My Custom Version",
                     exporterVersion);
    }

    @Test
    public void testMilestoneCustomSlaDueDate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("milestoneCustomSla.bpmn2");
        JSONObject milestone = getChildByName(process,
                                              "Milestone");
        JSONObject properties = milestone.getJSONObject("properties");
        String slaDueDate = properties.getString("customsladuedate");

        assertEquals("3m",
                     slaDueDate);
    }

    @Test
    public void testSubprocessDocumentation() throws Exception {
        JSONObject process = loader.loadProcessFromXml("subprocessWithDocumentation.bpmn2");
        JSONObject subprocess = getChildByName(process,
                                               "Sub-process");
        JSONObject properties = subprocess.getJSONObject("properties");

        String subprocessDocumentation = properties.getString("documentation");

        assertEquals("my subprocess documentation",
                     subprocessDocumentation);
    }

    @Test
    public void testStartEventCustomSlaDueDate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("customSlaNodesTest.bpmn2");
        JSONObject startTimer = getChildByName(process,
                                               "startTimer");
        JSONObject properties = startTimer.getJSONObject("properties");
        String slaDueDate = properties.getString("customsladuedate");

        assertEquals("3s",
                     slaDueDate);
    }

    @Test
    public void testBusinessRuleTaskCustomSlaDueDate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("customSlaNodesTest.bpmn2");
        JSONObject businessRuleTask = getChildByName(process,
                                                     "businessRuleTask");
        JSONObject properties = businessRuleTask.getJSONObject("properties");
        String slaDueDate = properties.getString("customsladuedate");

        assertEquals("3s",
                     slaDueDate);
    }

    @Test
    public void testUserTaskWithBoundaryEventCustomSlaDueDate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("customSlaNodesTest.bpmn2");
        JSONObject userTask = getChildByName(process,
                                             "userTask");
        JSONObject properties = userTask.getJSONObject("properties");
        String slaDueDate = properties.getString("customsladuedate");

        assertEquals("3s",
                     slaDueDate);

        JSONObject boundaryEvent = getChildByName(process,
                                                  "timerBoundary");
        JSONObject boundaryProperties = boundaryEvent.getJSONObject("properties");
        String boundarySlaDueDate = boundaryProperties.getString("customsladuedate");

        assertEquals("3s",
                     boundarySlaDueDate);
    }

    @Test
    public void testCallActivityWithBoundaryEventCustomSlaDueDate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("customSlaNodesTest.bpmn2");
        JSONObject callActivity = getChildByName(process,
                                                 "reusableSubProcess");
        JSONObject properties = callActivity.getJSONObject("properties");
        String slaDueDate = properties.getString("customsladuedate");

        assertEquals("3s",
                     slaDueDate);

        JSONObject boundaryEvent = getChildByName(process,
                                                  "timerBoundary2");
        JSONObject boundaryProperties = boundaryEvent.getJSONObject("properties");
        String boundarySlaDueDate = boundaryProperties.getString("customsladuedate");

        assertEquals("3s",
                     boundarySlaDueDate);
    }

    @Test
    public void testCatchingEventCustomSlaDueDate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("customSlaNodesTest.bpmn2");
        JSONObject catchingEvent = getChildByName(process,
                                                  "timer");
        JSONObject properties = catchingEvent.getJSONObject("properties");
        String slaDueDate = properties.getString("customsladuedate");

        assertEquals("3s",
                     slaDueDate);
    }

    @Test
    public void testSubprocessWithMessageBoundaryEventSlaDueDate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("customSlaSubprocessTest.bpmn2");

        JSONObject subprocess = getChildByName(process,
                                               "mySubProcess");
        JSONObject propertiesSubProcess = subprocess.getJSONObject("properties");
        String slaDueDateSubProcess = propertiesSubProcess.getString("customsladuedate");

        assertEquals("3s",
                     slaDueDateSubProcess);

        JSONObject messageBoundaryEvent = getChildByName(process,
                                                         "messageBoundary");
        JSONObject propertiesBoundary = messageBoundaryEvent.getJSONObject("properties");
        String slaDueDateBoundary = propertiesBoundary.getString("customsladuedate");

        assertEquals("3s",
                     slaDueDateBoundary);
    }

    @Test
    public void testInnerSubprocessWithMessageEventSlaDueDate() throws Exception {
        JSONObject process = loader.loadProcessFromXml("customSlaSubprocessTest.bpmn2");

        JSONObject subprocess = getChildByName(process,
                                               "mySubProcess");

        JSONObject innerSubprocess = getChildByName(subprocess,
                                                    "myEventSubProcess");

        JSONObject propertiesInnerSubProcess = innerSubprocess.getJSONObject("properties");
        String slaDueDateInnerSubProcess = propertiesInnerSubProcess.getString("customsladuedate");

        assertEquals("3s",
                     slaDueDateInnerSubProcess);

        JSONObject messageEvent = getChildByName(innerSubprocess,
                                                 "message");

        JSONObject propertiesMessageEvent = messageEvent.getJSONObject("properties");
        String slaDueDateMessageEvent = propertiesMessageEvent.getString("customsladuedate");

        assertEquals("3s",
                     slaDueDateMessageEvent);
    }

    @Test
    public void testRestWorkitemAssignmentsValueWithDashes() throws Exception {
        List<String> testWorkItemNames = Arrays.asList("REST");
        JSONObject process = loader.loadProcessFromXml("restWorkitemWithDashes.bpmn2",
                                                       testWorkItemNames);

        JSONObject workitem = getChildByName(process,
                                             "REST");
        assertNotNull(workitem);

        JSONObject workItemPropertiesProperties = workitem.getJSONObject("properties");
        assertEquals("ReadTimeout:String,ContentData:String,Password:String,Url:String,Method:String,Username:String,ConnectTimeout:String",
                     workItemPropertiesProperties.getString("datainputset"));
        assertEquals("Result:java.lang.Object",
                     workItemPropertiesProperties.getString("dataoutputset"));
        assertEquals("[din]ReadTimeout=application%2Fjson%3Bcharset%3DUTF-8,[din]ContentData=application%2Fjson%3Bcharset%3DUTF-8,[din]Password=application%2Fjson%3Bcharset%3DUTF-8,[din]Url=application%2Fjson%3Bcharset%3DUTF-8,[din]Method=application%2Fjson%3Bcharset%3DUTF-8,[din]Username=application%2Fjson%3Bcharset%3DUTF-8,[din]ConnectTimeout=application%2Fjson%3Bcharset%3DUTF-8",
                     workItemPropertiesProperties.getString("assignments"));
    }

    @Test
    public void testDiagramResolution() throws Exception {
        JSONObject process = loader.loadProcessFromXml("diagramresolution.bpmn2");
        JSONObject processProperties = process.getJSONObject("properties");

        String diagramResolution = processProperties.getString("diagramresolution");

        assertNotNull(diagramResolution);

        // after resolution has been processed it is default to 0.0
        assertEquals(diagramResolution, "0.0");
    }

    @Test
    public void testDiagramResolutionDefault() throws Exception {
        JSONObject process = loader.loadProcessFromXml("diagramresolutiondefault.bpmn2");
        JSONObject processProperties = process.getJSONObject("properties");

        String diagramResolution = processProperties.getString("diagramresolution");

        assertNotNull(diagramResolution);

        // after resolution has been processed it is default to 0.0
        assertEquals(diagramResolution, "0.0");
    }
}
