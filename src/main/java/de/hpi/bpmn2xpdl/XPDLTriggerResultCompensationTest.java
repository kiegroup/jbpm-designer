package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLTriggerResultCompensationTest extends TestCase {
	private String jsonParse = "{" +
		"\"activity\":\"123456\"" +
		"}";
	private String jsonWrite = "{" +
		"\"properties\":{" +
			"\"activityref\":\"123456\"}" +
		"}";
	private String xpdl = "<TriggerResultCompensation AttributeId=\"123456\" />";

	public void testParse() throws JSONException {
		XPDLTriggerResultCompensation trigger = new XPDLTriggerResultCompensation();
		trigger.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTriggerResultCompensation.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(trigger, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTriggerResultCompensation.class);
		XPDLTriggerResultCompensation trigger = (XPDLTriggerResultCompensation) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		trigger.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}
