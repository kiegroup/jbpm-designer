package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLTaskUserTest extends TestCase {

	private String jsonParse = "{" +
		"\"implementation\":\"User\"" +
		"}";
	private String jsonWrite = "{" +
		"\"tasktype\":\"User\"," +
		"\"implementation\":\"User\"" +
		"}";
	private String xpdl = "<TaskUser Implementation=\"User\" />";

	public void testParse() throws JSONException {
		XPDLTaskUser task = new XPDLTaskUser();
		task.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTaskUser.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(task, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTaskUser.class);
		XPDLTaskUser task = (XPDLTaskUser) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		task.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}
