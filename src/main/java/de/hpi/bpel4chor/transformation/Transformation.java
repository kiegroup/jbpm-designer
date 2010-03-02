package de.hpi.bpel4chor.transformation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;

import de.hpi.bpel4chor.transformation.TransformationResult.Type;
import de.hpi.bpel4chor.transformation.factories.ProcessFactory;
import de.hpi.bpel4chor.transformation.factories.TopologyFactory;
import de.hpi.bpel4chor.util.Output;

import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.Pool;
import de.hpi.bpel4chor.model.PoolSet;
import de.hpi.bpel4chor.parser.Parser;

/**
 * This class transforms the parsed diagram to BPEL4Chor.
 */
public class Transformation {
	
	private static Logger logger = Logger.getLogger(Transformation.class.toString());
	
	/**
	 * Transforms the diagram to BPEL4Chor.
	 * First the topology will be transformed using the TopologyFactory.
	 * Then each process will be transformed using the ProcessFactory.
	 * 
	 * @param diagram The diagram to transform.
	 * 
	 * @return A list of pairs of (boolean,string). 
	 * boolean denotes whether the transformation was successful
	 * string contains either the error message or the result
	 * The first element of the list is the topology, subsequent elements are BPEL processes
	 */
	private List<TransformationResult> transform(Diagram diagram) {
		List<TransformationResult> result = new ArrayList<TransformationResult>();
		
		if (diagram == null) {
			result.add(new TransformationResult(Type.DIAGRAM, new Output("No diagram found.")));
		} else {
			Output topOutput = new Output();
			Document topology = new TopologyFactory(diagram, topOutput).transformTopology();
			if (topOutput.isEmpty()) {
				result.add(new TransformationResult(Type.TOPOLOGY, topology));
			} else {
				result.add(new TransformationResult(Type.TOPOLOGY, topOutput));				
			}
			
			ProcessFactory factory = new ProcessFactory(diagram);
			for (Iterator<Pool> it = diagram.getPools().iterator(); it.hasNext();) {
				Output processOutput = new Output();
				Document process = factory.transformProcess(it.next(), processOutput);
				if (processOutput.isEmpty()) {
					result.add(new TransformationResult(Type.PROCESS, process));
				} else {
					result.add(new TransformationResult(Type.PROCESS, processOutput, process));				
				}
			}
			
			for (Iterator<PoolSet> it = diagram.getPoolSets().iterator(); it.hasNext();) {
				Output processOutput = new Output();
				Document process = factory.transformProcess(it.next(), processOutput);
				if (processOutput.isEmpty()) {
					result.add(new TransformationResult(Type.PROCESS, process));
				} else {
					result.add(new TransformationResult(Type.PROCESS, processOutput, process));				
				}
			}
		}
		
		return result;
	}	

	/**
	 * Implementation of the service operation transform.
	 * 
	 * First the given string will be parsed. 
	 * Then it will be transformed to BPEL4Chor.
	 * 
	 * The result is an array that either holds the topology (first String in array)
	 * and each processes as string or it holds the error information that were generated 
	 * during the parsing or transformation. Errors are combined as string and they are 
	 * located as first element of the array.
	 * 
	 * @param diagramStr the diagram as XPDL4Chor serialized to a string
	 * @param validate if true, the diagram will be validated against the XPDL4Chor schema 
	 * before the parsing starts
	 * 
	 * @return the topology (first element) and processes serialized as strings or
	 * error messages (first element)
	 */
	public List<TransformationResult> transform(String diagramStr, boolean validate) {
		logger.log(Level.FINE, "Start parsing");
		Output parserOutput = new Output();
		Diagram diagram = Parser.parse(diagramStr, validate, parserOutput);
		logger.log(Level.FINE, "Finished parsing");
		
		List<TransformationResult> result;
		if (parserOutput.isEmpty()) {
			logger.log(Level.FINE, "Start Transformation");
			result = transform(diagram);
			logger.log(Level.FINE, "Finished Transformation");
			return result;
		} else {
			result = new ArrayList<TransformationResult>();
			result.add(new TransformationResult(Type.DIAGRAM, parserOutput));
			return result;
		}
	}
}
