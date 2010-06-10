/**
 * 
 */
package de.hpi.bpmn.sese;

import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.XOREventBasedGateway;

/**
 * @author Sven Wagner-Boysen
 *
 */
public class XOREventBasedGatewaySplit extends XOREventBasedGateway implements
		Split {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn.sese.Split#getIncomingSequenceFlow()
	 */
	public SequenceFlow getIncomingSequenceFlow() {
		if (this.getIncomingSequenceFlows().size() != 1) {
			return null;
		}
		return this.getIncomingSequenceFlows().get(0);
	}

}
