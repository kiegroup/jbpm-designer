package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLTaskServiceTest extends TestCase {

	private String jsonParse = "{" +
		"\"implementation\":\"Goes here\"" +
		"}";
	private String jsonWrite = "{" +
		"\"tasktype\":\"Service\"," +
		"\"implementation\":\"Goes here\"" +
		"}";
	private String xpdl = "<TaskService Implementation=\"Goes here\" />";

	public void testParse() throws JSONException {
		XPDLTaskService task = new XPDLTaskService();
		task.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTaskService.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(task, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTaskService.class);
		XPDLTaskService task = (XPDLTaskService) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		task.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}
