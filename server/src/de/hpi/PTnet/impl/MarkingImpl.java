package de.hpi.PTnet.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNetFactory;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;

public class MarkingImpl implements Marking {

	private PetriNet net;
	private Map<Place,Integer> marking = new HashMap();
	private String markingStr;
	
	public MarkingImpl(PetriNet net) {
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
		markingStr = null;
		marking.put(p, numTokens);
	}

	public void addToken(Place p) {
		markingStr = null;
		setNumTokens(p, getNumTokens(p) + 1);
	}

	public boolean removeToken(Place p) {
		markingStr = null;
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
					str.append("(").append(p.getId()).append(",").append(numt).append(")");
				}
			}
			str.append("}");
			markingStr = str.toString();
		}
		return markingStr;
	}
	
	public void reset() {
		markingStr = null;
	}

}
