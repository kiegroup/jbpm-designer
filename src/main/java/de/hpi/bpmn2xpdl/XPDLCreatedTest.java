package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;


public class XPDLCreatedTest extends TestCase {
	
	private String json = "{" +
		"\"creationdate\":\"2010-08-24T00:00:00\"" +
		"}";
	private String xpdl = "<Created>2010-08-24T00:00:00</Created>";

	public void testParse() throws JSONException {
		XPDLCreated created = new XPDLCreated();
		created.parse(new JSONObject(json));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLCreated.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(created, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLCreated.class);
		XPDLCreated created = (XPDLCreated) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		created.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
