package de.hpi.bpel2bpmn.mapping;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.Pool;

public class ProcessMapping extends ElementMappingImpl {

	private static ElementMapping instance = null;
	
	public static ElementMapping getInstance() {
      if(null == instance) {
          instance = new ProcessMapping();
       }
       return instance;
	}
	
	public void mapElement(Node node, MappingContext mappingContext) {

		String name = BPEL2BPMNMappingUtil.getRealNameOfNode(node);

		if (name.equals("") || name == null)
			name = "Imported BPEL Proces";
		
		mappingContext.getDiagram().setTitle(name);
		
//		Pool pool = mappingContext.getFactory().createPool();
//		pool.setParent(mappingContext.getDiagram());
		// TODO: do we need to set the resource id?
		//pool.setResourceId();
		
//		Lane lane = mappingContext.getFactory().createLane();
//		lane.setParent(mappingContext.getDiagram());
		// TODO: do we need to set the resource id?
		//lane.setResourceId();

		
	}
}
