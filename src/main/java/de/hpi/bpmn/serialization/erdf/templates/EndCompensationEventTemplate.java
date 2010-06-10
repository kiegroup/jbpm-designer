package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndCompensationEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class EndCompensationEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new EndCompensationEventTemplate();
		}
		return instance;
	}

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		EndCompensationEvent e = (EndCompensationEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#EndCompensationEvent");
		appendOryxField(s,"eventtype","End");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"result","Compensation");
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
