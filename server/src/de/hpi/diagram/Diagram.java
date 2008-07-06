package de.hpi.diagram;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a basic diagram containing potentially typed
 * nodes and edges..
 *
 * @author Stefan Krumnow
 */
public class Diagram {
	List<DiagramEdge>edges;
	List<DiagramNode>nodes;
	
	public List<DiagramEdge> getEdges() {
		if (this.edges == null)
			this.edges = new ArrayList<DiagramEdge>();
		return this.edges;
	}

	public List<DiagramNode> getNodes() {
		if (this.nodes == null)
			this.nodes = new ArrayList<DiagramNode>();
		return this.nodes;
	}

}
