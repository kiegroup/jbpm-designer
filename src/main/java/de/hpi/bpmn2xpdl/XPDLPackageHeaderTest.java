package de.hpi.bpmn2xpdl;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.xmappr.Xmappr;

public class XPDLPackageHeaderTest extends TestCase {

	private String xpdl = "<PackageHeader><Vendor>Hasso Plattner Institute</Vendor><XPDLVersion>2.1</XPDLVersion></PackageHeader>";

	public void testParse() throws JSONException {
		XPDLPackageHeader header = new XPDLPackageHeader();

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLPackageHeader.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(header, writer);

		assertEquals(xpdl, writer.toString());
	}
}
