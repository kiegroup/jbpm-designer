package de.hpi.PTnet.pnml;

import java.util.Map;

import org.w3c.dom.Node;

import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetFactory;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.pnml.PetriNetPNMLImporter;

public class PTNetPNMLImporter extends PetriNetPNMLImporter {
	
	@Override
	protected PetriNet createPetriNet(Node nnode) {
		return PTNetFactory.eINSTANCE.createPetriNet();
	}

	@Override
	protected Place addPlace(PetriNet net, Node pnode, Map map) {
		Place p = super.addPlace(net, pnode, map);
		
		try {
			Node node = getChild(getChild(pnode, "initialMarking"), "value");
			((PTNet)net).getInitialMarking().setNumTokens(p, new Integer(getContent(node)));
		} catch (Exception e) {
		}

		return p;
	}

}
