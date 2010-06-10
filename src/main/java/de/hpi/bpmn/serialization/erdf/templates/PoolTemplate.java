package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class PoolTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new PoolTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {


		Pool p = (Pool) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(p));
		
		appendOryxField(s,"type",STENCIL_URI + "#Pool");
		appendNonConnectorStandardFields(p,s,transformationContext);
		appendResourceEndPattern(s, diagramObject, transformationContext);
		return s;
	}

}
