package de.hpi.bpel4chor.transformation;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import de.hpi.bpel4chor.transformation.factories.ProcessFactory;
import de.hpi.bpel4chor.transformation.factories.TopologyFactory;
import de.hpi.bpel4chor.util.Output;

import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.Pool;
import de.hpi.bpel4chor.model.PoolSet;

/**
 * This class transforms the parsed diagram to BPEL4Chor.
 */
public class Transformation {
	
	/**
	 * Serializes a DOM document to String.
	 * 
	 * @param document The document to serialize.
	 * @param output   The Output to print errors to.
	 * 
	 * @return The serialized document as string.
	 */
	private String domToString(Document document, Output output) {
		Source source = new DOMSource(document);
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys. INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");
			
			StringWriter sw=new StringWriter();
            StreamResult resultStream = new StreamResult(sw);
            transformer.transform(source, resultStream);
            return sw.toString();
		} catch (TransformerException e) {
			output.addError(e);
		}
		return output.getErrors();
	}
	
	/**
	 * Transforms the diagram to BPEL4Chor.
	 * First the topology will be transformed using the TopologyFactory.
	 * Then each process will be transformed using the ProcessFactory.
	 * 
	 * @param diagram The diagram to transform.
	 * 
	 * @return A string array containing the topology as first element 
	 * followed by the processes. If an error occured during the 
	 * transformation the string array has only one element that contains 
	 * this error serialized as string. 
	 */
	public String[] transform(Diagram diagram) {
		List<String> result = new ArrayList<String>();
		
		if (diagram == null) {
			result.add("Now diagram found.");
		} else {
			TransformationErrors topOutput = new TransformationErrors();
			Document topology = new TopologyFactory(diagram, topOutput).transformTopology();
			
			ProcessFactory factory = new ProcessFactory(diagram);
			if (topOutput.isEmpty()) {
				result.add(domToString(topology, topOutput));
			} else {
				result.add(topOutput.getErrors());				
			}
			
			for (Iterator<Pool> it = diagram.getPools().iterator(); it.hasNext();) {
				TransformationErrors processOutput = new TransformationErrors();
				Document process = factory.transformProcess(it.next(), processOutput);
				if (processOutput.isEmpty()) {
					result.add(domToString(process, processOutput));
				} else {
					result.add(processOutput.getErrors());
				}
			}
			
			for (Iterator<PoolSet> it = diagram.getPoolSets().iterator(); it.hasNext();) {
				TransformationErrors processOutput = new TransformationErrors();
				Document process = factory.transformProcess(it.next(), processOutput);
				if (processOutput.isEmpty()) {
					result.add(domToString(process, processOutput));
				} else {
					result.add(processOutput.getErrors());
				}
			}
		}
		
		return result.toArray(new String[result.size()]);
	}	
}
