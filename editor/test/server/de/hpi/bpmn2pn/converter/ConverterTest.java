package de.hpi.bpmn2pn.converter;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.IntermediateTimerEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.Task;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.serialization.BPMNDiagramBuilder;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.verification.PetriNetGraphAlgorithms;

public class ConverterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}
	
	@Test public void testAttachedIntermediateEvents() {
		BPMNDiagram diag = new BPMNDiagram();
		
		/*
		 * Build up a subprocess with attached intermediate event,
		 * 1 start + end event and one task
		 */
		SubProcess subprocess = BPMNDiagramBuilder.<SubProcess>addNode(diag, new SubProcess());
		StartPlainEvent subStart = BPMNDiagramBuilder.<StartPlainEvent>addNode(subprocess, new StartPlainEvent());
		EndPlainEvent subEnd = BPMNDiagramBuilder.<EndPlainEvent>addNode(subprocess, new EndPlainEvent());
		Task subTask = BPMNDiagramBuilder.<Task>addNode(subprocess, new Task());
		
		BPMNDiagramBuilder.connectNodes(diag, subStart, subTask);
		BPMNDiagramBuilder.connectNodes(diag, subTask, subEnd);
		
		IntermediateTimerEvent timer = BPMNDiagramBuilder.<IntermediateTimerEvent>addNode(subprocess, new IntermediateTimerEvent());
		timer.setActivity(subprocess);
		
		/*
		 * Embed subprocess into 1 start + end event + xor-join
		 */
		StartPlainEvent start = BPMNDiagramBuilder.<StartPlainEvent>addNode(diag, new StartPlainEvent());
		EndPlainEvent end = BPMNDiagramBuilder.<EndPlainEvent>addNode(diag, new EndPlainEvent());
		XORDataBasedGateway xorJoin = BPMNDiagramBuilder.<XORDataBasedGateway>addNode(diag, new XORDataBasedGateway());
		
		BPMNDiagramBuilder.connectNodes(diag, start, subprocess);
		BPMNDiagramBuilder.connectNodes(diag, timer, xorJoin);
		BPMNDiagramBuilder.connectNodes(diag, subprocess, xorJoin);
		BPMNDiagramBuilder.connectNodes(diag, xorJoin, end);
		
		/*
		 * Convert
		 */
		StandardConverter converter = new StandardConverter(diag);
		PetriNet net = converter.convert();
		
		/*
		 * Start tests
		 */
		assertTrue(PetriNetGraphAlgorithms.checkFlowRelationShipsConnected(net));
		assertTrue(PetriNetGraphAlgorithms.checkAlternatingTransitionsAndPlaces(net));
		assertTrue(PetriNetGraphAlgorithms.checkUniqueIds(net));
	}
	
	@After
	public void tearDown() throws Exception {
	}

}
