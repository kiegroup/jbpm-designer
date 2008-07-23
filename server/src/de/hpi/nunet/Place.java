/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet;

import java.util.ArrayList;
import java.util.List;


public class Place extends Node {
	
	private List<Token> tokens;

	public List<Token> getTokens() {
		if (tokens == null)
			tokens = new ArrayList();
		return tokens;
	}

	public boolean isCommunicationPlace() {
		return getProcessModel() == null;
	}

	public boolean isInternalPlace() {
		return getProcessModel() != null;
	}

} // Place