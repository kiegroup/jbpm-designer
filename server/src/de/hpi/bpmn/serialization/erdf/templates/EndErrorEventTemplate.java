package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndCompensationEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class EndErrorEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new EndErrorEventTemplate();
		}
		return instance;
	}

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		EndCompensationEvent e = (EndCompensationEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#EndErrorEvent");
		appendOryxField(s,"eventtype","End");
		appendStandardFields(s);
		appendOryxField(s,"result","Error");
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
