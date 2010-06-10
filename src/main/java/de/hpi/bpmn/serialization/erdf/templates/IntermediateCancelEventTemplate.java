package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.IntermediateCancelEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class IntermediateCancelEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new IntermediateCancelEventTemplate();
		}
		return instance;
	}	

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		IntermediateCancelEvent e = (IntermediateCancelEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#IntermediateCancelEvent");
		appendOryxField(s,"eventtype","Intermediate");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"trigger","Cancel");
		appendResourceLinkForBoundaryEvent(s, e, transformationContext);
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
