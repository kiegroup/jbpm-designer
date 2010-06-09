package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;


public class XPDLVersionTest  extends TestCase {
	private String json = "{" +
			"\"version\":\"10\"" +
			"}";
	private String xpdl = "<Version>10</Version>";

	public void testParse() throws JSONException {
		XPDLVersion version = new XPDLVersion();
		version.parse(new JSONObject(json));
	
		StringWriter writer = new StringWriter();
	
		Xmappr xmappr = new Xmappr(XPDLVersion.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(version, writer);
	
		assertEquals(xpdl, writer.toString());
	}
	
	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);
		
		Xmappr xmappr = new Xmappr(XPDLVersion.class);
		XPDLVersion version = (XPDLVersion) xmappr.fromXML(reader);
		
		JSONObject importObject = new JSONObject();
		version.write(importObject);
		
		assertEquals(json, importObject.toString());
	}
}
