package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndSignalEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class EndSignalEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new EndSignalEventTemplate();
		}
		return instance;
	}

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		EndSignalEvent e = (EndSignalEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#EndSignalEvent");
		appendOryxField(s,"eventtype","End");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"result","Signal");
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
		
	}

}
