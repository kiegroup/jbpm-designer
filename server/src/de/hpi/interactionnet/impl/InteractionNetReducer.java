package de.hpi.interactionnet.impl;

import org.w3c.dom.Document;

import de.hpi.PTnet.Marking;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.impl.BoundednessChecker;
import de.hpi.PTnet.impl.PTNetInterpreterImpl;
import de.hpi.PTnet.impl.PTNetReducer;
import de.hpi.PTnet.impl.StateSpaceCalculator;
import de.hpi.interactionnet.ActionTransition;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.interactionnet.pnml.InteractionNetPNMLExporter;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Transition;
import de.hpi.petrinet.pnml.XMLFileLoaderSaver;

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
			tnew.setRole(ta.getRole());
			return tnew;
		} else {
			return super.createNewTransition(net, t);
		}
	}

	private static int fileCounter = 3000;
	
	@Override
	protected void logIt(PetriNet net, Transition i) {
		try {
			XMLFileLoaderSaver saver = new XMLFileLoaderSaver();
			Document doc = saver.createNewDocument();
			new InteractionNetPNMLExporter().savePetriNet(doc, net);
			boolean isBounded = new BoundednessChecker(new PTNetInterpreterImpl(), (PTNet)net).checkBoundedness();
			int numUnreachable = 0;
			if (isBounded)
				numUnreachable = net.getTransitions().size() - new StateSpaceCalculator(new PTNetInterpreterImpl(), (PTNet)net, (Marking)net.getInitialMarking()).getReachableTransitions().size();
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
