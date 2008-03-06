package de.hpi.PTnet.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetFactory;
import de.hpi.PTnet.PTNetInterpreter;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.impl.PetriNetImpl;

public class PTNetImpl extends PetriNetImpl implements PTNet {
	
	protected Marking marking;

	@Override
	public List<Place> getPlaces() {
		if (places == null)
			places = new MyPlaceList(this);
		return places;
	}

	public Marking getInitialMarking() {
		if (marking == null)
			marking = PTNetFactory.eINSTANCE.createMarking(this);
		return marking;
	}
	
	public PTNetFactory getFactory() {
		return PTNetFactory.eINSTANCE;
	}
	
	// TODO implement this
	public PTNet getCopy() {
		return null;
	}
	
	@Override
	protected boolean doOptimization(String parameter) {
		if (parameter.equals(REMOVE_REDUNDANTPLACES))
			return new PTNetOptimizer(this).removeRedundantPlaces();
		else if (parameter.equals(REMOVE_REDUNDANTTRANSITIONS))
			return new PTNetOptimizer(this).removeRedundantTransitions();
		else if (parameter.equals(REMOVE_UNNECESSARYPLACES))
			return new PTNetOptimizer(this).removeUnnecessaryPlaces();
		else if (parameter.equals(REMOVE_UNNECESSARYTRANSITIONS))
			return new PTNetOptimizer(this).removeUnnecessaryTransitions();
		else if (parameter.equals(REMOVE_ALLTAUTRANSITIONS))
			return new PTNetOptimizer(this).removeAllTauTransitions();
		else if (parameter.equals(REMOVE_EASYTAUTRANSITIONS))
			return new PTNetOptimizer(this).removeEasyTauTransitions();
		else if (parameter.equals(REMOVE_UNREACHABLETRANSITIONS))
			return new PTNetOptimizer(this).removeUnreachableTransitions();
		else
			return false;
	}
	
	public PTNetInterpreter getInterpreter() {
		return getFactory().createInterpreter();
	}

	protected class MyPlaceList extends ArrayList<Place> {
		
		private static final long serialVersionUID = -1042530176195412148L;
		protected PTNet net;
		
		public MyPlaceList(PTNet owner) {
			this.net = owner;
		}

		@Override
		public Place remove(int index) {
			((MarkingImpl)net.getInitialMarking()).reset();
			return super.remove(index);
		}

		@Override
		public boolean remove(Object o) {
			((MarkingImpl)net.getInitialMarking()).reset();
			return super.remove(o);
		}

		@Override
		protected void removeRange(int fromIndex, int toIndex) {
			((MarkingImpl)net.getInitialMarking()).reset();
			super.removeRange(fromIndex, toIndex);
		}

		@Override
		public boolean removeAll(Collection list) {
			((MarkingImpl)net.getInitialMarking()).reset();
			return super.removeAll(list);
		}
	}
	
}
