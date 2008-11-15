package de.hpi.ibpmn;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.EndMessageEvent;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.IntermediateSignalEvent;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.MessageFlow;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.StartMessageEvent;
import de.hpi.bpmn.StartSignalEvent;
import de.hpi.bpmn.Task;
import de.hpi.bpmn.XORDataBasedGateway;

/**
 * @author Gero.Decker
 */
public class IBPMNFactory extends BPMNFactory {

	@Override
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

	@Override
	public ComplexGateway createComplexGateway() {
		return new OwnedComplexGateway();
	}

	@Override
	public EndMessageEvent createEndMessageEvent() {
		return null;
	}

	@Override
	public IntermediateMessageEvent createIntermediateMessageEvent() {
		return null;
	}

	@Override
	public IntermediateSignalEvent createIntermediateSignalEvent() {
		return null;
	}

	@Override
	public Lane createLane() {
		return null;
	}

	@Override
	public MessageFlow createMessageFlow() {
		return null;
	}

	@Override
	public ORGateway createORGateway() {
		return new OwnedORGateway();
	}

	@Override
	public StartMessageEvent createStartMessageEvent() {
		return null;
	}

	@Override
	public StartSignalEvent createStartSignalEvent() {
		return null;
	}

	@Override
	public Task createTask() {
		return null;
	}

	@Override
	public XORDataBasedGateway createXORDataBasedGateway() {
		return new OwnedXORDataBasedGateway();
	}
	
}


