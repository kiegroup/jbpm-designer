/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet.impl;

import java.util.Iterator;

import de.hpi.nunet.FlowRelationship;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.Place;
import de.hpi.nunet.Transition;


public class TransitionImpl extends NodeImpl implements Transition {
	
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