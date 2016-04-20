package org.jbpm.designer.bpmn2.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.DefaultProfileImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class Bpmn2JsonMarshallerTest {

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

    private JSONObject loadProcessFrom(String fileName) throws Exception {
        URL fileURL = Bpmn2JsonMarshallerTest.class.getResource(fileName);
        String definition = FileUtils.readFileToString(new File(fileURL.toURI()));

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

    private static String getPropertyValue(JSONObject bpmnElement, String propertyName) throws JSONException {
        return bpmnElement.getJSONObject("properties").getString(propertyName);
    }
}
