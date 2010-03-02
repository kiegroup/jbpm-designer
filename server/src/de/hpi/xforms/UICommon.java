package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public abstract class UICommon extends XFormsElement {
	
	public UICommon() {
		super();
		attributes.put("ref", null);
		attributes.put("bind", null);
	}
	
}
