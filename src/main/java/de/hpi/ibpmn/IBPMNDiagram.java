package de.hpi.ibpmn;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;
import de.hpi.ibpmn.validation.IBPMNSyntaxChecker;

public class IBPMNDiagram extends BPMNDiagram {

	@Override
	public BPMNSyntaxChecker getSyntaxChecker() {
		return new IBPMNSyntaxChecker(this);
	}

}
