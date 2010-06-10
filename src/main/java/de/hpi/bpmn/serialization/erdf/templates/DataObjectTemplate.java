package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DataObject;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class DataObjectTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new DataObjectTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		DataObject d = (DataObject) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(d));
		
		appendOryxField(s,"type",STENCIL_URI + "#DataObject");
		appendNonConnectorStandardFields(d,s,transformationContext);
		
		appendResourceEndPattern(s, diagramObject, transformationContext);
		return s;
	}

}
