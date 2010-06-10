package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class StartPlainEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new StartPlainEventTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext context) {

		StartPlainEvent e = (StartPlainEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#StartEvent");
		appendOryxField(s,"eventtype","Start");
		appendNonConnectorStandardFields(e,s,context);
		appendOryxField(s,"trigger","None");
		appendResourceEndPattern(s, e, context);
		
		return s;
	}

}
