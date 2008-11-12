package de.hpi.diagram;


public abstract class DiagramObject implements Comparable {

	protected String resourceId;
	protected String type;
	
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
}
