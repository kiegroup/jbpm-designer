package de.iaas.bpel.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import de.iaas.bpel.models.Container;
import de.iaas.bpel.models.Edge;
import de.iaas.bpel.models.Node;
import de.iaas.bpel.models.Process;
import de.iaas.bpel.validation.BPELSyntaxChecker;

/**
 * Copyright (c) 2008 Zhen Peng
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

public class BPELDiagram implements Container{
	
	protected String title;
	protected List<Node> childNodes;
	protected List<Edge> edges;
	protected List<Container> processes;
	
	public BPELSyntaxChecker getSyntaxChecker() {
		return new BPELSyntaxChecker(this);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public List<Edge> getEdges() {
		if (edges == null)
			edges = new ArrayList<Edge>();
		return edges;
	}
	
	public List<Node> getChildNodes() {
		if (childNodes == null)
			childNodes = new ArrayList<Node>();
		return childNodes;
	}
	
	public List<Container> getProcesses() {
		return processes;
	}
	
	public Process getProcess(){
		return (Process)processes.get(0);
	}
	
	// identifies sets of nodes, connected through SequenceFlow
	public void identifyProcesses() {
		processes = new ArrayList<Container>();
		
		List<Node> allNodes = new ArrayList<Node>();
		getAllNodesRecursively(this, allNodes);
		
		// identify components within allNodes
		while (allNodes.size() > 0) {
			Process currentProcess = new Process();
			processes.add(currentProcess);

			addNode(currentProcess, allNodes.get(0), allNodes);
		}
	}

	

	private void addNode(Process process, Node node, List<Node> allNodes) {
		if (!allNodes.contains(node))
			return;
		allNodes.remove(node);
		node.setProcess(process);
		
		// attention: navigate into both directions!
		for (Iterator<Edge> eiter = node.getIncomingEdges().iterator(); eiter.hasNext(); ) {
			Edge e = eiter.next();
		    addNode(process, (Node)e.getSource(), allNodes);
		}
		for (Iterator<Edge> eiter = node.getOutgoingEdges().iterator(); eiter.hasNext(); ) {
			Edge e = eiter.next();
			addNode(process, (Node)e.getTarget(), allNodes);
		}

	}

	private void getAllNodesRecursively(Container container, List<Node> allNodes) {
		for (Iterator<Node> niter = container.getChildNodes().iterator(); niter.hasNext(); ) {
			Node node = niter.next();
			if (node instanceof BasicActivity && !(node instanceof Invoke )) {
				allNodes.add(node);
			} else if (node instanceof Invoke || node instanceof Process
					|| node instanceof StructuredActivity || node instanceof ScopeActivity) {
				getAllNodesRecursively((Container)node, allNodes);
			}
		}
	}

}
