package de.hpi.interactionnet.localmodelgeneration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.TauTransition;
import de.hpi.petrinet.Transition;

public class LocalModelGenerator {
	
	public InteractionNet generateLocalModel(InteractionNet net, String roleName) {
		net = net.getCopy();
		StateSpaceAdmin admin = new StateSpaceAdmin(net);
		
		NextTransition nt = null;
		while ((nt = findNextTransition(net, roleName, admin)) != null) {
			List<Transition> tlist = getPrecedingTransitions(nt.m, nt.t, roleName);
			
			for (Transition t: tlist) {
				if (isLoopingTransition(t)) {
					applyRule1(net, t);
				} else if (!sharesInputPlace(t)) { // || !existsAlternativeEnablement(net, t)) {
					applyRule2(net, t);
				} else {
					applyRule3(net, t);
				}
			}
		}
		
		cleanUpPlaces(net);
		
		return net;
	}
	
	private void cleanUpPlaces(InteractionNet net) {
		List<Marking> removeM = new ArrayList<Marking>();
		List<Place> removeP = new ArrayList<Place>();
		for (Place p: net.getPlaces()) {
			if (p.getIncomingFlowRelationships().size() == 0 && p.getOutgoingFlowRelationships().size() == 0) {
				removeM.clear();
				for (Marking m: net.getFinalMarkings())
					if (m.getNumTokens(p) > 0)
						removeM.add(m);
				net.getFinalMarkings().removeAll(removeM);
				
				removeP.add(p);
			}
		}
		net.getPlaces().removeAll(removeP);
	}

	class NextTransition {
		NextTransition(Transition t, Marking m) {
			this.t = t;
			this.m = m;
		}
		Transition t;
		Marking m;
	}
	
	private NextTransition findNextTransition(InteractionNet newnet, String roleName, StateSpaceAdmin admin) {
		for (Transition t: newnet.getTransitions()) {
			if (t instanceof InteractionTransition) {
				InteractionTransition it = (InteractionTransition) t;
				if (inRoles(it, roleName)) continue;
				
				Marking m = admin.findPostMarking(it);
				if (m != null)
					return new NextTransition(it, m);
			}
		}
		return null;
	}
	
	private boolean inRoles(InteractionTransition t, String roleName) {
		return roleName.equals(t.getSender().getName()) || roleName.equals(t.getReceiver().getName());
	}

	private List<Transition> getPrecedingTransitions(Marking m, Transition t, String roleName) {
		List<Transition> ptlist = new ArrayList<Transition>();
		List<Place> outputPlaces = m.getMarkedPlaces();
		for (Place p: outputPlaces) {
			for (FlowRelationship rel: p.getIncomingFlowRelationships()) {
				Transition t2 = (Transition)rel.getSource();
				if (t2 == t || (t2 instanceof InteractionTransition && !inRoles((InteractionTransition)t2, roleName) && couldHaveFired(m, t))) {
					ptlist.add(t2);
				}
			}
		}
		return ptlist;
	}

	private Marking unfireTransition(Marking m, Transition t) {
		m = (Marking)m.getCopy();
		for (FlowRelationship rel: t.getOutgoingFlowRelationships())
			m.removeToken((Place)rel.getTarget());
		for (FlowRelationship rel: t.getIncomingFlowRelationships())
			m.addToken((Place)rel.getSource());
		return m;
	}

	private boolean couldHaveFired(Marking m, Transition t) {
		for (FlowRelationship rel: t.getOutgoingFlowRelationships()) {
			Place p = (Place)rel.getTarget();
			if (m.getNumTokens(p) == 0)
				return false;
		}
		return true;
	}

	protected boolean isLoopingTransition(Transition t) {
		if (t.getIncomingFlowRelationships().size() != t.getOutgoingFlowRelationships().size())
			return false;
		for (FlowRelationship rel: t.getIncomingFlowRelationships()) {
			Place p = (Place)rel.getSource();
			if (!isOutputPlace(t, p))
				return false;
		}
		for (FlowRelationship rel: t.getOutgoingFlowRelationships()) {
			Place p = (Place)rel.getTarget();
			if (!isInputPlace(p, t))
				return false;
		}
		return true;
	}

	protected boolean isInputPlace(Place p, Transition i) {
		for (FlowRelationship rel: i.getIncomingFlowRelationships()) {
			if (rel.getSource() == p)
				return true;
		}
		return false;
	}

	protected boolean isOutputPlace(Transition i, Place p) {
		for (FlowRelationship rel: i.getOutgoingFlowRelationships()) {
			if (rel.getTarget() == p)
				return true;
		}
		return false;
	}

	// simply deletes a transition
	protected void applyRule1(PetriNet net, Transition i) {
		net.getFlowRelationships().removeAll(i.getIncomingFlowRelationships());
		net.getFlowRelationships().removeAll(i.getOutgoingFlowRelationships());
		net.getTransitions().remove(i);
	}

	private int freshNameCounter = 1;
	
	// merges places
	protected void applyRule2(PetriNet net, Transition i) {
		for (Iterator<FlowRelationship> p1iter=i.getIncomingFlowRelationships().iterator(); p1iter.hasNext(); ) {
			FlowRelationship f1 = p1iter.next();
			Place p1 = (Place)f1.getSource();
			
			for (Iterator<FlowRelationship> p2iter=i.getOutgoingFlowRelationships().iterator(); p2iter.hasNext(); ) {
				FlowRelationship f2 = p2iter.next();
				Place p2 = (Place)f2.getTarget();

				// for each pair p1 / p2 we create a new place
				Place pnew = createNewPlace(net, p1, p2);
				net.getPlaces().add(pnew);
				pnew.setId("Px"+(freshNameCounter++));

				// add the flows
				for (Iterator<FlowRelationship> iter2=p1.getIncomingFlowRelationships().iterator(); iter2.hasNext(); ) {
					Transition i2 = (Transition)iter2.next().getSource();
					if (i2 != i) {
						FlowRelationship f = net.getFactory().createFlowRelationship();
						net.getFlowRelationships().add(f);
						f.setSource(i2);
						f.setTarget(pnew);
					}
				}
				for (Iterator<FlowRelationship> iter2=p2.getIncomingFlowRelationships().iterator(); iter2.hasNext(); ) {
					Transition i2 = (Transition)iter2.next().getSource();
					if (i2 != i) {
						FlowRelationship f = net.getFactory().createFlowRelationship();
						net.getFlowRelationships().add(f);
						f.setSource(i2);
						f.setTarget(pnew);
					}
				}
				for (Iterator<FlowRelationship> iter2=p1.getOutgoingFlowRelationships().iterator(); iter2.hasNext(); ) {
					Transition i2 = (Transition)iter2.next().getTarget();
					if (i2 != i) {
						FlowRelationship f = net.getFactory().createFlowRelationship();
						net.getFlowRelationships().add(f);
						f.setSource(pnew);
						f.setTarget(i2);
					}
				}
				for (Iterator<FlowRelationship> iter2=p2.getOutgoingFlowRelationships().iterator(); iter2.hasNext(); ) {
					Transition i2 = (Transition)iter2.next().getTarget();
					if (i2 != i) {
						FlowRelationship f = net.getFactory().createFlowRelationship();
						net.getFlowRelationships().add(f);
						f.setSource(pnew);
						f.setTarget(i2);
					}
				}
			}
		}

		// remove old places and flow relationships
		List<Place> rp = new ArrayList();
		for (Iterator<FlowRelationship> p1iter=i.getIncomingFlowRelationships().iterator(); p1iter.hasNext(); )
			rp.add((Place)p1iter.next().getSource());
		for (Iterator<FlowRelationship> p2iter=i.getOutgoingFlowRelationships().iterator(); p2iter.hasNext(); )
			rp.add((Place)p2iter.next().getTarget());
		for (Iterator<Place> pit=rp.iterator(); pit.hasNext(); ) {
			Place px = pit.next();
			net.getFlowRelationships().removeAll(px.getIncomingFlowRelationships());
			net.getFlowRelationships().removeAll(px.getOutgoingFlowRelationships());
		}
		net.getPlaces().removeAll(rp);
		net.getFlowRelationships().removeAll(i.getIncomingFlowRelationships());
		net.getFlowRelationships().removeAll(i.getOutgoingFlowRelationships());
		net.getTransitions().remove(i);
	}

	protected void applyRule3(PetriNet net, Transition i) {
//		List<Transition> newtransitions = new ArrayList<Transition>();

		List<Transition> succ = new ArrayList();
		List<Place> out = new ArrayList();
		for (Iterator<FlowRelationship> iter2=i.getOutgoingFlowRelationships().iterator(); iter2.hasNext(); ) {
			Place p = (Place)iter2.next().getTarget();
			out.add(p);
//			if (isInputPlace(p, i))
//				continue;
			for (Iterator<FlowRelationship> iter3=p.getOutgoingFlowRelationships().iterator(); iter3.hasNext(); ) {
				Transition i2 = (Transition)iter3.next().getTarget();
				if (!i2.equals(i) && !succ.contains(i2))
					succ.add(i2);
			}
		}
		
		for (Iterator<Transition> iter2=succ.iterator(); iter2.hasNext(); ) {
			Transition i2 = iter2.next();
			
			// check if the sequence i => i2 is possible at all
			if (isBadSequence(i, i2)) {
				
				if (!hasOtherInputsOrMarkedInputs(net, i2, i)) {
					net.getFlowRelationships().removeAll(i2.getIncomingFlowRelationships());
					net.getFlowRelationships().removeAll(i2.getOutgoingFlowRelationships());
					net.getTransitions().remove(i2);
				}
				
			} else {

				List<Place> in = new ArrayList();
				for (Iterator<FlowRelationship> iter3=i2.getIncomingFlowRelationships().iterator(); iter3.hasNext(); ) {
					Place p = (Place)iter3.next().getSource();
					in.add(p);
				}
	
				Transition inew = null;
				if (hasOtherInputsOrMarkedInputs(net, i2, i)) { // this is an optimization for avoiding unnecessary duplication
					inew = createNewTransition(net, i2);
					inew.setId("Tx"+(freshNameCounter++));
					net.getTransitions().add(inew);
	//				newtransitions.add(inew);
				} else {
					inew = i2;
				}
				
				// remove input places
				if (inew == i2) {
					for (Iterator<FlowRelationship> p1iter=i2.getIncomingFlowRelationships().iterator(); p1iter.hasNext(); ) {
						Place p1 = (Place)p1iter.next().getSource();
						net.getFlowRelationships().removeAll(p1.getIncomingFlowRelationships());
						net.getPlaces().remove(p1);
					}
					net.getFlowRelationships().removeAll(i2.getIncomingFlowRelationships());
				}
	
				for (Iterator<FlowRelationship> iter3=i.getIncomingFlowRelationships().iterator(); iter3.hasNext(); ) {
					Place p = (Place)iter3.next().getSource();
					if (!isInputPlace(p, inew))
						net.getFlowRelationships().add(createFlowRelationship(net, p, inew));
				}
				if (inew != i2)
					for (Iterator<FlowRelationship> iter3=i2.getIncomingFlowRelationships().iterator(); iter3.hasNext(); ) {
						Place p = (Place)iter3.next().getSource();
						if (!out.contains(p) && !isInputPlace(p, inew))
							net.getFlowRelationships().add(createFlowRelationship(net, p, inew));
					}
				for (Iterator<Place> iter3=out.iterator(); iter3.hasNext(); ) {
					Place p = iter3.next();
					if (!in.contains(p) && !isOutputPlace(inew, p))
						net.getFlowRelationships().add(createFlowRelationship(net, inew, p));
				}
				if (inew != i2)
					for (Iterator<FlowRelationship> iter3=i2.getOutgoingFlowRelationships().iterator(); iter3.hasNext(); ) {
						Place p = (Place)iter3.next().getTarget();
						if (!isOutputPlace(inew, p))
							net.getFlowRelationships().add(createFlowRelationship(net, inew, p));
					}
				
				// check if we created a looping tau transition...
				if (inew != i2 && inew instanceof TauTransition) {
					if (isLoopingTransition(inew)) {
						net.getFlowRelationships().removeAll(inew.getIncomingFlowRelationships());
						net.getFlowRelationships().removeAll(inew.getOutgoingFlowRelationships());
						net.getTransitions().remove(inew);
					}
				}
			}
		}

		if (succ.size() == 0)
			handleFinalMarkings(net, i);
		
		net.getFlowRelationships().removeAll(i.getIncomingFlowRelationships());
		net.getFlowRelationships().removeAll(i.getOutgoingFlowRelationships());
		net.getTransitions().remove(i);
	
//		optimizer.removeRedundantTransitions(newtransitions);
	}
	
	private void handleFinalMarkings(PetriNet net, Transition t) {
//		List<Place> removePlaces = new ArrayList<Place>();
//		for (FlowRelationship rel: t.getOutgoingFlowRelationships()) {
//			Place p = (Place)rel.getTarget();
//			if (p.getIncomingFlowRelationships().size() == 1)
//				removePlaces.add(p);
//		}
		
		List<Marking> newFinalMarkings = new ArrayList<Marking>();
//		List<Marking> removeFinalMarkings = new ArrayList<Marking>();
		for (Marking m: ((InteractionNet)net).getFinalMarkings()) {
//			boolean remove = false;
//			for (Place p: removePlaces) {
//				if (m.getNumTokens(p) > 0) {
//					removeFinalMarkings.add(m);
//					remove = true;
//					break;
//				}
//			}
//			if (!remove && couldHaveFired(m, t))
			if (couldHaveFired(m, t))
				newFinalMarkings.add(unfireTransition(m, t));
		}
//		((InteractionNet)net).getFinalMarkings().removeAll(removeFinalMarkings);
		((InteractionNet)net).getFinalMarkings().addAll(newFinalMarkings);
//		net.getPlaces().removeAll(removePlaces);
	}

	// checks whether the sequence i => i2 is possible
	//
	// assumption: net is 1-safe
	// therefore, conflict concerning input token or double creation of a token is not possible
	protected boolean isBadSequence(Transition i, Transition i2) {
		for (Iterator<FlowRelationship> it=i2.getIncomingFlowRelationships().iterator(); it.hasNext(); ) {
			Place p = (Place)it.next().getSource();
			if (isInputPlace(p, i) && !isOutputPlace(i, p))
				return true;
		}
		for (Iterator<FlowRelationship> it=i.getOutgoingFlowRelationships().iterator(); it.hasNext(); ) {
			Place p = (Place)it.next().getTarget();
			if (isOutputPlace(i2, p) && !isInputPlace(p, i2))
				return true;
		}
		return false;
	}

	protected Place createNewPlace(PetriNet net, Place p1, Place p2) {
		PTNet ptnet = (PTNet)net;
		Place pnew = ptnet.getFactory().createPlace();
		ptnet.getInitialMarking().setNumTokens(pnew, ptnet.getInitialMarking().getNumTokens(p1) + ptnet.getInitialMarking().getNumTokens(p2));
		InteractionNet inet = (InteractionNet)net;
		for (Marking m: inet.getFinalMarkings())
			m.setNumTokens(pnew, m.getNumTokens(p1) + m.getNumTokens(p2));
		return pnew;
	}

	protected Transition createNewTransition(PetriNet net, Transition t) {
		if (t instanceof InteractionTransition) {
			InteractionTransition ti = (InteractionTransition)t;
			InteractionTransition tnew = ((InteractionNet)net).getFactory().createInteractionTransition();
			tnew.setLabel(ti.getLabel());
			tnew.setSender(ti.getSender());
			tnew.setReceiver(ti.getReceiver());
			tnew.setMessageType(ti.getMessageType());
			return tnew;
		} else if (t instanceof ActionTransition) {
			ActionTransition ta = (ActionTransition)t;
			ActionTransition tnew = ((InteractionNet)net).getFactory().createActionTransition();
			tnew.setLabel(ta.getLabel());
			tnew.setRole(ta.getRole());
			return tnew;
		} else {
			TauTransition tnew = net.getFactory().createTauTransition();
			return tnew;
		}
	}

	protected boolean hasOtherInputsOrMarkedInputs(PetriNet net, Transition i2, Transition i) {
		for (FlowRelationship rel: i2.getIncomingFlowRelationships()) {
			Place p = (Place)rel.getSource();
			
			if (net.getInitialMarking().getNumTokens(p) > 0)
				return true;
			
			for (FlowRelationship rel2: p.getIncomingFlowRelationships()) {
				Transition i3 = (Transition)rel2.getSource();
				if (i3 != i)
					return true;
			}
		}
		return false;
	}

	protected FlowRelationship createFlowRelationship(PetriNet net, Node n1, Node n2) {
		FlowRelationship rel = net.getFactory().createFlowRelationship();
		rel.setSource(n1);
		rel.setTarget(n2);
		return rel;
	}

	protected boolean sharesInputPlace(Transition i) {
		for (FlowRelationship rel: i.getIncomingFlowRelationships()) {
			Place p = (Place)rel.getSource();
			// assumption: there are not two flow relationships with the same source / target
			if (p.getOutgoingFlowRelationships().size() > 1) {
				for (Iterator<FlowRelationship> it2=p.getOutgoingFlowRelationships().iterator(); it2.hasNext(); ) {
					Transition t = (Transition)it2.next().getTarget();
					if (t == i)
						continue;
//					if (!isOutputPlace(t, p))
						return true;
				}
			}
		}
		return false;
	}

	// checks whether ALL output places could also be marked by other transitions
	// or a bi-flow is connected to the output place
	protected boolean existsAlternativeEnablement(PetriNet net, Transition i) {
		boolean exists = false;
		for (FlowRelationship rel: i.getOutgoingFlowRelationships()) {
			Place p = (Place)rel.getTarget();
			
			boolean exists2 = false;
			for (FlowRelationship rel2: p.getIncomingFlowRelationships()) {
				Transition t = (Transition)rel2.getSource();
				if (t != i) {
					exists2 = true;
					if (isInputPlace(p, t))
						return true;
				}
			}
			exists &= (exists2 || net.getInitialMarking().getNumTokens(p) > 0);
		}
		return exists;
	}

}
