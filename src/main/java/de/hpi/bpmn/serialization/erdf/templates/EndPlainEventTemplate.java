package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class EndPlainEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new EndPlainEventTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext context) {

		EndPlainEvent e = (EndPlainEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#EndEvent");
		appendOryxField(s,"eventtype","End");
		appendNonConnectorStandardFields(e,s,context);
		appendOryxField(s,"result","None");
		appendResourceEndPattern(s, e, context);
		
		return s;
		
	}

}
