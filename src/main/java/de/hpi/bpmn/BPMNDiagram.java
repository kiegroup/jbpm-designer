package de.hpi.bpmn;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hpi.bpel4chor.model.activities.BlockActivity;
import de.hpi.bpel4chor.model.activities.Handler;
import de.hpi.bpel4chor.model.activities.StartEvent;
import de.hpi.bpel4chor.model.activities.Task;
import de.hpi.bpel4chor.model.activities.TriggerResultMessage;
import de.hpi.bpel4chor.model.artifacts.ParticipantSetDataObject;
import de.hpi.bpel4chor.model.artifacts.VariableDataObject;
import de.hpi.bpel4chor.model.connections.Association;
import de.hpi.bpel4chor.model.connections.MessageFlow;

import de.hpi.bpmn.validation.BPMNSyntaxChecker;

/**
 * 
 * @author Gero.Decker
 *
 */
public class BPMNDiagram implements Container {
	
	protected String title;
	protected String id;
	//BPMN extension for YAWL
	protected String dataTypeDefinition;
	
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

	/**
	 * the dataTypeDefinition getter
	 * @return dataTypeDefinition
	 */
	public String getDataTypeDefinition() {
		return dataTypeDefinition;
	}

	/**
	 * the dataTypeDefinition setter
	 * @param dataTypeDefinition
	 */
	public void setDataTypeDefinition(String dataTypeDefinition) {
		this.dataTypeDefinition = dataTypeDefinition;
	}

	public List<DataObject> getDataObjects() {
		if (dataObjects == null)
			dataObjects = new ArrayList<DataObject>();
		return dataObjects;
	}

	/**
	 * 
	 * @return list of all edges in the diagram (edges are not contained by Pool, Lane, etc.)
	 */
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
	
	/**
	 * 
	 * @return list of top-level processes: all nodes within the same process are connected through ControlFlow
	 */
	public List<Container> getProcesses() {
		return processes;
	}
	
	// identifies sets of nodes, connected through SequenceFlow
	public void identifyProcesses() {
		processes = new ArrayList<Container>();
		
		List<Node> allNodes = new ArrayList<Node>();
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
		for (Iterator<SequenceFlow> eiter = node.getIncomingSequenceFlows().iterator(); eiter.hasNext(); ) {
			Edge e = eiter.next();
			if(e.sourceAndTargetContainedInSamePool()){
				addNode(process, (Node)e.getSource(), allNodes);
			}
		}
		for (Iterator<SequenceFlow> eiter = node.getOutgoingSequenceFlows().iterator(); eiter.hasNext(); ) {
			Edge e = eiter.next();
			if(e.sourceAndTargetContainedInSamePool()){
				addNode(process, (Node)e.getTarget(), allNodes);
			}
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return The query language of the diagram.
	 */
	public URI getQueryLanguage() {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * @return The expression language defined for the diagram.
	 */
	public URI getExpressionLanguage() {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * @return The variable data objects in the diagram.
	 */
	public List<VariableDataObject> getVariableDataObjects() {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * Determines the first fault variable data object that is 
	 * associated with the given event. Only associations with the direction
	 * From are considered (association from event to data object).
	 * 
	 * @param errorEvent The event the data object must be associated with. 
	 * This must be an intermediate event with the trigger ResultError.
	 * 
	 * @return The first found fault variable data object. Or null if 
	 * no variable data object was found.
	 */
	public VariableDataObject getFaultVariable(de.hpi.bpel4chor.model.activities.IntermediateEvent errorEvent) {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * Determines the first handler that is connected the given event by an
	 * association. Only association with the direction To or From are
	 * considered. Moreover the arrow head of the association must always
	 * be on the handler side.
	 * 
	 * @param event The intermediate event to determine the connected handler
	 * for.
	 * 
	 * @return The first handler that is connected with the event. Or null if no
	 * handler was found.
	 */
	public Handler getAssociatedCompensationHandler(de.hpi.bpel4chor.model.activities.IntermediateEvent event) {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * Determines the first message variable data object that is 
	 * associated with the given start event. Only associations with the direction
	 * From are considered (association from event to data object).
	 * 
	 * @param start The start event the data object must be associated with. 
	 * This must be a start event with the trigger {@link TriggerResultMessage}.
	 * 
	 * @return The first found fault variable data object. Or null if 
	 * no variable data object was found.
	 */
	public VariableDataObject getMessageVariable(StartEvent start) {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * Adds a variable data object to the list of variable data objects 
	 * in the diagram.
	 *  
	 * @param data The variable data object to add.
	 */
	public void addVariableDataObject(VariableDataObject data) {
		// TODO Required by BPMN2BPEL transformation.
	}

	/**
	 * Adds an association to the list of associations in the diagram.
	 *  
	 * @param assoc The association to add.
	 */
	public void addAssociation(Association assoc) {
		// TODO Required by BPMN2BPEL transformation.
	}

	/**
	 * Determines the first counter variable data object that is 
	 * associated with the given looping task. Only associations with the direction
	 * To are considered (association from data object to task). 
	 * 
	 * @param loopingActivity The looping task the counter variable is associated with
	 * 
	 * @return The first found counter variable data object. Or null if
	 * no counter variable data object was found.
	 */
	public VariableDataObject getCounterVariable(Task loopingActivity) {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * Determines the first participant set data object that is associated
	 * with the given activity. Only associations with direction To are
	 * considered (association from data object to activity).
	 * 
	 * @param loopingActivity A multiple instance looping activity.
	 * 
	 * @return The first participant set data object associated with the
	 * given activity. Or null of no participant set was found.
	 */
	public ParticipantSetDataObject getLoopCounterSet(
			de.hpi.bpel4chor.model.activities.Activity loopingActivity) {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * Determines the first counter variable data object that is 
	 * located in the given block activity.
	 * 
	 * @param loopingActivity The looping block activity the counter variable is located in
	 * 
	 * @return The first found counter variable data object. Or null if no
	 * counter variable data object was found.
	 */
	public VariableDataObject getCounterVariable(BlockActivity loopingActivity) {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * Determines the first standard variable data objects that is associated with
	 * the given activity. If the activity is a receiving activity, then 
	 * associations from the activity to the data object are considered. If
	 * the activity is not receiving, associations from the data object to the 
	 * activity are considered.
	 * 
	 * 
	 * @param activity  The activity the data objects are associated with
	 * @param receiving True, if the activity is a receiving activity, 
	 * false otherwise.
	 * 
	 * @return The first standard variable data object associated with the
	 * activity. Or null if no variable data object was found.
	 */
	public VariableDataObject getStandardVariable(de.hpi.bpel4chor.model.activities.Activity activity, boolean receiving) {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * Collects the message flows that have the specified target object.
	 * 
	 * @param targetId     The id of the target object.
	 * 
	 * @return A list with the collected message flows. Or an empty
	 * list of no message flow was found.
	 */
	public List<MessageFlow> getMessageFlowsWithTarget(String targetId) {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * Returns the first message flow with the specified source object.
	 * 
	 * @param sourceId     The id of the source object.
	 * 
	 * @return The first message flow found. Or null if no message flow
	 * was found.
	 */
	public MessageFlow getMessageFlowWithSource(String sourceId) {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * Collects the message flows that have the specified source object.
	 * 
	 * @param sourceId     The id of the source object.
	 * 
	 * @return A list with the collected message flows. Or an empty
	 * list of no message flow was found.
	 */
	public List<MessageFlow> getMessageFlowsWithSource(String sourceId) {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * Determines all standard variable data objects that are associated with
	 * the given activity. If the activity is a receiving activity, then 
	 * associations from the activity to the data object are considered. If
	 * the activity is not receiving, associations from the data object to the 
	 * activity are considered.
	 * 
	 * 
	 * @param activity  The activity the data objects are associated with
	 * @param receiving True, if the activity is a receiving activity, 
	 * false otherwise.
	 * 
	 * @return A list with the associated standard variable data objects.
	 * Or an empty list if no variable data object was found.
	 */
	public List<VariableDataObject> getStandardVariables(
			de.hpi.bpel4chor.model.activities.Activity activity, boolean receiving) {
		// TODO Required by BPMN2BPEL transformation.
		return null;
	}

	/**
	 * Returns a list of {@link Pool}, one for each identified process
	 * 
	 * @return 
	 * 		The pools in the diagram.
	 */
	public List<Pool> getPools() {
		ArrayList<Pool> pools = new ArrayList<Pool>();
		
		for(Iterator<Container> it = this.getProcesses().iterator(); it.hasNext(); ) {
			Pool processPool = getPoolOfProcess(it.next());
			if (processPool != null) {
				pools.add(processPool);
			}
		}
		
		return pools;
	}
	
	/**
	 * Search the process for a pool and returns it.
	 * 
	 * @param process
	 * 		The process to handle
	 * @return
	 * 		The {@link Pool} if existing
	 */
	private Pool getPoolOfProcess(Container process) {
		for(Iterator<Node> it = process.getChildNodes().iterator(); it.hasNext();) {
			Node current = it.next();
			if (current instanceof Pool) {
				return (Pool) current;
			}
		}
		return null;
	}
}
