package de.hpi.petrinet.stepthrough;

import java.util.HashMap;
import java.util.List;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;
import de.hpi.bpmn.ComplexGateway;
import de.hpi.bpmn.ORGateway;

public class STSyntaxChecker extends BPMNSyntaxChecker {
	
	private static final String COMP_GATEWAY = "The Complex Gateway is not supported.";
	private static final String OR_GATEWAY = "The OR Gateway is not supported.";

	public STSyntaxChecker(BPMNDiagram diagram) {
		super(diagram);
	}
	
	protected boolean checkSTCompatibilityRecursively(Container container) {
		boolean incompatibilityFound = false;
		
		List<Node> nodes = container.getChildNodes();
		for(int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			
			// Complex Gateway and OR Gateway are not supported
			if(node instanceof ComplexGateway) {
				addError(node, COMP_GATEWAY);
				incompatibilityFound = true;
			}
			if(node instanceof ORGateway) {
				addError(node, OR_GATEWAY);
				incompatibilityFound = true;
			}
			
			// Check nodes inside the container
			if (node instanceof Container) {
				incompatibilityFound = incompatibilityFound || checkSTCompatibilityRecursively((Container)node);
			}
		}
		
		return !incompatibilityFound;
	}
	
	public boolean checkSTCompatibility() {
		return this.checkSTCompatibilityRecursively(diagram);
	}

	public boolean checkDiagram() {
		boolean isErrorFree;
		
		isErrorFree = this.checkSyntax();
		isErrorFree = isErrorFree && this.checkSTCompatibility();
		
		return isErrorFree;
	}
}
