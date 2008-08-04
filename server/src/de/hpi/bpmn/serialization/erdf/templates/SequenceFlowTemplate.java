package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class SequenceFlowTemplate extends ConnectorTemplate implements
		BPMN2ERDFTemplate {
	
	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new SequenceFlowTemplate();
		}
		return instance;
	}
	
	@Override
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext context) {
		
		SequenceFlow e = (SequenceFlow) diagramObject;
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#SequenceFlow");
		appendStandardFields(s);
		appendResourceEndPattern(s, e, context);
		
		return s;
	}

}
