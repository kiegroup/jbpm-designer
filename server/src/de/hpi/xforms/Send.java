package de.hpi.xforms;

public class Send extends AbstractAction {

	public Send() {
		super();
		attributes.put("submission", null);
	}
	
	@Override
	public String getStencilId() {
		return "Send";
	}
	
	@Override
	public String getTagName() {
		return "send";
	}

}
