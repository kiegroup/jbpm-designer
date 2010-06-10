package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.CollapsedSubprocess;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class CollapsedSubprocessTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new CollapsedSubprocessTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject, ERDFSerializationContext context) {
		
		CollapsedSubprocess t = (CollapsedSubprocess) diagramObject;
		
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(t));
		
		appendOryxField(s,"type",STENCIL_URI + "#CollapsedSubprocess");
		appendNonConnectorStandardFields(t,s,context);
		appendOryxField(s,"activitytype","Sub-Process");
		String color = t.getColor();
		if (color != null && color.length() > 0){
			appendOryxField(s,"bgcolor",color);
		}
		String subprocessRef = t.getSubprocessRef();
		if (subprocessRef != null && subprocessRef.length() > 0){
			appendOryxField(s, "entry", subprocessRef);
		}
		
		appendResourceEndPattern(s, diagramObject, context);
		return s;
	}

}
