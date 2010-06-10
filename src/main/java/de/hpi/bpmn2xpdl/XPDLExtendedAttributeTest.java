package de.hpi.bpmn2xpdl;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.xmappr.Xmappr;

public class XPDLExtendedAttributeTest extends TestCase {

	private String xpdl = "<ExtendedAttribute Name=\"TestKey\" Value=\"TestValue\" />";

	public void testParse() throws JSONException {
		XPDLExtendedAttribute attribute = new XPDLExtendedAttribute();
		attribute.setName("TestKey");
		attribute.setValue("TestValue");

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLExtendedAttribute.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(attribute, writer);

		assertEquals(xpdl, writer.toString());
	}
}
