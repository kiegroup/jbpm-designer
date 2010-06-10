package de.hpi.bpmn.sese;

import java.util.List;

import de.hpi.bpmn.SequenceFlow;

/**
 * This interface ensures that each gateway of split nature has exactly one 
 * outgoing sequence flow and a list of incoming sequence flows.
 * 
 * @author Sven Wagner-Boysen
 * 
 */
public interface Join {
	
	/**
	 * Returns the incoming {@link SequenceFlow} objects.
	 * 
	 * @return
	 * 		A List of the incoming {@link SequenceFlow}
	 */
	public List<SequenceFlow> getIncomingSequenceFlows();
	
	/**
	 * Returns the outgoing {@link SequenceFlow} object.
	 * 
	 * @return
	 * 		The outgoing {@link SequenceFlow}
	 */
	public SequenceFlow getOutgoingSequenceFlow();
	
//	/**
//	 * Sets the outgoing sequence flow
//	 * 
//	 * @param outgoingSequenceFlow
//	 * 		The outgoing {@link SequenceFlow}
//	 */
//	public void setOutgoingSequencFlow(SequenceFlow outgoingSequenceFlow);
}
