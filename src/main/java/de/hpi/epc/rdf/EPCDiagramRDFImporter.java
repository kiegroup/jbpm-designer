package de.hpi.epc.rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.hpi.diagram.Diagram;
import de.hpi.diagram.DiagramEdge;
import de.hpi.diagram.DiagramNode;


/**
 * This is a simple Version of RDF importer for EPCs
 * It does not import any attributes but only the structure.
 * 
 * @author Wille Tscheschner, Stefan Krumnow
 */
public class EPCDiagramRDFImporter {

	protected Document rdfdoc;
	
	public EPCDiagramRDFImporter(Document rdfdoc){
		this.rdfdoc = rdfdoc;
	}
	
	public Diagram loadEPCDiagram(){
		
		Node root = getRootNode( this.rdfdoc );
		
		if( root == null){
			return null;
		}
		
		ImportContext c = new ImportContext();
		c.diagram = new Diagram();
		c.nodes = new HashMap<String, DiagramNode>(); // key = resource id, value = node
		c.connections = new HashMap<String, DiagramNode>(); // key = to resource id, value = from
		
		List<Node> edges = new ArrayList<Node>();

		if (root.hasChildNodes()) {
			Node node = root.getFirstChild();
			while ((node = node.getNextSibling()) != null) {
				if (node instanceof Text)
					continue;

				String type = getType(node);
				if (type == null){
					continue;
				}

				if (type.equals("Function") || type.equals("Event") ||
						type.equals("AndConnector") || type.equals("OrConnector") ||
						type.equals("XorConnector") || type.equals("ProcessInterface") ||
						type.equals("Organization") || type.equals("Position") ||
						type.equals("Data") || type.equals("System")){
					addDiagramNode(type, node, c);
				} else if (type.equals("ControlFlow") || type.equals("Relation")) {
					edges.add(node);
				}
			}
			for (Node edgeNode : edges) {
				String type = getType(edgeNode);
				addDiagramEdge(type, edgeNode, c);
			}
		}
		return c.diagram;
	}
	
	protected void addDiagramNode(String type, Node node, ImportContext c){
		DiagramNode n = new DiagramNode();
		n.setType(type);
		String resourceId = getResourceId(node);
		n.setResourceId(resourceId);
		c.diagram.getNodes().add(n);
		c.nodes.put(resourceId, n);
		if (node.hasChildNodes()) {
			Node child = node.getFirstChild();
			while ((child = child.getNextSibling()) != null) {
				if (child instanceof Text)
					continue;
				String attribute = child.getNodeName().substring(
						child.getNodeName().indexOf(':') + 1);
				if (attribute.equals("outgoing")) {
					c.connections.put(getResourceId(getAttributeValue(child, "rdf:resource")), n);
				}
			}
		}
	}
	
	protected void addDiagramEdge(String type, Node node, ImportContext c){
		DiagramEdge e = new DiagramEdge();
		e.setType(type);
		String resourceId = getResourceId(node);
		e.setResourceId(resourceId);
		c.diagram.getEdges().add(e);
		e.setSource(c.connections.get(resourceId));
		if (node.hasChildNodes()) {
			Node n = node.getFirstChild();
			while ((n = n.getNextSibling()) != null) {
				if (n instanceof Text)
					continue;
				String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':') + 1);
				if (attribute.equals("outgoing")) {
					if (e.getTarget() == null){
						e.setTarget(c.nodes.get(getResourceId(getAttributeValue(n, "rdf:resource"))));
					}
				}
			}
		}
	}
	
	private String getAttributeValue(Node node, String attribute) {
		Node item = node.getAttributes().getNamedItem(attribute);
		if (item != null)
			return item.getNodeValue();
		else
			return null;
	}
	
	protected String getContent(Node node) {
		if (node != null && node.hasChildNodes())
			return node.getFirstChild().getNodeValue();
		return null;
	}

	protected String getType(Node node) {
		String type = getContent(getChild(node, "type"));
		if (type != null)
			return type.substring(type.indexOf('#') + 1);
		else
			return null;
	}

	protected String getResourceId(Node node) {
		Node item = node.getAttributes().getNamedItem("rdf:about");
		if (item != null)
			return getResourceId(item.getNodeValue());
		else
			return null;
	}

	protected String getResourceId(String id) {
		return id.substring(id.indexOf('#'));
	}

	protected Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().indexOf(name) >= 0) 
				return node;
		return null;
	}

	protected Node getRootNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("rdf:RDF"))
			return null;
		return node;
	}
	
	protected class ImportContext {
		Diagram diagram;
		Map<String, DiagramNode> nodes; // key = resource id, value = node
		Map<String, DiagramNode> connections; // key = to resource, value = from
	}
}

