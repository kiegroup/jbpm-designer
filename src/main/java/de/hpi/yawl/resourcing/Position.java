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

public class Position extends ResourcingType {

	String positionId = "";
	OrgGroup orgGroupBelongingTo;
	Position reportsTo;
	
	/**
	 * constructor of class 
	 */
	public Position() {
		super();
		id = "PO-" + id;
	}

	public String getPositionId() {
		return positionId;
	}

	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}

	public OrgGroup getOrgGroupBelongingTo() {
		return orgGroupBelongingTo;
	}

	public void setOrgGroupBelongingTo(OrgGroup orgGroupBelongingTo) {
		this.orgGroupBelongingTo = orgGroupBelongingTo;
	}

	public Position getReportsTo() {
		return reportsTo;
	}

	public void setReportsTo(Position reportsTo) {
		this.reportsTo = reportsTo;
	}
	
	/**
	 * @see de.hpi.yawl.resourcing.ResourcingType#writeAsMemberOfDistributionSetToYAWL()
	 */
	public String writeAsMemberOfDistributionSetToYAWL(){
		String s = "";
		s += String.format("\t\t<position>%s</position>\n", id);
		return s;
	}
	
	/**
	 * @see de.hpi.yawl.resourcing.ResourcingType#writeToYAWL()
	 */
	public String writeToYAWL(){
		String s = "";
		s += "\t\t<position id=\"" + id + "\">\n";
		s += "\t\t\t<title>" + name + "</title>\n";
		s = writePositionIdToYAWL(s);
		s = writeDescriptionToYAWL(s);
		s = writeNotesToYAWL(s);
		s = writeOrgGroupBelongingToToYAWL(s);
		s = writeReportsToToYAWL(s);
		s += "\t\t</position>\n";
		
		return s;
	}

	/**
	 * serializes positionId to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writePositionIdToYAWL(String s) {
		if (positionId.isEmpty())
			s += "\t\t\t<positionid />\n";
		else
			s += String.format("\t\t\t<positionid>%s</positionid>\n", positionId);

		return s;
	}

	/**
	 * serializes reportsTo to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeReportsToToYAWL(String s) {
		if(reportsTo != null)
			s += "\t\t\t<reportstoid>" + reportsTo.getId() + "</reportstoid>\n";
		
		return s;
	}

	/**
	 * serializes orgGroupBelongingTo to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeOrgGroupBelongingToToYAWL(String s) {
		if(orgGroupBelongingTo != null)
			s += "\t\t\t<orggroupid>" + orgGroupBelongingTo.getId() + "</orggroupid>\n";
		
		return s;
	}
	
}
