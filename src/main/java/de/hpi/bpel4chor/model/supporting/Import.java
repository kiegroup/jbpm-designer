package de.hpi.bpel4chor.model.supporting;

import java.net.URI;

/**
 * An import statement can be defined for pools or pool sets
 * to associate WSDL and XML files with the contained process.
 */
public class Import {
	
	private String namespace = null;
	private URI location = null;
	private URI importType = null;
	private String prefix = null;
	
	/**
	 * Constructor. Initializes the import object.
	 */
	public Import() {}

	/**
	 * @return The type of the imported file.
	 */
	public URI getImportType() {
		return this.importType;
	}

	/**
	 * @return The location of the imported file. The result is null if the 
	 * location was not specified.
	 */
	public URI getLocation() {
		return this.location;
	}

	/**
	 * @return The namespace of the wsdl or xml file.
	 */
	public String getNamespace() {
		return this.namespace;
	}

	/**
	 * @return The prefix that should be used to reference elements
	 * within the namespace of the file.
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * Sets the type of the imported file.
	 * 
	 * @param importType The type to set.
	 */
	public void setImportType(URI importType) {
		this.importType = importType;
	}

	/**
	 * Sets the location of the imported file.
	 * 
	 * @param location The location to set.
	 */
	public void setLocation(URI location) {
		this.location = location;
	}

	/**
	 * Sets she namespace of the imported xml or wsdl file.
	 * 
	 * @param namespace The namespace to set.
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Sets the prefix that should be used to reference elements
	 * within the namespace of the file.
	 * 
	 * @param prefix The prefix to set.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
