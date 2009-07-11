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
import de.hpi.bpt.process.epc.NonFlowObject;
import de.hpi.bpt.process.epc.ProcessInterface;
import de.hpi.petrinet.LabeledPlace;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;

public class PetriNetConverterAllLabels extends PetriNetConverter {
	
	public PetriNetConverterAllLabels(IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, de.hpi.bpt.process.epc.Node, NonFlowObject> epc, PetriNetFactory pnfactory) {
		super(epc, pnfactory);
	}
	
	@Override
	/**
	 * Creates a Petri net place representing the EPC event.
	 * 
	 * As we want to map all labels, a labeled Petri net place is used.
	 * 
	 * @param net
	 * @param event
	 * @param conversion context capturing the relation between Petri net and EPC elements
	 */
	protected void handleEvent(PetriNet net, Event event, ConversionContext c) {
		LabeledPlace p = this.pnfactory.createLabeledPlace();
		p.setId(event.getId());
		p.setLabel(event.getName());
		net.getPlaces().add(p);
		c.setAllConversionMaps(event, p);
	}

}
