package org.oryxeditor.server;

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
 * The initial version of the code in this file has been developed by
 * Stefan Krumnow and Falko Menge at SAP Research Brisbane and has been
 * contributed to the Oryx project in October 2008 under the terms of the
 * MIT License.
 * 
 * The XForm in editor/test/examples/service-composer-example-data.xhtml
 * contains example data that illustrate the usage of the parameters and allows
 * to directly start the Service Composer with a bunch of services.
 *
 * @author Stefan Krumnow
 * @author Falko Menge
 **/

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServiceComposerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String BASE_STENCILSET = "http://b3mn.org/stencilset/bpmn1.1#";

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected String baseUrl;
	protected Repository repository;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		process(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		process(request, response);
	}
	
	
	/**
	 * Method to process the HTTP request. 
	 * The basic steps are:
	 * <ul>
	 * 	<li>
	 * 		Parse the POST parameters and identify the services and their 
	 * 		properties. 
	 * 	</li>
	 * 	<li>
	 * 		Create a suitable stencil set extension based on the services. 
	 * 	</li>
	 * 	<li>
	 * 		Build and save a new model using the stencil set extension. 
	 * 	</li>
	 * </ul>
	 * 
	 * 
	 * @param request
	 * 		The HTTP request
	 * @param response
	 * 		The HTTP response
	 */
	protected void process(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.baseUrl = Repository.getBaseUrl(request);
		this.repository = new Repository(baseUrl);

		ArrayList<Service> services = parseParameters(request.getParameterMap());
		// println(services.toString());

		Date creationDate = new Date(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
		String sessionName = "Service Composer Session "
				+ dateFormat.format(creationDate);

		ArrayList<String> stencilSetExtensionUrls = new ArrayList<String>();
		// not specific enough:  stencilSetExtensionUrls.add("http://oryx-editor.org/stencilsets/extensions/bpmn1.1basicsubset#");
		stencilSetExtensionUrls.add("http://oryx-editor.org/stencilsets/extensions/bpmnservicecompositionsubset#"); // required for BPEL generation and user interface aggregation
		stencilSetExtensionUrls.add("http://oryx-editor.org/stencilsets/extensions/bpmn-xforms-user-interfaces#"); // required for user interface aggregation
		stencilSetExtensionUrls.add(generateStencilSetExtension(sessionName,
				services));
		
//		/* Add StencilSetExtension GoldenEye SES Astra Bachelorproject */
//		if (request.getParameter("type") != null) {
//			if (request.getParameter("type").equals("goldeneye")) {
//				stencilSetExtensionUrls.add(GoldenEyeStencilSetExtensionGeneratorServlet.
//				generateGoldenEyeStencilSet(sessionName + "GoldenEye" ,services));
//			}
//		}
		
		// generate BPMN model with a start event
		String startEventId = "oryx_" + UUID.randomUUID().toString();
		String model = repository.generateERDF(
						sessionName,
						"<a rel=\"oryx-render\" href=\"#"
								+ startEventId
								+ "\"/></div><div id=\""
								+ startEventId
								+ "\"><span class=\"oryx-type\">http://b3mn.org/stencilset/bpmn1.1#StartMessageEvent</span><span class=\"oryx-id\"></span><span class=\"oryx-categories\"></span><span class=\"oryx-documentation\"></span><span class=\"oryx-name\"></span><span class=\"oryx-assignments\"></span><span class=\"oryx-pool\"></span><span class=\"oryx-lanes\"></span><span class=\"oryx-eventtype\">Start</span><span class=\"oryx-trigger\">Message</span><span class=\"oryx-message\"></span><span class=\"oryx-implementation\">Web Service</span><span class=\"oryx-bgcolor\">#ffffff</span><span class=\"oryx-bounds\">15,225,45,255</span><a rel=\"raziel-parent\" href=\"#oryx-canvas123\"/>",
						"/stencilsets/bpmn1.1/bpmn1.1.json", BASE_STENCILSET,
						stencilSetExtensionUrls);
		String modelUrl = baseUrl
				+ repository.saveNewModel(model, "Service Composition "
						+ dateFormat.format(creationDate));

		// hack for reverse proxies:
		modelUrl = modelUrl.substring(modelUrl.lastIndexOf("http://"));

		// redirect client to editor with that newly generated model
		response.setHeader("Location", modelUrl);
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
	}

	protected String generateStencilSetExtension(String extensionName,
			ArrayList<Service> services) {
		String extension = generateJsonForStencilSetExtension(extensionName,services);
		String extensionNamespace = getStencilSetExtensionNamespace(extensionName);
		String extensionLocation = getStencilSetExtensionLocation(extensionName);
		StencilSetExtensionGenerator.saveStencilSetExtension(extensionLocation, extension);
		StencilSetExtensionGenerator.registerStencilSetExtension(
				extensionName,
				extensionNamespace, 
				getStencilSetExtensionDescription(extensionName, services), 
				extensionLocation,
				BASE_STENCILSET);
		return extensionNamespace;
	}

	private String generateJsonForStencilSetExtension(String extensionName,
			ArrayList<Service> services) {
		ArrayList<String> colors = new ArrayList<String>();
		colors.add("#eefecc"); // green
		colors.add("#cce5fe"); // light blue
		colors.add("#e5ccfe"); // purple
		colors.add("#ccccfe"); // dark blue
		// colors.add("#ffeecc"); // red
		// colors.add("#ffffcc"); // original yellow of BPMN stencil set
		ListIterator<String> colorIterator = colors.listIterator();

		Pattern pattern = Pattern.compile("([a-z])([A-Z0-9])");

		StringBuffer stencilsForOperations = new StringBuffer();
		ListIterator<Service> serviceIterator = services.listIterator();
		while (serviceIterator.hasNext()) {
			Service service = serviceIterator.next();
			if (!colorIterator.hasNext()) {
				colorIterator = colors.listIterator();
			}
			
			String color = colorIterator.next();
			
			ListIterator<PortType> portTypeIterator = service.portTypes.listIterator();
			while (portTypeIterator.hasNext()) {
				PortType portType = portTypeIterator.next();
				ListIterator<Operation> operationIterator = portType.operations.listIterator();
				
				while (operationIterator.hasNext()) {
					Operation operation = operationIterator.next();
					Matcher matcher = pattern.matcher(operation.name);
					String taskName = matcher.replaceAll("$1 $2");
					String xform = "";
					if (!operation.uiUrls.isEmpty()) {
						xform = operation.uiUrls.get(0);
					}
					stencilsForOperations.append("{" + "\"type\": \"node\"," + "\"id\":\""
									+ createJsonId(portType.name + "-"
											+ operation.name)
									+ "\","
									+ "\"superId\":\"Task\","
									+ "\"title\":\""
									+ taskName
									+ "\","
									+ "\"groups\":[\"Activities\"]," // The group 'Service Operations' appears too far on the bottom of the menu
									+ "\"description\":\"An invocation of operation '"
									+ operation.name
									+ "' of port type '"
									+ portType.name
									+ "' of the service described in '"
									+ service.wsdlUrl
									+ "'.\","
									+ "\"view\":\"activity/node.task.svg\","
									+ "\"icon\":\"new_task.png\","
									+ "\"roles\": [\"sequence_start\",\"sequence_end\",\"messageflow_start\", \"messageflow_end\",\"to_task_event\",\"from_task_event\",\"conditional_start\",\"default_start\", \"tc\", \"fromtoall\" ],"
									+ "\"properties\": [ "
									+ "{\"id\":\"name\",\"value\":\""
									+ taskName + "\" }, "
									+ "{\"id\":\"xform\",\"value\":\""
									// TODO: list of values from which the user can choose
									+ xform + "\" }, "
									+ "{\"id\":\"bgColor\",\"value\":\""
									+ color + "\"}" + "  ]},\n");
				}
			}
		}

		return "{\"title\":\""
				+ extensionName
				+ "\","
				+ "\"namespace\":\""
				+ getStencilSetExtensionNamespace(extensionName)
				+ "\","
				+ "\"description\":\""
				+ getStencilSetExtensionDescription(extensionName, services)
				+ "\","
				+ "\"extends\":\""
				+ BASE_STENCILSET
				+ "\","
				+ "\"stencils\":[\n"
				+ stencilsForOperations.toString()
				+ "],"
				+ "\"properties\":[],"
				+ "\"rules\": {\"connectionRules\": [],\"cardinalityRules\": [],\"containmentRules\": []},\"removestencils\": [],\"removeproperties\": []}";
	}

	private String getStencilSetExtensionDescription(String extensionName,
			ArrayList<Service> services) {
		return "Extension for " + extensionName + " using " + services.size()
				+ " services.";
	}

	private String getStencilSetExtensionNamespace(String extensionName) {
		return "http://oryx-editor.org/stencilsets/extensions/bpmn_"
				+ extensionName.toLowerCase().replace(" ", "_") + "#";
	}

	private String getStencilSetExtensionLocation(String extensionName) {
		return "bpmnservicecompositionsubset/"
				+ extensionName.toLowerCase().replace(" ", "_") + ".json";
	}

	private static String createJsonId(String name) {
		String result = name.toLowerCase();
		result = result.replace(" ", "");
		return result;
	}

	protected ArrayList<Service> parseParameters(Map<?, ?> parameterMap) {
		TreeMap<String, String> sortedParameterMap = new TreeMap<String, String>();
		for (Map.Entry<?, ?> parameter : parameterMap.entrySet()) {
			sortedParameterMap.put(parameter.getKey().toString(),
					((String[]) parameter.getValue())[0]);
		}
		ArrayList<Service> services = new ArrayList<Service>();
		Pattern pattern = Pattern.compile("^svc[0-9]+$");
		for (Map.Entry<String, String> parameter : sortedParameterMap
				.entrySet()) {
			String key = parameter.getKey();
			Matcher matcher = pattern.matcher(key);
			if (matcher.matches()) {
				String value = parameter.getValue();
				services.add(new Service(key, value, sortedParameterMap));
			}
		}
		return services;
	}

	protected void println(String output) {
		try {
			response.getWriter().println(output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static class Service {
		public String wsdlUrl;
		public String name;
		public String namespace;
		public ArrayList<PortType> portTypes;

		public Service(String id, String wsdlUrl, Map<String, String> parameterMap) {
			
			this.wsdlUrl = wsdlUrl;
			
			portTypes = new ArrayList<PortType>();
			for (Map.Entry<String, String> parameter : parameterMap.entrySet()) {
				String key = parameter.getKey();
				if (key.matches("^" + id + "_pt[0-9]+$")) {
					String value = parameter.getValue();
					portTypes.add(new PortType(key, value, parameterMap));
				} else if (key.matches("^" + id + "_name$")) {
					/* Get web service name */
					this.name = parameter.getValue();
				} else if (key.matches("^" + id + "_ns$")) {
					/* Get web service targetnamespace */
					this.namespace = parameter.getValue();
				}
			}
		}

		public String toString() {
			return wsdlUrl + "\n" + portTypes.toString() + "\n";
		}
	}

	static class PortType {
		public String name;
		public ArrayList<Operation> operations;

		public PortType(String id, String name, Map<String, String> parameterMap) {
			this.name = name;
			operations = new ArrayList<Operation>();
			for (Map.Entry<String, String> parameter : parameterMap.entrySet()) {
				String key = parameter.getKey();
				if (key.matches("^" + id + "_op[0-9]+$")) {
					String value = parameter.getValue();
					operations.add(new Operation(key, value, parameterMap));
				}
			}
		}

		public String toString() {
			return name + "\n" + operations.toString() + "\n";
		}
	}

	static class Operation {
		public String name;
		public Map<String, String> inputParts;
		public Map<String, String> outputParts;
		public String inputMessage;
		public String outputMessage;
		public ArrayList<String> uiUrls;

		public Operation(String id, String name,
				Map<String, String> parameterMap) {
			this.name = name;
			inputParts = new TreeMap<String, String>();
			outputParts = new TreeMap<String, String>();
			uiUrls = new ArrayList<String>();
			Pattern pattern = Pattern.compile("^(" + id
					+ "_((in)|(out))[0-9]+)_name$");
			for (Map.Entry<String, String> parameter : parameterMap.entrySet()) {
				String key = parameter.getKey();
				Matcher matcher = pattern.matcher(key);
				if (matcher.matches()) {
					String partName = parameter.getValue();
					Pattern pattern2 = Pattern.compile("^" + matcher.group(1)
							+ "_type$");
					for (Map.Entry<String, String> parameter2 : parameterMap.entrySet()) {
						String key2 = parameter2.getKey();
						Matcher matcher2 = pattern2.matcher(key2);
						if (matcher2.matches()) {
							String type = parameter2.getValue();
							if (matcher.group(2).equals("in")) {
								inputParts.put(partName, type);
							} else {
								outputParts.put(partName, type);
							}
						}
					}
				} else if (key.matches("^" + id + "_ui[0-9]+$")) {
					String value = parameter.getValue();
					uiUrls.add(value);
				} else if (key.matches("^" + id + "_inmsg$")) {
					/* Get operation input message */
					this.inputMessage = parameter.getValue();
				} else if (key.matches("^" + id + "_outmsg$")) {
					this.outputMessage = parameter.getValue();
				}

			}
		}

		public String toString() {
			return name + "\n" + inputParts.toString() + "\n"
					+ outputParts.toString() + "\n" + uiUrls.toString() + "\n";
		}
	}

}
