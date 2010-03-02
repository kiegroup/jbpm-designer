package de.hpi.PTnet.verification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;
import de.hpi.petrinet.verification.PetriNetInterpreter;

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
public class PTNetInterpreter implements PetriNetInterpreter {

	public Marking fireTransition(PetriNet net, Marking marking, Transition t) {
		Marking newmarking = (Marking)marking.getCopy();
		for (FlowRelationship rel: t.getIncomingFlowRelationships()) {
			Place p = (Place)rel.getSource();
			newmarking.removeToken(p);
		}
		for (FlowRelationship rel: t.getOutgoingFlowRelationships()) {
			Place p = (Place)rel.getTarget();
			newmarking.addToken(p);
		}
		return newmarking;
	}

	public List<Transition> getEnabledTransitions(PetriNet net,
			Marking marking) {
		List<Transition> transitions = new ArrayList();
		for (Iterator it=net.getTransitions().iterator(); it.hasNext(); ) {
			Transition t = (Transition)it.next();
			boolean isEnabled = true;
			for (FlowRelationship rel: t.getIncomingFlowRelationships()) {
				Place p = (Place)rel.getSource();
				if (marking.getNumTokens(p) == 0) {
					isEnabled = false;
					break;
				}
			}
			if (isEnabled)
				transitions.add(t);
		}
		return transitions;
	}

	public boolean[] getEnablement(PetriNet net, Marking marking) {
		boolean[] enabled = new boolean[net.getTransitions().size()];
		int i=0;
		for (Iterator it=net.getTransitions().iterator(); it.hasNext(); i++) {
			Transition t = (Transition)it.next();
			enabled[i] = true;
			for (FlowRelationship rel: t.getIncomingFlowRelationships()) {
				Place p = (Place)rel.getSource();
				if (marking.getNumTokens(p) == 0) {
					enabled[i] = false;
					break;
				}
			}
		}
		return enabled;
	}

	public Set<Transition> getReachableTransitions(PetriNet net, de.hpi.petrinet.Marking marking) {
		return new StateSpaceCalculator(this, (PTNet)net, (Marking)marking).getReachableTransitions();
	}

	public de.hpi.petrinet.Marking fireTransition(PetriNet net,
			de.hpi.petrinet.Marking marking, Transition t) {
		return fireTransition(net, (de.hpi.PTnet.Marking)marking, t);
	}

	public List<de.hpi.petrinet.Transition> getEnabledTransitions(PetriNet net,
			de.hpi.petrinet.Marking marking) {
		return getEnabledTransitions(net, (de.hpi.PTnet.Marking)marking);
	}

	public boolean[] getEnablement(PetriNet net, de.hpi.petrinet.Marking marking) {
		return getEnablement(net, (de.hpi.PTnet.Marking)marking);
	}

}
