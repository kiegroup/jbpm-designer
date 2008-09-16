package de.hpi.xforms;

public class Refresh extends AbstractAction {

	public Refresh() {
		super();
		attributes.put("model", null);
	}
	
	@Override
	public String getTagName() {
		return "refresh";
	}

}
