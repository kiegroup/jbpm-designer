package de.hpi.interactionnet.impl;

import de.hpi.interactionnet.Role;

public class RoleImpl implements Role {
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return getName();
	}

}
