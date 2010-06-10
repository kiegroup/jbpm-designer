package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.IntermediateSignalEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class IntermediateSignalEventTemplate extends NonConnectorTemplate {
	
	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new IntermediateSignalEventTemplate();
		}
		return instance;
	}	

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		IntermediateSignalEvent e = (IntermediateSignalEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		if (e.isThrowing()) {
			appendOryxField(s,"type",STENCIL_URI + "#IntermediateSignalEventThrowing");
		}
		else {
			appendOryxField(s,"type",STENCIL_URI + "#IntermediateSignalEventCatching");
		}
		appendOryxField(s,"eventtype","Intermediate");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"trigger","Signal");
		appendResourceLinkForBoundaryEvent(s, e, transformationContext);
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
