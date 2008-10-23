package org.oryxeditor.buildapps.sscompress;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;



public class SSCompressor {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		if(args.length < 1)
			throw new Exception("Missing argument! Usage: java SSCompressor <SSDirectory>");
		
		String ssDirString = args[0];
		
		File ssConf = new File(ssDirString + "/stencilsets.json"); 
		
		if(!ssConf.exists())
			throw new Exception("File " + ssDirString + "/stencilsets.json does not exist.");

		FileInputStream fin =  new FileInputStream(ssConf);
		StringBuffer jsonObjStr = new StringBuffer();

		String thisLine = "";
		BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
		
		while ((thisLine = myInput.readLine()) != null) {  
			jsonObjStr.append(thisLine);
		}


		JSONArray jsonObj = new JSONArray(jsonObjStr.toString());
		System.out.println("ss num: " + jsonObj.length());
		for(int i = 0; i < jsonObj.length(); i++) {
			JSONObject ssObj = jsonObj.getJSONObject(i);
			
			String ssUri = ssObj.getString("uri");
			
			File ssFile = new File(ssDirString + ssUri);
			
			if(!ssFile.exists())
				throw new Exception("Stencil set " + ssDirString + ssUri + " that is referenced in stencil set configuration file does not exist.");
			
			fin =  new FileInputStream(ssFile);
			StringBuffer ssString = new StringBuffer();

			thisLine = "";
			myInput = new BufferedReader(new InputStreamReader(fin));
			
			while ((thisLine = myInput.readLine()) != null) {  
				ssString.append(thisLine);
			}
			
			Pattern pattern = Pattern.compile("[\"\']view[\"\']\\s*:\\s*[\"\'].+[\"\']");
			
			Matcher matcher = pattern.matcher(ssString);
			
			if(matcher.find()) {
				System.out.println(matcher.start());
			}
		}
		
		for(int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
	}

}
