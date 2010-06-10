package de.hpi.bpmn2pn.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hpi.PTnet.PTNetFactory;
import de.hpi.bpmn.Association;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.DataObject;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.Task;
import de.hpi.bpmn2pn.model.ConversionContext;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class DataObjectConverter extends Converter {

//	private class DataObjectStates
//	{
//		String dataObjLabel;
//		ArrayList<String> states;
//		public boolean equals(Object other)
//		{
//			if (other instanceof DataObjectStates)
//				return this.dataObjLabel.equals(((DataObjectStates)other).dataObjLabel);
//			if (other instanceof String)
//				return this.dataObjLabel.equals(((DataObjectStates)other));
//			return false;
//		}
//	}
	public DataObjectConverter(BPMNDiagram diagram) {
		super(diagram, new PTNetFactory());
	}

	@Override
	protected ConversionContext setupConversionContext() {
		return new DataObjectConversionContext();
	}

	@Override
	protected void handleEndEvent(PetriNet net, EndEvent event, ConversionContext c) {
		// TODO Auto-generated method stub
		super.handleEndEvent(net, event, c);
	}

	@Override
	protected void handleIntermediateEvent(PetriNet net, IntermediateEvent event, ConversionContext c) {
		// TODO Auto-generated method stub
		super.handleIntermediateEvent(net, event, c);
	}

	@Override
	protected void handleStartEvent(PetriNet net, StartEvent event, ConversionContext c) {
		// TODO Auto-generated method stub
		super.handleStartEvent(net, event, c);
	}
	protected void handleUnconditionalUpdate(PetriNet net, Task task,String dataObjectLabel, ConversionContext c,String postState)
	{
		for(String state: ((DataObjectConversionContext)c).dataObjStates.get(dataObjectLabel))
		{
			Transition t = addLabeledTransition(net, task.getId(), task, 0, task.getLabel(), c);
			handleMessageFlow(net, task, t, t, c);
			addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(task)), t);
			addFlowRelationship(net, t, c.map.get(getOutgoingSequenceFlow(task)));
			
			// add flow relation with state places
			addFlowRelationship(net, ((DataObjectConversionContext) c).dataObjPlaces.get(dataObjectLabel+"_"+state), t);
			addFlowRelationship(net, t,((DataObjectConversionContext) c).dataObjPlaces.get(dataObjectLabel+"_"+postState));
			if (c.ancestorHasExcpH)
				handleExceptions(net, task, t, c);
			
			for (IntermediateEvent event: task.getAttachedEvents())
				handleAttachedIntermediateEventForTask(net, event, c);
		}
	}
	protected void handleConditionalUpdate(PetriNet net, Task task,String dataObjectLabel, ConversionContext c,String preState, String postState)
	{
		Transition t = addLabeledTransition(net, task.getId(), task, 0, task.getLabel(), c);
		handleMessageFlow(net, task, t, t, c);
		addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(task)), t);
		addFlowRelationship(net, t, c.map.get(getOutgoingSequenceFlow(task)));
		
		// add flow relation with state places
		addFlowRelationship(net, ((DataObjectConversionContext) c).dataObjPlaces.get(dataObjectLabel+"_"+preState), t);
		addFlowRelationship(net, t,((DataObjectConversionContext) c).dataObjPlaces.get(dataObjectLabel+"_"+postState));
		if (c.ancestorHasExcpH)
			handleExceptions(net, task, t, c);
		
		for (IntermediateEvent event: task.getAttachedEvents())
			handleAttachedIntermediateEventForTask(net, event, c);
	}
	protected void handleConditionalRead(PetriNet net, Task task,String dataObjectLabel, ConversionContext c,String preState)
	{
		Transition t = addLabeledTransition(net, task.getId(), task, 0, task.getLabel(), c);
		handleMessageFlow(net, task, t, t, c);
		addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(task)), t);
		addFlowRelationship(net, t, c.map.get(getOutgoingSequenceFlow(task)));
		
		// add flow relation with state places
		addFlowRelationship(net, ((DataObjectConversionContext) c).dataObjPlaces.get(dataObjectLabel+"_"+preState), t);
		addFlowRelationship(net, t,((DataObjectConversionContext) c).dataObjPlaces.get(dataObjectLabel+"_"+preState));
		if (c.ancestorHasExcpH)
			handleExceptions(net, task, t, c);
		
		for (IntermediateEvent event: task.getAttachedEvents())
			handleAttachedIntermediateEventForTask(net, event, c);
	}
	@Override
	protected void handleTask(PetriNet net, Task task, ConversionContext c) {
		if (!hasData(task))
			super.handleTask(net, task, c);
		else
		{
//			 start with adding transitions and flow relations
			List<Edge> in,out;
			DiagramObject source, target;
			String preState, postState;
			boolean wasRead;
			for(String doblabel:((DataObjectConversionContext)c).dataObjStates.keySet())
			{
					wasRead = false;
					in = task.getIncomingEdges();
					out = task.getOutgoingEdges();
					source = target = null;
					for (Edge inEdge : in)
					{
						if (!(inEdge instanceof Association))
							continue;
						source = inEdge.getSource();
						if (source instanceof DataObject)
						{
							if(((DataObject) source).getLabel().equals(doblabel))
							{
								wasRead = true;
								//look if there is an outgoing association for the same object
								preState = ((DataObject) source).getState();
								for(Edge outEdge: out)
								{
									if (!(outEdge instanceof Association))
										continue;
									target = outEdge.getTarget();
									if(((DataObject) target).getLabel().equals(doblabel))
									{
										// we need to connect places
										postState = ((DataObject) target).getState();
										if (!postState.equals(preState)) // this is an update
										{
											if (preState.length() == 0) // this is an unconditional update
											{
												handleUnconditionalUpdate(net, task, doblabel, c, postState);
											}
											else // this is a conditional update
											{
												handleConditionalUpdate(net, task, doblabel, c, preState, postState);
												
											}
										}
										else // they are equal this is a read-only
										{
											if (preState.length() > 0) // a bidirectional flow between activity transition and the state place
											{
												handleConditionalRead(net, task, doblabel, c, preState);
											}
										}
									}
									else
										target = null; // reset so that we know if we found a match or not
									
								}
								if (target == null) // this is a read only to the data object
								{
									
									if (preState.length() > 0) // this is the case of conditional read only
									{
										handleConditionalRead(net, task, doblabel, c, preState);
										
									}
								}
							}
							else
								source = null;
						}
		
						
					}
					if (!wasRead)
					{
						for(Edge outEdge: out)
						{
							if (!(outEdge instanceof Association))
								continue;
							target = outEdge.getTarget();
							if(((DataObject) target).getLabel().equals(doblabel))
							{
								// we need to connect places
								postState = ((DataObject) target).getState();
								handleUnconditionalUpdate(net, task, doblabel, c, postState);
							}
						}
					}
				}
			
		}

	}
	
	protected boolean hasData(Node node) {
		for (Edge e: node.getIncomingEdges()) {
			if (e instanceof Association && e.getSource() instanceof DataObject)
				return true;
		}
		for (Edge e: node.getOutgoingEdges()) {
			if (e instanceof Association && e.getTarget() instanceof DataObject)
				return true;
		}
		return false;
	}

	protected class DataObjectConversionContext extends ConversionContext {
		Map<String, Place> dataObjPlaces = new HashMap<String,Place>();
		Map<String, ArrayList<String>> dataObjStates = new HashMap<String, ArrayList<String>>();
	}

	@Override
	protected void handleDataObjects(PetriNet net, ConversionContext c) throws DataObjectNoInitStateException 
	{
		// attention: different data objects might refer to the same object at runtime!
		//diagram.getDataObjects()
		// TODO implement
		// Ahmed Awad 27.02.2008
		//1. Determine the set of data objects
		//2. for each data object identify the set of states
		//3. for each data object identify the initial state
		//4. if multiple initial state -> add the dummy state
		//5. if no initial state -> terminate with error

		// Identifying the set of states for each data object
		
		
		//ArrayList<DataObjectStates> processedDataObjects = new ArrayList<DataObjectStates>();
		for (DataObject dto: diagram.getDataObjects())
		{
			if (((DataObjectConversionContext) c).dataObjStates.containsKey(dto.getLabel())) // the data object is already there
			{	
				String st = dto.getState();
				if (!((DataObjectConversionContext) c).dataObjStates.get(dto.getLabel()).contains(st))
					((DataObjectConversionContext) c).dataObjStates.get(dto.getLabel()).add(st);
			}
			else
			{
				ArrayList<String> sts = new ArrayList<String>();
				sts.add(dto.getState());
				((DataObjectConversionContext) c).dataObjStates.put(dto.getLabel(), sts);
			}
		}
		for (String dolabel: ((DataObjectConversionContext) c).dataObjStates.keySet())
		{
			ArrayList<String> states = ((DataObjectConversionContext) c).dataObjStates.get(dolabel);
			
			for(String stlabel: states)
			{
				Place p = addPlace(net, dolabel+"_"+stlabel);
				((DataObjectConversionContext)c).dataObjPlaces.put(dolabel+"_"+stlabel, p);
			}
		}
		

			
		
	}
	

}
