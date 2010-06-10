package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class XORDataBasedGatewayTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new XORDataBasedGatewayTemplate();
		}
		return instance;
	}

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext context) {
		
		XORDataBasedGateway g = (XORDataBasedGateway) diagramObject;
		
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(g));
		
		appendOryxField(s,"type",STENCIL_URI + "#Exclusive_Databased_Gateway");
		appendNonConnectorStandardFields(g,s,context);
		
		appendResourceEndPattern(s, diagramObject, context);
		return s;
	}

}
