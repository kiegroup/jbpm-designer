package de.hpi.PTnet.verification;

import java.util.ArrayList;
import java.util.List;

import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.verification.PetriNetSyntaxChecker;

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
public class PTNetSyntaxChecker extends PetriNetSyntaxChecker {
	
	private static final String MORE_THAN_ONE_START_PLACE = "There ist more than one start place";
	private static final String MORE_THAN_ONE_END_PLACE = "There is more than one end place";
	private static final String NO_START_PLACE = "There is no start place";
	private static final String NO_END_PLACE = "There is no end place";
	private static final String NOT_CONNECTED = "Not all places and transitions lie on a path from the start place to the end place.";
	
	public PTNetSyntaxChecker(PTNet net) {
		super(net);
	}
	
	public boolean isWorkflowNet() {
		boolean foundStartPlace = false;
		boolean foundEndPlace = false;
		
		for (Place p: net.getPlaces()) {
			if (p.getIncomingFlowRelationships().size() == 0) {
				if (foundStartPlace) {
					addNodeError(p, MORE_THAN_ONE_START_PLACE);
//					return false;
				}
				foundStartPlace = true;
			}
			if (p.getOutgoingFlowRelationships().size() == 0) {
				if (foundEndPlace) {
					addNodeError(p, MORE_THAN_ONE_END_PLACE);
					return false;
				}
				foundEndPlace = true;
			}
		}
		if (!foundStartPlace) {
			addNodeError(null, NO_START_PLACE);
//			return false;
		}
		if (!foundEndPlace) {
			addNodeError(null, NO_END_PLACE);
//			return false;
		}
		
		List<Node> allNodes = new ArrayList();
		allNodes.addAll(net.getPlaces());
		allNodes.addAll(net.getTransitions());
		removeConnectedNodesFromList(allNodes.get(0), allNodes);
		if (allNodes.size() > 0) {
			addNodeError(null, NOT_CONNECTED);
			return false;
		}

		return true;
	}

	private void removeConnectedNodesFromList(Node node, List<Node> allNodes) {
		if (!allNodes.contains(node))
			return;
		allNodes.remove(node);
		
		for (FlowRelationship rel: node.getIncomingFlowRelationships())
			removeConnectedNodesFromList(rel.getSource(), allNodes);
		for (FlowRelationship rel: node.getOutgoingFlowRelationships())
			removeConnectedNodesFromList(rel.getTarget(), allNodes);
	}

}
