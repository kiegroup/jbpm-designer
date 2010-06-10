package de.hpi.bpmn.serialization.erdf.templates;

import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.UndirectedAssociation;
import de.hpi.bpmn.serialization.erdf.ERDFSerializationContext;

public class UndirectedAssociationTemplate extends ConnectorTemplate {
	
	private static BPMN2ERDFTemplate instance;

	public static BPMN2ERDFTemplate getInstance() {
		if (instance == null) {
			instance = new UndirectedAssociationTemplate();
		}
		return instance;
	}

	public StringBuilder getCompletedTemplate(DiagramObject diagramObject,
			ERDFSerializationContext transformationContext) {

		UndirectedAssociation e = (UndirectedAssociation) diagramObject;
		StringBuilder s = getResourceStartPattern(transformationContext.getResourceIDForDiagramObject(e));
		appendOryxField(s,"type",STENCIL_URI + "#Association_Undirected");
		appendStandardFields(s);
		appendDockerInformation(s, e);
		appendBounds(s, e);
		appendResourceEndPattern(s, e, transformationContext);
		
		return s;
	}

}
