package de.hpi.bpmn2xpdl;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.xmappr.Xmappr;

public class XPDLXPDLVersionTest extends TestCase {
	private String xpdl = "<XPDLVersion>2.1</XPDLVersion>";

	public void testParse() throws JSONException {
		XPDLXPDLVersion version = new XPDLXPDLVersion();
	
		StringWriter writer = new StringWriter();
	
		Xmappr xmappr = new Xmappr(XPDLXPDLVersion.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(version, writer);
	
		assertEquals(xpdl, writer.toString());
	}
}
