package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Upload extends FormControl {

	public Upload() {
		super();
		attributes.put("mediatype", null);
		attributes.put("incremental", null);
	}
	
	public String getTagName() {
		return "upload";
	}
	
}
