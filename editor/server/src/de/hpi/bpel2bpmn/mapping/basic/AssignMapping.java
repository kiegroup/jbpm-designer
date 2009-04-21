package de.hpi.bpel2bpmn.mapping.basic;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.mapping.MappingContext;
import de.hpi.bpmn.Task;

public class AssignMapping extends BasicActivityMapping {
	
	static private AssignMapping instance = null;
	
	static public AssignMapping getInstance() {
		if(null == instance) {
			instance = new AssignMapping();
		}
		return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {
		Task task = mappingContext.getFactory().createTask();
		task.setLabel("Assign activity");
		setConnectionPointsWithControlLinks(node, task, task, null, mappingContext);
		mappingContext.addMappingElementToSet(node,task);
		
//		String annotationText = "Actually a BPEL 'assign' activity maps to the 'assignment' attribute" +
//				" of a BPMN element. In order to preserve the point in time the data mediation is performed" +
//				" we introduce a separate task representing the 'assign' activity.";
//		createAnnotationAndAssociation(annotationText,task,mappingContext);
	}
}
