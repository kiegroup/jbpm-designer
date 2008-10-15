package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Textarea extends FormControl {

	public Textarea() {
		super();
		attributes.put("inputmode", null);
		attributes.put("incremental", null);
	}
	
	@Override
	public String getStencilId() {
		return "Textarea";
	}
	
	@Override
	public String getTagName() {
		return "textarea";
	}
	
}
