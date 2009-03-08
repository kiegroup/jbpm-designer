package de.hpi.petrinet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;

import de.hpi.diagram.verification.SyntaxChecker;
import de.hpi.petrinet.serialization.PetriNetPNMLExporter;
import de.hpi.petrinet.serialization.XMLFileLoaderSaver;
import de.hpi.petrinet.verification.PetriNetInterpreter;
import de.hpi.petrinet.verification.PetriNetSyntaxChecker;

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
public class PetriNet {
	
	protected List<Place> places;
	protected List<Transition> transitions;
	protected List<FlowRelationship> flowRelationships;
	protected List<Place> finalPlaces;

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
		return new PetriNetSyntaxChecker(this);
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
	

//	public void optimize(Map<String,Boolean> parameters) {
//		boolean changed = false;
//		do {
//			changed = false;
//			if (parameters != null)
//				for (Iterator<Entry<String,Boolean>> it=parameters.entrySet().iterator(); it.hasNext(); ) {
//					Entry<String,Boolean> e = it.next();
//					if (e.getValue().booleanValue())
//						changed |= doOptimization(e.getKey());
//				}
//		} while (changed);
//	}
//	
//	protected boolean doOptimization(String parameter) {
//		return false;
//	}
	
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

	@Override
	public String toString() {
		try {
			XMLFileLoaderSaver ls = new XMLFileLoaderSaver();
			Document doc = ls.createNewDocument();
			new PetriNetPNMLExporter().savePetriNet(doc, this);
			return ls.serializeToString(doc);
		} catch (Exception e) {
			return super.toString();
		}		
	}
	
	/**
	 * Returns all final places of this petri net (cached).
	 */
	public List<Place> getFinalPlaces(){
		if (finalPlaces == null) {
			finalPlaces = new LinkedList<Place>();
			for (Place place : this.getPlaces()) {
				if (place.isFinalPlace()) {
					finalPlaces.add(place);
				}
			}
		}

		return finalPlaces;
	}

	/**
	 * Returns the first final place, intended for use in workflow nets.
	 */
	public Place getFinalPlace(){
		return this.getFinalPlaces().get(0);
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
