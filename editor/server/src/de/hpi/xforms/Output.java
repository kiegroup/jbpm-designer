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
		attributes.remove("navindex");
		attributes.remove("accesskey");
	}
	
	@Override
	public String getStencilId() {
		return "Output";
	}
	
	@Override
	public String getTagName() {
		return "output";
	}
	
}
