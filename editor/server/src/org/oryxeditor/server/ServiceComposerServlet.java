package org.oryxeditor.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServiceComposerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		process(request, response);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		process(request, response);
    }

	protected void process(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		ArrayList<Service> services = parseParameters(request.getParameterMap());
		println(services.toString());
	}
	
	protected ArrayList<Service> parseParameters(Map<?, ?> parameterMap) {
		TreeMap<String, String> sortedParameterMap = new TreeMap<String, String>();
		for (Map.Entry<?, ?> parameter : parameterMap.entrySet()) {
			sortedParameterMap.put(parameter.getKey().toString(), ((String[]) parameter.getValue())[0]);
		}
		ArrayList<Service> services = new ArrayList<Service>();
		Pattern pattern = Pattern.compile("^svc[0-9]+$");
		for (Map.Entry<String, String> parameter : sortedParameterMap.entrySet()) {
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
		public ArrayList<PortType> portTypes;
		
		public Service(String id, String wsdlUrl, Map<String, String> parameterMap) {
			this.wsdlUrl = wsdlUrl;
			portTypes = new ArrayList<PortType>();
			for (Map.Entry<String, String> parameter : parameterMap.entrySet()) {
				String key = parameter.getKey();
	    		if (key.matches("^" + id + "_pt[0-9]+$")) {
	    			String value = parameter.getValue();
	    			portTypes.add(new PortType(key, value, parameterMap));
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
		
		public PortType (String id, String name,  Map<String, String> parameterMap) {
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
		public ArrayList<String> uiUrls;

		public Operation(String id, String name, Map<String, String> parameterMap) {
			this.name = name;
			inputParts = new TreeMap<String, String>();
			outputParts = new TreeMap<String, String>();
			uiUrls = new ArrayList<String>();
			Pattern pattern = Pattern.compile("^(" + id + "_((in)|(out))[0-9]+)_name$");
			for (Map.Entry<String, String> parameter : parameterMap.entrySet()) {
				String key = parameter.getKey();
				Matcher matcher = pattern.matcher(key);
				if (matcher.matches()) {
					String partName =  parameter.getValue();
					Pattern pattern2 = Pattern.compile("^" + matcher.group(1) + "_type$");
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
	    		}
			}
		}
		
		public String toString() {
			return name + "\n" + inputParts.toString() + "\n" + outputParts.toString() + "\n" + uiUrls.toString() + "\n";
		}
	}
	
}
