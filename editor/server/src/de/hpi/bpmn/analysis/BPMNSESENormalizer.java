package de.hpi.bpmn.analysis;

import java.util.Vector;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.sese.ANDGatewayJoin;
import de.hpi.bpmn.sese.ANDGatewaySplit;

/**
 * The single entry single exit normalizer ensures that each gateway is either of
 * split or join nature. Also activities should only have one incoming and one 
 * outgoing sequence flow.
 * 
 * @author Sven Wagner-Boysen
 */
public class BPMNSESENormalizer extends BPMNNormalizer{
	
	private BPMNFactory factory = new BPMNFactory();
	
	public BPMNSESENormalizer(BPMNDiagram diagram) {
		super(diagram);
	}
	
	/**
	 * Calls normalize method of the {@link BPMNNormalizer} and than starts to
	 * identify split an join gateways.
	 */
	public void normalize() {
		super.normalize();
		for(Container process : diagram.getProcesses()) {
			this.normalizeGateways(process);
			this.normalizePoolsAndLanes(process);
		}
	}
	
	/**
	 * Checks if a given process flow is surrounded by a pool. If not, it creates
	 * a pool with one containing lane inside.
	 * 
	 * @param process
	 * 		The process to normalize concerning pools
	 */
	private void normalizePoolsAndLanes(Container process) {
		if (!(process.getChildNodes().get(0) instanceof Pool)) {
			
			/* Create a new pool around and append the process */
			Pool newPool = factory.createPool();			
			Lane lane = factory.createLane();
			lane.getChildNodes().addAll(process.getChildNodes());
			newPool.getChildNodes().add(lane);
			
			addNode(newPool, process);
			addNode(lane, process);
		}
		
	}

	/**
	 * Identifies split respectively join gateways and creates the corresponding
	 * {@link Gateway} type.
	 * 
	 * @param process
	 * 		The process to be normalized by single entry single exit
	 */
	private void normalizeGateways(Container process){
		for(Gateway gateway : getGatewaysOfProcess(process)) {
			if(gateway.getIncomingSequenceFlows().size() == 1 && 
					gateway.getOutgoingSequenceFlows().size() > 1) {
				
				/* Gateway is a split gateway */
				convertToSplitGateway(gateway, process);
				
			} else if (gateway.getOutgoingSequenceFlows().size() == 1 &&
					gateway.getIncomingSequenceFlows().size() > 1) {
				
				/* Gateway is a join gateway */
				convertToJoinGateway(gateway, process);
				
			} else {
				normalizeMultipleFlowsForGateway(gateway, process);
			}
		}
	}
	
	/**
	 * Extract all gateways of a process
	 * 
	 * @param process
	 * 		The parent process
	 * @return
	 * 		The vector of {@link Gateway}
	 */
	private Vector<Gateway> getGatewaysOfProcess(Container process) {
		Vector<Gateway> gateways = new Vector<Gateway>();
		
		/* Search for gateways inside the process */
		for(Node node : process.getChildNodes()) {
			if(node instanceof Gateway) {
				gateways.add((Gateway) node);
			}
		}
		
		return gateways;
	}
	
	/**
	 * Creates a join and a split gateway of the corresponding type for the 
	 * passed gateway.
	 * 
	 * @param gateway
	 * 		The passed gateway.
	 */
	private void normalizeMultipleFlowsForGateway(Gateway gateway, Container process) {
		if (gateway instanceof ANDGateway) {
			/* Create the new join and split AndGateways */
			ANDGatewayJoin joinAndGateway = new ANDGatewayJoin();
			addNode(joinAndGateway, process);
			ANDGatewaySplit splitAndGateway = new ANDGatewaySplit();
			addNode(splitAndGateway, process);
			
			/* Adapt sequence flow to new gateways */
			for(SequenceFlow seqFlow : gateway.getIncomingSequenceFlows()) {
				seqFlow.setTarget(joinAndGateway);
			}
			for(SequenceFlow seqFlow : gateway.getOutgoingSequenceFlows()) {
				seqFlow.setSource(splitAndGateway);
			}
			
			connectNodes(joinAndGateway, splitAndGateway);
		} else if (false) {
			
		}
		
		
		removeNode(gateway);
		// TODO Also handle xor data and event based gateways
		
	}

	/**
	 * Converts the passed gateway to a join gateway of the corresponding type.
	 * 
	 * @param gateway
	 * 		The gateway to convert
	 * @return
	 * 		The converted join gateway
	 */
	private void convertToJoinGateway(Gateway gateway, Container process) {
		if(gateway instanceof ANDGateway) {
			ANDGatewayJoin agj = new ANDGatewayJoin();
			addNode(agj, process);
			
			/* Adapt sequence flow to new gateways */
			for(SequenceFlow seqFlow : gateway.getIncomingSequenceFlows()) {
				seqFlow.setTarget(agj);
			}
			for(SequenceFlow seqFlow : gateway.getOutgoingSequenceFlows()) {
				seqFlow.setSource(agj);
			}
		}
		removeNode(gateway);
	}
	
	/**
	 * Converts the passed gateway to a split gateway of the corresponding type.
	 * 
	 * @param gateway
	 * 		The gateway to convert
	 * @return
	 * 		The converted split gateway
	 */
	private void convertToSplitGateway(Gateway gateway, Container process) {
		if(gateway instanceof ANDGateway) {
			ANDGatewaySplit ags = new ANDGatewaySplit();
			addNode(ags, process);
			
			/* Adapt sequence flow to new gateways */
			for(SequenceFlow seqFlow : gateway.getIncomingSequenceFlows()) {
				seqFlow.setTarget(ags);
			}
			for(SequenceFlow seqFlow : gateway.getOutgoingSequenceFlows()) {
				seqFlow.setSource(ags);
			}
		}
		removeNode(gateway);
	}
}
