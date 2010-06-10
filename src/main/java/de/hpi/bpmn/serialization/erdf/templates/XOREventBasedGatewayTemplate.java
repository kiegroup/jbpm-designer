package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.XOREventBasedGateway;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class XOREventBasedGatewayTemplate extends NonConnectorTemplate {
	
	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new XOREventBasedGatewayTemplate();
		}
		return instance;
	}

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext context) {
		
		XOREventBasedGateway g = (XOREventBasedGateway) diagramObject;
		
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(g));
		
		appendOryxField(s,"type",STENCIL_URI + "#Exclusive_Eventbased_Gateway");
		appendNonConnectorStandardFields(g,s,context);
		
		appendResourceEndPattern(s, diagramObject, context);
		return s;
	}

}
