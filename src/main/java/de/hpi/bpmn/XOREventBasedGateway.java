package de.hpi.bpmn;

import de.hpi.bpmn.serialization.BPMNSerialization;

public class XOREventBasedGateway extends Gateway {

	protected boolean instantiate = false;

	@Override
	public StringBuilder getSerialization(BPMNSerialization serialization) {
		return serialization.getSerializationForDiagramObject(this);
	}
	
	public boolean isInstantiate() {
		return instantiate;
	}

	public void setInstantiate(boolean instantiate) {
		this.instantiate = instantiate;
	}

}
