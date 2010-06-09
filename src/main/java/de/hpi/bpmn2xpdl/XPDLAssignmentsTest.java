package de.hpi.bpmn2xpdl;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.xmappr.Xmappr;

public class XPDLAssignmentsTest extends TestCase {
	private String xpdl = "<Assignments />";

	public void testParse() throws JSONException {
		XPDLAssignments assigments = new XPDLAssignments();

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLAssignments.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(assigments, writer);

		assertEquals(xpdl, writer.toString());
	}
}
