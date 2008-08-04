package de.hpi.bpmn2pn.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.Activity;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.Event;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.XORDataBasedGateway;

/**
 * Copyright (c) 2008 Gero Decker
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
		Map<Gateway,Node> addedGateways = new HashMap<Gateway,Node>();
		for (Node node: process.getChildNodes()) {
			if (node instanceof Activity || node instanceof Event) {
				if (countSequenceFlows(node.getIncomingEdges()) > 1) {
					// create new xor-gateway
					XORDataBasedGateway g = factory.createXORDataBasedGateway();
//					g.setParent(node.getParent()); // attention: concurrent update!
//					g.setProcess(node.getProcess()); // attention: concurrent update!
					addedGateways.put(g, node);
	
					// reroute incoming branches
					for (Edge edge: new ArrayList<Edge>(node.getIncomingEdges()))
						if (edge instanceof SequenceFlow)
							edge.setTarget(g);
					
					// add new sequence flow
					SequenceFlow flow = factory.createSequenceFlow();
					diagram.getEdges().add(flow);
					flow.setSource(g);
					flow.setTarget(node);
				}
				if (countSequenceFlows(node.getOutgoingEdges()) > 1) {
					// create new xor-gateway
					ANDGateway g = factory.createANDGateway();
//					g.setParent(node.getParent()); // attention: concurrent update!
//					g.setProcess(node.getProcess()); // attention: concurrent update!
					addedGateways.put(g, node);
	
					// reroute outgoing branches
					for (Edge edge: new ArrayList<Edge>(node.getOutgoingEdges()))
						if (edge instanceof SequenceFlow)
							edge.setSource(g);
					
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
		for (Entry<Gateway,Node> entry: addedGateways.entrySet()) {
			Gateway g = entry.getKey();
			Node node = entry.getValue();
			g.setParent(node.getParent()); 
			g.setProcess(node.getProcess()); 
		}
	}

	protected int countSequenceFlows(List<Edge> edges) {
		int count = 0;
		for (Edge edge: edges)
			if (edge instanceof SequenceFlow)
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
