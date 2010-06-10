package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class ORGatewayTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new ORGatewayTemplate();
		}
		return instance;
	}

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {
		
		ORGateway g = (ORGateway) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(g));
		
		appendOryxField(s,"type",STENCIL_URI + "#OR_Gateway");
		appendNonConnectorStandardFields(g,s,transformationContext);
		
		appendResourceEndPattern(s, diagramObject, transformationContext);
		return s;
	}

}
