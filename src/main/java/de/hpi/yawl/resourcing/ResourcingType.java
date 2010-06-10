package de.hpi.yawl.resourcing;

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

import java.util.UUID;

import de.hpi.yawl.FileWritingForYAWL;

public abstract class ResourcingType implements FileWritingForYAWL {

	String id = "";
	String name = "";
	String description = "";
	String notes = "";

	/**
	 * constructor of class 
	 */
	public ResourcingType() {
		super();
		UUID generatedUuid = UUID.randomUUID();
		this.id = generatedUuid.toString();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * Serializes the resourcingType object to XML as a member of a YAWL tasks distributionSet
	 * @return XML String
	 */
	public String writeAsMemberOfDistributionSetToYAWL(){
		return "";
	}
	
	/**
	 * @see de.hpi.yawl.FileWritingForYAWL#writeToYAWL()
	 */
	public String writeToYAWL(){		
		return "";
	}

	/**
	 * serializes notes to XML
	 * @param s XML String
	 * @return XML String
	 */
	protected String writeNotesToYAWL(String s) {
		if (notes.isEmpty())
			s+= "\t<notes />\n";
		else
			s += String.format("\t\t\t<notes>%s</notes>\n", notes);

		return s;
	}

	/**
	 * serializes description to XML
	 * @param s XML String
	 * @return XML String
	 */
	protected String writeDescriptionToYAWL(String s) {
		if (description.isEmpty())
			s+= "\t<description />\n";
		else
			s += String.format("\t\t\t<description>%s</description>\n", description);

		return s;
	}

	/**
	 * serializes name to XML
	 * @param s XML String
	 * @return XML String
	 */
	protected String writeNameToYAWL(String s) {
		s += String.format("\t\t\t<name>%s</name>\n", name);
		return s;
	}

}