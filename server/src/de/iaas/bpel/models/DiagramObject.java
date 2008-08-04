package de.iaas.bpel.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2008 Zhen Peng
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public abstract class DiagramObject {

	protected String id;
	protected String resourceId;
	protected Map<String, String> properties;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null)
			id = id.replace("#", "");
		this.id = id;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public void setProperty(String key, String value){
		if (properties == null){
			properties = new HashMap();
		}
		properties.put(key, value);
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}

	public boolean equals(Object other)
	{
		if (resourceId != null && other instanceof DiagramObject)
			return this.resourceId.equals(((DiagramObject) other).getResourceId());
		else if (this == other)
			return true;
		else
			return false;
	}
}
