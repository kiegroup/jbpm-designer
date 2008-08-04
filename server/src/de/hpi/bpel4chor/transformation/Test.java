package de.hpi.bpel4chor.transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Test {
	
	public static void main(String args[]) {
		String path = "E:\\Studium\\Diplomarbeit\\Transformation\\Implementierung\\TestCases\\XPDL4Chor\\";
		
		//File file = new File(path + "bindSenderTo.xml");
		//File file = new File(path + "bindSenderTo2.xml");
		//File file = new File(path + "containment.xml");
		//File file = new File(path + "handlers.xml");
		//File file = new File(path + "inclusiveGateways.xml");
		//File file = new File(path + "multipleStart.xml");
		//File file = new File(path + "multipleStartEnd.xml");
		File file = new File(path + "PriceRequest.xml");
		//File file = new File(path + "request_with_referral_reference.xml");
		//File file = new File(path + "TestDumas.xml");
		//File file = new File(path + "variables.xml");
		//File file = new File(path + "sequenceFlow.xml");
		
		try {
			System.out.println("Start Test:");
			FileInputStream input = new FileInputStream(file);	
			long size = file.length();
			byte[] content = new byte[(int)size];
	
			input.read(content);
			String myString = new String(content);
			input.close();
			
			//String[] result  = new BPMN2BPELImpl().transform(myString, true);
			String[] result  = new BPMN2BPEL4ChorImpl().transform(myString, false);
			
			for (int i = 0; i < result.length; i++) {
				System.out.println(result[i]);
			}
			System.out.println("Finished Test.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
