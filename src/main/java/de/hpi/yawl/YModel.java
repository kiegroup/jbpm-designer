package de.hpi.yawl;

/**
 * Copyright (c) 2010, Armin Zamani
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * s
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.*;

public class YModel implements FileWritingForYAWL {
	private String uri; // The uri of the YAWL model
	private String description = "No description has been given.";
	private String dataTypeDefinition = "";
	private HashMap<String, YDecomposition> decompositions = new HashMap<String, YDecomposition>(); // All decompositions of the YAWL model

	/**
	 * Create a new YAWL mode, given its uri.
	 * @param uri The given uri.
	 */
	public YModel(String uri) {
		this.uri = uri.replaceAll(" ","."); // spaces are not allowed in uri's
	}

	/**
	 * Adds a given decomposition to the YAWL model.
	 * @param decomposition The given decomposition
	 */
	public void addDecomposition(YDecomposition decomposition) {
		decompositions.put(decomposition.getID(), decomposition);
	}
	
	/**
	 * creates a decomposition and adds it to the YAWL model
	 * @param id the decomposition id
	 * @return created decomposition
	 */
	public YDecomposition createDecomposition(String id){
		YDecomposition decomposition = new YDecomposition(id, false, XsiType.NetFactsType);
		addDecomposition(decomposition);
		return decomposition;
	}

	/**
	 * the decompositions getter
	 * @return collection of decompositions
	 */
	public Collection<YDecomposition> getDecompositions() {
		return decompositions.values();
	}

	/**
	 * returns a decomposition according to the given id
	 * @param id decomposition identifier
	 * @return decomposition
	 */
	public YDecomposition getDecomposition(String id) {
		return decompositions.get(id);
	}

	/**
	 * Returns whether the given name corresponds to a non-empty decomposition
	 * @param name The given name
	 * @return Whether this name corresponds to a non-empty decomposition
	 */
	public boolean isComposite(String name) {
		YDecomposition decomposition = decompositions.get(name);
		if (decomposition == null) {
			return false;
		}
		return!decomposition.getNodes().isEmpty();
	}

	public void setDataTypeDefinition(String dataTypeDefinition) {
		this.dataTypeDefinition = dataTypeDefinition;
	}

	public String getDataTypeDefinition() {
		return dataTypeDefinition;
	}

	/**
	 * @see de.hpi.yawl.FileWritingForYAWL#writeToYAWL()
	 */
	public String writeToYAWL() {
		String s = "";
		s += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		s += "<specificationSet xmlns=\"http://www.yawlfoundation.org/yawlschema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
		s += "version=\"2.0\" xsi:schemaLocation=\"http://www.yawlfoundation.org/yawlschema http://www.yawlfoundation.org/yawlschema/YAWL_Schema2.0.xsd\" >\n";
		s += String.format("\t<specification uri=\"%s\">\n", uri);
		s += "\t\t<metaData>\n";
		s += String.format("\t\t\t<description>%s</description>\n", description);
		s += "\t\t</metaData>\n";
		if(dataTypeDefinition == null || dataTypeDefinition.isEmpty())
			s += "\t\t<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" />\n";
		else
			s += "\t\t" + dataTypeDefinition + "\n";
		
		for (YDecomposition decomposition: decompositions.values())
			s += decomposition.writeToYAWL();
		
		s += "\t</specification>\n";
		s += "</specificationSet>\n";

		return s;
	}
}
