package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.StartSignalEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class StartSignalEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new StartSignalEventTemplate();
		}
		return instance;
	}	

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {
		
		StartSignalEvent e = (StartSignalEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#StartSignalEvent");
		appendOryxField(s,"eventtype","Start");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"trigger","Signal");
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
