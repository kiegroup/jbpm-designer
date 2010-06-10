package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class SubProcessTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new SubProcessTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext context) {
		
		SubProcess sp = (SubProcess) diagramObject;
		
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(sp));
		
		appendOryxField(s,"type",STENCIL_URI + "#Subprocess");
		appendNonConnectorStandardFields(sp,s,context);
		appendOryxField(s,"activitytype","Sub-Process");
		appendOryxField(s,"looptype",sp.getLoopType().toString());
		appendResourceEndPattern(s, diagramObject, context);
		
		return s;
	}

}
