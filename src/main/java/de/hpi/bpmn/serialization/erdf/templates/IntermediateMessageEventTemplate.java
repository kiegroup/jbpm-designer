package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class IntermediateMessageEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new IntermediateMessageEventTemplate();
		}
		return instance;
	}	

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext context) {
		
		IntermediateMessageEvent e = (IntermediateMessageEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(e));
		if (e.isThrowing()) {
			appendOryxField(s,"type",STENCIL_URI + "#IntermediateMessageEventThrowing");
		}
		else {
			appendOryxField(s,"type",STENCIL_URI + "#IntermediateMessageEventCatching");
		}
		appendOryxField(s,"eventtype","Intermediate");
		appendNonConnectorStandardFields(e,s,context);
		appendOryxField(s,"trigger","Message");
		appendResourceLinkForBoundaryEvent(s, e, context);
		appendResourceEndPattern(s, e, context);
		
		return s;
	}

}
