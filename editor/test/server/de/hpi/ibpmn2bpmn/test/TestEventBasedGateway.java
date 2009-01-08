package de.hpi.ibpmn2bpmn.test;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.XOREventBasedGateway;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.IBPMNFactory;
import de.hpi.ibpmn.OwnedXORDataBasedGateway;
import de.hpi.ibpmn2bpmn.IBPMN2BPMNConverter;

/**
 * @author Gero.Decker
 */
public class TestEventBasedGateway extends AbstractIBPMNTest {

	public void testSimple1() throws Exception {
		IBPMNFactory factory = new IBPMNFactory();
		IBPMNDiagram ibpmn = factory.createIBPMNDiagram();
		
		Pool p1 = (Pool)addNode(factory.createPool(), "p1", ibpmn);
		Pool p2 = (Pool)addNode(factory.createPool(), "p2", ibpmn);
	
		Node n1 = addNode(factory.createStartPlainEvent(), "n1", ibpmn);
		OwnedXORDataBasedGateway n2 = (OwnedXORDataBasedGateway)addNode(factory.createXORDataBasedGateway(), "n2", ibpmn);
		n2.getOwners().add((de.hpi.ibpmn.Pool)p1);
		Node i1 = addInteraction(factory.createIntermediateInteraction(), "i1", p1, p2, ibpmn);
		Node i2 = addInteraction(factory.createIntermediateInteraction(), "i2", p1, p2, ibpmn);
		Node n3 = addNode(factory.createXORDataBasedGateway(), "n3", ibpmn);
		Node n4 = addNode(factory.createEndPlainEvent(), "n4", ibpmn);
		
		addSequenceFlow(factory.createSequenceFlow(), n1, n2, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), n2, i1, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), n2, i2, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), i1, n3, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), i2, n3, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), n3, n4, ibpmn);
		
		
		BPMNDiagram bpmn = new IBPMN2BPMNConverter(ibpmn).convert();
		
		
		assertEquals(2, bpmn.getChildNodes().size());
		assertEquals(14, bpmn.getEdges().size());
		Pool p; Lane l;
		
		p = (Pool)bpmn.getChildNodes().get(0);
		l = (Lane)p.getChildNodes().get(0);
		assertEquals(6, l.getChildNodes().size());
		assertTrue(l.getChildNodes().get(1) instanceof XORDataBasedGateway);
		
		p = (Pool)bpmn.getChildNodes().get(1);
		l = (Lane)p.getChildNodes().get(0);
		assertEquals(6, l.getChildNodes().size());
		assertTrue(l.getChildNodes().get(1) instanceof XOREventBasedGateway);
	}

	public void testDuplication1() throws Exception {
		fail("DOES NOT WORK");
		/*IBPMNFactory factory = new IBPMNFactory();
		IBPMNDiagram ibpmn = factory.createIBPMNDiagram();
		
		Pool p1 = (Pool)addNode(factory.createPool(), "p1", ibpmn);
		Pool p2 = (Pool)addNode(factory.createPool(), "p2", ibpmn);
	
		Node n1 = addNode(factory.createStartPlainEvent(), "n1", ibpmn);
		OwnedXORDataBasedGateway n2 = (OwnedXORDataBasedGateway)addNode(factory.createXORDataBasedGateway(), "n2", ibpmn);
		n2.getOwners().add((de.hpi.ibpmn.Pool)p1);
		Node i1 = addInteraction(factory.createIntermediateInteraction(), "i1", p1, p2, ibpmn);
		Node i2 = addInteraction(factory.createIntermediateInteraction(), "i2", p2, p1, ibpmn);
		Node n3 = addNode(factory.createXORDataBasedGateway(), "n3", ibpmn);
		Node i3 = addInteraction(factory.createIntermediateInteraction(), "i1", p1, p2, ibpmn);
		Node n4 = addNode(factory.createEndPlainEvent(), "n4", ibpmn);
		
		addSequenceFlow(factory.createSequenceFlow(), n1, n2, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), n2, i1, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), n2, n3, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), i1, i2, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), i2, n3, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), n3, i3, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), i3, n4, ibpmn);
		
		
		BPMNDiagram bpmn = new IBPMN2BPMNConverter(ibpmn).convert();
		
		bpmn.identifyProcesses();
		BPMNSyntaxChecker checker = new BPMNSyntaxChecker(bpmn);
		boolean isOk = checker.checkSyntax();
		assertTrue(checker.getErrors().toString(), isOk);
		
		
		assertEquals(2, bpmn.getChildNodes().size());
		assertEquals(7+8+4, bpmn.getEdges().size()); // 7 sequence flows in p1, 8 sequence flows in p2, 4 message flows
		Pool p; Lane l;
		
//		p = (Pool)bpmn.getChildNodes().get(0);
//		l = (Lane)p.getChildNodes().get(0);
//		assertEquals(6, l.getChildNodes().size());
//		assertTrue(l.getChildNodes().get(1) instanceof XORDataBasedGateway);
		
		p = (Pool)bpmn.getChildNodes().get(1);
		l = (Lane)p.getChildNodes().get(0);
		assertEquals(8, l.getChildNodes().size());
		assertTrue(l.getChildNodes().get(1) instanceof XOREventBasedGateway);*/
	}

	// Example taken from CBP 2007 paper
	public void testSequentialization1() throws Exception {
		fail("DOES NOT WORK");
		/*IBPMNFactory factory = new IBPMNFactory();
		IBPMNDiagram ibpmn = factory.createIBPMNDiagram();
		
		Pool A = (Pool)addNode(factory.createPool(), "A", ibpmn);
		Pool B = (Pool)addNode(factory.createPool(), "B", ibpmn);
		Pool C = (Pool)addNode(factory.createPool(), "C", ibpmn);
	
		Node m1 = addInteraction(factory.createStartInteraction(), "m1", A, B, ibpmn);
		OwnedXORDataBasedGateway xor1 = (OwnedXORDataBasedGateway)addNode(factory.createXORDataBasedGateway(), "n1", ibpmn);
		xor1.getOwners().add((de.hpi.ibpmn.Pool)B);
		Node m2 = addInteraction(factory.createIntermediateInteraction(), "m2", B, A, ibpmn);
		Node m3 = addInteraction(factory.createIntermediateInteraction(), "m3", B, C, ibpmn);
		Node and1 = addNode(factory.createANDGateway(), "n2", ibpmn);
		Node m4 = addInteraction(factory.createIntermediateInteraction(), "m4", C, A, ibpmn);
		Node m5 = addInteraction(factory.createIntermediateInteraction(), "m5", C, B, ibpmn);
		Node m6 = addInteraction(factory.createIntermediateInteraction(), "m6", B, A, ibpmn);
		Node and2 = addNode(factory.createANDGateway(), "n3", ibpmn);
		Node xor2 = addNode(factory.createXORDataBasedGateway(), "n4", ibpmn);
		Node m7 = addInteraction(factory.createIntermediateInteraction(), "m7", A, B, ibpmn);
		Node n5 = addNode(factory.createEndPlainEvent(), "n5", ibpmn);

		addSequenceFlow(factory.createSequenceFlow(), m1, xor1, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), xor1, m2, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), m2, xor2, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), xor1, m3, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), m3, and1, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), and1, m4, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), m4, and2, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), and1, m5, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), m5, m6, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), m6, and2, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), and2, xor2, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), xor2, m7, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), m7, n5, ibpmn);
		
		
		BPMNDiagram bpmn = new IBPMN2BPMNConverter(ibpmn).convert();
		
		bpmn.identifyProcesses();
		BPMNSyntaxChecker checker = new BPMNSyntaxChecker(bpmn);
		boolean isOk = checker.checkSyntax();
		assertTrue(checker.getErrors().toString(), isOk);
		
		
//		assertEquals(2, bpmn.getChildNodes().size());
//		assertEquals(7+8+4, bpmn.getEdges().size()); // 7 sequence flows in p1, 8 sequence flows in p2, 4 message flows
//		Pool p; Lane l;
//		
////		p = (Pool)bpmn.getChildNodes().get(0);
////		l = (Lane)p.getChildNodes().get(0);
////		assertEquals(6, l.getChildNodes().size());
////		assertTrue(l.getChildNodes().get(1) instanceof XORDataBasedGateway);
//		
//		p = (Pool)bpmn.getChildNodes().get(1);
//		l = (Lane)p.getChildNodes().get(0);
//		assertEquals(8, l.getChildNodes().size());
//		assertTrue(l.getChildNodes().get(1) instanceof XOREventBasedGateway);*/
	}

}


