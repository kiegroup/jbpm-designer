package de.hpi.bpmn2xpdl;

import java.util.Arrays;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("MessageFlow")
public class XPDLMessageFlow extends XPDLThingConnectorGraphics {

	@Attribute("Source")
	protected String source;
	@Attribute("Target")
	protected String target;
	@Element("Message")
	protected XPDLMessage message;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				"MessageFlow"};
		return Arrays.asList(types).contains(stencil);
	}
	public XPDLMessage getMessage() {
		return message;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void readJSONmessage(JSONObject modelElement) throws JSONException {
		passInformationToMessage(modelElement, "message");
	}

	public void readJSONmessageunknowns(JSONObject modelElement) throws JSONException {
		passInformationToMessage(modelElement, "messageunknowns");
	}
	
	public void readJSONresourceId(JSONObject modelElement) throws JSONException {
		super.readJSONresourceId(modelElement);
		findSourceId(getResourceId());
	}
	
	public void readJSONsource(JSONObject modelElement) throws JSONException {
		findSourceId(getResourceId());
	}
	
	public void readJSONtarget(JSONObject modelElement) throws JSONException {
		JSONObject target = modelElement.getJSONObject("target");
		setTarget(target.optString("resourceId"));
	}
	
	public void setMessage(XPDLMessage messageValue) {
		message = messageValue;
	}
	
	public void setSource(String sourceValue) {
		source = sourceValue;
	}
	
	public void setTarget(String targetValue) {
		target = targetValue;
	}
	public void writeJSONgraphicsinfos(JSONObject modelElement) throws JSONException {
		super.writeJSONgraphicsinfos(modelElement);
		
		convertFirstAndLastDockerToRelative(getTarget(), getSource(), modelElement);
	}
	public void writeJSONmessage(JSONObject modelElement) throws JSONException {
		XPDLMessage messageObject = getMessage();
		if (messageObject != null) {
			initializeProperties(modelElement);
			messageObject.write(getProperties(modelElement));
		}
	}
	
	public void writeJSONsource(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "source", "");
	}
	
	public void writeJSONstencil(JSONObject modelElement) throws JSONException {
		JSONObject stencil = new JSONObject();
		stencil.put("id", "MessageFlow");
		
		modelElement.put("stencil", stencil);
	}
	
	public void writeJSONtarget(JSONObject modelElement) throws JSONException {
		JSONObject target = new JSONObject();
		target.put("resourceId", getTarget());
		
		modelElement.put("target", target);
	}
	@Override
	public void writeJSONoutgoing(JSONObject modelElement) throws JSONException {
		super.writeJSONoutgoing(modelElement);
		JSONArray outgoing = modelElement.optJSONArray("outgoing");
		outgoing.put(resourceIdToJSONObject(getTarget()));
		
	}
	
	protected void initializeMessage() {
		if (getMessage() == null) {
			setMessage(new XPDLMessage());
		}
	}
	
	protected void passInformationToMessage(JSONObject modelElement, String key) throws JSONException {
		initializeMessage();
		
		JSONObject passObject = new JSONObject();
		passObject.put("id", getProperId(modelElement));
		passObject.put(key, modelElement.optString(key));
		
		getMessage().parse(passObject);
	}
	
	private void findSourceId(String resourceId) throws JSONException{
		for(Entry<String, JSONObject> entry: getResourceIdToShape().entrySet()){
			JSONArray outgoings=entry.getValue().optJSONArray("outgoing");
			if(outgoings!=null){
				for(int i=0; i<outgoings.length();i++){
					String shapeId = outgoings.getJSONObject(i).optString("resourceId");
					if(resourceId.equals(shapeId)) {
						setSource(shapeId);
					}
				}
			}
		}
	}
}
