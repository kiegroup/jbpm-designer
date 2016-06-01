package org.jbpm.designer.bpmn2.impl;

import static org.junit.Assert.*;

import org.jbpm.designer.bpmn2.utils.Bpmn2Loader;
import org.jbpm.designer.bpmn2.validation.BPMN2SyntaxCheckerTest;
import org.json.JSONObject;
import org.junit.Test;

public class Bpmn2JsonMarshallerTest {

    public static final String COST_PER_TIME_UNIT = "unitcost";
    public static final String PROCESSING_TIME_MAX = "max";
    public static final String PROCESSING_TIME_MIN = "min";
    public static final String PROCESSING_TIME_MEAN = "mean";
    public static final String PROBABILITY = "probability";
    public static final String WORKING_HOURS = "workinghours";
    public static final String QUANTITY = "quantity";
    public static final String STANDARD_DEVIATION = "standarddeviation";
    public static final String DISTRIBUTION_TYPE = "distributiontype";

    private Bpmn2Loader loader = new Bpmn2Loader(Bpmn2JsonMarshallerTest.class);

    @Test
    public void testGroupMarshalling() throws Exception {
        JSONObject process = loader.loadProcessFromXml("group.bpmn2");
        JSONObject group = loader.getChildByName(process, "group");

        assertNotNull("Group with name 'group' not found in process.", group);
        assertEquals("Group has wrong documentation.", "group documentation", loader.getDocumentationFor(group));
    }

    @Test
    public void testBoundaryEventDocumentation() throws Exception {
        JSONObject process = loader.loadProcessFromXml("boundaryEventsDocumentation.bpmn2");
        JSONObject boundaryEvent = loader.getChildByName(process, "CancelOnTimer");

        assertNotNull("BoundaryEvent with name 'CancelOnTimer' not found in process.", boundaryEvent);
        assertEquals("BoundaryEvent has wrong documentation.", "Cancel task on timeout.", loader.getDocumentationFor(boundaryEvent));
    }

    @Test
    public void testSwimlaneDocumentation() throws Exception {
        JSONObject process = loader.loadProcessFromXml("swimlane.bpmn2");
        JSONObject swimlane = loader.getChildByName(process, "Documented Swimlane");

        assertNotNull("Swimlane with name 'Documented Swimlane' not found in process.", swimlane);
        assertEquals("Swimlane has wrong documentation.", "Some documentation for swimlane.", loader.getDocumentationFor(swimlane));
    }

    @Test
    public void testSendTaskDataInputs() throws Exception {
        String[] variableNames = {"Comment", "Content", "CreatedBy", "GroupId", "Locale",
                "NotCompletedNotify", "NotCompletedReassign", "NotStartedNotify", "NotStartedReassign",
                "Priority", "Skippable", "TaskName", "MyDataInput1", "MyDataInput2"};

        JSONObject process = loader.loadProcessFromXml("nonusertaskdatainputs.bpmn2");
        JSONObject sendtask = loader.getChildByName(process, "MySendTask");
        JSONObject properties = sendtask.getJSONObject("properties");
        String datainputset = properties.getString("datainputset");
        for (String variableName : variableNames) {
            String dataInput = variableName + ":String";
            assertTrue("Variable \"" + variableName + "\" not found in datainputset", datainputset.contains(dataInput));
        }

        String assignments = properties.getString("assignments");
        for (String variableName : variableNames) {
            String assignment = "[din]" + variableName + "=a" + variableName;
            assertTrue("Assignment \"" + assignment + "\" not found in assignments", assignments.contains(assignment));
        }
    }

    @Test
    public void testCustomTaskTaskName() throws Exception {
        String[] existingVariableNames = {"Message", "DataInput1"};

        String[] nonExistingVariableNames = {"TaskName"};

        JSONObject process = loader.loadProcessFromXml("customtasktaskname.bpmn2");
        JSONObject logtask = loader.getChildByName(process, "Log");
        JSONObject properties = logtask.getJSONObject("properties");
        String datainputset = properties.getString("datainputset");
        String assignments = properties.getString("assignments");

        for (String variableName : existingVariableNames) {
            String dataInput = variableName + ":String";
            assertTrue("Variable \"" + variableName + "\" not found in datainputset", datainputset.contains(dataInput));
        }
        for (String variableName : existingVariableNames) {
            String assignment = "[din]" + variableName + "=";
            assertTrue("Assignment \"" + assignment + "\" not found in assignments", assignments.contains(assignment));
        }

        for (String variableName : nonExistingVariableNames) {
            String dataInput = variableName + ":String";
            assertFalse("Variable \"" + variableName + "\" found in datainputset", datainputset.contains(dataInput));
        }
        for (String variableName : nonExistingVariableNames) {
            String assignment = "[din]" + variableName + "=";
            assertFalse("Assignment \"" + assignment + "\" found in assignments", assignments.contains(assignment));
        }
    }

    @Test
    public void testSimulationStartEvent() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject startEvent = loader.getChildByName(process, "StartEvent");
        JSONObject properties = startEvent.getJSONObject("properties");
        assertEquals(99, properties.getInt(PROBABILITY));
        assertEquals(5, properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(4, properties.getInt(PROCESSING_TIME_MIN));
        assertEquals("uniform", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationUserTask() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject userTask = loader.getChildByName(process, "UserTask");
        JSONObject properties = userTask.getJSONObject("properties");
        assertEquals(8, properties.getInt(COST_PER_TIME_UNIT));
        assertEquals(7, properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(3, properties.getInt(PROCESSING_TIME_MIN));
        assertEquals(5, properties.getInt(WORKING_HOURS));
        assertEquals(2, properties.getInt(QUANTITY));
        assertEquals("uniform", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationGatewayProbability() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject lowSequence = loader.getChildByName(process, "LowProbability");
        JSONObject highSequence = loader.getChildByName(process, "HighProbability");
        JSONObject lowProperties = lowSequence.getJSONObject("properties");
        assertEquals(30, lowProperties.getInt("probability"));
        JSONObject highProperties = highSequence.getJSONObject("properties");
        assertEquals(70, highProperties.getInt("probability"));
    }

    @Test
    public void testSimulationThrowEvent() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject throwEvent = loader.getChildByName(process, "ThrowEvent");
        JSONObject properties = throwEvent.getJSONObject("properties");
        assertEquals(3, properties.getInt(PROCESSING_TIME_MEAN));
        assertEquals("poisson", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationCallActivity() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject callActivity = loader.getChildByName(process, "CallActivity");
        JSONObject properties = callActivity.getJSONObject("properties");
        assertEquals(3, properties.getInt(COST_PER_TIME_UNIT));
        assertEquals(2, properties.getInt(PROCESSING_TIME_MEAN));
        assertEquals(1, properties.getInt(STANDARD_DEVIATION));
        assertEquals("normal", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationEndEvent() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject endEvent = loader.getChildByName(process, "EndEvent");
        JSONObject properties = endEvent.getJSONObject("properties");
        assertEquals(9, properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(8, properties.getInt(PROCESSING_TIME_MIN));
        assertEquals("uniform", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationSubProcess() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject subProcess = loader.getChildByName(process, "SubProcess");
        JSONObject properties = subProcess.getJSONObject("properties");
        assertEquals(12, properties.getInt(COST_PER_TIME_UNIT));
        assertEquals(6, properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(2, properties.getInt(PROCESSING_TIME_MIN));
        assertEquals("uniform", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationSubService() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject serviceTask = loader.getChildByName(loader.getChildByName(process, "SubProcess"), "SubService");
        JSONObject properties = serviceTask.getJSONObject("properties");
        assertEquals(14, properties.getInt(COST_PER_TIME_UNIT));
        assertEquals(7, properties.getInt(PROCESSING_TIME_MEAN));
        assertEquals("poisson", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationBoundaryEvent() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject boundaryEvent = loader.getChildByName(process, "BoundaryEvent");
        JSONObject properties = boundaryEvent.getJSONObject("properties");
        assertEquals(13, properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(4, properties.getInt(PROCESSING_TIME_MIN));
        assertEquals(85, properties.getInt(PROBABILITY));
        assertEquals("uniform", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationCancelEvent() throws Exception {
        JSONObject process = loader.loadProcessFromXml("simulationProcess.bpmn2");
        JSONObject cancelEvent = loader.getChildByName(process, "CancelEvent");
        JSONObject properties = cancelEvent.getJSONObject("properties");
        assertEquals(15, properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(6, properties.getInt(PROCESSING_TIME_MIN));
        assertEquals("uniform", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testUserTaskAndTaskName() throws Exception {
        JSONObject process = loader.loadProcessFromXml("userTaskWithTaskName.bpmn2", BPMN2SyntaxCheckerTest.class);
        JSONObject userTask = loader.getChildByName(process, "User Task");
        JSONObject properties = userTask.getJSONObject("properties");
        assertEquals("taskForSomebody", properties.getString("taskname"));
    }
}
