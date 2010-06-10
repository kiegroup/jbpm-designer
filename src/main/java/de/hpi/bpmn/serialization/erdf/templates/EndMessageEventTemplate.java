package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndMessageEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class EndMessageEventTemplate extends NonConnectorTemplate {

	
	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new EndMessageEventTemplate();
		}
		return instance;
	}

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		EndMessageEvent e = (EndMessageEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#EndMessageEvent");
		appendOryxField(s,"eventtype","End");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"result","Message");
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
