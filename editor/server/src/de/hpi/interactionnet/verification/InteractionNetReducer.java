package de.hpi.interactionnet.verification;

import org.w3c.dom.Document;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.verification.BoundednessChecker;
import de.hpi.PTnet.verification.PTNetInterpreter;
import de.hpi.PTnet.verification.PTNetReducer;
import de.hpi.PTnet.verification.StateSpaceCalculator;
import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.interactionnet.Role;
import de.hpi.interactionnet.serialization.InteractionNetPNMLExporter;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;
import de.hpi.petrinet.serialization.XMLFileLoaderSaver;

public class InteractionNetReducer extends PTNetReducer {

	protected Transition createNewTransition(PetriNet net, Transition t) {
		if (t instanceof InteractionTransition) {
			InteractionTransition ti = (InteractionTransition)t;
			InteractionTransition tnew = ((InteractionNet)net).getFactory().createInteractionTransition();
			tnew.setLabel(ti.getLabel());
			tnew.setSender(ti.getSender());
			tnew.setReceiver(ti.getReceiver());
			tnew.setMessageType(ti.getMessageType());
			return tnew;
		} else if (t instanceof ActionTransition) {
			ActionTransition ta = (ActionTransition)t;
			ActionTransition tnew = ((InteractionNet)net).getFactory().createActionTransition();
			tnew.setLabel(ta.getLabel());
			for (Role r: ta.getRoles())
				tnew.getRoles().add(r);
			return tnew;
		} else {
			return super.createNewTransition(net, t);
		}
	}

	@Override
	protected Place createNewPlace(PetriNet net, Place p1, Place p2) {
		Place pnew = super.createNewPlace(net, p1, p2);
		InteractionNet inet = (InteractionNet)net;
		for (Marking m: inet.getFinalMarkings())
			m.setNumTokens(pnew, m.getNumTokens(p1) + m.getNumTokens(p2));
		return pnew;
	}

	private static int fileCounter = 3000;
	
	@Override
	protected void logIt(PetriNet net, Transition i) {
		try {
			XMLFileLoaderSaver saver = new XMLFileLoaderSaver();
			Document doc = saver.createNewDocument();
			new InteractionNetPNMLExporter().savePetriNet(doc, net);
			boolean isBounded = new BoundednessChecker(new PTNetInterpreter(), (PTNet)net).checkBoundedness();
			int numUnreachable = 0;
			if (isBounded)
				numUnreachable = net.getTransitions().size() - new StateSpaceCalculator(new PTNetInterpreter(), (PTNet)net, (Marking)net.getInitialMarking()).getReachableTransitions().size();
			saver.saveDocumentToFile(doc, "test/de/hpi/interactionnet/test/testlog/log_"+(fileCounter++)+
					"_"+i+
					"_#unreachable="+numUnreachable+
					"_bounded="+isBounded+
					"_#t="+net.getTransitions().size()+
					"_#p="+net.getPlaces().size()+
					".pnml");
		} catch (Exception e) { 
		}
	}

}
