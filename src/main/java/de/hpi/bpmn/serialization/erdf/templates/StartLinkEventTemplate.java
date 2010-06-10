package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.StartLinkEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class StartLinkEventTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new StartLinkEventTemplate();
		}
		return instance;
	}	

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {
		
		StartLinkEvent e = (StartLinkEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#StartLinkEvent");
		appendOryxField(s,"eventtype","Start");
		appendNonConnectorStandardFields(e,s,transformationContext);
		appendOryxField(s,"trigger","Link");
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
