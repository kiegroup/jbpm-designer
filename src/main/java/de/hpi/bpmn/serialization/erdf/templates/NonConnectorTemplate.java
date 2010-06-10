package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;
import de.hpi.util.Bounds;


public abstract class NonConnectorTemplate extends BPMN2ERDFTemplateImpl {

	protected void appendResourceEndPattern(StringBuilder s, DiagramObject d, ERDFSerializationContext context) {
		
		for(Edge e : d.getOutgoingEdges()) {
			s.append("<a rel=\"raziel-outgoing\" href=\"#resource" + 
				context.getResourceIDForDiagramObject(e) + "\"/>");
		}
		
		s.append("</div>");
	}
	
	protected void appendResourceLinkForBoundaryEvent(StringBuilder s, IntermediateEvent i, ERDFSerializationContext context) {
		// is the event attached to an activity at all?
		if (i.getActivity() == null)
			return;
		
		s.append("<a rel=\"raziel-parent\" href=\"#resource");
		s.append(context.getResourceIDForDiagramObject(i.getActivity()));
		s.append("\"/>");
	}
	
	protected void appendNonConnectorStandardFields(Node n, StringBuilder s, ERDFSerializationContext context) {
		appendStandardFields(s);
		appendOryxField(s,"name",n.getLabel());
		Bounds b = n.getBounds();
		if (b != null){
			appendOryxField(s,"bounds",b.toString());
		}
		
		Container parent = n.getParent();
		if(parent != null)
			if(n.getParent().getClass().equals(SubProcess.class)) {
				String id = context.getResourceIDForDiagramObject((SubProcess)parent);
				s.append("<a rel=\"raziel-parent\" href=\"#resource" + id + "\"/>");
			}
	}
	
}
