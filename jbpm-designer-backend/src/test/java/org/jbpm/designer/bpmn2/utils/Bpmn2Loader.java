package org.jbpm.designer.bpmn2.utils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import bpsim.impl.BpsimPackageImpl;
import org.eclipse.bpmn2.Definitions;
import org.jboss.drools.impl.DroolsPackageImpl;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Bpmn2Loader {

    private Class testClass;
    private String processJson;
    JbpmProfileImpl profile = new JbpmProfileImpl();

    // It is by design (Unmarshaller = marshaller and vice versa)
    IDiagramProfile.IDiagramUnmarshaller marshaller = profile.createUnmarshaller();

    public Bpmn2Loader(Class testClass) {
        this.testClass = testClass;
    }

    public Definitions loadProcessFromJson(String fileName,
                                           String zOrderEnabled,
                                           String bpsimDisplay) throws Exception {

        profile.setZOrder(zOrderEnabled);
        profile.setBpsimDisplay(bpsimDisplay);

        // It is by design (Unmarshaller = marshaller and vice versa)
        IDiagramProfile.IDiagramMarshaller unmarshaller = profile.createMarshaller();

        URL fileURL = testClass.getResource(fileName);
        String json = new String(Files.readAllBytes(Paths.get(fileURL.toURI())));

        return unmarshaller.getDefinitions(json,
                                           "Email,HelloWorkItemHandler,Log,Rest,WebService");
    }

    public Definitions loadProcessFromJson(String fileName) throws Exception {
        return loadProcessFromJson(fileName,
                                   "false",
                                   "true");
    }

    public JSONObject loadProcessFromXml(String fileName,
                                         Class nonDefaultTestClass) throws Exception {
        URL fileURL = nonDefaultTestClass.getResource(fileName);
        String definition = new String(Files.readAllBytes(Paths.get(fileURL.toURI())));

        DroolsPackageImpl.init();
        BpsimPackageImpl.init();
        processJson = marshaller.parseModel(definition,
                                            profile,
                                            "Email,HelloWorkItemHandler,Log,Rest,WebService");

        JSONObject process = new JSONObject(processJson);
        if ("BPMNDiagram".equals(process.getJSONObject("stencil").getString("id"))) {
            return process;
        }

        throw new IllegalArgumentException("File " + fileName + " is not a valid BPMN2 process JSON");
    }

    public JSONObject loadProcessFromXml(String fileName) throws Exception {
        return loadProcessFromXml(fileName,
                                  testClass);
    }

    public static JSONObject getChildByName(JSONObject parent,
                                            String name) throws JSONException {
        JSONArray children = parent.getJSONArray("childShapes");
        for (int i = 0; i < children.length(); i++) {
            JSONObject child = children.getJSONObject(i);

            if (name.equals(getPropertyValue(child,
                                             "name"))) {
                return child;
            }
        }

        return null;
    }

    public static List<JSONObject> getChildByTypeName(JSONObject parent,
                                                      String type) throws JSONException {
        ArrayList<JSONObject> result = new ArrayList();
        JSONArray children = parent.getJSONArray("childShapes");
        for (int i = 0; i < children.length(); i++) {
            JSONObject child = children.getJSONObject(i);

            if (type.equals(getStencilValue(child,
                                            "id"))) {
                result.add(child);
            }
        }

        return result;
    }

    public static String getDocumentationFor(JSONObject bpmnElement) throws JSONException {
        return getPropertyValue(bpmnElement,
                                "documentation");
    }

    public static String getNameFor(JSONObject bpmnElement) throws JSONException {
        return getPropertyValue(bpmnElement,
                                "name");
    }

    private static String getPropertyValue(JSONObject bpmnElement,
                                           String propertyName) throws JSONException {
        return bpmnElement.getJSONObject("properties").getString(propertyName);
    }

    public static String getResourceId(JSONObject bpmnElement) throws JSONException {
        return bpmnElement.getString("resourceId");
    }

    private static String getStencilValue(JSONObject bpmnElement,
                                          String propertyName) throws JSONException {
        return bpmnElement.getJSONObject("stencil").getString(propertyName);
    }

    public JbpmProfileImpl getProfile() {
        return profile;
    }

    public String getProcessJson() {
        return processJson;
    }
}
