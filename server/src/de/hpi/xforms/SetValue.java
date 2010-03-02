package de.hpi.xforms;

public class SetValue extends AbstractAction {

	public SetValue() {
		super();
		attributes.put("ref", null);
		attributes.put("value", null);
	}
	
	@Override
	public String getStencilId() {
		return "SetValue";
	}
	
	@Override
	public String getTagName() {
		return "setvalue";
	}

}
