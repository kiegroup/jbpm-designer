package de.hpi.bpmn2xpdl;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.xmappr.Xmappr;

public class XPDLConformanceClassTest extends TestCase {

	private String xpdl = "<ConformanceClass GraphConformance=\"NON-BLOCKED\" BPMNModelPortabilityConformance=\"STANDARD\" />";

	public void testParse() throws JSONException {
		XPDLConformanceClass conformance = new XPDLConformanceClass();

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLConformanceClass.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(conformance, writer);

		assertEquals(xpdl, writer.toString());
	}
}
