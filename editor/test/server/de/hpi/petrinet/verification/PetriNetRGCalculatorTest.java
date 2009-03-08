package de.hpi.petrinet.verification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.verification.PTNetInterpreter;
import de.hpi.diagram.reachability.ReachabilityGraph;
import de.hpi.petrinet.AbstractPetriNetTest;
import de.hpi.petrinet.Marking;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class PetriNetRGCalculatorTest extends AbstractPetriNetTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test public void testCalculateWithSimpleNet(){
		// Petri net taken from BPM Weske book
		PetriNet net = new PTNet();
		Place pClaim = createPlace(net, "claim");
		Place pUnderConsideration = createPlace(net, "underConsideration");
		Place pReady = createPlace(net, "ready");
		Transition tPay = createTransition(net);
		Transition tRecord= createTransition(net);
		Transition tSendLetter = createTransition(net);
		
		createFlowRelationship(net, pClaim, tRecord);
		createFlowRelationship(net, tRecord, pUnderConsideration);
		createFlowRelationship(net, pUnderConsideration, tPay);
		createFlowRelationship(net, pUnderConsideration, tSendLetter);
		createFlowRelationship(net, tPay, pReady);
		createFlowRelationship(net, tSendLetter, pReady);
		
		PetriNetRGCalculator rgCalc = new PetriNetRGCalculator(net, new PTNetInterpreter());
		ReachabilityGraph<PetriNet, Transition, Marking> rg = rgCalc.calculate();
		
		assertEquals(3, rg.getMarkings().size());
		assertEquals(1, rg.getRoots().size());
		assertEquals(1, rg.getRoots().get(0).getNumTokens());
		assertEquals(1, rg.getRoots().get(0).getNumTokens(pClaim));
		assertEquals(1, rg.getLeaves().size());
		assertEquals(1, rg.getLeaves().get(0).getNumTokens());
		assertEquals(1, rg.getLeaves().get(0).getNumTokens(pReady));
	}
	
	@Test public void testCalculateWithLoop(){
		// Petri net taken from BPM Weske book
		PetriNet net = new PTNet();
		Place pI = createPlace(net, "i");
		Place p1 = createPlace(net, "p1");
		Place p2 = createPlace(net, "p2");
		Place p3 = createPlace(net, "p3");
		Place pO = createPlace(net, "o");
		Transition tA = createTransition(net, "tA");
		Transition tB1= createTransition(net, "tB1");
		Transition tB2= createTransition(net, "tB2");
		Transition tC = createTransition(net, "tC");
		Transition tD = createTransition(net, "tD");
		
		// Simple sequence
		createFlowRelationship(net, pI, tA);
		createFlowRelationship(net, tA, p1);
		createFlowRelationship(net, p1, tB1);
		createFlowRelationship(net, tB1, p3);
		createFlowRelationship(net, p3, tD);
		createFlowRelationship(net, tD, pO);
		// Small loop back from tB to p1
		createFlowRelationship(net, p1, tB2);
		createFlowRelationship(net, tB2, p2);
		createFlowRelationship(net, p2, tC);
		createFlowRelationship(net, tC, p1);
		
		PetriNetRGCalculator rgCalc = new PetriNetRGCalculator(net, new PTNetInterpreter());
		ReachabilityGraph<PetriNet, Transition, Marking> rg = rgCalc.calculate();

		// Root has exactly one successor
		assertEquals(1, rg.getSuccessors(rg.getRoots().get(0)).size());
		assertNotNull(rg.getSuccessor(rg.getRoots().get(0), tA));
		
		// tA => tB2 => tC is same (not only equal!) marking as tA
		Marking mAfterTA = rg.getSuccessor(rg.getRoots().get(0), tA);
		Marking mAfterTB2 = rg.getSuccessor(mAfterTA, tB2);
		Marking mAfterTC = rg.getSuccessor(mAfterTB2, tC);
		assertTrue(mAfterTA == mAfterTC);
	}
}
