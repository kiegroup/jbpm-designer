package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.Edge;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public abstract class ConnectorTemplate extends BPMN2ERDFTemplateImpl {

	protected void appendResourceEndPattern(StringBuilder s, Edge e, ERDFSerializationContext context) {
		s.append("<a rel=\"raziel-outgoing\" href=\"#resource" + 
				context.getResourceIDForDiagramObject(e.getTarget()) + "\"/>");
		s.append("<a rel=\"raziel-target\" href=\"#resource" + 
				context.getResourceIDForDiagramObject(e.getTarget()) + "\"/>");
		s.append("</div>");
	}

}
