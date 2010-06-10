package de.hpi.bpmn.serialization.erdf.templates;



public abstract class BPMN2ERDFTemplateImpl implements BPMN2ERDFTemplate {

	protected static final String STENCIL_URI = "http://b3mn.org/stencilset/bpmn1.1";
	
	protected StringBuilder getResourceStartPattern(String resourceID) {
		StringBuilder s = new StringBuilder();
		
		s.append("<div id=\"resource"+ resourceID +"\">");
	
		return s;
	}
	
	protected void appendOryxField(StringBuilder s, String field, String entry) {
		s.append("<span class=\"oryx-");
		s.append(field);
		if (entry != null) {
			s.append("\">");
			s.append(entry);
			s.append("</span>");
		}
		else {
			s.append("\"/>");
		}
	}
	
	protected void appendStandardFields(StringBuilder s) {
		appendOryxField(s,"id",null);
		appendOryxField(s,"categories",null);
		appendOryxField(s,"documentation",null);
		appendOryxField(s,"pool",null);
		appendOryxField(s,"lanes",null);
	}
}
