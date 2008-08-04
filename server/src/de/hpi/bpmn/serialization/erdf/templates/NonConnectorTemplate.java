package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;


public abstract class NonConnectorTemplate extends BPMN2ERDFTemplateImpl {

	protected void appendResourceEndPattern(StringBuilder s, DiagramObject d, ERDFSerializationContext context) {
		
		for(Edge e : d.getOutgoingEdges()) {
			s.append("<a rel=\"raziel-outgoing\" href=\"#resource" + 
				context.getResourceIDForDiagramObject(e) + "\"/>");
		}
		
		s.append("</div>");
	}
	
	protected void appendResourceLinkForBoundaryEvent(StringBuilder s, IntermediateEvent i, ERDFSerializationContext context) {
		s.append("<a rel=\"raziel-parent\" href=\"#resource");
		s.append(context.getResourceIDForDiagramObject(i.getActivity()));
		s.append("\"/>");
	}
}
