package org.oryxeditor.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright (c) 2008
 * SAP Research
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * 
 * Portions of the code in this file have been developed by
 * Stefan Krumnow and Falko Menge at SAP Research Brisbane and
 * contributed to the Oryx project in October 2008 under the
 * terms of the MIT License.
 *
 * @author Stefan Krumnow
 * @author Falko Menge
 **/

public class StencilSetExtensionGenerator {

	public static final String DEFAULT_STENCIL_SET_EXTENSION_NAME_PREFIX
			= "Generated Stencil Set Extension"; 
	public static final String DEFAULT_BASE_STENCIL_SET_PATH
			= "/stencilsets/bpmn1.1/bpmn1.1.json";
	public static final String DEFAULT_BASE_STENCIL_SET
			= "http://b3mn.org/stencilset/bpmn1.1#";
	public static final String DEFAULT_BASE_STENCIL
			= "Task";
	private static final String STENCILSET_EXTENSIONS_PATH
			= Repository.getOryxPath() + "stencilsets/extensions/";

	public static String generateStencilSetExtension(
			String extensionName,
			List<Map<String,String>> stencilPropertyMatrix,
			String[] propertyNames,
			String baseStencilSet,
			String baseStencil
	) {
		// check arguments
		if (extensionName == null || extensionName.length() == 0) {
			Date creationDate = new Date(System.currentTimeMillis());
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
			extensionName = DEFAULT_STENCIL_SET_EXTENSION_NAME_PREFIX + " " + dateFormat.format(creationDate);
		}
		if (baseStencilSet == null || baseStencilSet.length() == 0) {
			baseStencilSet = DEFAULT_BASE_STENCIL_SET;
		}
		if (baseStencil == null || baseStencil.length() == 0) {
			baseStencil = DEFAULT_BASE_STENCIL;
		}
		
		// generate JSON code
		String extensionNamespace = getStencilSetExtensionNamespace(extensionName);
		String extensionDescription = getStencilSetExtensionDescription(extensionName);
		String stencils = generateJsonForStencils(
				stencilPropertyMatrix,
				baseStencilSet,
				baseStencil
		);
		String extension = generateJsonForStencilSetExtension(
				extensionName,
				extensionNamespace,
				extensionDescription,
				baseStencilSet,
				stencils
		);

		// save JSON file
		String extensionLocation = getStencilSetExtensionLocation(extensionName);
		StencilSetExtensionGenerator.saveStencilSetExtension(extensionLocation, extension);
		
		// register in extensions.json
		StencilSetExtensionGenerator.registerStencilSetExtension(
				extensionName,
				extensionNamespace,
				extensionDescription,
				extensionLocation,
				baseStencilSet
				);
		return extensionNamespace;
	}

	private static String generateJsonForStencils(
			List<Map<String,String>> stencilPropertyMatrix,
			String baseStencilSet,
			String baseStencil
	) {
		// check arguments
		if (baseStencilSet == null || baseStencilSet.length() == 0) {
			baseStencilSet = DEFAULT_BASE_STENCIL_SET;
		}
		if (baseStencil == null || baseStencil.length() == 0) {
			baseStencil = DEFAULT_BASE_STENCIL;
		}
		
		String stencils = "";
		int stencilNumber = 0;
		ListIterator<Map<String,String>> stencilIterator = stencilPropertyMatrix.listIterator();
		while (stencilIterator.hasNext()) {
			// default values
			String type = "node";
			String id = UUID.randomUUID().toString();
			String superId = baseStencil;
			String title = "Generated Stencil " + ++stencilNumber;
			String group = "Activities";
			String description = "This is an automatically generated stencil.";
			String view = "activity/node.task.svg";
			String icon = "new_task.png";
			String roles = "\"sequence_start\",\"sequence_end\",\"messageflow_start\", \"messageflow_end\",\"to_task_event\",\"from_task_event\",\"conditional_start\",\"default_start\", \"tc\", \"fromtoall\"";
			String properties = "";
			
			// read common properties
			Map<String,String> propertyMap = stencilIterator.next();
			if (propertyMap.containsKey("type")) {
				type = propertyMap.remove("type");
			}
			if (propertyMap.containsKey("id")) {
				id = propertyMap.remove("id");
			}
			if (propertyMap.containsKey("superId")) {
				superId = propertyMap.remove("superId");
			}
			if (propertyMap.containsKey("title")) {
				title = propertyMap.remove("title");
			} else if (propertyMap.containsKey("name")) {
				title = propertyMap.get("name");
			}
			if (!propertyMap.containsKey("name")
					&& baseStencilSet == DEFAULT_BASE_STENCIL_SET
					&& baseStencil == DEFAULT_BASE_STENCIL
			) {
				propertyMap.put("name", title);
			}
			if (propertyMap.containsKey("group")) {
				group = propertyMap.remove("group");
			}
			if (propertyMap.containsKey("description")) {
				description = propertyMap.remove("description");
			}
			if (propertyMap.containsKey("view")) {
				view = propertyMap.remove("view");
			}
			if (propertyMap.containsKey("icon")) {
				icon = propertyMap.remove("icon");
			}
			if (propertyMap.containsKey("roles")) {
				roles = propertyMap.remove("roles");
			}
			
			// process stencil specific properties
			for (Map.Entry<String, String> property : propertyMap.entrySet()) {
				properties += "                {\n"
					+ "                    \"id\":\"" + property.getKey() + "\",\n"
					+ "                    \"value\":\"" + property.getValue() + "\"\n"
					+ "                },\n";
			}
			// remove last comma
			if (properties.length() > 0) {
				properties = properties.substring(0, properties.length() - 2) + "\n";
			}
			
			// generate JSON code
			stencils += "        {\n"
				+ "            \"type\": \"" + type + "\",\n"
				+ "            \"id\":\"" + id + "\",\n"
				+ "            \"superId\":\"" + superId + "\",\n"
				+ "            \"title\":\"" + title + "\",\n"
				+ "            \"groups\":[\"" + group + "\"],\n"
				+ "            \"description\":\"" + description + "\",\n"
				+ "            \"view\":\"" + view + "\",\n"
				+ "            \"icon\":\"" + icon + "\",\n"
				+ "            \"roles\": [ " + roles + " ],\n"
				+ "            \"properties\": [\n"
				+ properties
				+ "            ]\n"
				+ "        },\n";
		}
		// remove last comma
		if (stencils.length() > 0) {
			stencils = stencils.substring(0, stencils.length() - 2) + "\n";
		}
		return stencils;
	}

	public static String generateJsonForStencilSetExtension(
			String extensionName,
			String extensionNamespace,
			String extensionDescription,
			String baseStencilSet,
			String stencils
	) {
		// check arguments
		if (baseStencilSet == null || baseStencilSet.length() == 0) {
			baseStencilSet = DEFAULT_BASE_STENCIL_SET;
		}
		
		// generate JSON
		String json = "{\n"
			+ "    \"title\":\"" + extensionName + "\",\n"
			+ "    \"namespace\":\"" + extensionNamespace + "\",\n"
			+ "    \"description\":\"" + extensionDescription + "\",\n"
			+ "    \"extends\":\"" + baseStencilSet + "\",\n"
			+ "    \"stencils\":[\n"
			+ stencils
			+ "    ],\n"
			+ "    \"properties\":[],\n"
			+ "    \"rules\": {\n"
			+ "        \"connectionRules\": [],\n"
			+ "        \"cardinalityRules\": [],\n"
			+ "        \"containmentRules\": []\n"
			+ "    },\n"
			+ "    \"removestencils\": [],\n"
			+ "    \"removeproperties\": []\n"
			+ "}";
		return json;
	}

	protected static String getStencilSetExtensionDescription(String extensionName) {
		return "This is an automatically generated stencil set extension.";
	}

	protected static String getStencilSetExtensionNamespace(String extensionName) {
		return "http://oryx-editor.org/stencilsets/extensions/generated-stencil-set-extensions/" + extensionName.toLowerCase().replace(" ", "_") + "#";
	}

	private static String getStencilSetExtensionLocation(String extensionName) {
		return "generated-stencil-set-extensions/" + extensionName.toLowerCase().replace(" ", "_") + ".json";
	}

	public static void saveStencilSetExtension(
			String extensionLocation,
			String extension
	) {
		// TODO harden this code to avoid security issues
		File extensionFile = new File(STENCILSET_EXTENSIONS_PATH + extensionLocation);
		if (!extensionFile.exists()) {
			try {
				extensionFile.createNewFile();
				BufferedWriter extensionFileWriter = new BufferedWriter(new FileWriter(extensionFile));
				extensionFileWriter.write(extension);
				extensionFileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void registerStencilSetExtension(
			String name,
			String namespace,
			String description,
			String location,
			String baseStencilset
	) {

		File extensionFile = new File(STENCILSET_EXTENSIONS_PATH + location);
		File configFile = new File(STENCILSET_EXTENSIONS_PATH + "extensions.json");

		if (extensionFile.exists() && configFile.exists()) {
			String extensionDeclaration = "\t\t{\n"
				+ "\t\t\t\"title\":\"" + name + "\",\n"
				+ "\t\t\t\"namespace\":\"" + namespace + "\",\n"
				+ "\t\t\t\"description\":\"" + description + "\",\n"
				+ "\t\t\t\"definition\":\"" + location + "\",\n"
				+ "\t\t\t\"extends\":\"" + baseStencilset + "\"\n"
				+ "\t\t},";
			
			BufferedReader configFileReader;
			try {
				configFileReader = new BufferedReader(new FileReader(configFile));
				StringBuffer currentConfig = new StringBuffer();
				String line;
				while ((line = configFileReader.readLine()) != null){
					currentConfig.append(line+"\n");
				}
				// insert extension at the beginning of the list
				currentConfig.insert(currentConfig.indexOf("\"extensions\": [\n") + 16, extensionDeclaration +"\n");
				configFileReader.close();
				BufferedWriter configFileWriter = new BufferedWriter(new FileWriter(configFile));
				configFileWriter.write(currentConfig.toString());
				configFileWriter.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
