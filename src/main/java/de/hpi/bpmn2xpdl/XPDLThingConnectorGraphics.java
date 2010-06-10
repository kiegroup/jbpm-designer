package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("ConnectorThing")
public abstract class XPDLThingConnectorGraphics extends XPDLThing {

	@Element("ConnectorGraphicsInfos")
	protected XPDLConnectorGraphicsInfos connectorGraphics;
	
	public XPDLConnectorGraphicsInfos getConnectorGraphics() {
		return connectorGraphics;
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
	
	public void readJSONdockers(JSONObject modelElement) throws JSONException {
		JSONArray dockers = modelElement.optJSONArray("dockers");
		
		if (dockers != null) {			
			if (dockers.length() > 0) {
				initializeGraphics();
				JSONObject passObject = new JSONObject();
				passObject.put("dockers", dockers);
				passObject.put("target", modelElement.optJSONObject("target"));
				passObject.put("resourceId", modelElement.optString("resourceId"));
				getFirstGraphicsInfo().parse(passObject);
			}
		}
	}
	
	public void readJSONgraphicsinfounknowns(JSONObject modelElement) throws JSONException {
		passInformationToFirstGraphics(modelElement, "graphicsinfounknowns");
	}
	
	public void readJSONgraphicsinfosunknowns(JSONObject modelElement) throws JSONException {
		initializeGraphics();
		
		JSONObject passObject = new JSONObject();
		passObject.put("graphicsinfosunknowns", modelElement.optString("graphicsinfosunknowns"));
		getConnectorGraphics().parse(passObject);
	}
	
	public void setConnectorGraphics(XPDLConnectorGraphicsInfos graphics) {
		connectorGraphics = graphics;
	}
	
	public void writeJSONgraphicsinfos(JSONObject modelElement) throws JSONException {
		XPDLConnectorGraphicsInfos infos = getConnectorGraphics();
		if (infos != null) {
			infos.write(modelElement);
		}
	}
	
	protected XPDLConnectorGraphicsInfo getFirstGraphicsInfo() {
		return getConnectorGraphics().get(0);
	}
	
	protected void initializeGraphics() {
		if (getConnectorGraphics() == null) {
			setConnectorGraphics(new XPDLConnectorGraphicsInfos());
			getConnectorGraphics().add(new XPDLConnectorGraphicsInfo());
			getConnectorGraphics().get(0).setResourceIdToShape(getResourceIdToShape());
		}
	}
	
	protected void passInformationToFirstGraphics(JSONObject modelElement, String key) throws JSONException {
		initializeGraphics();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getFirstGraphicsInfo().parse(passObject);
	}
	protected void convertFirstAndLastDockerToRelative(String toID, String fromID, JSONObject modelElement) throws JSONException{
		JSONArray dockers = modelElement.optJSONArray("dockers");
		if(dockers!=null){
			if (fromID != null) {
				JSONObject firstDocker = dockers.optJSONObject(0);
				makeDockerRelativeToShape(fromID, firstDocker);
			}
			if (toID != null) {
				JSONObject lastDocker = dockers.optJSONObject(dockers.length() - 1);
				makeDockerRelativeToShape(toID, lastDocker);
					
				}			
		}
	}

	/**
	 * @param fromID
	 * @param firstDocker
	 * @throws JSONException
	 */
	private void makeDockerRelativeToShape(String fromID, JSONObject firstDocker)
			throws JSONException {
		if (firstDocker != null) {
			XPDLThing object = getResourceIdToObject().get(fromID);
			if (object instanceof XPDLThingNodeGraphics) {
				XPDLThingNodeGraphics thing = (XPDLThingNodeGraphics) object;
				if (thing.getNodeGraphics() != null) {
					if (thing.getNodeGraphics().getNodeGraphicsInfos() != null) {
						ArrayList<XPDLNodeGraphicsInfo> infos = thing.getNodeGraphics().getNodeGraphicsInfos();
						XPDLNodeGraphicsInfo info = infos.get(0);
						for (XPDLNodeGraphicsInfo iterate : infos) {
							if ("Oryx".equals(iterate.getToolId())) {
								info = iterate;
							}
						}
						if (info.getCoordinates() != null) {
							XPDLCoordinates coords = info.getCoordinates().get(0);
							
							firstDocker.put("x", firstDocker.optDouble("x") - coords.getXCoordinate());
							firstDocker.put("y", firstDocker.optDouble("y") - coords.getYCoordinate());
						}
					}
				}
			}
		}
	}
}