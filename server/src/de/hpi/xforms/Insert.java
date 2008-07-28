package de.hpi.xforms;

public class Insert extends AbstractAction {

	public Insert() {
		super();
		attributes.put("context", null);
		attributes.put("nodeset", null);
		attributes.put("at", null);
		attributes.put("origin", null);
	}
	
	@Override
	public String getTagName() {
		return "insert";
	}

}
