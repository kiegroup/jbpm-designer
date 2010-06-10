package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.StartMultipleEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class StartMultipleEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new StartMultipleEventTemplate();
		}
		return instance;
	}	

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		StartMultipleEvent e = (StartMultipleEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#StartMultipleEvent");
		appendOryxField(s,"eventtype","Start");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"trigger","Multiple");
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
