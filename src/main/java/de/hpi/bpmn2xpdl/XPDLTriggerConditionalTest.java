package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLTriggerConditionalTest extends TestCase {
	private String jsonParse = "{" +
	"\"condition\":\"A and B\"" +
	"}";
	private String jsonWrite = "{" +
		"\"properties\":{" +
			"\"conditionref\":\"A and B\"}" +
		"}"; 
	private String xpdl = "<TriggerConditional>A and B</TriggerConditional>";

	public void testParse() throws JSONException {
		XPDLTriggerConditional trigger = new XPDLTriggerConditional();
		trigger.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTriggerConditional.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(trigger, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTriggerConditional.class);
		XPDLTriggerConditional trigger = (XPDLTriggerConditional) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		trigger.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}
