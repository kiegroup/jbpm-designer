package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("ConformanceClass")
public class XPDLConformanceClass extends XMLConvertible {
	
	@Attribute("GraphConformance")
	protected String graphConformance = "NON-BLOCKED";
	@Attribute("BPMNModelPortabilityConformance")
	protected String bpmnConformance = "STANDARD";
	
	public String getBpmnConformance() {
		return bpmnConformance;
	}
	
	public String getGraphConformance() {
		return graphConformance;
	}
	
	public void readJSONconformanceclassunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "conformanceclassunknowns");
	}
	
	public void setBpmnConformance(String conformance) {
		bpmnConformance = conformance;
	}
	
	public void setGraphConformance(String conformance) {
		graphConformance = conformance;
	}
	
	public void writeJSONconformanceclassunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "conformanceclassunknowns");
	}
}
