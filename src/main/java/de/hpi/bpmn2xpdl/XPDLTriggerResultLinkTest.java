package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLTriggerResultLinkTest extends TestCase {
	
	private String jsonParse = "{" +
		"\"linkid\":\"101010\"" +
		"}";
	private String jsonWrite = "{" +
		"\"properties\":{" +
			"\"linkid\":\"101010\"}" +
		"}";
	private String xpdl = "<TriggerResultLink>101010</TriggerResultLink>";

	public void testParse() throws JSONException {
		XPDLTriggerResultLink trigger = new XPDLTriggerResultLink();
		trigger.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTriggerResultLink.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(trigger, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTriggerResultLink.class);
		XPDLTriggerResultLink trigger = (XPDLTriggerResultLink) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		trigger.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}
