package de.hpi.ibpmn;

import java.util.ArrayList;
import java.util.List;

import de.hpi.bpmn.XORDataBasedGateway;

/**
 * @author Gero.Decker
 */
public class OwnedXORDataBasedGateway extends XORDataBasedGateway implements OwnedNode {
	
	protected List<Pool> owners;

	public List<Pool> getOwners() {
		if (owners == null)
			owners = new ArrayList<Pool>();
		return owners;
	}

}


