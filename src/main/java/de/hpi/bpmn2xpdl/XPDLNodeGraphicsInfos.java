package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("NodeGraphicsInfos")
public class XPDLNodeGraphicsInfos extends XMLConvertible {

	@Element("NodeGraphicsInfo")
	protected ArrayList<XPDLNodeGraphicsInfo> nodeGraphicsInfos;

	public void add(XPDLNodeGraphicsInfo newNodeGraphicsInfos) {
		initializeNodeGraphicsInfos();
		
		getNodeGraphicsInfos().add(newNodeGraphicsInfos);
	}
	
	public XPDLNodeGraphicsInfo get(int index) {
		return nodeGraphicsInfos.get(index);
	}
	
	public ArrayList<XPDLNodeGraphicsInfo> getNodeGraphicsInfos() {
		return nodeGraphicsInfos;
	}
	
	public void readJSONgraphicsinfounknowns(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("graphicsinfounknowns", modelElement.optString("graphicsinfounknowns"));
		getFirstGraphicsInfo().parse(passObject);
	}
	
	public void readJSONgraphicsinfosunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "graphicsinfosunknowns");
	}

	public void setNodeGraphicsInfos(ArrayList<XPDLNodeGraphicsInfo> newNodeGraphicsInfos) {
		this.nodeGraphicsInfos = newNodeGraphicsInfos;
	}
	
	public void writeJSONgraphicsinfosunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "graphicsinfosunknowns");
	}
	
	public void writeJSONgraphicsinfo(JSONObject modelElement) {
		ArrayList<XPDLNodeGraphicsInfo> infos = getNodeGraphicsInfos();
		if (infos != null) {
			for (int i = 0; i < infos.size(); i++) {
				if (infos.get(i).getToolId().equals("Oryx")) {
					infos.get(i).write(modelElement);
					break;
				}
			}
			infos.get(0).write(modelElement);
		}
	}
	
	protected XPDLNodeGraphicsInfo getFirstGraphicsInfo() {
		return getNodeGraphicsInfos().get(0);
	}
	
	protected void initializeNodeGraphicsInfos() {
		if (getNodeGraphicsInfos() == null) {
			setNodeGraphicsInfos(new ArrayList<XPDLNodeGraphicsInfo>());
		}
	}
}
