/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet.impl;

import de.hpi.nunet.FlowRelationship;
import de.hpi.nunet.InterconnectionModel;
import de.hpi.nunet.Marking;
import de.hpi.nunet.Node;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.NuNetFactory;
import de.hpi.nunet.Place;
import de.hpi.nunet.ProcessModel;
import de.hpi.nunet.Token;
import de.hpi.nunet.Transition;


public class NuNetFactoryImpl implements NuNetFactory {

	public NuNet createNuNet() {
		return new NuNetImpl();
	}

	public Node createNode() {
		return new NodeImpl();
	}

	public Place createPlace() {
		return new PlaceImpl();
	}

	public Transition createTransition() {
		return new TransitionImpl();
	}

	public FlowRelationship createFlowRelationship() {
		return new FlowRelationshipImpl();
	}

	public Token createToken() {
		return new TokenImpl();
	}

	public InterconnectionModel createInterconnectionModel() {
		return new InterconnectionModelImpl();
	}

	public ProcessModel createProcessModel() {
		return new ProcessModelImpl();
	}

	public Marking createMarking(NuNet net) {
		return new MarkingImpl(net);
	}

	public static NuNetFactory init() {
		return new NuNetFactoryImpl();
	}

} //NuNetFactory
