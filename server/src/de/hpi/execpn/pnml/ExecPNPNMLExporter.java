package de.hpi.execpn.pnml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.hpi.execpn.AutomaticTransition;
import de.hpi.execpn.ExecPetriNet;
import de.hpi.execpn.FormTransition;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.TauTransition;
import de.hpi.petrinet.Transition;
import de.hpi.petrinet.pnml.PetriNetPNMLExporter;

public class ExecPNPNMLExporter extends PetriNetPNMLExporter {

	private ToolspecificPNMLHelper tsHelper;

	
	public ExecPNPNMLExporter() {
		tsHelper = new ToolspecificPNMLHelper();
	}
	
	@Override
	protected void handlePetriNetAttributes(Document doc, Element node,
			PetriNet net) {
		super.handlePetriNetAttributes(doc, node, net);
		node.setAttribute("type", "CP-net");
		Element name = (Element) node.appendChild(doc.createElement("name"));
		addContentElement(doc, name, "text", ((ExecPetriNet) net).getName());
	}

	@Override
	protected Element appendTransition(Document doc, Node netnode,
			Transition transition) {
		Element tnode = super.appendTransition(doc, netnode, transition);
		Element ts = tsHelper.addToolspecificElement(doc, tnode);

		if (transition instanceof FormTransition) {
			tsHelper.addModelReference(doc, ts, ((FormTransition) transition).getModelURL());
			tnode.setAttribute("type", "receive");
		} else /*if (transition instanceof TauTransition)*/{
			// TODO: What about guards?
			tnode.setAttribute("type", "automatic");
		}
		
		if (transition instanceof LabeledTransition) {
			LabeledTransition l = (LabeledTransition)transition;
			if (l.getAction() != null) {
				tsHelper.setTaskAndAction(doc, ts, l.getLabel(), l.getAction());
			}
		}
		
		if (transition.getGuard() != null) {
			tsHelper.setGuard(doc, ts, transition.getGuard());
		}
		
		// get incoming places out of petri net model
		List<FlowRelationship> incomingArcs = transition.getIncomingFlowRelationships();
		List<Place> incomingPlaces = new ArrayList<Place>();
		for (FlowRelationship arc : incomingArcs) {
			if (arc.getSource() instanceof Place) {
				Place incomingPlace = (Place) arc.getSource();
				incomingPlaces.add(incomingPlace);
			}
		}
		return tnode;
	}

	@Override
	protected Element appendPlace(Document doc, Node netnode, PetriNet net,
			Place place) {
		Element pnode = super.appendPlace(doc, netnode, net, place);

		Node n1node = pnode.appendChild(doc.createElement("name"));
		addContentElement(doc, n1node, "value", place.getId());
		addContentElement(doc, n1node, "text", place.getId());
		
		Element ts = tsHelper.addToolspecificElement(doc, pnode);

		for (Locator loc : place.getLocators()) {
			tsHelper.addLocator(doc, ts, loc);			
		}
		
		return pnode;
	}

}
