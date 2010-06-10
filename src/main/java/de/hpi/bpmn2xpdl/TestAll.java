package de.hpi.bpmn2xpdl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;

public class TestAll extends TestCase {

	protected static String path = "C:/Users/Markus Goetz/workspace/Oryx/editor/server/src/de/hpi/bpmn2xpdl/";
	
	public void testParse() throws JSONException, IOException {
		XPDLPackage convertPackage = new XPDLPackage();
		convertPackage.parse(new JSONObject(readFile(path + "unitTestParse.json")));

		StringWriter writer = new StringWriter();

		Xmappr xmappr = new Xmappr(XPDLPackage.class);
		xmappr.setPrettyPrint(true);
		xmappr.toXML(convertPackage, writer);
		String output = writer.toString() + "\n";
		
		assertEquals(readFile(path + "unitTest.xml"), output);
	}

	public void testWrite() throws IOException {		
		StringReader reader = new StringReader(readFile(path + "unitTest.xml"));

		Xmappr xmappr = new Xmappr(XPDLPackage.class);
		XPDLPackage convertPackage = (XPDLPackage) xmappr.fromXML(reader);

		JSONObject importObject = new JSONObject();
		convertPackage.write(importObject);
		String output = importObject.toString().replaceAll("oryx_[^\"]+", "oryx") + "\n";

		assertEquals(readFile(path + "unitTestWrite.json"), output);
	}
	
	private String readFile(String path) throws IOException {
		FileInputStream fileStream = new FileInputStream(path);
		DataInputStream dataStream = new DataInputStream(fileStream);
		InputStreamReader inputReader = new InputStreamReader(dataStream);
		BufferedReader bufferedReader = new BufferedReader(inputReader);
		String fileContentString = "";
		while (bufferedReader.ready()) {
			fileContentString += bufferedReader.readLine() + "\n";
		}
		bufferedReader.close();
		return fileContentString;
	}
}
