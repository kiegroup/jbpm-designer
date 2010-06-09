package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLAuthorTest extends TestCase {
	
	private String json = "{" +
		"\"author\":\"Max Mustermann\"" +
		"}";
	private String xpdl = "<Author>Max Mustermann</Author>";
	
	public void testParse() throws JSONException {
		XPDLAuthor newAuthor = new XPDLAuthor();
		newAuthor.parse(new JSONObject(json));
		
		StringWriter writer = new StringWriter();
		
		Xmappr xmappr = new Xmappr(XPDLAuthor.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(newAuthor, writer);
		
		assertEquals(xpdl, writer.toString());
	}
	
	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);
		
		Xmappr xmappr = new Xmappr(XPDLAuthor.class);
		XPDLAuthor newAuthor = (XPDLAuthor) xmappr.fromXML(reader);
		
		JSONObject importObject = new JSONObject();
		newAuthor.write(importObject);
		
		assertEquals(json, importObject.toString());
	}
}
