package org.oryxeditor.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Copyright (c) 2008-2009 
 * 
 * Zhen Peng
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
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class BPELImportPreprocessor {
	
	
	private HashMap<String, Object[]> linksMap = new HashMap<String, Object[]>();
	
	private ArrayList<String> linksList = new ArrayList<String>();

	// do a pre-processing on this bpel source
	// in this preprocessor the following works will be done:
	//  	1. mark all node stencil sets with the attribute "isNodeStencilSet"
	//         mark all edge stencil sets with the attribute "isEdgeStencilSet"
	//         in order to avoid the prefix problem
	//  	2. calculate the bounding of each shape
	//      3. generate for each shape a ID
	//  	4. move the <link> elements from <links> element to
	//         top of the root <process> element, and record linkID
	//         as the value of element <outgoing> under the corresponding
	//         activity, so they could be easier to handle in BPEL2eRDF.xslt
	//      5. integrate the first <condition> and <activity> element
	//         under a If-block into a <elseIF> element, so they
	//         they could be easier to transform in BPEL2eRDF.xslt
	//      6. transform the value of attribute "opaque" from "yes" to "true"
	public Document preprocessDocument(Document document) {
		
		// use a hash map to record all link node informations, so that we can
		// easily get all necessary informations about a link
		//
		// in this hash map:
		// key :link name - type String
		// value : {sourceNode ID,targetNode ID, linkID
		// 				transitionCondition element}  - type Object
		linksMap = new HashMap<String, Object[]>();
		
		// record all link names in this list, in the end we can immediately
		// find out how many links do we have and what their names are.
		// the linkID is also based on the index of the linkName in linksList
		// each linkID is in form "link_index"
		linksList = new ArrayList<String>();
		
		handleNode (document, 0);
		
		buildLinkElements(document);
		
		return document;
	}

	private void handleNode(Node currentNode, int position) {
		
		// handle only the Nodes with type "Element" and "Document" (root element), 
		// the other types e.g. "Attr","Comment" will be ignored 
		if (!(currentNode instanceof Element 
				|| currentNode instanceof Document)) {
			return;
		};
		
		// handle the current node first
		if (currentNode instanceof Element){
			
			Element currentElement = (Element) currentNode;

			// calculate the bounding and ID of each shapes
			// mark the node stencil set
			if (isStencilSet(currentElement)){
				currentElement.setAttribute("isNodeStencilSet", "true");
				generateBounding(currentElement, position);
				generateID(currentElement, position);
			}
			
			// record the necessary information of links
			if (currentNode.getNodeName().equals("source")){
				recordSourceNodeOfLink(currentElement);
			}
			
			if (currentNode.getNodeName().equals("target")){
				recordTargetNodeOfLink(currentElement);
			}
				
			// integrate the first <condition> and <activity> element
			// under a If-block into a <elseIF> element
			if (currentNode.getNodeName().equals("if")){
				handleIfElement(currentElement);
			}
			
			// transform the value of attribute "opaque" from "yes" 
			// to "true"
			String opaque = currentElement.getAttribute("opaque");
			if (opaque.equals("yes")){
				currentElement.setAttribute("opaque", "true");
			}
		}
		
		// after the current node is already handled, research recursive,
		// work on the child nodes.
		Node child;
		
		NodeList childNodes = currentNode.getChildNodes();
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if (child instanceof Element){
				handleNode (child, i);
			}
		};
		

	}
	

	private boolean isStencilSet(Element currentElement) {
		if (currentElement.getNodeName().equals("process")
				|| currentElement.getNodeName().equals("invoke")
				|| currentElement.getNodeName().equals("receive")
				|| currentElement.getNodeName().equals("reply")
				|| currentElement.getNodeName().equals("assign")
				|| currentElement.getNodeName().equals("copy")
				|| currentElement.getNodeName().equals("empty")
				|| currentElement.getNodeName().equals("opaqueActivity")
				|| currentElement.getNodeName().equals("validate")
				|| currentElement.getNodeName().equals("extensionActivity")
				|| currentElement.getNodeName().equals("wait")
				|| currentElement.getNodeName().equals("throw")
				|| currentElement.getNodeName().equals("exit")
				|| currentElement.getNodeName().equals("rethrow")
				|| currentElement.getNodeName().equals("if")
				|| currentElement.getNodeName().equals("elseif")
				|| currentElement.getNodeName().equals("else")
				|| currentElement.getNodeName().equals("flow")
				|| currentElement.getNodeName().equals("sequence")
				|| currentElement.getNodeName().equals("link")
				|| currentElement.getNodeName().equals("pick")
				|| currentElement.getNodeName().equals("onMessage")
				|| currentElement.getNodeName().equals("while")
				|| currentElement.getNodeName().equals("repeatUntil")
				|| currentElement.getNodeName().equals("forEach")
				|| currentElement.getNodeName().equals("compensate")
				|| currentElement.getNodeName().equals("compensateScope")
				|| currentElement.getNodeName().equals("scope")
				|| currentElement.getNodeName().equals("onEvent")
				|| currentElement.getNodeName().equals("eventHandlers")
				|| currentElement.getNodeName().equals("faultHandlers")
				|| currentElement.getNodeName().equals("compensationHandler")
				|| currentElement.getNodeName().equals("terminationHandler")
				|| currentElement.getNodeName().equals("catch")
				|| currentElement.getNodeName().equals("catchAll")){
        
			return true;
		}
			    
		return false;
	}

	/******************* generate Bounding and ID *****************/
	private void generateID(Element currentElement, int position) {
		if (currentElement.getNodeName().equals("process")){
			setID (currentElement, "oryx_0"); 
		} else {
			Element parent = (Element)currentElement.getParentNode();
			String parentID = getIDOfElement (parent);
			setID (currentElement, parentID + "_" + position);
		}
	}
	
	private void setID(Element currentElement, String id) {
		currentElement.setAttribute("id", id);
	}

	private String getIDOfElement(Element currentElement) {
		return currentElement.getAttribute("id");
	}

	private void generateBounding(Element currentElement, int position) {
		if (currentElement.getNodeName().equals("process")){
			setBound(currentElement, 114, 18, 714, 518);
			return;
		}
		
		// locate the left upper point of parent shape
		int LUXOfParent = getBoundLeftUpperX((Element)currentElement
				.getParentNode());
		int LUYOfParent = getBoundLeftUpperY((Element)currentElement
				.getParentNode());
		
		// calculate the left upper point of the current shape
		int LUX;
		int LUY;
		
		// handle the child nodes of flow in a different way
		// each row shows 3 shapes.
		if (currentElement.getParentNode().getNodeName().equals("flow")){
			
			int index = getIndexOfShape(currentElement);
			
			// the first shape of the first row
			if (index == 1){
				LUX = 30;
				LUY = 30;
			// the first shape of a row except the first row
			} else if ((index - 1) % 3 == 0){
				// find the upper shape
				Node flow = currentElement.getParentNode();
				NodeList childList = flow.getChildNodes();
				Element upperElement = (Element)childList.item(position - 3);
				
				LUX = 30;
				LUY = getBoundRightLowerY(upperElement) + 50;
			} else {
				// find the link shape
				Node flow = currentElement.getParentNode();
				NodeList childList = flow.getChildNodes();
				Element linkElement = (Element)childList.item(position - 1);
				
				LUX = getBoundRightLowerX(linkElement) + 50;
				LUY = getBoundLeftUpperY(linkElement);
			}
			
		} else {

			LUX = LUXOfParent + 30 + position * 5;
			LUY = LUYOfParent + 30 + position * 5;
		}
		
		int width = getWidthOf (currentElement);
		int height = getHeightOf (currentElement);

		int RLX = LUX + width;
		int RLY = LUY + height;
		
		setBound(currentElement, LUX, LUY, RLX, RLY);
					
	}


	private int getIndexOfShape(Element currentElement) {
		
		int index = 0;
		Node child;
		
		Node flow = currentElement.getParentNode();
		NodeList childList = flow.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++){
			child = childList.item(i);
			if (isActivity(child)){
				index ++;
				Element childElement = (Element) child;
				if (currentElement.equals(childElement)){
					return index;
				}
			}
		}
		return -1;
	}

	private boolean isActivity(Node currentNode) {
		
		if (!(currentNode instanceof Element)){
			return false;
		}
		
		if (currentNode.getNodeName().equals("receive")
				|| currentNode.getNodeName().equals("reply")
				|| currentNode.getNodeName().equals("invoke")
				|| currentNode.getNodeName().equals("assign")
				|| currentNode.getNodeName().equals("throw")
				|| currentNode.getNodeName().equals("exit")
				|| currentNode.getNodeName().equals("wait")
				|| currentNode.getNodeName().equals("empty")
				|| currentNode.getNodeName().equals("sequence")
				|| currentNode.getNodeName().equals("if")
				|| currentNode.getNodeName().equals("while")
				|| currentNode.getNodeName().equals("repeatUntil")
				|| currentNode.getNodeName().equals("forEach")
				|| currentNode.getNodeName().equals("pick")
				|| currentNode.getNodeName().equals("flow")
				|| currentNode.getNodeName().equals("scope")
				|| currentNode.getNodeName().equals("compensate")
				|| currentNode.getNodeName().equals("compensateScope")
				|| currentNode.getNodeName().equals("rethrow")
				|| currentNode.getNodeName().equals("validate")
				|| currentNode.getNodeName().equals("extensionActivity")){
			
			return true;
		}
		return false;
	}
	
	private void setBound(Element currentElement, int LUX, int LUY,
			int RLX, int RLY) {
		
		currentElement.setAttribute("boundLUX", Integer.toString(LUX));
		currentElement.setAttribute("boundLUY", Integer.toString(LUY));
		currentElement.setAttribute("boundRLX", Integer.toString(RLX));
		currentElement.setAttribute("boundRLY", Integer.toString(RLY));
		
	}

	private int getBoundLeftUpperX(Element currentElement) {
		String LUX = currentElement.getAttribute("boundLUX");
		return Integer.parseInt(LUX);
	}
	
	private int getBoundLeftUpperY(Element currentElement) {
		String LUY = currentElement.getAttribute("boundLUY");
		return Integer.parseInt(LUY);
	}

	private int getBoundRightLowerX(Element currentElement) {
		String RLX = currentElement.getAttribute("boundRLX");
		return Integer.parseInt(RLX);
	}
	
	private int getBoundRightLowerY(Element currentElement) {
		String RLY = currentElement.getAttribute("boundRLY");
		return Integer.parseInt(RLY);
	}
	private int getWidthOf(Element currentElement) {
		if (currentElement.getNodeName().equals("flow")){
			return 290;
		} else if (currentElement.getNodeName().equals("eventHandlers")
				|| currentElement.getNodeName().equals("faultHandlers")
				|| currentElement.getNodeName().equals("compensationHandler")
				|| currentElement.getNodeName().equals("terminationHandler")){		
			return 160;
		} else {		
			return 100;
		}
	}

	private int getHeightOf(Element currentElement) {
		if (currentElement.getNodeName().equals("flow")){	
			return 250;	
		}  else {
			return 80;
		}
	}
	
	/*********************** handle If elements *****************/
	private void handleIfElement(Element ifElement) {
		// find element <condition> and the single activity
		Node condition = null;
		Node activity = null;
		
		Node child;
		
		NodeList childList = ifElement.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++){
			child = childList.item(i);
			
			if (isActivity(child)){
				activity = child;
			};
			
			if (child instanceof Element && 
					child.getNodeName().equals("condition")){
				condition = child;
			}
		}
		
		if (condition == null && activity == null){
			return;
		}
		
		// remove both nodes from <if> element
		ifElement.removeChild(condition);
		ifElement.removeChild(activity);
		
		// append them to <elseif> element
		Element elseif = ifElement.getOwnerDocument().createElement("elseif");
		elseif.appendChild(condition);
		elseif.appendChild(activity);
		
		// append <elseif> to <if>
		ifElement.appendChild(elseif);
	}
	
	/*********************** handle link elements *****************/
	private void recordSourceNodeOfLink(Element source) {
		// get linkName
		String linkName = source.getAttribute("linkName");
		
		int index = getIndexInLinksList(linkName);
		
		if (index == -1){
			linksList.add(linkName);
			linksMap.put(linkName, 
					new Object[]{null, null, null, null});
			index = linksList.size()-1;
		}
		
		// get transitionCondition
		Element transitionCondition = getChildElementWithNodeName(source,
				"transitionCondition");
		
		Element sources = (Element)source.getParentNode();
		Element currentElement = (Element)sources.getParentNode();
		
		String sourceID = getIDOfElement(currentElement);
		String targetID = getTargetID(linkName);
		String linkID = "link_" + Integer.toString(index);
		
		linksMap.put(linkName, 
				new Object[]{sourceID, targetID, linkID, transitionCondition});
		
		// record linkID as element <outGoing> under currentElement
		Element outgoing = source.getOwnerDocument().createElement("outgoing");
		outgoing.setAttribute("linkID", linkID);
		currentElement.appendChild(outgoing);
	}

	private void recordTargetNodeOfLink(Element target) {
		String linkName = target.getAttribute("linkName");
		
		int index = getIndexInLinksList(linkName);
		
		if (index == -1){
			linksList.add(linkName);
			linksMap.put(linkName, 
					new Object[]{null, null, null, null});
			index = linksList.size() - 1;
		}
		
		Element targets = (Element)target.getParentNode();
		Element currentElement = (Element)targets.getParentNode();
		
		String sourceID = getSourceID(linkName);
		String targetID = getIDOfElement(currentElement);
		String linkID = "link_" + Integer.toString(index);
		Element transitionCondition = getTransitionCondition(linkName);
		
		linksMap.put(linkName, 
				new Object[]{sourceID, targetID, linkID, transitionCondition});
		
	}
	
	private int getIndexInLinksList(String linkName) {
		int index = 0;
		String nameItem;
		
		Iterator<String> iterLinksList = linksList.iterator();
		while (iterLinksList.hasNext()){
			index ++;
			nameItem = iterLinksList.next();
			if (nameItem.equals(linkName)){
				return index;
			}
		}
		return -1;
	}
	
	private String getSourceID(String linkName) {
		Object[] infoSet = linksMap.get(linkName);
		return (String)infoSet[0];
	}
	
	private String getTargetID(String linkName) {
		Object[] infoSet = linksMap.get(linkName);
		return (String)infoSet[1];
	}

	private String getLinkID(String linkName) {
		Object[] infoSet = linksMap.get(linkName);
		return (String)infoSet[2];
	}
	
	private Element getTransitionCondition(String linkName) {
		Object[] infoSet = linksMap.get(linkName);
		return (Element)infoSet[3];
	}
	
	private Element getChildElementWithNodeName(Node currentNode, 
			String childName) {
		
		NodeList childrenList = currentNode.getChildNodes();
		for (int i = 0; i < childrenList.getLength(); i++){
			Node child = childrenList.item(i);
			if (child instanceof Element 
					&& child.getNodeName().equals(childName)){
				return (Element)child;
			}
		}
		return null;
	}
	
	/*********************** build link element ***********************/
	private void buildLinkElements(Document document) {
		
		Element process = getChildElementWithNodeName(document, "process");
		
		if (process == null) {
			return;
		}
		
		String linkName;
		String linkID;
		String targetID;
		Element transitionCondition;
		
		Iterator<String> iterLinksList = linksList.iterator();
		while (iterLinksList.hasNext()){
			linkName = iterLinksList.next();
			
			linkID = getLinkID(linkName);
			targetID = getTargetID(linkName);
			transitionCondition = getTransitionCondition(linkName);
			
			Element linkInfoSet = document.createElement("linkInfoSet");
			linkInfoSet.setAttribute("id", linkID);
			linkInfoSet.setAttribute("isEdgeStencilSet", "true");
			linkInfoSet.setAttribute("linkName", linkName);
			linkInfoSet.setAttribute("targetID", targetID);
			linkInfoSet.appendChild(transitionCondition);
			
			process.appendChild(linkInfoSet);
		}
	}

}