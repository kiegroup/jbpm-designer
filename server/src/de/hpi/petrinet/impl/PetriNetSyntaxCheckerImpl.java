package de.hpi.petrinet.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.SyntaxChecker;
import de.hpi.petrinet.Transition;

public class PetriNetSyntaxCheckerImpl implements SyntaxChecker {

	private static final String NOT_BIPARTITE = "The graph is not bipartite";
	private static final String NO_LABEL = "Label not set for a labeled transition";
	private static final String NO_ID = "There is a node without id";
	private static final String SAME_SOURCE_AND_TARGET = "Two flow relationships have the same source and target";
	private static final String NODE_NOT_SET = "A node is not set for a flowrelationship";
	
	protected PetriNet net;
	protected Map<String,String> errors;
	
	public PetriNetSyntaxCheckerImpl(PetriNet net) {
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

	public Map<String,String> getErrors() {
		return errors;
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
			for (Iterator<FlowRelationship> it2=p.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
				FlowRelationship rel1 = it2.next();
				for (Iterator<FlowRelationship> it3=rel1.getSource().getOutgoingFlowRelationships().iterator(); it3.hasNext(); ) {
					FlowRelationship rel2 = it3.next();
					if (rel1 != rel2 && p == rel2.getTarget()) {
						addFlowRelationshipError(rel1, SAME_SOURCE_AND_TARGET);
						return false;
					}
				}
			}
			for (Iterator<FlowRelationship> it2=p.getOutgoingFlowRelationships().iterator(); it2.hasNext(); ) {
				FlowRelationship rel1 = it2.next();
				for (Iterator<FlowRelationship> it3=rel1.getTarget().getIncomingFlowRelationships().iterator(); it3.hasNext(); ) {
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
		errors.put(node.getId(), errorCode);
	}

	protected void addFlowRelationshipError(FlowRelationship rel, String errorCode) {
		errors.put(rel.toString(), errorCode);
	}

}
