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

import de.hpi.PTnet.PTNet;
import de.hpi.bpt.process.epc.Connection;
import de.hpi.bpt.process.epc.Connector;
import de.hpi.bpt.process.epc.ControlFlow;
import de.hpi.bpt.process.epc.Event;
import de.hpi.bpt.process.epc.FlowObject;
import de.hpi.bpt.process.epc.Function;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.Node;
import de.hpi.bpt.process.epc.NonFlowObject;
import de.hpi.bpt.process.epc.ProcessInterface;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;
import de.hpi.petrinet.Place;

/**
 * Converts an EPC into a PT net. 
 * 
 * The EPC must NOT contain any OR connectors!!!
 * 
 * Main method: convert()
 * 
 * @author matthias.weidlich
 *
 */
public class PTNetConverter extends PetriNetConverter {

	public PTNetConverter(
			IEPC<ControlFlow, FlowObject, Event, Function, Connector, ProcessInterface, Connection, Node, NonFlowObject> epc,
			PetriNetFactory pnfactory) {
		super(epc, pnfactory);

	}
	
	/**
	 * Convert the EPC into a PT net. This is based on the 
	 * EPC to Petri net conversion.
	 */
	@Override
	public PTNet convert() {
		return (PTNet) super.convert();
	}
	
	/**
	 * We add the initial marking to the generated Petri net.
	 */
	@Override
	protected void handleEvent(PetriNet net, Event event, ConversionContext c) {
		super.handleEvent(net, event, c);
		if (this.epc.getPredecessors(event).isEmpty()) {
			de.hpi.petrinet.Node n = c.getConversionMapIn().get(event);
			if (n instanceof Place)
				((PTNet)net).getInitialMarking().addToken((Place)n);
		}
	}

}
