package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.IntermediateConditionalEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class IntermediateConditionalEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new IntermediateConditionalEventTemplate();
		}
		return instance;
	}	

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		IntermediateConditionalEvent e = (IntermediateConditionalEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#IntermediateConditionalEvent");
		appendOryxField(s,"eventtype","Intermediate");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"trigger","Conditional");
		appendResourceLinkForBoundaryEvent(s, e, transformationContext);
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
