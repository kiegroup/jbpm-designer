package de.hpi.bpmn2xpdl;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLCoordinatesTest extends TestCase {

	private String json = "{" +
		"\"x\":\"100\"," +
		"\"y\":\"100\"," +
		"}";
	private String xpdl = "<Coordinates XCoordinate=\"100.0\" YCoordinate=\"100.0\" />";

	public void testParse() throws JSONException {
		XPDLCoordinates coordinates = new XPDLCoordinates();
		coordinates.parse(new JSONObject(json));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLCoordinates.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(coordinates, writer);

		assertEquals(xpdl, writer.toString());
	}
}
