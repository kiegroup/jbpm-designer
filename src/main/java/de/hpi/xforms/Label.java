package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Label extends XFormsElement implements PCDataContainer {
	
	protected String content;

	public Label() {
		super();
		attributes.put("ref", null);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String getStencilId() {
		return "Label";
	}
	
	@Override
	public String getTagName() {
		return "label";
	}

}
