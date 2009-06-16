package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Value extends XFormsElement implements PCDataContainer {
	
	protected String content;

	public Value() {
		super();
		attributes.put("ref", null);
		attributes.put("bind", null);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getTagName() {
		return "value";
	}

}
