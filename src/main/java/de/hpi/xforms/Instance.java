package de.hpi.xforms;

import org.w3c.dom.Document;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Instance extends XFormsElement {
	
	protected Document content;
	
	public Instance() {
		super();
		attributes.put("src", null);
		attributes.put("resource", null);
	}
	
	public Document getContent() {
		return content;
	}
	
	public void setContent(Document content) {
		this.content = content;
	}
	
	@Override
	public String getTagName() {
		return "instance";
	}

}
