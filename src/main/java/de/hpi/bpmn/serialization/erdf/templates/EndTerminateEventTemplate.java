package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndTerminateEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class EndTerminateEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new EndTerminateEventTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		EndTerminateEvent e = (EndTerminateEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#EndTerminateEvent");
		appendOryxField(s,"eventtype","End");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"result","Terminate");
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
