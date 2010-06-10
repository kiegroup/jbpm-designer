package de.hpi.bpel4chor.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.hpi.bpel4chor.model.supporting.Import;
import de.hpi.bpel4chor.util.Output;

/**
 * A swimlane containes a process and can be a pool or a pool set. 
 */
public abstract class Swimlane extends GraphicalObject {

	private String name = null;
	private String targetNamespace = null;
	private String prefix = null;
	private Process process = null;
	private List<Import> imports = new ArrayList<Import>();
	
	/**
	 * Constructor. Initializes the swimlane and generates a unique id.
	 * 
	 * @param output The output to print errors to.
	 */
	public Swimlane(Output output) {
		super(output);
	}
	
	/**
	 * Determines the import statement that is defined for the given namespace. 
	 * 
	 * @param namespace The namespace of the import statement.
	 * 
	 * @return The first found import statement or null, if no import 
	 * with this namespace exists.
	 */
	public Import getImportForNamespace(String namespace) {
		for (Iterator<Import> it = this.imports.iterator(); it.hasNext();) {
			Import imp = it.next();
			if (imp.getNamespace().equals(namespace)) {
				return imp;
			}
		}
		return null;
	}
	
	/**
	 * Determines the import statement that is defined for the given prefix. 
	 * 
	 * @param prefix The prefix of the import statement.
	 * 
	 * @return The first found import statement or null, if no import 
	 * with this prefix exists.
	 */
	public Import getImportForPrefix(String prefix) {
		for (Iterator<Import> it = this.imports.iterator(); it.hasNext();) {
			Import imp = it.next();
			if (imp.getPrefix().equals(prefix)) {
				return imp;
			}
		}
		return null;
	}
	
	/**
	 * Adds an import definition to the list of imports for the swimlane.
	 * 
	 * @param imp Import definition to add.
	 */
	public void addImport(Import imp) {
		this.imports.add(imp);
	}

	/**
	 * @return The process that is contained in the swimlane.
	 */
	public Process getProcess() {
		return this.process;
	}

	/**
	 * Sets the process contained in the swimlane.
	 * 
	 * @param process The process contained in the swimlane.
	 */
	public void setProcess(Process process) {
		this.process = process;
	}

	/**
	 * @return A list with imports defined for the swimlane.
	 */
	public List<Import> getImports() {
		return this.imports;
	}

	/**
	 * @return The name of the swimlane.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return The prefix defined for the target namespace of the swimlane.
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * @return The target namespace defined for the swimlane.
	 */
	public String getTargetNamespace() {
		return this.targetNamespace;
	}

	/**
	 * Sets the name of the swimlane.
	 * 
	 * @param name The new name of the swimlane.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the prefix for the target namespace of the swimlane.
	 * 
	 * @param prefix The new prefix.
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}	

	/**
	 * Sets the target namespace of the swimlane.
	 * 
	 * @param targetNamespace The new target namespace.
	 */
	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}
}
