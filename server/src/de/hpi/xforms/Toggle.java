package de.hpi.xforms;

public class Toggle extends AbstractAction {

	public Toggle() {
		super();
		attributes.put("case", null);
	}
	
	@Override
	public String getTagName() {
		return "toggle";
	}

}
