/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet;

import java.util.Iterator;


public class Transition extends Node {

	/**
	 * 
	 * @return true if outgoing flow connection has a "new" assigned, else false
	 */
	public boolean createsName() {
		for (Iterator<FlowRelationship> it = getOutgoingFlowRelationships().iterator(); it.hasNext(); ) {
			if (it.next().getVariables().contains(NuNet.NEW))
				return true;
		}
		return false;
	}

	public boolean isCommunicationTransition() {
		for (Iterator<FlowRelationship> iter=getIncomingFlowRelationships().iterator(); iter.hasNext(); ) {
			Place p = (Place)iter.next().getSource();
			if (p.isCommunicationPlace())
				return true;
		}
		for (Iterator<FlowRelationship> iter=getOutgoingFlowRelationships().iterator(); iter.hasNext(); ) {
			Place p = (Place)iter.next().getTarget();
			if (p.isCommunicationPlace())
				return true;
		}
		return false;
	}

} // Transition