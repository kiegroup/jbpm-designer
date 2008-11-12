package de.hpi.xforms;

public class Reset extends AbstractAction {

	public Reset() {
		super();
		attributes.put("model", null);
	}
	
	@Override
	public String getStencilId() {
		return "Reset";
	}
	
	@Override
	public String getTagName() {
		return "reset";
	}

}
