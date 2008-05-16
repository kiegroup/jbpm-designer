package de.hpi.execpn.pnml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.hpi.execpn.AutomaticTransition;
import de.hpi.execpn.ExecFlowRelationship;
import de.hpi.execpn.ExecPetriNet;
import de.hpi.execpn.FormTransition;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.ExecPlace;
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
		if (!tsHelper.hasChildWithName(tnode, "name")){
			Node n1node = tnode.appendChild(doc.createElement("name"));
			addContentElement(doc, n1node, "value", transition.getId());
			addContentElement(doc, n1node, "text", transition.getId());
		}
		
		if (transition instanceof FormTransition) {
			FormTransition formT = (FormTransition) transition;
			tsHelper.addModelReference(doc, ts, formT.getModelURL());
			tnode.setAttribute("type", "receive");
			tsHelper.addFormAndBindings(doc, ts, formT.getFormURL(), formT.getBindingsURL());
		} else /*if (transition instanceof AutomaticTransition)*/{
			tnode.setAttribute("type", "automatic");
		}
		
		
		// TODO possibly refactor action into TransitionImpl
		if (transition instanceof LabeledTransition) {
			LabeledTransition lTrans = (LabeledTransition) transition;
			if (lTrans.getAction() != null && !lTrans.getAction().equals("")) {
				tsHelper.setTaskAndAction(doc, ts, lTrans.getLabel(), lTrans.getAction());
			}
		}
		if (transition instanceof AutomaticTransition) {
			AutomaticTransition lTrans = (AutomaticTransition) transition;
			if (lTrans.getAction() != null && !lTrans.getAction().equals("")) {
				tsHelper.setTaskAndAction(doc, ts, lTrans.getLabel(), lTrans.getAction());
			}
		}
		
		if (transition instanceof AutomaticTransition) {
			AutomaticTransition auto = (AutomaticTransition) transition;
			tsHelper.setFireTypeManual(doc, ts, auto.isManuallyTriggered());
			if (auto.getXsltURL() != null) {
				tsHelper.setFireXsltURL(doc, ts, auto.getXsltURL());
			}
			if (auto.isManuallyTriggered()){
				tnode.setAttribute("type", "receive");
			}else{
				tnode.setAttribute("type", "automatic");
			}
		}
		
		if (transition.getGuard() != null) {
			tsHelper.setGuard(doc, ts, transition.getGuard());
		}
		
		if (transition.getRolename() != null) {
			tsHelper.setRolename(doc, ts, transition.getRolename());
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
		
		// set type of Place and add data model to DataPlace
		if (place instanceof ExecPlace) {
			ExecPlace execplace = ((ExecPlace)place);
			pnode.setAttribute("type", execplace.getType().toString());
			if (execplace.getType() == ExecPlace.Type.data)
				addContentElement(doc, pnode, "model", execplace.getModel());
		}
		else
			pnode.setAttribute("type", ExecPlace.Type.flow.toString());
		

		Node n1node = pnode.appendChild(doc.createElement("name"));
		addContentElement(doc, n1node, "value", place.getId());
		addContentElement(doc, n1node, "text", place.getId());
		
		Element ts = tsHelper.addToolspecificElement(doc, pnode);

		for (Locator loc : place.getLocators()) {
			tsHelper.addLocator(doc, ts, loc);			
		}
		
		return pnode;
	}
	
	@Override
	protected Element appendFlowRelationship(Document doc, Node netnode, FlowRelationship rel) {
		Element fnode = super.appendFlowRelationship(doc, netnode, rel);
		if (rel instanceof ExecFlowRelationship){
			Element ts = tsHelper.addToolspecificElement(doc, fnode);
			tsHelper.setArcTransformationURL(doc, ts,((ExecFlowRelationship)rel).getTransformationURL());
		}
		return fnode;
	}

}
