package de.hpi.xforms;

public class Load extends AbstractAction {

	public Load() {
		super();
		attributes.put("ref", null);
		attributes.put("resource", null);
		attributes.put("show", null);
	}
	
	@Override
	public String getStencilId() {
		return "Load";
	}
	
	@Override
	public String getTagName() {
		return "load";
	}

}
