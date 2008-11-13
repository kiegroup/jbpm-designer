package de.hpi.bpel4chor.transformation;


import java.util.ArrayList;
import java.util.List;

import de.hpi.bpel4chor.parser.Parser;
import de.hpi.bpel4chor.parser.ParserErrors;

import de.hpi.bpel4chor.model.Diagram;

/**
 * Implementation of the web service operations.
 * 
 */
public class XPDL4Chor2BPEL4Chor {

	/**
	 * Implementation of the web service operation transform.
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
		System.out.println("Start parsing");
		ParserErrors parserOutput = new ParserErrors();
		Diagram diagram = Parser.parse(diagramStr, validate, parserOutput);
		System.out.println("Finished parsing");
		List<TransformationResult> result;
		
		if (parserOutput.isEmpty()) {
			System.out.println("Start Transformation");
			Transformation transformation = new Transformation();
			result = transformation.transform(diagram);
			System.out.println("Finished Transformation");
			return result;
		} else {
			result = new ArrayList<TransformationResult>();
			result.add(new TransformationResult(false, parserOutput.getErrors()));
			return result;
		}
	}
}
