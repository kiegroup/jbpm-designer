package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLScriptTest extends TestCase {

	private String json = "{" +
		"\"expressionlanguage\":\"Javascript\"" +
		"}";
	private String xpdl = "<Script Type=\"Javascript\" />";

	public void testParse() throws JSONException {
		XPDLScript script = new XPDLScript();
		script.parse(new JSONObject(json));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLScript.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(script, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLScript.class);
		XPDLScript script = (XPDLScript) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		script.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
