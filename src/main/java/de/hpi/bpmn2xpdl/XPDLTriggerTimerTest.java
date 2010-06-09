package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLTriggerTimerTest extends TestCase {

	private String jsonParse = "{" +
		"\"timecycle\":\"10 weeks\"" +
		"}";
	private String jsonWrite = "{\"properties\":{\"timecycle\":\"10 weeks\"}}";
	private String xpdl = "<TriggerTimer TimerCycle=\"10 weeks\" />";

	public void testParse() throws JSONException {
		XPDLTriggerTimer trigger = new XPDLTriggerTimer();
		trigger.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTriggerTimer.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(trigger, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTriggerTimer.class);
		XPDLTriggerTimer trigger = (XPDLTriggerTimer) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		trigger.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}
