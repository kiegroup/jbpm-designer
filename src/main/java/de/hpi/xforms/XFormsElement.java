package de.hpi.xforms;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public abstract class XFormsElement {
	
	protected Map<String, String> attributes;
	protected String resourceId;

	public XFormsElement() {
		super();
		attributes = new HashMap<String, String>();
		attributes.put("id", null);
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public Map<String, String> getAttributes() {
		return attributes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final XFormsElement other = (XFormsElement) obj;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		return true;
	}
	
	public String getStencilId() {
		return null;
	}
	
	public abstract String getTagName();
	
}
