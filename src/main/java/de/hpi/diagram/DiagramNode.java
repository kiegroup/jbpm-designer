package de.hpi.diagram;

import java.util.ArrayList;
import java.util.List;

public class DiagramNode extends DiagramObject {
	
	protected List<DiagramEdge> outgoingEdges;
	protected List<DiagramEdge> incomingEdges;
	
	public List<DiagramEdge> getIncomingEdges() {
		if (incomingEdges == null)
			incomingEdges = new ArrayList<DiagramEdge>();
		return incomingEdges;
	}

	public List<DiagramEdge> getOutgoingEdges() {
		if (outgoingEdges == null)
			outgoingEdges = new ArrayList<DiagramEdge>();
		return outgoingEdges;
	}

}
