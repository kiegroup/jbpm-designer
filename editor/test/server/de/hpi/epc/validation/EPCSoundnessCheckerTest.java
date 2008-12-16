package de.hpi.epc.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.epc.AbstractEPCTest;
import de.hpi.epc.Marking;

public class EPCSoundnessCheckerTest extends AbstractEPCTest {
	IEPC epc;
	EPCSoundnessChecker soundnessChecker;
	IEPC epcWithLoop;
	EPCSoundnessChecker epcWithLoopSC;
	IEPC epcWithLoopAndOr;
	EPCSoundnessChecker epcWithLoopAndOrSC;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		epc = openEpcFromFile("simpleEPC.rdf");
		soundnessChecker = new EPCSoundnessChecker(epc);
		soundnessChecker.calculate();
		epcWithLoop = openEpcFromFile("epcWithLoop.rdf");
		epcWithLoopSC = new EPCSoundnessChecker(epcWithLoop);
		epcWithLoopSC.calculate();
		epcWithLoopAndOr = openEpcFromFile("soundnessTestEpc.rdf");
		epcWithLoopAndOrSC = new EPCSoundnessChecker(epcWithLoopAndOr);
		epcWithLoopAndOrSC.calculate();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testIsSound(){
		assertTrue(soundnessChecker.isSound());
		assertTrue(epcWithLoopSC.isSound());
		assertFalse(epcWithLoopAndOrSC.isSound());
	}
	
	@Test public void missings(){
		
	}
	
	@Test
	public void testCheck(){
		List<IControlFlow> badStartArcs = soundnessChecker.badStartArcs;
		List<IControlFlow> badEndArcs = soundnessChecker.badEndArcs;
		
		for(IControlFlow cf : badStartArcs){
			System.out.println("badStartArcs");
			System.out.println(cf.getId());
		}
		for(IControlFlow cf : badEndArcs){
			System.out.println("badEndArcs");
			System.out.println(cf.getId());
		}
		
		System.out.println(epcWithLoopAndOrSC.badStartArcs.size());
		System.out.println(epcWithLoopAndOrSC.badEndArcs.size());
		System.out.println(epcWithLoopAndOrSC.goodInitialMarkings.size());
		System.out.println(epcWithLoopAndOrSC.goodFinalMarkings.size());
		
		//assertEquals(0, epcWithLoopAndOrSC.badEndArcs.size());
		assertEquals(1, epcWithLoopAndOrSC.goodInitialMarkings.size());
		Collection<IControlFlow> goodInitialMarkingArcs = epcWithLoopAndOrSC.goodInitialMarkings.get(0).filterByState(epcWithLoopAndOr.getControlFlow(), Marking.State.POS_TOKEN); 
		assertEquals(1, goodInitialMarkingArcs.size());
		assertTrue(goodInitialMarkingArcs.iterator().next().getId().equals("oryx_9C3AB1F0-3B7A-4BDD-8824-D76EC9CA8A12"));
		assertEquals(1, epcWithLoopAndOrSC.badStartArcs.size());
		assertTrue(epcWithLoopAndOrSC.badStartArcs.get(0).getId().equals("oryx_717CDF61-44E4-45C1-AE9D-29EEE6637AD3"));
		//assertEquals(2, epcWithLoopAndOrSC.goodFinalMarkings.size());
	}
}
