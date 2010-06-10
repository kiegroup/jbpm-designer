package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Loop")
public class XPDLLoop extends XMLConvertible {
	
	@Attribute("Type")
	protected String loopType;
	
	@Element("LoopStandard")
	protected XPDLLoopStandard loopStandard;
	@Element("MultiInstance")
	protected XPDLMultiInstance multiInstance;
	
	public XPDLLoopStandard getLoopStandard() {
		return loopStandard;
	}
	
	public String getLoopType() {
		return loopType;
	}
	
	public XPDLMultiInstance getMultiInstance() {
		return multiInstance;
	}
	
	public void readJSONcomplexmi_condition(JSONObject modelElement) throws JSONException {
		passInformationToMI(modelElement, "complexmi_condition");
	}
	
	public void readJSONloopcondition(JSONObject modelElement) throws JSONException {
		passInformationToStandard(modelElement, "loopcondition");
	}
	
	public void readJSONloopcounter(JSONObject modelElement) throws JSONException {
		if (modelElement.optString("looptype").equals("MultiInstance")) {
			passInformationToMI(modelElement, "loopcounter");
		} else {
			passInformationToStandard(modelElement, "loopcounter");
		}
	}
	
	public void readJSONloopmaximum(JSONObject modelElement) throws JSONException {
		passInformationToStandard(modelElement, "loopmaximum");
	}
	
	public void readJSONloopunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "loopunknowns");
	}
	
	public void readJSONlooptype(JSONObject modelElement) {
		setLoopType(modelElement.optString("looptype"));
	}
	
	public void readJSONmiloopunknowns(JSONObject modelElement) throws JSONException {
		passInformationToMI(modelElement, "miloopunknowns");
	}
	
	public void readJSONmi_condition(JSONObject modelElement) throws JSONException {
		passInformationToMI(modelElement, "mi_condition");
	}
	
	public void readJSONmi_flowcondition(JSONObject modelElement) throws JSONException {
		passInformationToMI(modelElement, "mi_flowcondition");
	}
	
	public void readJSONmi_ordering(JSONObject modelElement) throws JSONException {
		passInformationToMI(modelElement, "mi_ordering");
	}
	
	public void readJSONstandardloopunknowns(JSONObject modelElement) throws JSONException {
		passInformationToStandard(modelElement, "standardloopunknowns");
	}
	
	public void readJSONtesttime(JSONObject modelElement) throws JSONException {
		passInformationToStandard(modelElement, "testtime");
	}
	
	public void setLoopStandard(XPDLLoopStandard loop) {
		loopStandard = loop;
	}
	
	public void setLoopType(String typeValue) {
		loopType = typeValue;
	}
	
	public void setMultiInstance(XPDLMultiInstance loop) {
		multiInstance = loop;
	}
	
	public void writeJSONloopinstance(JSONObject modelElement) {
		if (getLoopStandard() != null) {
			getLoopStandard().write(getProperties(modelElement));
		} else if (getMultiInstance() != null) {
			getMultiInstance().write(getProperties(modelElement));
		}
	}
	
	public void writeJSONloopunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "loopunknowns");
	}
	
	public void writeJSONlooptype(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "looptype", getLoopType());
	}
	
	protected JSONObject getProperties(JSONObject modelElement) {
		return modelElement.optJSONObject("properties");
	}
	
	protected void initializeLoopStandard() {
		if (getLoopStandard() == null) {
			setLoopStandard(new XPDLLoopStandard());
		}
	}
	
	protected void initializeMultiInstance() {
		if (getMultiInstance() == null) {
			setMultiInstance(new XPDLMultiInstance());
		}
	}
	
	protected void initializeProperties(JSONObject modelElement) throws JSONException {
		JSONObject properties = modelElement.optJSONObject("properties");
		if (properties == null) {
			JSONObject newProperties = new JSONObject();
			modelElement.put("properties", newProperties);
			properties = newProperties;
		}
	}
	
	protected void passInformationToMI(JSONObject modelElement, String key) throws JSONException {
		String loopType = modelElement.optString("looptype");
		if (loopType.equals("MultiInstance")) {
			initializeMultiInstance();
		
			JSONObject passObject = new JSONObject();
			passObject.put(key, modelElement.optString(key));
		
			getMultiInstance().parse(passObject);
		}
	}
	
	protected void passInformationToStandard(JSONObject modelElement, String key) throws JSONException {
		String loopType = modelElement.optString("looptype");
		if (loopType.equals("Standard")) {
			initializeLoopStandard();
		
			JSONObject passObject = new JSONObject();
			passObject.put(key, modelElement.optString(key));
		
			getLoopStandard().parse(passObject);
		}
	}
	
	protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
		initializeProperties(modelElement);
		
		getProperties(modelElement).put(key, value);
	}
}
