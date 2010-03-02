package de.hpi.nunet.validation;

import java.util.ArrayList;
import java.util.Iterator;

import de.hpi.nunet.FlowRelationship;
import de.hpi.nunet.InterconnectionModel;
import de.hpi.nunet.Node;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.Place;
import de.hpi.nunet.Token;
import de.hpi.nunet.Transition;

public class SyntaxValidator {
	
	protected static final String NOT_BIPARTITE = "The graph is not bipartite";
	private static final String INVALID_VARIABLE_LISTS = "The lengths of the arc expressions do not match";
	private static final String INVALID_TOKEN = "The number of names in a token does not match the corresponding arc expressions";
	private static final String NEW_IN_INPUT_VARIABLES = "'new' occurs as input variable";
	private static final String INVALID_OUTPUT_VARIABLES = "There are output variables which are not input variables";
	private static final String MISSING_PROCESS_MODEL = "There is no process model set for a transition";
	private static final String INVALID_INTERNAL_PLACE = "Two nodes of different process models are connected";
	private static final String INVALID_MARKING = "Tokens reside on a place with incoming edges";
	private static final String INVALID_MARKING2 = "Tokens reside on a communication place";
	
	private String errorCode;
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public boolean isValidNuNet(NuNet net) {
		if (!isBipartite(net))
			return false;
		if (!variableListsHaveValidLength(net))
			return false;
		if (!outputVariablesProperlySet(net))
			return false;
		errorCode = null;
		return true;
	}
	
	public boolean isValidInterconnectionModel(InterconnectionModel model) {
		if (!isValidNuNet(model))
			return false;
		if (!transitionsHaveProcessModelAssigned(model))
			return false;
		if (!internalPlacesConnectedToProperTransitions(model))
			return false;
		if (!hasValidMarking(model))
			return false;
		errorCode = null;
		return true;
	}

	protected boolean isBipartite(NuNet net) {
		for (Iterator<FlowRelationship> it=net.getFlowRelationships().iterator(); it.hasNext(); ) {
			FlowRelationship rel = it.next();
			Node s = rel.getSource();
			Node t = rel.getTarget();
			if (!((s instanceof Place && t instanceof Transition) || (s instanceof Transition && t instanceof Place))) {
				errorCode = NOT_BIPARTITE+" ("+s.getLabel()+" and "+t.getLabel()+" are connected)";
				return false;
			}
		}
		return true;
	}

	private boolean variableListsHaveValidLength(NuNet net) {
		for (Iterator<Place> it=net.getPlaces().iterator(); it.hasNext(); ) {
			Place p = it.next();
			int length = -1;
			
			for (Iterator<FlowRelationship> it2=p.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
				FlowRelationship rel = it2.next();
				if (length == -1)
					length = rel.getVariables().size();
				else if (length != rel.getVariables().size()) {
					errorCode = INVALID_VARIABLE_LISTS+" (place "+p.getLabel()+")";
					return false;
				}
			}
			
			for (Iterator<FlowRelationship> it2=p.getOutgoingFlowRelationships().iterator(); it2.hasNext(); ) {
				FlowRelationship rel = it2.next();
				if (length == -1)
					length = rel.getVariables().size();
				else if (length != rel.getVariables().size()) {
					errorCode = INVALID_VARIABLE_LISTS+" (place "+p.getLabel()+")";
					return false;
				}
			}
			
			for (Iterator<Token> it2=net.getInitialMarking().getTokens(p).iterator(); it2.hasNext(); ) {
				Token tok = it2.next();
				if (length == -1)
					length = tok.getNames().size();
				else if (length != tok.getNames().size()) {
					errorCode = INVALID_TOKEN+" (place "+p.getLabel()+")";
					return false;
				}
			}
		}
		return true;
	}

	private boolean outputVariablesProperlySet(NuNet net) {
		ArrayList inputVariables = new ArrayList();
		for (Iterator<Transition> it=net.getTransitions().iterator(); it.hasNext(); ) {
			Transition t = it.next();
			
			// retrieve all input variables
			inputVariables.clear();
			for (Iterator<FlowRelationship> it2=t.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
				FlowRelationship rel = it2.next();
				inputVariables.addAll(rel.getVariables());
			}
			
			if (inputVariables.contains(NuNet.NEW)) {
				errorCode = NEW_IN_INPUT_VARIABLES+" (transition "+t.getLabel()+")";
				return false;
			}
			
			for (Iterator<FlowRelationship> it2=t.getOutgoingFlowRelationships().iterator(); it2.hasNext(); ) {
				FlowRelationship rel = it2.next();
				
				for (Iterator<String> it3=rel.getVariables().iterator(); it3.hasNext(); ) {
					String v = it3.next();
					if (!v.equals(NuNet.NEW) && !inputVariables.contains(v)) {
						errorCode = INVALID_OUTPUT_VARIABLES+" (transition "+t.getLabel()+")";
						return false;
					}
				}
			}
		}
		
		return true;
	}

	private boolean transitionsHaveProcessModelAssigned(InterconnectionModel model) {
		for (Iterator<Transition> it=model.getTransitions().iterator(); it.hasNext(); ) {
			Transition t = it.next();
			if (t.getProcessModel() == null) {
				errorCode = MISSING_PROCESS_MODEL+" (transition "+t.getLabel()+")";
				return false;
			}
		}
		return true;
	}

	private boolean internalPlacesConnectedToProperTransitions(InterconnectionModel model) {
		for (Iterator<Place> it=model.getPlaces().iterator(); it.hasNext(); ) {
			Place p = it.next();
			if (p.getProcessModel() == null) 
				continue;
			for (Iterator<FlowRelationship> it2=p.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
				Transition t = (Transition)it2.next().getSource();
				if (!t.getProcessModel().equals(p.getProcessModel())) {
					errorCode = INVALID_INTERNAL_PLACE+" (place "+p.getLabel()+" and transition "+t.getLabel()+")";
					return false;
				}
			}
			for (Iterator<FlowRelationship> it2=p.getOutgoingFlowRelationships().iterator(); it2.hasNext(); ) {
				Transition t = (Transition)it2.next().getTarget();
				if (!t.getProcessModel().equals(p.getProcessModel())) {
					errorCode = INVALID_INTERNAL_PLACE+" (place "+p.getLabel()+" and transition "+t.getLabel()+")";
					return false;
				}
			}
		}
		return true;
	}

	private boolean hasValidMarking(InterconnectionModel model) {
		for (Iterator<Place> it=model.getPlaces().iterator(); it.hasNext(); ) {
			Place p = it.next();
			if (model.getInitialMarking().getTokens(p).size() > 0 && p.getIncomingFlowRelationships().size() > 0) {
				errorCode = INVALID_MARKING+" (place "+p.getLabel()+")";
				return false;
			}
			if (model.getInitialMarking().getTokens(p).size() > 0 && p.getProcessModel() == null) {
				errorCode = INVALID_MARKING2+" (place "+p.getLabel()+")";
				return false;
			}
		}
		return true;
	}

}
