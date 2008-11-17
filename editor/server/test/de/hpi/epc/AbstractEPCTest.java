package de.hpi.epc;

import junit.framework.TestCase;
import de.hpi.bpt.process.epc.ControlFlow;
import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.IFlowObject;

public class AbstractEPCTest extends TestCase {
	protected IEPC epc;

	protected IControlFlow connect(IFlowObject source, IFlowObject target){
		IControlFlow cf = new ControlFlow(source, target);
		epc.addControlFlow(cf);
		return cf;
	}
	
	protected IFlowObject add(IFlowObject fo){
		epc.addFlowObject(fo);
		return fo;
	}
}