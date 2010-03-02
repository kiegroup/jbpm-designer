/**
 * 
 */
package de.hpi.bpmn2bpel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;


import de.hpi.bpel4chor.util.Output;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Task;
import de.hpi.bpmn2bpel.TransformationResult.Type;
import de.hpi.bpmn2bpel.factories.DeploymentDescriptorFactory;
import de.hpi.bpmn2bpel.factories.ProcessFactory;
import de.hpi.bpmn2bpel.factories.apacheode.DeployProcessFactory;

/**
 * Copyright (c) 2009 Falko Menge
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * @author Falko Menge
 *
 */
public class BPMN2BPELTransformer {
	
	/**
	 * Transforms a BPMN diagram into an executable BPEL process for Apache ODE.
	 * 
	 * @param diagram
	 * 		The BPMN diagram
	 * @return
	 * 		All necessary elements of an Apache ODE Deployment unit. The BPEL 
	 * 		process, service's WSDLs, process's WSDL and deployment descriptor. 
	 */
	public List<TransformationResult> transform(BPMNDiagram diagram) {
		Document process = null;
		List<TransformationResult> results = new ArrayList<TransformationResult>();
		ProcessFactory factory = new ProcessFactory(diagram);
		
		// use only the first pool
		// TODO rewrite to work without pools 
		Iterator<de.hpi.bpmn.Pool> it = diagram.getPools().iterator();
		if (it.hasNext()) {
			Output processOutput = new Output();
			process = factory.transformProcess(it.next(), processOutput);
			results.add(new TransformationResult(Type.PROCESS, process));
		}
		
		
		return results;
	}
	
	/**
	 * Transforms the processes contained by die BPMN-Diagram and deploys the 
	 * business process on Apache ODE BPEL-Engine.
	 * 
	 * @param diagram
	 * 		The {@link BPMNDiagram}
	 * @param apacheOdeUrl
	 * 		The URL to Apache ODE root
	 * @return
	 * 		The result elements of the transformation
	 */
	public List<TransformationResult> transformAndDeployProcessOnOde(
			BPMNDiagram diagram,
			String apacheOdeUrl) {
		Set<String> wsdlUrls = this.getSetOfWsdlUrls(diagram);
		
		/* Transform BPMN to BPEL */
		List<TransformationResult> results = this.transform(diagram);
		
		/* Create deployment descriptor for Apache ODE */ 
		DeployProcessFactory deploymentFactory = 
			DeployProcessFactory.getNewDeployProcessFactory(apacheOdeUrl);
		List<TransformationResult> deploymentElements = 
			deploymentFactory.buildDeployProcessData(results.get(0), wsdlUrls);
		
		deploymentFactory.deployProcessOnApacheOde();
		
		/* Append Apache ODE Deployment Descriptor and Process WSDL */
		results.addAll(deploymentElements);
		
		
		return results;
	}
	
	/**
	 * Retrieves the set of used WSDL URLs in the BPMN diagram.
	 * 
	 * @param diagram
	 * 		The BPMN diagram
	 * @return
	 * 		The set of WSDL URLs
	 */
	private Set<String> getSetOfWsdlUrls(BPMNDiagram diagram) {
		HashSet<String> wsdlUrls = new HashSet<String>();
		
		for(Node node : diagram.getChildNodes()) {
			if(node instanceof Task) {
				wsdlUrls.add(((Task) node).getWsdlUrl());
			}
		}
		
		return wsdlUrls;
	}
}
