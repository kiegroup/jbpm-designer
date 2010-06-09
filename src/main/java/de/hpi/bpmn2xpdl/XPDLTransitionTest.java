package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLTransitionTest extends TestCase {

	private String jsonParse = "{" +
		"\"conditiontype\":\"None\"," +
		"\"conditionexpression\":\"Should not be displayed\"," +
		"\"quantity\":\"10\"" +
		"}";
	private String xpdl = "<Transition Quantity=\"10\" />";

	public void testParse() throws JSONException {
		XPDLTransition transition = new XPDLTransition();
		transition.parse(new JSONObject(jsonParse));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLTransition.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(transition, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLTransition.class);
		XPDLTransition transition = (XPDLTransition) xmappr.fromXML(reader);

		assertEquals("10", transition.getQuantity());
	}
}