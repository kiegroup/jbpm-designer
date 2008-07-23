/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet;



public class NuNetFactory {

	public static NuNetFactory eINSTANCE = new NuNetFactory();

	public NuNet createNuNet() {
		return new NuNet();
	}

	public Node createNode() {
		return new Node();
	}

	public Place createPlace() {
		return new Place();
	}

	public Transition createTransition() {
		return new Transition();
	}

	public FlowRelationship createFlowRelationship() {
		return new FlowRelationship();
	}

	public Token createToken() {
		return new Token();
	}

	public InterconnectionModel createInterconnectionModel() {
		return new InterconnectionModel();
	}

	public ProcessModel createProcessModel() {
		return new ProcessModel();
	}

	public Marking createMarking(NuNet net) {
		return new Marking(net);
	}

} //NuNetFactory
