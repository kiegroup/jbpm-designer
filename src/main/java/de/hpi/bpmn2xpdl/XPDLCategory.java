package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("Category")
public class XPDLCategory extends XMLConvertible {
	
	@Attribute("Id")
	protected String id;
	@Text
	protected String content;
	
	public String getId() {
		return id;
	}
	
	public String getContent() {
		return content;
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(modelElement.optString("id")+"-category");
	}
	
	public void readJSONcategories(JSONObject modelElement) {
		setContent(modelElement.optString("categories"));
	}
	
	public void readJSONcategoryunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "categoryunknowns");
	}
	
	public void setId(String idValue) {
		id = idValue;
	}
	
	public void setContent(String contentValue) {
		content = contentValue;
	}
	
	public void writeJSONcategory(JSONObject modelElement) throws JSONException {
		modelElement.put("categories", getContent());
	}
	
	public void writeJSONcategoryunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "categoryunknowns");
	}
}
