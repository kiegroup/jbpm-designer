package de.hpi.bpmn2bpel.factories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.Task;
import de.hpi.bpmn.Process;
import de.hpi.bpmn2bpel.model.Container4BPEL;

/**
 * This class creates the deployment descriptor for the Apache ODE BPEL-Engine.
 * 
 * <p>The basic steps are: </p>
 * <ol>
 * 	<li>Create the {@literal deploy} root element and insert the used namespaces.</li>
 *  
 *  <li>Create a {@literal process} child element for each process</li>
 *  
 *  <li>Insert a provide element to enable invoking the process as a service.</li>
 *  
 *  <li>Step over all {@link Task}s and insert the appropriate provide or invoke
 *  	element for it. 
 *  </li>
 * </ol>
 * @author Sven Wagner-Boysen
 *
 */
public class DeploymentDescriptorFactory {
	private BPMNDiagram diagram;
	
	public DeploymentDescriptorFactory(BPMNDiagram diagram) {
		this.diagram = diagram;
	}
	
	/**
	 * Entry point to generate the deployment descriptor.
	 * 
	 * @return
	 * 		The deployment descriptor XML document
	 */
	public Document generateDeploymentDescriptor() {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.newDocument();
			
			/* Create deploy root element */
			Element deploy = createDeployElement(document);
			
			/* Create process elements */
			List<Element> processes = createProcessElements(document);
			
			/* Append child nodes */
			for(Element process : processes) {
				deploy.appendChild(process);
			}
			document.appendChild(deploy);
			
			return document;
			
			
		} catch (ParserConfigurationException e) {
			//TODO Exception handling
			e.printStackTrace();
			
		}
		return null;
	}
	
	/**
	 * Creates the deploy tag root element and sets standard attributes like
	 * namespaces.
	 * 
	 * @param document
	 * 		The deployment descriptor XML document
	 * @return
	 * 		The deploy element
	 */
	private Element createDeployElement(Document document) {
		Element deploy = document.createElement("deploy");
		
		/* Set attributes */
		//TODO: Set namespaces
		return deploy;
	}
	
	/**
	 * Creates a process element for each {@link Process}.
	 * 
	 * @param document
	 * 		The deployment descriptor document
	 * @return
	 * 		A list of process elements
	 */
	private List<Element> createProcessElements(Document document) {
		ArrayList<Element> processes = new ArrayList<Element>();
		
		for(Container container : diagram.getProcesses()) {
			
			if (!(container instanceof Container4BPEL)) {
				//TODO: Exception maybe necessary
				continue;
			}
			
			Process process = (Process) container;
			Element processElement = document.createElement("process");
			
			/* Set process attributes */
			//TODO: Set process deploy attributes
			
			/* Insert provide element for process service */
			Element processProvideServiceElement = createProvideElementForProcess(process, document);
			processElement.appendChild(processProvideServiceElement);
			
			/* Insert invoke|provide element for each task */
			insertElementsForTasks(document, processElement, process);
			
			processes.add(processElement);
			
		}
		
		return processes;
	}
	
	/**
	 * Creates an invoke element for each task and appends it to the process
	 * element.
	 * 
	 * @param document
	 * 		Deployment descriptor XML document
	 * @param processElement
	 * 		The process XML element
	 * @param process
	 * 		The BPMN process
	 */
	private void insertElementsForTasks(Document document,
			Element processElement, Process process) {
		for(Task t : process.getTasks()) {
			Element invoke = document.createElement("invoke");
//			invoke.setAttribute("name", t.getLabel());
			
			//TODO handle partner links
			
			processElement.appendChild(invoke);
		}
		
	}
	
	/**
	 * Creates the provide element for the process service.
	 * 
	 * @param process
	 * 		The BPMN process
	 * @param document
	 * 		The XML document
	 * @return
	 * 		The resulting provide element
	 */
	private Element createProvideElementForProcess(Process process, Document document) {
		Element processProvide = document.createElement("provide");
		//TODO Handle partner links etc.
		
		return processProvide;
	}
}
