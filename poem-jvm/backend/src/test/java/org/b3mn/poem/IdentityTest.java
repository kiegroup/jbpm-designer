package org.b3mn.poem;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

public class IdentityTest {
	
	@Test public void instanciate() {
		String test = "openid";
		
		assertEquals(Identity.instance(test).getUri(), test);
	}
	
	@Test public void subject() {
		String openid = "http://ole.myopenid.com/";
		Iterator<Access> access = Identity.subject(openid).iterator();
		while(access.hasNext()) {
			Access item = access.next();
			System.err.println(item.getObject_name());
			assertEquals(item.getSubject_name(), openid);
		}
	}
	
	@Test public void object() {
		String uri = "/data/model/10";
		String openid = "http://ole.myopenid.com/";
		Iterator<Access> access = Identity.object(uri).iterator();
		while(access.hasNext()) {
			Access item = access.next();
			if(item.getAccess_term().equalsIgnoreCase("owner"))
				assertEquals(item.getSubject_name(), openid);
		}
	}
	
	@Test public void access() {
		String term = "owner", openid = "http://ole.myopenid.com/", uri = "/data/model/10", rel="self";
		Identity model = Identity.instance(uri);
		Access access = model.access(openid,rel);
		String new_Term = access.getAccess_term();

		assertEquals(term, new_Term);
	}


}
