package de.hpi.PTnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;


/**
 * Copyright (c) 2008 Gero Decker, Kai Schlichting
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
public class Marking implements de.hpi.petrinet.Marking {
	
	private PetriNet net;
	private Map<Place,Integer> marking = new HashMap<Place,Integer>();
	private String markingStr;
	
	public Marking(PetriNet net) {
		this.net = net;
	}
	
	public Marking getCopy() {
		Marking newmarking = PTNetFactory.eINSTANCE.createMarking(net);
		for (Iterator<Entry<Place,Integer>> it=marking.entrySet().iterator(); it.hasNext(); ) {
			Entry<Place,Integer> e = it.next();
			newmarking.setNumTokens(e.getKey(), e.getValue());
		}
		return newmarking;
	}

	public int getNumTokens() {
		int count = 0;
		for (Iterator<Integer> it=marking.values().iterator(); it.hasNext(); )
			count += it.next();
		return count;
	}

	public int getNumTokens(Place p) {
		Integer numTokens = marking.get(p);
		if (numTokens == null)
			return 0;
		else
			return numTokens.intValue();
	}

	public void setNumTokens(Place p, int numTokens) {
		reset();
		marking.put(p, numTokens);
	}

	public void addToken(Place p) {
		reset();
		setNumTokens(p, getNumTokens(p) + 1);
	}

	public boolean removeToken(Place p) {
		reset();
		int numTokens = getNumTokens(p);
		if (numTokens > 0) {
			setNumTokens(p, numTokens-1);
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		if (markingStr == null) {
			StringBuilder str = new StringBuilder();
			str.append("{");
			boolean firstEntry = true;
			for (Place p: net.getPlaces()) {
				int numt = getNumTokens(p);
				if (numt > 0) {
					if (firstEntry)
						firstEntry = false;
					else
						str.append(",");
					str.append(p.getId());
					if (numt > 1)
						str.append(":").append(numt);
				}
			}
			str.append("}");
			markingStr = str.toString();
		}
		return markingStr;
	}
	
	protected void reset() {
		markingStr = null;
	}

	public List<Place> getMarkedPlaces() {
		List<Place> places = new ArrayList<Place>();
		for (Place p: net.getPlaces()) {
			if (getNumTokens(p) > 0)
				places.add(p);
		}
		return places;
	}
	
	public Place findUnsafePlace() {
		for (Place p: net.getPlaces()) {
			if (getNumTokens(p) > 1)
				return p;
		}
		return null;
	}
	
	public boolean equals(Object o){
		if(o instanceof Marking){
			return toString().equals(((Marking)o).toString());
		} else {
			return false;
		}
	}

	public JSONObject toJson() throws JSONException {
		JSONObject marking = new JSONObject();
		
		for(Place place : net.getPlaces()){
			marking.put(place.getResourceId(), this.getNumTokens(place));
		}
		
		return marking;
	}

	/**
	 * Returns true if all intermediate places have no token and at least one 
	 * end place have a token. This implementation refers to the definition of 
	 * the end state of a workflow net (a Petri net with 1 end place) 
	 */
	public boolean isFinalMarking() {
		return !hasTokenOnIntermediatePlace() && hasOneTokenOnFinalPlace();
	}
	
	public List<Transition> getEnabledTransitions(){
		return net.getInterpreter().getEnabledTransitions(net, this);
	}
	
	public boolean hasEnabledTransitions(){
		return getEnabledTransitions().size() > 0;
	}
	
	/**
	 * Returns true if there is a token on a non-final place, but no transition can fire anymore.
	 */
	public boolean isDeadlock(){
		return hasTokenOnIntermediatePlace() && !hasEnabledTransitions();
	}
	
	protected boolean hasOneTokenOnFinalPlace(){
		for(Place place : net.getPlaces()){
			if(place.isFinalPlace() && this.getNumTokens(place) == 1){
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean hasTokenOnIntermediatePlace(){
		for(Place place : net.getPlaces()){
			if(!place.isFinalPlace() && this.getNumTokens(place) > 0){
				return true;
			}
		}
		return false;
	}

	public PetriNet getNet() {
		return net;
	}

	public void setNet(PetriNet net) {
		this.net = net;
	}
}
