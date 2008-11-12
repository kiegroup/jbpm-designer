package de.hpi.ibpmn;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;

/**
 * @author Gero.Decker
 */
public class IBPMNFactory extends BPMNFactory {

	public BPMNDiagram createBPMNDiagram() {
		return new IBPMNDiagram();
	}
	
	public IBPMNDiagram createIBPMNDiagram() {
		return new IBPMNDiagram();
	}
	
	public StartInteraction createStartInteraction() {
		return new StartInteraction();
	}
	
	public IntermediateInteraction createIntermediateInteraction() {
		return new IntermediateInteraction();
	}
	
	public EndInteraction createEndInteraction() {
		return new EndInteraction();
	}
	
}


