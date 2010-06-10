package de.hpi.PTnet.serialization;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.serialization.PetriNetPNMLExporter;

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
public class PTNetPNMLExporter extends PetriNetPNMLExporter {

	@Override
	protected void handlePetriNetAttributes(Document doc, Element node, PetriNet net) {
		super.handlePetriNetAttributes(doc, node, net);
		node.setAttribute("type", "PT net");
	}

	@Override
	protected Element appendPlace(Document doc, Node netnode, PetriNet net, Place place) {
		Element pnode = super.appendPlace(doc, netnode, net, place);
		
		Node imode = pnode.appendChild(doc.createElement("initialMarking"));
		Node imvnode = imode.appendChild(doc.createElement("value"));
		imvnode.appendChild(doc.createTextNode(""+net.getInitialMarking().getNumTokens(place)));
		
		return pnode;
	}

}
