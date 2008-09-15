package de.hpi.xforms;

public class SetIndex extends AbstractAction {

	public SetIndex() {
		super();
		attributes.put("repeat", null);
		attributes.put("index", null);
	}
	
	@Override
	public String getTagName() {
		return "setindex";
	}

}
