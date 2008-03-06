package de.hpi.bpmn2execpn.converter;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.Task;
import de.hpi.bpmn2pn.converter.Converter;
import de.hpi.execpn.ExecPetriNet;
import de.hpi.execpn.impl.ExecPNFactoryImpl;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Transition;

public class ExecConverter extends Converter {
	
	protected String modelURL;

	public ExecConverter(BPMNDiagram diagram, String modelURL) {
		super(diagram, new ExecPNFactoryImpl(modelURL));
		this.modelURL = modelURL;
	}

	@Override
	protected void handleDiagram(PetriNet net, ConversionContext c) {
		((ExecPetriNet)net).setName(diagram.getTitle());
	}

	@Override
	protected void createStartPlaces(PetriNet net, ConversionContext c) {
		// do nothing...: we want start transitions instead of start places
	}

	// TODO this is a dirty hack...
	@Override
	protected void handleTask(PetriNet net, Task task, ConversionContext c) {
		Transition t = addLabeledTransition(net, task.getId(), task.getLabel());
		
		handleMessageFlow(net, task, t, t, c);
		addFlowRelationship(net, c.map.get(getIncomingSequenceFlow(task)), t);
		addFlowRelationship(net, t, c.map.get(getOutgoingSequenceFlow(task)));
		if (c.ancestorHasExcpH)
			handleExceptions(net, task, t, c);
		
		for (IntermediateEvent event: task.getAttachedEvents())
			handleAttachedIntermediateEventForTask(net, event, c);
	}
	
	

}
