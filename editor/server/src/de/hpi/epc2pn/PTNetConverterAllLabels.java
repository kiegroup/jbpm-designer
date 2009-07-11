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

public class PTNetConverterAllLabels extends PetriNetConverterAllLabels {

	public PTNetConverterAllLabels(
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
