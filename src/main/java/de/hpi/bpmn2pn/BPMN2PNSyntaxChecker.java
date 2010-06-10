package de.hpi.bpmn2pn;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;

public class BPMN2PNSyntaxChecker extends BPMNSyntaxChecker {

	public BPMN2PNSyntaxChecker(BPMNDiagram diagram) {
		super(diagram);
		forbiddenNodes.add("EndTerminateEvent");
		forbiddenNodes.add("IntermediateCompensationEvent");
		forbiddenNodes.add("EndCompensationEvent");
		forbiddenNodes.add("EndLinkEvent");
		forbiddenNodes.add("StartLinkEvent");
		forbiddenNodes.add("ORGateway");
		forbiddenNodes.add("ComplexGateway");
		forbiddenNodes.add("MultipleInstanceActivity");
	}
}