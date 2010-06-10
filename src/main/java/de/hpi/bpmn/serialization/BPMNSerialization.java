package de.hpi.bpmn.serialization;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.Association;
import de.hpi.bpmn.CollapsedSubprocess;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.DataObject;
import de.hpi.bpmn.EndCancelEvent;
import de.hpi.bpmn.EndCompensationEvent;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.EndLinkEvent;
import de.hpi.bpmn.EndMessageEvent;
import de.hpi.bpmn.EndMultipleEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.EndSignalEvent;
import de.hpi.bpmn.EndTerminateEvent;
import de.hpi.bpmn.IntermediateCancelEvent;
import de.hpi.bpmn.IntermediateCompensationEvent;
import de.hpi.bpmn.IntermediateConditionalEvent;
import de.hpi.bpmn.IntermediateErrorEvent;
import de.hpi.bpmn.IntermediateLinkEvent;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.IntermediateMultipleEvent;
import de.hpi.bpmn.IntermediatePlainEvent;
import de.hpi.bpmn.IntermediateSignalEvent;
import de.hpi.bpmn.IntermediateTimerEvent;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.MessageFlow;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartConditionalEvent;
import de.hpi.bpmn.StartLinkEvent;
import de.hpi.bpmn.StartMessageEvent;
import de.hpi.bpmn.StartMultipleEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.StartSignalEvent;
import de.hpi.bpmn.StartTimerEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Task;
import de.hpi.bpmn.TextAnnotation;
import de.hpi.bpmn.UndirectedAssociation;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.XOREventBasedGateway;

/**
 * Every target language for serializations of a BPMN model has to implement
 * this interface. An implementation of this interface is passed to all 
 * BPMN elements that are serialized. They do nothing more than calling the 
 * method getSerializationForDiagramObject(). The actual method is then selected
 * based on the type information.
 * 
 * Please see the package de.hpi.bpmn.serialization.erdf for an exemplary 
 * implementation.
 * 
 * @author matthias.weidlich
 *
 */
public interface BPMNSerialization {
	
	public StringBuilder getSerializationHeader();
	
	public StringBuilder getSerializationFooter();

	/*
	 * Activities, Pools, Lanes, Data Objects, Flows, etc.
	 */
	public StringBuilder getSerializationForDiagramObject(Task task);
	public StringBuilder getSerializationForDiagramObject(SubProcess subProcess);
	public StringBuilder getSerializationForDiagramObject(CollapsedSubprocess collabsedSubprocess);
	public StringBuilder getSerializationForDiagramObject(Lane lane);
	public StringBuilder getSerializationForDiagramObject(Pool pool);
	public StringBuilder getSerializationForDiagramObject(MessageFlow messageFlow);
	public StringBuilder getSerializationForDiagramObject(SequenceFlow sequenceFlow);
	public StringBuilder getSerializationForDiagramObject(TextAnnotation textAnnotation);
	public StringBuilder getSerializationForDiagramObject(Association association);
	public StringBuilder getSerializationForDiagramObject(DataObject dataObject);
	public StringBuilder getSerializationForDiagramObject(UndirectedAssociation undirectedAssociation);

	/*
	 * Gateways
	 */
	public StringBuilder getSerializationForDiagramObject(ANDGateway andGateway);
	public StringBuilder getSerializationForDiagramObject(ComplexGateway complexGateway);
	public StringBuilder getSerializationForDiagramObject(ORGateway orGateway);
	public StringBuilder getSerializationForDiagramObject(XORDataBasedGateway xorDataBasedGateway);
	public StringBuilder getSerializationForDiagramObject(XOREventBasedGateway xorEventBasedGateway);
	
	/*
	 * Events
	 */
	public StringBuilder getSerializationForDiagramObject(StartPlainEvent startPlainEvent);
	public StringBuilder getSerializationForDiagramObject(StartMessageEvent startMessageEvent);
	public StringBuilder getSerializationForDiagramObject(StartMultipleEvent startMultipleEvent);
	public StringBuilder getSerializationForDiagramObject(StartSignalEvent startSignalEvent);
	public StringBuilder getSerializationForDiagramObject(StartTimerEvent startTimerEvent);
	public StringBuilder getSerializationForDiagramObject(StartLinkEvent startLinkEvent);
	public StringBuilder getSerializationForDiagramObject(StartConditionalEvent startConditionalEvent);

	public StringBuilder getSerializationForDiagramObject(IntermediatePlainEvent intermediatePlainEvent);
	public StringBuilder getSerializationForDiagramObject(IntermediateMessageEvent intermediateMessageEvent);
	public StringBuilder getSerializationForDiagramObject(IntermediateMultipleEvent intermediateMultipleEvent);
	public StringBuilder getSerializationForDiagramObject(IntermediateSignalEvent intermediateSignalEvent);
	public StringBuilder getSerializationForDiagramObject(IntermediateTimerEvent intermediateTimerEvent);
	public StringBuilder getSerializationForDiagramObject(IntermediateLinkEvent intermediateLinkEvent);
	public StringBuilder getSerializationForDiagramObject(IntermediateCancelEvent intermediateCancelEvent);
	public StringBuilder getSerializationForDiagramObject(IntermediateCompensationEvent intermediateCompensationEvent);
	public StringBuilder getSerializationForDiagramObject(IntermediateConditionalEvent intermediateConditionalEvent);
	public StringBuilder getSerializationForDiagramObject(IntermediateErrorEvent intermediateErrorEvent);
	
	public StringBuilder getSerializationForDiagramObject(EndPlainEvent endPlainEvent);
	public StringBuilder getSerializationForDiagramObject(EndMessageEvent endMessageEvent);
	public StringBuilder getSerializationForDiagramObject(EndMultipleEvent endMultipleEvent);
	public StringBuilder getSerializationForDiagramObject(EndSignalEvent endSignalEvent);
	public StringBuilder getSerializationForDiagramObject(EndTerminateEvent endTerminateEvent);
	public StringBuilder getSerializationForDiagramObject(EndCancelEvent endCancelEvent);
	public StringBuilder getSerializationForDiagramObject(EndCompensationEvent endCompensationEvent);
	public StringBuilder getSerializationForDiagramObject(EndErrorEvent endErrorEvent);
	public StringBuilder getSerializationForDiagramObject(EndLinkEvent endLinkEvent);


}
