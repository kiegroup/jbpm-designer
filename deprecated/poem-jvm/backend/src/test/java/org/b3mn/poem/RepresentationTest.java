package org.b3mn.poem;

import static org.junit.Assert.assertEquals;

import org.b3mn.poem.Identity;
import org.junit.Test;

public class RepresentationTest {
	@Test public void update() {
		
		String uri = "/data/model/1", title="JUnitTest";
		Identity model = Identity.instance(uri);
		Representation.update(model.getId(), title, null, null, null);
		
		Representation rep = model.read();
		assertEquals(title, rep.getTitle());
		
	}

}
