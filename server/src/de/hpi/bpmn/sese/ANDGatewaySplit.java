/**
 * 
 */
package de.hpi.bpmn.sese;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.analysis.BPMNSESENormalizer;

/**
 * This class represents an {@link ANDGateway} with a splitting nature. 
 * 
 * It is used by the {@link BPMNSESENormalizer} and export to BPEL.
 * 
 * @author Sven Wagner-Boysen
 */
public class ANDGatewaySplit extends ANDGateway implements Split {
	

	public SequenceFlow getIncomingSequenceFlow() {
		if(this.getIncomingSequenceFlows().size() != 1) {
			return null;
		}
		return this.getIncomingSequenceFlows().get(0);
	}
}
