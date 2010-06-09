package de.hpi.bpmn2xpdl;

import java.util.Arrays;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Pool")
public class XPDLPool extends XPDLThingNodeGraphics {
	
	protected XPDLWorkflowProcess accordingProcess;
	
	@Attribute("BoundaryVisible")
	protected boolean boundaryVisible = true;
	@Attribute("MainPool")
	protected boolean mainPool = false;
	@Attribute("Process")
	protected String process;
	@Attribute("Orientation")
	protected String orientation = "HORIZONTAL";
	
	@Element("Lanes")
	protected XPDLLanes lanes;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				"Pool",
				"CollapsedPool"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public XPDLWorkflowProcess getAccordingProcess() {
		return accordingProcess;
	}
	
	public boolean getBoundaryVisible() {
		return boundaryVisible;
	}
	
	public XPDLLanes getLanes() {
		return lanes;
	}
	
	public boolean getMainPool() {
		return mainPool;
	}
	
	public String getProcess() {
		return process;
	}
	
	public String getOrientation() {
		return orientation;
	}
	
	public void readJSONadhoc(JSONObject modelElement) {
	}
	
	public void readJSONadhocordering(JSONObject modelElement) {
	}
	
	public void readJSONadhoccompletioncondition(JSONObject modelElement) {
	}
	
	public void readJSONboundaryvisible(JSONObject modelElement) {
		setBoundaryVisible(modelElement.optBoolean("boundaryvisible"));
	}
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
		JSONObject properties = modelElement.optJSONObject("properties");
		if (properties != null) {
			properties.put("resourceId", modelElement.optString("resourceId"));
			addLanes(modelElement, getProperId(properties), "pool");
		} else {
			addLanes(modelElement, getProperId(modelElement), "pool");
		}		
	}

	public void readJSONenableinstancecompensation(JSONObject modelElement) {
	}
	
	public void readJSONlanesunknowns(JSONObject modelElement) throws JSONException {
		initializeLanes();
		
		JSONObject passObject = new JSONObject();
		passObject.put("lanesunknowns", modelElement.optString("lanesunknowns"));
		getLanes().parse(passObject);
	}
	
	public void readJSONmainpool(JSONObject modelElement) {
		setMainPool(modelElement.optBoolean("mainpool"));
	}
	
	public void readJSONparticipantref(JSONObject modelElement) {
	}
	
	public void readJSONpoolcategories(JSONObject modelElement) throws JSONException {
		JSONObject categorieObject = new JSONObject();
		categorieObject.put("categories", modelElement.optString("poolcategories"));
		categorieObject.put("id", getProperId(modelElement));
		
		parse(categorieObject);
	}
	
	public void readJSONpooldocumentation(JSONObject modelElement) throws JSONException {
		JSONObject documentationObject = new JSONObject();
		documentationObject.put("documentation", modelElement.optString("pooldocumentation"));
		documentationObject.put("id", getProperId(modelElement));
		
		parse(documentationObject);
	}
	
	
	public void readJSONpoolid(JSONObject modelElement) {
		String test = getProperId(modelElement);
		setId(test);
	}
	
	public void readJSONprocesscategories(JSONObject modelElement) {
	}
	
	public void readJSONprocessdocumentation(JSONObject modelElement) {
	}
	
	public void readJSONprocessname(JSONObject modelElement) {
	}
	
	public void readJSONprocessref(JSONObject modelElement) {
	}
	
	public void readJSONprocesstype(JSONObject modelElement) {
	}
	
	public void readJSONprocessunknowns(JSONObject modelElement) {
	}
	
	public void readJSONstatus(JSONObject modelElement) {
	}
	
	public void readJSONsuppressjoinfailure(JSONObject modelElement) {
	}
	
	public void setAccordingProcess(XPDLWorkflowProcess processValue) {
		accordingProcess = processValue;
		setProcess(processValue.getId());
	}
	
	public void setBoundaryVisible(boolean visibility) {
		boundaryVisible = visibility;
	}
	
	public void setLanes(XPDLLanes lanesValue) {
		lanes = lanesValue;
	}
	
	public void setMainPool(boolean isMainPool) {
		mainPool = isMainPool;
	}
	
	public void setOrientation(String orientationValue) {
		orientation = orientationValue;
	}
	
	public void setProcess(String processValue) {
		process = processValue;
	}
	
	public void writeJSONaccordingprocess(JSONObject modelElement) {
		XPDLWorkflowProcess process = getAccordingProcess();
		if (process != null) {
			process.write(modelElement);
		}
	}
	
	public void writeJSONboundaryvisible(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "boundaryvisible", getBoundaryVisible());
	}
	
	public void writeJSONmainpool(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "mainpool", getMainPool());
	}
	
	public void writeJSONprocessunknowns(JSONObject modelElement) {
	}
	
	public void writeJSONlanes(JSONObject modelElement) {
		XPDLLanes lanesList = getLanes();
		if (lanesList != null) {
			lanesList.write(modelElement);
		}
	}
	
	public void writeJSONstencil(JSONObject modelElement) throws JSONException {
		writeStencil(modelElement, "Pool");
	}
	
	public void writeMainPool(JSONObject modelElement) throws JSONException {
		XPDLWorkflowProcess processOfPool = getAccordingProcess();
		if (processOfPool != null) {
			processOfPool.writeChildrenOnly(modelElement);
		}
	}
	
	protected void addLanes(JSONObject modelElement, String parentId, String parentType) throws JSONException {
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		if(childShapes != null) {
			for(int i = 0; i < childShapes.length(); i++) {
				JSONObject childShape = childShapes.optJSONObject(i);
				String stencil = childShape.optJSONObject("stencil").optString("id");
				
				if(XPDLLane.handlesStencil(stencil)) {
					childShape.put("parent" + parentType, parentId);
					XPDLLane nextLane = createLane(childShape);
					
					addLanes(childShape, nextLane.getId(), "lane");
				} else if (XPDLTransition.handlesStencil(stencil)) {
					passToAccodingProcess(childShape);
					addLanes(childShape, parentId, parentType);
				} else if (XPDLActivity.handlesStencil(stencil)) {
					passToAccodingProcess(childShape);
					addLanes(childShape, parentId, parentType);
				} else {
					addLanes(childShape, parentId, parentType);
				}
			}
		}		
	}
	
	protected XPDLLane createLane(JSONObject modelElement) {
		initializeLanes();
		
		XPDLLane nextLane = new XPDLLane();
		nextLane.setResourceIdToShape(getResourceIdToShape());
		nextLane.parse(modelElement);
		getLanes().add(nextLane);
		
		return nextLane;
	}
	
	protected String getProperId(JSONObject modelElement) {
		String idValue = modelElement.optString("poolid");
		if (!idValue.equals("")) {
			return idValue;
		}
		return modelElement.optString("resourceId");
	}
	
	protected void initializeLanes() {
		if(getLanes() == null) {
			setLanes(new XPDLLanes());
		}
	}
	
	protected void passToAccodingProcess(JSONObject modelElement) throws JSONException {
		JSONArray childShapes = new JSONArray();
		childShapes.put(modelElement);
		
		JSONObject passObject = new JSONObject();
		passObject.put("childShapes", childShapes);
		
		getAccordingProcess().parse(passObject);
	}

	public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
		if (getLanes() != null) {
			getLanes().createAndDistributeMapping(mapping);
		}
	}
}