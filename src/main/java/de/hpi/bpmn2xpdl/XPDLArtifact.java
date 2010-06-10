package de.hpi.bpmn2xpdl;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Artifact")
public class XPDLArtifact extends XPDLThingNodeGraphics {
	
	@Attribute("ArtifactType")
	protected String artifactType;
	@Attribute("TextAnnotation")
	protected String textAnnotation;
	
	@Element("DataObject")
	protected XPDLDataObject dataObject;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				"DataObject",
				"TextAnnotation",
				"Group"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public String getArtifactType() {
		return artifactType;
	}
	
	public XPDLDataObject getDataObject() {
		return dataObject;
	}
	
	public String getTextAnnotation() {
		return textAnnotation;
	}
	
	public void readJSONartifacttype(JSONObject modelElement) {
		setArtifactType(modelElement.optString("artifacttype"));
	}
	
	public void readJSONdataobjectunknowns(JSONObject modelElement) throws JSONException {
		passInformationToDataObject(modelElement, "dataobjectunknowns");
	}
	
	public void readJSONitems(JSONObject modelElement) {
	}
	
	public void readJSONname(JSONObject modelElement) {
		if (modelElement.optString("artifacttype").equals("DataObject")) {
			initializeDataObject();
			getDataObject().setName(modelElement.optString("name"));
		} else {
			super.readJSONname(modelElement);
		}
	}
	
	public void readJSONproducedatcompletion(JSONObject modelElement) throws JSONException {
		passInformationToDataObject(modelElement, "producedatcompletion");
	}
	
	public void readJSONrequiredforstart(JSONObject modelElement) throws JSONException {
		passInformationToDataObject(modelElement, "requiredforstart");
	}
	
	public void readJSONstate(JSONObject modelElement) throws JSONException {
		passInformationToDataObject(modelElement, "state");
	}
	
	public void readJSONtext(JSONObject modelElement) {
		setTextAnnotation(modelElement.optString("text"));
	}
	
	public void readJSONtotalCount(JSONObject modelElement) {
	}
	
	public void setArtifactType(String type) {
		artifactType = type;
	}
	
	public void setDataObject(XPDLDataObject dataObjectValue) {
		dataObject = dataObjectValue;
	}
	
	public void setTextAnnotation(String annotation) {
		textAnnotation = annotation;
	}
	
	public void writeJSONartifacttype(JSONObject modelElement) throws JSONException {
		String type = getArtifactType();
		
		if (type != null) {
			if (type.equals("Group")) {
				putProperty(modelElement, "artifacttype", "Group");
				writeStencil(modelElement, "Group");				
			} else if (type.equals("Annotation")) {
				putProperty(modelElement, "artifacttype", "Annotation");
				putProperty(modelElement, "text", getTextAnnotation());
				writeStencil(modelElement, "TextAnnotation");
			} else {
				writeDataObject(modelElement);
			}
		} else {
			writeDataObject(modelElement);
		}
	}
	
	protected void initializeDataObject() {
		if (getDataObject() == null) {
			setDataObject(new XPDLDataObject());
		}
	}
	
	protected void passInformationToDataObject(JSONObject modelElement, String key) throws JSONException {
		initializeDataObject();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		getDataObject().parse(passObject);
	}
	
	protected void writeDataObject(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "artifacttype", "DataObject");
		
		writeStencil(modelElement, "DataObject");
		XPDLDataObject object = getDataObject();
		if (object != null) {
			object.write(modelElement);
		}
	}	
}
