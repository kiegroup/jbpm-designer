package org.oryxeditor.server;

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
	
	
	// do a pre-processing on this bpel source
	// in this preprocessor the following works will be done:
	//  	1. handle different namespaces of bpel process
	//  	2. calculate the bounding of each shape
	//  	3. move the <link> elements from <links> element to
	//         top of the root <process> element, so they could
	//         be easier to handle in BPEL2eRDF.xslt
	//      4. integrate the first <condition> and <activity> element
	//         under a If-block into a <elseIF> element, so they
	//         they could be easier to transform in BPEL2eRDF.xslt
	public Document preprocessDocument(Document document) {
		
		handleNode (document, 0);
		
		buildLinkElements();
		
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
			
			if (currentNode.getNodeName().equals("process")){
				// handle different namespaces of bpel process
				handleNamespaceOfProcess(currentElement);
			}
		
			// calculate the bounding of each shape 
			if (isStencilSet(currentElement)){
				generateBounding(currentElement, position);
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
			
		}
		
		// after the current node is already handled, research recursive,
		// work on the child nodes.
		NodeList childNodes = currentNode.getChildNodes();
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if (child instanceof Element){
				handleNode (child, i);
			}
		};
		

	}
	
	private void handleNamespaceOfProcess(Element currentElement) {
		// TODO Auto-generated method stub
		
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

	/*********************** generate Bounding *****************/
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
			
			int index = getIndexOfElement(currentElement);
			
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


	private int getIndexOfElement(Element currentElement) {
		int index = 0;
		
		Node flow = currentElement.getParentNode();
		NodeList childList = flow.getChildNodes();
		Node child;
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
				|| currentElement.getNodeName().equals("eventHandlers")
				|| currentElement.getNodeName().equals("eventHandlers")
				|| currentElement.getNodeName().equals("eventHandlers")){		
			return  160;
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
		Node condition = null;
		Node activity = null;
		
		NodeList childList = ifElement.getChildNodes();
		Node child;
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
	private void recordSourceNodeOfLink(Element currentElement) {
		// TODO Auto-generated method stub
		
	}

	private void recordTargetNodeOfLink(Element currentElement) {
		// TODO Auto-generated method stub
		
	}
	private void buildLinkElements() {
		// TODO Auto-generated method stub
		
	}

}