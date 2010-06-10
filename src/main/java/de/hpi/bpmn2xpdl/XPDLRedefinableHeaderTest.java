package de.hpi.bpmn2xpdl;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.xmappr.Xmappr;

public class XPDLRedefinableHeaderTest extends TestCase {

	private String xpdl = "<RedefinableHeader />";

	public void testParse() throws JSONException {
		XPDLRedefinableHeader header = new XPDLRedefinableHeader();

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLRedefinableHeader.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(header, writer);

		assertEquals(xpdl, writer.toString());
	}
}
