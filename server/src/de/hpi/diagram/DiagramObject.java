package de.hpi.diagram;

import java.util.Map;


public abstract class DiagramObject implements Comparable {

	protected String resourceId;
	protected String type;
	protected Map<String, String> properties;
	
	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int compareTo(Object o){
		return this.getResourceId().compareTo(((DiagramObject)o).getResourceId());
	}
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public String getPropertyValue(String key) {
		if (properties != null) {
			return properties.get(key);
		} else {
			return null;
		}
	}
	
	public boolean hasPropertyValue(String key) {
		return (properties != null) && properties.containsKey(key);
	}
}
