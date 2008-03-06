package de.hpi.nunet.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.hpi.nunet.Marking;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.Place;
import de.hpi.nunet.Token;

public class MarkingImpl implements Marking {
	
	private NuNet net;
	private Map<Place,List<Token>> marking;
	private Set<String> names;
	
	public MarkingImpl(NuNet net) {
		this.net = net;
		this.marking = new HashMap(net.getPlaces().size());
		this.names = null;
		
		for (Iterator<Place> it=net.getPlaces().iterator(); it.hasNext(); )
			marking.put(it.next(), new ArrayList());
	}
	
	public Marking getCopy() {
		MarkingImpl newmarking = new MarkingImpl(net);
		for (Iterator<Entry<Place,List<Token>>> it=marking.entrySet().iterator(); it.hasNext(); ) {
			Entry<Place,List<Token>> e = it.next();
			Place p = e.getKey();
			List<Token> tokens = e.getValue();
			
			// copy token list
			newmarking.getTokens(p).addAll(tokens);
//			for (Iterator<Token> it2=tokens.iterator(); it2.hasNext(); ) {
//				Token token = it2.next();
//				Token newtoken = new TokenImpl();
//				newtoken.getNames().addAll(token.getNames());
//				newmarking.get(p).add(newtoken);
//			}
		}
		return newmarking;
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (Iterator<Place> it=net.getPlaces().iterator(); it.hasNext(); ) {
			Place p = it.next();
			List<Token> tokens = getTokens(p);
			
			List<String> serializedTokens = new ArrayList(tokens.size());
			for (Iterator<Token> it2=tokens.iterator(); it2.hasNext(); ) {
				serializedTokens.add(it2.next().toString());
			}
			
			Collections.sort(serializedTokens); // creating the canonical form

			str.append("("+p.getLabel()+",{");
			for (Iterator<String> it2=serializedTokens.iterator(); it2.hasNext(); ) {
				str.append(it2.next());
				if (it2.hasNext())
					str.append(',');
			}
			str.append("})");
			if (it.hasNext())
				str.append(", ");
		}
		return str.toString();
	}
	
//	public String toString() {
//		List<Place> places = new ArrayList(marking.keySet());
//		Collections.sort(places, new Comparator<Place>() {
//			public int compare(Place p1, Place p2) {
//				return p1.getLabel().compareTo(p2.getLabel());
//			}
//		});
//		return toString(places);
//	}
//	
	public boolean equals(Object obj) {
		if (obj instanceof Marking)
			return toString().equals(((Marking)obj).toString());
		else
			return false;
	}
	
	public boolean containsName(String name) {
		return getNames().contains(name);
	}
	
	public Set<String> getNames() {
		if (names == null) {
			names = new HashSet();
			for (Iterator<List<Token>> it=marking.values().iterator(); it.hasNext(); ) {
				List<Token> tokens = it.next();
				for (Iterator<Token> it2=tokens.iterator(); it2.hasNext(); ) {
					Token token = it2.next();
					for (Iterator<String> it3=token.getNames().iterator(); it3.hasNext(); ) {
						String id = it3.next();
						if (!names.contains(id))
							names.add(id);
					}
				}
			}
		}
		return names;
	}
	
	public String toStringUncolored(List<Place> places) {
		StringBuilder str = new StringBuilder();
		for (Iterator<Place> it=places.iterator(); it.hasNext(); ) {
			Place p = it.next();
			int numtokens = marking.get(p).size();
			for (int i=0; i<numtokens; i++) {
				str.append(p.getLabel());
				if (it.hasNext())
					str.append(',');
			}
		}
		return str.toString();
	}

	public String toStringUncolored() {
		List<Place> places = new ArrayList(marking.keySet());
		Collections.sort(places, new Comparator<Place>() {
			public int compare(Place p1, Place p2) {
				return p1.getLabel().compareTo(p2.getLabel());
			}
		});
		return toStringUncolored(places);
	}

	public List<Token> getTokens(Place p) {
		List<Token> tokens = marking.get(p);
		if (tokens == null && net.getPlaces().contains(p)) {
			tokens = new ArrayList();
			marking.put(p, tokens);
		}
		return tokens;
	}

	public int getNumTokens() {
		int count = 0;
		for (Iterator<List<Token>> it=marking.values().iterator(); it.hasNext(); ) {
			count += it.next().size();
		}
		return count;
	}

}
