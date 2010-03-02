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
		attributes.put("action", null);
		attributes.put("mode", null);
		attributes.put("method", null);
		attributes.put("validate", null);
		attributes.put("relevant", null);
		attributes.put("serialization", null);
		attributes.put("version", null);
		attributes.put("indent", null);
		attributes.put("mediatype", null);
		attributes.put("encoding", null);
		attributes.put("omit-xml-declaration", null);
		attributes.put("standalone", null);
		attributes.put("cdata-section-elements", null);
		attributes.put("replace", null);
		attributes.put("instance", null);
		attributes.put("target", null);
		attributes.put("separator", null);
		attributes.put("includenamespaceprefixes", null);
	}
	
	@Override
	public String getTagName() {
		return "submission";
	}

}
