
package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * Copyright (c) 2008 
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
public class BPELExporter extends HttpServlet {

	private static final long serialVersionUID = 316274845723034029L;
	
//	private static Configuration config = null;
	
    /**
     * The POST request.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
    	
    	// RDF2BPEL XSLT source
    	final String xsltFilename = System.getProperty("catalina.home") + "/webapps/oryx/xslt/RDF2BPEL.xslt";
    	final File xsltFile = new File(xsltFilename);
    	final Source xsltSource = new StreamSource(xsltFile);	
    	
    	// Transformer Factory
    	final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    	// Get the rdf source
    	final Source rdfSource;
    	String rdfString = req.getParameter("data");
    	InputStream rdf = new ByteArrayInputStream(rdfString.getBytes());
    	rdfSource = new StreamSource(rdf);
  
    	// Get the result string
    	String resultString = null;
    	try {
    		Transformer transformer = transformerFactory.newTransformer(xsltSource);
    		StringWriter writer = new StringWriter();
    		transformer.transform(rdfSource, new StreamResult(writer));
    		resultString = writer.toString();
    	} catch (Exception e){
    		handleException(res, e); 
    		return;
    	}

    	if (resultString != null){
    		try {
    			resultString = rearrange (res, resultString);
    			printResponse (res, resultString);
    		    return;
    		} catch (Exception e){
    		    handleException(res, e); 
    		}
    	}
    }
    
   private String rearrange (HttpServletResponse res, String oldString){
	   
	   StringWriter out = new StringWriter();
	   try {
			// transform string to document
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream oldResultInputStream = new ByteArrayInputStream(oldString.getBytes());
			Document oldDocument = builder.parse(oldResultInputStream);
			
			// rearrange document
			Document newDocument = rearrangeDocument (oldDocument);
			
			// transform document to string
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(newDocument);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
			out.flush();
	 
		} catch (Exception e){
		    handleException(res, e); 
		}
		
		return out.toString();

   }
    
   private Document rearrangeDocument (Document document){
	   
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
			   // TODO implement
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
	   
	   // after the arrangement of current node finished, delete the attribute "bounds" of 
	   // child nodes
	   childNodes = currentNode.getChildNodes();
	   Element childElement;
	   for (int i=0; i<childNodes.getLength(); i++){
		   child = childNodes.item(i);
		   if (child instanceof Element){
			   childElement = (Element) child;
			   childElement.removeAttribute("bounds");
		   }
	   };
   }
   
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
			   if (!childElement.getAttribute("bounds").isEmpty()){
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
	   
	   if (bounds.isEmpty()){
		   return 0;
	   }
	   
	   int indexOfFirstComma = bounds.indexOf(',');
	   int indexOfSecondComma = bounds.indexOf(',', indexOfFirstComma + 1);	   
	
	   int leftUpperX = Integer.parseInt(bounds.substring(0, indexOfFirstComma));
	   int leftUpperY = Integer.parseInt(bounds.substring(indexOfFirstComma + 1, indexOfSecondComma));
	  
	   return leftUpperX + leftUpperY;
   }
   
   private void printResponse(HttpServletResponse res, String text){
    	if (res != null){
 
        	// Get the PrintWriter
        	res.setContentType("text/plain");
        	
        	PrintWriter out = null;
        	try {
        	    out = res.getWriter();
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
        	
    		out.print(text);
    	}
    }
    
    
    private void printError(HttpServletResponse res, String err){
    	if (res != null){
 
        	// Get the PrintWriter
        	res.setContentType("text/html");
        	
        	PrintWriter out = null;
        	try {
        	    out = res.getWriter();
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
        	
    		out.print("{success:false, content:'"+err+"'}");
    	}
    }
    
	private void handleException(HttpServletResponse res, Exception e) {
		e.printStackTrace();
		printError(res, e.getLocalizedMessage());
	}
    
}
