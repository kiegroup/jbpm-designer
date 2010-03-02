/**
 * 
 */
package org.oryxeditor.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
public class GoldenEyeStencilSetExtensionGeneratorServlet extends ServiceComposerServlet{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2255306279308291665L;
	
//	/** Enumeration to distinguish variable types {@code INPUT} and {@code OUTPUT}. */
//	private enum TypeOfVariable {
//		/** The method call is meant to process an input variable. */
//		INPUT, 
//		/** The method call is meant to process an output variable. */
//		OUTPUT
//	}
	
	
	/* (non-Javadoc)
	 * @see org.oryxeditor.server.ServiceComposerServlet#process(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void process(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.baseUrl = Repository.getBaseUrl(request);
		this.repository = new Repository(baseUrl);

		ArrayList<Service> services = parseParameters(request.getParameterMap());

		Date creationDate = new Date(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
		String sessionName = "GoldenEye TestCase "
				+ dateFormat.format(creationDate);

		ArrayList<String> stencilSetExtensionUrls = new ArrayList<String>();

//		stencilSetExtensionUrls.add(generateStencilSetExtension(sessionName,
//				services));
		
		/* Add StencilSetExtension GoldenEye SES Astra Bachelorproject */
		stencilSetExtensionUrls.add(
				generateGoldenEyeStencilSet(sessionName + "GoldenEye" ,services));
		stencilSetExtensionUrls.add("http://oryx-editor.org/stencilsets/extensions/bpmnservicecompositionsubset-goldeneye#");
		
		// generate BPMN model with a start event
		String startEventId = "oryx_" + UUID.randomUUID().toString();
		
		/* Prepare generated model */
		StringBuilder modelData = new StringBuilder();
		modelData.append("<a rel=\"oryx-render\" href=\"#");
		modelData.append(startEventId);
		modelData.append("\"/></div><div id=\"");
		modelData.append(startEventId);
		modelData.append("\"><span class=\"oryx-type\">http://b3mn.org/stencilset/bpmn1.1#StartMessageEvent</span><span class=\"oryx-id\"></span><span class=\"oryx-categories\"></span><span class=\"oryx-documentation\"></span><span class=\"oryx-name\"></span><span class=\"oryx-assignments\"></span><span class=\"oryx-pool\"></span><span class=\"oryx-lanes\"></span><span class=\"oryx-eventtype\">Start</span><span class=\"oryx-trigger\">Message</span><span class=\"oryx-message\"></span><span class=\"oryx-implementation\">Web Service</span><span class=\"oryx-bgcolor\">#ffffff</span><span class=\"oryx-bounds\">15,225,45,255</span><a rel=\"raziel-parent\" href=\"#oryx-canvas123\"/>");
		
		String model = repository.generateERDF(
						sessionName,
						modelData.toString(),
						"/stencilsets/bpmn1.1/bpmn1.1.json", BASE_STENCILSET,
						stencilSetExtensionUrls);
		
		String modelUrl = baseUrl
				+ repository.saveNewModel(model, sessionName);

		// hack for reverse proxies:
		modelUrl = modelUrl.substring(modelUrl.lastIndexOf("http://"));

		// redirect client to editor with that newly generated model
		response.setHeader("Location", modelUrl);
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
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
	protected String generateGoldenEyeStencilSet(String extensionName, List<Service> services) {
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
	 * 	<li>A task stencil for each service's operation</li>
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
	private String generateJsonForStencilSetExtension(String extensionName, List<Service> services) throws JSONException {
		/* The array of task object stencils */
		JSONArray taskStencils = new JSONArray();
		
		/* The whole stencil set extension */
		JSONObject stencilSetExtension = new JSONObject();

		/* Generate a task object for each service operation */
		for (Service service : services) {
			for (PortType portType : service.portTypes) {
				for (Operation operation : portType.operations) {
					taskStencils.put(generateTaskObjectStencil(
							service, 
							operation, 
							portType, 
							createJsonId("task-" + service.name + "-" + operation.name), 
							generatePropertiesProperty(operation)));
				}
			}
		}
		
		
		/* Generate stencil set extension body */
		stencilSetExtension.put("title", extensionName);
		stencilSetExtension.put("namespace", StencilSetExtensionGenerator.getStencilSetExtensionNamespace(extensionName));
		stencilSetExtension.put("description", getStencilSetExtensionDescription(extensionName, services));
		stencilSetExtension.put("extends", ServiceComposerServlet.BASE_STENCILSET);
		stencilSetExtension.put("stencils", taskStencils);
		stencilSetExtension.put("properties", new JSONArray());
		stencilSetExtension.put("removestencils", new JSONArray());
		stencilSetExtension.put("removeproperties", new JSONArray());
		
		return stencilSetExtension.toString(4);
	}
	
	/**
	 * Creates the {@code InputSets} property of a {@code Task}
	 * 
	 * @param operation
	 * 		The operation that belongs to the data object
	 * @param type
	 * 		The type of the data object (input | output)
	 * @return
	 * 		The JSONString of the properties property
	 * @throws JSONException 
	 */
	private JSONObject generatePropertiesProperty(Operation operation) throws JSONException {
		JSONObject property = new JSONObject();
		property.put("id", "inputsets");
		property.put("type", "Complex");
		property.put("title", "InputSets");
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
	private JSONArray generateComplexItems(Map<String, String> params) throws JSONException {
		JSONArray complexItems = new JSONArray();
		
		for (String key : params.keySet() ) {
			/* The type of the variable e.g. int, bool, string */
			String varType = params.get(key);
			JSONObject complexItem = new JSONObject();
			
			complexItem.put("id", key);
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
	private JSONArray generateChoiceItems(String choicesString) throws JSONException {
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
	 * Creates the stencil {@code JSONObject} for a task object.
	 * 
	 * @param id
	 * 		The stencil's identifier
	 * @param operation
	 * 		The operation the task belongs to
	 * @param service
	 * 		The service the task belongs to
	 * @param inputSets
	 * 		The parameters to invoke the server method
	 * @return
	 * 		The stencil's {@code JSONObject}
	 * @throws JSONException 
	 */
	private JSONObject generateTaskObjectStencil(
			Service service, 
			Operation operation,
			PortType portType,
			String id,
			JSONObject inputSets) throws JSONException {
		
		JSONObject taskStencil = new JSONObject();
		
		taskStencil.put("type", "node");
		taskStencil.put("id", id);
		taskStencil.put("title", operation.name);
		taskStencil.put("groups", new JSONArray("[\"Activities\"]"));
		taskStencil.put("description",
				"The task to operation '" + operation.name + "' " 
				+ "of the service '" + service.name + "'");
		
		/* Graphical representation */
		taskStencil.put("view", "activity/node.task.svg");
		taskStencil.put("icon", "new_task.png");
				
		/* Set connection roles */
		taskStencil.put("roles", new JSONArray("[\"Task\"," +
				"\"sequence_start\"," +
				"\"sequence_end\"," +
				"\"messageflow_start\"," +
				"\"messageflow_end\"," +
				"\"to_task_event\"," +
				"\"from_task_event\"," +
				"\"tc\"," +
				"\"fromtoall\"," +
				"\"ActivitiesMorph\"," +
				"\"FromEventbasedGateway\"]\""));
		
		/* Set stencil's properties */
		JSONArray jsonProperties = new JSONArray();
		
		/* Create name property */
		JSONObject name = new JSONObject();
		name.put("id", "name");
		name.put("title", "Name");
		name.put("type", "String");
		name.put("value", operation.name + " Task");
		name.put("refToView","acttext");
		name.put("readonly", true);
		jsonProperties.put(name);
		
		/* Create wsdl-url property */
		JSONObject wsdlUrl = new JSONObject();
		wsdlUrl.put("id", "wsdlurl");
		wsdlUrl.put("title", "wsdlUrl");
		wsdlUrl.put("type", "String");
		wsdlUrl.put("value", service.wsdlUrl);
		wsdlUrl.put("readonly", true);
		jsonProperties.put(wsdlUrl);
		
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
		
		/* Create property InMessageType */
		JSONObject inMessageTypeProp = new JSONObject();
		inMessageTypeProp.put("id", "inmessagetype");
		inMessageTypeProp.put("title", "InMessageType");
		inMessageTypeProp.put("type", "String");
		inMessageTypeProp.put("value", operation.inputMessage);
		inMessageTypeProp.put("readonly", true);
		jsonProperties.put(inMessageTypeProp);
		
		/* Create property OutMessageType */
		JSONObject outMessageTypeProp = new JSONObject();
		outMessageTypeProp.put("id", "outmessagetype");
		outMessageTypeProp.put("title", "OutMessageType");
		outMessageTypeProp.put("type", "String");
		outMessageTypeProp.put("value", operation.outputMessage);
		outMessageTypeProp.put("readonly", true);
		jsonProperties.put(outMessageTypeProp);
		
		/* Create property port type */
		JSONObject portTypeProp = new JSONObject();
		portTypeProp.put("id", "portType");
		portTypeProp.put("title", "PortType");
		portTypeProp.put("type", "String");
		portTypeProp.put("value", portType.name);
		portTypeProp.put("readonly", true);
		jsonProperties.put(portTypeProp);
		
		/* Append input sets property */
		jsonProperties.put(inputSets);
		
		/* Append standard task properties */
		for (JSONObject property : createStandardTaskProperties()) {
			jsonProperties.put(property);
		}
		
		taskStencil.put("properties", jsonProperties);
		
		return taskStencil;
	}
	
	/**
	 * Create the standard properties of a task object.
	 * 
	 * @return
	 * 		A list of JSON object properties
	 * @throws JSONException 
	 */
	private List<JSONObject> createStandardTaskProperties() throws JSONException {
		ArrayList<JSONObject> properties = new ArrayList<JSONObject>();
		
		JSONObject bgColor = new JSONObject();
		bgColor.put("id", "bgColor");
		bgColor.put("type", "Color");
		bgColor.put("title", "Background Color");
		bgColor.put("value", "#ffffcc");
		bgColor.put("refToView", "taskrect");
		bgColor.put("fill", true);
		bgColor.put("stroke", false);
		properties.add(bgColor);
		
		JSONObject loopType = new JSONObject();
		loopType.put("id", "loopType");
		loopType.put("type", "Choice");
		loopType.put("title", "LoopType");
		loopType.put("value", "None");
		
		/* LoopType choice items */
		JSONArray loopTypeItems = new JSONArray();
		loopTypeItems.put(new JSONObject("{" +
				"\"id\":\"c1\"," +
				"\"title\":\"None\"," +
				"\"value\":\"None\"," +
				"\"refToView\":\"none\"" +
				"}"));
		loopTypeItems.put(new JSONObject("{" +
				"\"id\":\"c2\"," +
				"\"title\":\"Standard\"," +
				"\"value\":\"Standard\"," +
				"\"refToView\":\"loop\"" +
				"}"));
		loopTypeItems.put(new JSONObject("{" +
				"\"id\":\"c3\"," +
				"\"title\":\"MultiInstance\"," +
				"\"value\":\"MultiInstance\"," +
				"\"refToView\":\"multiple\"" +
				"}"));
		
		
		loopType.put("items", loopTypeItems);
		properties.add(loopType);
		
		/* Compensation property */
		JSONObject compensationProp = new JSONObject();
		compensationProp.put("id", "isCompensation");
		compensationProp.put("type", "Boolean");
		compensationProp.put("title", "isCompensation");
		compensationProp.put("value", false);
		compensationProp.put("refToView", "compensation");
		properties.add(compensationProp);
		
		return properties;
	}
	
	private String createJsonId(String name) {
		String result = name.toLowerCase();
		result = result.replace(" ", "");
		return result;
	}
	
	private String getStencilSetExtensionLocation(String extensionName) {
		return "bpmnservicecompositionsubset/"
				+ extensionName.toLowerCase().replace(" ", "_") + ".json";
	}
	
	private String getStencilSetExtensionDescription(String extensionName,
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
