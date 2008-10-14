package de.hpi.xforms;

public class Rebuild extends AbstractAction {

	public Rebuild() {
		super();
		attributes.put("model", null);
	}
	
	@Override
	public String getStencilId() {
		return "Rebuild";
	}
	
	@Override
	public String getTagName() {
		return "rebuild";
	}

}
