package de.hpi.diagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a basic diagram containing potentially typed
 * nodes and edges..
 *
 * @author Stefan Krumnow
 */
public class Diagram {
	List<DiagramEdge>edges;
	List<DiagramNode>nodes;
	protected Map<String, String> properties;
	
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
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public String getPropertyValue(String key) {
		if (properties != null) {
			return properties.get(key);
		} else {
			return null;
		}
	}
	
	public boolean hasPropertyValue(String key) {
		return (properties != null) && properties.containsKey(key);
	}

}
