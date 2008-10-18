package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Input extends FormControl {

	public Input() {
		super();
		attributes.put("inputmode", null);
		attributes.put("incremental", null);
	}
	
	@Override
	public String getStencilId() {
		return "Input";
	}
	
	@Override
	public String getTagName() {
		return "input";
	}
	
}
