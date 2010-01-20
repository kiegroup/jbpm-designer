package de.hpi.petrinet.serialization;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
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
public class PetriNetPNMLExporter {
	public enum Tool {
	    YASPER
	}

	protected Tool targetTool;
	
	public void savePetriNet(Document doc, PetriNet net) {
		ensureUniqueIDs(net);
		
		Node root = doc.appendChild(doc.createElement("pnml"));
		Element netnode = (Element)root.appendChild(doc.createElement("net"));
		
		handlePetriNetAttributes(doc, netnode, net);

		for (Iterator<Place> it=net.getPlaces().iterator(); it.hasNext(); ) {
			appendPlace(doc, netnode, net, it.next());
		}
		
		for (Iterator<Transition> it=net.getTransitions().iterator(); it.hasNext(); ) {
			Transition t = it.next();
			appendTransition(doc, netnode, t);
		}
		
		for (Iterator<FlowRelationship> it=net.getFlowRelationships().iterator(); it.hasNext(); ) {
			appendFlowRelationship(doc, netnode, it.next());
		}
	}

	protected void ensureUniqueIDs(PetriNet net) {
		Set<String> ids = new HashSet<String>();
		int newpcounter = 1;
		int newtcounter = 1;
		
		for (Transition t: net.getTransitions()) {
			if (t.getId() == null || ids.contains(t.getId())) {
				while (ids.contains("t_"+newtcounter))
					newtcounter++;
				t.setId("p_"+(newtcounter++));
			}
			ids.add(t.getId());
		}
		for (Place p: net.getPlaces()) {
			if (p.getId() == null || ids.contains(p.getId())) {
				while (ids.contains("p_"+newpcounter))
					newpcounter++;
				p.setId("p_"+(newpcounter++));
			}
			ids.add(p.getId());
		}
	}

	protected void handlePetriNetAttributes(Document doc, Element node, PetriNet net) {
		node.setAttribute("id", "Net-One");
		node.setAttribute("type", "Petri net");
	}
	
	protected Element appendPlace(Document doc, Node netnode, PetriNet net, Place place) {
		Element pnode = (Element)netnode.appendChild(doc.createElement("place"));
		pnode.setAttribute("id", place.getId());
		
		// If initial marking needed
		if (net.getInitialMarking().getNumTokens(place) > 0){
			Node markingNode = pnode.appendChild(doc.createElement("initialMarking"));
			addContentElement(doc, markingNode, "text", String.valueOf(net.getInitialMarking().getNumTokens(place)));
		}
		
		String label = place.getLabel();
		if (null != label && !label.isEmpty()) {
			Node n1node = pnode.appendChild(doc.createElement("name"));
			if(targetTool != Tool.YASPER){
				addContentElement(doc, n1node, "value", label);
			}
			addContentElement(doc, n1node, "text", label);
		}
		
		return pnode;
	}

	protected Element appendTransition(Document doc, Node netnode, Transition transition) {
		Element tnode = (Element)netnode.appendChild(doc.createElement("transition"));
		tnode.setAttribute("id", transition.getId());
		if (transition instanceof LabeledTransition) {
			Node n1node = tnode.appendChild(doc.createElement("name"));
			if(targetTool != Tool.YASPER){
				addContentElement(doc, n1node, "value", ((LabeledTransition)transition).getLabel());
			}
			addContentElement(doc, n1node, "text", ((LabeledTransition)transition).getLabel());
		}
		return tnode;
	}

	protected Element appendFlowRelationship(Document doc, Node netnode, FlowRelationship rel) {
		Element fnode = (Element)netnode.appendChild(doc.createElement("arc"));
		fnode.setAttribute("id", "from_"+rel.getSource().getId()+"_to_"+rel.getTarget().getId());
		fnode.setAttribute("source", rel.getSource().getId());
		fnode.setAttribute("target", rel.getTarget().getId());
		
		if(rel instanceof de.hpi.highpetrinet.HighFlowRelationship){
			de.hpi.highpetrinet.HighFlowRelationship hRel = (de.hpi.highpetrinet.HighFlowRelationship)rel;
			
			if (hRel.getType()==de.hpi.highpetrinet.HighFlowRelationship.ArcType.Read){
				Node typeNode = fnode.appendChild(doc.createElement("type"));
				addContentElement(doc, typeNode, "text", "read");
			} else if (hRel.getType()==de.hpi.highpetrinet.HighFlowRelationship.ArcType.Reset){
				Node typeNode = fnode.appendChild(doc.createElement("type"));
				addContentElement(doc, typeNode, "text", "reset");
			} else if (hRel.getType()==de.hpi.highpetrinet.HighFlowRelationship.ArcType.Inhibitor){
				Node typeNode = fnode.appendChild(doc.createElement("type"));
				addContentElement(doc, typeNode, "text", "inhibitor");
			}
		}
		
		Node insnode = fnode.appendChild(doc.createElement("inscription"));
		
		String label = rel.getLabel();
		if (null != label && !label.isEmpty()) {
			if (targetTool != Tool.YASPER){
				addContentElement(doc, insnode, "value", label);
			}
			addContentElement(doc, insnode, "text", label);
		}
		// TODO this was the default behavior independent of a set label
		// why is this required
		else {
			if (targetTool != Tool.YASPER){
				addContentElement(doc, insnode, "value", "1");
			}
			addContentElement(doc, insnode, "text", "1");
		}
		
		return fnode;
	}
	
	protected void addContentElement(Document doc, Node node, String tagName, String content) {
		Node cnode = node.appendChild(doc.createElement(tagName));
		cnode.appendChild(doc.createTextNode(content));
	}

	public Tool getTargetTool() {
		return targetTool;
	}

	public void setTargetTool(Tool targetTool) {
		this.targetTool = targetTool;
	}
}
