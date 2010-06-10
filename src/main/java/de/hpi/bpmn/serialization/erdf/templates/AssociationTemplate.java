package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.Association;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class AssociationTemplate extends ConnectorTemplate {

	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new AssociationTemplate();
		}
		return instance;
	}
	
	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		Association e = (Association) diagramObject;
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#Association");
		appendStandardFields(s);
		appendDockerInformation(s, e);
		appendBounds(s, e);
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
