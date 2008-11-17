package de.hpi.epc.stepthrough;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import util.PrivateAccessor;
import de.hpi.bpt.process.epc.Connector;
import de.hpi.bpt.process.epc.ConnectorType;
import de.hpi.bpt.process.epc.EPC;
import de.hpi.bpt.process.epc.Event;
import de.hpi.bpt.process.epc.Function;
import de.hpi.bpt.process.epc.IFlowObject;
import de.hpi.epc.AbstractEPCTest;

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
	
	public void testShouldBeAutomaticallyExecuted(){
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
