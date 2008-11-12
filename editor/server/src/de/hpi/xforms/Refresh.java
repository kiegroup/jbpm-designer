package de.hpi.xforms;

public class Refresh extends AbstractAction {

	public Refresh() {
		super();
		attributes.put("model", null);
	}
	
	@Override
	public String getStencilId() {
		return "Refresh";
	}
	
	@Override
	public String getTagName() {
		return "refresh";
	}

}
