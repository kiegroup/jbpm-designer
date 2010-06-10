package de.hpi.bpel4chor.util;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class provides helper methods for handling XML documents.
 */
public class XMLUtil {
	
	/** 
	 * Serialized a node to a string. XML declarations will be omitted. 
	 * 
	 * @param node The node to be serialized.
	 * 
	 * @return The serialized node.
	 * 
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static String nodeToString(Node node) throws 
			TransformerFactoryConfigurationError, TransformerException {
		Transformer transformer = 
			TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty("omit-xml-declaration", "yes"); 
		StringWriter sw = new StringWriter();
		transformer.transform(new DOMSource(node), new StreamResult(sw));
		return sw.toString();
	}

	/**
	 * Returns the value of a node as string. Of the node has 
	 * child nodes these will also be serialized as string.
	 * 
	 * @param node   The node to get the value from.
	 * @param output The ouput to print errors to.
	 * 
	 * @return The serialized node value.
	 */
	public static String getNodeValue(Node node, Output output) {
        String result = "";
        NodeList children = node.getChildNodes();
        try {
	        for (int i = 0; i < children.getLength(); i++) {
	            Node textChild = children.item(i);
	            if (textChild.getNodeType() != Node.TEXT_NODE) {
	            	result = result + nodeToString(node);
	            } else {
	            	result = result + textChild.getNodeValue();
	            }
	        }
        } catch (TransformerException e) {
        	output.addError(e);
        } catch (TransformerFactoryConfigurationError e) {
        	output.addError(e);
        }
        return result.trim();
	}
	
	/**
	 * Determines the first child node of the parent with the specified tag
	 * name.
	 * 
	 * @param parent   The parent node of the child node to be determined.
	 * @param nodeName The name of the child node to be determined.
	 * 
	 * @return The determined child node with the given name.
	 */
	public static Node getChildWithName(Node parent, String nodeName) {
		if (parent != null) {
			NodeList childs = parent.getChildNodes();
			for (int i = 0; i < childs.getLength(); i++) {
				Node child = childs.item(i);
				if ((child.getLocalName() != null) && 
						child.getLocalName().equals(nodeName)) {
					return childs.item(i);	
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the first node in the node list that is an element.
	 * 
	 * @param nodes The list of nodes that contains the element to be
	 *              determined.
	 * 
	 * @return The first element in the given node list.
	 */
	public static Node getFirstElement(NodeList nodes) {
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				return node;
			}
		}
		return null;
	}
}
