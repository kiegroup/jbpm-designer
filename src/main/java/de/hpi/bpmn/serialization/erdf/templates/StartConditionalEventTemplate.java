package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.StartConditionalEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class StartConditionalEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new StartConditionalEventTemplate();
		}
		return instance;
	}	

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {
		
		StartConditionalEvent e = (StartConditionalEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#StartConditionalEvent");
		appendOryxField(s,"eventtype","Start");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"trigger","Conditional");
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
