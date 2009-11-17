package org.oryxeditor.buildapps.sscompress;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.commons.codec.binary.Base64;



public class SSCompressor {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		if(args.length < 1)
			throw new Exception("Missing argument! Usage: java SSCompressor <SSDirectory>");
		
		//get stencil set directory from arguments
		String ssDirString = args[0];
		
		//get stencil set configuration file
		File ssConf = new File(ssDirString + "/stencilsets.json"); 
		
		if(!ssConf.exists())
			throw new Exception("File " + ssDirString + "/stencilsets.json does not exist.");

		//read stencil set configuration
		StringBuffer jsonObjStr = readFile(ssConf);

		JSONArray jsonObj = new JSONArray(jsonObjStr.toString());
		
		//iterate all stencil set configurations
		for(int i = 0; i < jsonObj.length(); i++) {
			JSONObject ssObj = jsonObj.getJSONObject(i);
			
			//get stencil set location
			if(ssObj.has("uri")) {
				String ssUri = ssObj.getString("uri");
				
				File ssFile = new File(ssDirString + ssUri);
				
				if(!ssFile.exists())
					throw new Exception("Stencil set " + ssDirString + ssUri + " that is referenced in stencil set configuration file does not exist.");
				
				String ssDir = ssFile.getParent();
				
				//read stencil set file
				StringBuffer ssString = readFile(ssFile);
				
				// store copy of original stencilset file (w/o SVG includes) with postfix '-nosvg'
				int pIdx = ssUri.lastIndexOf('.');
				File ssNoSvgFile = new File(ssDirString + ssUri.substring(0, pIdx) + "-nosvg" + ssUri.substring(pIdx));
				writeFile(ssNoSvgFile, ssString.toString());
				
				//***include svg files***
				
				//get view property
				Pattern pattern = Pattern.compile("[\"\']view[\"\']\\s*:\\s*[\"\']\\S+[\"\']");
				
				Matcher matcher = pattern.matcher(ssString);
				
				StringBuffer tempSS = new StringBuffer();
				
				int lastIndex = 0;
				
				//iterate all view properties
				while(matcher.find()) {
					tempSS.append(ssString.substring(lastIndex, matcher.start()));
					
					lastIndex = matcher.end();
					
					//get svg file name
					String filename = matcher.group().replaceFirst("[\"\']view[\"\']\\s*:\\s*[\"\']", "");
					filename = filename.substring(0, filename.length()-1);
					
					//get svg file
					File svgFile = new File(ssDir + "/view/" + filename);
					
					if(!svgFile.exists())
						throw new Exception("SVG File " + svgFile.getPath() + " does not exists!. Compressing stencil sets aborted!");
					
					StringBuffer svgString = readFile(svgFile);
					
					//check, if svgString is a valid xml file
					/*try {
						DocumentBuilderFactory builder = DocumentBuilderFactory.newInstance();
						DocumentBuilder document = builder.newDocumentBuilder();
						document.parse(svgString.toString());
					} catch(Exception e) {
						throw new Exception("File " + svgFile.getCanonicalPath() + " is not a valid XML file: " + e.getMessage());
					}*/
					
					
					//append file content to output json file (replacing existing json file)
					tempSS.append("\"view\":\"");
					tempSS.append(svgString.toString().replaceAll("[\\t\\n\\x0B\\f\\r]", " ").replaceAll("\"", "\\\\\""));
					//newSS.append(filename);
					tempSS.append("\"");
				}
				
				tempSS.append(ssString.substring(lastIndex));
				//***end include svg files***
				
				/*
				 * BAD IDEA, BECAUSE IT INCREASES THROUGHPUT
				
				//***include png files
				//get icon property
				pattern = Pattern.compile("[\"\']icon[\"\']\\s*:\\s*[\"\']\\S+[\"\']");
				
				matcher = pattern.matcher(tempSS);
				
				StringBuffer finalSS = new StringBuffer();
				
				lastIndex = 0;
				
				//iterate all icon properties
				while(matcher.find()) {
					finalSS.append(tempSS.substring(lastIndex, matcher.start()));
					
					lastIndex = matcher.end();
					
					//get icon file name
					String filename = matcher.group().replaceFirst("[\"\']icon[\"\']\\s*:\\s*[\"\']", "");
					filename = filename.substring(0, filename.length()-1);
					
					//get icon file
					File pngFile = new File(ssDir + "/icons/" + filename);
					
					if(!pngFile.exists())
						throw new Exception("SVG File " + pngFile.getPath() + " does not exists!. Compressing stencil sets aborted!");
					
					StringBuffer pngString = readFile(pngFile);
					
					//append file content to output json file (replacing existing json file)
					finalSS.append("\"icon\":\"javascript:");
					finalSS.append(encodeBase64(pngString.toString()));
					finalSS.append("\"");
				}
				
				finalSS.append(tempSS.substring(lastIndex));
				//***end include png files
				*/
				//write compressed stencil set file
				writeFile(ssFile, tempSS.toString());
				
				System.out.println("Compressed stencil set file " + ssFile.getCanonicalPath());
			}
		}
	}

	private static StringBuffer readFile(File file) throws Exception {
		FileInputStream fin =  new FileInputStream(file);
		StringBuffer result = new StringBuffer();

		String thisLine = "";
		BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
		
		while ((thisLine = myInput.readLine()) != null) {  
			result.append(thisLine);
			result.append("\n");
		}
		
		myInput.close();
		fin.close();
		
		return result;
	}
	
	private static void writeFile(File file, String text) throws Exception {
		FileOutputStream fos =  new FileOutputStream(file);
		
		BufferedWriter myOutput = new BufferedWriter(new OutputStreamWriter(fos));
		
		myOutput.write(text);
		myOutput.flush();
		
		myOutput.close();
		fos.close();
	}
	
	/*private static String encodeBase64(String text) {
		byte[] encoded = Base64.encodeBase64(text.getBytes());
		
		return new String(encoded);
	}*/
}
