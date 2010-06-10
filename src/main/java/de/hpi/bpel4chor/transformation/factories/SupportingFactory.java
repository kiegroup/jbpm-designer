package de.hpi.bpel4chor.transformation.factories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.hpi.bpel4chor.model.Container;
import de.hpi.bpel4chor.model.Diagram;
import de.hpi.bpel4chor.model.Process;
import de.hpi.bpel4chor.model.Swimlane;
import de.hpi.bpel4chor.model.activities.Scope;
import de.hpi.bpel4chor.model.artifacts.VariableDataObject;
import de.hpi.bpel4chor.model.supporting.Correlation;
import de.hpi.bpel4chor.model.supporting.CorrelationSet;
import de.hpi.bpel4chor.model.supporting.Expression;
import de.hpi.bpel4chor.model.supporting.FromPart;
import de.hpi.bpel4chor.model.supporting.FromSpec;
import de.hpi.bpel4chor.model.supporting.Import;
import de.hpi.bpel4chor.model.supporting.Loop;
import de.hpi.bpel4chor.model.supporting.ToPart;
import de.hpi.bpel4chor.model.supporting.ToSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import de.hpi.bpel4chor.util.BPELUtil;
import de.hpi.bpel4chor.util.Output;

/**
 * This factory is used for generating certain child elements of BPEL4Chor 
 * activities such as "from", "to" or "correlations". 
 * 
 * <p>An instance of this class can only be used for one diagram.</p>
 */
public class SupportingFactory {
	
	private Diagram diagram = null;
	private Document document = null;
	private Output output = null;
	
	private static final String FROM = "from";
	private static final String TO = "to";
	
	/**
	 * Constructor. Initializes the supporting factory with the
	 * diagram that contains the elements to be transformed.
	 *  
	 * @param diagram   The diagram to be transformed.
	 * @param document  The document to create the BPEL4Chor elements for.
	 * @param output    The {@link Output} to print errors to. 
	 */
	public SupportingFactory(Diagram diagram, Document document, Output output) {
		this.diagram = diagram;
		this.document = document;
		this.output = output;
	}
	
	/**
	 * Creates a from or to spec element for a variable. If the variable
	 * name is not defined, an error is added to the output. 
	 * 
	 * @param elementName   The name of the element to be created 
	 *                      ({@value #FROM} or {@value #TO}).
	 * @param varName       The variable name of the spec element.
	 * @param part          The part name of the spec element.
	 * @param query         The query of the spec element.
	 * @param queryLanguage The query language of the spec element.
	 * 
	 * @return The created "from" or "to" element.
	 */
	private Element createVariableSpec(String elementName, String varName, 
			String part, String query, String queryLanguage) {
		Element result = this.document.createElement(elementName);
		if (varName == null || varName.equals("")) {
			this.output.addGeneralError("A " + elementName + 
				" element of the type Variable must define a variable name.");
		} else {
			result.setAttribute("variable", varName);
		}
		if (part != null) {
			result.setAttribute("part", part);
		}
		if (query != null) {
			Element queryElement = this.document.createElement("query");
			if (queryLanguage != null) {
				queryElement.setAttribute("queryLanguage", queryLanguage);
			}
			if (query.equals("")) {
				queryElement.setAttribute("opaque", "yes");
			} else {
				queryElement.appendChild(this.document.createTextNode(query));
			}
			result.appendChild(queryElement);
		}
		return result;
	}
	
	/**
	 * Creates a from or to spec element for a variable. If the variable
	 * name or the variable property is not defined, an error is added to the
	 * output. 
	 * 
	 * @param elementName   The name of the element to be created 
	 *                      ({@value #FROM} or {@value #TO}).
	 * @param varName       The variable name of the spec element.
	 * @param property      The variable property name of the spec element.
	 * 
	 * @return The created "from" or "to" element.
	 */
	private Element createVariablePropertySpec(String elementName, String varName, 
			String property) {
		Element result = this.document.createElement(elementName);
		if (varName == null || varName.equals("")) {
			this.output.addGeneralError("A " + elementName + 
				" element of type VarProperty must define a variable name.");
		} else {
			result.setAttribute("variable", varName);
		}
		
		if (property == null || property.equals("")) {
			this.output.addGeneralError("A " + elementName + 
				" element of type VarProperty must define a property name.");
		} else {
			result.setAttribute("property", property);
		}
		return result;
	}
	
	/**
	 * Creates a from or to spec element for a variable using the defined
	 * expression.
	 * 
	 * @param elementName        The name of the element to be created 
	 *                           ({@value #FROM} or {@value #TO}).
	 * @param expressionLanguage The expression language of the spec element.
	 * @param expression         The expression of the spec element.
	 * 
	 * @return The created "from" or "to" element.
	 */
	private Element createExpressionSpec(String elementName, 
			String expressionLanguage, String expression) {
		Element result = this.document.createElement(elementName);
		if (expressionLanguage != null) {
			result.setAttribute("expressionLanguage", expressionLanguage);
		}
		if ((expression != null) && (expression.equals(""))) {
			result.setAttribute("opaque", "yes");
		} else {
			result.appendChild(this.document.createTextNode(expression));
		}
		return result;
	}
	
	/**
	 * Creates a "to" element from the given ToSpec object. The ToSpec
	 * object should be of the type Variable. If the ToSpec object does 
	 * not define a variable name, an error is added to the output.
	 * 
	 * @param toSpec The ToSpec of the type Variable.
	 * 
	 * @return The created "to" element.
	 */
	private Element createVariableToSpec(ToSpec toSpec) {
		return createVariableSpec(TO, toSpec.getVariableName(), 
				toSpec.getPart(), toSpec.getQuery(), toSpec.getQueryLanguage());
	}
	
	/**
	 * Creates a "to" element from the given ToSpec object. The ToSpec
	 * object should be of the type VarProperty. If the ToSpec object does 
	 * not define a variable name or a variable property, an error is added
	 * to the output.
	 * 
	 * @param toSpec The ToSpec of the type VarProperty.
	 * 
	 * @return The created "to" element.
	 */
	private Element createVariablePropertyToSpec(ToSpec toSpec) {
		return createVariablePropertySpec(TO, toSpec.getVariableName(), 
				toSpec.getProperty());
	}
	
	/**
	 * Creates a "to" element from the given ToSpec object. The ToSpec
	 * object should be of the type Expression.
	 * 
	 * @param toSpec The ToSpec of the type Expression.
	 * 
	 * @return The created "to" element.
	 */
	private Element createExpressionToSpec(ToSpec toSpec) {
		return createExpressionSpec(
				TO, toSpec.getExpressionLanguage(), toSpec.getExpression());
	}
	
	/**
	 * Creates a "to" element from the given ToSpec object (see
	 * {@link #createVariableToSpec(ToSpec)}, 
	 * {@link #createVariablePropertyToSpec(ToSpec)} or 
	 * {@link #createExpressionToSpec(ToSpec)}.
	 * 
	 * @param toSpec The ToSpec object to create the "to" element from.
	 * 
	 * @return The created "to" element.
	 */
	public Element createToSpecElement(ToSpec toSpec) {
		if (toSpec == null) { 
			return null; 
		}
		
		Element result = null;
		String type = toSpec.getType();
		if (type.equals(ToSpec.TYPE_VARIABLE)) {
			result = createVariableToSpec(toSpec);
		} else if (type.equals(ToSpec.TYPE_VAR_PROPERTY)) {
			result = createVariablePropertyToSpec(toSpec);
		} else if (type.equals(ToSpec.TYPE_EXPRESSION)) {
			createExpressionToSpec(toSpec);
		} else {
			// empty toSpec
			result = this.document.createElement("to");
		}
		
		return result;
	}
	
	/**
	 * Creates a "from" element from the given FromSpec object. The FromSpec
	 * object should be of the type Variable. If the FromSpec object does 
	 * not define a variable name, an error is added to the output.
	 * 
	 * @param fromSpec The FromSpec of the type Variable.
	 * 
	 * @return The created "from" element.
	 */
	private Element createVariableFromSpec(FromSpec fromSpec) {
		return createVariableSpec(FROM, fromSpec.getVariableName(), 
				fromSpec.getPart(), fromSpec.getQuery(), 
				fromSpec.getQueryLanguage());
	}
	
	/**
	 * Creates a "from" element from the given FromSpec object. The FromSpec
	 * object should be of the type VarProperty. If the FromSpec object does 
	 * not define a variable name or a variable property, an error is added
	 * to the output.
	 * 
	 * @param fromSpec The FromSpec of the type VarProperty.
	 * 
	 * @return The created "from" element.
	 */
	private Element createVariablePropertyFromSpec(FromSpec fromSpec) {
		return createVariablePropertySpec(FROM, fromSpec.getVariableName(), 
				fromSpec.getProperty());
	}
	
	/**
	 * Creates a "from" element from the given FromSpec object. The FromSpec
	 * object should be of the type Expression.
	 * 
	 * @param fromSpec The FromSpec of the type Expression.
	 * 
	 * @return The created "from" element.
	 */
	private Element createExpressionFromSpec(FromSpec fromSpec) {
		return createExpressionSpec(
				FROM, fromSpec.getExpressionLanguage(), fromSpec.getExpression());
	}
	
	/**
	 * Creates a "from" element from the given FromSpec object (see
	 * {@link #createVariableFromSpec(FromSpec)}, 
	 * {@link #createVariablePropertyFromSpec(FromSpec)} or 
	 * {@link #createExpressionFromSpec(FromSpec)}.
	 * 
	 * @param fromSpec The FromSpec object to create the "from" element from.
	 * 
	 * @return The created "from" element.
	 */
	public Element createFromSpecElement(FromSpec fromSpec) {
		if (fromSpec == null) {
			return null;
		}
		
		Element result = null;
		String type = fromSpec.getType();
		if (type.equals(FromSpec.TYPE_VARIABLE)) {
			result = createVariableFromSpec(fromSpec);
		} else if (type.equals(FromSpec.TYPE_VAR_PROPERTY)) {
			result = createVariablePropertyFromSpec(fromSpec);
		} else if (type.equals(FromSpec.TYPE_EXPRESSION)) {
			result = createExpressionFromSpec(fromSpec);
		} else if (type.equals(FromSpec.TYPE_LITERAL)) {
			result = this.document.createElement("from");
			Element literal = this.document.createElement("literal");
			literal.appendChild(
					this.document.createTextNode(fromSpec.getLiteral()));
			result.appendChild(literal);
		} else if (type.equals(FromSpec.TYPE_EMPTY)) {
			result = this.document.createElement("from");
		} else if (type.equals(FromSpec.TYPE_OPAQUE)) {
			result = this.document.createElement("opaqueFrom");	
		}
		return result;
	}
	
	/**
	 * Creates a "toParts" element containing a "toPart" element
	 * for each ToPart in the given list.
	 * 
	 * @param toParts ToParts to create into the "toParts" element
	 * 
	 * @return The created "toParts" element or null if the ToParts
	 * list is empty.
	 */
	public Element createToPartsElement(List<ToPart> toParts) {
		if (toParts.isEmpty()) 
		{ 
			return null; 
		}
		Element result = this.document.createElement("toParts");
		for (Iterator<ToPart> it = toParts.iterator(); it.hasNext();) {
			ToPart toPart = it.next();
			Element element = this.document.createElement("toPart");
			element.setAttribute("part", toPart.getPart());
			element.setAttribute("fromVariable", toPart.getFromVariable());
			result.appendChild(element);
		}
		return result;
	}
	
	/**
	 * Creates a "fromParts" element containing a "fromPart" element
	 * for each FromPart in the given list.
	 * 
	 * @param fromParts fromParts to create into the "fromParts" element
	 * 
	 * @return The created "fromParts" element or null if the FromParts
	 * list is empty.
	 */
	public Element createFromPartsElement(List<FromPart> fromParts) {
		if (fromParts.isEmpty()) 
		{ 
			return null; 
		}
		Element result = this.document.createElement("fromParts");
		for (Iterator<FromPart> it = fromParts.iterator(); it.hasNext();) {
			FromPart fromPart = it.next();
			Element element = this.document.createElement("fromPart");
			element.setAttribute("part", fromPart.getPart());
			element.setAttribute("toVariable", fromPart.getToVariable());
			result.appendChild(element);
		}
		return result;
	}
	
	/**
	 * Creates a "variable" element from the given variable data object.
	 * 
	 * <p>If the variabe does not define a prefix or if an import for the 
	 * prefix is not defined in the swimlane, an error is added to the output.
	 * If the type of the variable is not defined, an error is added to the
	 * output, too.</p>
	 * 
	 * @param swimlane   The swimlane, the variable data object is located in.
	 * @param dataObject The data object to create the "variable" element from.
	 * 
	 * @return The created "variable" element.
	 */
	public Element createVariableElement(Swimlane swimlane, 
			VariableDataObject dataObject) {
		Element result = this.document.createElement("variable");
		result.setAttribute("name", dataObject.getName());
	
		String value = dataObject.getVariableTypeValue();
		String prefix = value.substring(0, value.indexOf(':'));
		if (value.indexOf(':') < 0) {
			this.output.addError(
					"There is a prefix missing for the variable type value " +
					value + "of this variable ", dataObject.getId());
			return result;
		}
		// set import
		Import imp = swimlane.getImportForPrefix(prefix);
		if (imp == null) {
			this.output.addError(
					"There is an import element missing for the prefix " +
					prefix + "of this variable ", dataObject.getId());
			return result;
		}
		
		// set type
		if (dataObject.getVariableType().equals(
				VariableDataObject.VARIABLE_TYPE_MESSAGE)) {	
			result.setAttribute("messageType", value); 			
		} else if (dataObject.getVariableType().equals(
				VariableDataObject.VARIABLE_TYPE_XML_ELEMENT)) {
			result.setAttribute("element", value);
		} else if ((dataObject.getVariableType().equals(
				VariableDataObject.VARIABLE_TYPE_XML_TYPE))) {
			result.setAttribute("type", value);
		} else {
			this.output.addError("The type of this variable " +
					"could not be determined.", dataObject.getName());
		}
		
		// set fromSpec
		Element fromSpec = createFromSpecElement(dataObject.getFromSpec());
		if (fromSpec != null) {
			result.appendChild(fromSpec);
		}
		
		return result;
	}
	
	/**
	 * Creates a "variables" element that contains a "variable" element for
	 * each standard variable data object that is contained in the container. 
	 * 
	 * @param swimlane  The swimlane, the container belongs to
	 * @param container The container that contains the variable data objects
	 * 
	 * @return The created "variables" element or null, if there are no 
	 * standard variable data objects in the container.
	 */
	public Element createVariablesElement(Swimlane swimlane, Container container) {
		Element result = this.document.createElement("variables");
		boolean found = false;
		for (Iterator<VariableDataObject> it = 
			this.diagram.getVariableDataObjects().iterator();it.hasNext();) {
			VariableDataObject dataObject = it.next();
			if ((dataObject.getContainer() != null) &&
					dataObject.getContainer().equals(container) &&
					dataObject.getType().equals(VariableDataObject.TYPE_STANDARD)) {
				Element variable = createVariableElement(swimlane, dataObject);
				result.appendChild(variable);
				found = true;
			}
			
		}
		if (found) {
			return result;
		} 
		return null;
	}
	
	/**
	 * Creates a "correlations" element containing a "correlation" element
	 * for each Correlation in the given list.
	 * 
	 * @param correlations Correlations to create into the "correlations"
	 *                     element
	 * 
	 * @return The created "correlations" element or null if the correlations
	 * list is empty.
	 */
	public Element createCorrelationsElement(List<Correlation> correlations) {
		if (correlations.isEmpty()) {
			return null;
		}
		
		Element result = this.document.createElement("correlations");
		for (Iterator<Correlation> it = correlations.iterator(); it.hasNext();) {
			Correlation correlation = it.next();
			Element element = this.document.createElement("correlation");
			element.setAttribute("set", correlation.getSet());
			String initiate = BPELUtil.getInitiate(correlation);
			if (initiate != null) {
				element.setAttribute("initiate", initiate);
			}

			String pattern = BPELUtil.getPattern(correlation);
			if (pattern != null) {
				element.setAttribute("pattern", pattern);
			}
			result.appendChild(element);
		}
		return result;
	}
	
	/**
	 * Creates a "correlationSets" element containing a "correlationSet" element
	 * for each CorrelationSet in the given list.
	 * 
	 * @param sets CorrelationSets to create into the "correlationSets" element
	 * 
	 * @return The created "correlationSets" element or null if the 
	 * correlationSets list is empty.
	 */
	private Element createCorrelationSetsElement(List<CorrelationSet> sets) {
		if (sets.isEmpty()) {
			return null;
		}
		
		Element result = this.document.createElement("correlationSets");
		for (Iterator<CorrelationSet> it = sets.iterator(); it.hasNext();) {
			CorrelationSet corrSet = it.next(); 
			Element element = this.document.createElement("correlationSet");
			element.setAttribute("name", corrSet.getName());
			String properties = "";
			for (Iterator<String> itProp = 
				corrSet.getProperties().iterator(); itProp.hasNext();) {
				properties = properties + " " + itProp.next();
			}
			element.setAttribute("properties", properties.trim());
			result.appendChild(element);
		}
		
		return result;
	}
	
	/**
	 * Creates a "correlationSets" element for the CorrelationSets defined in 
	 * the given scope (see {@link #createCorrelationSetsElement(List)}.
	 * 
	 * @param scope The scope that contains the CorrelationSets
	 * 
	 * @return The created "correlationSets" element or null if the 
	 * scope does not define CorrelationSets.
	 */
	public Element createCorrelationSetsElement(Scope scope) {
		return createCorrelationSetsElement(scope.getCorrelationSets());
	}
	
	/**
	 * Creates a "correlationSets" element for the CorrelationSets defined in 
	 * the given process (see {@link #createCorrelationSetsElement(List)}.
	 * 
	 * @param process The process that contains the CorrelationSets
	 * 
	 * @return The created "correlationSets" element or null if the 
	 * process does not define CorrelationSets.
	 */
	public Element createCorrelationSetsElement(Process process) {
		return createCorrelationSetsElement(process.getCorrelationSets());
	}
	
	/**
	 * Creates an expression element with the given tag name. An expression
	 * element defines an expressionLanguage attribute and the expression 
	 * value is the text child node of the expression element. An expression
	 * is created as "opaque" if there is no expression value defined.
	 *  
	 * @param tagName    The tag name of the element to be created.
	 * @param expression The expression that holds the expression language and
	 *                   the expression value.
	 * 
	 * @return The created expression element with the given tag name.
	 */
	public Element createExpressionElement(String tagName, Expression expression) {
		Element result = this.document.createElement(tagName);
		if (expression == null) {
			result.setAttribute("opaque", "yes");
		} else {
			if (expression.getExpressionLanguage() != null) {
				result.setAttribute("expressionLanguage", 
						expression.getExpressionLanguage());
			}
			String expressionStr = expression.getExpression();
			if ((expressionStr == null) || (expressionStr.equals(""))) {
				result.setAttribute("opaque", "yes");
			} else {
				result.appendChild(this.document.createTextNode(expressionStr));
			}
		}
		
		return result;
	}
	
	/**
	 * Creates an "import" element for each Import defined in the swimlane.
	 * 
	 * @param swimlane The swimlane to create the imports for.
	 * 
	 * @return A list with the created "import" elements.
	 */
	public List<Element> createImportElements(Swimlane swimlane) {
		List<Element> result = new ArrayList<Element>();
		List<Import> imports = swimlane.getImports();
		for (Iterator<Import> it = imports.iterator(); it.hasNext();) {
			Import imp = it.next();
			Element element = this.document.createElement("import");
			element.setAttribute("namespace", imp.getNamespace());
			if (imp.getLocation() != null) {
				element.setAttribute("location", imp.getLocation().toASCIIString());
			}
			element.setAttribute("importType", imp.getImportType().toASCIIString());
			result.add(element);
		}
		return result;
	}
	
	/**
	 * Creates a "messageExchanges" element containing a "messageExchange"
	 * element for each message exchange defined in the given list.
	 * 
	 * @param messageExchanges The values to create a "messageExchange" element
	 *                         for each.
	 *                         
	 * @return The created "messageExchanges" element or null if the 
	 * given list is empty.
	 */
	private Element createMessageExchangesElement(List<String> messageExchanges) {
		if (messageExchanges.isEmpty()) {
			return null;
		}
		Element result = this.document.createElement("messageExchanges");
		for (Iterator<String> it = messageExchanges.iterator(); it.hasNext();) {
			Element element = this.document.createElement("messageExchange");
			element.setAttribute("name", it.next());
			result.appendChild(element);
		}
		return result;
	}
	
	/**
	 * Creates a "messageExchanges" element for the message exchanges defined
	 * in the given scope (see {@link #createMessageExchangesElement(List)}).
	 * 
	 * @param scope The scope that contains the message exchanges
	 * 
	 * @return The created "messageExchanges" element or null if the 
	 * scope does not define message exchanges.
	 */
	public Element createMessageExchangesElement(Scope scope) {
		return createMessageExchangesElement(scope.getMessageExchanges());
	}
	
	/**
	 * Creates a "messageExchanges" element for the message exchanges defined
	 * in the given process (see {@link #createMessageExchangesElement(List)}).
	 * 
	 * @param process The process that contains the message exchanges
	 * 
	 * @return The created "messageExchanges" element or null if the 
	 * process does not define message exchanges.
	 */
	public Element createMessageExchangesElement(Process process) {
		return createMessageExchangesElement(process.getMessageExchanges());
	}
	
	/**
	 * Creates the "completionCondition" element for a looping activity.
	 * 
	 * @param loop The Loop to create the "completionCondition" element for.
	 * 
	 * @return The created "completionCondition" element or null if the
	 * loop does not define a completion condition.
	 */
	public Element createCompletionCondition(Loop loop) {
		if (loop.getCompletionCondition() == null) {
			return null;
		}
		Element branches = createExpressionElement(
				"branches", loop.getCompletionCondition());
		if (loop.isSuccessfulBranchesOnly() != null) {
			branches.setAttribute("successfulBranchesOnly", loop.isSuccessfulBranchesOnly());
		}
		Element completionCond = 
			this.document.createElement("completionCondition");
		completionCond.appendChild(branches);
		return completionCond;
	}
	
	/**
	 * Creates a BPEL4Chor "links" element containing a
	 * "link" element for each link in the list.	 
	 * 
	 * @param links The links contained in the "links" element
	 * 
	 * @return The created BPEL4Chor "links" element
	 */
	public Element createLinks(List<Link> links) {
		boolean needed = false;
		Element result = this.document.createElement("links");
		for (int i = 0; i < links.size(); i++) {
			Element link = this.document.createElement("link");
			link.setAttribute("name", links.get(i).getName());
			result.appendChild(link);
			needed = true;
		}
		if (needed) {
			return result;
		}
		return null;
	}
}
