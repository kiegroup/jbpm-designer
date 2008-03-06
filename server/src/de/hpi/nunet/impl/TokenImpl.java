/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hpi.nunet.Token;

public class TokenImpl implements Token {
	
	private List<String >names;

	public List<String> getNames() {
		if (names == null)
			names = new ArrayList();
		return names;
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append('[');
		for (Iterator<String> it3=getNames().iterator(); it3.hasNext(); ) {
			str.append(it3.next());
			if (it3.hasNext()) 
				str.append(',');
		}
		str.append(']');
		return str.toString();
	}

} // Token