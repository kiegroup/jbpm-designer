package de.hpi.xforms;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class Submission extends XFormsElement {
	
	//protected Header header;
	
	public Submission() {
		super();
		attributes.put("ref", null);
		attributes.put("resource", null);
		attributes.put("method", null);
		attributes.put("serialization", null);
		attributes.put("version", null);
		attributes.put("encoding", null);
		attributes.put("target", null);
	}

	public String getTagName() {
		return "submission";
	}

}
