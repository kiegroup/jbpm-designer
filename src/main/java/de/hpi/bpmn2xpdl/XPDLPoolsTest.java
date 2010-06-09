package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class XPDLPoolsTest extends TestCase {
	private String json = "{\"poolsunknowns\":\"rO0ABXNyACVkZS5ocGkuYnBtbjJ4cGRsLlhN" +
	"TFVua25vd25zQ29udGFpbmVyAAAAAAAAAAECAAJM\\r\\nABF1bmtub3duQXR0cmlidXRlc3QAE0xqYXZhL" +
	"3V0aWwvSGFzaE1hcDtMAA91bmtub3duRWxlbWVu\\r\\ndHN0ABVMamF2YS91dGlsL0FycmF5TGlzdDt4cH" +
	"NyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDR\\r\\nAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHh" +
	"wP0AAAAAAAAx3CAAAABAAAAAAeHNyABNqYXZh\\r\\nLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARz" +
	"aXpleHAAAAABdwQAAAAKc3IAFW9yZy54bWFw\\r\\ncHIuRG9tRWxlbWVudImen1gfMVQJAgADTAAKYXR0c" +
	"mlidXRlc3EAfgABTAAIZWxlbWVudHN0ABBM\\r\\namF2YS91dGlsL0xpc3Q7TAAEbmFtZXQAG0xqYXZheC" +
	"94bWwvbmFtZXNwYWNlL1FOYW1lO3hwc3EA\\r\\nfgAEP0AAAAAAAAx3CAAAABAAAAAAeHNxAH4ABgAAAAB" +
	"3BAAAAAp4c3IAGWphdmF4LnhtbC5uYW1l\\r\\nc3BhY2UuUU5hbWWBbagt/DvdbAIAA0wACWxvY2FsUGFy" +
	"dHQAEkxqYXZhL2xhbmcvU3RyaW5nO0wA\\r\\nDG5hbWVzcGFjZVVSSXEAfgAPTAAGcHJlZml4cQB+AA94c" +
	"HQADFVua25vd25DaGlsZHQAAHEAfgAS\\r\\neA==\"}";
	private String xpdl = "<Pools><UnknownChild /></Pools>";

	public void testParse() throws JSONException {
		XPDLPools pools = new XPDLPools();
		pools.parse(new JSONObject(json));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLPools.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(pools, writer);

		assertEquals(xpdl, writer.toString());
	}

	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);

		Xmappr xmappr = new Xmappr(XPDLPools.class);
		XPDLPools pools = (XPDLPools) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		pools.write(importObject);

		assertEquals(json, importObject.toString());
	}
}
