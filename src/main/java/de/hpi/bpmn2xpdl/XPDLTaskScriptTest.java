package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLTaskScriptTest extends TestCase {

	private String jsonWrite = "{" +
	"\"tasktype\":\"Script\"" +
	"}";
	private String xpdl = "<TaskScript />";

	public void testParse() throws JSONException {
		XPDLTaskScript task = new XPDLTaskScript();

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTaskScript.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(task, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTaskScript.class);
		XPDLTaskScript task = (XPDLTaskScript) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		task.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}
