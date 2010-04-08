package de.hpi.petrinet.layouting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.hpi.petrinet.Node;
import de.hpi.petrinet.NodeImpl;

public class PetriNetLayoutMatrixTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	protected Node node;
	protected PetriNetLayoutMatrix matrix;
	
	@Before
	public void setUp() throws Exception {
		matrix = new PetriNetLayoutMatrix();
		node = new NodeImpl();
		matrix.set(2, 2, node);
		matrix.set(20, 20, node);
		matrix.set(200, 200, node);
	}
	
	@Test public void testGet(){		
		assertEquals(matrix.get(2, 2), node);
		assertEquals(matrix.get(20, 20), node);
		assertEquals(matrix.get(200, 200), node);
		assertEquals(matrix.get(2, 2), node);
		assertEquals(matrix.get(201, 201), null);
		assertEquals(matrix.get(400, 400), null);
	}
	
	@Test public void testContains(){
		assertTrue(matrix.contains(node));
		assertFalse(matrix.contains(new NodeImpl()));
	}
	
	@Test public void testSizeRows(){
		assertEquals(201, matrix.sizeRows);
		matrix.set(400, 200, new NodeImpl());
		assertEquals(401, matrix.sizeRows);
	}
	
	@Test public void testSizeCols(){
		assertEquals(201, matrix.sizeCols);
		matrix.set(400, 300, new NodeImpl());
		assertEquals(301, matrix.sizeCols);
	}
}
