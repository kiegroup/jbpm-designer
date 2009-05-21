package de.hpi.bpmn2bpel.model.supporting;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.hpi.bpmn2bpel.util.BPELUtil;
import de.hpi.bpmn2bpel.model.BPELDataObject;
import de.hpi.bpmn2bpel.model.supporting.FromSpec.fromTypes;
import de.hpi.bpmn2bpel.model.supporting.ToSpec.toTypes;

/**
 * A copy object is used to copy the value of one variable to another variable. 
 * For this purpose FromSpec and ToSpec objects are used.
 */
public class Copy {
	
	private String keepSrcElementName = null;
	private String ignoreMissingFromData = null;
	private FromSpec fromSpec = null;
	private ToSpec toSpec = null;
	private Document xmlDocument;
	
	/**
	 * The default constructor
	 * @param xmlDoc
	 */
	public Copy()  {
		this.xmlDocument = null;
	}
	
	/**
	 * Constructor. Initializes the copy object with a XML document.
	 */
	public Copy(Document xmlDoc)  {
		this.xmlDocument = xmlDoc;
	}

	/**
	 * Generates the from part based on the value of the properties property of
	 * a data object.
	 * 
	 * @param inputDataObject
	 * 			The properties value.
	 * @throws JSONException 
	 */
	public void setFromSpecBasedOnDataObject(BPELDataObject inputDataObject) throws JSONException {
		FromSpec from = new FromSpec();
		from.setType(fromTypes.LITERAL);
		
		/* Create variable element tag, equal to the related operation name */
		Element literalContent = this.xmlDocument.createElementNS(
				inputDataObject.getNamespace(), inputDataObject.getOperation());
		
		/* Add parameter values from the properties property */
		JSONObject parameters = inputDataObject.getProperties();
		JSONArray parameterNames = parameters.names();
		
		for (int i = 0; i < parameterNames.length(); i++) {
			/* Create parameter */
			Element param = this.xmlDocument.createElement(parameterNames.getString(i));
			param.setTextContent(parameters.getString(parameterNames.getString(i)));
			
			/* Append parameter to literal element */
			literalContent.appendChild(param);
		}
		
		/* Set from spec */
		from.setLiteral(literalContent);
		setFromSpec(from);
	}
	
	/**
	 * @return The FromSpec object that determines, which value should
	 * be copied
	 */
	public FromSpec getFromSpec() {
		return this.fromSpec;
	}

	/**
	 * @return "yes", if the name of the source element should be kept during
	 * the copying, "no" otherwise.
	 */
	public String isKeepSrcElementName() {
		return this.keepSrcElementName;
	}

	/**
	 * @return The ToSpec object that determines, where the value should
	 * be copied to.
	 */
	public ToSpec getToSpec() {
		return this.toSpec;
	}

	/**
	 * @return "yes", if missing it should be ignored if the from data is 
	 * missing, "no" otherwise.
	 */
	public String isIgnoreMissingFromData() {
		return this.ignoreMissingFromData;
	}

	/**
	 * Sets the value for the ignoreMissingFromData attribute.
	 * 
	 * @param ignoreMissingFromData "yes", if missing it should be ignored if
	 * the from data is missing, "no" otherwise.
	 */
	public void setIgnoreMissingFromData(String ignoreMissingFromData) {
		this.ignoreMissingFromData = ignoreMissingFromData;
	}

	/**
	 * Sets the value for the keepSrcElementName attribute.
	 * 
	 * @param keepSrcElementName "yes", if the name of the source 
	 * element should be kept during the copying, "no" otherwise.
	 */
	public void setKeepSrcElementName(String keepSrcElementName) {
		this.keepSrcElementName = keepSrcElementName;
	}

	/**
	 * Sets the FromSpec object that determines which value should
	 * be copied.
	 * 
	 * @param fromSpec The FromSpec object to set.
	 */
	public void setFromSpec(FromSpec fromSpec) {
		this.fromSpec = fromSpec;
	}

	/**
	 * Sets the ToSpec object that determines, where the value should
	 * be copied to.
	 * 
	 * @param toSpec The ToSpec object to set.
	 */
	public void setToSpec(ToSpec toSpec) {
		this.toSpec = toSpec;
	}
	
	/**
	 * Creates to part of the copy element from the passed data object. Basically 
	 * it only sets the name of the variable from the information of the data 
	 * object.
	 * 
	 * @param dataObject
	 * 		The source data object
	 */
	public void setToSpecBasedOnDataObject(BPELDataObject dataObject) {
		ToSpec to = new ToSpec();
		
		/* The message part of a generated service should name 'parameters' */
		to.setType(toTypes.VARIABLE);
		to.setPart("parameters");
		to.setVariableName(dataObject.getId());
		
		this.setToSpec(to);
	}
}
