package de.hpi.bpmn.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.verification.WeakTerminationChecker;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn2pn.converter.HighConverter;
import de.hpi.highpetrinet.HighLabeledTransition;
import de.hpi.highpetrinet.HighPetriNet;
import de.hpi.highpetrinet.HighSilentTransition;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

/**
 * Copyright (c) 2008 Kai Schlichting
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
	public List<DiagramObject> conflictingBPMNNodes;
	
	public BPMNValidator(BPMNDiagram diagram) {
		this.diagram = diagram;
		this.errors = new HashMap<String,String>();
		conflictingBPMNNodes = new ArrayList<DiagramObject>();
	}

	//Use leadsToGoodMarking and bpmnNodes to see Validation results
	public void validate() {
		HighPetriNet net = new HighConverter(this.diagram).convert();
		WeakTerminationChecker checker = new WeakTerminationChecker(net, getFinalMarking(net));
		List<Transition> conflictingTransitions = new ArrayList<Transition>();
		
		leadsToEnd = checker.check(conflictingTransitions);
		
		//add bpmn objects belonging to a conflicting transition to conflictingBPMNNodes
		conflictingBPMNNodes.clear();
		for(Transition t : conflictingTransitions){
			DiagramObject obj = null;
			if (t instanceof HighSilentTransition){
				obj = ((HighSilentTransition)t).getBPMNObj();
			} else {
				obj = ((HighLabeledTransition)t).getBPMNObj();
			}
			if(!conflictingBPMNNodes.contains(obj))
				conflictingBPMNNodes.add(obj);
		}
	}
	
	public List<Marking> getFinalMarking(HighPetriNet net){
		this.diagram.identifyProcesses();
		
		//find all end events on highest level
		//List<EndEvent> endEvents = new ArrayList<EndEvent>();
		Marking m = new Marking(net);
		List<Marking> markings = new ArrayList<Marking>();
		markings.add(m);
		
		
		//TODO should be all end events be in the final marking? perhaps calculate
		//all combininations of end events, where at least one occurs?
		for(Container p : this.diagram.getProcesses()){
			for(Node n : p.getChildNodes()){
				//TODO terminate event shouldn't count for final marking?
				if(n instanceof EndEvent){
					for(Transition t : net.getTransitions()){
						if(t.getId().equals(n.getId())){
							for(FlowRelationship rel : t.getOutgoingFlowRelationships()){
								m.addToken((Place)rel.getTarget());
							}
						}
					}
				}
			}
		}
		
		return markings;
	}

	public List<DiagramObject> getConflictingBPMNNodes() {
		return conflictingBPMNNodes;
	}

	public void setConflictingBPMNNodes(List<DiagramObject> conflictingBPMNNodes) {
		this.conflictingBPMNNodes = conflictingBPMNNodes;
	}
}
