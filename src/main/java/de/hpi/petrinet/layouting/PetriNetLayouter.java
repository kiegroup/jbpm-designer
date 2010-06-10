package de.hpi.petrinet.layouting;

import java.util.LinkedList;
import java.util.List;

import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;
import de.hpi.util.Bounds;

public class PetriNetLayouter {
	PetriNet net;
	PetriNetLayoutMatrix matrix;
	List<Node> examinedNodes;
	
	public PetriNetLayouter(PetriNet net){
		this.net = net;
	}
	
	public void layout(){
		matrix = new PetriNetLayoutMatrix();
		examinedNodes = new LinkedList<Node>();
		
		buildLayoutMatrix();
		
		setBounds();
	}
	
	private void setBounds() {
		for(int row = 0; row < matrix.sizeRows; row ++){
			for(int col = 0; col < matrix.sizeCols; col ++){
				Node node = matrix.get(row, col);
				if(node != null){
					int height = 0;
					int width = 0;
					int margin = 80; //distance from left and upper corner of oryx canvas
					int x = margin + 100*col; //center x
					int y = margin + 100*row; // center y
					if(node instanceof Place){
						height = 30;
						width = 30;
					} else if (node instanceof LabeledTransition) {
						height = 40;
						width = 80;
					} else { //nop transition
						height = 50;
						width = 10;
					}
					Bounds bounds = new Bounds(x-width/2, y-height/2, x+width/2, y+height/2);
					node.setBounds(bounds);
				}
			}
		}
	}

	public void buildLayoutMatrix(){
		takeStep(getStartNodes(), 0);
	}
	
	public void takeStep(List<Node> nodes, int step){
		if(nodes.size() == 0) return;
		
		List<Node> nextNodes = new LinkedList<Node>();

		int i = 0;
		for(Node node : nodes){
			// Set position in layouting matrix
			matrix.set(i, step, node);
			
			addNextNodes(nextNodes, node);
			
			i++;
		}
		
		step++;
		takeStep(nextNodes, step);
	}
	
	public void addNextNodes(List<Node> nextNodes, Node node){
		for(FlowRelationship rel : node.getOutgoingFlowRelationships()){
			if(		// If next node isn't already in nextNodes (e.g. if node has multiple incoming arcs)
					!nextNodes.contains(rel.getTarget()) && 
					// If next node hasn't already been added to matrix
					!matrix.contains(rel.getTarget())
			){
				nextNodes.add(rel.getTarget());
			}
		}
	}

	public List<Node> getStartNodes(){
		List<Node> startNodes = new LinkedList<Node>();
		
		for(Place place : net.getPlaces()){
			if(place.getIncomingFlowRelationships().size() == 0){
				startNodes.add(place);
			}
		}
		
		for(Transition transition : net.getTransitions()){
			if(transition.getIncomingFlowRelationships().size() == 0){
				startNodes.add(transition);
			}
		}
		
		return startNodes;
	}
}
