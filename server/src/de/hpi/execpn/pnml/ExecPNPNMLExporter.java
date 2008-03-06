package de.hpi.execpn.pnml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.hpi.execpn.ExecPetriNet;
import de.hpi.execpn.FormTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Transition;
import de.hpi.petrinet.pnml.PetriNetPNMLExporter;

public class ExecPNPNMLExporter extends PetriNetPNMLExporter {

	@Override
	protected void handlePetriNetAttributes(Document doc, Element node, PetriNet net) {
		super.handlePetriNetAttributes(doc, node, net);
		node.setAttribute("type", "CP-net");
		Element name = (Element)node.appendChild(doc.createElement("name"));
		addContentElement(doc, name, "text", ((ExecPetriNet)net).getName());
	}

	@Override
	protected Element appendTransition(Document doc, Node netnode, Transition transition) {
		Element tnode = super.appendTransition(doc, netnode, transition);
		Element ts = (Element)tnode.appendChild(doc.createElement("toolspecific"));
		ts.setAttribute("tool", "Petri Net Engine");
		ts.setAttribute("version", "1.0");
		if (transition instanceof FormTransition) {
			Element output = (Element)ts.appendChild(doc.createElement("output"));
			Element model = (Element)output.appendChild(doc.createElement("model"));
			model.setAttribute("href", ((FormTransition)transition).getModelURL());
		}
		return tnode;
	}

}
