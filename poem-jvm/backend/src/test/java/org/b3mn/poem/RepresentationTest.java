package org.b3mn.poem;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class RepresentationTest {
	@Test public void update() {
		
		String uri = "/data/model/1", title="JUnitTest";
		Identity model = Identity.instance(uri);
		Representation rep = model.read();
		rep.setTitle(title);
		rep.update();
		
		assertEquals(title, rep.getTitle());
		
	}

}
