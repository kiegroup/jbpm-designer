package de.hpi.treeGraph;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import javax.xml.parsers.*;

import org.w3c.dom.*;

public class Diagram {
	
	protected Set<Shape> shapes = new HashSet<Shape>();
	

	public Diagram(String eRdf) throws Exception {
		this.deserializeFromeRdf(eRdf);
	}


	private Shape getShapeById(String id) {
		for (Shape shape : this.shapes) {
			if (shape.getId().equals(id)) {
				return shape;
			}
		}
		return null;
	}
	
	private Collection<Node> getAllNodes() {
		Collection<Node> nodes = new ArrayList<Node>();
		for (Shape shape : this.shapes) {
			if (shape instanceof Node) {
				nodes.add((Node) shape);
			}
		}
		return nodes;
	}
	
	private Collection<Edge> getAllEdges() {
		Collection<Edge> edges = new ArrayList<Edge>();
		for (Shape shape : this.shapes) {
			if (shape instanceof Edge) {
				edges.add((Edge) shape);
			}
		}
		return edges;
	}
	
	public void printDebug(org.w3c.dom.Node node, String indent) {
		if (node == null) return;
		for (int i=0; i <node.getChildNodes().getLength(); i++) {
			org.w3c.dom.Node childNode = node.getChildNodes().item(i);
			System.out.println(indent + "<" + childNode.getNodeName());
			printDebug(childNode, indent + "\t");
		}
	}
	
	// Returns a list of xml elements which have the given tag name 
	// and an attribute with the given value 
	private List<Element> getElementsByIdAndTag(Element rootElement, 
			String tagName, String attributeName, String attributeValue) {
		
		List<Element> elements = new ArrayList<Element>();
		  
		NodeList nodeList = rootElement.getElementsByTagName(tagName); 
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i) instanceof org.w3c.dom.Node) {
				Element element = (Element) nodeList.item(i);
				if (element.hasAttribute(attributeName)) {
					if (element.getAttribute(attributeName).equals(attributeValue)) {
						elements.add(element);
					}
				}
			}
		}
		
		return elements;
	}
	
	private void deserializeFromeRdf(String eRdf) throws Exception {
		
		// Initialize document parser
		Document doc = DocumentBuilderFactory
			.newInstance()
			.newDocumentBuilder()
			.parse(new StringBufferInputStream(eRdf));
		
		// Search the canvas
		Element oryxCanvas = this.getElementsByIdAndTag((Element)doc.getFirstChild(), 
				"div", "class", "-oryx-canvas").get(0);
		
		if (oryxCanvas == null) {
			throw new Exception("Deserialization Error: Invalid eRDF input. Oryx canvas not found.");
		}
		
		List<String> renderIds = new ArrayList<String>();
		
		// Get Ids of all rendering objects
		for(Element renderElement : this.getElementsByIdAndTag(oryxCanvas, "a", "rel", "oryx-render")) {
			renderIds.add(renderElement.getAttribute("href").substring(1)); // Remove leading # from id
		}
		
		createShapeInstances(doc, renderIds);
		
		createShapeRelations(doc, renderIds);
	}
	
	// Creates a node or edge instance in the diagram for each shape
	private void createShapeInstances(Document doc, List<String> renderIds) {
		// Iterate over all rendering objects
		for (String id : renderIds) {
			// Get object main div 
			Element element = this.getElementsByIdAndTag((Element)doc.getFirstChild(), "div", "id", id).get(0);
			if (element != null) {
				// Get URI of the stencil
				String type = this.getElementsByIdAndTag(element, "span", "class", "oryx-type").get(0).getTextContent();
				// Create shape instance with id and stencil type
				Shape shape = Shape.getInstance(id, type);
				if (shape != null) {
					this.shapes.add(shape); 
				}
			}
		}
	}
	
	
	// Iterate over all rendering objects to set relations between all shapes
	private void createShapeRelations(Document doc, List<String> renderIds) {
		
		for (String id : renderIds) {
			// Get object main div 
			Element element = this.getElementsByIdAndTag((Element)doc.getFirstChild(), "div", "id", id).get(0);
			if (element != null) {
				Shape outgoingShape =  this.getShapeById(id);
				// Iterate over all objects with an raziel-outgoing relationship
				for (Element razielElement : this.getElementsByIdAndTag(element, "a", "rel", "raziel-outgoing")) {
					String incomingShapeId = razielElement.getAttribute("href").substring(1); // Remove leading '#'
					Shape incomingShape = this.getShapeById(incomingShapeId);
					if (incomingShapeId != null){
						// Create incoming and outgoing relationship for each object
						outgoingShape.addOutgoingShape(incomingShape);
						incomingShape.addIncomingShape(outgoingShape);
					}
				}
			}
		}
	}
	
	// Returns all ids of nodes that doesn't have an incoming edge
	public Collection<String> getRootNodeIds() {
		Collection<String> rootNodeIds = new ArrayList<String>();
		for (Node node : this.getAllNodes()) {
			if (node.getIncomingShapes().size() == 0) {
				rootNodeIds.add(node.getId());
			}
		}
		
		return rootNodeIds;
	}
	
	// Returns all ids of edges that haven't 1 incoming and 1 outgoing shape
	public Collection<String> getUnconnectedEdgeIds() {
		Collection<String> edgeIds = new ArrayList<String>();
		for (Edge edge : this.getAllEdges()) {
			if ((edge.getIncomingShapes().size() != 1) || 
					(edge.getOutgoingShapes().size() != 1)){
				edgeIds.add(edge.getId());
			}
		}
		
		return edgeIds;
	}
}
