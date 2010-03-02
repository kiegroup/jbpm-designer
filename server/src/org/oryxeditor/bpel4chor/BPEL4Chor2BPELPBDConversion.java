package org.oryxeditor.bpel4chor;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Copyright (c) 2009-2010 
 * 
 * Changhua Li
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


/**
 * !!!!!!Attention!!!!!!
 * Now this files works isolated from the other files, which outside of this directory.
 * But it should be added into oryx as a plugin in the further.
 * 
 * It will be used for the Transformation of the BPEL4Chor to BPEL.
 * 
 * It was designed for the Diplom Arbeit of Changhua Li(student of uni. stuttgart), 
 * It is the procedure of conversion for an PBD , which was designed in the Studien Arbeit
 * of Peter Reimann(2008)
 */
public class BPEL4Chor2BPELPBDConversion extends FunctionsOfBPEL4Chor2BPEL {
	
	protected Document currentDocument;
	protected static String process_nsprefix;
	protected static Set<String> paSetList = new HashSet<String>();
	// attention: global name space prefix set here is namespacePrefixSet !!
	
	/**
	 * Algorithm 3.5 and Algorithm 3.17 Conversion of one PBD into BPEL
	 * 
	 * @param {Element} currentElement     The current element
	 */
	public void convertPBD(Element currentElement){
															// the input process points on the <process> activity of current PBD
		//String process_nsprefix;							// the name space prefix of the target name space of the current PBD
		Set<String> nsprefixSet = this.namespacePrefixSet;	// a list of name space prefixes referencing to the name
															// spaces of the WSDL definitions of port types used in
															// this process
		Set<String> paSetList = this.paSetList;				// a list of sets of participant references over which the 
															// set-based <forEach> activities in this process iterate
															// they are used as names of variables containing an array of
															// endpoint references
		//System.out.println("scope set is: " + scopeSet);
		//System.out.println("pa2scopeMap is: " + pa2scopeMap);
		//System.out.println("process set is: " + processSet);
		//System.out.println("partnerLink set is: " + plSet);
		//System.out.println("sc2plMap is: " + sc2plMap);
		//System.out.println("messageConstructsSet is: " + messageConstructsSet);
		//System.out.println("messageLinkSet is: " + messageLinkSet);
		//System.out.println("ml2ptMap is: " + ml2ptMap);
		//System.out.println("ml2mcMap is: " + ml2mcMap);
		System.out.println("nsprefixSet is: " + nsprefixSet);
		System.out.println("ns2prefixMap is: " + ns2prefixMap);
		
		process_nsprefix = fprefixNSofPBD(currentElement);
		System.out.println("process_nsprefix of PBD is: " + fprefixNSofPBD(currentElement));
		System.out.println("name of currentElement is: " + currentElement.getNodeName());

		// add the declaration of the name space of the partner link type definitions
		currentElement.setAttribute("xmlns:plt", topologyNS + "/partnerLinkTypes");
		System.out.println("xmlns:plt=" + currentElement.getAttribute("xmlns:plt"));
	
		// start depth-first search
		executeDepthFirstSearch(currentElement);							// algorithm 3.7

		// add partner link declarations to the process
		String localName = currentElement.getAttribute("name");				// localName is value of "name" of the PBD
		System.out.println("#########" + localName);
		declarePartnerLinks(currentElement, localName);						// algorithm 3.6
		
		// add the declarations of the name spaces of the port type definitions
		declareNameSpaces(currentElement, nsprefixSet);						// algorithm 3.8
		
		// declare the variables containing an array of endpoint references
		declareSrefVariables(currentElement, paSetList);					// algorithm 3.18
	}
	
	/**
	 * Algorithm 3.6 declarepartnerLinks
	 * 
	 * @param {Element} currentElement     current element (process or scope)
	 * @param {String}  localName          value of name attribute
	 */
	private void declarePartnerLinks(Element currentElement, String localName){
		String sc;											// an element of (scopeSet U processSet), it is a QName
		Set<PartnerLink> partnerLinkSet;					// plSet for declarePartnerLinks is ready
		PartnerLink pl = null;								// a single partner link declaration
		Element partnerLinks, partnerLink;					// single BPEL constructs
		String role;										// a participant reference, it is a participant
		String s;											// NCName
		
		sc = buildQName(process_nsprefix, localName);		// the global name of the current scope or process and the corresponding 
															// element of (scopeSet U processSet)
		if (scopeSet.contains(sc) || processSet.contains(sc)){
			// sc is a process or a single participant reference is limited to the scope sc
			// thus the function fpartnerLinksScope can be used on sc
			// determine the set of partner link declarations that need to be declared
			partnerLinkSet = (Set<PartnerLink>)fpartnerLinksScope(sc);
			if(partnerLinkSet != null){
				// There are partner links to be declared
				//System.out.println(partnerLinkSet.iterator().next().getName());
				partnerLinks = currentDocument.createElement("partnerLinks");
				currentElement.appendChild(partnerLinks);					// adding a <partnerLinks> declaration
				//System.out.println("partnerLinks is: " + partnerLinks.getTagName());
				Iterator<PartnerLink> it = partnerLinkSet.iterator();
				while(it.hasNext()){
					pl = (PartnerLink)it.next();
					// create a new partner link declaration for pl
					partnerLink = currentDocument.createElement("partnerLink");
					partnerLink.setAttribute("name", pl.getName());
					s = ftypePL(pl);
					partnerLink.setAttribute("partnerLinkType", "plt:" + s);	// "plt" is the name space prefix of the name space of
																				// the partner link type declarations
					role = fmyRolePL(pl);										// the myRole of pl
					if (!role.isEmpty()){
						partnerLink.setAttribute("myRole", role);
					}
					role = fpartnerRolePL(pl);									// the partnerRole of pl
					if (!role.isEmpty()){
						partnerLink.setAttribute("partnerRole", role);
					}
					partnerLinks.appendChild(partnerLink);						// add the new partner link declaration to the 
																				// <partnerLinks> declaration
				}
			}
		}
	}

	/**
	 * Algorithm 3.7 executeDepth-firstSearch
	 * 
	 * @param {Element} currentConstruct     The current BPEL construct
	 */
	private void executeDepthFirstSearch(Element currentConstruct){
		// the input currentConstruct points on the tag of the current BPEL construct
		NodeList constructList;													// a list of BPEL constructs
		Node construct;															// a single BPEL construct
		constructList = currentConstruct.getChildNodes();
		for(int i=0; i<constructList.getLength(); i++){
			construct = constructList.item(i);
			if (construct instanceof Element){
				System.out.println("current construct is: " + ((Element)construct).getTagName());
				modifyConstruct((Element)construct);
			}
		}
	}

	/**
	 * Algorithm 3.8 declareNameSpaces
	 * 
	 * @param {Element} construct     The tag of the current BPEL construct
	 * @param {Set}     nsprefixSet   includes the name space prefixes referencing to the name spaces 
	 *                                which need to be declared within construct
	 */
	protected void declareNameSpaces(Element construct, Set<String> nsprefixSet){
		// the input construct points on the tag of the current BPEL construct, and nsprefixSet includes the name space prefixes
		// referencing to the name spaces which need to be declared within construct
		String nsprefix = EMPTY;												// a single name space prefix
		// add each name space declaration to the construct
		Iterator<String> it = namespacePrefixSet.iterator();
		while(it.hasNext()){
			nsprefix = (String)it.next();
			if (!nsprefix.equals("targetNamespace")){
				construct.setAttribute("xmlns:" + nsprefix, ns2prefixMap.get(nsprefix));
			}
		}
	}
	
	/**
	 * Algorithm 3.9 and 3.14 (refined)modifyConstruct
	 * 
	 * @param {Element} construct     The current BPEL construct
	 */
	private void modifyConstruct(Element construct){
		// the input construct points on the tag of the current BPEL construct
		String constructName = construct.getTagName();							// the name of the current BPEL construct
		Element fEScope;														// a single BPEL construct
		
		if (constructName.equals("invoke") || constructName.equals("onMessage") || constructName.equals("onEvent")){
			// construct is an <invoke> activity or an <onMessage> or <onEvent> construct
			modifyMessageConstruct(construct, constructName);					// algorithm 3.10
			executeDepthFirstSearch((Element)construct);						// continue depth-first search
		}
		else if (constructName.equals("receive") || constructName.equals("reply")){
			// construct is a <receive> or <reply> activity
			modifyMessageConstruct(construct, constructName);					// algorithm 3.10
		}
		else if (constructName.equals("scope") && construct.hasAttribute("wsu:id")){
			// construct is a <scope> activity having a wsu:id attribute assigned
			executeDepthFirstSearch((Element)construct);						// continue depth-first search
			declarePartnerLinks(construct, construct.getAttribute("wsu:id"));	// algorithm 3.10
		}
		else if (constructName.equals("forEach")){
			// construct is a <forEach> activity
			NodeList nl = construct.getElementsByTagName("scope");
			for (int i=0; i<nl.getLength(); i++){
				fEScope = (Element)nl.item(i);									// fEScope points on the <scope> activity nested
				System.out.println("i= " + i);									// in the <forEach> activity
				// continue depth-first search on the scope
				modifyConstruct(fEScope);										// recursion
				if (construct.hasAttribute("wsu:id")){
					String id = construct.getAttribute("wsu:id");				// the value of the wsu:id attribute is stored in id since it is
																				// used twice
					// modify the set-based <forEach> activity
					modifyForEach(construct, fEScope, id);						// algorithm 3.15
					
					// add partner link declarations to the scope
					declarePartnerLinks(fEScope, construct.getAttribute("wsu:id"));
				}
			}
			// replace the wsu:id attribute to name attribute 
			if(construct.hasAttribute("wsu:id")){
				String nameOfForEach = construct.getAttribute("wsu:id");
				construct.removeAttribute("wsu:id");
				construct.setAttribute("name", nameOfForEach);
			}
		}
		else if (constructName.equals("correlationSets")){
			// construct is a <correlationSets> construct
			// it includes one or more <correlationSet> constructs (and nothing else)
			Element corrSet;													// a single BPEL construct
			NodeList corrList = construct.getElementsByTagName("correlationSet");// corrList becomes the list of <correlationSet> consturcts nested
																				// in the current <correlationSets> construct
			// modify each of these <correlationSet> constructs
			for (int i=0; i<corrList.getLength(); i++){
				corrSet = (Element)corrList.item(i);
				modifyCorrelationSet(corrSet);									// algorithm 3.13
			}
		}
		else {
			// construct may be any BPEL construct, e.g. a structured activity
			// continue depth-first search
			executeDepthFirstSearch(construct);
		}
	}
	
	/**
	 * Algorithm 3.10 modifyMessageConstruct
	 * 
	 * @param {Element} construct     The current BPEL construct
	 * @param {String}  constructName The local name of this construct
	 */
	private void modifyMessageConstruct(Element construct, String constructName){
		// the input construct points on the tag of the current message construct, and constructName is the local name of it
		String mc = EMPTY;														// the current element of MC(messageConstructsSet)
		String constructID = EMPTY;												// NCName
		PartnerLink pl = null;													// a single partner link declaration
		String pt = EMPTY;														// a single port type
		String op = EMPTY;														// a single operation
		String pt_nsprefix = EMPTY;												// the name space prefix of the port type pt
		
		// change the wus:id attribute to a name attribute if possible
		if (construct.hasAttribute("wsu:id")){
			constructID = construct.getAttribute("wsu:id");
		}
		if (!constructName.equals("onMessage") && !constructName.equals("onEvent")){
			// <onMessage> and <onEvent> constructs are not allowed to have a name attribute assigned
			construct.removeAttribute("wsu:id");
			construct.setAttribute("name", constructID);
		}
		mc = buildQName(process_nsprefix, constructID);							// mc becomes the global name of the current message
																				// construct and the corresponding element of MC
		// add a partnerLink attribute to the message construct
		pl = fpartnerLinkMC(mc);												// pl becomes the partner link declaration used by 
																				// the current message construct
		if (pl != null){
			construct.setAttribute("partnerLink", pl.getName());
			//System.out.println("mc mapped to pl: " + pl.getName());
		}
		
		// add a portType attribute to the message construct
		pt = fportTypeMC(mc);
		if(pt != null){															// when there is no mapping between mc an pt then null
			construct.setAttribute("portType", pt);
			
			// add the name space prefix of pt to the global list nsprefixList if it has not been added before
			pt_nsprefix = fnsprefixPT(pt);
			if (!namespacePrefixSet.contains(pt_nsprefix) && !pt_nsprefix.equals(EMPTY)){
				namespacePrefixSet.add(pt);
			}
		}
		
		// add an operation attribute to the message construct
		op = foperationMC(mc);
		if(op != null){															// when there is no mapping between mc an op then null
			construct.setAttribute("operation", fremoveNSPrefix(op));			// the operation attribute needs to be associated with
																				// the local name of the operation
		}
	}

	/**
	 * Algorithm 3.13 modifyCorrelationSet
	 * 
	 * @param {Element} correlationSet     The tag of the current <correlationSet> construct
	 */
	private void modifyCorrelationSet(Element correlationSet){
		// the input correlationSet points on the tag of the current <correlationSet> construct
		Set<String> propNameSet = new HashSet<String>();						// a list of property names
		String propName;														// a single property name
		Set<String> propertySet = new HashSet<String>();						// a list of WSDL properties
																				// initially the empty list
		String property;														// a single WSDL property
		String nsprefix;														// a single name space prefix
		
		// get the list of property names of the current correlation set
		propNameSet = getAttributeValueAsList(correlationSet, "properties");
		if (!propNameSet.isEmpty()){
			// use the function fpropertyCorrPropName to get corresponding list of WSDL properties
			Iterator<String> it = propNameSet.iterator();
			while(it.hasNext()){
				propName = (String)it.next();
				property = fpropertyCorrPropName(propName);
				propertySet.add(property);
				// add the name space prefix of the current WSDL property to the global list nsprefixList of name space
				// prefixes if it has not been added before
				// the corresponding name space declaration is added to the <process> activity after having finished the
				// depth-first search
				nsprefix = fnsprefixProperty(property);
				if (!namespacePrefixSet.contains(nsprefix)){
					namespacePrefixSet.add(nsprefix);
				}
			}
		}
		// change the property names to the corresponding references to WSDL properties
		addAttributeList(correlationSet, "properties", propertySet);
	}
	
	/**
	 * Algorithm 3.15 modifyForEach
	 * 
	 * @param {Element} forEach     The tag <forEach>
	 * @param {Element} fEScope     a BPEL construct
	 * @param {String}  id          NCName
	 */
	private void modifyForEach(Element forEach, Element fEScope, String id){
		// the input forEach points on the tag of the current <forEach> activity, the input fEScope on the tag of the
		// <scope> activity nested in the <forEach> activity, and the input id is its wsu:id
		String fe;																// name of <Scope> inherits of QName
		String set;																// name of <Participant> inherits of NCName
		Element startCounter = currentDocument.createElement("startCounterValue");	// the <startCounterValue>
		Element finalCounter = currentDocument.createElement("finalCounterValue");  // the <finalCounterValue>
		Element completionCondition = getChildElement(forEach, "completionCondition");// the optional <completionCondition>
																					// construct of the current <forEach>
		fe = buildQName(process_nsprefix, id);
		System.out.println("firstStep: "+fe);
		// the global name of the current <forEach> activity and the corresponding element of scopeSet
		if (scopeSet.contains(fe)){
			System.out.println("i am in!");
			// a set of participant references is associated with fe by a forEach attribute, thus it is a set-based
			// <forEach> activity.
			// add the counterName attribute
			forEach.setAttribute("counterName", "i_" + id);
			// add the contents to the <startCounterValue> and the <finalCounterValue>
			startCounter.setTextContent("0");
			set = fsetForEach(fe);
			finalCounter.setTextContent("count($" + set + "/)-1");
			// fsetForEach(fe) is the name of the set of participant references over which the current <forEach> activity
			// iterates
			// Add this name to the global list paSet if it has not been added before
			if(!paSetList.contains(set)){ 							
				paSetList.add(set);
			}
			// enclose the <startCounterValue> and the <finalCounterValue> constructs before the <completionCondition>
			// construct and the <scope> activity of the <forEach> activity
			forEach.removeChild(completionCondition);
			forEach.removeChild(fEScope);
			forEach.appendChild(startCounter);
			forEach.appendChild(finalCounter);
			forEach.appendChild(completionCondition);
			forEach.appendChild(fEScope);
			// add the <assign> activities to copy the endpoint references on the partner links
			fe = "seller:innerscope";									// this Line is just for test it.
			Set<PartnerLink> plSetForEach = fpartnerLinksScope(fe);
			System.out.println("fe is: " + fe);
			System.out.println("scopeSet is: " + scopeSet);
			System.out.println("sc2plMap is: " + sc2plMap);
			System.out.println("plSetForEach is: " + plSetForEach);
			if(plSetForEach != null && plSetForEach.size() == 1){
				addAssignsToForEach(fEScope, set, plSetForEach.iterator().next(), id);// algorithm 3.16
			}
			//addAssignsToForEach(fEScope, set, null, id);				// this Line is just for test it.
		}
	}

	/**
	 * Algorithm 3.16 addAssignsToForEach
	 * 
	 * @param {Element}     fEScope     The tag <scope> nested in the current <forEach> activity
	 * @param {String}      set         It is the set participant references over which the <forEach> activity iterates
	 * @param {PartnerLink} pl          It is partner link declaration between the owner and the iterator of the <forEach>
	 * @param {String}      id          It is its wsu:id
	 */
	private void addAssignsToForEach(Element fEScope, String set, PartnerLink pl, String id){
		Element nestedActivity = null;
		NodeList nl = fEScope.getChildNodes();
		Node childNode;
		for(int i=0; i<nl.getLength(); i++){
			childNode = nl.item(i);
			if(childNode instanceof Element){
				nestedActivity = (Element)childNode;								// last child of the <scope> activity
																					// this is the nested activity
			}
		}
		
		Element sequence = currentDocument.createElement("sequence");				// the new <sequence> activity
		Element assign   = currentDocument.createElement("assign");					// the new <assign> activity
		Element copy	 = currentDocument.createElement("copy");					// the new <copy> activity
		Element from	 = currentDocument.createElement("from");					// the new <from> activity
		Element query	 = currentDocument.createElement("query");					// the new <query> activity
		Element to		 = currentDocument.createElement("to");						// the new <to> activity
		
		fEScope.removeChild(nestedActivity);										// remove the nested activity
		from.setAttribute("variable", set);											// specify the <copy> construct
		query.setTextContent("[$i_" + id + "]");
		to.setAttribute("partnerLink", pl.getName());
		
		// arrange the <copy> construct and add it to the <assign> activity
		from.appendChild(query);
		copy.appendChild(from);
		copy.appendChild(to);
		assign.appendChild(copy);
		
		sequence.appendChild(assign);							// add the <assign> activity to the new <sequence> activity
		sequence.appendChild(nestedActivity);					// add the nested activity to the new <sequence> activity
		fEScope.appendChild(sequence);							// add the <sequence> activity to the <scope> activity
	}

	/**
	 * Algorithm 3.18 declareSrefVariables
	 * 
	 * @param {Element} construct     current BPEL construct
	 * @param {Set}     paSetList     list of sets of participant reference
	 */
	private void declareSrefVariables(Element construct, Set<String> paSetList){
		// the input construct points on the tag of the current BPEl construct, and PaSetList is the list of sets of
		// participant references for which a variable containing the corresponding array of endpoint references needs
		// to be declared.
		Element variables, variable;							// single BPEL constructs
		String paSetElement;									// a participant references
		
		if(paSetList != null){									// there are variables to be declared
			// add the name space declaration for the data type service-refs
			String srefsNamespace = "http://www.bpel4chor.org/service-references";
			construct.setAttribute("xmlns:srefs", srefsNamespace);
			// add a <variables> declaration
			//System.out.println(getChildElement(construct, "variables"));
			if((getChildElement(construct, "variables")) == null){
				variables = currentDocument.createElement("variables");
				construct.appendChild(variables);
			}
			// variables becomes the <variables> declaration
			variables = getChildElement(construct, "variables");
			// declare all necessary variables
			System.out.println("paSet is: " + paSet);
			Iterator<String> it = paSet.iterator();
			while(it.hasNext()){
				paSetElement = it.next();
				if(paSetList.contains(paSetElement)){
					// variable becomes a new <variable> declaration
					variable = currentDocument.createElement("variable");
					// add a name and a type attribute to the <variable> declaration
					variable.setAttribute("name", paSetElement);
					variable.setAttribute("type", "srefs:service-refs");
					// add the <variable> declaration to the <variables> declaration
					variables.appendChild(variable);
				}
			}
			System.out.println("paSetList is: " + paSetList);
		}
	}
	
	/**
	 * function: fprefixNSofPBD
	 * 
	 * @param  {Element} currentElement     
	 * @return {String}  result
	 */
	private String fprefixNSofPBD(Element currentElement){
		if (!(currentElement instanceof Node || currentElement instanceof Document)) {
			return null;
		}
		
		String result = EMPTY;
		if (currentElement.getNodeName().equals("process") && currentElement.hasAttribute("targetNamespace")){
			String tns = currentElement.getAttribute("targetNamespace");
			if (ns2prefixMap.containsValue(tns)){
				Iterator<String> it = ns2prefixMap.keySet().iterator();
				while(it.hasNext()){
					String key = it.next().toString();
					String value = ns2prefixMap.get(key);
					if (value.equals(tns)){
						return result = key; 
					}
				}
			}
			else
			{
				result = "process with topology not matched!!";
			}
		}
		return result;
	}
	
	/**
	 * fremoveNSPrefix function: this function returns the second NCName of the	QName
	 * 
	 * @param {String} name     It is a name of QName
	 * @return{String} output   a NCName, name without ":"
	 */
	private String fremoveNSPrefix(String name){
		if(name.contains(":")){
			return name.split(":")[1].toString();
		}
		return EMPTY;
	}

	/**
	 * getAttributeValueAsList function: this function returns the value of the attribute 
	 *                                   having the name name as list.
	 * 
	 * @param {Element} currentElement     current Element
	 * @param {String}  attributeName      name of attribute
	 * @return {Set}    valueSet		   valueSet according the specified attribute
	 */
	private Set<String> getAttributeValueAsList(Element currentElement, String attributeName){
		Set<String> valueSet = new HashSet<String>();
		if (!currentElement.hasAttribute(attributeName)){
			return valueSet;
		}
		
		String values = currentElement.getAttribute(attributeName);
		if (values.contains(" ")){
			String[] valuesList = values.split(" ");
			for (int i=0; i<valuesList.length; i++){
				valueSet.add(valuesList[i]);
			}
		}
		else
		{
			valueSet.add(values);
		}
		
		return valueSet;
	}

	/**
	 * addAttributeList function: this function adds an attribute having the name name, 
	 *                            as value the attribute gets the list.
	 * 
	 * @param {Element} currentElement     current Element
	 * @param {String}  attributeName      name of attribute
	 * @param {Set}     valueList		   the set of value
	 */
	private void addAttributeList(Element currentElement, String attributeName, Set<String> valueList){
		Object[] valuesArray = valueList.toArray();
		if (!valueList.isEmpty()){
			String values = valuesArray[0].toString();
			for (int i=1; i<valuesArray.length; i++){
				if (!valuesArray[i].toString().equals(EMPTY)){
					values = values + " " + valuesArray[i].toString();
				}
			}
			currentElement.setAttribute(attributeName, values);
		}
	}

	/**
	 * getChildElement function: this function get the specified name of the childElement of currentElement.
	 * 
	 * @param {Element} currentElement     
	 * @param {String}  childElement       
	 * @return {Element}returnElement      
	 */
	private Element getChildElement(Element currentElement, String childElement){
		Element returnElement = null;
		if (currentElement.hasChildNodes()){
			NodeList nl = currentElement.getChildNodes();
			Node child;
			for(int i=0; i<nl.getLength(); i++){
				child = nl.item(i);
				if (child instanceof Element && child.getNodeName().equals(childElement)){
					return returnElement = (Element)child;
				}
			}
		}
		return returnElement;
	}
	
	/**************************main*******************************/
	public static void main(String argv[]) throws ParserConfigurationException, 
										SAXException, IOException, TransformerFactoryConfigurationError, TransformerException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		if(docBuilder.isNamespaceAware()){
			Document docGround = docBuilder.parse("/home/eysler/work/DiplomArbeit/oryx-editor/editor/server/src/org/oryxeditor/bpel4chor/testFiles/groundingSA.bpel");
			Document docTopo = docBuilder.parse("/home/eysler/work/DiplomArbeit/oryx-editor/editor/server/src/org/oryxeditor/bpel4chor/testFiles/topologySA.xml");
			Document docPBD = docBuilder.parse("/home/eysler/work/DiplomArbeit/oryx-editor/editor/server/src/org/oryxeditor/bpel4chor/testFiles/processSA.bpel");
			
			BPEL4Chor2BPELTopologyAnalyze topoAnaly = new BPEL4Chor2BPELTopologyAnalyze();
			BPEL4Chor2BPELGroundingAnalyze grouAnaly = new BPEL4Chor2BPELGroundingAnalyze();
			BPEL4Chor2BPELPBDConversion pbdCon = new BPEL4Chor2BPELPBDConversion();

			//topology analyze
			topoAnaly.nsAnalyze(docTopo);
			topoAnaly.paTypeAnalyze(docTopo);
			topoAnaly.paAnalyze(docTopo);
			topoAnaly.mlAnalyze(docTopo);
			topoAnaly.getMl2BindSenderToMap(((Element)docTopo.getFirstChild()));
			
			grouAnaly.namespacePrefixSet = topoAnaly.namespacePrefixSet;    // will be used in grounding nsAnalyze
			grouAnaly.namespaceSet = topoAnaly.namespaceSet;				// will be used in grounding nsAnalyze
			grouAnaly.ns2prefixMap = topoAnaly.ns2prefixMap;				// will be used in grounding nsAnalyze
			grouAnaly.messageConstructsSet = topoAnaly.messageConstructsSet;
			grouAnaly.messageLinkSet = topoAnaly.messageLinkSet;
			grouAnaly.ml2mcMap = topoAnaly.ml2mcMap;
			grouAnaly.ml2paMap = topoAnaly.ml2paMap; 						// will be used in fparefsML() and in Alg. 3.4
			grouAnaly.ml2bindSenderToMap = topoAnaly.ml2bindSenderToMap; 	// will be used in mlAnalyze
			grouAnaly.pa2scopeMap = topoAnaly.pa2scopeMap; 					// will be used in Alg. 3.4 createPartnerLinkDeclarations
			grouAnaly.paTypeSet = topoAnaly.paTypeSet;                      // will be used in Alg. 3.4 createPartnerLinkDeclarations
			grouAnaly.pa2paTypeMap = topoAnaly.pa2paTypeMap;              	// will be used in Alg. 3.4 createPartnerLinkDeclarations
			grouAnaly.paType2processMap = topoAnaly.paType2processMap;      // will be used in Alg. 3.4 createPartnerLinkDeclarations
			
			//grounding analyze
			grouAnaly.nsAnalyze(docGround);
			grouAnaly.mlAnalyze(docGround);
			grouAnaly.propertyAnalyze(docGround);
			
			pbdCon.scopeSet = topoAnaly.scopeSet;							// will be used in Conversion of PBD
			pbdCon.processSet = topoAnaly.processSet;						// will be used in Conversion of PBD
			pbdCon.topologyNS = topoAnaly.topologyNS;						// will be used in Conversion of PBD
			pbdCon.forEach2setMap = topoAnaly.forEach2setMap;				// will be used in Conversion of PBD
			pbdCon.paSet = topoAnaly.paSet;									// will be used in Conversion of PBD
			pbdCon.pa2scopeMap = topoAnaly.pa2scopeMap; 					// will be used in Conversion of PBD
			pbdCon.ns2prefixMap = grouAnaly.ns2prefixMap;					// will be used in Conversion of PBD
			pbdCon.namespacePrefixSet = grouAnaly.namespacePrefixSet;		// will be used in Conversion of PBD
			pbdCon.plSet = grouAnaly.plSet;									// will be used in Conversion of PBD
			pbdCon.sc2plMap = grouAnaly.sc2plMap;							// will be used in Conversion of PBD
			pbdCon.pl2plTypeMap = grouAnaly.pl2plTypeMap;					// will be used in Conversion of PBD
			pbdCon.pl2myRoleMap = grouAnaly.pl2myRoleMap;					// will be used in Conversion of PBD
			pbdCon.pl2partnerRoleMap = grouAnaly.pl2partnerRoleMap;			// will be used in Conversion of PBD
			pbdCon.messageConstructsSet = grouAnaly.messageConstructsSet;	// will be used in Conversion of PBD
			pbdCon.mc2plMap = grouAnaly.mc2plMap;							// will be used in Conversion of PBD
			pbdCon.ml2mcMap = grouAnaly.ml2mcMap;							// will be used in Conversion of PBD
			pbdCon.messageLinkSet = grouAnaly.messageLinkSet;				// will be used in Conversion of PBD
			pbdCon.ml2ptMap = grouAnaly.ml2ptMap;							// will be used in Conversion of PBD
			pbdCon.ml2opMap = grouAnaly.ml2opMap;							// will be used in Conversion of PBD
			pbdCon.corrPropName2propertyMap = grouAnaly.corrPropName2propertyMap;  // will be used in Conversion of PBD
			pbdCon.property2nsprefixOfPropMap = grouAnaly.property2nsprefixOfPropMap; // will be used in Conversion of PBD
			
			//PBD conversion
			pbdCon.currentDocument = docPBD;
			pbdCon.convertPBD((Element)docPBD.getFirstChild());

			/**************************output of the converted PBD******************************/
			Source sourceBPEL = new DOMSource(docPBD);
			File bpelFile = new File("/home/eysler/work/DiplomArbeit/oryx-editor/editor/server/src/org/oryxeditor/bpel4chor/testFiles/PBDConvertion.bpel");
			Result resultBPEL = new StreamResult(bpelFile);
			 
			// Write the converted docPBD to the file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(sourceBPEL, resultBPEL);
		}
	}
}
