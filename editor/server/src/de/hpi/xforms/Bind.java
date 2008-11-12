package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Bind extends XFormsElement {

	public Bind() {
		super();
		attributes.put("nodeset", null);
		attributes.put("type", null);
		attributes.put("readonly", null);
		attributes.put("required", null);
		attributes.put("relevant", null);
		attributes.put("calculate", null);
		attributes.put("constraint", null);
		attributes.put("p3ptype", null);
	}
	
	@Override
	public String getTagName() {
		return "bind";
	}

}
