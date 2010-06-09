package de.hpi.bpmn2xpdl;

import java.io.StringReader;
import java.io.StringWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

import junit.framework.TestCase;

public class XPDLWorkflowProcessTest extends TestCase {

	private String json = XPDLWorkflowProcess.implicitPool;
	private String xpdl= "<WorkflowProcess AdhocOrdering=\"Sequential\" ProcessType=\"None\" Status=\"None\" SuppressJoinFailure=\"true\" Id=\"MainPool-process\" Name=\"MainProcess\" />";
	
	public void testParse() throws JSONException {
		XPDLWorkflowProcess process = new XPDLWorkflowProcess();
		process.parse(new JSONObject(json));
	
		StringWriter writer = new StringWriter();
	
		Xmappr xmappr = new Xmappr(XPDLWorkflowProcess.class);
		xmappr.setPrettyPrint(false);
		xmappr.toXML(process, writer);
	
		assertEquals(xpdl, writer.toString());
	}
	
	public void testWrite() {		
		StringReader reader = new StringReader(xpdl);
		
		Xmappr xmappr = new Xmappr(XPDLWorkflowProcess.class);
		XPDLWorkflowProcess process = (XPDLWorkflowProcess) xmappr.fromXML(reader);
		
		assertEquals("Sequential", process.getAdhocOrdering());
		assertEquals("MainProcess", process.getName());
		assertEquals("MainPool-process", process.getId());
		assertEquals("None", process.getProcessType());
		assertEquals("None", process.getStatus());
	}
}
