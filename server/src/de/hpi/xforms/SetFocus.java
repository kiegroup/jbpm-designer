package de.hpi.xforms;

public class SetFocus extends AbstractAction {

	public SetFocus() {
		super();
		attributes.put("control", null);
	}
	
	@Override
	public String getStencilId() {
		return "SetFocus";
	}
	
	@Override
	public String getTagName() {
		return "setfocus";
	}

}
