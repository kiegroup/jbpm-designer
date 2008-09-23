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
	protected boolean isChecked = false;
	
	protected Set<Shape> shapes = new HashSet<Shape>();
	
	protected Set<Shape> shapesWithErrors = new HashSet<Shape>();
	
	protected Node rootNode = null;
		
	protected List<Element> getElementsByIdAndTag(Element rootElement, 
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
	
	protected Node getRootNode() {
		return this.rootNode; 
	}
	
	
	protected Shape getShapeById(String id) {
		for (Shape shape : this.shapes) {
			if (shape.getId().equals(id)) {
				return shape;
			}
		}
		return null;
	}
	
	protected Collection<Node> getAllNodes() {
		Collection<Node> nodes = new ArrayList<Node>();
		for (Shape shape : this.shapes) {
			if (shape instanceof Node) {
				nodes.add((Node) shape);
			}
		}
		return nodes;
	}
	
	protected Collection<Edge> getAllEdges() {
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
	
	public boolean deserializeFromeRdf(String eRdf)  throws Exception {
		
		Document doc = DocumentBuilderFactory
			.newInstance()
			.newDocumentBuilder()
			.parse(new StringBufferInputStream(eRdf));
		
		NodeList nodeList = doc.getElementsByTagName("div"); 
		
		Element oryxCanvas = this.getElementsByIdAndTag((Element)doc.getFirstChild(), 
				"div", "class", "-oryx-canvas").get(0);
		
		if (oryxCanvas == null) {
			throw new Exception("Deserialization Error: Invalid eRDF input. Oryx canvas not found.");
		}
		
		List<String> renderIds = new ArrayList<String>();
		
		for(Element renderElement:this.getElementsByIdAndTag(oryxCanvas, "a", "rel", "oryx-render")) {
			renderIds.add(renderElement.getAttribute("href").substring(1)); // Remove leading # from id
		}
		
		for (String id : renderIds) {
			//Element element = doc.getElementById(id);
			Element element = this.getElementsByIdAndTag((Element)doc.getFirstChild(), "div", "id", id).get(0);
			if (element != null) {
				String type = this.getElementsByIdAndTag(element, "span", "class", "oryx-type").get(0).getTextContent();
				if (type.endsWith("Edge")) {
					this.shapes.add(new Edge(id));
				}
				if (type.endsWith("Node")) {
					this.shapes.add(new Node(id));
				}
			}
		}
		for (String id : renderIds) {
			Element element = this.getElementsByIdAndTag((Element)doc.getFirstChild(), "div", "id", id).get(0);
			if (element != null) {
				Shape s =  this.getShapeById(id);
				for (Element e : this.getElementsByIdAndTag(element, "a", 
						"rel", "raziel-outgoing")) {
					Shape shape = this.getShapeById(e.getAttribute("href").substring(1));
					if (shape.getIngoingShape() == null){
						s.attachShape(shape);
					} else {
						this.shapesWithErrors.add(shape);
					}

				}
			}
		}
		return this.checkSyntax();
	}
	
	public boolean checkSyntax() {
		this.rootNode = null;
		
		// Find double root nodes
		for (Node node : this.getAllNodes()) {
			if (node.getIngoingShape() == null) {
				if (this.rootNode == null) {
					this.rootNode = node;
				} else {
					this.shapesWithErrors.add(node);
					this.shapesWithErrors.add(rootNode);
				}			
			}
		}
		
		// Find edges that aren't connected to exactly two nodes
		for (Edge edge : this.getAllEdges()) {
			if ((edge.getIngoingShape() != null) && (edge.getOutgoingShapes().size() == 1)) {
				if ((edge.getIngoingShape() instanceof Node) && 
						(edge.getOutgoingShapes().get(0) instanceof Node)) {
					continue;
				}
			}
			this.shapesWithErrors.add(edge);
		}
		
		if (this.rootNode != null && this.shapesWithErrors.size() == 0) {
			this.isChecked = true;
			return true;
		} else {
			return false;
		}
	}

}
