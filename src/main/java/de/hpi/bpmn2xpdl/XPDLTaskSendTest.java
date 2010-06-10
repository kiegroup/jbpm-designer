package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLTaskSendTest extends TestCase {

	private String jsonParse = "{" +
	"\"implementation\":\"Goes here\"" +
	"}";
	private String jsonWrite = "{" +
	"\"tasktype\":\"Send\"," +
	"\"implementation\":\"Goes here\"" +
	"}";
	private String xpdl = "<TaskSend Implementation=\"Goes here\" />";

	public void testParse() throws JSONException {
		XPDLTaskSend task = new XPDLTaskSend();
		task.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTaskSend.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(task, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTaskSend.class);
		XPDLTaskSend task = (XPDLTaskSend) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		task.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}
