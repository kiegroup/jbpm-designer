package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Task;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;


public class TaskTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new TaskTemplate();
		}
		return instance;
	}

	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject, ERDFSerializationContext context) {
		
		Task t = (Task) diagramObject;
		
		StringBuilder s = getResourceStartPattern(context.getResourceIDForDiagramObject(t));
		
		appendOryxField(s,"type",STENCIL_URI + "#Task");
		appendNonConnectorStandardFields(t,s);
		appendOryxField(s,"activitytype","Task");
		appendOryxField(s,"startquantity","1");
		appendOryxField(s,"looptype",t.getLoopType().toString());
		
		appendResourceEndPattern(s, diagramObject, context);
		return s;
	}

}
