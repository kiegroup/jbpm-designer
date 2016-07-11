package org.jbpm.designer.bpmn2.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;


import org.apache.commons.io.FileUtils;
import bpsim.impl.BpsimPackageImpl;
import org.jboss.drools.impl.DroolsPackageImpl;

import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.DefaultProfileImpl;
import org.json.JSONArray;
import org.json.JSONException;
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


    DefaultProfileImpl profile = new DefaultProfileImpl();
    // It is by design (Unmarshaller = marshaller)
    IDiagramProfile.IDiagramUnmarshaller marshaller = profile.createUnmarshaller();

    @Test
    public void testGroupMarshalling() throws Exception {
        JSONObject process = loadProcessFrom("group.bpmn2");
        JSONObject group = getChildByName(process, "group");

        assertNotNull("Group with name 'group' not found in process.", group);
        assertEquals("Group has wrong documentation.", "group documentation", getDocumentationFor(group));
    }

    @Test
    public void testBoundaryEventDocumentation() throws Exception {
        JSONObject process = loadProcessFrom("boundaryEventsDocumentation.bpmn2");
        JSONObject boundaryEvent = getChildByName(process, "CancelOnTimer");

        assertNotNull("BoundaryEvent with name 'CancelOnTimer' not found in process.", boundaryEvent);
        assertEquals("BoundaryEvent has wrong documentation.", "Cancel task on timeout.", getDocumentationFor(boundaryEvent));
    }

    @Test
    public void testSwimlaneDocumentation() throws Exception {
        JSONObject process = loadProcessFrom("swimlane.bpmn2");
        JSONObject swimlane = getChildByName(process, "Documented Swimlane");

        assertNotNull("Swimlane with name 'Documented Swimlane' not found in process.", swimlane);
        assertEquals("Swimlane has wrong documentation.", "Some documentation for swimlane.", getDocumentationFor(swimlane));
    }

    @Test
    public void testConstraint() throws Exception {
        JSONObject process = loadProcessFrom("constraint.bpmn2");
        JSONObject condition1 = getChildByName(process, "Condition1");
        JSONObject condition2 = getChildByName(process, "Condition2");
        JSONObject condition1Properties = condition1.getJSONObject("properties");
        JSONObject condition2Properties = condition2.getJSONObject("properties");
        assertEquals("return  KieFunctions.endsWith(customVar, \"sample\");", condition1Properties.getString("conditionexpression"));
        assertEquals("return  !KieFunctions.isNull(intVar);", condition2Properties.getString("conditionexpression"));
    }

    @Test
    public void testSendTaskDataInputs() throws Exception {
        String[] variableNames = {"Comment", "Content", "CreatedBy", "GroupId", "Locale",
                "NotCompletedNotify", "NotCompletedReassign", "NotStartedNotify", "NotStartedReassign",
                "Priority", "Skippable", "TaskName", "MyDataInput1", "MyDataInput2"};

        JSONObject process = loadProcessFrom("nonusertaskdatainputs.bpmn2");
        JSONObject sendtask = getChildByName(process, "MySendTask");
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

        JSONObject process = loadProcessFrom("customtasktaskname.bpmn2");
        JSONObject logtask = getChildByName(process, "Log");
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
        JSONObject process = loadProcessFrom("simulationProcess.bpmn2");
        JSONObject startEvent = getChildByName(process, "StartEvent");
        JSONObject properties = startEvent.getJSONObject("properties");
        assertEquals(99, properties.getInt(PROBABILITY));
        assertEquals(5, properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(4, properties.getInt(PROCESSING_TIME_MIN));
        assertEquals("uniform", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationUserTask() throws Exception {
        JSONObject process = loadProcessFrom("simulationProcess.bpmn2");
        JSONObject userTask = getChildByName(process, "UserTask");
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
        JSONObject process = loadProcessFrom("simulationProcess.bpmn2");
        JSONObject lowSequence = getChildByName(process, "LowProbability");
        JSONObject highSequence = getChildByName(process, "HighProbability");
        JSONObject lowProperties = lowSequence.getJSONObject("properties");
        assertEquals(30, lowProperties.getInt("probability"));
        JSONObject highProperties = highSequence.getJSONObject("properties");
        assertEquals(70, highProperties.getInt("probability"));
    }

    @Test
    public void testSimulationThrowEvent() throws Exception {
        JSONObject process = loadProcessFrom("simulationProcess.bpmn2");
        JSONObject throwEvent = getChildByName(process, "ThrowEvent");
        JSONObject properties = throwEvent.getJSONObject("properties");
        assertEquals(3, properties.getInt(PROCESSING_TIME_MEAN));
        assertEquals("poisson", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationCallActivity() throws Exception {
        JSONObject process = loadProcessFrom("simulationProcess.bpmn2");
        JSONObject callActivity = getChildByName(process, "CallActivity");
        JSONObject properties = callActivity.getJSONObject("properties");
        assertEquals(3, properties.getInt(COST_PER_TIME_UNIT));
        assertEquals(2, properties.getInt(PROCESSING_TIME_MEAN));
        assertEquals(1, properties.getInt(STANDARD_DEVIATION));
        assertEquals("normal", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationEndEvent() throws Exception {
        JSONObject process = loadProcessFrom("simulationProcess.bpmn2");
        JSONObject endEvent = getChildByName(process, "EndEvent");
        JSONObject properties = endEvent.getJSONObject("properties");
        assertEquals(9, properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(8, properties.getInt(PROCESSING_TIME_MIN));
        assertEquals("uniform", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationSubProcess() throws Exception {
        JSONObject process = loadProcessFrom("simulationProcess.bpmn2");
        JSONObject subProcess = getChildByName(process, "SubProcess");
        JSONObject properties = subProcess.getJSONObject("properties");
        assertEquals(12, properties.getInt(COST_PER_TIME_UNIT));
        assertEquals(6, properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(2, properties.getInt(PROCESSING_TIME_MIN));
        assertEquals("uniform", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationSubService() throws Exception {
        JSONObject process = loadProcessFrom("simulationProcess.bpmn2");
        JSONObject serviceTask = getChildByName(getChildByName(process, "SubProcess"), "SubService");
        JSONObject properties = serviceTask.getJSONObject("properties");
        assertEquals(14, properties.getInt(COST_PER_TIME_UNIT));
        assertEquals(7, properties.getInt(PROCESSING_TIME_MEAN));
        assertEquals("poisson", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationBoundaryEvent() throws Exception {
        JSONObject process = loadProcessFrom("simulationProcess.bpmn2");
        JSONObject boundaryEvent = getChildByName(process, "BoundaryEvent");
        JSONObject properties = boundaryEvent.getJSONObject("properties");
        assertEquals(13, properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(4, properties.getInt(PROCESSING_TIME_MIN));
        assertEquals(85, properties.getInt(PROBABILITY));
        assertEquals("uniform", properties.getString(DISTRIBUTION_TYPE));
    }

    @Test
    public void testSimulationCancelEvent() throws Exception {
        JSONObject process = loadProcessFrom("simulationProcess.bpmn2");
        JSONObject cancelEvent = getChildByName(process, "CancelEvent");
        JSONObject properties = cancelEvent.getJSONObject("properties");
        assertEquals(15, properties.getInt(PROCESSING_TIME_MAX));
        assertEquals(6, properties.getInt(PROCESSING_TIME_MIN));
        assertEquals("uniform", properties.getString(DISTRIBUTION_TYPE));
    }

    private JSONObject loadProcessFrom(String fileName) throws Exception {
        URL fileURL = Bpmn2JsonMarshallerTest.class.getResource(fileName);
        String definition = FileUtils.readFileToString(new File(fileURL.toURI()));

        DroolsPackageImpl.init();
        BpsimPackageImpl.init();
        String jsonString = marshaller.parseModel(definition, profile, "Email,HelloWorkItemHandler,Log,Rest,WebService");

        JSONObject process = new JSONObject(jsonString);
        if ("BPMNDiagram".equals(process.getJSONObject("stencil").getString("id"))) {
            return process;
        }

        throw new IllegalArgumentException("File " + fileName + " is not a valid BPMN2 process JSON");
    }

    private static JSONObject getChildByName(JSONObject parent, String name) throws JSONException {
        JSONArray children = parent.getJSONArray("childShapes");
        for (int i = 0; i < children.length(); i++) {
            JSONObject child = children.getJSONObject(i);

            if (name.equals(getPropertyValue(child, "name"))) {
                return child;
            }
        }

        return null;
    }

    private static String getDocumentationFor(JSONObject bpmnElement) throws JSONException {
        return getPropertyValue(bpmnElement, "documentation");
    }

    @Test
    public void testCallActivityAssignments() throws Exception {
        JSONObject process = loadProcessFrom("callActivityInSubprocess.bpmn2");
        JSONObject subProcess = getChildByName(process, "SubProcess");
        JSONObject callActivity = getChildByName(subProcess, "callActivity");
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

    private static String getPropertyValue(JSONObject bpmnElement, String propertyName) throws JSONException {
        return bpmnElement.getJSONObject("properties").getString(propertyName);
    }
}
