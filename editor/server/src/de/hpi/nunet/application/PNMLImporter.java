package de.hpi.nunet.application;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.hpi.nunet.FlowRelationship;
import de.hpi.nunet.InterconnectionModel;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.NuNetFactory;
import de.hpi.nunet.Place;
import de.hpi.nunet.ProcessModel;
import de.hpi.nunet.Token;
import de.hpi.nunet.Transition;

public class PNMLImporter {
	
	private NuNetFactory factory;
	
	public PNMLImporter() {
		this.factory = NuNetFactory.eINSTANCE;
	}
	
	public NuNet loadNuNet(Document doc) {
		InterconnectionModel net = factory.createInterconnectionModel();
		
		Node netnode = getNetNode(doc);
		if (netnode == null)
			return net;
		
		Map map = new HashMap();
		
		for (Node node=netnode.getFirstChild(); node.getNextSibling() != null; node=node.getNextSibling()) {
			if (node.getNodeName().equals("place")) {
				addPlace(net, node, map);
			} else if (node.getNodeName().equals("transition")) {
				addTransition(net, node, map);
			} else if (node.getNodeName().equals("arc")) {
				addArc(net, node, map);
			}
		}
		
		if (net.getProcessModels().size() == 0) {
			NuNet net2 = factory.createNuNet();
			net2.getPlaces().addAll(net.getPlaces());
			net2.getTransitions().addAll(net.getTransitions());
			net2.getFlowRelationships().addAll(net.getFlowRelationships());
			
			// copy tokens
			Iterator<Place> it2=net2.getPlaces().iterator();
			for (Iterator<Place> it=net.getPlaces().iterator(); it.hasNext(); )
				net2.getInitialMarking().getTokens(it2.next()).addAll(net.getInitialMarking().getTokens(it.next()));
			return net2;
		} else {
			return net;
		}
	}

	private void addPlace(InterconnectionModel net, Node pnode, Map map) {
		Place p = factory.createPlace();
		net.getPlaces().add(p);
		String id = pnode.getAttributes().getNamedItem("id").getNodeValue();
		map.put(id, p);
		
		p.setLabel(getContent(getChild(getChild(pnode, "name"), "value")));
		if (p.getLabel() == null) {
			p.setLabel(getContent(getChild(getChild(pnode, "name"), "text")));
			if (p.getLabel() == null)
				p.setLabel(id);
		}
		
		Node node = getChild(pnode, "initialMarking");
		if (node != null && node.hasChildNodes()) {
			for (node=node.getFirstChild(); node.getNextSibling() != null; node=node.getNextSibling()) {
				if (node.getNodeName().equals("token")) {
					Token tok = factory.createToken();
					net.getInitialMarking().getTokens(p).add(tok);
					if (node.hasChildNodes())
						for (Node node2=node.getFirstChild(); node2.getNextSibling() != null; node2=node2.getNextSibling()) {
							if (node2.getNodeName().equals("name")) {
								tok.getNames().add(getContent(node2));
							}
						}
				}
			}
		}
		
		checkProcessModel(net, p, pnode);
	}

	private void addTransition(InterconnectionModel net, Node tnode, Map map) {
		Transition t = factory.createTransition();
		net.getTransitions().add(t);
		String id = tnode.getAttributes().getNamedItem("id").getNodeValue();
		map.put(id, t);

		t.setLabel(getContent(getChild(getChild(tnode, "name"), "value")));
		if (t.getLabel() == null) {
			t.setLabel(getContent(getChild(getChild(tnode, "name"), "text")));
			if (t.getLabel() == null)
				t.setLabel(id);
		}
		
		checkProcessModel(net, t, tnode);
	}

	private void addArc(InterconnectionModel net, Node anode, Map map) {
		FlowRelationship rel = factory.createFlowRelationship();
		net.getFlowRelationships().add(rel);
		
		Node src = anode.getAttributes().getNamedItem("source");
		Node trg = anode.getAttributes().getNamedItem("target");
		if (src != null && trg != null) {
			rel.setSource((de.hpi.nunet.Node)map.get(src.getNodeValue()));
			rel.setTarget((de.hpi.nunet.Node)map.get(trg.getNodeValue()));
		}

		Node node = getChild(getChild(anode, "inscription"), "expression");
		if (node != null && node.hasChildNodes()) {
			for (Node node2=node.getFirstChild(); node2.getNextSibling() != null; node2=node2.getNextSibling()) {
				if (node2.getNodeName().equals("var")) {
					rel.getVariables().add(getContent(node2));
				} else if (node2.getNodeName().equals("new")) {
					rel.getVariables().add(NuNet.NEW);
				}
			}
		}
	}
	
	private void checkProcessModel(InterconnectionModel net, de.hpi.nunet.Node n, Node pnode) {
		String pmname = getContent(getChild(pnode, "process"));
		if (pmname != null) {
			for (Iterator<ProcessModel> it=net.getProcessModels().iterator(); it.hasNext(); ) {
				ProcessModel pm = it.next();
				if (pm.getName().equals(pmname)) {
					n.setProcessModel(pm);
					return;
				}
			}
			ProcessModel pm = factory.createProcessModel();
			pm.setName(pmname);
			net.getProcessModels().add(pm);
			n.setProcessModel(pm);
		}
	}

	private String getContent(Node node) {
		if (node != null && node.hasChildNodes())
			return node.getFirstChild().getNodeValue();
		return null;
	}
	
	private Node getChild(Node n, String name) {
		if (n == null || !n.hasChildNodes())
			return null;
		for (Node node=n.getFirstChild(); node.getNextSibling() != null; node=node.getNextSibling())
			if (node.getNodeName().equals(name)) 
				return node;
		return null;
	}

	private Node getNetNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("pnml"))
			return null;
		node = node.getFirstChild();
		if (node == null) 
			return null;
		node = node.getNextSibling();
		if (node == null || !node.getNodeName().equals("net")) 
			return null;
		return node;
	}

}
