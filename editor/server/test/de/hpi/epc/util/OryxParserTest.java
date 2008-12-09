package de.hpi.epc.util;


import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IFlowObject;
import de.hpi.epc.AbstractEPCTest;

public class OryxParserTest extends AbstractEPCTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		epc = openEpcFromFile("soundnessTestEpc.rdf");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	@Ignore("ids have to be adjusted to example!!")
	public void testControlFlows(){
		boolean orConn1Found = false;
		boolean orConn2Found = false;
		boolean orConn3Found = false;
		for(IFlowObject node : (Collection<IFlowObject>)epc.getFlowObjects()){
			if(node.getId().equals("oryx_694B77A9-5D1A-44C2-A41F-47884A88C7F2")){
				orConn1Found = true;
				testIds(epc.getOutgoingControlFlow(node), "oryx_33F53D90-8292-483A-9F48-9C4327E61F3E", "oryx_7F73B581-9EB7-45CC-A94B-947E04F82AAA");
				testIds(epc.getIncomingControlFlow(node), "oryx_E72C9CFA-B163-4655-94A9-9EC9D52419B2");
			} else if(node.getId().equals("oryx_ED73D3B0-E40E-48F6-9016-3FC4EF4F28CD")){
				orConn2Found = true;
				testIds(epc.getIncomingControlFlow(node), "oryx_64C72D6A-9630-41EC-9A28-CAE29598BD30", "oryx_38D89B01-EAF8-4475-AFF9-36ABA9C12588");				
				testIds(epc.getOutgoingControlFlow(node), "oryx_E72C9CFA-B163-4655-94A9-9EC9D52419B2");
			} else if(node.getId().equals("oryx_46BFB3D1-0EFD-41E7-B2D2-F6BD7BC4A519")){
				orConn3Found = true;
				testIds(epc.getIncomingControlFlow(node), "oryx_ACF2941C-33A7-4378-BFBF-CFFD4885D30E", "oryx_D26A4EDD-E1F7-4A7C-AC28-A2B8ED0F25E7");				
				testIds(epc.getOutgoingControlFlow(node), "oryx_D5D028B0-1227-4B7C-8ACF-582208C2CEB6");
			}
		}
		
		assertTrue(orConn1Found);
		assertTrue(orConn2Found);
		assertTrue(orConn3Found);
	}
	
	protected void testIds(Collection<IControlFlow> cfs, String id){
		assertTrue(cfs.size() == 1);
		
		assertTrue(cfs.iterator().next().getId().equals(id));
	}
	
	protected void testIds(Collection<IControlFlow> cfs, String id1, String id2){
		assertTrue(cfs.size() == 2);
		
		Iterator<IControlFlow> it = cfs.iterator();
		IControlFlow cf1 = it.next();
		IControlFlow cf2 = it.next();
		
		assertTrue((cf1.getId().equals(id1) && cf2.getId().equals(id2)) || (cf2.getId().equals(id1) && cf1.getId().equals(id2)));
	}
}
