package de.hpi.bpmn2xpdl;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.xmappr.Xmappr;

public class XPDLImplementationTest extends TestCase {

	private String xpdl = "<Implementation />";

	public void testParse() throws JSONException {
		XPDLImplementation implementation = new XPDLImplementation();

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLImplementation.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(implementation, writer);

		assertEquals(xpdl, writer.toString());
	}
}
