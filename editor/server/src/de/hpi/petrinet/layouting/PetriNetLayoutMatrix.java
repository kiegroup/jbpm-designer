package de.hpi.petrinet.layouting;

import java.util.LinkedList;
import java.util.List;

import de.hpi.petrinet.Node;

public class PetriNetLayoutMatrix {
	private final Node[][] data;
	//TODO not very performant data structure!
	private List<Node> containedData;
	
    public PetriNetLayoutMatrix() {
        data = new Node[100][100];
        containedData = new LinkedList<Node>();
    }
    
    public void set(int row, int col, Node val){
    	containedData.add(val);
    	data[row][col] = val;
    }
    
    public Node get(int row, int col){
    	return data[row][col];
    }
    
    public boolean contains(Node node){
    	return containedData.contains(node);
    }
}
