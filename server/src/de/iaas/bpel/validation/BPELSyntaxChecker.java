package de.iaas.bpel.validation;

import java.util.HashMap;
import java.util.Map;



import de.hpi.petrinet.verification.SyntaxChecker;
import de.iaas.bpel.models.BPELDiagram;
import de.iaas.bpel.models.Container;
import de.iaas.bpel.models.DiagramObject;
import de.iaas.bpel.models.Edge;
import de.iaas.bpel.models.Node;
import de.iaas.bpel.models.Process;

public class BPELSyntaxChecker implements SyntaxChecker {
	
	private static final String NO_SOURCE = "An edge must have a source.";
	private static final String NO_TARGET = "An edge must have a target.";
	private static final String NONE_PROCESS = "No Process exists";


	protected BPELDiagram diagram;
	protected Process process;
	protected Map<String,String> errors;
	
	public BPELSyntaxChecker(BPELDiagram diagram) {
		this.diagram = diagram;
		this.errors = new HashMap<String,String>();
	}

	public boolean checkSyntax() {
		errors.clear();
		if (diagram == null)
			return false;
		
		if (diagram.getChildNodes() == null) {
			addError(process, NONE_PROCESS);
		} else {		
			process = (Process) diagram.getChildNodes().get(0);
		    checkEdges();
		    checkNodesRecursively(diagram);
		}
		
		return errors.size() == 0;
	}
	
	public Map<String,String> getErrors() {
		return errors;
	}

	protected void checkEdges() {
		for (Edge edge: diagram.getEdges()) {
			if (edge.getSource() == null)
				addError(edge, NO_SOURCE);
			else if (edge.getTarget() == null) 
				addError(edge, NO_TARGET);
		}
	}

	protected boolean checkNodesRecursively(Container container) {
		for (Node node: container.getChildNodes()) {
			
			checkNode(node);

			if (node instanceof Container)
				checkNodesRecursively((Container)node);

		}
		return (errors.size() == 0);
	}

	protected boolean checkNode(Node node) {
		if (node.getParent() == null) 
			return false;
		
		return true;
	}
	

	protected void addError(DiagramObject obj, String errorCode) {
		errors.put(obj.getResourceId(), errorCode);
	}

}
