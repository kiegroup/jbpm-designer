package de.hpi.xforms;

public class Dispatch extends AbstractAction {

	public Dispatch() {
		super();
		attributes.put("name", null);
		attributes.put("target", null);
		attributes.put("delay", null);
		attributes.put("bubbles", null);
		attributes.put("cancelable", null);
	}
	
	@Override
	public String getStencilId() {
		return "Dispatch";
	}
	
	@Override
	public String getTagName() {
		return "dispatch";
	}

}
