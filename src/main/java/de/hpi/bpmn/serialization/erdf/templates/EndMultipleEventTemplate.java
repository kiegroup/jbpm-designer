package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndMultipleEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class EndMultipleEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new EndPlainEventTemplate();
		}
		return instance;
	}

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		EndMultipleEvent e = (EndMultipleEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#EndMultipleEvent");
		appendOryxField(s,"eventtype","End");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"result","Multiple");
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
