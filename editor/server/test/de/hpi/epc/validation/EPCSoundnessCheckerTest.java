package de.hpi.epc.validation;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.epc.AbstractEPCTest;

public class EPCSoundnessCheckerTest extends AbstractEPCTest {
	IEPC epc;
	EPCSoundnessChecker soundnessChecker;
	IEPC epcWithLoop;
	EPCSoundnessChecker epcWithLoopSC;

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
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testIsSound(){
		System.out.println(epcWithLoopSC.badEndArcs.size());
		System.out.println(epcWithLoopSC.badStartArcs.size());
		
		assertTrue(soundnessChecker.isSound());
		assertTrue(epcWithLoopSC.isSound());
	}
	
	@Test
	public void testCheck(){
		assertTrue(soundnessChecker.badEndArcs.size() == 0);
		assertTrue(soundnessChecker.badStartArcs.size() == 0);
		
		
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
	}
}
