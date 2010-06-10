package de.hpi.bpel4chor.parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hpi.bpel4chor.util.BPELUtil;
import de.hpi.bpel4chor.util.Output;
import de.hpi.bpel4chor.util.XMLUtil;
import de.hpi.bpel4chor.model.supporting.Copy;
import de.hpi.bpel4chor.model.supporting.Correlation;
import de.hpi.bpel4chor.model.supporting.CorrelationSet;
import de.hpi.bpel4chor.model.supporting.Expression;
import de.hpi.bpel4chor.model.supporting.FromPart;
import de.hpi.bpel4chor.model.supporting.FromSpec;
import de.hpi.bpel4chor.model.supporting.Import;
import de.hpi.bpel4chor.model.supporting.Loop;
import de.hpi.bpel4chor.model.supporting.ToPart;
import de.hpi.bpel4chor.model.supporting.ToSpec;

/**
 * This class is used for parsing supporting elements like expression, copy,
 * or loop elements. 
 */
public class SupportingParser {
	
	// expression
	private static final String EXPRESSION_LANGUAGE = "ScriptGrammar";
	
	// copy
	private static final String KEEP_SRC_ELEMENT_NAME = "KeepSrcElementName";
	private static final String IGNORE_MISSING_FROM_DATA = "IgnoreMissingFromData";
	private static final String FROM_SPEC = "FromSpec";
	private static final String TO_SPEC = "ToSpec";
	
	// correlation
	private static final String CORRELATION = "Correlation";
	private static final String SET = "Set";
	private static final String INITIATE = "Initiate";
	private static final String PATTERN = "Pattern";
	
	// from part
	private static final String FROM_PART = "FromPart";
	private static final String PART = "Part";
	private static final String TO_VARIABLE = "ToVariable";
	
	// to part
	private static final String TO_PART = "ToPart";
	private static final String FROM_VARIABLE = "FromVariable";
	
	// from spec
	private static String TYPE = "Type";
	private static String VARIABLE_NAME = "VariableName";
	private static String PROPERTY = "Property";
	private static String EXPRESSION = "Expression";
	private static String QUERY_LANGUAGE = "QueryLanguage";
	private static String QUERY = "Query";
	private static String LITERAL = "Literal";
	
	// import
	private static final String NAMESPACE = "Namespace";
	private static final String LOCATION = "Location";
	private static final String IMPORT_TYPE = "ImportType";
	private static final String PREFIX = "Prefix";
	
	// loop
	private static final String LOOP_TYPE = "LoopType";
	private static final String TEST_TIME = "TestTime";
	private static final String LOOP_STANDARD = "LoopStandard";
	private static final String LOOP_MULTIPLE_INSTANCE = "LoopMultiInstance";
	private static final String LOOP_CONDITION = "LoopCondition";
	private static final String LOOP_CONDITION_LANGUAGE = "LoopConditionLanguage";
	private static final String ORDERING = "MI_Ordering";
	private static final String SUCCESSFUL_BRANCHES_ONLY = "SuccessfulBranchesOnly";
	private static final String START_COUNTER_VALUE = "StartCounterValue";
	private static final String FINAL_COUNTER_VALUE = "FinalCounterValue";
	private static final String COMPLETION_CONDITION = "CompletionCondition";
	
/********************** expression ***************************************************/
	
	/**
	 * Parses an expression node and creates a new Expression object containing
	 * the parsed information.
	 * 
	 * @param expressionNode The expression node to be parsed.
	 * @param output         The output to print errors to.
	 * 
	 * @return The created and filled Expression object.
	 */
	public static Expression parseExpression(Node expressionNode, Output output) {
		Expression exp = new Expression();
		
		Node languageAttr = 
			expressionNode.getAttributes().getNamedItem(EXPRESSION_LANGUAGE);
		if (languageAttr != null) {
			exp.setExpressionLanguage(languageAttr.getNodeValue());
		}
		String value = XMLUtil.getNodeValue(expressionNode, output);
		if (!value.equals("")) {
			exp.setExpression(value);
		}
		
		return exp;
	}
	
/********************** copy ***************************************************/
	
	/**
	 * Parses the attributes of a copy node and adds the information to a copy
	 * object.
	 * 
	 * @param copyNode The copy node to be parsed.
	 * @param copy     The copy object to add the parsed information to.
	 */
	private static void parseCopyAttributes(Copy copy, Node copyNode) {
		NamedNodeMap attributes = copyNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(KEEP_SRC_ELEMENT_NAME)) {
				copy.setKeepSrcElementName(BPELUtil.booleanToYesNo( 
					new Boolean(attribute.getNodeValue()).booleanValue()));
			} else if (attribute.getLocalName().equals(IGNORE_MISSING_FROM_DATA)) {
				copy.setIgnoreMissingFromData(BPELUtil.booleanToYesNo( 
					new Boolean(attribute.getNodeValue()).booleanValue()));
			}
		}
	}
	
	/**
	 * Parses the child elements of a copy node and adds the information to a copy
	 * object.
	 * 
	 * @param copyNode The copy node to be parsed.
	 * @param copy     The copy object to add the parsed information to.
	 * @param output         The output to print errors to.
	 */
	private static void parseCopyElements(Copy copy, Node copyNode, Output output) {
		NodeList childs = copyNode.getChildNodes();
		
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(FROM_SPEC)) {
				copy.setFromSpec(parseFromSpec(child, output));
			} else if (child.getLocalName().equals(TO_SPEC)) {
				copy.setToSpec(parseToSpec(child, output));
			}
		}
	}
	
	/**
	 * Parses a copy node and creates a new Copy object containing the
	 * parsed information.
	 * 
	 * @param copyNode The copy node to be parsed.
	 * @param output   The output to print errors to.
	 * 
	 * @return The created and filled Copy object.
	 */
	public static Copy parseCopy(Node copyNode, Output output) {
		Copy copy = new Copy();
		
		parseCopyAttributes(copy, copyNode);
		parseCopyElements(copy, copyNode, output);
		
		return copy;
	}
	
/********************** correlation ***************************************************/
	
	/**
	 * Parses a correlation node and creates a new Correlation object containing the
	 * parsed information.
	 * 
	 * @param correlationNode The correlation node to be parsed.
	 * @param output          The output to print errors to.
	 * 
	 * @return The created and filled Correlation object.
	 */
	private static Correlation parseCorrelation(Node correlationNode, Output output) {
		Correlation correlation = new Correlation();
		
		NamedNodeMap attributes = correlationNode.getAttributes();
		if (attributes.getNamedItem(SET) == null) {
			output.addParseError(
					"An correlation element does not have a set specified.", correlationNode);			
		} else {
			correlation.setSet(BPELUtil.stringToNCName(
					attributes.getNamedItem(SET).getNodeValue()));
		}
		
		if (attributes.getNamedItem(INITIATE) != null) {		
			correlation.setInitiate(attributes.getNamedItem(INITIATE).getNodeValue());
		}
		
		if (attributes.getNamedItem(PATTERN) != null) {		
			correlation.setPattern(attributes.getNamedItem(PATTERN).getNodeValue());
		}
		
		return correlation;
	}
	
	/**
	 * Parses a correlations node and creates a new Correlation object for each
	 * contained correlation node {@link #parseCorrelation(Node, Output)}). 
	 * 
	 * @param correlationsNode The correlations node to be parsed.
	 * @param output           The output to print errors to.
	 * 
	 * @return A list with the created and filled Correlation objects.
	 */
	public static List<Correlation> parseCorrelations(Node correlationsNode, Output output) {
		List<Correlation> result = null;
		if (correlationsNode != null) { 
			result = new ArrayList<Correlation>();
			NodeList correlationNodes = correlationsNode.getChildNodes();
			for (int i = 0; i < correlationNodes.getLength(); i++) {
				Node correlationNode = correlationNodes.item(i);
				if ((correlationNode.getLocalName() != null) &&
						correlationNode.getLocalName().equals(CORRELATION)) {
					Correlation correlation = parseCorrelation(correlationNodes.item(i), output);
					result.add(correlation);
				}
			}
		}
		return result;
	}
	
/********************** correlation set ***************************************************/
	
	/**
	 * Parses the attributes of a correlation set node and adds the information to a
	 * CorrelationSet object.
	 * 
	 * @param set                The CorrelationSet object to add the parsed 
	 *                           information to.
	 * @param correlationSetNode The node to be parsed.
	 * @param output             The output to print errors to.
	 */
	private static void parseCorrelationSetAttributes(CorrelationSet set, Node correlationSetNode, Output output) {
		Node nameNode = correlationSetNode.getAttributes().getNamedItem("Name");
		if (nameNode == null) {
			output.addParseError("A correlation property does not have a name specified.", correlationSetNode);
		} else {
			set.setName(BPELUtil.stringToNCName(nameNode.getNodeValue()));
		}
	}
	
	/**
	 * Parses the child elements of a correlation set node and adds the information to a
	 * CorrelationSet object. A correlation set node is a data field node that specifies
	 * the correlation properties as elements in a schema type. 
	 * 
	 * @param set                The CorrelationSet object to add the parsed 
	 *                           information to.
	 * @param correlationSetNode The node to be parsed.
	 */
	private static void parseCorrelationSetElements(CorrelationSet set, Node correlationSetNode) {
		Node dataTypeNode = XMLUtil.getChildWithName(correlationSetNode, "DataType");
		if (dataTypeNode == null) {
			return;
		}
		
		Node schemaTypeNode = 
			XMLUtil.getChildWithName(dataTypeNode, "SchemaType");
		if (schemaTypeNode == null) {
			return;
		}
		
		Node schemaNode = XMLUtil.getChildWithName(schemaTypeNode, "schema");
		if (schemaNode == null) {
			return;
		}
		
		NodeList schemaChildNodes = schemaNode.getChildNodes();
		for (int j = 0; j < schemaChildNodes.getLength(); j++) {
			Node element = schemaChildNodes.item(j);
			if ((element.getLocalName() != null) && 
					element.getLocalName().equals("element")) {
				Node attribute = element.getAttributes().getNamedItem("name");
				if (attribute != null) {
					set.addProperty(attribute.getNodeValue());
				}
			}
		}
	}
	
	/**
	 * Parses a correlation set node and creates a new CorrelationSet object to
	 * store the parsed information to. 
	 * 
	 * @param correlationSetNode The correlation set node to be parsed.
	 * @param output             The output to print errors to.
	 * 
	 * @return The created and filled CorrelationSet object.
	 */
	public static CorrelationSet parseCorrelationSet(Node correlationSetNode, Output output) {
		CorrelationSet correlationSet = new CorrelationSet();
		
		parseCorrelationSetAttributes(correlationSet, correlationSetNode, output);
		parseCorrelationSetElements(correlationSet, correlationSetNode);
		
		return correlationSet;
	}
	
/********************** from parts ***************************************************/
	
	/**
	 * Parses a from parts node and creates a new FromPart object for each
	 * contained from part node {@link #parseFromPart(Node)}). 
	 * 
	 * @param fromPartsNode The from parts node to be parsed.
	 * 
	 * @return A list with the created and filled FromPart objects.
	 */
	public static List<FromPart> parseFromParts(Node fromPartsNode) {
		List<FromPart> result = null;
		if (fromPartsNode != null) {
			result = new ArrayList<FromPart>();
			NodeList fromPartNodes = fromPartsNode.getChildNodes();
			for (int i = 0; i < fromPartNodes.getLength(); i++) {
				Node fromPartNode = fromPartNodes.item(i);
				if ((fromPartNode.getLocalName() != null) &&
						fromPartNode.getLocalName().equals(FROM_PART)) {
					FromPart fromPart = parseFromPart(fromPartNode);
					result.add(fromPart);
				}
			}
		}
		return result;
	}
	
	/**
	 * Parses a from part node and creates a new FromPart object the
	 * parsed information will be stored in.
	 * 
	 * @param fromPartNode The node to be parsed.
	 * 
	 * @return The created and filled FromPart object.
	 */
	private static FromPart parseFromPart(Node fromPartNode) {
		FromPart fromPart = new FromPart();
		
		NamedNodeMap attributes = fromPartNode.getAttributes();
		if (attributes.getNamedItem(PART) != null) {
			fromPart.setPart(attributes.getNamedItem(PART).getNodeValue());
		}
		
		if (attributes.getNamedItem(TO_VARIABLE) != null) {
			fromPart.setToVariable(BPELUtil.stringToNCName(
					attributes.getNamedItem(TO_VARIABLE).getNodeValue()));
		}
		
		return fromPart;
	}
	
/********************** to parts ***************************************************/
	
	/**
	 * Parses a to parts node and creates a new ToPart object for each
	 * contained to part node {@link #parseToPart(Node)}). 
	 * 
	 * @param toPartsNode The from parts node to be parsed.
	 * 
	 * @return A list with the created and filled ToPart objects.
	 */
	public static List<ToPart> parseToParts(Node toPartsNode) {
		List<ToPart> result = null;
		if (toPartsNode != null) {
			result = new ArrayList<ToPart>();
			NodeList toPartNodes = toPartsNode.getChildNodes();
			for (int i = 0; i < toPartNodes.getLength(); i++) {
				Node toPartNode = toPartNodes.item(i);
				if ((toPartNode.getLocalName() != null) &&
						toPartNode.getLocalName().equals(TO_PART)) {
					ToPart toPart = parseToPart(toPartNodes.item(i));
					result.add(toPart);
				}
			}
		}
		return result;
	}
	
	/**
	 * Parses a to part node and creates a new ToPart object the
	 * parsed information will be stored in.
	 * 
	 * @param toPartNode The node to be parsed.
	 * 
	 * @return The created and filled ToPart object.
	 */
	private static ToPart parseToPart(Node toPartNode) {
		ToPart toPart = new ToPart();
		
		NamedNodeMap attributes = toPartNode.getAttributes();
		if (attributes.getNamedItem(PART) != null) {
			toPart.setPart(attributes.getNamedItem(PART).getNodeValue());
		}
		
		if (attributes.getNamedItem(FROM_VARIABLE) != null) {
			toPart.setFromVariable(BPELUtil.stringToNCName(
					attributes.getNamedItem(FROM_VARIABLE).getNodeValue()));
		}
		
		return toPart;
	}
	
/********************** from spec ***************************************************/
	
	/**
	 * Parses the attributes of a from spec node and adds the parsed
	 * information to the given FromSpec object.
	 * 
	 * @param fromSpec     The FromSpec object to add the parsed information to.
	 * @param fromSpecNode The node to be parsed.
	 */
	private static void parseFromSpecAttributes(FromSpec fromSpec, Node fromSpecNode) {
		NamedNodeMap attributes = fromSpecNode.getAttributes();
		if (attributes.getNamedItem(TYPE) != null) {
			fromSpec.setType(attributes.getNamedItem(TYPE).getNodeValue());
		}
		
		if (attributes.getNamedItem(VARIABLE_NAME) != null) {
			fromSpec.setVariableName(
					attributes.getNamedItem(VARIABLE_NAME).getNodeValue());
		}
		
		if (attributes.getNamedItem(PART) != null) {
			fromSpec.setPart(attributes.getNamedItem(PART).getNodeValue());
		}
		
		if (attributes.getNamedItem(PROPERTY) != null) {
			fromSpec.setProperty(
					attributes.getNamedItem(PROPERTY).getNodeValue());
		}
	}
	
	/**
	 * Parses the child elements of a from spec node and adds the parsed
	 * information to the given FromSpec object.
	 * 
	 * @param fromSpec     The FromSpec object to add the parsed information to.
	 * @param fromSpecNode The node to be parsed.
	 * @param output       The output to print errors to.
	 */
	private static void parseFromSpecElements(FromSpec fromSpec, Node fromSpecNode, Output output) {
		NodeList childs = fromSpecNode.getChildNodes();
		
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(LITERAL)) {
				fromSpec.setLiteral(XMLUtil.getNodeValue(child, output));
			} else if (child.getLocalName().equals(QUERY)) {
				fromSpec.setQuery(XMLUtil.getNodeValue(child, output));
				Node queryLanguageNode = child.getAttributes().getNamedItem(QUERY_LANGUAGE);
				if (queryLanguageNode != null) {
					fromSpec.setQueryLanguage(queryLanguageNode.getNodeValue());
				}
			} else if (child.getLocalName().equals(EXPRESSION)) {
				fromSpec.setExpression(XMLUtil.getNodeValue(child, output));
				Node expressionLanguageNode = 
					child.getAttributes().getNamedItem(EXPRESSION_LANGUAGE);
				if (expressionLanguageNode != null) {
					fromSpec.setExpressionLanguage(expressionLanguageNode.getNodeValue());
				}
			}
		}
	}
	
	/**
	 * Parses a to from spec node and creates a new FromSpec object the
	 * parsed information will be stored in.
	 * 
	 * @param fromSpecNode The node to be parsed.
	 * @param output       The output to print errors to.
	 * 
	 * @return The created and filled FromSpec object.
	 */
	public static FromSpec parseFromSpec(Node fromSpecNode, Output output) {
		FromSpec fromSpec = new FromSpec();
		
		parseFromSpecAttributes(fromSpec, fromSpecNode);
		parseFromSpecElements(fromSpec, fromSpecNode, output);
		
		return fromSpec;
	}
	
/********************** to spec ***************************************************/
	
	/**
	 * Parses the attributes of a to spec node and adds the parsed
	 * information to the given ToSpec object.
	 * 
	 * @param toSpec     The ToSpec object to add the parsed information to.
	 * @param toSpecNode The node to be parsed.
	 */
	private static void parseToSpecAttributes(ToSpec toSpec, Node toSpecNode) {
		NamedNodeMap attributes = toSpecNode.getAttributes();
		if (attributes.getNamedItem(TYPE) != null) {
			toSpec.setType(attributes.getNamedItem(TYPE).getNodeValue());
		}
		
		if (attributes.getNamedItem(VARIABLE_NAME) != null) {
			toSpec.setVariableName(attributes.getNamedItem(VARIABLE_NAME).getNodeValue());
		}
		
		if (attributes.getNamedItem(PART) != null) {
			toSpec.setPart(attributes.getNamedItem(PART).getNodeValue());
		}
		
		if (attributes.getNamedItem(PROPERTY) != null) {
			toSpec.setProperty(attributes.getNamedItem(PROPERTY).getNodeValue());
		}
	}
	
	/**
	 * Parses the child elements of a to spec node and adds the parsed
	 * information to the given ToSpec object.
	 * 
	 * @param toSpec     The ToSpec object to add the parsed information to.
	 * @param toSpecNode The node to be parsed.
	 * @param output       The output to print errors to.
	 */
	private static void parseToSpecElements(ToSpec toSpec, Node toSpecNode, Output output) {
		NodeList childs = toSpecNode.getChildNodes();
		
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(QUERY)) {
				toSpec.setQuery(XMLUtil.getNodeValue(child, output));
				Node queryLanguageNode = 
					child.getAttributes().getNamedItem(QUERY_LANGUAGE);
				if (queryLanguageNode != null) {
					toSpec.setQueryLanguage(queryLanguageNode.getNodeValue());
				}
			} else if (child.getLocalName().equals(EXPRESSION)) {
				toSpec.setExpression(XMLUtil.getNodeValue(child, output));
				Node expressionLanguageNode = 
					child.getAttributes().getNamedItem(EXPRESSION_LANGUAGE);
				if (expressionLanguageNode != null) {
					toSpec.setExpressionLanguage(expressionLanguageNode.getNodeValue());
				}
			}
		}
	}
	
	/**
	 * Parses a to spec node and creates a new ToSpec object the
	 * parsed information will be stored in.
	 * 
	 * @param toSpecNode The node to be parsed.
	 * @param output     The output to print errors to.
	 * 
	 * @return The created and filled ToSpec object.
	 */
	public static ToSpec parseToSpec(Node toSpecNode, Output output) {
		ToSpec toSpec = new ToSpec();
		
		parseToSpecAttributes(toSpec, toSpecNode);
		parseToSpecElements(toSpec, toSpecNode, output);
		
		return toSpec;
	}
	
/********************** import ***************************************************/
	
	/**
	 * Parses an import node and creates a new Import object the
	 * parsed information will be stored in.
	 * 
	 * @param importNode The node to be parsed.
	 * @param output     The output to print errors to.
	 * 
	 * @return The created and filled Import object.
	 */
	public static Import parseImport(Node importNode, Output output) {
		Import imp = new Import();
		
		NamedNodeMap attributes = importNode.getAttributes();
		if (attributes.getNamedItem(NAMESPACE) == null) {
			output.addParseError("An import element does " +
					"not have a specified namespace attribute.", importNode);			
		} else {
			imp.setNamespace(attributes.getNamedItem(NAMESPACE).getNodeValue());
		}
		
		if (attributes.getNamedItem(LOCATION) != null) {
			try {
				imp.setLocation(new URI(attributes.getNamedItem(LOCATION).getNodeValue()));
			} catch (URISyntaxException e) {
				output.addError(e);
			}
		}
		
		if (attributes.getNamedItem(IMPORT_TYPE) == null) {
			output.addParseError("An import element does " +
					"not have a specified import type attribute.", importNode);			
		} else {
			try {
				imp.setImportType(new URI(
						attributes.getNamedItem(IMPORT_TYPE).getNodeValue()));
			} catch (URISyntaxException e) {
				output.addError(e);
			}
		}
		
		if (attributes.getNamedItem(PREFIX) == null) {
			output.addError("An import element does " +
					"not have a specified prefix attribute.", attributes.toString());			
		} else {
			imp.setPrefix(attributes.getNamedItem(PREFIX).getNodeValue());
		}
		
		return imp;
	}
	
/********************** loop ***************************************************/

	/**
	 * Parses the attributes of a to loop node and adds the parsed
	 * information to the given Loop object.
	 * 
	 * @param loop     The Loop object to add the parsed information to.
	 * @param loopNode The node to be parsed.
	 * @param output   The output to print errors to.
	 */
	private static void parseLoopAttributes(Loop loop, Node loopNode, Output output) {
		Node typeAttr = 
			loopNode.getAttributes().getNamedItem(LOOP_TYPE);
		if (typeAttr != null) {
			loop.setLoopType(typeAttr.getNodeValue());
		} else {
			output.addParseError("A loop does not have a loop type defined.", loopNode);
		}
	}
	
	/**
	 * Parses a standard loop node and adds the parsed information
	 * to the given Loop object.
	 *  
	 * @param loop         The Loop object to add the parsed information to.
	 * @param standardNode The standard loop node to be parsed.
	 */
	private static void parseStandard(Loop loop, Node standardNode) {
		NamedNodeMap attributes = standardNode.getAttributes();
		Expression loopCondition = new Expression();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(TEST_TIME)) {
				loop.setTestTime(attribute.getNodeValue());
			} else if (attribute.getLocalName().equals(LOOP_CONDITION)) {
				loopCondition.setExpression(attribute.getNodeValue());
			} else if (attribute.getLocalName().equals(LOOP_CONDITION_LANGUAGE)) {
				loopCondition.setExpressionLanguage(attribute.getLocalName());
			}
		}
		
		loop.setLoopCondition(loopCondition);
	}
	
	/**
	 * Parses a multi-instance loop node and adds the parsed information
	 * to the given Loop object.
	 *  
	 * @param loop                 The Loop object to add the parsed 
	 *                             information to.
	 * @param multipleInstanceNode The mulit-instance loop node to be parsed.
	 * @param output               The output to print errors to.
	 */
	private static void parseMultipleInstance(
			Loop loop, Node multipleInstanceNode, Output output) {
		NamedNodeMap attributes = multipleInstanceNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getLocalName().equals(SUCCESSFUL_BRANCHES_ONLY)) {
				loop.setSuccessfulBranchesOnly(BPELUtil.booleanToYesNo(
					new Boolean(attribute.getNodeValue()).booleanValue()));
			} else if (attribute.getLocalName().equals(ORDERING)) {
				loop.setOrdering(attribute.getNodeValue());
			}
		}
		
		NodeList childs = multipleInstanceNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(START_COUNTER_VALUE)) {
				loop.setStartCounterValue(parseExpression(child, output));
			} else if (child.getLocalName().equals(FINAL_COUNTER_VALUE)) {
				loop.setFinalCounterValue(parseExpression(child, output));
			} else if (child.getLocalName().equals(COMPLETION_CONDITION)) {
				loop.setCompletionCondition(parseExpression(child, output));
			}
		}
	}
	
	/**
	 * Parses the child elements of a to loop node and adds the parsed
	 * information to the given Loop object.
	 * 
	 * @param loop     The Loop object to add the parsed information to.
	 * @param loopNode The node to be parsed.
	 * @param output   The output to print errors to.
	 */
	private static void parseLoopElements(Loop loop, Node loopNode, Output output) {
		NodeList childs = loopNode.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getLocalName() == null) {
				continue;
			}
			if (child.getLocalName().equals(LOOP_STANDARD)) {
				parseStandard(loop, child);
				break;
			} else if (child.getLocalName().equals(LOOP_MULTIPLE_INSTANCE)) {
				parseMultipleInstance(loop, child, output);
				break;
			}
		}
	}
	
	/**
	 * Parses a loop node and creates a new Loop object the
	 * parsed information will be stored in.
	 * 
	 * @param loopNode The node to be parsed.
	 * @param output   The output to print errors to.
	 * 
	 * @return The created and filled Loop object.
	 */
	public static Loop parseLoop(Node loopNode, Output output) {
		Loop loop = new Loop();
		
		parseLoopAttributes(loop, loopNode, output);
		parseLoopElements(loop, loopNode, output);
		
		return loop;
	}
}
