package de.hpi.interactionnet.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.InteractionNetFactory;
import de.hpi.interactionnet.InteractionTransition;
import de.hpi.interactionnet.Role;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.Place;

/**
 * main method: loadIPN()
 * 
 * @author gero.decker
 *
 */
public class InteractionNetRDFImporter {
	
	protected Document doc;
	protected InteractionNetFactory factory;
	
	protected class ImportContext {
		InteractionNet net;
		Map<String,de.hpi.petrinet.Node> objects; // key = resource id, value = diagram object
		Map<String,de.hpi.petrinet.Node> connections; // key = to resource id, value = from node
	}
	
	public InteractionNetRDFImporter(Document doc) {
		this.doc = doc;
	}
	
	public InteractionNet loadInteractionNet() {
		Node root = getRootNode(doc);
		if (root == null) return null;
		
		factory = new InteractionNetFactory();
		
		ImportContext c = new ImportContext();
		c.net = factory.createInteractionNet();
//		Map map = new HashMap();		
		c.objects = new HashMap(); // key = resource id, value = node
		c.connections = new HashMap(); // key = to resource id, value = from node
		
		List<Node> edges = new ArrayList();
		
		// handle nodes
		for (Node node=root.getFirstChild(); node != null; node=node.getNextSibling()) {
			if (node instanceof Text) continue;
			
			String type = getType(node);
			if (type == null) continue;
			
			if (type.equals("Place")) {
				addPlace(node, c);
			} else if (type.equals("Interaction")) {
				addInteraction(node, c);
			} else if (type.equals("Arc")) {
				edges.add(node);
			}
		}
		
		// handle edges (except undirected associations)
		for (Node node: edges) {
			addArc(node, c);
		}
		
		return c.net;
	}
	
	protected void addPlace(Node node, ImportContext c) {
		Place p = factory.createPlace();
		c.net.getPlaces().add(p);
		c.objects.put(getResourceId(node), p);
		
		for (Node n=node.getFirstChild(); n != null; n=n.getNextSibling()) {
			if (n instanceof Text) continue;
			String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':')+1);
			
			if (attribute.equals("id")) {
				String id = getContent(n);
				if (id != null)
					p.setId(id);
			} else if (attribute.equals("marked")) {
				if ("true".equals(getContent(n)))
					c.net.getInitialMarking().addToken(p);
			} else if (attribute.equals("outgoing")) {
				c.connections.put(getResourceId(getAttributeValue(n, "rdf:resource")), p);
			}
		}
		if (p.getId() == null)
			p.setId(getResourceId(node));
	}

	protected void addInteraction(Node node, ImportContext c) {
		InteractionTransition t = factory.createInteractionTransition();
		c.net.getTransitions().add(t);
		c.objects.put(getResourceId(node), t);
		
		for (Node n=node.getFirstChild(); n != null; n=n.getNextSibling()) {
			if (n instanceof Text) continue;
			String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':')+1);
			
			if (attribute.equals("sender")) {
				t.setSender(findOrCreateRole(getContent(n), c));
			} else if (attribute.equals("receiver")) {
				t.setReceiver(findOrCreateRole(getContent(n), c));
			} else if (attribute.equals("messagetype")) {
				t.setMessageType(getContent(n));
			} else if (attribute.equals("outgoing")) {
				c.connections.put(getResourceId(getAttributeValue(n, "rdf:resource")), t);
			}
		}
		if (t.getId() == null)
			t.setId(getResourceId(node));
	}

	protected void addArc(Node node, ImportContext c) {
		FlowRelationship arc = factory.createFlowRelationship();
		c.net.getFlowRelationships().add(arc);
		setConnections(arc, node, c);
	}

	protected void setConnections(FlowRelationship arc, Node node, ImportContext c) {
		arc.setSource(c.connections.get(getResourceId(node)));
		
		for (Node n=node.getFirstChild(); n != null; n=n.getNextSibling()) {
			if (n instanceof Text) continue;
			String attribute = n.getNodeName().substring(n.getNodeName().indexOf(':')+1);
			
			if (attribute.equals("outgoing")) {
				arc.setTarget(c.objects.get(getResourceId(getAttributeValue(n, "rdf:resource"))));
			}
		}
	}
	
	protected Role findOrCreateRole(String roleName, ImportContext c) {
		for (Role r: c.net.getRoles()) {
			if (r.getName().equals(roleName))
				return r;
		}
		Role r = factory.createRole();
		r.setName(roleName);
		c.net.getRoles().add(r);
		return r;
	}

	

	protected String getContent(Node node) {
		if (node != null && node.hasChildNodes())
			return node.getFirstChild().getNodeValue();
		return null;
	}
	
	private String getAttributeValue(Node node, String attribute) {
		Node item = node.getAttributes().getNamedItem(attribute);
		if (item != null)
			return item.getNodeValue();
		else
			return null;
	}

	protected String getType(Node node) {
		String type = getContent(getChild(node, "type"));
		if (type != null)
			return type.substring(type.indexOf('#')+1);
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
		return id.substring(id.indexOf('#')+1);
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

}
