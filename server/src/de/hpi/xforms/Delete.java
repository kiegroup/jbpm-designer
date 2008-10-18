package de.hpi.xforms;

public class Delete extends AbstractAction {

	public Delete() {
		super();
		attributes.put("context", null);
		attributes.put("nodeset", null);
		attributes.put("at", null);
	}
	
	@Override
	public String getStencilId() {
		return "Delete";
	}
	
	@Override
	public String getTagName() {
		return "delete";
	}

}
