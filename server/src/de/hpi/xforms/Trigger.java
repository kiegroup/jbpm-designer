package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Trigger extends FormControl {
	
	@Override
	public String getStencilId() {
		return "Trigger";
	}
	
	@Override
	public String getTagName() {
		return "trigger";
	}

}
