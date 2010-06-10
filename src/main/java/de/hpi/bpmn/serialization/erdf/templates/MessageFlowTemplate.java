package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.MessageFlow;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class MessageFlowTemplate extends ConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new MessageFlowTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		MessageFlow e = (MessageFlow) diagramObject;
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#MessageFlow");
		appendStandardFields(s);
		appendDockerInformation(s, e);
		appendBounds(s, e);
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
