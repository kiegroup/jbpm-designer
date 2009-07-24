package de.hpi.PTnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hpi.PTnet.verification.BoundednessChecker;
import de.hpi.PTnet.verification.PTNetInterpreter;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;

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
public class PTNet extends PetriNet {
	
	protected Marking marking;
	
	protected Boolean isBound;
	
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
	
	public void setInitialMarking(Marking marking) {
		this.marking = marking;
		this.marking.setNet(this);
	}
	
	public PTNetFactory getFactory() {
		return PTNetFactory.eINSTANCE;
	}
	
	/**
	 * Creates a copy of the PTNet. Note that the initial marking
	 * is also copied.
	 * 
	 * @return the clone of the PTNet
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		PTNet clone = (PTNet) super.clone();
		Marking cloneMarking = new Marking(clone);

		// copy places once again, in order to introduce a MyPlaceList
		MyPlaceList myPlaceList = new MyPlaceList(clone);
		for(Place p : clone.getPlaces()) {
			myPlaceList.add(p);
		}
		clone.setPlaces(myPlaceList);		
		
		// set the marking for the clone
		for (Place p : clone.getPlaces()) {
			for (Place p2 : this.getPlaces()) {
				if (p.getId().equals(p2.getId())) 
					cloneMarking.setNumTokens(p, this.marking.getNumTokens(p2));
			}
		}
		clone.setInitialMarking(cloneMarking);
		return clone;
	}
	
//	@Override
//	protected boolean doOptimization(String parameter) {
//		if (parameter.equals(REMOVE_REDUNDANTPLACES))
//			return new PTNetOptimizer(this).removeRedundantPlaces();
//		else if (parameter.equals(REMOVE_REDUNDANTTRANSITIONS))
//			return new PTNetOptimizer(this).removeRedundantTransitions();
//		else if (parameter.equals(REMOVE_UNNECESSARYPLACES))
//			return new PTNetOptimizer(this).removeUnnecessaryPlaces();
//		else if (parameter.equals(REMOVE_UNNECESSARYTRANSITIONS))
//			return new PTNetOptimizer(this).removeUnnecessaryTransitions();
//		else if (parameter.equals(REMOVE_ALLTAUTRANSITIONS))
//			return new PTNetOptimizer(this).removeAllTauTransitions();
//		else if (parameter.equals(REMOVE_EASYTAUTRANSITIONS))
//			return new PTNetOptimizer(this).removeEasyTauTransitions();
//		else if (parameter.equals(REMOVE_UNREACHABLETRANSITIONS))
//			return new PTNetOptimizer(this).removeUnreachableTransitions();
//		else
//			return false;
//	}
	
	public PTNetInterpreter getInterpreter() {
		return getFactory().createInterpreter();
	}

	public boolean isBound() {
		if (this.isBound == null)
			this.isBound = new BoundednessChecker(new PTNetInterpreter(), this).checkBoundedness();
		return this.isBound;
	}
		
	protected class MyPlaceList extends ArrayList<Place> {
		
		private static final long serialVersionUID = -1042530176195412148L;
		protected PTNet net;
		
		public MyPlaceList(PTNet owner) {
			this.net = owner;
		}

		@Override
		public Place remove(int index) {
			((Marking)net.getInitialMarking()).reset();
			return super.remove(index);
		}

		@Override
		public boolean remove(Object o) {
			((Marking)net.getInitialMarking()).reset();
			return super.remove(o);
		}

		@Override
		protected void removeRange(int fromIndex, int toIndex) {
			((Marking)net.getInitialMarking()).reset();
			super.removeRange(fromIndex, toIndex);
		}

		@Override
		public boolean removeAll(Collection list) {
			((Marking)net.getInitialMarking()).reset();
			return super.removeAll(list);
		}
	}
	
}
