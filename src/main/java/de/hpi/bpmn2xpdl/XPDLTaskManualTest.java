package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLTaskManualTest extends TestCase {

	private String jsonWrite = "{" +
		"\"tasktype\":\"Manual\"" +
		"}";
	private String xpdl = "<TaskManual />";

	public void testParse() throws JSONException {
		XPDLTaskManual task = new XPDLTaskManual();

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTaskManual.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(task, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTaskManual.class);
		XPDLTaskManual task = (XPDLTaskManual) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		task.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}
