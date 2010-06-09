package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;


public class XPDLDataObjectTest extends TestCase {
	private String jsonParse = "{" +
		"\"requiredforstart\":\"false\"," +
		"\"producedatcompletion\":\"true\"," +
		"\"state\":\"\"" +
		"}";
	private String xpdl = "<DataObject ProducedAtCompletion=\"true\" RequiredForStart=\"false\" />";

	public void testParse() throws JSONException {
		XPDLDataObject dataObject = new XPDLDataObject();
		dataObject.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLDataObject.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(dataObject, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLDataObject.class);
		XPDLDataObject dataObject = (XPDLDataObject) xmappr.fromXML(reader);

		assertEquals(true, dataObject.getProducedAtCompletion());
		assertEquals(false, dataObject.getRequiredForStart());
	}
}
