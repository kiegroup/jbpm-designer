package de.hpi.ibpmn;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpmn.StartTimerEvent;

/**
 * @author gero.decker
 */
public class OwnedStartTimerEvent extends StartTimerEvent implements OwnedNode {

	protected List<Pool> owners;

	public List<Pool> getOwners() {
		if (owners == null)
			owners = new ArrayList<Pool>();
		return owners;
	}

}


