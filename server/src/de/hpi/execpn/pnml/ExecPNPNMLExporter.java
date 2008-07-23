package de.hpi.execpn.pnml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.hpi.execpn.AutomaticTransition;
import de.hpi.execpn.ExecFlowRelationship;
import de.hpi.execpn.ExecLabeledTransition;
import de.hpi.execpn.ExecPetriNet;
import de.hpi.execpn.ExecPlace;
import de.hpi.execpn.ExecTransition;
import de.hpi.execpn.FormTransition;
import de.hpi.execpn.TransformationTransition;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;
import de.hpi.petrinet.serialization.PetriNetPNMLExporter;

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
			tsHelper.addFormAndBindings(doc, ts, formT.getFormURL(), formT.getBindingsURL());
		}
		
		if (transition instanceof TransformationTransition) {
			TransformationTransition lTrans = (TransformationTransition) transition;
			if (lTrans.getXsltURL() != null) {
				tsHelper.setFireXsltURL(doc, ts, lTrans.getXsltURL());
			}

			if (lTrans.getAction() != null && !lTrans.getAction().equals("")) {
				tsHelper.setTaskAndAction(doc, ts, lTrans.getTask(), lTrans.getAction());
			}
		}
		if (transition instanceof AutomaticTransition) {
			AutomaticTransition aTrans = (AutomaticTransition) transition;
			if (aTrans.getXsltURL() != null) {
				tsHelper.setFireXsltURL(doc, ts, aTrans.getXsltURL());
			}
		}
		if (transition instanceof LabeledTransition) {
			ExecLabeledTransition lTrans = (ExecLabeledTransition) transition;
			if (lTrans.getAction() != null && !lTrans.getAction().equals("")) {
				tsHelper.setTaskAndAction(doc, ts, lTrans.getTask(), lTrans.getAction());
			}
			tnode.setAttribute("type", "receive");
		} else {
			tnode.setAttribute("type", "automatic");
		}
		if (((ExecTransition)transition).getGuard() != null) {
			tsHelper.setGuard(doc, ts, ((ExecTransition)transition).getGuard());
		}
		
		if (((ExecTransition)transition).getRolename() != null) {
			tsHelper.setRolename(doc, ts, ((ExecTransition)transition).getRolename());
		}
		
		if (((ExecTransition)transition).getContextPlaceID() != null) {
			tsHelper.setContextPlaceID(doc, ts, ((ExecTransition)transition).getContextPlaceID());
		}
		
		// get incoming places out of petri net model
		List<? extends FlowRelationship> incomingArcs = transition.getIncomingFlowRelationships();
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
		Element ts = tsHelper.addToolspecificElement(doc, pnode);
		
		// set type of Place and add data model to DataPlace
		if (place instanceof ExecPlace) {
			ExecPlace execplace = ((ExecPlace)place);
			pnode.setAttribute("type", execplace.getType().toString());
			if (execplace.getType() == ExecPlace.Type.data) {
				addContentElement(doc, pnode, "model", execplace.getModel());
				// add Data Object name
				Element namenode = doc.createElement("name");
				namenode.setTextContent(execplace.getName());
				ts.appendChild(namenode);
			}
		}
		else
			pnode.setAttribute("type", ExecPlace.Type.flow.toString());
		

		Node n1node = pnode.appendChild(doc.createElement("name"));
		addContentElement(doc, n1node, "value", place.getId());
		addContentElement(doc, n1node, "text", place.getId());
		

		for (Locator loc : ((ExecPlace)place).getLocators()) {
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
			if (((ExecFlowRelationship)rel).getMode()==ExecFlowRelationship.RELATION_MODE_READTOKEN){
				fnode.setAttribute("type", "read");
			}
		}
		return fnode;
	}

}
