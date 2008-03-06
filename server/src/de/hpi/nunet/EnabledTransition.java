package de.hpi.nunet;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class EnabledTransition {
	
	public EnabledTransition(Transition transition, Map<String,String> mode) {
		this.transition = transition;
		this.mode = mode;
		this.tokens = new Token[0];
	}
	
	public EnabledTransition(Transition transition, Map<String,String> mode, Token[] tokens) {
		this.transition = transition;
		this.mode = mode;
		this.tokens = tokens;
	}
	
//	public TransitionMode(Transition transition, List<String>[] tokens) {
//		this.transition = transition;
//		this.tokens = tokens;
//		setMode();
//	}
	
	public Transition transition;
	public Map<String,String> mode; // maps names to variables
	public Token[] tokens;
	
//	private void setMode() {
//		// to be implemented...
//	}
//
	
	public boolean createsFreshName() {
		for (Iterator<FlowRelationship> it=transition.getOutgoingFlowRelationships().iterator(); it.hasNext(); ) {
			FlowRelationship rel = it.next();
			for (Iterator<String> it2=rel.getVariables().iterator(); it2.hasNext(); ) {
				String v = it2.next();
				if (v.equals(NuNet.NEW)) 
					return true;
			}
		}
		return false;
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("transition ").append(transition.getLabel()).append(", mode = {");
		for (Iterator<Entry<String,String>> it = mode.entrySet().iterator(); it.hasNext(); ) {
			Entry<String,String> entry = it.next();
			str.append("(").append(entry.getKey()).append(", ").append(entry.getValue()).append(")");
			if (it.hasNext())
				str.append(", ");
		}
		str.append("}");
		return str.toString();
	}

	public boolean matches(EnabledTransition tmode) {
		if (!transition.getLabel().equals(tmode.transition.getLabel()))
			return false;
		for (Iterator<Entry<String,String>> it=mode.entrySet().iterator(); it.hasNext(); ) {
			Entry<String,String> e = it.next();
			if (tmode.mode.containsKey(e.getKey()) && !tmode.mode.get(e.getKey()).equals(e.getValue()))
				return false;
		}
		for (Iterator<Entry<String,String>> it=tmode.mode.entrySet().iterator(); it.hasNext(); ) {
			Entry<String,String> e = it.next();
			if (mode.containsKey(e.getKey()) && !mode.get(e.getKey()).equals(e.getValue()))
				return false;
		}
		return true;
	}
}
