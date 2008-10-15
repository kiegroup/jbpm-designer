package de.hpi.xforms;

public class Recalculate extends AbstractAction {

	public Recalculate() {
		super();
		attributes.put("model", null);
	}
	
	@Override
	public String getStencilId() {
		return "Recalculate";
	}
	
	@Override
	public String getTagName() {
		return "recalculate";
	}

}
