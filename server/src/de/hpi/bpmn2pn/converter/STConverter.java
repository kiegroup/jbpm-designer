package de.hpi.bpmn2pn.converter;

import de.hpi.PTnet.PTNet;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn2pn.model.ConversionContext;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.TauTransition;
import de.hpi.petrinet.stepthrough.AutoSwitchLevel;
import de.hpi.petrinet.stepthrough.STLabeledTransitionImpl;
import de.hpi.petrinet.stepthrough.STPetriNetFactoryImpl;
import de.hpi.petrinet.stepthrough.STTauTransitionImpl;

public class STConverter extends Converter {
	public STConverter(BPMNDiagram diagram) {
		super(diagram, new STPetriNetFactoryImpl());
	}
	
	@Override
	protected void createStartPlaces(PetriNet net, ConversionContext c) {
		super.createStartPlaces(net, c);
		for (Container process: diagram.getProcesses()) {
			Place p = c.getSubprocessPlaces(process).startP;
			((PTNet)net).getInitialMarking().addToken(p);
		}
	}
	
	@Override
	protected TauTransition addTauTransition(PetriNet net, String id, DiagramObject BPMNObj, int autoLevel) {
		STTauTransitionImpl t = (STTauTransitionImpl) addSimpleTauTransition(net, id);
		t.setBPMNObj(BPMNObj);
		t.setAutoSwitchLevel(intLevelToAutoSwitchLevel(autoLevel));
		return t;
	}
	
	@Override
	protected LabeledTransition addLabeledTransition(PetriNet net, String id, DiagramObject BPMNObj, int autoLevel, String label) {
		STLabeledTransitionImpl t = (STLabeledTransitionImpl) addSimpleLabeledTransition(net, id, label);
		t.setBPMNObj(BPMNObj);
		t.setAutoSwitchLevel(intLevelToAutoSwitchLevel(autoLevel));
		return t;
	}
	
	private AutoSwitchLevel intLevelToAutoSwitchLevel(int autoLevel) {
		if(autoLevel >= 2) return AutoSwitchLevel.FullAuto;
		else if(autoLevel == 1) return AutoSwitchLevel.SemiAuto;
		else return AutoSwitchLevel.NoAuto;
	}
}
