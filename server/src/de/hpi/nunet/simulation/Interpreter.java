package de.hpi.nunet.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hpi.nunet.FlowRelationship;
import de.hpi.nunet.Marking;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.Place;
import de.hpi.nunet.Token;
import de.hpi.nunet.Transition;
import de.hpi.nunet.EnabledTransition;
import de.hpi.nunet.NuNetFactory;

public class Interpreter {
	
	private NuNetFactory factory = NuNetFactory.eINSTANCE;
	
	public List<EnabledTransition> getEnabledTransitions(NuNet net, Marking marking) {
		List<EnabledTransition> transitions = new ArrayList<EnabledTransition>();
		
		for (Iterator<Transition> it = net.getTransitions().iterator(); it.hasNext(); ) {
			Transition t = it.next();

			// quick check: is there a token on each input place?
			if (!enoughInputTokens(t, marking))
				continue;
			
			// are there any incoming flow relationships?
			if (t.getIncomingFlowRelationships().size() == 0) {
				transitions.add(new EnabledTransition(t, new HashMap<String,String>()));
				continue;
			}
			
			// now we have to find all matching modes
			addMatchingModes(t, marking, transitions);
		}
		
		return transitions;
	}

	private boolean enoughInputTokens(Transition t, Marking marking) {
		for (Iterator<FlowRelationship> it2=t.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
			Place p = (Place)it2.next().getSource();
			if (marking.getTokens(p).size() == 0) 
				return false;
		}
		return true;
	}

	// precondition: at least one input place + lengths of names and variables correspond
	private void addMatchingModes(Transition t, Marking marking, List<EnabledTransition> transitions) {
		// copy the list of incoming flow relationships
		List<FlowRelationship> rels = new ArrayList<FlowRelationship>(t.getIncomingFlowRelationships());
		
		//
		Token[] tokens = new Token[rels.size()];
		
		Map<String,String> mode = new HashMap<String,String>();
		addMatchingModes(t, rels, marking, 0, mode, tokens, transitions);
	}

	private void addMatchingModes(Transition t, List<FlowRelationship> rels, Marking marking, int i, Map<String, String> currentMode, Token[] tokens, List<EnabledTransition> transitions) {
		if (i == 0)
			currentMode.clear(); // this map is used for all combinations of tokens...
		
		FlowRelationship rel = rels.get(i);
		for (Iterator<Token> it2=marking.getTokens((Place)rel.getSource()).iterator(); it2.hasNext(); ) {
			Token token = it2.next();
			tokens[i] = token;

			Map<String, String> mode = new HashMap<String, String>(currentMode);
			
			// check if the current token violates the current mode
			if (!extendCurrentMode(rel.getVariables(), token, mode))
				continue;
			
			if (i+1 < rels.size()) {
				addMatchingModes(t, rels, marking, i+1, mode, tokens, transitions);
			} else {
				Token[] tokenscopy = new Token[tokens.length];
				System.arraycopy(tokens, 0, tokenscopy, 0, tokens.length);
				transitions.add(new EnabledTransition(t, mode, tokenscopy));
			}
		}
	}

	private boolean extendCurrentMode(List<String> variables, Token token, Map<String, String> currentMode) {
		Iterator<String> itv=variables.iterator();
		for (Iterator<String> itn=token.getNames().iterator(); itn.hasNext(); ) {
			String n = itn.next();
			String v = itv.next();
			
			String cN = currentMode.get(v);
			if (cN == null)
				currentMode.put(v, n);
			else if (!cN.equals(n))
				return false;
		}
		return true;
	}

	// NEW must be set in the mode
	public void fireTransition(NuNet net, Marking marking, EnabledTransition tmode) {
		if (tmode.createsFreshName() && tmode.mode.get(NuNet.NEW) == null) {
			tmode.mode.put(NuNet.NEW, createFreshName(marking));
		}
		
		// remove tokens from input places
		int i=0;
		for (Iterator<FlowRelationship> it = tmode.transition.getIncomingFlowRelationships().iterator(); it.hasNext(); i++) {
			Place p = (Place)it.next().getSource();
			marking.getTokens(p).remove(tmode.tokens[i]);
		}
		
		// produce tokens on output places
		for (Iterator<FlowRelationship> it = tmode.transition.getOutgoingFlowRelationships().iterator(); it.hasNext(); ) {
			FlowRelationship rel = it.next();
			Place p = (Place)rel.getTarget();
			
			// create new token
			Token newtoken = factory.createToken();
			for (Iterator<String> it2 = rel.getVariables().iterator(); it2.hasNext(); ) {
				newtoken.getNames().add(tmode.mode.get(it2.next()));
			}
			
			marking.getTokens(p).add(newtoken);
		}
	}
	
	public void fireTransition(NuNet net, Marking marking, Transition transition, Map<String,String> mode, String newName) {
		// find tokens
//		for (Iterator<FlowRelationship> it = transition.getIncomingFlowRelationships().iterator(); it.hasNext(); ) {
//		Place p = (Place)it.next().getSource();
//		// find token
//		for (Iterator<List<String>> it2 = p.getTokens().iterator(); it2.hasNext(); ) {
//			for (Iterator<>)
//		}
//	}
		
	}
	
	private String createFreshName(Marking m) {
        int freshNameCounter = 1;
		while (m.containsName("new#"+freshNameCounter)) {
			freshNameCounter++;
		}
		return "new#"+freshNameCounter;
	}

}
