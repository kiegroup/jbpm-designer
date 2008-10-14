package de.hpi.xforms;

public class Insert extends AbstractAction {

	public Insert() {
		super();
		attributes.put("context", null);
		attributes.put("nodeset", null);
		attributes.put("at", null);
		attributes.put("position", null);
		attributes.put("origin", null);
	}
	
	@Override
	public String getStencilId() {
		return "Insert";
	}
	
	@Override
	public String getTagName() {
		return "insert";
	}

}
