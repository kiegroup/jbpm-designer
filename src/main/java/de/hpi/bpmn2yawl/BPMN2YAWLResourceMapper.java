package de.hpi.bpmn2yawl;

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

import java.util.HashMap;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Pool;
import de.hpi.yawl.resourcing.*;

public class BPMN2YAWLResourceMapper {

	/**
	 * the hashmap for BPMN resourcing nodes and YAWL resourcing objects
	 */
	private HashMap<Node, ResourcingType> nodeMap;
	
	/**
	 * the resourcing nodeMap getter
	 * @return resourcing nodeMap
	 */
	public HashMap<Node, ResourcingType> getNodeMap() {
		return nodeMap;
	}

	/**
	 * the resourcing nodeMap setter
	 * @param nodeMap resourcing nodeMap
	 */
	public void setNodeMap(HashMap<Node, ResourcingType> nodeMap) {
		this.nodeMap = nodeMap;
	}

	/**
	 * maps BPMN swimlanes in a BPMN diagram to YAWL resourcing
	 * @param diagram BPMN diagram
	 * @return serialized YAWL resourcing information
	 */
	public String translate(BPMNDiagram diagram) {
		
		setNodeMap(new HashMap<Node, ResourcingType>());
		OrgData orgData = new OrgData();

		for (Node node : diagram.getChildNodes()){
			if(!(node instanceof Pool))
				continue;
			
			Pool pool = (Pool)node;
			
			if(pool.getChildNodes().size() == 0)
				//pool has no nodes
				continue;
			
			mapToOrgGroup(orgData, pool, nodeMap);
			
			for (Node subNode : pool.getChildNodes()){
				if (subNode instanceof Lane){
					Lane lane = (Lane) subNode;
					handleLaneAccordingResourcingType(orgData, lane, nodeMap);
				}
			}
		}
		return orgData.writeToYAWL();
	}

	/**
	 * maps a BPMN Lane to a YAWL participant
	 * @param orgData organizational data
	 * @param lane Lane to be mapped
	 * @param nodeMap resourcing nodeMap
	 */
	private void mapLaneToParticipant(OrgData orgData, Lane lane,
			HashMap<Node, ResourcingType> nodeMap) {
		Participant participant = new Participant();
		participant.setLastname(lane.getLabel());
		orgData.getParticipants().add(participant);
		nodeMap.put(lane, participant);
	}

	/**
	 * maps a BPMN Lane to a YAWL OrgGroup
	 * @param orgData organizational data
	 * @param lane Lane to be mapped
	 * @param nodeMap resourcing nodeMap
	 */
	private void mapToOrgGroup(OrgData orgData, Node node,
			HashMap<Node, ResourcingType> nodeMap) {
		OrgGroup orgGroup = new OrgGroup();
		orgGroup.setName(node.getLabel());
		orgGroup.setGroupType("GROUP");
		if (node instanceof Lane){
			Lane lane = (Lane)node;
			orgGroup.setBelongsToID((OrgGroup)nodeMap.get(lane.getPool()));
		}
		orgData.getOrgGroups().add(orgGroup);
		nodeMap.put(node, orgGroup);
	}

	/**
	 * maps a BPMN Lane to a YAWL position
	 * @param orgData organizational data
	 * @param lane Lane to be mapped
	 * @param nodeMap resourcing nodeMap
	 */
	private void mapLaneToPosition(OrgData orgData, Lane lane, 
			HashMap<Node, ResourcingType> nodeMap) {
		Position position = new Position();
		position.setName(lane.getLabel());
		position.setOrgGroupBelongingTo((OrgGroup)nodeMap.get(lane.getPool()));
		orgData.getPositions().add(position);
		nodeMap.put(lane, position);
	}

	/**
	 * maps a BPMN Lane to a YAWL role
	 * @param orgData organizational data
	 * @param lane Lane to be mapped
	 * @param nodeMap resourcing nodeMap
	 */
	private void mapLaneToRole(OrgData orgData, Lane lane, HashMap<Node, ResourcingType> nodeMap) {
		Role role = new Role();
		role.setName(lane.getLabel());
		orgData.getRoles().add(role);
		nodeMap.put(lane, role);
	}
	
	/**
	 * maps the given lane to participant, role or position according to Resourcing Type attribute
	 * if the lane is not nested
	 * @param orgData organizational data
	 * @param lane Lane to be mapped
	 * @param nodeMap resourcing nodeMap
	 */
	private void handleLaneAccordingResourcingType(OrgData orgData, Lane lane, HashMap<Node, ResourcingType> nodeMap)
	{
		boolean shouldCheckResourceType = checkForNestedLane(orgData, lane, nodeMap);

		if (shouldCheckResourceType){
			if(lane.getResourcingType().equalsIgnoreCase("participant"))
				mapLaneToParticipant(orgData, lane, nodeMap);
			else if(lane.getResourcingType().equalsIgnoreCase("role"))
				mapLaneToRole(orgData, lane, nodeMap);
			else if(lane.getResourcingType().equalsIgnoreCase("position"))
				mapLaneToPosition(orgData, lane, nodeMap);
		}
	}

	/**
	 * checks if the given lane is nested
	 * @param orgData organizational data
	 * @param lane Lane to be mapped
	 * @param nodeMap resourcing nodeMap
	 * @return result of check
	 */
	private boolean checkForNestedLane(OrgData orgData, Lane lane, HashMap<Node, ResourcingType> nodeMap)
	{
		boolean isNotNested = true;
		
		for (Node laneNode : lane.getChildNodes()){
			if (laneNode instanceof Lane){
				if (isNotNested){
					mapToOrgGroup(orgData, lane, nodeMap);
					isNotNested = false;
				}
				handleLaneAccordingResourcingType(orgData, (Lane)laneNode, nodeMap);
			}
		}
		return isNotNested;
	}

}
