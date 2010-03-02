package de.hpi.epc.stepthrough;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import util.PrivateAccessor;
import de.hpi.bpt.process.epc.Connector;
import de.hpi.bpt.process.epc.ConnectorType;
import de.hpi.bpt.process.epc.EPC;
import de.hpi.bpt.process.epc.Event;
import de.hpi.bpt.process.epc.Function;
import de.hpi.bpt.process.epc.IFlowObject;
import de.hpi.epc.AbstractEPCTest;
import java.util.Arrays;

public class EPCStepThroughInterpreterTest extends AbstractEPCTest {
	EPCStepThroughInterpreter epcST;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		epc = new EPC();
		epcST = new EPCStepThroughInterpreter(epc); 
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test public void testSetInitialMarking(){
		EPC epc = new EPC();
		
		Event e1 = new Event();
		epc.addFlowObject(e1);
		
		Event e2 = new Event();
		epc.addFlowObject(e2);
		
		Event e3 = new Event();
		epc.addFlowObject(e3);
		
		Connector xor = new Connector(ConnectorType.AND);
		epc.addFlowObject(xor);
		
		Function f1 = new Function();
		epc.addFlowObject(f1);
		
		Function f2 = new Function();
		epc.addFlowObject(f2);
		
		epc.addControlFlow(e1, xor);
		epc.addControlFlow(xor, e2);
		epc.addControlFlow(xor, e3);
		epc.addControlFlow(e2, f1);
		epc.addControlFlow(e3, f2);
		
		EPCStepThroughInterpreter interpreter = new EPCStepThroughInterpreter(epc);
		String[] resources = {e1.getId()};
		interpreter.setInitialMarking(Arrays.asList(resources));
		assertEquals(2, interpreter.getFireableNodes().size());
		assertTrue(interpreter.getFireableNodes().contains(f1));
		assertTrue(interpreter.getFireableNodes().contains(f2));
	}
	
	@Test public void testShouldBeAutomaticallyExecuted(){
		assertTrue(invokeShouldBeAutomaticallyExecuted(new Event()));
		assertFalse(invokeShouldBeAutomaticallyExecuted(new Function()));
		
		//TODO use mocking library!!! And complete tests!!!
		// Connector joins
		IFlowObject f1 = add(new Function());
		IFlowObject f2 = add(new Function());
		IFlowObject orSplit = add(new Connector(ConnectorType.OR));
		connect(orSplit, f1);
		connect(orSplit, f2);
		
		assertFalse(invokeShouldBeAutomaticallyExecuted(orSplit));
	}
	
	private boolean invokeShouldBeAutomaticallyExecuted(IFlowObject node){
		Object[] params = {node};
		return (Boolean)PrivateAccessor.invokePrivateMethod(epcST, "shouldBeAutomaticallyExecuted", params);
	}
}
