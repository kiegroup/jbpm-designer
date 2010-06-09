package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Route")
public class XPDLRoute extends XMLConvertible {

	@Attribute("ExclusiveType")
	protected String exclusiveType;
	@Attribute("GatewayType")
	protected String gatewayType;
	
	@Attribute("IncomingCondition")
	protected String incomingCondition;
	@Attribute("Instantiate")
	protected String instantiate;
	@Attribute("MarkerVisible")
	protected String markerVisible;
	@Attribute("OutgoingCondition")
	protected String outgoingCondition;
	
	public String getExclusiveType() {
		return exclusiveType;
	}
	
	public String getGatewayType() {
		return gatewayType;
	}
	
	public String getIncomingCondition() {
		return incomingCondition;
	}
	
	public String getInstantiate() {
		return instantiate;
	}
	
	public String getMarkerVisible() {
		return markerVisible;
	}
	
	public String getOutgoingCondition() {
		return outgoingCondition;
	}
	
	public void readJSONgatewaytype(JSONObject modelElement) {
		String type = modelElement.optString("gatewaytype");
		if (type != null) {
			if (type.equals("XOR")) {
				setGatewayType("Exclusive");
			} else if (type.equals("OR")) {
				setGatewayType("Inclusive");
			} else if (type.equals("AND")) {
				setGatewayType("Parallel");
			} else {
				setGatewayType(type);
			}
		}
	}
	
	public void readJSONincomingcondition(JSONObject modelElement) {
		setIncomingCondition(modelElement.optString("incomingcondition"));
	}
	
	public void readJSONinstantiate(JSONObject modelElement) {
		setInstantiate(modelElement.optString("instantiate"));
	}
	
	public void readJSONmarkervisible(JSONObject modelElement) {
		setMarkerVisible(modelElement.optString("markervisible"));
	}
	
	public void readJSONoutgoingcondition(JSONObject modelElement) {
		setOutgoingCondition(modelElement.optString("outgoingcondition"));
	}
	
	public void readJSONrouteunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "routeunknowns");
	}
	
	public void readJSONxortype(JSONObject modelElement) {
		setExclusiveType(modelElement.optString("xortype"));
	}
	
	public void setExclusiveType(String type) {
		exclusiveType = type;
	}
	
	public void setGatewayType(String type) {
		gatewayType = type;
	}
	
	public void setIncomingCondition(String condition) {
		incomingCondition = condition;
	}
	
	public void setInstantiate(String instantiateValue) {
		instantiate = instantiateValue;
	}
	
	public void setMarkerVisible(String isVisible) {
		markerVisible = isVisible;
	}
	
	public void setOutgoingCondition(String condition) {
		outgoingCondition = condition;
	}
	
	public void writeJSONgatewaytype(JSONObject modelElement) throws JSONException {
		String type = getGatewayType();		
		
		if (type != null) {
			if (type.equals("Exclusive") || type.equals("XOR")) {
				putProperty(modelElement, "gatewaytype", "XOR");
				String xorTypeOfGateway = getExclusiveType();
				if (xorTypeOfGateway != null) {
					if (xorTypeOfGateway.equals("Event")) {
						writeStencil(modelElement, "Exclusive_Eventbased_Gateway");
					} else {
						writeStencil(modelElement, "Exclusive_Databased_Gateway");
					}
				} else {
					writeStencil(modelElement, "Exclusive_Databased_Gateway");
				}
			} else if (type.equals("Inclusive") || type.equals("OR")) {
				putProperty(modelElement, "gatewaytype", "OR");
				writeStencil(modelElement, "OR_Gateway");
			} else if (type.equals("Parallel") || type.equals("AND")) {
				putProperty(modelElement, "gatewaytype", "AND");
				writeStencil(modelElement, "AND_Gateway");
			} else if (type.equals("Complex")){
				putProperty(modelElement, "gatewaytype", type);
				writeStencil(modelElement, "Complex_Gateway");
			} else {
				putProperty(modelElement, "gatewaytype", "AND");
				writeStencil(modelElement, "AND_Gateway");
			}
		} else {
			putProperty(modelElement, "gatewaytype", "AND");
			writeStencil(modelElement, "AND_Gateway");
		}
	}
	
	public void writeJSONincomingcondition(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "incomingcondition", getIncomingCondition());
	}
	
	public void writeJSONinstantiate(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "instantiate", getInstantiate());
	}
	
	public void writeJSONmarkervisible(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "markervisible", getMarkerVisible());
	}
	
	public void writeJSONoutgoingcondition(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "outgoingcondition", getOutgoingCondition());
	}
	
	public void writeJSONrouteunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "routeunknowns");
	}
	
	public void writeJSONxortype(JSONObject modelElement) throws JSONException {
		String type = getExclusiveType();
		if (type != null) {
			putProperty(modelElement, "xortype", type);
		}
	}
	
	protected JSONObject getProperties(JSONObject modelElement) {
		return modelElement.optJSONObject("properties");
	}
	
	protected void initializeProperties(JSONObject modelElement) throws JSONException {
		JSONObject properties = modelElement.optJSONObject("properties");
		if (properties == null) {
			JSONObject newProperties = new JSONObject();
			modelElement.put("properties", newProperties);
			properties = newProperties;
		}
	}
	
	protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
		initializeProperties(modelElement);
		
		getProperties(modelElement).put(key, value);
	}
	
	protected void writeStencil(JSONObject modelElement, String stencil) throws JSONException {
		JSONObject stencilObject = new JSONObject();
		stencilObject.put("id", stencil);
		
		modelElement.put("stencil", stencilObject);
	}
}
