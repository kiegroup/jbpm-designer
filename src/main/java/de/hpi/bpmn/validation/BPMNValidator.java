package de.hpi.bpmn.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.verification.MaxStatesExceededException;
import de.hpi.PTnet.verification.WeakTerminationChecker;
import de.hpi.PTnet.verification.WeakTerminationChecker.UnboundedNetException;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.EndTerminateEvent;
import de.hpi.bpmn2pn.converter.HighConverter;
import de.hpi.highpetrinet.HighLabeledTransition;
import de.hpi.highpetrinet.HighPetriNet;
import de.hpi.highpetrinet.HighSilentTransition;
import de.hpi.highpetrinet.HighTransition;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

/**
 * Copyright (c) 2008 Kai Schlichting, Gero Decker
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
public class BPMNValidator {
	protected BPMNDiagram diagram;
	protected Map<String,String> errors;
	public boolean leadsToEnd;
	public List<DiagramObject> deadlockBPMNNodes;
	DiagramObject unsafeBPMNNode;
	
	public BPMNValidator(BPMNDiagram diagram) {
		this.diagram = diagram;
		this.errors = new HashMap<String,String>();
		deadlockBPMNNodes = new ArrayList<DiagramObject>();
		unsafeBPMNNode = null;
	}
	
	//Use leadsToGoodMarking and bpmnNodes to see Validation results
	public void validate() {
		HighPetriNet net = new HighConverter(this.diagram).convert();
		WeakTerminationChecker checker = new WeakTerminationChecker(net, getFinalMarking(net));
		List<Transition> deadlockTransitions = new ArrayList<Transition>();
		
		try {
			leadsToEnd = checker.check();
			deadlockTransitions.addAll(checker.getDeadlockingTransitions());
		} catch (MaxStatesExceededException e) {
			leadsToEnd = false;
		} catch (UnboundedNetException e) {
			leadsToEnd = false;
		}
		
		//add bpmn objects belonging to a conflicting transition to conflictingBPMNNodes
		deadlockBPMNNodes.clear();
		for(Transition t : deadlockTransitions){
			DiagramObject obj = null;
			if (t instanceof HighSilentTransition){
				obj = ((HighSilentTransition)t).getBPMNObj();
			} else {
				obj = ((HighLabeledTransition)t).getBPMNObj();
			}
			if(!deadlockBPMNNodes.contains(obj))
				deadlockBPMNNodes.add(obj);
		}
		
		//if the net is unsafe (lack of synchronisation), there is exactly one unsafe node
		if(checker.getUnsafeTransition() != null) {
			if (checker.getUnsafeTransition() instanceof HighSilentTransition){
				unsafeBPMNNode = ((HighSilentTransition)checker.getUnsafeTransition()).getBPMNObj();
			} else {
				unsafeBPMNNode = ((HighLabeledTransition)checker.getUnsafeTransition()).getBPMNObj();
			}
		}
	}
	
	/* Process must have been normalized before, so that each process each extacly one
	 * end event
	 */
	public List<Marking> getFinalMarking(HighPetriNet net){
		//find end events in each process on highest level
		//List<EndEvent> endEvents = new ArrayList<EndEvent>();
		Marking m = new Marking(net);
		List<Marking> markings = new ArrayList<Marking>();
		markings.add(m);
		
		for(Transition transition : net.getTransitions()){
			DiagramObject bpmnObject = ((HighTransition)transition).getBPMNObj();
			if(		//it's an end event ...
					bpmnObject instanceof EndEvent && 
					// ..., not a terminate end event, ...
					!(bpmnObject instanceof EndTerminateEvent) && 
					// and part of the top-level process
					diagram.getProcesses().contains(((EndEvent)bpmnObject).getProcess()) 
			){
				m.addToken((Place)transition.getOutgoingFlowRelationships().get(0).getTarget());
			}
		}
		
		return markings;
	}

	public List<DiagramObject> getDeadlockBPMNNodes() {
		return deadlockBPMNNodes;
	}
	
	public DiagramObject getUnsafeBPMNNode() {
		return unsafeBPMNNode;
	}
}
