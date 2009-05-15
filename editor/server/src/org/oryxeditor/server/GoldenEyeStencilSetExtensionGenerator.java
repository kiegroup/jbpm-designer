/**
 * 
 */
package org.oryxeditor.server;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
	
	private static StringBuilder dataObjectStencils = new StringBuilder();
	
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
		String extension = generateJsonForStencilSetExtension(extensionName,services);
		return "";
	}
	
	private static String generateJsonForStencilSetExtension(String extensionName, List<Service> services) {
		
		for (Service service : services) {
			for (PortType portType : service.portTypes) {
				for (Operation operation : portType.operations) {
					
					
////					generateAndAddDataObjectStencil(
//							operation.name + " Variable", 
//							TypeOfVariable.INPUT, 
//							"DataObject-" + createJsonId("DataObject-" + service.name + "-" + operation.name), 
//							operation.name,
//							service.name, 
//							"properties");
					
					
				}
			}
		}
		return null;
	}
	
	private static String generatePropertiesProperty(Operation operation, TypeOfVariable type) {
		StringBuilder property = new StringBuilder();
		property.append("{");
		property.append("\"id\":\"properties\",");
		property.append("\"type\":\"Complex\",");
		property.append("\"title\":\"Properties\",");
		property.append("\"value\":\"\","); //TODO: add params
		
		/* Add complex items */
		property.append("\"complexItems\": [");
		
		
		property.append("]");
		
		property.append("}");
		return "";
	}
	
	/**
	 * Generates the complex items from the parameters map.
	 * 
	 * @param params
	 * 		The source parameters
	 * @return
	 * 		The complex items as string
	 */
	private static String generateComplexItems(Map<String, String> params) {
		StringBuilder complexItems = new StringBuilder();
		for (String key : params.keySet() ) {
			/* The type of the variable e.g. int, bool, string */
			String varType = params.get(key);
			
			complexItems.append("{");
			complexItems.append("\"id\":\"" + key.toLowerCase() + "\",");
			complexItems.append("\"name\": \"" + key + "\",");
			
			/* Handle integer type */
			if (varType.equals("http://www.w3.org/2001/XMLSchema^int")) {
				complexItems.append("\"type\":\"Integer\"");
				complexItems.append("\"value\":\"\"");
			
			/* Handle string type */
			} else if (varType.equals("http://www.w3.org/2001/XMLSchema^string")) {
				complexItems.append("\"type\":\"String\"");
				complexItems.append("\"value\":\"\"");
			
			/* Handle float types */
			} else if (varType.equals("http://www.w3.org/2001/XMLSchema^float") ||
					varType.equals("http://www.w3.org/2001/XMLSchema^double")) {
				complexItems.append("\"type\":\"Float\"");
				complexItems.append("\"value\":\"\"");
			
			/* Handle boolean type */
			} else if (varType.equals("http://www.w3.org/2001/XMLSchema^boolean")) {
				complexItems.append("\"type\":\"Boolean\"");
				complexItems.append("\"value\":\"true\"");
			
			/* Handle enumeration type */
			} else if (varType.matches("^http://b3mn.org/ORYXtype^enum{.*?}$")) {
				complexItems.append("\"type\":\"Choice\"");
				
				
//				complexItems.append("\"value\":\"\"");
				
				
				
				generateChoiceItems(varType.substring(varType.indexOf("{"), varType.lastIndexOf("}")));
			}
			
			complexItems.append("}");
			
			
			
		}
		return "";
	}
	
	/**
	 * Generates the options of a choice type.
	 * 
	 * @param string
	 * 		Comma separated list of options
	 * @return
	 * 		The oryx stencil set string of the choice options
	 */
	private static String generateChoiceItems(String choicesString) {
		String[] choices = choicesString.split(",");
		StringBuilder items = new StringBuilder();
		
		for (int i = 0; i < choices.length; i++) {
			items.append("{");
			items.append("\"id\":" + choices[i] + ",");
			items.append("\"title: \"" + choices[i] + ",");
			items.append("\"value: \"" + choices[i] );
			if (i == choices.length - 1) {
				items.append("}");
			} else {
				items.append("},");
			}
		}
		return items.toString();
	}

	/**
	 * Creates the stencil set string for a data object and appends it.
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
	 */
	private static void generateAndAddDataObjectStencil(
			String dataObjectName, 
			TypeOfVariable type, 
			String id, 
			String operationName,
			String serviceName,
			String properties,
			String messageType) {
		
		dataObjectStencils.append("{ \"type\": \"node\", ");
		dataObjectStencils.append("\"id\":\"DataObject-");
		dataObjectStencils.append(id + "\",");
		dataObjectStencils.append("\"superId\":\"DataObject\",");
		dataObjectStencils.append("\"title\":\"" + dataObjectName + "\",");
		dataObjectStencils.append("\"groups\":[\"Artifacts\"],");
		
		/* Add description to stencil */
		dataObjectStencils.append("\"description\":\"The "); 
		dataObjectStencils.append(type.toString().toLowerCase()); 
		dataObjectStencils.append("variable to operation '" + operationName + "' ");
		dataObjectStencils.append("of the service '" + serviceName + "\"" );
		
		dataObjectStencils.append("\"view\":\"artifact/node.data.object.svg\",");
		dataObjectStencils.append("\"icon\":\"new_data_object.png\",");
		
		/* Set connection rules */
		dataObjectStencils.append("\"roles\": [dataObjectStencils.append( \"fromtoall\" ],");
		
		/* Set stencil's properties */
		dataObjectStencils.append("\"properties\": [ ");
		dataObjectStencils.append("{\"id\":\"name\",\"value\":\"" + dataObjectName + "\" }, ");
		dataObjectStencils.append("{\"id\":\"messageType\",\"value\":\"" + messageType + "\" }, ");
		dataObjectStencils.append(properties);

		dataObjectStencils.append("}");
		
/* Create data objects for each service method input and output message */
//String, Integer, Double, Enum, Boolean 
	}
	
	private static String createJsonId(String name) {
		String result = name.toLowerCase();
		result = result.replace(" ", "");
		return result;
	}
}
