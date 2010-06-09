package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLResultErrorTest extends TestCase {

	private String jsonParse = "{" +
		"\"errorcode\":\"101010\"" +
		"}";
	private String jsonWrite = "{" +
		"\"properties\":{" +
			"\"errorcode\":\"101010\"}" +
		"}";
	private String xpdl = "<ResultError ErrorCode=\"101010\" />";

	public void testParse() throws JSONException {
		XPDLResultError trigger = new XPDLResultError();
		trigger.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLResultError.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(trigger, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLResultError.class);
		XPDLResultError trigger = (XPDLResultError) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		trigger.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}