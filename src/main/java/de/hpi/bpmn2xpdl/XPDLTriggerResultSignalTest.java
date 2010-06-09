package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;


public class XPDLTriggerResultSignalTest extends TestCase {
	private String jsonParse = "{" +
			"\"signalref\":\"aSignalId\"" +
			"}";
	private String jsonWrite = "{\"properties\":{\"signalref\":\"aSignalId\"}}";
	private String xpdl = "<TriggerResultSignal>aSignalId</TriggerResultSignal>";

	public void testParse() throws JSONException {
		XPDLTriggerResultSignal trigger = new XPDLTriggerResultSignal();
		trigger.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTriggerResultSignal.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(trigger, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTriggerResultSignal.class);
		XPDLTriggerResultSignal trigger = (XPDLTriggerResultSignal) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		trigger.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}
