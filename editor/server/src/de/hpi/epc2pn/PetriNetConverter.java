/**
 * Copyright (c) 2009 Matthias Weidlich
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
package de.hpi.epc2pn;

import de.hpi.bpt.process.epc.Connection;
import de.hpi.bpt.process.epc.Connector;
import de.hpi.bpt.process.epc.ControlFlow;
import de.hpi.bpt.process.epc.Event;
import de.hpi.bpt.process.epc.FlowObject;
import de.hpi.bpt.process.epc.Function;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.IFlowObject;
import de.hpi.bpt.process.epc.NonFlowObject;
import de.hpi.bpt.process.epc.ProcessInterface;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

/**
 * Converts an EPC into a Petri net. 
 * 
 * The EPC must NOT contain any OR connectors!!!
 * 
 * Main method: convert()
 * 
 * @author matthias.weidlich
 *
 */
public class PetriNetConverter {

	protected IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> epc;
	protected PetriNetFactory pnfactory;
	
	public PetriNetConverter(IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> epc, PetriNetFactory pnfactory) {
		this.epc = epc;
		this.pnfactory = pnfactory;
	}
		
	/**
	 * Creates a Petri net transition representing the EPC function.
	 * 
	 * @param net
	 * @param function
	 * @param conversion context capturing the relation between Petri net and EPC elements
	 */
	protected void handleFunction(PetriNet net, Function function, ConversionContext c) {
		LabeledTransition t = this.pnfactory.createLabeledTransition();
		t.setId(function.getId());
		t.setLabel(function.getName());
		net.getTransitions().add(t);
		c.setAllConversionMaps(function, t);
	}
	
	/**
	 * Creates a Petri net place representing the EPC event.
	 * 
	 * @param net
	 * @param event
	 * @param conversion context capturing the relation between Petri net and EPC elements
	 */
	protected void handleEvent(PetriNet net, Event event, ConversionContext c) {
		Place p = this.pnfactory.createPlace();
		p.setId(event.getId());
		net.getPlaces().add(p);
		c.setAllConversionMaps(event, p);
	}
	
	/**
	 * Handles control flow of the EPC. Additional places and transitions might
	 * be created, if the corresponding Petri net elements cannot be connected right
	 * away.
	 * 
	 * @param net
	 * @param controlflow
	 * @param conversion context capturing the relation between Petri net and EPC elements
	 */
	protected void handleControlFlow(PetriNet net, ControlFlow f, ConversionContext c) {
		Node source = c.getConversionMapOut().get(f.getSource());
		Node target = c.getConversionMapIn().get(f.getTarget());

		/*
		 * In certain cases the straight-forward mapping of a flow arc
		 * between an XOR split (a place) and an AND join (a transition)
		 * might result in non-free choice constructs, which are not semantically 
		 * correct and therefore have to be avoided.
		 */
		if (f.getSource() instanceof Connector && f.getTarget() instanceof Connector) {
			Connector src1 = (Connector) f.getSource();
			Connector tar1 = (Connector) f.getTarget();
			if (src1.isXOR() && tar1.isAND()) {
				Transition t = this.pnfactory.createSilentTransition();
				t.setId("xor/and helper " + c.getId());
				net.getTransitions().add(t);
				this.createFlowRelationship(net, source, t);
				connectTwoTransitions(net, t, target, "xor/and helper " + c.getId());
				return;
			}
		}
		
		if ((source instanceof Place && target instanceof Transition) || (source instanceof Transition && target instanceof Place)) {
			createFlowRelationship(net, source, target);
		}
		else if ((source instanceof Place && target instanceof Place)) {
			connectTwoPlaces(net, source, target, "helper transition " + c.getId() + f.toString());
		}
		else if ((source instanceof Transition && target instanceof Transition)) {
			connectTwoTransitions(net, source, target, "helper place " + c.getId());
		}
	}

	
	protected void createFlowRelationship(PetriNet net, Node src, Node tar) {
		FlowRelationship f = this.pnfactory.createFlowRelationship();
		f.setSource(src);
		f.setTarget(tar);
		net.getFlowRelationships().add(f);
	}
	
	protected void connectTwoPlaces(PetriNet net, Node in, Node out, String id) {
		Transition t = this.pnfactory.createSilentTransition();
		t.setId(id);
		net.getTransitions().add(t);
		this.createFlowRelationship(net, in, t);
		this.createFlowRelationship(net, t, out);
	}

	protected void connectTwoTransitions(PetriNet net, Node in, Node out, String id) {
		Place p = this.pnfactory.createPlace();
		p.setId(id);
		net.getPlaces().add(p);
		this.createFlowRelationship(net, in, p);
		this.createFlowRelationship(net, p, out);
	}	

	/**
	 * Handles a connector of the EPC. 
	 * 
	 * @param net
	 * @param controlflow
	 * @param conversion context capturing the relation between Petri net and EPC elements
	 */
	protected void handleConnector(PetriNet net, Connector connector, ConversionContext c) {
		if (connector.isXOR()) {
			Place p = this.pnfactory.createPlace();
			p.setId(connector.getId() + " place");
			net.getPlaces().add(p);
			c.setAllConversionMaps(connector, p);
		}
		else if (connector.isAND()) {
			Transition t = this.pnfactory.createSilentTransition();
			t.setId(connector.getId() + " transition");
			net.getTransitions().add(t);
			c.setAllConversionMaps(connector, t);
		}
	}
	
	/**
	 * Converts the EPC hold as a private member into a Petri net.
	 * 
	 * @return the Petri net that corresponds to the EPC
	 */
	public PetriNet convert() {
		if (epc == null)
			return null;
		
		PetriNet net = pnfactory.createPetriNet();
		ConversionContext c = new ConversionContext();
				
		for(IFlowObject o : this.epc.getFlowObjects()) {
			if (o instanceof Function) {
				handleFunction(net, (Function) o, c);
			} 
			else if (o instanceof Event) {
				handleEvent(net, (Event) o, c);
			} 
			else if (o instanceof Connector) {
				// OR joins cannot be mapped at all!!!
				assert(!((Connector) o).isOR());
				handleConnector(net, (Connector) o, c);
			}
		}
		
		for(ControlFlow f : this.epc.getControlFlow()) {
			handleControlFlow(net, f, c);
		}
		return net;
	}

}
