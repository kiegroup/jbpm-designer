package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Output extends FormControl {

	public Output() {
		super();
		attributes.put("value", null);
		attributes.put("mediatype", null);
	}
	
	@Override
	public String getTagName() {
		return "output";
	}
	
}
