package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLDocumentationTest extends TestCase {
	private String json = "{" +
		"\"documentation\":\"Some documentation\"" +
		"}";
	private String xpdl = "<Documentation>Some documentation</Documentation>";

	public void testParse() throws JSONException {
		XPDLDocumentation documentation = new XPDLDocumentation();
		documentation.parse(new JSONObject(json));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLDocumentation.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(documentation, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLDocumentation.class);
		XPDLDocumentation documentation = (XPDLDocumentation) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		documentation.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
