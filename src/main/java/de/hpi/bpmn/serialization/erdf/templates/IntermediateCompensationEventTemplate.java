package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.IntermediateCompensationEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class IntermediateCompensationEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new IntermediateCompensationEventTemplate();
		}
		return instance;
	}	

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		IntermediateCompensationEvent e = (IntermediateCompensationEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		if (e.isThrowing()) {
			appendOryxField(s,"type",STENCIL_URI + "#IntermediateCompensationEventThrowing");
		}
		else {
			appendOryxField(s,"type",STENCIL_URI + "#IntermediateCompensationEventCatching");
		}
		appendOryxField(s,"eventtype","Intermediate");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"trigger","Compensation");
		appendResourceLinkForBoundaryEvent(s, e, transformationContext);
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
