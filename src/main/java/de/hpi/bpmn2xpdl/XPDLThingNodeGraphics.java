package de.hpi.bpmn2xpdl;

import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("NodeThing")
public abstract class XPDLThingNodeGraphics extends XPDLThing {

	@Element("NodeGraphicsInfos")
	protected XPDLNodeGraphicsInfos nodeGraphics;

	public XPDLNodeGraphicsInfos getNodeGraphics() {
		return nodeGraphics;
	}

	public void readJSONbgcolor(JSONObject modelElement) throws JSONException {
		passInformationToFirstGraphics(modelElement, "bgcolor");
	}

	public void readJSONbounds(JSONObject modelElement) throws JSONException {
		initializeGraphics();
	
		JSONObject bounds = new JSONObject();
		bounds.put("bounds", modelElement.optJSONObject("bounds"));
		getFirstGraphicsInfo().parse(bounds);
	}
	
	public void readJSONgraphicsinfounknowns(JSONObject modelElement) throws JSONException {
		passInformationToFirstGraphics(modelElement, "graphicsinfounknowns");
	}
	
	public void readJSONgraphicsinfosunknowns(JSONObject modelElement) throws JSONException {
		initializeGraphics();
		
		JSONObject passObject = new JSONObject();
		passObject.put("graphicsinfosunknowns", modelElement.optString("graphicsinfosunknowns"));
		getNodeGraphics().parse(passObject);
	}

	public void setNodeGraphics(XPDLNodeGraphicsInfos graphics) {
		nodeGraphics = graphics;
	}
	
	public void writeJSONgraphicsinfos(JSONObject modelElement) {
		XPDLNodeGraphicsInfos infos = getNodeGraphics();
		if (infos != null) {
			infos.write(modelElement);
		}
	}
	
	public void writeJSONoutgoing(JSONObject modelElement) throws JSONException {
		JSONArray outgoing = new JSONArray();
		if(getId()==null)
			return;
		
		for (Entry<String, XPDLThing> entry : getResourceIdToObject().entrySet()) {
			if (entry.getValue() instanceof XPDLTransition) {
				XPDLTransition t= (XPDLTransition)entry.getValue();
				if (getId().equals(t.getFrom())){
					JSONObject j= new JSONObject();
					j.put("resourceId", entry.getKey());
					outgoing.put(j);
				}
			}else if(entry.getValue() instanceof XPDLMessageFlow) {
				XPDLMessageFlow t= (XPDLMessageFlow)entry.getValue();
				if (getId().equals(t.getSource())){
					JSONObject j= new JSONObject();
					j.put("resourceId", entry.getKey());
					outgoing.put(j);
				}
			}else if(entry.getValue() instanceof XPDLAssociation) {
				XPDLAssociation t= (XPDLAssociation)entry.getValue();
				if (getId().equals(t.getSource())){
					JSONObject j= new JSONObject();
					j.put("resourceId", entry.getKey());
					outgoing.put(j);
				}
			}
		}
		modelElement.put("outgoing", outgoing);
	}
	
	protected XPDLNodeGraphicsInfo getFirstGraphicsInfo() {
		return getNodeGraphics().get(0);
	}

	protected void initializeGraphics() {
		if (getNodeGraphics() == null) {
			setNodeGraphics(new XPDLNodeGraphicsInfos());
			getNodeGraphics().add(new XPDLNodeGraphicsInfo());
		}
	}
	
	protected void passInformationToFirstGraphics(JSONObject modelElement, String key) throws JSONException {
		initializeGraphics();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getFirstGraphicsInfo().parse(passObject);
	}
}
