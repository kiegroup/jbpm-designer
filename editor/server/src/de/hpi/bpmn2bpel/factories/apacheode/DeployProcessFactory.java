package de.hpi.bpmn2bpel.factories.apacheode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.configuration.DatabaseConfiguration;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.jdom.input.SAXBuilder;

import de.hpi.bpmn2bpel.TransformationResult;
import de.hpi.bpmn2bpel.TransformationResult.Type;
import de.hpi.bpmn2bpel.factories.apacheode.deploymentservice.DeploymentServiceLayer;
import de.hpi.bpmn2bpel.factories.apacheode.deploymentservice.stub.DeployUnit;

/**
 * This class provides methods to create the process WSDL and the deployment
 * descriptor on the basis of the BPEL-Process document.
 * 
 * @author Paul Schroeder
 * @author Sven Wagner-Boysen
 */
public class DeployProcessFactory {

	/** logger variable */
	private static Logger logger = Logger.getLogger(DeployProcessFactory.class);
	
	private final static String partnerLinkNamespaceTag = "plnk";
	private String ODE_URL = "http://localhost:8080/ode/";
	
	private Document		bpelDocument;	
	private Document		processWsdlDocument;
	private Element			definitions;
	private Document		deployDocument;
	private Element			deploy;
	private Set<String>		wsdlUrls;
	private HashMap<String, String> wsdls;
	private List<TransformationResult> deployProcessData;
	
	public static DeployProcessFactory getNewDeployProcessFactory(String apacheOdeUrl) {
		DeployProcessFactory deployProcessFactory = new DeployProcessFactory();
		deployProcessFactory.wsdls = new HashMap<String, String>();
		deployProcessFactory.ODE_URL = apacheOdeUrl;
		return deployProcessFactory;
	}
	
	/**
	 * Creates t
	 * @param bpelProcess
	 */
	public List<TransformationResult> buildDeployProcessData(
			TransformationResult bpelProcess,
			Set<String> wsdlUrls) {
		
		this.deployProcessData = new ArrayList<TransformationResult>();
		
		/* retrieve BPEL Document */
		this.bpelDocument = createDocumentFromString(createStringFromDocument(bpelProcess.getDocument()));
		
		/* set wsdlUrls */
		this.wsdlUrls = wsdlUrls;
		
		/* document builder */
		DocumentBuilder builder = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			builder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage(), e);
		}
		processWsdlDocument = builder.newDocument();
		Element processWsdl = createBpelProcessWsdl();
		processWsdlDocument.appendChild(processWsdl);
		
		/* Append process WSDL to process's deployment data */
		this.deployProcessData.add(
				new TransformationResult(Type.PROCESS_WSDL, processWsdlDocument));
		
		
		/* 
		 * Generate Apache ODE Deployment Descriptor 
		 */
		
		/* Build new document for deployment descriptor */
		deployDocument = builder.newDocument();		
		Element deploymentDescriptorDocument = createDeploymentDescriptor();
		deployDocument.appendChild(deploymentDescriptorDocument);
		
		/* Append deployment descriptor to process's deployment data */
		this.deployProcessData.add(
				new TransformationResult(Type.DEPLOYMENT_DESCRIPTOR, deployDocument));
		
		return this.deployProcessData;
	}
	
	/**
	 * Creates the WSDL document to describe the web-service, representing the 
	 * business process in Apache ODE.
	 * 
	 * @return
	 * 		The WSDL definition element
	 */
	private Element createBpelProcessWsdl() {
		
		this.definitions = this.processWsdlDocument.createElement("definitions");
		Element bpelProcessElement = (Element) bpelDocument.getElementsByTagName(
				"process").item(0);
		
		String processName = bpelProcessElement.getAttribute("name");
		String processTargetNamespace = bpelProcessElement.getAttribute("targetNamespace");
		
		this.definitions.setAttribute("name", processName);
		this.definitions.setAttribute("targetNamespace", processTargetNamespace);
		this.definitions.setAttribute("xmlns:tns", processTargetNamespace);
		this.definitions.setAttribute("xmlns", "http://schemas.xmlsoap.org/wsdl/");
		this.definitions.setAttribute("xmlns:soap", "http://schemas.xmlsoap.org/wsdl/soap/");
		this.definitions.setAttribute("xmlns:" + partnerLinkNamespaceTag, "http://docs.oasis-open.org/wsbpel/2.0/plnktype");
		
		NodeList partnerLinksNodeList = bpelDocument.getElementsByTagNameNS("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "partnerLink");
		
		/* add partner links */
		for( Element e: createPartnerLinkType(partnerLinksNodeList)){
			this.definitions.appendChild(e);
		}
		
		NodeList importsNodeList = bpelDocument.getElementsByTagNameNS("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "import");
		for( Element e: copyImports(importsNodeList)){
			this.definitions.appendChild(e);
		}
		
		Element processWsdlTypes = createTypes();
		this.definitions.appendChild(processWsdlTypes);
		
		for( Element e: createMessages()){
			this.definitions.appendChild(e);
		}
		
		Element processWsdlPortType = createPortType();
		this.definitions.appendChild(processWsdlPortType);
		
		Element processWsdlBinding = createBindings();
		this.definitions.appendChild(processWsdlBinding);
		
		Element processWsdlService = createServiceElement();
		this.definitions.appendChild(processWsdlService);
		
		return this.definitions;
	}

	private Element createDeploymentDescriptor() {
		
		this.deploy = deployDocument.createElement("deploy");
		this.deploy.setAttribute("xmlns", "http://www.apache.org/ode/schemas/dd/2007/03");
		
		Element process = deployDocument.createElement("process");
		this.deploy.appendChild(process);
		
		Element processNode = (Element) bpelDocument.getElementsByTagName("process").item(0); 
		String processName = processNode.getAttribute("name");
		String deploymProcessName = "tns:" + processName;
		process.setAttribute("name", deploymProcessName);
		
		Element processEvents = deployDocument.createElement("process-events");
		process.appendChild(processEvents);		
		processEvents.setAttribute("generate", "all");
		
		for( Element e: createProvide()){
			process.appendChild(e);
		}
		
		for( Element e: createInvoke()){
			process.appendChild(e);
		}
		
		return deploy;
	}

	private List<Element> createProvide() {
		
		List<Element> provideList = new ArrayList<Element>();
		
		NodeList provideNodeList = bpelDocument.getElementsByTagName("receive");
		for(int provideNr = 0; provideNr < provideNodeList.getLength(); provideNr++) {
			
			Element provideTag = (Element) provideNodeList.item(provideNr);
			String providePartnerLink = provideTag.getAttribute("partnerLink");
			String provideServiceName = provideTag.getAttribute("serviceName");
			String providePortType = provideTag.getAttribute("portType");
			
			Element provide = deployDocument.createElement("provide");			
			provide.setAttribute("partnerLink", providePartnerLink);
			
			Element service = createProvideInvokeService(provideServiceName, providePortType, null);
			provide.appendChild(service);
			
			provideList.add(provide);
			
		}
		return provideList;
	}

	private List<Element> createInvoke() {
		
//		List<EWSDL> ewsdls = executionPlan.getEwsdlsForExecution();
		List<Element> invokeList = new ArrayList<Element>();
		
		/*get all invoke tags*/
		NodeList invokeNodeList = bpelDocument.getElementsByTagName("invoke");
	
		/* test if there are more than one soap:address tag in the wsdl*/
		for(int invokeNr = 0; invokeNr < invokeNodeList.getLength(); invokeNr++) {
	
			Element invokeTag = (Element) invokeNodeList.item(invokeNr);
			String invokePartnerLink = invokeTag.getAttribute("partnerLink");
			String invokePortType = invokeTag.getAttribute("portType");
			
			String wsdlPortTypeName = getLastPartofString(invokePortType, ":");
			
			Element deployInvoke = deployDocument.createElement("invoke");
			deployInvoke.setAttribute("partnerLink", invokePartnerLink);
			
//			String proxyUrl = systemConfiguration.getString(SystemConfiguration.STATION_PROXY_URL_PROPERTY);
			
			for(String wsdlUrl : this.wsdlUrls) {
				
				Document docWsdl = createDocumentFromUrl(wsdlUrl);
				
				NodeList wsdlPortTypeNodeList = docWsdl.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "portType");
				for(int wsdlPortTypeNr = 0; wsdlPortTypeNr < wsdlPortTypeNodeList.getLength(); wsdlPortTypeNr++) {
					
					Element wsdlPortType = (Element) wsdlPortTypeNodeList.item(wsdlPortTypeNr);
					if(wsdlPortType.getAttribute("name").equals(wsdlPortTypeName)){
						
						String serviceName = invokeTag.getAttribute("serviceName");
						String portType = invokeTag.getAttribute("portType");
						Element invokeService = createProvideInvokeService(serviceName, portType, docWsdl);
						
						deployInvoke.appendChild(invokeService);
						
//						Element wsdlNameTag = (Element) docWsdl.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "definitions").item(0);
//						String wsdlName = wsdlNameTag.getAttribute("name");
//						String invokeServiceName = wsdlTargetNamespacePrefix + ":" + wsdlName;
////						invokeService.setAttribute("name", invokeServiceName);
//						
////						NodeList wsdlServiceNameNodeList = docWsdl.getElementsByTagName("service");
////						for(int wsdlServiceNameNr = 0; wsdlServiceNameNr < wsdlServiceNameNodeList.getLength(); wsdlServiceNameNr++) {
////							
////							Element wsdlServiceNameTag = (Element) wsdlServiceNameNodeList.item(wsdlServiceNameNr);
////	
////							if(wsdlServiceNameTag.getAttribute("name").equals(wsdlName)) {
////								
////								Element wsdlServicePort = (Element) wsdlServiceNameTag.getElementsByTagName("port").item(0);
////								String invokeServicePort = wsdlServicePort.getAttribute("name");						
////								invokeService.setAttribute("port", invokeServicePort);
////								
////							}
////						}
					}
				}
			}
			invokeList.add(deployInvoke);
		}
		
		return invokeList;
	}
	
	private Element createProvideInvokeService(String serviceName, String portType, Document wsdl) {
		
		Element service = deployDocument.createElement("service");	
		String serviceNamePrefix = getFirstPartofString(portType, ":");
		String name = serviceNamePrefix + ":" + serviceName;
		service.setAttribute("name", name);
		
		setNamespace(deploy , portType);
		String port = getPortForServiceName(serviceName, wsdl);
		service.setAttribute("port", port);
		
		return service;
	}
	
	private void setNamespace(Element document, String portType) {
		
		String prefix = getFirstPartofString(portType, ":");
		String namespaceUri = bpelDocument.lookupNamespaceURI(prefix);
		
		document.setAttribute("xmlns:" + prefix, namespaceUri);		
	}

	private String getPortForServiceName(String serviceName, Document wsdl) {
		
		String port = null;		
		NodeList bpelServiceNodeList = null; 
		if(wsdl == null){
			bpelServiceNodeList = processWsdlDocument.getElementsByTagName("service");
			for(int bpelServiceNodeListNr = 0; bpelServiceNodeListNr < bpelServiceNodeList.getLength(); bpelServiceNodeListNr++){
				Element bpelServiceNode = (Element) bpelServiceNodeList.item(bpelServiceNodeListNr);
				String bpelServiceName = bpelServiceNode.getAttribute("name");
				if(bpelServiceName.equals(serviceName)){
					Element portTag = (Element) bpelServiceNode.getElementsByTagName("port").item(0);
					port = portTag.getAttribute("name");
				}
			}
		}
		else{
			bpelServiceNodeList = wsdl.getElementsByTagName("service");
			
			for(int bpelServiceNodeListNr = 0; bpelServiceNodeListNr < bpelServiceNodeList.getLength(); bpelServiceNodeListNr++){
				Element bpelServiceNode = (Element) bpelServiceNodeList.item(bpelServiceNodeListNr);
				String bpelServiceName = bpelServiceNode.getAttribute("name");
				if(bpelServiceName.equals(serviceName)){
					Element portTag = (Element) bpelServiceNode.getElementsByTagName("port").item(0);
					port = portTag.getAttribute("name");
				}
			}
		}	
		
		return port;
	}

	private Element createServiceElement() {
		String serviceName = getBpelProcessServiceName();
		
		Element service = processWsdlDocument.createElement("service");
		service.setAttribute("name", serviceName);
		
		Element port = processWsdlDocument.createElement("port");
		port.setAttribute("name", "InvokePort");
		port.setAttribute("binding", "tns:WsBinding");
		service.appendChild(port);
		
		Element soapAddress = processWsdlDocument.createElement("soap:address");
		soapAddress.setAttribute("location",  ODE_URL + "/processes/" + serviceName);
		port.appendChild(soapAddress);
		
		return service;
	}

	private String getBpelProcessServiceName() {
		NodeList partnerLinkNodeList = bpelDocument.getElementsByTagName("partnerLink");
		String serviceName = null;
		for(int partnerLinkNodeListNr = 0; partnerLinkNodeListNr < partnerLinkNodeList.getLength(); partnerLinkNodeListNr++){
			
			Element partnerLinkNode = (Element) partnerLinkNodeList.item(partnerLinkNodeListNr);
			String partnerLinkServiceName = partnerLinkNode.getAttribute("serviceName");
			if(partnerLinkServiceName.contains("InvokeProcess")){
				serviceName = partnerLinkServiceName;
				break;
			}
		}
		
		
		this.deployProcessData.add(new TransformationResult(Type.SERVICE_NAME, serviceName));
		return serviceName;
	}

	private Element createBindings() {
		
		Element binding = processWsdlDocument.createElement("binding");
		binding.setAttribute("name", "WsBinding");
		binding.setAttribute("type", "tns:InvokeProcess");
		
		Element soapBinding = processWsdlDocument.createElement("soap:binding");
		soapBinding.setAttribute("style", "document");
		soapBinding.setAttribute("transport", "http://schemas.xmlsoap.org/soap/http");
		binding.appendChild(soapBinding);
		
		Element operation = processWsdlDocument.createElement("operation");
		operation.setAttribute("name", "process");
		binding.appendChild(operation);
		
		String targetNamespace = this.definitions.getAttribute("targetNamespace");
		Element soapOperation = processWsdlDocument.createElement("soap:operation");
		soapOperation.setAttribute("soapAction", targetNamespace);
		operation.appendChild(soapOperation);
		
		Element input = processWsdlDocument.createElement("input");
		operation.appendChild(input);
		
		Element soapBody1 = processWsdlDocument.createElement("soap:body");
		soapBody1.setAttribute("use", "literal");
		input.appendChild(soapBody1);
		
		Element output = processWsdlDocument.createElement("output");
		operation.appendChild(output);
		
		Element soapBody2 = processWsdlDocument.createElement("soap:body");
		soapBody2.setAttribute("use", "literal");
		output.appendChild(soapBody2);
		
		return binding;
	}

	private Element createPortType() {
		Element portType = processWsdlDocument.createElement("portType");
		portType.setAttribute("name", "InvokeProcess");
		
		Element operation = processWsdlDocument.createElement("operation");
		operation.setAttribute("name", "process");
		portType.appendChild(operation);
		
		Element input = processWsdlDocument.createElement("input");
		input.setAttribute("message", "tns:InvokeProcessRequestMessage");
		operation.appendChild(input);
		
		Element output = processWsdlDocument.createElement("output");
		output.setAttribute("message", "tns:InvokeProcessResponseMessage");
		operation.appendChild(output);
		
		return portType;
	}

	private List<Element> createMessages() {
		
		List<Element> messages = new ArrayList<Element>();
		Element message1 = processWsdlDocument.createElement("message");
		message1.setAttribute("name", "InvokeProcessRequestMessage");
		Element part1 = processWsdlDocument.createElement("part");
		message1.appendChild(part1);
		part1.setAttribute("name", "payload");
		part1.setAttribute("element", "tns:InvokeProcessRequest");
		
		Element message2 = processWsdlDocument.createElement("message");
		message2.setAttribute("name", "InvokeProcessResponseMessage");
		Element part2 = processWsdlDocument.createElement("part");
		message2.appendChild(part2);
		part2.setAttribute("name", "payload");
		part2.setAttribute("element", "tns:InvokeProcessResponse");
		
		messages.add(message1);
		messages.add(message2);
		
		return messages;
	}

	private Element createTypes() {
		Element type = processWsdlDocument.createElement("types");
		
		Element schema = processWsdlDocument.createElement("schema");
		type.appendChild(schema);
		
		String targetNamespace = this.definitions.getAttribute("targetNamespace");
		
		schema.setAttribute("attributeFormDefault","unqualified");
		schema.setAttribute("elementFormDefault","qualified");
		schema.setAttribute("xmlns","http://www.w3.org/2001/XMLSchema");
		schema.setAttribute("targetNamespace",targetNamespace);
		
		Element element1 = processWsdlDocument.createElement("element");
		element1.setAttribute("name", "InvokeProcessRequest");
		Element complexType1 = processWsdlDocument.createElement("complexType");
		Element sequence1 = processWsdlDocument.createElement("sequence");
		Element element11 = processWsdlDocument.createElement("element");
		element11.setAttribute("name", "token");
		element11.setAttribute("type", "string");
		Element element12 = processWsdlDocument.createElement("element");
		element12.setAttribute("name", "reportingServiceUrl");
		element12.setAttribute("type", "string");
		
		Element element2 = processWsdlDocument.createElement("element");
		element2.setAttribute("name", "InvokeProcessResponse");
		Element complexType2 = processWsdlDocument.createElement("complexType");
		Element sequence2 = processWsdlDocument.createElement("sequence");
		Element element21 = processWsdlDocument.createElement("element");
		element21.setAttribute("name", "result");
		element21.setAttribute("type", "string");
		
		schema.appendChild(element1);
		element1.appendChild(complexType1);
		complexType1.appendChild(sequence1);		
		sequence1.appendChild(element11);
		sequence1.appendChild(element12);
		
		schema.appendChild(element2);
		element2.appendChild(complexType2);
		complexType2.appendChild(sequence2);		
		sequence2.appendChild(element21);
		
		return type;		
	}

	private List<Element> createPartnerLinkType(NodeList partnerLinks) {

		List<Element> partnerLinkTypes = new ArrayList<Element>();
		for(int partnerLinksNr = 0; partnerLinksNr < partnerLinks.getLength(); partnerLinksNr++){
			
			Element partnerLinkNode = (Element) partnerLinks.item(partnerLinksNr);
			String partnerLinkTypeTag = partnerLinkNode.getAttribute("partnerLinkType");
			
			Element partnerLinkType = processWsdlDocument.createElement(partnerLinkNamespaceTag + ":partnerLinkType");
			String partnerTypeLinkName = getLastPartofString(partnerLinkTypeTag, ":");
			partnerLinkType.setAttribute("name", partnerTypeLinkName);
			
			partnerLinkType.appendChild(createPartnerLinkTypeRole(partnerLinkNode, partnerLinksNr + 1));
			partnerLinkTypes.add(partnerLinkType);
		}
		return partnerLinkTypes;
	}
	
	private Element createPartnerLinkTypeRole(Element partnerLinkNode, int partnerLinkNr) {
		
		Element partnerLinkTypeRole = processWsdlDocument.createElement(partnerLinkNamespaceTag + ":role");
		String partnerTypeLinkRoleName = partnerLinkNode.getAttribute("partnerRole");
		if(partnerTypeLinkRoleName.length() == 0){
			partnerTypeLinkRoleName = partnerLinkNode.getAttribute("myRole");
		}
		partnerLinkTypeRole.setAttribute("name", partnerTypeLinkRoleName);
		
		String partnerLinkServiceName = partnerLinkNode.getAttribute("serviceName");
		String BpelInvokePortType = getPortTypeForServiceName(partnerLinkServiceName);
		String partnerLinkTypePortTypePrefix = null;
		
		if(BpelInvokePortType == null){
			BpelInvokePortType = getBpelTnsAndServiceName();
			partnerLinkTypePortTypePrefix = "tns";
		}
		else{
			partnerLinkTypePortTypePrefix = "wsdl" + partnerLinkNr;
		}
		
		String partnerLinkTypePortType = getLastPartofString(BpelInvokePortType, ":"); 
		String partnerLinkNamespacePrefix = getFirstPartofString(BpelInvokePortType, ":");
		String partnerLinkNamespace = bpelDocument.lookupNamespaceURI(partnerLinkNamespacePrefix);
		
		partnerLinkTypeRole.setAttribute("portType", partnerLinkTypePortTypePrefix + ":" + partnerLinkTypePortType);
		
		setNamespacePrefix(partnerLinkTypePortTypePrefix, partnerLinkNamespace);
		
		return partnerLinkTypeRole;
	}
	
	private List<Element> copyImports(NodeList importsNodeList) {
		
		List<Element> imports = new ArrayList<Element>();
		for(int importsNodeListNr = 0; importsNodeListNr < importsNodeList.getLength(); importsNodeListNr++){
			
			Element importNode = (Element) importsNodeList.item(importsNodeListNr);
			String importNodeLocation = importNode.getAttribute("location");
			String importNodeNamespace = importNode.getAttribute("namespace");
			String wsdlNamespace = bpelDocument.lookupNamespaceURI("tns");
			
			if(!importNodeNamespace.equals(wsdlNamespace)){
			
				Element importTag = processWsdlDocument.createElement("import");
				importTag.setAttribute("location", importNodeLocation);
				importTag.setAttribute("namespace", importNodeNamespace);
				
				imports.add(importTag);
			}
		}
		return imports;
	}

	/**
	 * Create a w3c document from a given URI.
	 * 
	 * @param uri
	 * @return
	 * @throws IOException 
	 */
	private Document createDocumentFromUrl(String wsdlUrl) {
		try {
			URL url = new URL(wsdlUrl);
			URLConnection connection = url.openConnection();
			
			BufferedReader responseReader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			
			String line;
	        String responseText = "";
	        while ((line = responseReader.readLine()) != null) {
	            responseText += line;
	        }
			
			Document wsdlDocument = createDocumentFromString(responseText);
			Element defElement = (Element) wsdlDocument.getElementsByTagName("definitions").item(0);
			String serviceName = defElement.getAttribute("name");
			this.wsdls.put(serviceName, responseText);
			return wsdlDocument;
		} catch (Exception e) {
			logger.error("Cannnot create document from uri", e);
		} 	
		return null;
	}
	
	/**
	 * Create a w3c document from a given string.
	 * 
	 * @param stringVar
	 * @return
	 * @throws BuildBpelAccessoriesFailException
	 */
	private Document createDocumentFromString(String stringVar) {
		
		/* Wrap WSDL string in an InputSource for the DocumentBuilder */
		InputSource stringSource         = new InputSource(new StringReader(stringVar));
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		
		/* Activate validation and namespace support */
		documentBuilderFactory.setValidating(false);
		documentBuilderFactory.setNamespaceAware(true);
		
		/* Create the w3c.dom.Document */
		Document docDocument = null;
		try {
			docDocument = documentBuilderFactory.newDocumentBuilder().parse(stringSource);
		} catch (Exception e) {
			logger.error("Cannnot create document from string", e);
		} 	
		return docDocument;
	}
	
	/**
	 * Create a string from a given w3c document.
	 * 
	 * @param docDocument
	 * @return
	 * @throws BuildBpelAccessoriesFailException
	 */
	private String createStringFromDocument(Document docDocument) {
		
		OutputFormat format = new OutputFormat(docDocument);
		StringWriter stringOut = new StringWriter();
		XMLSerializer serial = new XMLSerializer(stringOut, format);
		try {
			serial.serialize(docDocument);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String docOutString = stringOut.toString();
		
		return docOutString;
		
		
		
//		Source source = new DOMSource(docDocument);
//		StringWriter stringWriter = new StringWriter();
//		Result result = new StreamResult(stringWriter);
//		
//		//TODO fill exceptions messageses
//		try {
//			TransformerFactory transformerFactory = TransformerFactory.newInstance();
//			Transformer transformer = transformerFactory.newTransformer();
//			transformer.transform(source, result);
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		
//		String docDocumentString = stringWriter.getBuffer().toString();
//		
//		return docDocumentString;
	}

	private String getPortTypeForServiceName(String servicename) {
		
		String portType = null;
		NodeList invokeNodeList = bpelDocument.getElementsByTagName("invoke");
		for(int invokeNodeListNr = 0; invokeNodeListNr < invokeNodeList.getLength();invokeNodeListNr++){
			Element invokeNode = (Element) invokeNodeList.item(invokeNodeListNr);
			String invokeNodeServiceName = invokeNode.getAttribute("serviceName");
			if(invokeNodeServiceName.equals(servicename)){
				portType = invokeNode.getAttribute("portType");
			}
		}
		
		return portType;
	}
	
	/**
	 * Used to get the last part of a string. The String will be 
	 * splitted at the position of the given separator.
	 * 
	 * @param identifier
	 * @param separator
	 * @return
	 */
	private String getLastPartofString(String identifier, String separator){		
		return identifier.substring(identifier.lastIndexOf(separator) + 1);		
	}
	
	/**
	 * Used to get the first part of a string. The String will be 
	 * splitted at the position of the given separator.
	 * 
	 * @param identifier
	 * @param separator
	 * @return
	 */
	private String getFirstPartofString(String identifier, String separator){
		return identifier.substring(0, identifier.lastIndexOf(separator));
	}
	
	private String getBpelTnsAndServiceName(){
		Element bpelRootElement = (Element) bpelDocument.getElementsByTagNameNS("http://docs.oasis-open.org/wsbpel/2.0/process/executable", "process").item(0);
		String serviceName = "InvokeProcess";
		String targetNamespace = bpelRootElement.getAttribute("targetNamespace");
		String targetNamespacePrefix = bpelRootElement.lookupPrefix(targetNamespace);
		
		return targetNamespacePrefix + ":" + serviceName;
	}
	
	private void setNamespacePrefix(String prefix, String url){		
		this.definitions.setAttribute("xmlns:" + prefix, url);
	}
	
	public void deployProcessOnApacheOde() {
		DeploymentServiceLayer deploymentServiceLayer = 
			new DeploymentServiceLayer(this.ODE_URL);
		
		DeployUnit du = deploymentServiceLayer.deploy(
				"myProcess", 
				createStringFromDocument(this.bpelDocument), 
				createStringFromDocument(this.processWsdlDocument), 
				createStringFromDocument(this.deployDocument), 
				this.wsdls);
		
		String name = du.getName();
	}
}
