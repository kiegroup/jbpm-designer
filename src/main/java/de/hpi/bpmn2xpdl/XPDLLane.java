package de.hpi.bpmn2xpdl;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Lane")
public class XPDLLane extends XPDLThingNodeGraphics {

	@Attribute("ParentLane")
	protected String parentLane;
	@Attribute("ParentPool")
	protected String parentPool;

	public static boolean handlesStencil(String stencil) {
		String[] types = { "Lane" };
		return Arrays.asList(types).contains(stencil);
	}

	public String getParentLane() {
		return parentLane;
	}

	public String getParentPool() {
		return parentPool;
	}

	public void readJSONparentlane(JSONObject modelElement) {
		setParentLane(modelElement.optString("parentlane"));
	}

	public void readJSONparentpool(JSONObject modelElement) {
		setParentPool(modelElement.optString("parentpool"));
	}
	
	public void readJSONshowcaption(JSONObject modelElement) {
		createExtendedAttribute("showcaption", modelElement.optString("showcaption"));
	}

	public void setParentLane(String laneId) {
		parentLane = laneId;
	}

	public void setParentPool(String poolId) {
		parentPool = poolId;
	}
	
	public void writeJSONstencil(JSONObject modelElement) throws JSONException {
		writeStencil(modelElement, "Lane");
	}
}