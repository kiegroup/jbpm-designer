package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.Activity;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public abstract class ConnectorTemplate extends BPMN2ERDFTemplateImpl {

	protected void appendResourceEndPattern(StringBuilder s, Edge e, ERDFSerializationContext context) {
		s.append("<a rel=\"raziel-outgoing\" href=\"#resource" + 
				context.getResourceIDForDiagramObject(e.getTarget()) + "\"/>");
		s.append("<a rel=\"raziel-target\" href=\"#resource" + 
				context.getResourceIDForDiagramObject(e.getTarget()) + "\"/>");
		s.append("</div>");
	}
	
	protected void appendDockerInformation(StringBuilder s, Edge e){
		String dockers = "";
		if (e.getSource() instanceof Activity){
			dockers += "50 40 ";
		} else if  (e.getSource() instanceof Gateway){ 
			dockers += "20 20 ";
		} else {
			dockers += "15 15 ";
		}
		if (e.getTarget() instanceof Activity){
			dockers += "50 40 ";
		} else if (e.getTarget() instanceof Gateway){
			dockers += "20 20 ";
		} else {
			dockers += "15 15 ";
		}
		appendOryxField(s,"dockers",dockers+" #");
	}

}
