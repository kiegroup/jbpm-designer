package de.hpi.bpel2bpmn.mapping;

import org.w3c.dom.Node;

import de.hpi.bpel2bpmn.util.BPEL2BPMNMappingUtil;

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
	}
}
