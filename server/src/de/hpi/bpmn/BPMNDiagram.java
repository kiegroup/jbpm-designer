package de.hpi.bpmn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hpi.bpmn.validation.BPMNSyntaxChecker;

/**
 * 
 * @author Gero.Decker
 *
 */
public class BPMNDiagram implements Container {
	
	protected String title;
	protected List<Node> childNodes;
	protected List<DataObject> dataObjects;
	protected List<Edge> edges;
	protected List<Container> processes;
	
	public BPMNSyntaxChecker getSyntaxChecker() {
		return new BPMNSyntaxChecker(this);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<DataObject> getDataObjects() {
		if (dataObjects == null)
			dataObjects = new ArrayList();
		return dataObjects;
	}

	/**
	 * 
	 * @return list of all edges in the diagram (edges are not contained by Pool, Lane, etc.)
	 */
	public List<Edge> getEdges() {
		if (edges == null)
			edges = new ArrayList();
		return edges;
	}

	public List<Node> getChildNodes() {
		if (childNodes == null)
			childNodes = new ArrayList();
		return childNodes;
	}
	
	/**
	 * 
	 * @return list of top-level processes: all nodes within the same process are connected through ControlFlow
	 */
	public List<Container> getProcesses() {
		return processes;
	}
	
	// identifies sets of nodes, connected through SequenceFlow
	public void identifyProcesses() {
		processes = new ArrayList();
		
		List<Node> allNodes = new ArrayList();
		getAllNodesRecursively(this, allNodes);
		
		// handle subprocesses => trivial
		for (Iterator<Node> niter = allNodes.iterator(); niter.hasNext(); ) {
			Node node = niter.next();
			if (node instanceof SubProcess)
				handleSubProcess((SubProcess)node);
		}
		
		// identify components within allNodes
		while (allNodes.size() > 0) {
			Process currentProcess = new Process();
			processes.add(currentProcess);

			addNode(currentProcess, allNodes.get(0), allNodes);
		}
	}
	
	protected void handleSubProcess(SubProcess process) {
		for (Iterator<Node> citer = process.getChildNodes().iterator(); citer.hasNext(); ) {
			Node node = citer.next();
			node.process = process;
			if (node instanceof SubProcess)
				handleSubProcess((SubProcess)node);
		}
	}

	// TODO: handle compensation flow
	private void addNode(Process process, Node node, List<Node> allNodes) {
		if (!allNodes.contains(node))
			return;
		allNodes.remove(node);
		node.setProcess(process);
		
		// attention: navigate into both directions!
		for (Iterator<Edge> eiter = node.getIncomingEdges().iterator(); eiter.hasNext(); ) {
			Edge e = eiter.next();
			if (e instanceof ControlFlow)
				addNode(process, (Node)e.getSource(), allNodes);
		}
		for (Iterator<Edge> eiter = node.getOutgoingEdges().iterator(); eiter.hasNext(); ) {
			Edge e = eiter.next();
			if (e instanceof ControlFlow)
				addNode(process, (Node)e.getTarget(), allNodes);
		}
		// attention: navigate into both directions!
		if (node instanceof IntermediateEvent) {
			if (((IntermediateEvent)node).getActivity() != null) {
				addNode(process, ((IntermediateEvent)node).getActivity(), allNodes);
			}
		} else if (node instanceof Activity) {
			for (IntermediateEvent event: ((Activity)node).getAttachedEvents())
				addNode(process, event, allNodes);
		}
	}

	// stops at subprocesses
	private void getAllNodesRecursively(Container container, List<Node> allNodes) {
		for (Iterator<Node> niter = container.getChildNodes().iterator(); niter.hasNext(); ) {
			Node node = niter.next();
			if (node instanceof Activity || node instanceof Event || node instanceof Gateway) {
				allNodes.add(node);
			} else if (node instanceof Pool || node instanceof Lane) {
				getAllNodesRecursively((Container)node, allNodes);
			}
		}
	}

}
