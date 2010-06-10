package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.IntermediateErrorEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class IntermediateErrorEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new IntermediateErrorEventTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext context) {
		
		IntermediateErrorEvent e = (IntermediateErrorEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#IntermediateErrorEvent");
		appendOryxField(s,"eventtype","Intermediate");
		appendNonConnectorStandardFields(e,s,context);
		appendOryxField(s,"trigger","Error");
		appendResourceLinkForBoundaryEvent(s, e, context);
		appendResourceEndPattern(s, e, context);
		
		return s;
	}

}
