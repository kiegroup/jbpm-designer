package de.hpi.execpn.pnml;

import java.util.ArrayList;
import java.util.List;

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
import de.hpi.petrinet.Transition;
import de.hpi.petrinet.pnml.PetriNetPNMLExporter;

public class ExecPNPNMLExporter extends PetriNetPNMLExporter {
	private static final String toolTitle = "Petri Net Engine";
	private static final String toolVersion = "1.0";
	private static final String caseIdName = "case_id";
	private static final String caseIdXSDType = "xsd:integer";
	private static final String caseIdXPathExpression = "/data/case_id";

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
		
		Element ts = (Element) tnode.appendChild(doc
				.createElement("toolspecific"));
		ts.setAttribute("tool", toolTitle);
		ts.setAttribute("version", toolVersion);
		if (transition instanceof FormTransition) {
			Element output = (Element) ts.appendChild(doc.createElement("output"));
			Element model = (Element) output.appendChild(doc.createElement("model"));
			model.setAttribute("href", ((FormTransition) transition).getModelURL());
		} else if (transition instanceof AutomaticTransition){
			// TODO: What about guards?
			Element fire = (Element) ts.appendChild(doc.createElement("fire"));
			fire.setAttribute("type", "automatic");
		} 
		
		if (transition instanceof LabeledTransition) {
			LabeledTransition l = (LabeledTransition)transition;
			if (l.getAction() != null) {
				Element output = (Element) ts.appendChild(doc.createElement("worklist"));
				output.setAttribute("task", l.getLabel());
				output.setAttribute("action", l.getAction());
			}
		}

		// get incoming places out of petri net model
		List<FlowRelationship> incomingArcs = transition
				.getIncomingFlowRelationships();
		List<Place> incomingPlaces = new ArrayList<Place>();
		for (FlowRelationship arc : incomingArcs) {
			if (arc.getSource() instanceof Place) {
				Place incomingPlace = (Place) arc.getSource();
				incomingPlaces.add(incomingPlace);
			}
		}

		/*
		 * Should be done by the engine by now 
		 * 
		if (incomingPlaces.size() > 1) {
			Element guard = (Element) ts.appendChild(doc.createElement("guard"));

			for (int i = 0; i < incomingPlaces.size() - 1; i++) {
				Place first = incomingPlaces.get(i);
				Place second = incomingPlaces.get(i + 1);

				addContentElement(doc, guard, "expr", 
						first.getId()
						+ "." + caseIdName
						+ " == "
						+ second.getId() + "." + caseIdName);
			}
		}
		 */
		return tnode;
	}

	@Override
	protected Element appendPlace(Document doc, Node netnode, PetriNet net,
			Place place) {
		Element pnode = super.appendPlace(doc, netnode, net, place);

		Node n1node = pnode.appendChild(doc.createElement("name"));
		addContentElement(doc, n1node, "value", place.getId());
		addContentElement(doc, n1node, "text", place.getId());
/*
 * handled by engine
 
		// standard locator for case_id
		Element ts = (Element) pnode.appendChild(doc
				.createElement("toolspecific"));
		ts.setAttribute("tool", toolTitle);
		ts.setAttribute("version", toolVersion);
		Element locator = (Element) ts
				.appendChild(doc.createElement("locator"));
		addContentElement(doc, locator, "name", caseIdName);
		addContentElement(doc, locator, "type", caseIdXSDType);
		addContentElement(doc, locator, "expr", caseIdXPathExpression);
*/
		return pnode;
	}

}
