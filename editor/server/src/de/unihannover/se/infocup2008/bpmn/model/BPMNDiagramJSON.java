package de.unihannover.se.infocup2008.bpmn.model;

public class BPMNDiagramJSON extends BPMNAbstractDiagram<BPMNElementJSON> {

	@Override
	protected BPMNElementJSON newElement() {
		return new BPMNElementJSON();
	}

}
