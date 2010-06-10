package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("LoopStandard")
public class XPDLLoopStandard extends XMLConvertible {

	@Attribute("LoopCondition")
	protected String loopCondition;
	@Attribute("LoopCounter")
	protected String loopCounter;
	@Attribute("LoopMaximum")
	protected String loopMaximum;
	@Attribute("TestTime")
	protected String testTime;
	
	public String getLoopCondition() {
		return loopCondition;
	}
	
	public String getLoopCounter() {
		return loopCounter;
	}
	
	public String getLoopMaximum() {
		return loopMaximum;
	}
	
	public String getTestTime() {
		return testTime;
	}
	
	public void readJSONloopcondition(JSONObject modelElement) {
		setLoopCondition(modelElement.optString("loopcondition"));
	}
	
	public void readJSONloopcounter(JSONObject modelElement) {
		setLoopCounter(modelElement.optString("loopcounter"));
	}
	
	public void readJSONloopmaximum(JSONObject modelElement) {
		setLoopMaximum(modelElement.optString("loopmaximum"));
	}
	
	public void readJSONstandardloopunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "standardloopunknowns");
	}
	
	public void readJSONtesttime(JSONObject modelElement) {
		setTestTime(modelElement.optString("testtime"));
	}
	
	public void setLoopCondition(String loopCondition) {
		this.loopCondition = loopCondition;
	}
	
	public void setLoopCounter(String loopCounter) {
		this.loopCounter = loopCounter;
	}
	
	public void setLoopMaximum(String loopMaximum) {
		this.loopMaximum = loopMaximum;
	}
	
	public void setTestTime(String testTime) {
		this.testTime = testTime;
	}
	
	public void writeJSONloopcondition(JSONObject modelElement) throws JSONException {
		modelElement.put("loopcondition", getLoopCondition());
	}
	
	public void writeJSONloopcounter(JSONObject modelElement) throws JSONException {
		modelElement.put("loopcounter", getLoopCounter());
	}
	
	public void writeJSONloopmaximum(JSONObject modelElement) throws JSONException {
		modelElement.put("loopmaximum", getLoopMaximum());
	}
	
	public void writeJSONstandardloopunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "standardloopunknowns");
	}
	
	public void writeJSONtesttimes(JSONObject modelElement) throws JSONException {
		modelElement.put("testtime", getTestTime());
	}
}
