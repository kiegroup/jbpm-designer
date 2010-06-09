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

import java.util.ArrayList;

public class Participant extends ResourcingType {

	String userid;
	String password;
	String firstname;
	String lastname;
	Boolean isAdministrator = false;
	Boolean isAvailable = true;
	ArrayList<Role> roles = new ArrayList<Role>();
	ArrayList<Position> positions = new ArrayList<Position>();
	ArrayList<Capability> capabilities = new ArrayList<Capability>();
	String privileges = "11100000"; //Standard privilege bitmap
	
	/**
	 * constructor of class 
	 */
	public Participant() {
		super();
		id = "PA-" + id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Boolean getIsAdministrator() {
		return isAdministrator;
	}

	public void setIsAdministrator(Boolean isAdministrator) {
		this.isAdministrator = isAdministrator;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public ArrayList<Role> getRoles() {
		return roles;
	}

	public void setRoles(ArrayList<Role> roles) {
		this.roles = roles;
	}

	public ArrayList<Position> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<Position> positions) {
		this.positions = positions;
	}

	public ArrayList<Capability> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(ArrayList<Capability> capabilities) {
		this.capabilities = capabilities;
	}

	public String getPrivileges() {
		return privileges;
	}

	public void setPrivileges(String privileges) {
		this.privileges = privileges;
	}
	
	/**
	 * @see de.hpi.yawl.resourcing.ResourcingType#writeAsMemberOfDistributionSetToYAWL()
	 */
	public String writeAsMemberOfDistributionSetToYAWL(){
		String s = "";
		s += String.format("\t\t<participant>%s</participant>\n", id);
		return s;
	}
	
	/**
	 * @see de.hpi.yawl.resourcing.ResourcingType#writeToYAWL()
	 */
	public String writeToYAWL(){
		String s = "";
		
		s += String.format("\t\t<participant id=\"%s\">\n", id);
		s += String.format("\t\t\t<userid>%s</userid>\n", userid);
	    s += String.format("\t\t\t<password>%s</password>\n", password);
	    s += String.format("\t\t\t<firstname>%s</firstname>\n", firstname);
	    s += String.format("\t\t\t<lastname>%s</lastname>\n", lastname);
	    s = writeDescriptionToYAWL(s);
	    s = writeNotesToYAWL(s);
	    
	    s += String.format("\t\t\t<isAdministrator>%s</isAdministrator>\n", writeIsAdministratorToYAWL());
	    s += String.format("\t\t\t<isAvailable>%s</isAvailable>\n", writeIsAvailableToYAWL());
	    
	    s = writeRolesToYAWL(s);
	    s = writePositionsToYAWL(s);
	    s = writeCapabilitiesToYAWL(s);
	    
	    s += String.format("\t\t\t<privileges>%s</privileges>\n", privileges);
	    s += "\t\t</participant>\n";
		
		return s;
	}

	/**
	 * serializes isAvailable to XML
	 * @return value as String
	 */
	private String writeIsAvailableToYAWL() {
		if (isAvailable)
			return "true";
		
		return "false";
	}

	/**
	 * serializes isAdministrator to XML
	 * @return value as String
	 */
	private String writeIsAdministratorToYAWL() {
		if (isAdministrator)
			return "true";
		
		return "false";
	}

	/**
	 * serializes roles to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeRolesToYAWL(String s) {
		if(roles.isEmpty())
	    	s += "\t\t\t<roles />\n";
	    else{
	    	s += "\t\t\t<roles>\n";
	    	for (Role role : roles){
	    		s += "\t\t\t\t<role>" + role.getId() + "</role>\n";
	    	}
	    	s += "\t\t\t</roles>\n";
	    }
		return s;
	}

	/**
	 * serializes positions to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writePositionsToYAWL(String s) {
		if(positions.isEmpty())
	    	s += "\t\t\t<positions />\n";
	    else{
	    	s += "\t\t\t<positions>\n";
	    	for (Position position : positions){
	    		s += "\t\t\t\t<position>" + position.getId() + "</position>\n";
	    	}
	    	s += "\t\t\t</positions>\n";
	    }
		return s;
	}

	/**
	 * serializes capabilities to XML
	 * @param s XML String
	 * @return XML String
	 */
	private String writeCapabilitiesToYAWL(String s) {
		if(capabilities.isEmpty())
	    	s += "\t\t\t<capabilities />\n";
	    else{
	    	s += "\t\t\t<capabilities>\n";
	    	for (ResourcingType capability : capabilities){
	    		s += "\t\t\t\t<capability>" + capability.getId() + "</capability>\n";
	    	}
	    	s += "\t\t\t</capabilities>\n";
	    }
		return s;
	}
	
}
