package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.TextAnnotation;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class TextAnnotationTemplate extends NonConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new TextAnnotationTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		TextAnnotation t = (TextAnnotation) diagramObject;
		
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(t));
		
		appendOryxField(s,"type",STENCIL_URI + "#TextAnnotation");
		appendOryxField(s,"text",t.getText());
		appendStandardFields(s);
		appendResourceEndPattern(s, diagramObject, transformationContext);
		
		return s;
	}

}
