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
	
	public String getTagName() {
		return "textarea";
	}
	
}
