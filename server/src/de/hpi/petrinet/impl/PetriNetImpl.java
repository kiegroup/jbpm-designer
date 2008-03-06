package de.hpi.petrinet.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.Marking;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;
import de.hpi.petrinet.PetriNetInterpreter;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.SyntaxChecker;
import de.hpi.petrinet.Transition;

public class PetriNetImpl implements PetriNet {
	
	protected List<Place> places;
	protected List<Transition> transitions;
	protected List<FlowRelationship> flowRelationships;

	public List<FlowRelationship> getFlowRelationships() {
		if (flowRelationships == null)
			flowRelationships = new MyFlowRelationshipList();
		return flowRelationships;
	}
	
	public List<Place> getPlaces() {
		if (places == null)
			places = new ArrayList();
		return places;
	}

	public List<Transition> getTransitions() {
		if (transitions == null)
			transitions = new ArrayList();
		return transitions;
	}
	
	public SyntaxChecker getSyntaxChecker() {
		return new PetriNetSyntaxCheckerImpl(this);
	}

	// TODO to be implemented
	public PetriNet getCopy() {
		return null;
	}

	public PetriNetFactory getFactory() {
		return PetriNetFactory.eINSTANCE;
	}

	public Marking getInitialMarking() {
		return null;
	}
	

	public void optimize(Map<String,Boolean> parameters) {
		boolean changed = false;
		do {
			changed = false;
			if (parameters != null)
				for (Iterator<Entry<String,Boolean>> it=parameters.entrySet().iterator(); it.hasNext(); ) {
					Entry<String,Boolean> e = it.next();
					if (e.getValue().booleanValue())
						changed |= doOptimization(e.getKey());
				}
		} while (changed);
	}
	
	protected boolean doOptimization(String parameter) {
		return false;
	}
	
//	protected boolean doOptimization(Map<String,Boolean> parameters, String parameter) {
//		if (parameters == null)
//			return false;
//		Boolean b = parameters.get(parameter);
//		if (b == null)
//			return false;
//		else
//			return b.booleanValue();
//	}
	
	public PetriNetInterpreter getInterpreter() {
		return null;
	}

	protected class MyFlowRelationshipList extends ArrayList<FlowRelationship> {
		
		private static final long serialVersionUID = 7350067193890668068L;

		@Override
		public FlowRelationship remove(int index) {
			FlowRelationship rel = super.remove(index);
			if (rel != null) {
				rel.setSource(null);
				rel.setTarget(null);
			}
			return rel;
		}

		@Override
		public boolean remove(Object o) {
			boolean removed = super.remove(o);
			if (removed) {
				((FlowRelationship)o).setSource(null);
				((FlowRelationship)o).setTarget(null);
			}
			return removed;
		}

		@Override
		protected void removeRange(int fromIndex, int toIndex) {
			for (int i=fromIndex; i<toIndex; i++) {
				FlowRelationship rel = get(i);
				rel.setSource(null);
				rel.setTarget(null);
			}
			super.removeRange(fromIndex, toIndex);
		}

		@Override
		public boolean removeAll(Collection list) {
			List mylist = new ArrayList(list);
			for (Iterator it=mylist.iterator(); it.hasNext(); ) {
				FlowRelationship rel = (FlowRelationship)it.next();
				rel.setSource(null);
				rel.setTarget(null);
			}
			return super.removeAll(mylist);
		}
	}
	
}
