package de.hpi.bpel4chor.model.supporting;

/**
 * A copy object is used to copy the value of one variable to another variable. 
 * For this purpose FromSpec and ToSpec objects are used.
 */
public class Copy {
	
	private String keepSrcElementName = null;
	private String ignoreMissingFromData = null;
	private FromSpec fromSpec = null;
	private ToSpec toSpec = null;
	
	/**
	 * Constructor. Initializes the copy object.
	 */
	public Copy()  {}

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
}
