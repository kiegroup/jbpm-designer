package de.hpi.epc;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import de.hpi.bpt.process.epc.Connector;
import de.hpi.bpt.process.epc.ControlFlow;
import de.hpi.bpt.process.epc.EPC;
import de.hpi.bpt.process.epc.Event;
import de.hpi.bpt.process.epc.Function;
import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IFlowObject;
import de.hpi.epc.Marking.NodeNewMarkingPair;

public class MarkingTest extends AbstractEPCTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	public void testOrSplit(){
		epc = new EPC();
		
		IFlowObject e1 = add(new Event());
		IFlowObject f1 = add(new Function());
		connect(e1, f1);
		IFlowObject orSplit = add(new Connector(de.hpi.bpt.process.epc.ConnectorType.OR));
		connect(f1, orSplit);
		IFlowObject f2 = add(new Function());
		connect(orSplit, f2);
		IFlowObject f3 = add(new Function());
		connect(orSplit, f3);

		
		List<IFlowObject> startNodes = new LinkedList<IFlowObject>();
		startNodes.add(e1);
		Marking intialMarking = Marking.getInitialMarking(epc, startNodes);
		List<NodeNewMarkingPair> newMarkingPairs = intialMarking.propagate(epc);
		
		assertTrue(newMarkingPairs.size() == 1);
		assertEquals(newMarkingPairs.get(0).node, f1);
		
		newMarkingPairs = newMarkingPairs.get(0).newMarking.propagate(epc);
		
		assertTrue(newMarkingPairs.size() == 3);
		for(NodeNewMarkingPair nodeNewMarking : newMarkingPairs){
			assertEquals(nodeNewMarking.node, orSplit);
		}
	}
	
	public void testOrJoin(){
		epc = new EPC();
		
		IFlowObject e1 = add(new Event());
		IFlowObject f1 = add(new Function());
		connect(e1, f1);
		IFlowObject e2 = add(new Event());
		IFlowObject f2 = add(new Function());
		connect(e2, f2);
		IFlowObject orJoin = add(new Connector(de.hpi.bpt.process.epc.ConnectorType.OR));
		connect(f1, orJoin);
		connect(f2, orJoin);
		IFlowObject f3 = add(new Function());
		connect(orJoin, f3);

		// Comb 1: Only e1
		List<IFlowObject> startNodes = new LinkedList<IFlowObject>();
		startNodes.add(e1);
		Marking intialMarking = Marking.getInitialMarking(epc, startNodes);
		List<NodeNewMarkingPair> newMarkingPairs = intialMarking.propagate(epc);
		
		assertTrue(newMarkingPairs.size() == 1);
		assertEquals(newMarkingPairs.get(0).node, f1);
		
		newMarkingPairs = newMarkingPairs.get(0).newMarking.propagate(epc);
		
		assertTrue(newMarkingPairs.size() == 1);
		assertEquals(newMarkingPairs.get(0).node, orJoin);
		
		// Comb 2: e1 + e2
		//TODO
	}
	
	public void testEquals(){
		IControlFlow cf = new ControlFlow(new Event(), new Event());
		cf.setId("blub");
		
		Marking m1 = new Marking();
		m1.applyContext(cf, Marking.Context.DEAD);
		m1.applyState(cf, Marking.State.NEG_TOKEN);
		
		Marking m2 = new Marking();
		m2.applyContext(cf, Marking.Context.DEAD);
		m2.applyState(cf, Marking.State.NEG_TOKEN);
		
		Marking m3 = m2.clone();
		m3.applyContext(cf, Marking.Context.WAIT);
		
		assertTrue(m1.equals(m2));
		assertTrue(m2.equals(m1));
		assertFalse(m1.equals(m3));
		assertFalse(m1.equals(new Marking()));
		
		List<Marking> list = new LinkedList<Marking>();
		list.add(m2);
		assertTrue(list.contains(m2));
		assertTrue(list.contains(m1));
		assertFalse(list.contains(new Marking()));
		assertFalse(list.contains(m3));
	}
}
