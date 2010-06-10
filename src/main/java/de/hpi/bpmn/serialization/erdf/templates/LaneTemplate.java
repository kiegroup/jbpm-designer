package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class LaneTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new LaneTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		Lane l = (Lane) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(l));
		
		appendOryxField(s,"type",STENCIL_URI + "#Lane");
		appendNonConnectorStandardFields(l,s,transformationContext);
		appendResourceEndPattern(s, diagramObject, transformationContext);
		return s;
	}

}
