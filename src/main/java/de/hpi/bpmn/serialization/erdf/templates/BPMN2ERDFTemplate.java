package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public interface BPMN2ERDFTemplate {
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject, ERDFSerializationContext transformationContext);

}
