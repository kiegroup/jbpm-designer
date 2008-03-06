package de.hpi.PTnet.pnml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.pnml.PetriNetPNMLExporter;

public class PTNetPNMLExporter extends PetriNetPNMLExporter {

	@Override
	protected void handlePetriNetAttributes(Document doc, Element node, PetriNet net) {
		super.handlePetriNetAttributes(doc, node, net);
		node.setAttribute("type", "PT net");
	}

	@Override
	protected Element appendPlace(Document doc, Node netnode, PetriNet net, Place place) {
		Element pnode = super.appendPlace(doc, netnode, net, place);
		
		Node imode = pnode.appendChild(doc.createElement("initialMarking"));
		Node imvnode = imode.appendChild(doc.createElement("value"));
		imvnode.appendChild(doc.createTextNode(""+net.getInitialMarking().getNumTokens(place)));
		
		return pnode;
	}

}
