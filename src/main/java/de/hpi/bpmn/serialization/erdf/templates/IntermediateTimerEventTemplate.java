package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.IntermediateTimerEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class IntermediateTimerEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new IntermediateTimerEventTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext context) {

		IntermediateTimerEvent e = (IntermediateTimerEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#IntermediateTimerEvent");
		appendOryxField(s,"eventtype","Intermediate");
		appendNonConnectorStandardFields(e,s,context);
		appendOryxField(s,"trigger","Timer");
		appendResourceLinkForBoundaryEvent(s, e, context);
		appendResourceEndPattern(s, e, context);
		
		return s;
	}

}
