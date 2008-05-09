package org.b3mn.poem;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Date;
import org.junit.Test;

public class IdentityTest {
	
	
	@Test public void getAcccess() {
		String uri = "/data/model/10";
		List<Access> access = Identity.instance(uri).getAccess();
		assertEquals(1, access.size());
	}
	
	@Test public void access() {
		String term = "owner", openid = "https://openid.hpi.uni-potsdam.de/user/ole.eckermann", uri = "/data/model/9", rel="/self";
		Identity model = Identity.instance(uri);
		Access access = model.access(openid,rel);
		String new_Term = access.getAccess_term();
		assertEquals(term, new_Term);
	}
	
	@Test public void getModels() {
		String openid = "https://openid.hpi.uni-potsdam.de/user/ole.eckermann";
		List<Representation> models = Identity.instance(openid).getModels("bpmn", new Date(0), new Date(109,0,1), false, false);
		System.err.println(models.size());
		assertEquals("bpmn",models.get(0).getType());
	}
	
	@Test public void userHierarchy() {
		String hierarchy = "U25";
		assertEquals(hierarchy, Identity.instance("https://openid.hpi.uni-potsdam.de/user/ole.eckermann").getUserHierarchy());
		assertEquals("U2", Identity.instance("ownership").getUserHierarchy());
	}
	
	@Test public void ensureUser() {
		String hierarchy = "U25";
		assertEquals(hierarchy, Identity.ensureSubject("https://openid.hpi.uni-potsdam.de/user/ole.eckermann").getUserHierarchy());
	}
	
	@Test public void createAndDeleteInteraction() {
		Identity subject = Identity.instance("https://openid.hpi.uni-potsdam.de/user/hagen.overdick");
		Identity object =  Identity.instance("/data/model/9");
		String term = "write";
		if(Interaction.exist(subject.getUserHierarchy(), object.getModelHierarchy(), term) == null) {
			Interaction right = new Interaction();
		    right.setSubject(subject.getUserHierarchy());
		    right.setObject(object.getModelHierarchy());
		    right.setScheme("http://b3mn.org/http");
		    right.setTerm(term);
		    right.setObject_self(true);
		    right.save();
		    assertEquals(term, object.access(subject.getUri(), "/self").getAccess_term());
		    right.delete();
		}
	}
	
	@Test public void newModelandDeleteModel() {
		Identity owner = Identity.instance("https://openid.hpi.uni-potsdam.de/user/ole.eckermann");
		String title = "New Process";
		String type = "bpmn";
		String mime_type = "application/xhtml+xml";
		String language = "US_en";
		String summary = "JUnit Test Process";
		String content = "<div id=\"oryxcanvas\" class=\"-oryx-canvas\"><span class=\"oryx-mode\">writeable</span><span class=\"oryx-mode\">fullscreen</span><a rel=\"oryx-stencilset\" href=\"/files/stencilsets/bpmn/bpmn.json\"/></div>";
		Identity identity = Identity.newModel(owner, title, type, mime_type, language, summary, content);
		assertEquals(identity.read().getTitle(),title);
		identity.delete();
	}

}
