package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("ConnectorGraphicsInfos")
public class XPDLConnectorGraphicsInfos extends XMLConvertible {

	@Element("ConnectorGraphicsInfo")
	protected ArrayList<XPDLConnectorGraphicsInfo> connectorGraphicsInfos;

	public void add(XPDLConnectorGraphicsInfo newConnectorGraphicsInfos) {
		initializeConnectorGraphicsInfos();
		
		getConnectorGraphicsInfos().add(newConnectorGraphicsInfos);
	}
	
	public XPDLConnectorGraphicsInfo get(int index) {
		return connectorGraphicsInfos.get(index);
	}
	
	public ArrayList<XPDLConnectorGraphicsInfo> getConnectorGraphicsInfos() {
		return connectorGraphicsInfos;
	}
	
	public void readJSONgraphicsinfounknowns(JSONObject modelElement) throws JSONException {
		JSONObject passObject = new JSONObject();
		passObject.put("graphicsinfounknowns", modelElement.optString("graphicsinfounknowns"));
		getFirstGraphicsInfo().parse(passObject);
	}
	
	public void readJSONgraphicsinfosunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "graphicsinfosunknowns");
	}

	public void setConnectorGraphicsInfos(ArrayList<XPDLConnectorGraphicsInfo> newConnectorGraphicsInfos) {
		this.connectorGraphicsInfos = newConnectorGraphicsInfos;
	}
	
	public void writeJSONgraphicsinfosunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "graphicsinfosunknowns");
	}
	
	public void writeJSONgraphicsinfo(JSONObject modelElement) {
		ArrayList<XPDLConnectorGraphicsInfo> infos = getConnectorGraphicsInfos();
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
	
	protected XPDLConnectorGraphicsInfo getFirstGraphicsInfo() {
		return getConnectorGraphicsInfos().get(0);
	}
	
	protected void initializeConnectorGraphicsInfos() {
		if (getConnectorGraphicsInfos() == null) {
			setConnectorGraphicsInfos(new ArrayList<XPDLConnectorGraphicsInfo>());
		}
	}
}
