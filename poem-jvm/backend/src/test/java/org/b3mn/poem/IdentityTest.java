package org.b3mn.poem;

import static org.junit.Assert.*;

import org.junit.Test;

public class IdentityTest {
	
	@Test public void instanciate() {
		String test = "openid";
		
		assertEquals(Identity.instance(test).getUri(), test);
	}


}
