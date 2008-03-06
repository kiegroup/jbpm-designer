/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet;


public interface NuNetFactory {

	NuNetFactory eINSTANCE = de.hpi.nunet.impl.NuNetFactoryImpl.init();

	NuNet createNuNet();

	Place createPlace();

	Transition createTransition();

	FlowRelationship createFlowRelationship();

	Token createToken();

	InterconnectionModel createInterconnectionModel();

	ProcessModel createProcessModel();
	
	Marking createMarking(NuNet net);

} //NuNetFactory
