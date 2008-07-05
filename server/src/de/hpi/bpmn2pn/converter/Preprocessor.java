package de.hpi.bpmn2pn.converter;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.Activity;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.ControlFlow;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.Event;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.XORDataBasedGateway;

public class Preprocessor {
	
	protected BPMNDiagram diagram;
	protected BPMNFactory factory;

	public Preprocessor(BPMNDiagram diagram, BPMNFactory factory) {
		this.diagram = diagram;
		this.factory = factory;
	}
	
	public void process() {
		for (Container proc: diagram.getProcesses()) {
			
			// handle several incoming/outgoing sequence flow edges
			handleSequenceFlow(proc);
	
			// handle missing start / event events
			introduceStartAndEndEvents(proc);
			
			// replace loop activities
			expandLoopActivities(proc);
		}
	}

	protected void handleSequenceFlow(Container process) {
		for (Node node: process.getChildNodes()) {
			if (node instanceof Activity || node instanceof Event) {
				if (countSequenceFlows(node.getIncomingEdges()) > 1) {
					// create new xor-gateway
					XORDataBasedGateway g = factory.createXORDataBasedGateway();
					g.setParent(node.getParent());
					g.setProcess(node.getProcess());
	
					// reroute incoming branches
					for (Edge edge: node.getIncomingEdges())
						if (edge instanceof ControlFlow)
							edge.setTarget(node);
					
					// add new sequence flow
					SequenceFlow flow = factory.createSequenceFlow();
					diagram.getEdges().add(flow);
					flow.setSource(g);
					flow.setTarget(node);
				}
				if (countSequenceFlows(node.getOutgoingEdges()) > 1) {
					// create new xor-gateway
					ANDGateway g = factory.createANDGateway();
					g.setParent(node.getParent());
					g.setProcess(node.getProcess());
	
					// reroute outgoing branches
					for (Edge edge: node.getOutgoingEdges())
						if (edge instanceof ControlFlow)
							edge.setSource(node);
					
					// add new sequence flow
					SequenceFlow flow = factory.createSequenceFlow();
					diagram.getEdges().add(flow);
					flow.setSource(node);
					flow.setTarget(g);
				}
			}
			if (node instanceof SubProcess)
				handleSequenceFlow((SubProcess)node);
		}
	}

	protected int countSequenceFlows(List<Edge> edges) {
		int count = 0;
		for (Edge edge: edges)
			if (edge instanceof ControlFlow)
				count++;
		return count;
	}

	protected void introduceStartAndEndEvents(Container process) {
		// TODO do not introduce events for adhoc subprocesses
		List<Node> startNodes = new ArrayList();
		List<Node> endNodes = new ArrayList();
		for (Node node: process.getChildNodes()) {
			if (!(node instanceof StartEvent || (node instanceof IntermediateEvent && ((IntermediateEvent)node).getActivity() != null)) 
					&& countSequenceFlows(node.getIncomingEdges()) == 0)
				startNodes.add(node);
			if (!(node instanceof EndEvent) 
					&& countSequenceFlows(node.getOutgoingEdges()) == 0)
				endNodes.add(node);
			
			if (node instanceof SubProcess)
				introduceStartAndEndEvents((SubProcess)node);
		}
		
		if (startNodes.size() > 0) {
			// introduce start plain event
			StartPlainEvent e = factory.createStartPlainEvent();
			e.setParent(startNodes.get(0).getParent());
			e.setProcess(process);
			
			// add sequence flow
			SequenceFlow flow = factory.createSequenceFlow();
			diagram.getEdges().add(flow);
			flow.setSource(e);
			
			if (startNodes.size() == 1) {
				flow.setTarget(startNodes.get(0));
				
			} else {
				// add AND gateway
				ANDGateway g = factory.createANDGateway();
				g.setParent(startNodes.get(0).getParent());
				g.setProcess(process);
				flow.setTarget(g);
				
				for (Node node: startNodes) {
					// add sequence flow
					SequenceFlow flow2 = factory.createSequenceFlow();
					diagram.getEdges().add(flow2);
					flow2.setSource(g);
					flow2.setTarget(node);
				}
			}
		}
		if (endNodes.size() > 0) {
			for (Node node: endNodes) {
				// introduce end plain event
				EndPlainEvent e = factory.createEndPlainEvent();
				e.setParent(endNodes.get(0).getParent());
				e.setProcess(process);
				
				// add sequence flow
				SequenceFlow flow2 = factory.createSequenceFlow();
				diagram.getEdges().add(flow2);
				flow2.setSource(node);
				flow2.setTarget(e);
			}
		}
	}

	protected void expandLoopActivities(Container process) {
		// TODO Auto-generated method stub
		
	}

}
