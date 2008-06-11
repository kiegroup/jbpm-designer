package de.hpi.bpmn2pn.converter;

import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.impl.PTNetFactoryImpl;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn2pn.model.ConversionContext;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;
import de.hpi.petrinet.Place;

public class StandardConverter extends Converter {

	public StandardConverter(BPMNDiagram diagram) {
		super(diagram, new PTNetFactoryImpl());
	}

	public StandardConverter(BPMNDiagram diagram, PetriNetFactory pnfactory) {
		super(diagram, pnfactory);
	}

	@Override
	protected void createStartPlaces(PetriNet net, ConversionContext c) {
		super.createStartPlaces(net, c);
		for (Container process: diagram.getProcesses()) {
			Place p = c.getSubprocessPlaces(process).startP;
			((PTNet)net).getInitialMarking().addToken(p);
		}
	}

}
