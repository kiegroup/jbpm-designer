package de.hpi.ibpmn2bpmn.test;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.IBPMNFactory;
import de.hpi.ibpmn2bpmn.IBPMN2BPMNConverter;

/**
 * @author Gero.Decker
 */
public class TestSimple extends AbstractIBPMNTest {
	
	public void testSimple1() throws Exception {
		IBPMNFactory factory = new IBPMNFactory();
		IBPMNDiagram ibpmn = factory.createIBPMNDiagram();
		
		Pool p1 = (Pool)addNode(factory.createPool(), "p1", ibpmn);
		Pool p2 = (Pool)addNode(factory.createPool(), "p2", ibpmn);
	
		Node i1 = addInteraction(factory.createStartInteraction(), "i1", p1, p2, ibpmn);
		Node i2 = addInteraction(factory.createIntermediateInteraction(), "i2", p2, p1, ibpmn);
		Node i3 = addNode(factory.createEndPlainEvent(), "i3", ibpmn);
		
		addSequenceFlow(factory.createSequenceFlow(), (Node)i1, (Node)i2, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), (Node)i2, (Node)i3, ibpmn);
		
		
		BPMNDiagram bpmn = new IBPMN2BPMNConverter(ibpmn).convert();
		
		
		assertEquals(2, bpmn.getChildNodes().size());
		assertEquals(7, bpmn.getEdges().size());
		Pool p = (Pool)bpmn.getChildNodes().get(0);
		assertEquals(1, p.getChildNodes().size());
		Lane l = (Lane)p.getChildNodes().get(0);
		assertEquals(4, l.getChildNodes().size());
		assertTrue(l.getChildNodes().get(0) instanceof StartPlainEvent);
		assertTrue(l.getChildNodes().get(1) instanceof IntermediateMessageEvent);
		assertTrue(l.getChildNodes().get(2) instanceof IntermediateMessageEvent);
		assertTrue(l.getChildNodes().get(3) instanceof EndPlainEvent);

		p = (Pool)bpmn.getChildNodes().get(1);
		assertEquals(1, p.getChildNodes().size());
		l = (Lane)p.getChildNodes().get(0);
		assertEquals(3, l.getChildNodes().size());
	}
	
	public void testSimple2() throws Exception {
		IBPMNFactory factory = new IBPMNFactory();
		IBPMNDiagram ibpmn = factory.createIBPMNDiagram();
		
		Pool p1 = (Pool)addNode(factory.createPool(), "p1", ibpmn);
		Pool p2 = (Pool)addNode(factory.createPool(), "p2", ibpmn);
		Pool p3 = (Pool)addNode(factory.createPool(), "p3", ibpmn);
	
		Node i1 = addInteraction(factory.createStartInteraction(), "i1", p1, p2, ibpmn);
		Node i2 = addInteraction(factory.createIntermediateInteraction(), "i2", p2, p3, ibpmn);
		Node i3 = addNode(factory.createEndPlainEvent(), "i3", ibpmn);
		
		addSequenceFlow(factory.createSequenceFlow(), (Node)i1, (Node)i2, ibpmn);
		addSequenceFlow(factory.createSequenceFlow(), (Node)i2, (Node)i3, ibpmn);
		
		
		BPMNDiagram bpmn = new IBPMN2BPMNConverter(ibpmn).convert();
		
		
		assertEquals(3, bpmn.getChildNodes().size());
		assertEquals(8, bpmn.getEdges().size());
		Pool p = (Pool)bpmn.getChildNodes().get(0);
		assertEquals(1, p.getChildNodes().size());
		Lane l = (Lane)p.getChildNodes().get(0);
		assertEquals(3, l.getChildNodes().size());
		assertTrue(l.getChildNodes().get(0) instanceof StartPlainEvent);
		assertTrue(l.getChildNodes().get(1) instanceof IntermediateMessageEvent);
		assertTrue(l.getChildNodes().get(2) instanceof EndPlainEvent);

		p = (Pool)bpmn.getChildNodes().get(1);
		assertEquals(1, p.getChildNodes().size());
		l = (Lane)p.getChildNodes().get(0);
		assertEquals(3, l.getChildNodes().size());
	}

}


