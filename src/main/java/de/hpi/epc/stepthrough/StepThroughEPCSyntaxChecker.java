package de.hpi.epc.stepthrough;

import de.hpi.diagram.Diagram;
import de.hpi.epc.validation.EPCSyntaxChecker;

public class StepThroughEPCSyntaxChecker extends EPCSyntaxChecker {

	public StepThroughEPCSyntaxChecker(Diagram diagram) {
		super(diagram);
		checkFunctionFollowsFunction = false;
		checkExactlyOneStartEvent = false;
	}

}