package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLCategoryTest extends TestCase {
	
	private String jsonParse = "{" +
		"\"id\":\"ABC\"," +
		"\"categories\":\"Content\"" +
		"}";
	private String jsonWrite = "{" +
		"\"categories\":\"Content\"" +
		"}";
	private String xpdl = "<Category Id=\"ABC-category\">Content</Category>";

	public void testParse() throws JSONException {
		XPDLCategory category = new XPDLCategory();
		category.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLCategory.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(category, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLCategory.class);
		XPDLCategory category = (XPDLCategory) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		category.write(importObject);

		assertEquals(jsonWrite, importObject.toString());
	}
}
