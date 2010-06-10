package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.IntermediateLinkEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class IntermediateLinkEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new IntermediateLinkEventTemplate();
		}
		return instance;
	}	

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		IntermediateLinkEvent e = (IntermediateLinkEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		if (e.isThrowing()) {
			appendOryxField(s,"type",STENCIL_URI + "#IntermediateLinkEventThrowing");
		}
		else {
			appendOryxField(s,"type",STENCIL_URI + "#IntermediateLinkEventCatching");
		}
		appendOryxField(s,"eventtype","Intermediate");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"trigger","Link");
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
