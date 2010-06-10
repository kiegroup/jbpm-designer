package de.hpi.bpmn2yawl;

/**
 * Copyright (c) 2010, Armin Zamani
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
 * s
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndErrorEvent;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.ORGateway;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.analysis.BPMNNormalizer;

import java.util.Vector;

public class BPMN2YAWLNormalizer extends BPMNNormalizer{
	/**
	 * BPMN diagram that is normalized
	 */
	BPMNDiagram diagram;
	
	/**
	 * constructor of class 
	 */
	public BPMN2YAWLNormalizer(BPMNDiagram diagram){
		super(diagram);
		this.diagram = diagram;
	}
	
	/**
	 * normalizes the BPMN diagram in a way that conforms to YAWL mapping rules
	 */
	public void normalizeForYAWL(){
		normalize();
	}
	
	/**
	 * @see de.hpi.bpmn.analysis.BPMNNormalizer#normalizeMultipleStartEvents(de.hpi.bpmn.Container, java.util.Vector)
	 * merges all start events to one start event
	 */
	@Override
	protected void normalizeMultipleStartEvents(Container process,
			Vector<StartEvent> startEvents){
		if (startEvents.size() < 2)
			//nothing has to be normalized
			return;
		
		int counter = 0;
		Vector<String> nodeLabels = new Vector<String>();
		StartPlainEvent start = new StartPlainEvent();
		addNode(start, process);
		
		for (StartEvent s : startEvents){
			for (Edge e : s.getOutgoingEdges()){
				Node node = (Node)e.getTarget();
				connectNodes(start, node);
				
				checkNodeLabel(nodeLabels, node, counter);
				counter++;
			}	
			removeNode(s);
		}
	}

	/**
	 * checks if the node label is unique or adds the node label to the list of node labels
	 * @param nodeLabels list of node labels
	 * @param node BPMN Node with label
	 * @param counter node counter
	 */
	private void checkNodeLabel(Vector<String> nodeLabels, Node node,
			int counter) {
		if(nodeLabels.contains(node.getLabel()))
			node.setLabel(node.getLabel() + counter);
		else
			nodeLabels.add(node.getLabel());
	}
	
	/**
	 * @see de.hpi.bpmn.analysis.BPMNNormalizer#normalizeMultipleEndEvents(de.hpi.bpmn.Container, java.util.Vector)
	 * skips the normalization for end error events
	 */
	@Override
	protected void normalizeMultipleEndEvents(Container process, Vector<EndEvent> endEvents) {
		
		if (checkIfEndErrorEventsExist(endEvents))
			return;
		
		EndPlainEvent end = addEndPlainEvent(process);

		ORGateway gateway = addOrGateway(process);

		connectNodes(gateway, end);

		convertEndEventsToIntermediateEvents(process, endEvents, gateway);
	}

	/**
	 * converts End Events to Intermediate Events
	 * @param process process context
	 * @param endEvents list of end events
	 * @param gateway OR Gateway to which the events are connected
	 */
	private void convertEndEventsToIntermediateEvents(Container process,
			Vector<EndEvent> endEvents, ORGateway gateway) {
		int index = 0;
		for (EndEvent e : endEvents) {
			removeNode(e);

			IntermediateEvent iEvent = convertToIntermediateEvent(e);
			addNode(iEvent, process);

			e.getIncomingEdges().get(0).setTarget(iEvent);

			// Id is needed because incoming edges of or-join needs ids to find
			// all combinations
			connectNodes(iEvent, gateway).setId("seq" + index + e.getId());
			index++;
		}
	}

	/**
	 * creates an OR Gateway and adds it to the process
	 * @param process Process context
	 * @return OR Gateway
	 */
	private ORGateway addOrGateway(Container process) {
		ORGateway gateway = new ORGateway();
		addNode(gateway, process);
		return gateway;
	}

	/**
	 * creates an End Plain Event and adds it to the process
	 * @param process Process context
	 * @return End Plain event
	 */
	private EndPlainEvent addEndPlainEvent(Container process) {
		EndPlainEvent end = new EndPlainEvent();
		addNode(end, process);
		return end;
	}

	/**
	 * checks if an end error event exists in the list of end events
	 * @param endEvents list of end events
	 * @return result of check
	 */
	private boolean checkIfEndErrorEventsExist(Vector<EndEvent> endEvents) {
		for(EndEvent event : endEvents){
			if(event instanceof EndErrorEvent)
				return true;
		}
		return false;
	}
	
	/**
	 * adds the given node to the diagram and process
	 * @param node BPMN node
	 * @param process process context
	 */
	protected void addNode(Node node, Container process) {
		diagram.getChildNodes().add(node);
		node.setParent(process);
		node.setProcess(process);
	}
}
