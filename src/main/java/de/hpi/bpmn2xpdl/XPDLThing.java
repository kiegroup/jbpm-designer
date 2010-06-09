package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.diagram.OryxUUID;

@RootElement("Thing")
public abstract class XPDLThing extends XMLConvertible {

	@Attribute("Id")
	protected String id;
	@Attribute("Name")
	protected String name;
	protected String resourceId;
	
	@Element("Object")
	protected XPDLObject object;
	@Element("ExtendedAttributes")
	protected XPDLExtendedAttributes extendedAttributes;
	
	public XPDLExtendedAttributes getExtendedAttributes() {
		return extendedAttributes;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public XPDLObject getObject() {
		return object;
	}
	
	public String getResourceId() {
		return resourceId;
	}
	
	public void readJSONcategories(JSONObject modelElement) throws JSONException {
		passInformationToObject(modelElement, "categories");
	}
	
	public void readJSONcategoriesunknowns(JSONObject modelElement) throws JSONException {
		passInformationToObject(modelElement, "categoriesunknowns");
	}
	
	public void readJSONcategoryunknowns(JSONObject modelElement) throws JSONException {
		passInformationToObject(modelElement, "categoryunknowns");
	}
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
	}
	
	public void readJSONdocumentation(JSONObject modelElement) throws JSONException {
		passInformationToObject(modelElement, "documentation");
	}
	
	public void readJSONdocumentationunknowns(JSONObject modelElement) throws JSONException {
		passInformationToObject(modelElement, "documentationunknowns");
	}
	
	public void readJSONlanes(JSONObject modelElement) {
	}
	
	public void readJSONbgcolor(JSONObject modelElement) throws JSONException {
	}
	
	public void readJSONdockers(JSONObject modelElement) throws JSONException {
		JSONArray dockers = modelElement.optJSONArray("dockers");
		
		if (dockers != null) {
			if (dockers.length() >= 2) {
				JSONObject firstDocker = dockers.optJSONObject(0);
				createExtendedAttribute("docker" + 0 + "X", firstDocker.optString("x"));
				
				JSONObject lastDocker = dockers.optJSONObject(dockers.length()-1);
				createExtendedAttribute("docker" + String.valueOf(dockers.length()-1) + "Y", lastDocker.optString("y"));
			}
		}
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(getProperId(modelElement));
	}

	public void readJSONname(JSONObject modelElement) {
		setName(modelElement.optString("name"));
	}
	
	public void readJSONobjectunknowns(JSONObject modelElement) throws JSONException {
		passInformationToObject(modelElement, "objectunknowns");
	}
	
	public void readJSONoutgoing(JSONObject modelElement) {
	}
	
	public void readJSONpool(JSONObject modelElement) {
	}

	public void readJSONproperties(JSONObject modelElement) throws JSONException {
		JSONObject properties = modelElement.optJSONObject("properties");
		properties.put("resourceId", modelElement.optString("resourceId"));
		parse(properties);
	}

	public void readJSONresourceId(JSONObject modelElement) throws JSONException {
		setResourceId(modelElement.optString("resourceId"));
		if (!modelElement.has("outgoing")) {
			setId(getProperId(modelElement));	
		}
	}
	
	public void readJSONstencil(JSONObject modelElement) {
	}
	
	public void readJSONunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "unknowns");
	}
	
	public void setExtendedAttributes(XPDLExtendedAttributes attributes) {
		extendedAttributes = attributes;
	}
	
	public void setId(String idValue) {
		id = idValue;
	}

	public void setName(String nameValue) {
		name = nameValue;
	}
	
	public void setObject(XPDLObject objectValue) {
		object = objectValue;
	}
	
	public void setResourceId(String idValue) {
		resourceId = idValue;
	}
	
	public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
		if (modelElement.optJSONArray("childShapes") == null) {
			modelElement.put("childShapes", new JSONArray());
		}
	}
	
	public void writeJSONid(JSONObject modelElement) throws JSONException {
		String idValue = getId();
		if (idValue == null) {
			putProperty(modelElement, "id", "");
		} else {
			putProperty(modelElement, "id", idValue);
		}
	}
	
	public void writeJSONname(JSONObject modelElement) throws JSONException {
		String nameValue = getName();
		if (nameValue == null) {
			putProperty(modelElement, "name", "");
		} else {
			putProperty(modelElement, "name", nameValue);
		}
	}
	
	public void writeJSONobject(JSONObject modelElement) throws JSONException {
		XPDLObject containedObject = getObject();
		
		if (containedObject != null) {
			initializeProperties(modelElement);
			containedObject.write(getProperties(modelElement));
		}
	}
	
	public void writeJSONoutgoing(JSONObject modelElement) throws JSONException {
		JSONArray outgoing = new JSONArray();
		if(getResourceIdToObject()==null){
			modelElement.put("outgoing", outgoing);
			return;
		}
			
		// FIXME get all outgoing for a shape
		for(Entry<String, XPDLThing> entry: getResourceIdToObject().entrySet()){
			XPDLThing thing= entry.getValue();
			if(thing instanceof XPDLAssociation){
				if(getId().equals(((XPDLAssociation) thing).getSource()))
						outgoing.put(resourceIdToJSONObject(entry.getKey()));
			}else if(thing instanceof XPDLMessageFlow){
				if(getId().equals(((XPDLMessageFlow) thing).getSource()))
						outgoing.put(resourceIdToJSONObject(entry.getKey()));
			}else if(thing instanceof XPDLTransition){
				if(getId().equals(((XPDLTransition) thing).getFrom()))
						outgoing.put(resourceIdToJSONObject(entry.getKey()));
			}
		}
		
		modelElement.put("outgoing", outgoing);

	}
	
	protected JSONObject resourceIdToJSONObject(String id) throws JSONException{
		JSONObject target = new JSONObject();
		target.put("resourceId", id);
		return target;
	}
	public void writeJSONresourceId(JSONObject modelElement) throws JSONException {
		if (getId() != null) {
			modelElement.put("resourceId", getId());
		} else {
			modelElement.put("resourceId", OryxUUID.generate());
		}
	}
	
	public void writeJSONunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "unknowns");
	}
	
	protected void createExtendedAttribute(String key, String value) {
		initializeExtendedAttributes();
		
		XPDLExtendedAttribute attribute = new XPDLExtendedAttribute();
		attribute.setName(key);
		attribute.setValue(value);
		
		getExtendedAttributes().add(attribute);
	}
	
	protected String fetchExtendedAttribute(String key) {
		ArrayList<XPDLExtendedAttribute> attributes = getExtendedAttributes().getExtendedAttributes();
		
		for (int i = 0; i < attributes.size(); i++) {
			XPDLExtendedAttribute attribute = attributes.get(i);
			if (attribute.getName().equals(key)) {
				String value = attribute.getValue();
				attributes.remove(i);
				return value;
			}
		}
		return null;
	}
	
	protected String getProperId(JSONObject modelElement) {
		String idValue = modelElement.optString("id");;
		
		if (!idValue.equals("")) {
			return modelElement.optString("id");
		}
		return modelElement.optString("resourceId");
	}
	
	protected JSONObject getProperties(JSONObject modelElement) {
		return modelElement.optJSONObject("properties");
	}
	
	protected void initializeExtendedAttributes() {
		if (getExtendedAttributes() == null) {
			setExtendedAttributes(new XPDLExtendedAttributes());
		}
	}
	
	protected void initializeObject() {
		if (getObject() == null) {
			setObject(new XPDLObject());
		}
	}
	
	protected void initializeProperties(JSONObject modelElement) throws JSONException {
		JSONObject properties = modelElement.optJSONObject("properties");
		if (properties == null) {
			JSONObject newProperties = new JSONObject();
			modelElement.put("properties", newProperties);
			properties = newProperties;
		}
	}
	
	protected void passInformationToObject(JSONObject modelElement, String key) throws JSONException {
		initializeObject();
		
		JSONObject passObject = new JSONObject();
		passObject.put("id", getProperId(modelElement));
		passObject.put(key, modelElement.optString(key));
		
		getObject().parse(passObject);
	}
	
	protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
		initializeProperties(modelElement);
		
		getProperties(modelElement).put(key, value);
	}
	
	protected void putProperty(JSONObject modelElement, String key, boolean value) throws JSONException {
		initializeProperties(modelElement);
		
		getProperties(modelElement).put(key, value);
	}
	
	protected void writeStencil(JSONObject modelElement, String stencil) throws JSONException {
		JSONObject stencilObject = new JSONObject();
		stencilObject.put("id", stencil);
		
		modelElement.put("stencil", stencilObject);
	}
}
