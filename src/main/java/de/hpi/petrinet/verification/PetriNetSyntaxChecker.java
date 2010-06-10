package de.hpi.petrinet.verification;

import java.util.HashMap;
import java.util.Iterator;

import de.hpi.diagram.verification.AbstractSyntaxChecker;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

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
public class PetriNetSyntaxChecker extends AbstractSyntaxChecker {
/*
	private static final String NOT_BIPARTITE = "The graph is not bipartite";
	private static final String NO_LABEL = "Label not set for a labeled transition";
	private static final String NO_ID = "There is a node without id";
	private static final String SAME_SOURCE_AND_TARGET = "Two flow relationships have the same source and target";
	private static final String NODE_NOT_SET = "A node is not set for a flowrelationship";
*/	
	private static final String NOT_BIPARTITE = "PetriNet_NOT_BIPARTITE";
	private static final String NO_LABEL = "PetriNet_NO_LABEL";
	private static final String NO_ID = "PetriNet_NO_ID";
	private static final String SAME_SOURCE_AND_TARGET = "PetriNet_SAME_SOURCE_AND_TARGET";
	private static final String NODE_NOT_SET = "PetriNet_NODE_NOT_SET";
	
	protected PetriNet net;
	
	public PetriNetSyntaxChecker(PetriNet net) {
		this.net = net;
		this.errors = new HashMap<String,String>();
	}
	
	public boolean checkSyntax() {
		if (!properFlowrelationships())
			return false;
		if (!isBipartite())
			return false;
		if (!noDuplicateFlowrelationships())
			return false;
		errors.clear();
		return true;
	}

	protected boolean isBipartite() {
		for (Iterator<Place> it=net.getPlaces().iterator(); it.hasNext(); ) {
			Place p = it.next();
			if (p.getId() == null) {
				addNodeError(p, NO_ID);
				return false;
			}
		}
		for (Iterator<Transition> it=net.getTransitions().iterator(); it.hasNext(); ) {
			Transition t = it.next();
			if (t.getId() == null) {
				addNodeError(t, NO_ID);
				return false;
			}
			if (t instanceof LabeledTransition && ((LabeledTransition)t).getLabel() == null) {
				addNodeError(t, NO_LABEL);
				return false;
			}
		}
		for (Iterator<FlowRelationship> it=net.getFlowRelationships().iterator(); it.hasNext(); ) {
			FlowRelationship rel = it.next();
			Node s = rel.getSource();
			Node t = rel.getTarget();
			if (!((s instanceof Place && t instanceof Transition) || (s instanceof Transition && t instanceof Place))) {
				addNodeError(s, NOT_BIPARTITE);
				addNodeError(t, NOT_BIPARTITE);
				return false;
			}
		}
		return true;
	}
	
	protected boolean noDuplicateFlowrelationships() {
		for (Iterator<Place> it=net.getPlaces().iterator(); it.hasNext(); ) {
			Place p = it.next();
			for (Iterator<? extends FlowRelationship> it2=p.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
				FlowRelationship rel1 = it2.next();
				for (Iterator<? extends FlowRelationship> it3=rel1.getSource().getOutgoingFlowRelationships().iterator(); it3.hasNext(); ) {
					FlowRelationship rel2 = it3.next();
					if (rel1 != rel2 && p == rel2.getTarget()) {
						addFlowRelationshipError(rel1, SAME_SOURCE_AND_TARGET);
						return false;
					}
				}
			}
			for (Iterator<? extends FlowRelationship> it2=p.getOutgoingFlowRelationships().iterator(); it2.hasNext(); ) {
				FlowRelationship rel1 = it2.next();
				for (Iterator<? extends FlowRelationship> it3=rel1.getTarget().getIncomingFlowRelationships().iterator(); it3.hasNext(); ) {
					FlowRelationship rel2 = it3.next();
					if (rel1 != rel2 && p == rel2.getSource()) {
						addFlowRelationshipError(rel1, SAME_SOURCE_AND_TARGET);
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean properFlowrelationships() {
		for (Iterator<FlowRelationship> it=net.getFlowRelationships().iterator(); it.hasNext(); ) {
			FlowRelationship rel = it.next();
			if (rel.getSource() == null || rel.getTarget() == null) {
				addFlowRelationshipError(rel, NODE_NOT_SET);
				return false;
			}
		}
		return true;
	}
	
	protected void addNodeError(Node node, String errorCode) {
		errors.put(node.getResourceId(), errorCode);
	}

	protected void addFlowRelationshipError(FlowRelationship rel, String errorCode) {
		errors.put(rel.getResourceId(), errorCode);
	}

}
