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
	//  	2. calculate the bounds of each shape
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

			// calculate the bounds and ID of each shapes
			// mark the node stencil set
			if (isStencilSet(currentElement)){
				currentElement.setAttribute("isNodeStencilSet", "true");
				generateBounds(currentElement, position);
				generateID(currentElement, position);

				// integrate the first <condition> and <activity> element
				// under a If-block into a <elseIf> element
				if (currentElement.getLocalName().equals("if")){
					handleIfElement(currentElement);
				}
				
				// transform the value of attribute "opaque" from "yes" 
				// to "true"
				String opaque = currentElement.getAttribute("opaque");
				if (opaque.equals("yes")){
					currentElement.setAttribute("opaque", "true");
				}
			}
			
			
			if (currentElement.getLocalName().equals("source")){
				// record the necessary information of links
				recordSourceNodeOfLink(currentElement);
			} else if (currentElement.getLocalName().equals("target")){
				recordTargetNodeOfLink(currentElement);
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
	
	private static boolean isFromBPELNamespace(Node node) {

		return (node.getNamespaceURI() == null ||
				node.getNamespaceURI().equals("http://docs.oasis-open.org/wsbpel/2.0/process/abstract") ||
				node.getNamespaceURI().equals("http://docs.oasis-open.org/wsbpel/2.0/process/executable"));
	}

		private static boolean isStencilSet(Element currentElement) {
		String localName = currentElement.getLocalName();
		//System.out.println(localName);
		return (isFromBPELNamespace(currentElement) &&
				(localName.equals("process")
				|| localName.equals("invoke")
				|| localName.equals("receive")
				|| localName.equals("reply")
				|| localName.equals("assign")
				|| localName.equals("copy")
				|| localName.equals("empty")
				|| localName.equals("opaqueActivity")
				|| localName.equals("validate")
				|| localName.equals("extensionActivity")
				|| localName.equals("wait")
				|| localName.equals("throw")
				|| localName.equals("exit")
				|| localName.equals("rethrow")
				|| localName.equals("if")
				|| localName.equals("elseif")
				|| localName.equals("else")
				|| localName.equals("flow")
				|| localName.equals("sequence")
				|| localName.equals("pick")
				|| localName.equals("onMessage")
				|| localName.equals("while")
				|| localName.equals("repeatUntil")
				|| localName.equals("forEach")
				|| localName.equals("compensate")
				|| localName.equals("compensateScope")
				|| localName.equals("scope")
				|| localName.equals("onEvent")
				|| localName.equals("eventHandlers")
				|| localName.equals("faultHandlers")
				|| localName.equals("compensationHandler")
				|| localName.equals("terminationHandler")
				|| localName.equals("catch")
				|| localName.equals("catchAll")));
	}
	
	/******************* generate Bounding and ID *****************/
	private void generateID(Element currentElement, int position) {
		if (isFromBPELNamespace(currentElement) && currentElement.getLocalName().equals("process")){
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

	private void generateBounds(Element currentElement, int position) {
		if (isFromBPELNamespace(currentElement) && currentElement.getLocalName().equals("process")){
			setBounds(currentElement, 114, 18, 714, 518);
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
		if (isFromBPELNamespace(currentElement) && currentElement.getLocalName().equals("flow")){
			
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
		
		setBounds(currentElement, LUX, LUY, RLX, RLY);
					
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
		
		Element currentElement = (Element) currentNode;
		if (isFromBPELNamespace(currentNode) && 
				(currentElement.getLocalName().equals("receive")
				|| currentElement.getLocalName().equals("reply")
				|| currentElement.getLocalName().equals("invoke")
				|| currentElement.getLocalName().equals("assign")
				|| currentElement.getLocalName().equals("throw")
				|| currentElement.getLocalName().equals("exit")
				|| currentElement.getLocalName().equals("wait")
				|| currentElement.getLocalName().equals("empty")
				|| currentElement.getLocalName().equals("sequence")
				|| currentElement.getLocalName().equals("if")
				|| currentElement.getLocalName().equals("while")
				|| currentElement.getLocalName().equals("repeatUntil")
				|| currentElement.getLocalName().equals("forEach")
				|| currentElement.getLocalName().equals("pick")
				|| currentElement.getLocalName().equals("flow")
				|| currentElement.getLocalName().equals("scope")
				|| currentElement.getLocalName().equals("compensate")
				|| currentElement.getLocalName().equals("compensateScope")
				|| currentElement.getLocalName().equals("rethrow")
				|| currentElement.getLocalName().equals("validate")
				|| currentElement.getLocalName().equals("extensionActivity"))){
			
			return true;
		}
		return false;
	}
	
	private void setBounds(Element currentElement, int LUX, int LUY,
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
		if (!isFromBPELNamespace(currentElement))
			return 100;
		
		if (currentElement.getLocalName().equals("flow")){
			return 290;
		} else if (isFromBPELNamespace(currentElement) &&
				(currentElement.getLocalName().equals("eventHandlers")
				|| currentElement.getLocalName().equals("faultHandlers")
				|| currentElement.getLocalName().equals("compensationHandler")
				|| currentElement.getLocalName().equals("terminationHandler"))){		
			return 160;
		} else {		
			return 100;
		}
	}

	private int getHeightOf(Element currentElement) {
		if (isFromBPELNamespace(currentElement) && currentElement.getLocalName().equals("flow")){
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
					isFromBPELNamespace(child) && child.getLocalName().equals("condition")){
				condition = child;
			}
		}
		
		if (condition == null && activity == null){
			return;
		}
		
		Node succ = null;
		
		// remove both nodes from <if> element
		if (condition != null) {
			succ = condition.getNextSibling();
			// succ is either activity (if activity for condition is given) or an else(if) or null
			ifElement.removeChild(condition);
		}
		if (activity != null) {
			succ = activity.getNextSibling();
			// succ is now either an else(if) or null
			ifElement.removeChild(activity);
		}
		
		// append them to <elseif> element
		Element elseif = ifElement.getOwnerDocument().createElementNS(ifElement.getNamespaceURI(), "elseif");
		if (condition != null)
			elseif.appendChild(condition);
		if (activity != null)
			elseif.appendChild(activity);
		
		// insert elseif as first child of <if> - to keep the ordering of the elements
		ifElement.insertBefore(elseif, succ);
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
			if (child instanceof Element &&
					isFromBPELNamespace(child)
					&& child.getLocalName().equals(childName)){
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