package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLNoTest extends TestCase {
	
	private String json = "{" +
		"\"tasktype\":\"None\"" +
		"}";
	private String xpdl = "<No />";

	public void testParse()  {
		XPDLNo no = new XPDLNo();

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLNo.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(no, writer);

		assertEquals(xpdl, writer.toString());
	}
	
	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLNo.class);
		XPDLNo no = (XPDLNo) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		no.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
