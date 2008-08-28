package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class ComplexGatewayTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new ComplexGatewayTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {
		
		ComplexGateway g = (ComplexGateway) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(g));
		
		appendOryxField(s,"type",STENCIL_URI + "#Complex_Gateway");
		appendNonConnectorStandardFields(g,s);
		
		appendResourceEndPattern(s, diagramObject, transformationContext);
		return s;
	}

}
