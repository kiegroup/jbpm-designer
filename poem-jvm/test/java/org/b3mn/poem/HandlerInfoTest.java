/**
 * 
 */
package org.b3mn.poem;

import static org.junit.Assert.*;

import org.b3mn.poem.handler.HandlerBase;
import org.b3mn.poem.mock.TestHandlerWithModelContext;
import org.b3mn.poem.mock.TestHandlerWithoutModelContext;
import org.b3mn.poem.util.AccessRight;
import org.b3mn.poem.util.HandlerInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author nico
 *
 */
public class HandlerInfoTest {
	
	private HandlerInfo handlerInfoWMC;
	private HandlerInfo handlerInfoWOMC;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		handlerInfoWMC = new HandlerInfo(TestHandlerWithModelContext.class);
		handlerInfoWOMC = new HandlerInfo(TestHandlerWithoutModelContext.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		handlerInfoWMC = null;
		handlerInfoWOMC = null;
	}

	/**
	 * Test method for {@link org.b3mn.poem.util.HandlerInfo#getFilterMapping()}.
	 
	@Test
	public final void testGetFilterMapping() {
		fail("Not yet implemented"); // TODO
	}
	*/
	
	/**
	 * Test method for {@link org.b3mn.poem.util.HandlerInfo#getSortMapping()}.
	 
	@Test
	public final void testGetSortMapping() {
		fail("Not yet implemented"); // TODO
	}
	*/
	
	/**
	 * Test method for {@link org.b3mn.poem.util.HandlerInfo#getExportInfo()}.
	 */
	@Test
	public final void testGetExportInfo() {
		assertNull(handlerInfoWMC.getExportInfo());
		assertNull(handlerInfoWOMC.getExportInfo());
	}

	/**
	 * Test method for {@link org.b3mn.poem.util.HandlerInfo#getUri()}.
	 */
	@Test
	public final void testGetUri() {
		String result = handlerInfoWMC.getUri();
		assertTrue("URI: " + result, "/test".equals(result));
		
		result = handlerInfoWOMC.getUri();
		assertTrue("URI: " + result, "/test2".equals(result));
	}

	/**
	 * Test method for {@link org.b3mn.poem.util.HandlerInfo#isNeedsModelContext()}.
	 */
	@Test
	public final void testIsNeedsModelContext() {
		assertTrue(handlerInfoWMC.isNeedsModelContext());
		assertFalse(handlerInfoWOMC.isNeedsModelContext());
	}

	/**
	 * Test method for {@link org.b3mn.poem.util.HandlerInfo#isPermitPublicUserAccess()}.
	 */
	@Test
	public final void testIsPermitPublicUserAccess() {
		assertFalse(handlerInfoWMC.isPermitPublicUserAccess());
		assertFalse(handlerInfoWOMC.isPermitPublicUserAccess());
	}

	/**
	 * Test method for {@link org.b3mn.poem.util.HandlerInfo#isFilterBrowser()}.
	 */
	@Test
	public final void testIsFilterBrowser() {
		assertTrue(handlerInfoWMC.isFilterBrowser());
		assertTrue(handlerInfoWOMC.isFilterBrowser());
	}

	/**
	 * Test method for {@link org.b3mn.poem.util.HandlerInfo#getHandlerClass()}.
	 */
	@Test
	public final void testGetHandlerClass() {
		assertTrue(TestHandlerWithModelContext.class.equals(handlerInfoWMC.getHandlerClass()));
		assertTrue(TestHandlerWithoutModelContext.class.equals(handlerInfoWOMC.getHandlerClass()));
	}

	/**
	 * Test method for {@link org.b3mn.poem.util.HandlerInfo#HandlerInfo(java.lang.String)}.
	 */
	@Test
	public final void testHandlerInfoString() {
		try {
			HandlerInfo hi = new HandlerInfo("org.b3mn.poem.TestHandlerWithModelContext");
			hi = new HandlerInfo("org.b3mn.poem.TestHandlerWithoutModelContext");
		} catch (ClassNotFoundException e) {
			assertTrue(false);
		}
	}

	/**
	 * Test method for {@link org.b3mn.poem.util.HandlerInfo#HandlerInfo(java.lang.Class)}.
	 */
	@Test
	public final void testHandlerInfoClassOfQextendsHandlerBase() {
		HandlerInfo hi = new HandlerInfo(TestHandlerWithModelContext.class);
		assertNotNull(hi);
		
		hi = new HandlerInfo(TestHandlerWithoutModelContext.class);
		assertNotNull(hi);
	}

	/**
	 * Test method for {@link org.b3mn.poem.util.HandlerInfo#getAccessRestriction(java.lang.String)}.
	 */
	@Test
	public final void testGetAccessRestriction() {
		assertTrue(handlerInfoWMC.getAccessRestriction("doGet").toString(), 
				AccessRight.NONE.equals(handlerInfoWMC.getAccessRestriction("doGet")));
		assertTrue(handlerInfoWMC.getAccessRestriction("doPost").toString(), 
				AccessRight.NONE.equals(handlerInfoWMC.getAccessRestriction("doPost")));
		
		assertNull(handlerInfoWOMC.getAccessRestriction("doGet"));
		assertNull(handlerInfoWOMC.getAccessRestriction("doPost"));
	}

}
