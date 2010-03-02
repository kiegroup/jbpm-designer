/**
 * 
 */
package de.hpi.bpmn.sese;

import java.util.List;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.analysis.BPMNSESENormalizer;

/**
 * This class represents an {@link ANDGateway} with a joining nature. 
 * 
 * It is used by the {@link BPMNSESENormalizer} and export to BPEL.
 * 
 * @author Sven Wagner-Boysen
 */
public class ANDGatewayJoin extends ANDGateway implements Join {
	
	public SequenceFlow getOutgoingSequenceFlow() {
		if(this.getIncomingSequenceFlows().size() != 1) {
			return null;
		}
		return this.getIncomingSequenceFlows().get(0);
	}

}
