/**
 * 
 */
package org.oryxeditor.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.ServiceComposerServlet.Operation;
import org.oryxeditor.server.ServiceComposerServlet.PortType;
import org.oryxeditor.server.ServiceComposerServlet.Service;



/**
 * This class offers some static methods to create the stencil set extension used
 * to generate BPEL code for the GoldenEye SES Astra satellite test system.
 * Basically it generates the data objects that represents the input and output
 * variables of the web service calls.  
 * 
 * @author Sven Wagner-Boysen
 *
 */
public class GoldenEyeStencilSetExtensionGenerator {
	
	
	/** Enumeration to distinguish variable types {@code INPUT} and {@code OUTPUT}. */
	private enum TypeOfVariable {
		/** The method call is meant to process an input variable. */
		INPUT, 
		/** The method call is meant to process an output variable. */
		OUTPUT
	}
	
	/**
	 * Entry point to generate the GoldenEye stencil set extension.
	 * 
	 * @param string 
	 * 		The name of the stencil set
	 * @param services
	 * 		The used web services
	 * @return
	 * 		The stencil set extension URL
	 */
	public static String generateGoldenEyeStencilSet(String extensionName, List<Service> services) {
		String extensionNamespace = StencilSetExtensionGenerator.getStencilSetExtensionNamespace(extensionName);
		String extension;
		try {
			extension = generateJsonForStencilSetExtension(extensionName,services);
		} catch (JSONException e) {
			extension = e.toString();
		}
		String extensionLocation = getStencilSetExtensionLocation(extensionName);
		StencilSetExtensionGenerator.saveStencilSetExtension(extensionLocation, extension);
		StencilSetExtensionGenerator.registerStencilSetExtension(
				extensionName,
				extensionNamespace, 
				getStencilSetExtensionDescription(extensionName, services), 
				extensionLocation,
				ServiceComposerServlet.BASE_STENCILSET);
		return extensionNamespace;
	}
	
	/**
	 * Generates the JSON String for the stencil set extension. It contains: 
	 * <ul> 
	 * 	<li>DataObjects for each service's operation input part</li>
	 * </ul>
	 * 
	 * @param extensionName
	 * 		The name of the stencil set extension
	 * @param services
	 * 		The web service descriptions
	 * @return
	 * 		The resulting stencil set extension
	 * @throws JSONException 
	 */
	private static String generateJsonForStencilSetExtension(String extensionName, List<Service> services) throws JSONException {
		/* The array of data object stencils */
		JSONArray dataObjectStencils = new JSONArray();
		
		/* The whole stencil set extension */
//		StringBuffer stencilSetExtension = new StringBuffer();
		JSONObject stencilSetExtension = new JSONObject();

		/* Generate data objects for each service operation input parts */
		for (Service service : services) {
			for (PortType portType : service.portTypes) {
				for (Operation operation : portType.operations) {
					dataObjectStencils.put(generateDataObjectStencil(
							service, 
							operation, 
							portType, 
							TypeOfVariable.INPUT, 
							createJsonId("dataobject-" + service.name + "-" + operation.name), 
							generatePropertiesProperty(operation, TypeOfVariable.INPUT)));
				}
			}
		}
		
		
		/* Generate stencil set extension body */
		stencilSetExtension.put("title", extensionName);
		stencilSetExtension.put("namespace", StencilSetExtensionGenerator.getStencilSetExtensionNamespace(extensionName));
		stencilSetExtension.put("description", getStencilSetExtensionDescription(extensionName, services));
		stencilSetExtension.put("extends", ServiceComposerServlet.BASE_STENCILSET);
		stencilSetExtension.put("stencils", dataObjectStencils);
		stencilSetExtension.put("properties", new JSONArray());
		stencilSetExtension.put("removestencils", new JSONArray());
		stencilSetExtension.put("removeproperties", new JSONArray());
		
		
//		stencilSetExtension.append("{\"title\":\"");
//		stencilSetExtension.append(extensionName);
//		stencilSetExtension.append("\",");
//		stencilSetExtension.append("\"namespace\":\"");
//		stencilSetExtension.append(StencilSetExtensionGenerator.getStencilSetExtensionNamespace(extensionName));
//		stencilSetExtension.append("\",");
//		stencilSetExtension.append("\"description\":\"");
//		stencilSetExtension.append(getStencilSetExtensionDescription(extensionName, services));
//		stencilSetExtension.append("\",");
//		stencilSetExtension.append("\"extends\":\"");
//		stencilSetExtension.append(ServiceComposerServlet.BASE_STENCILSET);
//		stencilSetExtension.append("\",");
//		stencilSetExtension.append("\"stencils\":[\n");
//		stencilSetExtension.append(dataObjectStencils.toString());
//		stencilSetExtension.append("],");
//		stencilSetExtension.append("\"properties\":[],");
//		stencilSetExtension.append("\"rules\": {\"connectionRules\": [],\"cardinalityRules\": [],\"containmentRules\": []},\"removestencils\": [],\"removeproperties\": []}");
		
		return stencilSetExtension.toString(4);
	}
	
	/**
	 * Creates the {@code Properties} property of a {@code DataObject}
	 * 
	 * @param operation
	 * 		The operation that belongs to the data object
	 * @param type
	 * 		The type of the data object (input | output)
	 * @return
	 * 		The JSONString of the properties property
	 * @throws JSONException 
	 */
	private static JSONObject generatePropertiesProperty(Operation operation, TypeOfVariable type) throws JSONException {
		JSONObject property = new JSONObject();
		property.put("id", "properties");
		property.put("type", "Complex");
		property.put("title", "Properties");
		property.put("description", "The input params for " + operation.name);
		
		/* Generate default value */
		JSONObject jsonItem = new JSONObject();
		for (String param : operation.inputParts.keySet()) {
			jsonItem.put(param, "");
		}
		
		property.put("value", "{\"totalCount\": 1, \"items\":[" 
				+ jsonItem.toString() + "]}");
		
		/* Add complex items */
		property.put("complexItems", generateComplexItems(operation.inputParts));
		
		return property;
	}
	
	/**
	 * Generates the complex items from the parameters map.
	 * 
	 * @param params
	 * 		The source parameters
	 * @return
	 * 		The complex items {@code JSONArray}
	 * @throws JSONException 
	 */
	private static JSONArray generateComplexItems(Map<String, String> params) throws JSONException {
		JSONArray complexItems = new JSONArray();
		
		for (String key : params.keySet() ) {
			/* The type of the variable e.g. int, bool, string */
			String varType = params.get(key);
			JSONObject complexItem = new JSONObject();
			
			complexItem.put("id", key.toLowerCase());
			complexItem.put("name", key);
			complexItem.put("width", 100);
			
			/* Handle integer type */
			if (varType.equals("http://www.w3.org/2001/XMLSchema^int")) {
				complexItem.put("type", "String");
				complexItem.put("value", "");
			
			/* Handle string type */
			} else if (varType.equals("http://www.w3.org/2001/XMLSchema^string")) {
				
				complexItem.put("type", "String");
				complexItem.put("value", "");
			
			/* Handle float types */
			} else if (varType.equals("http://www.w3.org/2001/XMLSchema^float") ||
					varType.equals("http://www.w3.org/2001/XMLSchema^double")) {
				complexItem.put("type", "String");
				complexItem.put("value", "");
			
			/* Handle boolean type */
			} else if (varType.equals("http://www.w3.org/2001/XMLSchema^boolean")) {
				complexItem.put("type", "Boolean");
				complexItem.put("value", true);
			
			/* Handle enumeration type */
			} else if (varType.matches("^http://b3mn\\.org/ORYXtype\\^enum\\{(.*)\\}$")) {
				complexItem.put("type", "Choice");
				complexItem.put("items", 
						generateChoiceItems(
							varType.substring(
									varType.indexOf("{") + 1, 
									varType.lastIndexOf("}"))));
				
			} else {
				/* Not a supported type, remove it*/
				complexItem = null;
			}
			
			/* If an unsupported type was recognized, do not add the item */
			if (complexItem != null) {
				/* Put complex item to complex item list */
				complexItems.put(complexItem);
			}
			
		}
		return complexItems;
	}
	
	/**
	 * Generates the options of a choice type.
	 * 
	 * @param string
	 * 		Comma separated list of options
	 * @return
	 * 		The oryx stencil set string of the choice options
	 * @throws JSONException 
	 */
	private static JSONArray generateChoiceItems(String choicesString) throws JSONException {
		String[] choices = choicesString.split(",");
		JSONArray items = new JSONArray();
		
		for (int i = 0; i < choices.length; i++) {
			JSONObject item = new JSONObject();
			item.put("id", choices[i]);
			item.put("title", choices[i]);
			item.put("value", choices[i]);
			
			/* Add to item to item list */
			items.put(item);
		}
		return items;
	}

	/**
	 * Creates the stencil set {@code JSONObject} for a data object.
	 * 
	 * @param dataObjectName
	 * 		The name of the data object
	 * @param type
	 * 		Determines, if this is either an {@code OUTPUT} or {@code INPUT} variable
	 * @param id
	 * 		The stencil's identifier
	 * @param operationName
	 * 		The name of the operation the variable belongs to
	 * @param serviceName
	 * 		The service name the variable belongs to
	 * @param properties
	 * 		The parameters stored in the variable
	 * @return
	 * 		The stencil's {@code JSONObject}
	 * @throws JSONException 
	 */
	private static JSONObject generateDataObjectStencil(
			Service service, 
			Operation operation,
			PortType portType,
			TypeOfVariable type, 
			String id,
			JSONObject properties) throws JSONException {
		
		JSONObject dataObjectStencil = new JSONObject();
		
		dataObjectStencil.put("type", "node");
		dataObjectStencil.put("id", id);
//		dataObjectStencil.put("superId", "DataObject");
		dataObjectStencil.put("title", operation.name + " Variable");
		dataObjectStencil.put("groups", new JSONArray("[\"Artifacts\"]"));
		dataObjectStencil.put("description", "The " 
				+ type.toString().toLowerCase() +
				"variable to operation '" + operation.name + "' " 
				+ "of the service '" + service.name + "'");
		
		/* Graphical representation */
		dataObjectStencil.put("view", "artifact/node.data.object.svg");
		dataObjectStencil.put("icon", "new_data_object.png");
				
		/* Set connection roles */
		dataObjectStencil.put("roles", new JSONArray("[\"DataObject\",\"fromtoall\"]"));
		
		/* Set stencil's properties */
		JSONArray jsonProperties = new JSONArray();
		
		/* Create name property */
		JSONObject name = new JSONObject();
		name.put("id", "name");
		name.put("title", "Name");
		name.put("type", "String");
		name.put("value", operation.name + " Variable");
		name.put("refToView","text_name");
		jsonProperties.put(name);
		
		/* Create namespace property */
		JSONObject serviceNamespace = new JSONObject();
		serviceNamespace.put("id", "namespace");
		serviceNamespace.put("title", "Namespace");
		serviceNamespace.put("type", "String");
		serviceNamespace.put("value", service.namespace);
		serviceNamespace.put("readonly", true);
		jsonProperties.put(serviceNamespace);
		
		/* Create service name property */
		JSONObject serviceNameProp = new JSONObject();
		serviceNameProp.put("id", "servicename");
		serviceNameProp.put("title", "Service Name");
		serviceNameProp.put("type", "String");
		serviceNameProp.put("value", service.name);
		serviceNameProp.put("readonly", true);
		jsonProperties.put(serviceNameProp);
		
		/* Create operation property */
		JSONObject operationNameProp = new JSONObject();
		operationNameProp.put("id", "operation");
		operationNameProp.put("title", "Service Operation");
		operationNameProp.put("type", "String");
		operationNameProp.put("value", operation.name);
		operationNameProp.put("readonly", true);
		jsonProperties.put(operationNameProp);
		
		/* Create property messageType */
		JSONObject messageTypeProp = new JSONObject();
		messageTypeProp.put("id", "messagetype");
		messageTypeProp.put("title", "MessageType");
		messageTypeProp.put("type", "String");
		messageTypeProp.put("value", operation.inputMessage);
		messageTypeProp.put("readonly", true);
		jsonProperties.put(messageTypeProp);
		
		/* Create property port type */
		JSONObject portTypeProp = new JSONObject();
		portTypeProp.put("id", "porttype");
		portTypeProp.put("title", "PortType");
		portTypeProp.put("type", "String");
		portTypeProp.put("value", portType.name);
		portTypeProp.put("readonly", true);
		jsonProperties.put(portTypeProp);
		
		/* Append properties property */
		jsonProperties.put(properties);
		
		dataObjectStencil.put("properties", jsonProperties);
		
		return dataObjectStencil;
	}
	
	private static String createJsonId(String name) {
		String result = name.toLowerCase();
		result = result.replace(" ", "");
		return result;
	}
	
	private static String getStencilSetExtensionLocation(String extensionName) {
		return "bpmnservicecompositionsubset/"
				+ extensionName.toLowerCase().replace(" ", "_") + ".json";
	}
	
	private static String getStencilSetExtensionDescription(String extensionName,
			List<Service> services) {
		return "Extension for " + extensionName + " using " + services.size()
				+ " services.";
	}
	private static String getStencilSetExtensionDescription(String extensionName,
			ArrayList<Service> services) {
		return "Extension for " + extensionName + " using " + services.size()
				+ " services.";
	}
}
