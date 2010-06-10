package de.hpi.bpmn2xpdl;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.xmappr.Xmappr;

public class XPDLVendorTest extends TestCase {
	private String xpdl = "<Vendor>Hasso Plattner Institute</Vendor>";

	public void testParse() throws JSONException {
		XPDLVendor vendor = new XPDLVendor();

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLVendor.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(vendor, writer);

		assertEquals(xpdl, writer.toString());
	}
}
