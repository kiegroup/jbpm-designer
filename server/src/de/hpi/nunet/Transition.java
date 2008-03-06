/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet;


public interface Transition extends Node {

	/**
	 * 
	 * @return true if outgoing flow connection has a "new" assigned, else false
	 */
	boolean createsName();

	boolean isCommunicationTransition();

} // Transition