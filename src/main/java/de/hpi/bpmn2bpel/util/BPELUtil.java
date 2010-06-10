package de.hpi.bpmn2bpel.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

import de.hpi.bpel4chor.model.activities.Activity;
import de.hpi.bpel4chor.model.supporting.Correlation;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Task;

/**
 * This class provides helper methods for generating the BPEL4Chor elements.
 */
public class BPELUtil {
	
	/**
	 * Generates a "yes"/"no" value from the given boolean value.
	 * 
	 * @param value The boolean value to transform to "yes" or "no"
	 * 
	 * @return "yes" if the boolean value is true, "no" otherwise.
	 */
	public static String booleanToYesNo(boolean value) {
		if (value) {
			return "yes";
		}
		return "no";
	}
	
	/**
	 * Generates the name of the scope from the given activity.
	 * The pattern for the name is: "generatedScope_" + activityName
	 * 
	 * @param act The activity to generate a scope name for
	 * 
	 * @return The generated scope name.
	 */
	public static String generateScopeName(Activity act) {
		return "generatedScope_" + act.getName();
	}
	
	/**
	 * Determines the value of an initiate attribute for the 
	 * given correlation (see {@link Correlation#INITIATE_NO},
	 * {@link Correlation#INITIATE_YES} and {@link Correlation#INITIATE_JOIN}).
	 * 
	 * @param correlation The correlation to generate the initiate
	 *                    value from.
	 * 
	 * @return "no", "yes" or "join" depending on the initiate
	 * of the Correlation object.
	 */
	public static String getInitiate(Correlation correlation) {
		String initiate = correlation.getInitiate();
		if (initiate != null) {
			if (initiate.equals(Correlation.INITIATE_NO)) {
				return "no";
			} else if (initiate.equals(Correlation.INITIATE_YES)) {
				return "yes";
			} else if (initiate.equals(Correlation.INITIATE_JOIN)) {
				return "join";
			}
		}
		return null;
	}
	
	/**
	 * Determines the value of a pattern attribute for the 
	 * given correlation (see {@link Correlation#PATTERN_REQUEST},
	 * {@link Correlation#PATTERN_RESPONSE} and 
	 * {@link Correlation#PATTERN_REQUEST_RESPONSE}).
	 * 
	 * @param correlation The correlation to generate the pattern
	 *                    value from.
	 * 
	 * @return "request", "response" or "request-response" depending on
	 * the pattern of the Correlation object.
	 */
	public static String getPattern(Correlation correlation) {
		String pattern = correlation.getPattern();
		if (pattern != null) {
			if (pattern.equals(Correlation.PATTERN_REQUEST)) {
				return "request";
			} else if (pattern.equals(Correlation.PATTERN_RESPONSE)) {
				return "response";
			} else if (pattern.equals(Correlation.PATTERN_REQUEST_RESPONSE)) {
				return "request-response";
			}
		}
		return null;
	}
	
	/**
	 * Transforms a string value to an NCName.
	 * 
	 * <p>If the string value does not start with a letter or a "_" character,
	 * a "_" character is added at the beginning of the value.</p>
	 * 
	 * <p>":" and " " are removed from the string value and the following 
	 * character becomes an upper case.</p>
	 * 
	 * <p>If the string value is the empty string or "##oaque" it will not
	 * be transformed.</p>
	 * 
	 * @param name The string value to transform.
	 * @return The resulting NCName.
	 */
	public static String stringToNCName(String name) {
		if ((name == null) ||  name.equals("") || name.equals("##opaque")) {
			return name;
		}
		String result = name;
		if (!Character.isLetter(result.charAt(0)) && !result.startsWith("_")) {
			result = "_" + result; 
		}
		
		int index = result.indexOf(':');
		if (index < 0 ) {
			index = result.indexOf(' ');
		}
		while (index >= 0) {
			String toUpper = result.substring(index + 1, index + 2);
			
			result = result.substring(0, index) + toUpper.toUpperCase() + result.substring(index + 2);
			
			index = result.indexOf(':');
			if (index < 0 ) {
				index = result.indexOf(' ');
			}
		}
		return result;
	}
	
	/**
	 * Sets set standard attributes of a BPEL4Chor activity.
	 * 
	 * @param element The BPEL4Chor activity element to set the attributes for.
	 * @param act     The activity to get the attribute values from.
	 */
	public static void setStandardAttributes(Element element, Node node) {
		if (node.getLabel() != null) {
			element.setAttribute("name", node.getLabel());
		}
//		if (act.getSuppressJoinFailure() != null) {
//			element.setAttribute("suppressJoinFailure", 
//				act.getSuppressJoinFailure());
//		}
	}
	
	/**
	 * Creates the String representation of a XML node and returns it.
	 * 
	 * @param node
	 * 		The source XML node
	 * @return
	 * 		The XML node as a String
	 */
	public static String xmlNodeToString(org.w3c.dom.Node node) {
		String xmlString = null;
		
		try {
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(node);
			transformer.transform(source, result);
			xmlString = result.getWriter().toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return xmlString;
	}
	
	/**
	 * Returns a distinct list of services
	 * 
	 * @param services
	 * 		The source services list
	 * @return
	 * 		The resulting distinct service list
	 */
	public static List<Task> getDistinctServiceList(List<Task> services) {
		ArrayList<Task> distinctServices = new ArrayList<Task>();
		
		for (Task service : services) {
			if(!isServiceContainedInList(service, distinctServices)) {
				distinctServices.add(service);
			}
		}
		
		return distinctServices;
	}
	
	/**
	 * Returns true if service is contained in services.
	 *  
	 * @param service
	 * @param services
	 * 
	 * @return
	 * 		The search result
	 */
	private static boolean isServiceContainedInList(Task service, List<Task> services) {
		for (Task task : services) {
			if (task.describesEqualService(service)) {
				return true;
			}
		}
		
		return false;
	}
}
