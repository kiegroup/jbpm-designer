package org.b3mn.poem;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class IdentityTest {
	
	@Test public void instanciate() {
		String test = "openid";
		
		assertEquals(Identity.instance(test).getUri(), test);
	}
	
	@Test public void getAcccess() {
		String uri = "/data/model/10";
		List<Access> access = Identity.instance(uri).getAccess();
		assertEquals(2, access.size());
	}
	
	@Test public void access() {
		String term = "owner", openid = "http://ole.myopenid.com/", uri = "/data/model/10", rel="/self";
		Identity model = Identity.instance(uri);
		Access access = model.access(openid,rel);
		String new_Term = access.getAccess_term();

		assertEquals(term, new_Term);
	}

}
