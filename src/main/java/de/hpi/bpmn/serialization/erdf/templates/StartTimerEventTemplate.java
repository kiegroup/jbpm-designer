package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.StartTimerEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class StartTimerEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new StartTimerEventTemplate();
		}
		return instance;
	}	
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		StartTimerEvent e = (StartTimerEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#StartTimerEvent");
		appendOryxField(s,"eventtype","Start");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"trigger","Timer");
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
