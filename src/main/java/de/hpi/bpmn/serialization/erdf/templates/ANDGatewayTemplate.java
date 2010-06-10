package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class ANDGatewayTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new ANDGatewayTemplate();
		}
		return instance;
	}

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {
		
		ANDGateway g = (ANDGateway) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(g));
		
		appendOryxField(s,"type",STENCIL_URI + "#AND_Gateway");
		appendNonConnectorStandardFields(g,s,transformationContext);
		
		appendResourceEndPattern(s, diagramObject, transformationContext);
		return s;
	}

}
