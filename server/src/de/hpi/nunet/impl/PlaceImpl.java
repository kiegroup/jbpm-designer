/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet.impl;

import java.util.ArrayList;
import java.util.List;

import de.hpi.nunet.Place;
import de.hpi.nunet.Token;

public class PlaceImpl extends NodeImpl implements Place {
	
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