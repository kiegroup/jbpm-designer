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

public class OrgGroup extends ResourcingType {

	String groupType;
	OrgGroup belongsToID;
	
	/**
	 * constructor of class 
	 */
	public OrgGroup() {
		super();
		id = "OG-" + id;
	}

	/**
	 * the groupType getter
	 * @return groupType
	 */
	public String getGroupType() {
		return groupType;
	}

	/**
	 * the groupType setter
	 * @param groupType
	 */
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	/**
	 * the belongsToID getter
	 * @return belongsToID
	 */
	public OrgGroup getBelongsToID() {
		return belongsToID;
	}

	/**
	 * the belongsToID getter
	 * @param belongsToID
	 */
	public void setBelongsToID(OrgGroup belongsToID) {
		this.belongsToID = belongsToID;
	}
	
	/**
	 * @see de.hpi.yawl.resourcing.ResourcingType#writeAsMemberOfDistributionSetToYAWL()
	 */
	public String writeAsMemberOfDistributionSetToYAWL(){
		String s = "";
		s += String.format("\t\t<orgGroup>%s</orgGroup>\n", id);
		return s;
	}

	/**
	 * serializes the BelongsToId to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeBelongsToIdToYAWL(String s) {
		if(belongsToID != null)
			s += String.format("\t\t\t<belongsToID>%s</belongsToID>\n", belongsToID.getId());
		return s;
	}
	
	/**
	 * @see de.hpi.yawl.resourcing.ResourcingType#writeToYAWL()
	 */
	public String writeToYAWL(){
		String s = "";
		s += "\t\t<orgGroup id=\"" + id + "\">\n";
		s += "\t\t\t<groupName>" + name + "</groupName>\n";
		s += "\t\t\t<groupType>" + groupType + "</groupType>\n";
		s = writeDescriptionToYAWL(s);
		s = writeNotesToYAWL(s);
		s = writeBelongsToIdToYAWL(s);
		s += "\t\t</orgGroup>\n";
		
		return s;
	}


	
	
}
