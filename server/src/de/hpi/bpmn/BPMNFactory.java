package de.hpi.bpmn;

public class BPMNFactory {
	
	public BPMNDiagram createBPMNDiagram() {
		return new BPMNDiagram();
	}
	
	public Pool createPool() {
		return new Pool();
	}

	public Lane createLane() {
		return new Lane();
	}


	public Task createTask() {
		return new Task();
	}

	public SubProcess createSubProcess() {
		return new SubProcess();
	}


	public StartPlainEvent createStartPlainEvent() {
		return new StartPlainEvent();
	}

	public StartMessageEvent createStartMessageEvent() {
		return new StartMessageEvent();
	}

	public StartTimerEvent createStartTimerEvent() {
		return new StartTimerEvent();
	}

	public StartRuleEvent createStartRuleEvent() {
		return new StartRuleEvent();
	}

	public StartLinkEvent createStartLinkEvent() {
		return new StartLinkEvent();
	}

	public StartMultipleEvent createStartMultipleEvent() {
		return new StartMultipleEvent();
	}

	public IntermediatePlainEvent createIntermediatePlainEvent() {
		return new IntermediatePlainEvent();
	}

	public IntermediateTimerEvent createIntermediateTimerEvent() {
		return new IntermediateTimerEvent();
	}

	public IntermediateCancelEvent createIntermediateCancelEvent() {
		return new IntermediateCancelEvent();
	}

	public IntermediateCompensationEvent createIntermediateCompensationEvent() {
		return new IntermediateCompensationEvent();
	}

	public IntermediateRuleEvent createIntermediateRuleEvent() {
		return new IntermediateRuleEvent();
	}

	public IntermediateLinkEvent createIntermediateLinkEvent() {
		return new IntermediateLinkEvent();
	}

	public IntermediateMultipleEvent createIntermediateMultipleEvent() {
		return new IntermediateMultipleEvent();
	}


	public IntermediateErrorEvent createIntermediateErrorEvent() {
		return new IntermediateErrorEvent();
	}

	public IntermediateMessageEvent createIntermediateMessageEvent() {
		return new IntermediateMessageEvent();
	}


	public EndPlainEvent createEndPlainEvent() {
		return new EndPlainEvent();
	}

	public EndCancelEvent createEndCancelEvent() {
		return new EndCancelEvent();
	}

	public EndCompensationEvent createEndCompensationEvent() {
		return new EndCompensationEvent();
	}

	public EndMessageEvent createEndMessageEvent() {
		return new EndMessageEvent();
	}

	public EndErrorEvent createEndErrorEvent() {
		return new EndErrorEvent();
	}

	public EndTerminateEvent createEndTerminateEvent() {
		return new EndTerminateEvent();
	}

	public EndLinkEvent createEndLinkEvent() {
		return new EndLinkEvent();
	}

	public EndMultipleEvent createEndMultipleEvent() {
		return new EndMultipleEvent();
	}


	public XORDataBasedGateway createXORDataBasedGateway() {
		return new XORDataBasedGateway();
	}

	public XOREventBasedGateway createXOREventBasedGateway() {
		return new XOREventBasedGateway();
	}

	public ANDGateway createANDGateway() {
		return new ANDGateway();
	}

	public ComplexGateway createComplexGateway() {
		return new ComplexGateway();
	}

	public ORGateway createORGateway() {
		return new ORGateway();
	}


	public DataObject createDataObject() {
		return new DataObject();
	}
	
	public ExecDataObject createExecDataObject() {
		return new ExecDataObject();
	}

	public TextAnnotation createTextAnnotation() {
		return new TextAnnotation();
	}

	public SequenceFlow createSequenceFlow() {
		return new SequenceFlow();
	}

	public MessageFlow createMessageFlow() {
		return new MessageFlow();
	}

	public DefaultFlow createDefaultFlow() {
		return new DefaultFlow();
	}

	public ConditionalFlow createConditionalFlow() {
		return new ConditionalFlow();
	}

	public Association createAssociation() {
		return new Association();
	}

	public UndirectedAssociation createUndirectedAssociation() {
		return new UndirectedAssociation();
	}

}
