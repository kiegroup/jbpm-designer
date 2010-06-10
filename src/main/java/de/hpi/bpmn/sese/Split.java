package de.hpi.bpmn.sese;

import java.util.List;

import de.hpi.bpmn.SequenceFlow;

/**
 * This interface ensures that each gateway of split nature has exactly one 
 * incoming sequence flow and a list of outgoing sequence flows.
 * 
 * @author Sven Wagner-Boysen
 * 
 */
public interface Split {
	
	/**
	 * Returns the outgoing {@link SequenceFlow}.
	 * 
	 * @return 
	 * 		A list of the outgoing {@link SequenceFlow}
	 */
	public List<SequenceFlow> getOutgoingSequenceFlows();
	
	/**
	 * Returns the incoming {@link SequenceFlow} object.
	 * 
	 * @return
	 * 		The incoming {@link SequenceFlow} object.
	 */
	public SequenceFlow getIncomingSequenceFlow();
	
//	/**
//	 * Sets the incoming sequence flow.
//	 * 
//	 * @param sequenceFlow
//	 * 		The incoming sequence flow
//	 */
//	//public void setIncomingSequenceFlow(SequenceFlow sequenceFlow);

}
