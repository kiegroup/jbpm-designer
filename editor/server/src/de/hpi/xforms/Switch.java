package de.hpi.xforms;

import java.util.ArrayList;
import java.util.List;

public class Switch extends XFormsUIElement {
	
	protected List<Case> cases;

	public Switch() {
		super();
		attributes.put("ref", null);
	}
	
	public List<Case> getCases() {
		if(cases==null)
			cases = new ArrayList<Case>();
		return cases;
	}
	
	@Override
	public String getStencilId() {
		return "Switch";
	}
	
	@Override
	public String getTagName() {
		return "switch";
	}

}
