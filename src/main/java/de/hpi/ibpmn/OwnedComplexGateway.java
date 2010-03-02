package de.hpi.ibpmn;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpmn.ComplexGateway;

/**
 * @author Gero.Decker
 */
public class OwnedComplexGateway extends ComplexGateway implements OwnedNode {

	protected List<Pool> owners;

	public List<Pool> getOwners() {
		if (owners == null)
			owners = new ArrayList<Pool>();
		return owners;
	}

}


