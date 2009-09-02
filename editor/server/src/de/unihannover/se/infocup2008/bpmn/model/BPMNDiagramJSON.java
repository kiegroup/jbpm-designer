package de.unihannover.se.infocup2008.bpmn.model;


public class BPMNDiagramJSON extends BPMNAbstractDiagram implements BPMNDiagram {

	@Override
	protected BPMNElementJSON newElement() {
		return new BPMNElementJSON();
	}

}
