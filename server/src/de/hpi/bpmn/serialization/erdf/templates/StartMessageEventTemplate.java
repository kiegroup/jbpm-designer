package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.StartMessageEvent;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class StartMessageEventTemplate extends NonConnectorTemplate implements
		BPMN2ERDFTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new StartMessageEventTemplate();
		}
		return instance;
	}	


	@Override
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject, 
			ERDFSerializationContext context) {
		
		StartMessageEvent e = (StartMessageEvent) diagramObject;
		
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#StartMessageEvent");
		appendOryxField(s,"eventtype","Start");
		appendStandardFields(s);
		appendOryxField(s,"trigger","Message");
		appendResourceEndPattern(s, e, context);
		
		return s;
	}


}
