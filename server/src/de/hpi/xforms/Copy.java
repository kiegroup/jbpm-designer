package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Copy extends XFormsElement {
	
	protected String content;

	public Copy() {
		super();
		attributes.put("ref", null);
		attributes.put("bind", null);
	}
	
	public String getTagName() {
		return "copy";
	}

}
