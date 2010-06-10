package de.hpi.petrinet.serialization;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.PetriNetFactory;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.SilentTransition;
import de.hpi.petrinet.Transition;

/**
 * Copyright (c) 2008 Gero Decker
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public abstract class PetriNetPNMLImporter {
	
	public PetriNet loadPetriNet(Document doc) {
		Node netnode = getNetNode(doc);
		if (netnode == null)
			return null;
		
		PetriNet net = createPetriNet(netnode);
		
		Map<String,de.hpi.petrinet.Node> map = new HashMap<String, de.hpi.petrinet.Node>();
		
		Node node = netnode.getFirstChild();
		while (node != null ) {
			if (node.getNodeName().equals("place")) {
				addPlace(net, node, map);
			} else if (node.getNodeName().equals("transition")) {
				addTransition(net, node, map);
			} else if (node.getNodeName().equals("arc")) {
				addArc(net, node, map);
			}
			node = node.getNextSibling();
		}
		
		return net;
	}
	
	protected PetriNet createPetriNet(Node nnode) {
		return PetriNetFactory.eINSTANCE.createPetriNet();
	}

	protected Place addPlace(PetriNet net, Node pnode, Map<String, de.hpi.petrinet.Node> map) {
		Place p = createPlace(net, pnode);
		net.getPlaces().add(p);
		String id = pnode.getAttributes().getNamedItem("id").getNodeValue();
		p.setId(id);
		map.put(id, p);
		
		return p;
	}
	
	protected Place createPlace(PetriNet net, Node pnode) {
		return PetriNetFactory.eINSTANCE.createPlace();
	}

	protected Transition addTransition(PetriNet net, Node tnode, Map<String,de.hpi.petrinet.Node> map) {
		String label = getContent(getChild(getChild(tnode, "name"), "value"));
		if (label == null)
			label = getContent(getChild(getChild(tnode, "name"), "text"));
		
		Transition t;
		if (label != null) {
			if ((t = createLabeledTransition(net, tnode)) != null)
				((LabeledTransition)t).setLabel(label);
			else
				t = createSilentTransition(net, tnode);
			
		} else {
			t = createSilentTransition(net, tnode);
		}
		
		net.getTransitions().add(t);
		String id = tnode.getAttributes().getNamedItem("id").getNodeValue();
		t.setId(id);
		map.put(id, t);
		
		return t;
	}
	
	protected LabeledTransition createLabeledTransition(PetriNet net, Node tnode) {
		return PetriNetFactory.eINSTANCE.createLabeledTransition();
	}
	
	protected SilentTransition createSilentTransition(PetriNet net, Node tnode) {
		return PetriNetFactory.eINSTANCE.createSilentTransition();
	}

	protected FlowRelationship addArc(PetriNet net, Node anode, Map<String,de.hpi.petrinet.Node> map) {
		FlowRelationship rel = createFlowRelationship(net, anode);
		net.getFlowRelationships().add(rel);
		
		Node src = anode.getAttributes().getNamedItem("source");
		Node trg = anode.getAttributes().getNamedItem("target");
		if (src != null && trg != null) {
			rel.setSource((de.hpi.petrinet.Node)map.get(src.getNodeValue()));
			rel.setTarget((de.hpi.petrinet.Node)map.get(trg.getNodeValue()));
		}
		
		return rel;
	}
	
	protected FlowRelationship createFlowRelationship(PetriNet net, Node anode) {
		return PetriNetFactory.eINSTANCE.createFlowRelationship();
	}

	protected String getContent(Node node) {
		if (node != null && node.hasChildNodes())
			return node.getFirstChild().getNodeValue();
		return null;
	}
	
	protected Node getChild(Node n, String name) {
		if (n == null)
			return null;
		for (Node node=n.getFirstChild(); node != null; node=node.getNextSibling())
			if (node.getNodeName().equals(name)) 
				return node;
		return null;
	}

	private Node getNetNode(Document doc) {
		Node node = doc.getDocumentElement();
		if (node == null || !node.getNodeName().equals("pnml"))
			return null;
		return getChild(node, "net");
	}

}
