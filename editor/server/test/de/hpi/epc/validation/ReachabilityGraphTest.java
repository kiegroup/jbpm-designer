package de.hpi.epc.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.hpi.bpt.process.epc.IEPC;
import de.hpi.epc.AbstractEPCTest;
import de.hpi.epc.Marking;

public class ReachabilityGraphTest extends AbstractEPCTest {
	static public ReachabilityGraph rg;
	static public IEPC epc;
	static public IEPC epcWithLoop;
	static public ReachabilityGraph epcWithLoopRg;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		epc = openEpcFromFile("simpleEPC.rdf");
		rg = new ReachabilityGraph(epc);
		rg.calculate();
		epcWithLoop = openEpcFromFile("epcWithLoop.rdf");
		epcWithLoopRg = new ReachabilityGraph(epcWithLoop);
		epcWithLoopRg.calculate();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		System.out.println("=============");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void calculate() {
		ReachabilityGraph epcWithLoopRg2 = new ReachabilityGraph(epcWithLoop);
		epcWithLoopRg2.calculate();
		
		for(Marking m : rg.getLeaves()){
			assertTrue(m.isFinalMarking(epc));
		}
		for(Marking m : epcWithLoopRg.getLeaves()){
			assertTrue(m.isFinalMarking(epcWithLoop));
		}
		
		// should not have any dublicated markings
		for( Marking m1 : epcWithLoopRg.getMarkings() ){
			for( Marking m2 : epcWithLoopRg.getMarkings() ){
				if(m1 == m2)
					break;
				assertFalse(m1.equals(m2));
			}
		}
	}

	@Test
	public void add() {
	}
	
	@Test public void findByMarking(){
		for (Marking m : epcWithLoopRg.getMarkings()){
			assertNotNull(epcWithLoopRg.findByMarking(m));
		}
		assertNull(epcWithLoopRg.findByMarking(new Marking()));
	}

	@Test
	public void isRoot() {
		for(Marking m : rg.getRoots()){
			assertTrue(rg.isRoot(m));
		}
		for(Marking m : rg.getLeaves()){
			assertFalse(rg.isRoot(m));
		}
	}

	@Test
	public void isLeaf() {
		for(Marking m : rg.getRoots()){
			assertFalse(rg.isLeaf(m));
		}
		for(Marking m : rg.getLeaves()){
			assertTrue(rg.isLeaf(m));
		}
		for(Marking m : epcWithLoopRg.getRoots()){
			assertFalse(epcWithLoopRg.isLeaf(m));
		}
		for(Marking m : epcWithLoopRg.getLeaves()){
			assertTrue(epcWithLoopRg.isLeaf(m));
		}
	}

	@Test
	public void getPredecessors() {
		for(Marking m : rg.getRoots()){
			assertEquals(rg.getPredecessors(m).size(), 0);
		}
		
		Marking initialMarking = rg.getRoots().get(0);
		Marking nextMarking = initialMarking.propagate(epc).get(0).newMarking;
		assertTrue(rg.getPredecessors(nextMarking).contains(initialMarking));
	}

	@Test
	public void getSuccessors() {
		for(Marking m : rg.getLeaves()){
			assertEquals(0, rg.getSuccessors(m).size());
		}
		
		Marking initialMarking = rg.getRoots().get(0);
		Marking nextMarking = initialMarking.propagate(epc).get(0).newMarking;
		assertTrue(rg.getSuccessors(initialMarking).contains(nextMarking));
	}

	@Test
	public void getRoots() {
		assertEquals(1, rg.getRoots().size());
	}

	@Test
	public void getLeaves() {
		assertEquals(2, rg.getLeaves().size());
	}
}
