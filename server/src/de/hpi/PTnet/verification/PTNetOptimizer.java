package de.hpi.PTnet.verification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.SilentTransition;
import de.hpi.petrinet.Transition;

public class PTNetOptimizer {
	
	protected PTNet net;
	
	public PTNetOptimizer(PTNet net) {
		this.net = net;
	}
	
	public boolean removeUnnecessaryPlaces() {
		return removeEndPlaces();
	}

	public boolean removeEndPlaces() {
		List<Place> plist = new ArrayList();
		for (Iterator<Place> it=net.getPlaces().iterator(); it.hasNext(); ) {
			Place p = it.next();
			if (p.getOutgoingFlowRelationships().size() > 0 || p.getIncomingFlowRelationships().size() > 1)
				continue;
			
			if (p.getIncomingFlowRelationships().size() == 0) {
				plist.add(p);
				continue;
			}
			
			// only eliminate it if the preceding transition has further output places
			FlowRelationship rel = p.getIncomingFlowRelationships().get(0);
			if (rel.getSource().getOutgoingFlowRelationships().size() > 1) {
				net.getFlowRelationships().remove(rel);
				plist.add(p);
			}
		}
		net.getPlaces().removeAll(plist);
		return (plist.size() > 0);
	}

	public boolean removeUnnecessaryTransitions() {
		List<Transition> tlist = new ArrayList();
		List<Place> plist = new ArrayList();
		for (Iterator<Place> it=net.getPlaces().iterator(); it.hasNext(); ) {
			Place p = it.next();
			if (p.getIncomingFlowRelationships().size() > 0 || net.getInitialMarking().getNumTokens(p) > 0)
				continue;
			
			plist.add(p);
			for (Iterator<? extends FlowRelationship> it2=p.getOutgoingFlowRelationships().iterator(); it2.hasNext(); ) {
				Transition t = (Transition)it2.next().getTarget();
				if (!tlist.contains(t))
					tlist.add(t);
			}
		}

		for (Iterator<Transition> it=tlist.iterator(); it.hasNext(); ) {
			Transition t = it.next();
			net.getFlowRelationships().removeAll(t.getIncomingFlowRelationships());
			net.getFlowRelationships().removeAll(t.getOutgoingFlowRelationships());
		}
		for (Iterator<Place> it=plist.iterator(); it.hasNext(); ) {
			Place p = it.next();
			net.getFlowRelationships().removeAll(p.getOutgoingFlowRelationships());
		}
		
		net.getTransitions().removeAll(tlist);
		net.getPlaces().removeAll(plist);

		return (tlist.size() > 0 || plist.size() > 0);
	}

	public boolean removeAllTauTransitions() {
		int oldsize = net.getTransitions().size();
		List<Transition> removeList = new ArrayList();
		PTNetReducer reducer = new PTNetReducer();
	
//		do {
			removeList.clear();
			
			for (Iterator<Transition> it=net.getTransitions().iterator(); it.hasNext(); ) {
				Transition t = it.next();
				if (shouldRemoveTransition(t))
					removeList.add(t);
			}
			
			reducer.removeTransitions(net, removeList);
//		} while (removeList.size() > 0);
		return (net.getTransitions().size() < oldsize);
	}

	public boolean removeEasyTauTransitions() {
		int oldsize = net.getTransitions().size();
		List<Transition> removeList = new ArrayList();
		PTNetReducer reducer = new PTNetReducer();
	
//		do {
			removeList.clear();
			addEasyTransitions(removeList);			
			reducer.removeTransitions(net, removeList);
//		} while (removeList.size() > 0);
		return (net.getTransitions().size() < oldsize);
	}

	protected boolean shouldRemoveTransition(Transition t) {
		if (!(t instanceof SilentTransition))
			return false;
		for (Iterator iter=t.getIncomingFlowRelationships().iterator(); iter.hasNext(); ) {
			FlowRelationship f = (FlowRelationship)iter.next();
			Place p = (Place)f.getSource();
			if (p.getOutgoingFlowRelationships().size() > 1)
				return false;
		}
		return true;
	}

	public void addEasyTransitions(List<Transition> removeList) {
		for (Iterator<Transition> it=net.getTransitions().iterator(); it.hasNext(); ) {
			Transition t = it.next();
			if (t instanceof SilentTransition) {
//				if (t.getIncomingFlowRelationships().size() > 1 || t.getOutgoingFlowRelationships().size() > 1)
//					continue;
				for (FlowRelationship rel: t.getIncomingFlowRelationships()) {
					Place p = (Place)rel.getSource();
					if (p.getOutgoingFlowRelationships().size() == 1) {
						removeList.add(t);
						break;
					}
				}
			}
		}
	}

	public boolean removeUnreachableTransitions() {
		Set<Transition> reachableTransitions = net.getInterpreter().getReachableTransitions(net, net.getInitialMarking());
		int oldsize = net.getTransitions().size();
		
		List<Transition> removeList = new ArrayList(net.getTransitions());
		removeList.removeAll(reachableTransitions);
	
//		do {
			for (Iterator<Transition> it=removeList.iterator(); it.hasNext(); ) {
				Transition t = it.next();
				net.getFlowRelationships().removeAll(t.getIncomingFlowRelationships());
				net.getFlowRelationships().removeAll(t.getOutgoingFlowRelationships());
				net.getTransitions().remove(t);
			}
//		} while (removeList.size() > 0);
		return (net.getTransitions().size() < oldsize);
	}
	
	public boolean removeRedundantPlaces() {
		Place[] places = (Place[])net.getPlaces().toArray();
		List<Place> removeList = new ArrayList<Place>();
		
		List<Transition> list = new ArrayList<Transition>();
		
		for (int i=0; i<places.length; i++) {
			Place p1 = places[i];
			for (int j=i+1; j<places.length; j++) {
				Place p2 = places[j];
				
				if (p1.getIncomingFlowRelationships().size() != p2.getIncomingFlowRelationships().size() ||
						p1.getOutgoingFlowRelationships().size() != p2.getOutgoingFlowRelationships().size())
					continue;
				
				boolean sameTransitions = true;
				list.clear();
				for (Iterator<? extends FlowRelationship> it=p1.getIncomingFlowRelationships().iterator(); it.hasNext(); )
					list.add((Transition)it.next().getSource());
				for (Iterator<? extends FlowRelationship> it=p2.getIncomingFlowRelationships().iterator(); it.hasNext(); ) {
					if (!list.contains((Transition)it.next().getSource())) {
						sameTransitions = false;
						break;
					}
				}
				if (!sameTransitions) 
					continue;
				list.clear();
				for (Iterator<? extends FlowRelationship> it=p1.getOutgoingFlowRelationships().iterator(); it.hasNext(); )
					list.add((Transition)it.next().getTarget());
				for (Iterator<? extends FlowRelationship> it=p2.getOutgoingFlowRelationships().iterator(); it.hasNext(); ) {
					if (!list.contains((Transition)it.next().getTarget())) {
						sameTransitions = false;
						break;
					}
				}
				if (sameTransitions)
					removeList.add(p2);
			}
		}
		for (Iterator<Place> it=removeList.iterator(); it.hasNext(); ) {
			Place p = it.next();
			net.getFlowRelationships().removeAll(p.getIncomingFlowRelationships());
			net.getFlowRelationships().removeAll(p.getOutgoingFlowRelationships());
			net.getPlaces().remove(p);
		}
		return removeList.size() > 0;
	}
	
	public boolean removeRedundantTransitions() {
		Object[] transitions = net.getTransitions().toArray();
		List<Transition> removeList = new ArrayList<Transition>();
		
		List<Place> list = new ArrayList<Place>();
		
		for (int i=0; i<transitions.length; i++) {
			Transition t1 = (Transition)transitions[i];
			for (int j=i+1; j<transitions.length; j++) {
				Transition t2 = (Transition)transitions[j];
				
				if (!t1.isSimilarTo(t2))
					continue;
				
				if (t1.getIncomingFlowRelationships().size() != t2.getIncomingFlowRelationships().size() ||
						t1.getOutgoingFlowRelationships().size() != t2.getOutgoingFlowRelationships().size())
					continue;
				
				boolean samePlaces = true;
				list.clear();
				for (FlowRelationship rel: t1.getIncomingFlowRelationships())
					list.add((Place)rel.getSource());
				for (FlowRelationship rel: t2.getIncomingFlowRelationships()) {
					if (!list.contains((Place)rel.getSource())) {
						samePlaces = false;
						break;
					}
				}
				if (!samePlaces) 
					continue;
				list.clear();
				for (FlowRelationship rel: t1.getOutgoingFlowRelationships())
					list.add((Place)rel.getTarget());
				for (FlowRelationship rel: t2.getOutgoingFlowRelationships()) {
					if (!list.contains((Place)rel.getTarget())) {
						samePlaces = false;
						break;
					}
				}
				if (samePlaces)
					removeList.add(t2);
			}
		}
		for (Iterator<Transition> it=removeList.iterator(); it.hasNext(); ) {
			Transition t = it.next();
			net.getFlowRelationships().removeAll(t.getIncomingFlowRelationships());
			net.getFlowRelationships().removeAll(t.getOutgoingFlowRelationships());
			net.getTransitions().remove(t);
		}
		return removeList.size() > 0;
	}
	
	public boolean removeRedundantTransitions(List<Transition> transitions) {
		return removeRedundantTransitions();
	}
	
}
