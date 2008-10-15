package de.hpi.petrinet.verification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.SilentTransition;
import de.hpi.petrinet.Transition;

public abstract class CopyOfPetriNetReducer {
	
	private int freshNameCounter = 1;
	
	public void removeTransitions(PetriNet net, List<Transition> removeList) {
		// TODO remove
//		logIt(net, null);

		Object[] rl = removeList.toArray();
		boolean cancel;
		do {
			cancel = true;
			for (int i=0; i<rl.length; i++)
				if (rl[i] != null && removeTransition(net, (Transition)rl[i], true)) {
					rl[i] = null;
					cancel = false;
				}
		} while (!cancel);
		for (int i=0; i<rl.length; i++)
			if (rl[i] != null)
				removeTransition(net, (Transition)rl[i], false);
		
//		doRemoveTransitions(net, removeList, false);
//		removeList = doRemoveTransitions(net, removeList, true);
//		doRemoveTransitions(net, removeList, false);
	}

	protected void logIt(PetriNet net, Transition i) {
	}

	protected boolean removeTransition(PetriNet net, Transition i, boolean onlyRemoveEasyOnes) {
		if (isLoopingTransition(i)) {
			applyRule1(net, i);
		} else if ((!sharesInputPlace(i) || !existsAlternativeEnablement(net, i)) && wouldRespect1Safeness(net, i)) {
			applyRule2(net, i);
		} else if (!onlyRemoveEasyOnes) {
			applyRule3(net, i);
		} else {
			return false;
		}

		logIt(net, i);
		
		return true;
	}

	// simply deletes a transition
	protected void applyRule1(PetriNet net, Transition i) {
		net.getFlowRelationships().removeAll(i.getIncomingFlowRelationships());
		net.getFlowRelationships().removeAll(i.getOutgoingFlowRelationships());
		net.getTransitions().remove(i);
	}

	// merges places
	protected void applyRule2(PetriNet net, Transition i) {
		for (Iterator<? extends FlowRelationship> p1iter=i.getIncomingFlowRelationships().iterator(); p1iter.hasNext(); ) {
			FlowRelationship f1 = p1iter.next();
			Place p1 = (Place)f1.getSource();
			
			for (Iterator<? extends FlowRelationship> p2iter=i.getOutgoingFlowRelationships().iterator(); p2iter.hasNext(); ) {
				FlowRelationship f2 = p2iter.next();
				Place p2 = (Place)f2.getTarget();

				// for each pair p1 / p2 we create a new place
				Place pnew = createNewPlace(net, p1, p2);
				net.getPlaces().add(pnew);
				pnew.setId("Px"+(freshNameCounter++));

				// add the flows
				for (Iterator<? extends FlowRelationship> iter2=p1.getIncomingFlowRelationships().iterator(); iter2.hasNext(); ) {
					Transition i2 = (Transition)iter2.next().getSource();
					if (i2 != i) {
						FlowRelationship f = net.getFactory().createFlowRelationship();
						net.getFlowRelationships().add(f);
						f.setSource(i2);
						f.setTarget(pnew);
					}
				}
				for (Iterator<? extends FlowRelationship> iter2=p2.getIncomingFlowRelationships().iterator(); iter2.hasNext(); ) {
					Transition i2 = (Transition)iter2.next().getSource();
					if (i2 != i) {
						FlowRelationship f = net.getFactory().createFlowRelationship();
						net.getFlowRelationships().add(f);
						f.setSource(i2);
						f.setTarget(pnew);
					}
				}
				for (Iterator<? extends FlowRelationship> iter2=p1.getOutgoingFlowRelationships().iterator(); iter2.hasNext(); ) {
					Transition i2 = (Transition)iter2.next().getTarget();
					if (i2 != i) {
						FlowRelationship f = net.getFactory().createFlowRelationship();
						net.getFlowRelationships().add(f);
						f.setSource(pnew);
						f.setTarget(i2);
					}
				}
				for (Iterator<? extends FlowRelationship> iter2=p2.getOutgoingFlowRelationships().iterator(); iter2.hasNext(); ) {
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
		for (Iterator<? extends FlowRelationship> p1iter=i.getIncomingFlowRelationships().iterator(); p1iter.hasNext(); )
			rp.add((Place)p1iter.next().getSource());
		for (Iterator<? extends FlowRelationship> p2iter=i.getOutgoingFlowRelationships().iterator(); p2iter.hasNext(); )
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
		for (Iterator<? extends FlowRelationship> iter2=i.getOutgoingFlowRelationships().iterator(); iter2.hasNext(); ) {
			Place p = (Place)iter2.next().getTarget();
			out.add(p);
//			if (isInputPlace(p, i))
//				continue;
			for (Iterator<? extends FlowRelationship> iter3=p.getOutgoingFlowRelationships().iterator(); iter3.hasNext(); ) {
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
				for (Iterator<? extends FlowRelationship> iter3=i2.getIncomingFlowRelationships().iterator(); iter3.hasNext(); ) {
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
					for (Iterator<? extends FlowRelationship> p1iter=i2.getIncomingFlowRelationships().iterator(); p1iter.hasNext(); ) {
						Place p1 = (Place)p1iter.next().getSource();
						net.getFlowRelationships().removeAll(p1.getIncomingFlowRelationships());
						net.getPlaces().remove(p1);
					}
					net.getFlowRelationships().removeAll(i2.getIncomingFlowRelationships());
				}
	
				for (Iterator<? extends FlowRelationship> iter3=i.getIncomingFlowRelationships().iterator(); iter3.hasNext(); ) {
					Place p = (Place)iter3.next().getSource();
					if (!isInputPlace(p, inew))
						net.getFlowRelationships().add(createFlowRelationship(net, p, inew));
				}
				if (inew != i2)
					for (Iterator<? extends FlowRelationship> iter3=i2.getIncomingFlowRelationships().iterator(); iter3.hasNext(); ) {
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
					for (Iterator<? extends FlowRelationship> iter3=i2.getOutgoingFlowRelationships().iterator(); iter3.hasNext(); ) {
						Place p = (Place)iter3.next().getTarget();
						if (!isOutputPlace(inew, p))
							net.getFlowRelationships().add(createFlowRelationship(net, inew, p));
					}
				
				// check if we created a looping tau transition...
				if (inew != i2 && inew instanceof SilentTransition) {
					if (isLoopingTransition(inew) || isRedundant(inew)) {
						net.getFlowRelationships().removeAll(inew.getIncomingFlowRelationships());
						net.getFlowRelationships().removeAll(inew.getOutgoingFlowRelationships());
						net.getTransitions().remove(inew);
					}
				}
			}
		}

		net.getFlowRelationships().removeAll(i.getIncomingFlowRelationships());
		net.getFlowRelationships().removeAll(i.getOutgoingFlowRelationships());
		net.getTransitions().remove(i);
		
//		optimizer.removeRedundantTransitions(newtransitions);
	}
	
	protected boolean isLoopingTransition(Transition t) {
		if (t.getIncomingFlowRelationships().size() != t.getOutgoingFlowRelationships().size())
			return false;
		for (Iterator<? extends FlowRelationship> it=t.getIncomingFlowRelationships().iterator(); it.hasNext(); ) {
			Place p = (Place)it.next().getSource();
			if (!isOutputPlace(t, p))
				return false;
		}
		for (Iterator<? extends FlowRelationship> it=t.getOutgoingFlowRelationships().iterator(); it.hasNext(); ) {
			Place p = (Place)it.next().getTarget();
			if (!isInputPlace(p, t))
				return false;
		}
		return true;
	}

	protected boolean isInputPlace(Place p, Transition i) {
		for (Iterator<? extends FlowRelationship> it=i.getIncomingFlowRelationships().iterator(); it.hasNext(); ) {
			if (it.next().getSource() == p)
				return true;
		}
		return false;
	}

	protected boolean isOutputPlace(Transition i, Place p) {
		for (Iterator<? extends FlowRelationship> it=i.getOutgoingFlowRelationships().iterator(); it.hasNext(); ) {
			if (it.next().getTarget() == p)
				return true;
		}
		return false;
	}

	protected abstract Place createNewPlace(PetriNet net, Place p1, Place p2);
	
	protected Transition createNewTransition(PetriNet net, Transition t) {
		if (t instanceof LabeledTransition) {
			LabeledTransition tnew = net.getFactory().createLabeledTransition();
			tnew.setLabel(((LabeledTransition)t).getLabel());
			return tnew;
		} else {
			SilentTransition tnew = net.getFactory().createSilentTransition();
			return tnew;
		}
	}

	

	protected boolean hasOtherInputsOrMarkedInputs(PetriNet net, Transition i2, Transition i) {
		for (Iterator<? extends FlowRelationship> iter=i2.getIncomingFlowRelationships().iterator(); iter.hasNext(); ) {
			Place p = (Place)iter.next().getSource();
			
			if (net.getInitialMarking().getNumTokens(p) > 0)
				return true;
			
			for (Iterator<? extends FlowRelationship> iter2=p.getIncomingFlowRelationships().iterator(); iter2.hasNext(); ) {
				Transition i3 = (Transition)iter2.next().getSource();
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
		for (Iterator<? extends FlowRelationship> iter=i.getIncomingFlowRelationships().iterator(); iter.hasNext(); ) {
			FlowRelationship f = iter.next();
			Place p = (Place)f.getSource();
			// assumption: there are not two flow relationships with the same source / target
			if (p.getOutgoingFlowRelationships().size() > 1) {
				for (Iterator<? extends FlowRelationship> it2=p.getOutgoingFlowRelationships().iterator(); it2.hasNext(); ) {
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
		for (Iterator<? extends FlowRelationship> it=i.getOutgoingFlowRelationships().iterator(); it.hasNext(); ) {
			Place p = (Place)it.next().getTarget();
			
			boolean exists2 = false;
			for (Iterator<? extends FlowRelationship> it2=p.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
				Transition t = (Transition)it2.next().getSource();
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

	// checks whether there is a transition which has input (or output) places among both input and output places of i
	// exists a transition t != i, p (t F p and p F i and (...))
	protected boolean wouldRespect1Safeness(PetriNet net, Transition i) {
		List<Transition> in_t = new ArrayList<Transition>();
		List<Transition> out_t = new ArrayList<Transition>();
		for (Iterator<? extends FlowRelationship> it=i.getIncomingFlowRelationships().iterator(); it.hasNext(); ) {
			Place p = (Place)it.next().getSource();
			for (Iterator<? extends FlowRelationship> it2 = p.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
				Transition t = (Transition)it2.next().getSource();
				if (t != i)
					in_t.add(t);
			}
			for (Iterator<? extends FlowRelationship> it2 = p.getOutgoingFlowRelationships().iterator(); it2.hasNext(); ) {
				Transition t = (Transition)it2.next().getTarget();
				if (t != i)
					out_t.add(t);
			}
		}

		for (Iterator<? extends FlowRelationship> it=i.getOutgoingFlowRelationships().iterator(); it.hasNext(); ) {
			Place p = (Place)it.next().getTarget();
			for (Iterator<? extends FlowRelationship> it2 = p.getIncomingFlowRelationships().iterator(); it2.hasNext(); ) {
				Transition t = (Transition)it2.next().getSource();
				if (in_t.contains(t))
					return false;
			}
			for (Iterator<? extends FlowRelationship> it2 = p.getOutgoingFlowRelationships().iterator(); it2.hasNext(); ) {
				Transition t = (Transition)it2.next().getTarget();
				if (out_t.contains(t))
					return false;
			}
		}
		
		return true;
	}

	// checks whether the sequence i => i2 is possible
	//
	// assumption: net is 1-safe
	// therefore, conflict concerning input token or double creation of a token is not possible
	protected boolean isBadSequence(Transition i, Transition i2) {
		for (Iterator<? extends FlowRelationship> it=i2.getIncomingFlowRelationships().iterator(); it.hasNext(); ) {
			Place p = (Place)it.next().getSource();
			if (isInputPlace(p, i) && !isOutputPlace(i, p))
				return true;
		}
		for (Iterator<? extends FlowRelationship> it=i.getOutgoingFlowRelationships().iterator(); it.hasNext(); ) {
			Place p = (Place)it.next().getTarget();
			if (isOutputPlace(i2, p) && !isInputPlace(p, i2))
				return true;
		}
		return false;
	}

	// checks if a tau-transition is an exact copy of an already existing transition
	protected boolean isRedundant(Transition i) {
		List<Place> in = new ArrayList<Place>(i.getIncomingFlowRelationships().size());
		for (Iterator<? extends FlowRelationship> it=i.getIncomingFlowRelationships().iterator(); it.hasNext(); )
			in.add((Place)it.next().getSource());
		List<Place> out = new ArrayList<Place>(i.getIncomingFlowRelationships().size());
		for (Iterator<? extends FlowRelationship> it=i.getOutgoingFlowRelationships().iterator(); it.hasNext(); )
			out.add((Place)it.next().getTarget());

		if (in.size() > 0)
			for (Iterator<? extends FlowRelationship> it=in.get(0).getOutgoingFlowRelationships().iterator(); it.hasNext(); ) {
				Transition t = (Transition)it.next().getTarget();
				if (t != i && t instanceof SilentTransition && hasSameInputAndOutputPlaces(t, in, out))
					return true;
			}
		else if (out.size() > 0)
			for (Iterator<? extends FlowRelationship> it=out.get(0).getIncomingFlowRelationships().iterator(); it.hasNext(); ) {
				Transition t = (Transition)it.next().getSource();
				if (t != i && t instanceof SilentTransition && hasSameInputAndOutputPlaces(t, in, out))
					return true;
			}
		
		return false;
	}

	protected boolean hasSameInputAndOutputPlaces(Transition t, List<Place> in, List<Place> out) {
		if (t.getIncomingFlowRelationships().size() != in.size() || t.getOutgoingFlowRelationships().size() != out.size())
			return false;
		
		for (Iterator<? extends FlowRelationship> it=t.getIncomingFlowRelationships().iterator(); it.hasNext(); )
			if (!in.contains((Place)it.next().getSource()))
				return false;
		for (Iterator<? extends FlowRelationship> it=t.getOutgoingFlowRelationships().iterator(); it.hasNext(); )
			if (!out.contains((Place)it.next().getTarget()))
				return false;
		
		return true;
	}

}
