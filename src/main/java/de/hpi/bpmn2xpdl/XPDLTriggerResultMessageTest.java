package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLTriggerResultMessageTest extends TestCase {
	private String jsonParse = "{" +
		"\"message\":\"Some messages are listed here\"" +
		"}";
	private String jsonWrite = "{" +
		"\"properties\":{" +
			"\"message\":\"Some messages are listed here\"}" +
		"}";
	private String xpdl = "<TriggerResultMessage><Message>Some messages are listed here</Message></TriggerResultMessage>";

	public void testParse() throws JSONException {
		XPDLTriggerResultMessage trigger = new XPDLTriggerResultMessage();
		trigger.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTriggerResultMessage.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(trigger, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTriggerResultMessage.class);
		XPDLTriggerResultMessage trigger = (XPDLTriggerResultMessage) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		trigger.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}
