package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLModificationDateTest extends TestCase {

	private String json = "{" +
		"\"modificationdate\":\"2010-08-24T00:00:00\"" +
		"}";
	private String xpdl = "<ModificationDate>2010-08-24T00:00:00</ModificationDate>";

	public void testParse() throws JSONException {
		XPDLModificationDate modified = new XPDLModificationDate();
		modified.parse(new JSONObject(json));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLModificationDate.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(modified, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLModificationDate.class);
		XPDLModificationDate modified = (XPDLModificationDate) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		modified.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
