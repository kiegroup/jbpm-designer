package org.oryxeditor.server;

import java.util.ArrayList;
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
public class BPELProcessRefiner {
	

   public Document rearrangeDocument (Document document){
	   
	   rearrangeNode (document);
	   
	   return document;
   }
   
   
   private void rearrangeNode (Node currentNode){
	      
	   // handle only the Nodes with type "Element" and "Document" (root element), 
	   // the other types e.g. "Attr","Comment" will be ignored 
	   if (!(currentNode instanceof Element || currentNode instanceof Document)) {
		   return;
	   };
	   
	   // recursive research, work on the child nodes at first.
	   NodeList childNodes = currentNode.getChildNodes();
	   Node child;
	   for (int i=0; i<childNodes.getLength(); i++){
		   child = childNodes.item(i);
		   if (child instanceof Element){
			   rearrangeNode (child);
		   }
	   };
	   
	   //after all of the child nodes are already handled, handle the current node
	   if (currentNode instanceof Element){
		   //the following elements could contain more than one child nodes,
		   //rearrange their child nodes.
		   if (currentNode.getNodeName().equals("process")
			   ||currentNode.getNodeName().equals("invoke")
			   ||currentNode.getNodeName().equals("scope")
			   ||currentNode.getNodeName().equals("assign")
			   ||currentNode.getNodeName().equals("eventHandlers")
			   ||currentNode.getNodeName().equals("faultHandlers")
			   ||currentNode.getNodeName().equals("compensationHandler")
			   ||currentNode.getNodeName().equals("terminationHandler")
			   ||currentNode.getNodeName().equals("if")
			   ||currentNode.getNodeName().equals("sequence")
			   ||currentNode.getNodeName().equals("pick") ){
			   
			   rearrangeChildNodesOfCurrentNode((Element)currentNode);
		   };
		   
		   // the order of child Nodes in "flow" is not based on the position of child nodes,
		   // but the order of the edges "link", so we handle it in a different way.
		   if (currentNode.getNodeName().equals("flow")){
			   
			   System.out.println("in flow element...");
			   
			   // find all child nodes which are the source nodes of a link but 
			   // not the target nodes of any other links, put them into a record
			   // list "childrenList", Then use a Breadth-First Search to find all
			   // other child nodes and arrange them with a correct order, in the
			   // end the result will be also in "childrenList" 
			   ArrayList<Element> childrenList = new ArrayList<Element>();
			   
			   // record all targetNode IDs
			   ArrayList<String> targetIDList = new ArrayList<String>();
			   NodeList childNodesOfFlow = currentNode.getChildNodes();
			   Node childOfFlow;
			   for (int i=0; i<childNodesOfFlow.getLength(); i++){
				   childOfFlow = childNodesOfFlow.item(i);
				   if (childOfFlow instanceof Element && childOfFlow.getNodeName().equals("links")){
					   Element links = (Element) childOfFlow;
					   NodeList childNodesOfLinks = links.getChildNodes();
					   Node link;
					   for (int k=0; k<childNodes.getLength(); k++){
						   link = childNodesOfLinks.item(k);
						   if (link instanceof Element && link.getNodeName().equals("link")){
							   String targetID = ((Element)link).getAttribute("targetID");
							   targetIDList.add(targetID); 
						   };
					   }
					   break;
				   }
			   };
			   
			   // filter child nodes of flow with targetIDList in order get all
			   // nodes which are just the source nodes of a link but not the 
			   // target nodes of any other links, also are the start nodes of 
			   // BFS
			   for (int i=0; i<childNodesOfFlow.getLength(); i++){
				   childOfFlow = childNodesOfFlow.item(i);
				   if (childOfFlow instanceof Element  
						   && !childOfFlow.getNodeName().equals("links")
						   && !isTargetOfOtherNode((Element)childOfFlow, targetIDList)){
					   
					   childrenList.add((Element)childOfFlow);
					   System.out.println("add Node : " +  childOfFlow.getNodeName() +
							   " in list...");
				   }
			   };
			   
			   // if no such node can be found, there are 2 possibilities
			   // 1: flow doesn't contain any child at all.
			   // 2: there is a node circle.
			   // for the second situation, we put the first element node of flow in the list
			   if (childrenList.size()==0){
				   for (int i=0; i<childNodesOfFlow.getLength(); i++){
					   childOfFlow = childNodesOfFlow.item(i);
					   if (childOfFlow instanceof Element
							   && !childOfFlow.getNodeName().equals("links")){
						   childrenList.add((Element)childOfFlow);
						   break;
					   }
				   };
			   }
			   
			   System.out.println("BFS starts...");
			   BFSForArrangingChildrenOfFlow (0, childrenList);
			   
			   // delete old children
			   childNodesOfFlow = currentNode.getChildNodes();
			   Node oldChild;
			   for (int i=0; i < childNodesOfFlow.getLength(); i++){
				   oldChild = childNodesOfFlow.item(i);
				   if (oldChild instanceof Element
						   && !oldChild.getNodeName().equals("links")){
					   currentNode.removeChild(oldChild);
				   }
			   }
			   
			   // add children again with the new order
			   Element newChild;
			   for (int i=0; i < childrenList.size(); i++){
				   newChild = childrenList.get(i);
				   currentNode.appendChild(newChild);
			   }
			   
		   };
		   
		   // remove the head "elseif" of the first child in if-block
		   if (currentNode.getNodeName().equals("if")){
			   // get the first child node with neme "elseif"
			   childNodes = currentNode.getChildNodes();
			   Node firstElseifChild = null;
			   boolean found = false;
			   for (int i=0; i<childNodes.getLength() && !found; i++){
				   child = childNodes.item(i);
				   if (child instanceof Element && child.getNodeName().equals("elseif")){
					   firstElseifChild = child;
					   found = true;
				   }
			   };
			   
			   // replace "elseif" with the sole two inner elements "condition" and "activity" 
			   if (found){
				   Node condition = null;
				   Node activity = null;
				   NodeList childrenOfFirstChild = firstElseifChild.getChildNodes();
				   for (int i=0; i < childrenOfFirstChild.getLength(); i++){
					   child = childrenOfFirstChild.item(i);
					   if (child instanceof Element){
						   if (child.getNodeName().equals("condition")){
							   condition = child;
						   } else {
							   activity = child;
						   }
					   };
				   };
				   
				   Node nextSibling = firstElseifChild.getNextSibling();
				   currentNode.removeChild(firstElseifChild);
				   
				   if (condition != null){
					   currentNode.insertBefore(condition, nextSibling);
				   };
				   
				   if (activity != null){
					   currentNode.insertBefore(activity, nextSibling);
				   }
			   }
		   }
	   }
	   
	   // after the arrangement of current node finished, delete the useless attribute 
	   // "bounds" "id" "outgoing" and "visited" from child nodes
	   childNodes = currentNode.getChildNodes();
	   Element childElement;
	   for (int i=0; i<childNodes.getLength(); i++){
		   child = childNodes.item(i);
		   if (child instanceof Element){
			   childElement = (Element) child;
			   childElement.removeAttribute("bounds");
			   //childElement.removeAttribute("id");
			   //childElement.removeAttribute("outgoing");
			   //childElement.removeAttribute("visited");
		   }
	   };
   }
   
   /***************************  rearrange child nodes  *****************************/
	
   private void rearrangeChildNodesOfCurrentNode(Element currentNode){
   
	   // get all the children with be attribute "bounds"
	   // record them in a array list "toArrangeChildren"
	   ArrayList<Element> toArrangeChildren = new ArrayList<Element>();
	   NodeList allChildren = currentNode.getChildNodes();
	   Node child;
	   for (int i=0; i<allChildren.getLength(); i++){
		   child = allChildren.item(i);
		   if (child instanceof Element){
			   Element childElement = (Element) child;
			   if (childElement.getAttribute("bounds") != ""){
				   toArrangeChildren.add((Element) child);
			   }
		   }
	   };
       
	   // with a quick-sort method rearrange the children of currentNode;
	   // copy the list first
	   ArrayList<Element> arrangedChildren = new ArrayList<Element>();
	   Iterator<Element> toArrangeChildrenIter = toArrangeChildren.iterator();
	   Element thisElement;
	   while (toArrangeChildrenIter.hasNext()){
		   thisElement = toArrangeChildrenIter.next();
		   arrangedChildren.add(thisElement);
	   };
	   quickSortForArrangingChildren(arrangedChildren, 0, arrangedChildren.size()-1);
	   
	   // delete old children
	   Element oldChild;
	   for (int i=0; i < toArrangeChildren.size(); i++){
		   oldChild = toArrangeChildren.get(i);
		   currentNode.removeChild(oldChild);
	   }
	   
	   // add children again with the new order
	   Element newChild;
	   for (int i=0; i < arrangedChildren.size(); i++){
		   newChild = arrangedChildren.get(i);
		   currentNode.appendChild(newChild);
	   }
  }
   
   private void quickSortForArrangingChildren
   		(ArrayList<Element> childrenList, int left, int right){
	   
	   if (left < right){
		   int i,j;
		   Element middle,elementTemp;
		   
		   i = left;
		   j = right; 
		   middle = childrenList.get((left + right)/2);
		   
		   while (i<=j) {
			   while (isBefore(childrenList.get(i), middle) && (i < right)) i++;
			   while (isBefore(middle, childrenList.get(j)) && (j > left)) j--;
			  
			   if (i<=j){
				   elementTemp = childrenList.get(i);
				   childrenList.set(i, childrenList.get(j));
				   childrenList.set(j, elementTemp); 
			   
				   i++;
				   j--;
			   };
		   };
			   
		   if (left < j){
			   quickSortForArrangingChildren(childrenList, left, j);
		   };
		   
		   if (i < right) {
			   quickSortForArrangingChildren(childrenList, i, right);
		   }
	   }
   }
   
   private boolean isBefore(Element e1, Element e2){
	   int valueOfE1 = getBoundsValueOf (e1);
	   int valueOfE2 = getBoundsValueOf (e2);
	   
	   return valueOfE1 < valueOfE2;
   }
   
   private int getBoundsValueOf (Element e){
	   String bounds = e.getAttribute("bounds");
	   
	   if (bounds == ""){
		   return 0;
	   }
	   
	   int indexOfFirstComma = bounds.indexOf(',');
	   int indexOfSecondComma = bounds.indexOf(',', indexOfFirstComma + 1);	   
	
	   int leftUpperX = Integer.parseInt(bounds.substring(0, indexOfFirstComma));
	   int leftUpperY = Integer.parseInt(bounds.substring(indexOfFirstComma + 1, indexOfSecondComma));
	  
	   return leftUpperX + leftUpperY;
   }
   
   
   
   /******************************* handle flow element*********************************/
	  
   private boolean isTargetOfOtherNode(Element e,
		   ArrayList<String> targetIDList) {
	   
	   String currentId = e.getAttribute("id");
	
	   if (currentId == null){
		   return false;
	   }
	
	   Iterator<String> listIter = targetIDList.iterator();
	   String targetId;
	   while (listIter.hasNext()){
		   targetId = listIter.next();
		   if (targetId.equals(currentId)){
			   return true;
		   }
	   }
	   
	   return false;
	}
   
   private void BFSForArrangingChildrenOfFlow (int index, 
		   ArrayList<Element> childrenList){
	   
	   if (index >= childrenList.size()){
		   return;
	   }
	   
	   Element sourceNode = childrenList.get(index);
	   
	   System.out.println("index :" + index);
	   System.out.println("node : " + sourceNode.getNodeName());
	   
	   markNodeAsVisited (sourceNode);
	   
	   ArrayList<Element> outgoingLinks = findAllOutgoingLinksOf(sourceNode);
	   
	   addStandardElements_Targets(sourceNode, outgoingLinks);
	
	   Element targetNode;
	   Iterator<Element> outgoingLinksIter = outgoingLinks.iterator();
	   Element link;
	   while (outgoingLinksIter.hasNext()){
		   link = outgoingLinksIter.next();
		   targetNode = findTargetNodeOfLink(link, (Element)sourceNode.getParentNode());
		   System.out.println("targetNode :" + targetNode);
		   if (targetNode != null){
			   addStandardElements_Sources(sourceNode, targetNode, link);
			   if (!isVisited(targetNode)) {
				   childrenList.add(targetNode);
			   }
		   }
	   };
	   
	   BFSForArrangingChildrenOfFlow (index + 1, childrenList);
   }
   
   private void  addStandardElements_Targets (Element sourceNode, 
		   ArrayList<Element> outgoingLinks){
	   
	   // find element <targets>
	   Element targets = null;
	   NodeList childNodes = sourceNode.getChildNodes();
	   Node child;
	   for (int i=0; i<childNodes.getLength(); i++){
		   child = childNodes.item(i);
		   if (child instanceof Element && child.getNodeName().equals("targets")){
			   targets = (Element)child;
		   }
	   };

	   // if no "targets" element exists, creat a new one.
	   if (targets == null){
		   targets = sourceNode.getOwnerDocument().createElement("targets");
		   sourceNode.appendChild(targets);
	   }
	   
	   // add all <target> element
	   Iterator<Element> outgoingLinksIter = outgoingLinks.iterator();
	   Element link;
	   Element target;
	   String linkName;
	   while (outgoingLinksIter.hasNext()){
		   // get link and linkName
		   link = outgoingLinksIter.next();
		   linkName = link.getAttribute("name");
		   
		   // create a new element <target> within element <targets> 
		   target = sourceNode.getOwnerDocument().createElement("target");
		   targets.appendChild(target);
		   
		   // add the attribute "linkName"
		   target.setAttribute("linkName", linkName);  
	   };
	   
   }
   
  private void addStandardElements_Sources(Element sourceNode, Element targetNode, Element link) {
	  // find element <sources>
	  Element sources = null;
	  NodeList childNodes = targetNode.getChildNodes();
	  Node child;
	  for (int i=0; i<childNodes.getLength(); i++){
		  child = childNodes.item(i);
		  if (child instanceof Element && child.getNodeName().equals("sources")){
			  sources = (Element)child;
		  }
	  };
	
	  // if no "sources" element exists, creat a new one.
	  if (sources == null){
		  sources = targetNode.getOwnerDocument().createElement("sources");
		  sourceNode.appendChild(sources);
	  }
	
	  // create a new element <source> within element <sources>
	  Element source = targetNode.getOwnerDocument().createElement("source");
	  sources.appendChild(source);
	  
	  // add attribute "linkName"
	  String linkName = link.getAttribute("name");
	  source.setAttribute("linkeName", linkName);
	  
	  // add element <transitionCondition>
	  Element transitionCondition = targetNode.getOwnerDocument().createElement("transitionCondition");
	  source.appendChild(transitionCondition);
	  Element joinCondition = getJoinConditionOfNode (sourceNode);
	  if (joinCondition != null){
		  String expressionLanguage = joinCondition.getAttribute("expressionLanguage");
		  String booleanExpression = joinCondition.getNodeValue();
		  transitionCondition.setAttribute("expressionLanguage", expressionLanguage);
		  transitionCondition.setNodeValue(booleanExpression);
	  }
  }


  private Element getJoinConditionOfNode(Element sourceNode) {
	  // find element <targets>
	  Element targets = null;
	  NodeList childNodes = sourceNode.getChildNodes();
	  Node child;
	  for (int i=0; i<childNodes.getLength(); i++){
		  child = childNodes.item(i);
		  if (child instanceof Element && child.getNodeName().equals("targets")){
			  targets = (Element)child;
		  }
	  };

	  // if no "targets" element exists, return null
	  if (targets == null){
		  return null;
	  }
	  
	  //find element <joinCondition> under element <targets>
	  childNodes = targets.getChildNodes();
	  for (int i=0; i<childNodes.getLength(); i++){
		  child = childNodes.item(i);
		  if (child instanceof Element && child.getNodeName().equals("joinCondition")){
			  return (Element)child;
		  }
	  };
	  
	  return null;
  }


  private void markNodeAsVisited (Element e){
	  e.setAttribute("visited", "true");
  }
  
  
  private boolean isVisited(Element e) {
	  String result = e.getAttribute("visited");
	  if (result.equals("true")){
		  return true;
	  } else {
		  return false;
	  }
  }


  private Element findTargetNodeOfLink(Element link, Element flow) {
	  System.out.println("findTargetNodeOfLink :" + link.getNodeName());
	  // get targetNode id
	  String targetId = link.getAttribute("targetID");

	  // if it doesn't exist, return null
	  if (targetId == null){
		  return null;
	  }
	  
	  // search node under flow element with id
	  NodeList childNodes = flow.getChildNodes();
	  Node child;
	  String nodeId;
	  for (int i=0; i<childNodes.getLength(); i++){
		  child = childNodes.item(i);
		  if (child instanceof Element
				  && !child.getNodeName().equals("links")){
			  nodeId = ((Element)child).getAttribute("id");
			  if (nodeId.equals(targetId)){
				  return (Element) child;
			  }
		  }
	  }
	  
	  return null;
  }

  
  private ArrayList<Element> findAllOutgoingLinksOf (Element e){
	  ArrayList<Element> linksList = new ArrayList<Element>() ;
	  
	  ArrayList<Element> setOfLinksUnderParentFlow = getSetOfLinksUnderParentFlow(e);
	  
	  NodeList childNodesOfE = e.getChildNodes();
	  Node childOfE;
	  Element bufferElemet;
	  Element outgoingLink;
	  String id;
	  
	  for (int i=0; i<childNodesOfE.getLength(); i++){
		  childOfE = childNodesOfE.item(i);
		  if (childOfE instanceof Element && childOfE.getNodeName().equals("outgoing")){
			  bufferElemet = (Element) childOfE;
			  id = bufferElemet.getNodeValue();
			  outgoingLink = getLinkWithID(id, setOfLinksUnderParentFlow);
			  if (outgoingLink != null){
				  linksList.add(outgoingLink);
			  }
		  }
	  }; 
	   
	  return linksList;
  }
  
  
  private ArrayList<Element> getSetOfLinksUnderParentFlow (Element e){
	  
	  ArrayList<Element> setOfLinks = new ArrayList<Element>();
	  
	  // find element <links>
	  Element flow = (Element)e.getParentNode();
	  Element links = null;
	  NodeList childNodesOfFlow = flow.getChildNodes();
	  Node childOfFlow;
	  for (int i=0; i<childNodesOfFlow.getLength(); i++){
		  childOfFlow = childNodesOfFlow.item(i);
		  if (childOfFlow instanceof Element && childOfFlow.getNodeName().equals("links")){
			   links = (Element)childOfFlow;
		  }
	  };
	  
	  System.out.println("links :" + links.getNodeName());
	  
	  // if no element <links> exists, return null
	  if (links == null){
		  return null;
	  }
	  
	  // get all elements <link> under element <links>
	  NodeList childNodesOfLinks = links.getChildNodes();
	  Node childOfLinks;
	  for (int i=0; i<childNodesOfLinks.getLength(); i++){
		  childOfLinks = childNodesOfLinks.item(i);
		  if (childOfLinks instanceof Element && childOfLinks.getNodeName().equals("link")){
			  System.out.println("add a link " + ((Element)childOfLinks).getAttribute("id"));
			  setOfLinks.add((Element)childOfLinks);
		  }
	  };
	  
	  return setOfLinks;
  }
  
  private Element getLinkWithID (String id, ArrayList<Element> setOfLinks){
	  Iterator<Element> setOfLinksIter = setOfLinks.iterator();
	  Element link;
	  String currentID;
	 
	  while (setOfLinksIter.hasNext()){
		  link = setOfLinksIter.next();
		  currentID = link.getAttribute("id");
		  if (currentID.equals(id)) {
			  System.out.println("find a link : " + link.getAttribute("id"));
			  return link;
		  }
	  };  
	  
	  return null;
  }
}