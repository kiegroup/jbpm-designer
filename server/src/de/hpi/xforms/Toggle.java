package de.hpi.xforms;

public class Toggle extends AbstractAction {

	public Toggle() {
		super();
		attributes.put("case", null);
	}
	
	@Override
	public String getStencilId() {
		return "Toggle";
	}
	
	@Override
	public String getTagName() {
		return "toggle";
	}

}
