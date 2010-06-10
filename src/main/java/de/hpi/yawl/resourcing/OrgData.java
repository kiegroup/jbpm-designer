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

import de.hpi.yawl.FileWritingForYAWL;

public class OrgData implements FileWritingForYAWL {

	ArrayList<Participant> participants = new ArrayList<Participant>();
	ArrayList<Role> roles = new ArrayList<Role>();
	ArrayList<Position> positions = new ArrayList<Position>();
	ArrayList<Capability> capabilities = new ArrayList<Capability>();
	ArrayList<OrgGroup> orgGroups = new ArrayList<OrgGroup>();

	/**
	 * the List of Participants getter
	 * @return List of participants
	 */
	public ArrayList<Participant> getParticipants() {
		return participants;
	}

	/**
	 * the List of Roles getter
	 * @return List of roles
	 */
	public ArrayList<Role> getRoles() {
		return roles;
	}

	/**
	 * the List of Positions getter
	 * @return List of positions
	 */
	public ArrayList<Position> getPositions() {
		return positions;
	}

	/**
	 * the List of Capabilities getter
	 * @return List of capabilities
	 */
	public ArrayList<Capability> getCapabilities() {
		return capabilities;
	}

	/**
	 * the List of OrgGroups getter
	 * @return List of orgGroups
	 */
	public ArrayList<OrgGroup> getOrgGroups() {
		return orgGroups;
	}

	/**
	 * @see de.hpi.yawl.FileWritingForYAWL#writeToYAWL()
	 */
	public String writeToYAWL(){
		String s = "";
		s += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		s += "<orgdata>\n";
		
		s = writeParticipantsToYAWL(s);
		
		s = writeRolesToYAWL(s);
		
		s = writePositionsToYAWL(s);
		
		s = writeOrgGroupsToYAWL(s);
		
		s += "</orgdata>";
		
		return s;
	}

	/**
	 * serializes orgGroups to XML
	 * @param s XML string
	 * @return XML string
	 */
	private String writeOrgGroupsToYAWL(String s) {
		if(orgGroups.isEmpty())
	    	s += "\t<orggroups />\n";
	    else{
	    	s += "\t<orggroups>\n";
	    	for (OrgGroup orgGroup : orgGroups)
	    		s += orgGroup.writeToYAWL();
	    	
	    	s += "\t</orggroups>\n";
	    }
		return s;
	}

	/**
	 * serializes positions to XML
	 * @param s XML string
	 * @return XML string
	 */
	private String writePositionsToYAWL(String s) {
		if(positions.isEmpty())
	    	s += "\t<positions />\n";
	    else{
	    	s += "\t<positions>\n";
	    	for (Position position : positions)
	    		s += position.writeToYAWL();
	    	
	    	s += "\t</positions>\n";
	    }
		return s;
	}

	/**
	 * serializes roles to XML
	 * @param s XML string
	 * @return XML string
	 */
	private String writeRolesToYAWL(String s) {
		if(roles.isEmpty())
	    	s += "\t<roles />\n";
	    else{
	    	s += "\t<roles>\n";
	    	for (Role role : roles)
	    		s += role.writeToYAWL();
	    	
	    	s += "\t</roles>\n";
	    }
		return s;
	}

	/**
	 * serializes participants to XML
	 * @param s XML string
	 * @return XML string
	 */
	private String writeParticipantsToYAWL(String s) {
		if(participants.isEmpty())
	    	s += "\t<participants />\n";
	    else{
	    	s += "\t<participants>\n";
	    	for (Participant participant : participants)
	    		s += participant.writeToYAWL();
	    	
	    	s += "\t</participants>\n";
	    }
		return s;
	}
}