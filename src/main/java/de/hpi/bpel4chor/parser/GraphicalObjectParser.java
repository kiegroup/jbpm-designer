package de.hpi.bpel4chor.parser;

import de.hpi.bpel4chor.model.GraphicalObject;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.hpi.bpel4chor.util.Output;

/**
 * This class parses the attributes that are common for each graphical object.
 * 
 * This parser should be used for all graphical objects before the additional 
 * information is parsed.
 */
public class GraphicalObjectParser {
	
	private static final String ID = "Id";

	/**
	 * Parses the attributes that are common for each graphical object.
	 * 
	 * If the graphical object does not specify an id, an error is added
	 * to the output.
	 * 
	 * @param object     The graphical object to store the parsed information into.
	 * @param objectNode The graphical object node to be parsed.
	 * @param output     The Output to print the errors to.
	 */
	public static void parse(GraphicalObject object, Node objectNode, Output output) {
		NamedNodeMap attributes = objectNode.getAttributes();
		if (attributes.getNamedItem(ID) == null) {
			output.addParseError("A graphical object does " +
					"not have a specified Id.", objectNode);			
		} else {
			object.setId(attributes.getNamedItem(ID).getNodeValue());
		}
	}
}
