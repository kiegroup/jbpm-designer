package de.hpi.ibpmn;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpmn.SubProcess;

/**
 * @author gero.decker
 */
public class ComplexInteraction extends SubProcess implements OwnedNode {

	protected List<Pool> owners;

	public List<Pool> getOwners() {
		if (owners == null)
			owners = new ArrayList<Pool>();
		return owners;
	}

}


