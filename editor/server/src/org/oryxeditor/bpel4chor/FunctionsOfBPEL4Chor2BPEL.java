package org.oryxeditor.bpel4chor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
 * these are the base functions(3.1 - 3.36), which was designed in the Studien Arbeit
 * of Peter Reimann(2008)
 */
public class FunctionsOfBPEL4Chor2BPEL {

	final static String EMPTY="";

	/*************************Name space***************************/
	// 3.1: record all name spaces of QName
	protected Set<String> namespaceSet = new HashSet<String>();

	// 3.2: record all name space prefixes of QName
	protected Set<String> namespacePrefixSet = new HashSet<String>();
	protected HashMap<String, String> ns2prefixMap = new HashMap<String, String>();
	protected String topologyNS;					// it will be used in conversion of PBD

	/**********************Method of Name Space******************************/
	
	/**
	 * to create the Sets: namespaceSet, namespaceprefixSet and Mapping: ns2prefixMap
	 * 
	 * @param {Node}   currentNode     The current node of the XML file
	 * @param {String} nodeName        The name of the Node
	 */
	protected void getNamespaceSet(Node currentNode, String nodeName){
		if(!(currentNode instanceof Element || currentNode instanceof Document)){
			return;
		};

		String str;
		String[] strSplit, prefixSplit;

		if(currentNode.getNodeName().equals(nodeName)){
			for(int i=0; i<currentNode.getAttributes().getLength(); i++){
				str = currentNode.getAttributes().item(i).toString();
				strSplit = str.split("=");
				if(strSplit[0].contains("xmlns") || (strSplit[0].equals("targetNamespace"))){
					if(strSplit[0].equals("targetNamespace")){
						ns2prefixMap.put(strSplit[0], strSplit[1].replaceAll("\"", ""));
						String valueOfTopologyNS = strSplit[1].replaceAll("\"", "");
						topologyNS = strSplit[1].replaceAll("\"", "");
						ns2prefixMap.put("topologyNS", valueOfTopologyNS);
						String targetNS = "targetNamespace";
						namespacePrefixSet.add(targetNS);
						namespaceSet.add(valueOfTopologyNS);
					}
					if(strSplit[0].contains("xmlns:")){
						prefixSplit = strSplit[0].split(":");
						namespacePrefixSet.add(prefixSplit[1]);
						namespaceSet.add(strSplit[1].replaceAll("\"", ""));
						ns2prefixMap.put(prefixSplit[1],strSplit[1].replaceAll("\"", ""));
					}
					if(strSplit[0].equals("xmlns")){
						namespaceSet.add(strSplit[1].replaceAll("\"", ""));
						ns2prefixMap.put(strSplit[0],strSplit[1].replaceAll("\"", ""));
					}
				}
			}
		}
		// recursive to search name space 
		NodeList childNodes = currentNode.getChildNodes();
		Node child;
		for(int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				getNamespaceSet(child, nodeName);
			}	
		}
	}

	/**
	 * function 3.3: assigning a name space to its name space prefix. prefixNS: NSPrefix -> NS
	 * 
	 * @param {String} prefix     The prefix of name space
	 * @return {String}           The name space according mapping
	 */
	protected String fprefixNS(String prefix){
		if(!ns2prefixMap.isEmpty() && ns2prefixMap.containsKey(prefix)){
			return ns2prefixMap.get(prefix);
		}
		return EMPTY;
	}

	/*************************ParticipantType***********************/
	// 3.4: participant types set
	protected Set<String> paTypeSet = new HashSet<String>();

	// 3.5: process set
	protected Set<String> processSet = new HashSet<String>();

	// for the function 3.6 fprocessPaType
	protected HashMap<String, String> paType2processMap = new HashMap<String, String>();


	/***********************Method of ParticipantType*************************/
	/**
	 * function 3.4: To create the participantTypeSet
	 * 
	 * @param {Element} currentElement     The current Element
	 * @return {Set}    paTypeSet          The participantType set
	 */
	protected Set<String> getPaTypeSet(Element currentElement){
		if(!(currentElement instanceof Node || currentElement instanceof Document)){
			return null;
		};

		if(currentElement.getNodeName().equals("participantType")){
			// analyze name space of participantType node
			String paTypeNameAttribute = currentElement.getAttribute("name");
			paTypeSet.add(paTypeNameAttribute);
		}

		// recursive to search name space 
		NodeList childNodes = currentElement.getChildNodes();
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				getPaTypeSet((Element)child);
			}	
		}
		return paTypeSet;
	}

	/**
	 * function 3.5: To create the processSet
	 * 
	 * @param {Element} currentElement     The current element
	 * @return {Set}    processSet         The process set
	 */
	protected Set<String> getProcessSet(Element currentElement){
		if(!(currentElement instanceof Node || currentElement instanceof Document)){
			return null;
		};

		if(currentElement.getNodeName().equals("participantType")){
			// analyze namespace of participantType node
			String pbd = currentElement.getAttribute("participantBehaviorDescription");
			processSet.add(pbd);
		}

		// recursive to search name space 
		NodeList childNodes = currentElement.getChildNodes();
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				getProcessSet((Element)child);
			}	
		}
		return processSet;
	}

	/**
	 * To create the paType2processMap for function 3.6
	 * 
	 * @param {Element} currentElement     The current element
	 * @return {HashMap} paType2processMap The mapping of paType and process
	 */
	protected HashMap<String, String> getPaType2ProcessMap(Element currentElement){
		if(!(currentElement instanceof Node || currentElement instanceof Document)) {
			return null;
		};

		if(currentElement.getNodeName().equals("participantType")){

			// analyze namespace of participantType node
			String paName = currentElement.getAttribute("name");
			String pbd = currentElement.getAttribute("participantBehaviorDescription");
			paType2processMap.put(paName, pbd);
		}

		// recursive to search name space 
		NodeList childNodes = currentElement.getChildNodes();
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				getPaType2ProcessMap((Element)child);
			}	
		}
		return paType2processMap;
	}

	/**
	 * function 3.6: processPaType: PaType -> Process
	 * 
	 * @param {String} paType     The participant type
	 * @return {String} process   The process 
	 */
	protected String fprocessPaType(String paType){
		if(!paType.isEmpty() && paType2processMap.containsKey(paType)){
			return paType2processMap.get(paType);
		}
		return EMPTY;
	}

	/**
	 * function 3.7: nsprefixProcess: Process -> NSPrefix
	 * 
	 * @param {String} process     The process
	 * @return {String} nsprefix   The name space prefix
	 */
	protected String fnsprefixProcess(String process){
		String[] nsprefixSplit;
		if(process.contains(":")){
			nsprefixSplit = process.split(":");
			return nsprefixSplit[0];
		}
		return EMPTY;
	}

	/*************************Participants**************************/
	// 3.8: participant set
	protected Set<String> paSet = new HashSet<String>();

	// 3.10: scopes set
	protected Set<String> scopeSet = new HashSet<String>();

	// for function 3.9 ftypePa
	protected HashMap<String, String> pa2paTypeMap = new HashMap<String, String>();

	// for function 3.11 fscopePa
	protected HashMap<String, Object> pa2scopeMap = new HashMap<String, Object>();
	protected HashMap<String, String> pa2foreachInScopeMap = new HashMap<String, String>();

	/********************Method of Participant*************************/
	/**
	 * function 3.8: To create the participant set
	 * 
	 * @param {Element} currentElement     The current element
	 * @return {Set}    paSet              The participant set
	 */
	protected Set<String> getPaSet(Element currentElement){
		if(!(currentElement instanceof Node || currentElement instanceof Document)){
			return null;
		}

		if(currentElement.getNodeName().equals("participant") || 
			currentElement.getNodeName().equals("participantSet")){
				String paNameAttribute = currentElement.getAttribute("name");
				paSet.add(paNameAttribute);
		}

		// make forEach2setMap for PBDConvertion (base for function 3.36)
		if(currentElement.getNodeName().equals("participantSet")){
			String paSetName = currentElement.getAttribute("name");
			if(currentElement.hasAttribute("scope")){
				String scContent = currentElement.getAttribute("scope");
				// it allow just one scope for scope attribute
				fsetForEach(scContent, EMPTY);
			}
		    if(currentElement.hasAttribute("forEach")){
				String scContent = currentElement.getAttribute("forEach");
				// it allow many forEachs for forEach attribute
				if(scContent.contains(" ")){
					String[] scArray = scContent.split(" ");
					for(int i=0;i<scArray.length;i++){
						fsetForEach(scArray[i].toString(), paSetName);
					}
				}
				else{
					fsetForEach(scContent, paSetName);
				}
			}
		}

		// recursive to search name space 
		NodeList childNodes = currentElement.getChildNodes();
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				getPaSet((Element)child);
			}	
		}
		return paSet;
	}

	/**
	 * To create pa2paTypeMap for 3.9
	 * 
	 * @param {Element} currentElement     The current element
	 */
	// get pa2paTypeMap for 3.9 
	protected void getPa2PaTypeMap(Element currentElement){
		if(!(currentElement instanceof Node || currentElement instanceof Document)){
			return;
		}

		if(currentElement.getNodeName().equals("participant")){
			try{
				if(!(currentElement.getAttribute("name") == "") &&
						!(currentElement.getAttribute("type") == "")){
					String paNameAttribute = currentElement.getAttribute("name");
					String paTypeAttribute = currentElement.getAttribute("type");
					pa2paTypeMap.put(paNameAttribute, paTypeAttribute);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		if(currentElement.getNodeName().equals("participantSet")){
			try{
				String paNameAttribute = currentElement.getAttribute("name");
				String paTypeAttribute = currentElement.getAttribute("type");
				pa2paTypeMap.put(paNameAttribute, paTypeAttribute);
				if(currentElement.hasChildNodes()){
					NodeList childNodes = currentElement.getChildNodes();
					Node child;
					for(int i = 0; i < childNodes.getLength(); i++){
						child = childNodes.item(i);
						if(child instanceof Element){
							String childPaNameAttribute = child.getAttributes().getNamedItem("name").getNodeValue();
							String childPaTypeAttribute = paTypeAttribute;
							pa2paTypeMap.put(childPaNameAttribute, childPaTypeAttribute);
						}
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		NodeList childNodes = currentElement.getChildNodes();
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				getPa2PaTypeMap((Element)child);
			}	
		}
	}

	/**
	 * function 3.9: typePa: Pa -> paType
	 * 
	 * @param {String} participant       The participant
	 * @return {String} participantType  The participantType
	 */
	protected String ftypePa (String participant){
		if(!paTypeSet.isEmpty()){
			Iterator<String> it = paTypeSet.iterator();
			while (it.hasNext()){
				String participantType = (String)it.next();
				if(pa2paTypeMap.get(participant).equals(participantType)){
					return participantType;
				}
			}
		}
		return EMPTY;
	}

	/**
	 * function 3.10: getScopeSet for <scope> and <forEach> attribute of participant 
	 * 
	 * @param {Element} currentElement     The current element
	 * @return {Set}    scopeSet           The scope set
	 */
	protected Set<String> getScopeSet(Element currentElement){
		if(!(currentElement instanceof Node || currentElement instanceof Document)){
			return null;
		}

		if(currentElement.getNodeName().equals("participant") || currentElement.getNodeName().equals("participantSet")){
			try{
				// it is perhaps there are many forEachs in forEach attribute
				if(!(currentElement.getAttribute("forEach") == "")){
					String forEachAttribute = currentElement.getAttribute("forEach");
					if(forEachAttribute.contains(" ")){
						String[] forEachArray = forEachAttribute.split(" ");
						for(int i=0;i<forEachArray.length;i++){
							scopeSet.add(forEachArray[i]);
						}
					}
					else{
						scopeSet.add(forEachAttribute);
					}
				}

				// it will be just one designed scope in scope attribute 
				if(!(currentElement.getAttribute("scope") == "")){
					String scopeAttribute = currentElement.getAttribute("scope");
					scopeSet.add(scopeAttribute);
				}
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}
		}

		NodeList childNodes = currentElement.getChildNodes();
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				getScopeSet((Element)child);
			}	
		}
		return scopeSet;
	}

	/**
	 * To create mapping{participant, scope||forEach} for function 3.11
	 * 
	 * @param {Element} currentElement     The current element
	 */
	protected void getPa2ScopeMap(Element currentElement){
		if(!((currentElement instanceof Node) || (currentElement instanceof Document))){
			return;
		}

		if(currentElement.getNodeName().equals("participant")){
			try{
				if((currentElement.getAttribute("name") != "") &&
						(currentElement.getAttribute("scope") != "")){
					String paNameAttribute = currentElement.getAttribute("name");
					String paScopeAttribute = currentElement.getAttribute("scope");
					pa2scopeMap.put(paNameAttribute, paScopeAttribute);
				}
				else if((currentElement.getAttribute("name") != "") &&
						(currentElement.getAttribute("forEach") != "")){
					String paNameAttribute = currentElement.getAttribute("name");
					String paForEachAttribute = currentElement.getAttribute("forEach");
					if(paForEachAttribute.contains(" ")){
						String[] paForEachArray = paForEachAttribute.split(" ");
						for(int i=0;i<paForEachArray.length;i++){
							//TODO:: to be refined with many forEachs in forEach attribute of a single participant of
							//       participantSet
							pa2foreachInScopeMap.put(paForEachArray[i], "<ForEach>");
							pa2scopeMap.put(paNameAttribute, pa2foreachInScopeMap);
							//pa2scopeMap.put(paNameAttribute, paForEachArray[i]);
							scopeSet.add(paForEachArray[i]);
						}
					}
					else{
						scopeSet.add(paForEachAttribute);
						pa2scopeMap.put(paNameAttribute, paForEachAttribute);
					}
				}
				else{
					String paNameAttribute = currentElement.getAttribute("name");
					pa2scopeMap.put(paNameAttribute, EMPTY);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(currentElement.getNodeName().equals("participantSet")){
			try{
				if((currentElement.getAttribute("name") != "") &&
						(currentElement.getAttribute("scope") != "")){
					String paNameAttribute = currentElement.getAttribute("name");
					pa2scopeMap.put(paNameAttribute, EMPTY);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	
		// recursive to search
		NodeList childNodes = currentElement.getChildNodes();
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				getPa2ScopeMap((Element)child);
			}	
		}
	}

	/**
	 * function 3.11: scopePa: Pa -> Scope U {EMPTY}
	 * 
	 * @param {String} participant     The participant
	 * @return {Object} EMPTY or ArrayList for <forEach> activity
	 */
	protected Object fscopePa(String participant){
		try{
			if(pa2scopeMap.containsKey(participant)){
				return pa2scopeMap.get(participant);
			}
			else
				return EMPTY;	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return EMPTY;
	}

	/*************************MessageLink***************************/
	protected Set<String> messageLinkSet = new HashSet<String>();

	protected Set<String> messageConstructsSet = new HashSet<String>();

	protected String ns = ""; // to save the name of PBD

	protected HashMap<String, Object> ml2mcMap = new HashMap<String, Object>();

	protected HashMap<String, Object> ml2paMap = new HashMap<String, Object>();

	//in fbindSenderToML defined and for grounding Analyze
	protected HashMap<String, String> ml2bindSenderToMap = new HashMap<String, String>(); 

	/***********************Method of MessageLink***********************/
	/**
	 * function 3.12: create message construct set
	 * 
	 * @param {Node} currentNode          The current node
	 * @return {Set} messageConstructsSet The message constructs set
	 */
	protected Set<String> getMessageConstructsSet (Node currentNode){
		NodeList childNodes = ((Element)currentNode).getElementsByTagName("messageLink");
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				String receiver = ((Element)child).getAttribute("receiver");
				String sender1 = "";
				if(((Element) child).hasAttribute("sender")){
					sender1 = ((Element)child).getAttribute("sender");
				}
				else {
					ArrayList<String> sendersList = null;
					String senders = ((Element)child).getAttribute("senders");
					String[] sendersSplit = senders.split(" ");
					for(int j=0; j<sendersSplit.length; j++){
						sendersList.add(sendersSplit[j]);
						sender1 = (String)sendersList.get(0);
					}
				}
				String receiveActivity = ((Element)child).getAttribute("receiveActivity");
				String sendActivity = ((Element)child).getAttribute("sendActivity");
				String receiverns = fnsprefixProcess(fprocessPaType(ftypePa(receiver)));
				//TODO: sendersList[], just sender1 is done.
				String senderns = fnsprefixProcess(fprocessPaType(ftypePa(sender1)));
				String mc2 = buildQName(receiverns, receiveActivity);
				String mc1 = buildQName(senderns, sendActivity);
				messageConstructsSet.add(mc2);
				messageConstructsSet.add(mc1);
			}
		}
		return messageConstructsSet;
	}

	/**
	 * function: To build QName for function 3.12 
	 * 
	 * @param {String} prefix     The prefix
	 * @param {String} NCName     The NCName
	 * @return {String} QName     The QName
	 */
	protected static String buildQName(String prefix, String NCName){
		return prefix + ":" + NCName;
	}

	/**
	 * function 3.13: To create message link set
	 * 
	 * @param {Element} currentElement     The current element
	 * @return {Set}    messageLinkSet     The message link set
	 */
	protected Set<String> getMessageLinkSet(Element currentElement){
		if(!(currentElement instanceof Node || currentElement instanceof Document)){
			return null;
		}

		if(currentElement.getNodeName().equals("messageLink")){
			try{
				String mlString = currentElement.getAttribute("name");
				messageLinkSet.add(mlString);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		// recursive to search name space 
		NodeList childNodes = currentElement.getChildNodes();
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				getMessageLinkSet((Element)child);
			}	
		}
		return messageLinkSet;
	}

	/**
	 * function 3.14: constructsML: ML -> MC x MC
	 * for each messageLink. to specified a send and a receive activity.
	 * 
	 * @param {String} mlName               The message link
	 * @return {ArrayList} mcSenderReceiver The ArrayList [senderNS:senderActivity, receiverNS:receiverActivity]
	 */
	protected ArrayList<String> fconstructsML(String ml){
		ArrayList<String> mcSenderReceiver = new ArrayList<String>();
		if(!ml2mcMap.isEmpty()){
			mcSenderReceiver = (ArrayList<String>)ml2mcMap.get(ml);
		}
		return mcSenderReceiver;
	}

	/**
	 * function 3.15: parefsML: ML -> 2^Pa x Pa
	 * 
	 * @param {String} ml                             The message link
	 * @return {ArrayList} outputSenderReceiverPaList The ArrayList [[senderArrayListPa], receiverPa]
	 */
	protected ArrayList<Object> fparefsML(String ml){
		ArrayList<Object> outputSenderReceiverPaList = new ArrayList<Object>();
		if(!ml2paMap.isEmpty()){
			outputSenderReceiverPaList = (ArrayList<Object>)ml2paMap.get(ml);
		}
		return outputSenderReceiverPaList;
	}

	/**
	 * function 3.16: bindSenderToML: ML -> Pa U {EMPTY}
	 * 
	 * @param {Element} currentElement                                                   The current element
	 * @param {String}  messageLink                                                      The message link
	 * @return {String} participant or EMPTY(if not found bindSenderTo attribute in ml)  The participant
	 */
	protected String fbindSenderToML(Element currentElement, String messageLink){
		getMl2BindSenderToMap(currentElement);
		String participant = EMPTY;
		if(ml2bindSenderToMap.containsValue(messageLink)){
			String paValue = ml2bindSenderToMap.get(messageLink);
			if(paSet.contains(paValue)){
				return participant = paValue;
			}
		}		
		return participant;
	}

	/**
	 * To create mapping[messageLink, bindSenderTo]
	 * will be used in fbindSenderToML and Grounding analyze
	 * 
	 * @param {Element} currentElement      The current element
	 */
	protected void getMl2BindSenderToMap(Element currentElement){
		if(!(currentElement instanceof Node || currentElement instanceof Document)){
			return;
		}

		NodeList childNodes = currentElement.getElementsByTagName("messageLink");
		Element child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = (Element)childNodes.item(i);
			if(child.hasAttribute("bindSenderTo")){
				String key = child.getAttribute("name");
				String value = child.getAttribute("bindSenderTo");
				ml2bindSenderToMap.put(key, value);
			}
			else{
				String key = child.getAttribute("name");
				String value = EMPTY;
				ml2bindSenderToMap.put(key, value);
			}
		}
	}

	/*********************Methods of Grounding analyze****************************/
	// 3.17: to save the portType of messageLink of grounding
	protected Set<String> ptSet = new HashSet<String>();

	// 3.19: to save the operation of messageLink of grounding
	protected Set<String> oSet = new HashSet<String>();

	// 3.20: fportTypeMC()
	protected HashMap<String, String> ml2ptMap = new HashMap<String, String>();

	// 3.21: foperationMC()
	protected HashMap<String, String> ml2opMap = new HashMap<String, String>();

	// 3.22: partnerLink Set
	protected Set<String> plSet = new HashSet<String>();

	// 3.23: messageConstruct --> partnerLink Mapping
	protected HashMap<String, PartnerLink> mc2plMap = new HashMap<String, PartnerLink>();

	// 3.24: scope --> partnerLinkSet Mapping
	protected HashMap<String, Set<PartnerLink>> sc2plMap = new HashMap<String, Set<PartnerLink>>();

	// 3.25: partnerLinkType Set
	protected Set<String> plTypeSet = new HashSet<String>();

	// 3.26: partnerLink --> partnerLinkType
	protected HashMap<String, String> pl2plTypeMap = new HashMap<String, String>();

	// 3.27: communication Map for partnerLink
	protected HashMap<Object, Object> commMap = new HashMap<Object, Object>();

	// 3.28: communication --> partnerLink X partnerLink
	protected HashMap<Comm, Object> comm2plsMap = new HashMap<Comm, Object>();

	// 3.29: communication --> partnerLinkType
	protected HashMap<Comm, String> comm2pltMap = new HashMap<Comm, String>();

	// 3.30: partnerLink --> myRole 
	protected HashMap<String, String> pl2myRoleMap = new HashMap<String, String>();

	// 3.31: partnerLink --> partnerRole
	protected HashMap<String, String> pl2partnerRoleMap = new HashMap<String, String>();

	// relation COMM ((A,c),(b,d))
	protected Set<Object> commSet = new HashSet<Object>();

	/**
	 * function 3.17: To create the port type set
	 * will be used in messageLink of grounding
	 * 
	 * @param {Element} currentElement     The current element
	 * @return {Set}    ptSet              The port type set
	 */
	protected Set<String> getPtSet(Element currentElement){
		Set<String> portTypeSet = new HashSet<String>();
		NodeList childNodes = currentElement.getElementsByTagName("messageLink");
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(((Element)child).hasAttribute("portType")){
				portTypeSet.add(((Element)child).getAttribute("portType"));
			}
		}
		return portTypeSet;
	}

	/**
	 * function 3.18: nsprefixPT: PT -> NSPrefix
	 * 
	 * @param {String} portType     The port type
	 * @return {String} nsprefix    The name space prefix
	 */
	protected String fnsprefixPT (String portType){
		String[] nsprefixSplit;
		if(portType.contains(":")){
			nsprefixSplit = portType.split(":");
			return nsprefixSplit[0];
		}
		return EMPTY;
	}

	/**
	 * function 3.19: To create operation set
	 * will be used in messageLink of grounding
	 * 
	 * @param {Element} currentElement     The current element
	 * @return {Set}    operationSet       The operation set
	 */
	protected Set<String> getOSet(Element currentElement){
		if(!(currentElement instanceof Node || currentElement instanceof Document)){
			return null;
		}

		Set<String> operationSet = new HashSet<String>();
		NodeList childNodes = currentElement.getElementsByTagName("messageLink");
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(((((Element)child).hasAttribute("portType")) && 
					((Element)child).hasAttribute("operation"))){
				String NCName = ((Element)child).getAttribute("portType").split(":")[0];
				String Value  = ((Element)child).getAttribute("operation");
				operationSet.add(NCName + ":" + Value);
			}
		}
		return operationSet;
	}

	/**
	 * function for 3.20: To create a mapping[messageConstruct, messageLink] 
	 * we assume here, that we have already messageLinkSet, ml2mcMap, constrctsML from topologyAnalyze
	 *  
	 * @return {HashMap} mc2mlMap     The mapping of messageConstruct to messageLink
	 */
	protected HashMap<String, String> getMc2mlMap(){
		HashMap<String, String> mc2mlMap = new HashMap<String, String>();
		ArrayList<String> valueList = new ArrayList<String>();
		Iterator<String> it = messageLinkSet.iterator();
		while (it.hasNext()){
			String ml = (String)it.next();
			valueList = (ArrayList<String>)ml2mcMap.get(ml);
			String mc1Str = valueList.get(0).toString().split(":")[1];
			String mc2Str = valueList.get(1).toString().split(":")[1];
			mc2mlMap.put(mc1Str, ml);
			mc2mlMap.put(mc2Str, ml);
		}
		return mc2mlMap;
	}

	/**
	 * function 3.20: portTypeMC: MC -> PT
	 * 
	 * @param {Element} currentElement     The current element
	 * @param {String}  mc                 The message construct
	 * @return {String} portType           The port type
	 */
	protected String fportTypeMC(Element currentElement, String mc){
		String portType = EMPTY;
		NodeList childNodes = currentElement.getElementsByTagName("messageLink");
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				String ml = ((Element)child).getAttribute("name");
				String pt = ((Element)child).getAttribute("portType");
				// create messageLinkSet of grounding
				//messageLinkSet.add(ml);
				// create ml2ptMap for 3.20 function 
				ml2ptMap.put(ml, pt);
			}
		}
		HashMap<String, String> mc2mlMap = getMc2mlMap();
		if(ml2ptMap.containsKey(mc2mlMap.get((mc.split(":"))[1]))){
			return ml2ptMap.get(mc2mlMap.get((mc.split(":"))[1]));
		}
		return portType;
	}

	/**
	 * function 3.20: portTypeMC: MC -> PT
	 * 
	 * @param {String}  mc          The message construct
	 * @return {String} portType    The port type
	 */
	protected String fportTypeMC(String mc){
		HashMap<String, String> mc2mlMap = getMc2mlMap();
		if(ml2ptMap.containsKey(mc2mlMap.get((mc.split(":"))[1]))){
			return ml2ptMap.get(mc2mlMap.get((mc.split(":"))[1]));
		}
		return EMPTY;
	}

	/**
	 * function 3.21: operationMC: MC -> O
	 * 
	 * @param {Element} currentElement     The current Element
	 * @param {String}  mc                 The message construct
	 * @return {String} operation          The operation
	 */
	protected String foperationMC (Element currentElement, String mc){
		String operation = EMPTY;
		NodeList childNodes = currentElement.getElementsByTagName("messageLink");
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				String ml = ((Element)child).getAttribute("name");
				String op = ((Element)child).getAttribute("operation");
				if(!op.contains(":")){
					op = fnsprefixPT(((Element)child).getAttribute("portType")) + ":" + op;
				}
				// create messageLinkSet of grounding
				//messageLinkSet.add(ml);
				// create ml2opMap for 3.21 function 
				ml2opMap.put(ml, op);
			}
		}
		HashMap<String, String> mc2mlMap = getMc2mlMap();
		if(ml2opMap.containsKey(mc2mlMap.get((mc.split(":"))[1]))){
			return ml2opMap.get(mc2mlMap.get((mc.split(":"))[1]));
		}
		return operation;
	}

	/**
	 * function 3.21: operationMC: MC -> O
	 * 
	 * @param {String}  mc         The message construct
	 * @return {String} operation  The operation
	 */
	protected String foperationMC(String mc){
		HashMap<String, String> mc2mlMap = getMc2mlMap();
		if(ml2opMap.containsKey(mc2mlMap.get((mc.split(":"))[1]))){
			return ml2opMap.get(mc2mlMap.get((mc.split(":"))[1]));
		}
		return EMPTY;
	}

	/**
	 * function 3.23: partnerLinkMC: MC -> PL
	 * create a mapping mc2plMap(mc, pl)
	 * 
	 * @param {String}      mc        The message construct
	 * @param {PartnerLink} pl        The partner link
	 */
	protected void fpartnerLinkMC(String mc, PartnerLink pl){
		mc2plMap.put(mc, pl);
	}

	/**
	 * function 3.23: partnerLinkMC: MC -> PL
	 * 
	 * @param {String}       mc       The message construct
	 * @return {PartnerLink} pl       The partner link
	 */
	protected PartnerLink fpartnerLinkMC(String mc){
		if(mc2plMap.containsKey(mc)){
			return mc2plMap.get(mc);
		}
		return null;
	}

	/**
	 * function 3.24: partnerLinksScope: (Scope U Process) -> 2^PL
	 * create a mapping sc2plMap [sc, partnerLinkSet]
	 * 
	 * @param {String} sc             The element of scopeSet and processSet
	 * @param {Set}    partnerLinkSet The partner link set
	 */
	protected void fpartnerLinksScope(String sc, Set<PartnerLink> partnerLinkSet){
		sc2plMap.put(sc, partnerLinkSet);
	}

	/**
	 * function 3.24: partnerLinksScope: (Scope U Process) -> 2^PL
	 * 
	 * @param {String} sc             The element of scopeSet and processSet
	 * @return {Set}   partnerLinkSet The partner link set
	 */
	protected Set<PartnerLink> fpartnerLinksScope(String sc){
		if(sc2plMap.containsKey(sc)){
			return sc2plMap.get(sc);
		}
		return null;
	}

	/**
	 * function 3.26: typePL: PL -> PLType
	 * create a mapping pl2plTypeMap[pl.getName(), plType]
	 * 
	 * @param {PartnerLink} pl       The partner link
	 * @param {String}      plType   The partner link type
	 */
	protected void ftypePL(PartnerLink pl, String plType){
		pl2plTypeMap.put(pl.getName(), plType);
		pl.setPartnerRole(plType);
	}

	/**
	 * function 3.26: typePL: PL -> PLType
	 * 
	 * @param {PartnerLink} pl       The partner link
	 * @return {String}     plType   The partner link type
	 */
	protected String ftypePL(PartnerLink pl){
		if(pl2plTypeMap.containsKey(pl.getName())){
			return pl2plTypeMap.get(pl.getName());
		}
		return EMPTY;
	}

	/**
	 * function 3.27: commMap
	 * Model is [(A, A_pt), (b, b_pt)]
	 * 
	 * @return {HashMap} coMap 
	 */
	protected HashMap<Object, Object> getCommMap(){
		HashMap<Object, Object> coMap = new HashMap<Object, Object>();
		// TODO: 3.27
		return coMap;
	}

	/**
	 * function 3.28: partnerLinksComm: Comm -> PL x PL
	 * create a mapping comm2plsMap[comm, plsPair]
	 * 
	 * @param {Comm}      comm     The communication((A,c),(b,d))
	 * @param {ArrayList} plsPair  The pair of partner link(pl1, pl2)
	 */
	protected void fpartnerLinksComm(Comm comm, ArrayList<Object> plsPair){
		comm2plsMap.put(comm, plsPair);
	}

	/**
	 * function 3.28: partnerLinksComm: Comm -> PL x PL
	 * 
	 * @param {Comm}       comm     The communication((A,c),(b,d))
	 * @return {ArrayList} plsPair  The pair of partner link(pl1, pl2)
	 */
	protected ArrayList<Object> fpartnerLinksComm(Comm comm){
		ArrayList<Object> plsPair = null;
		if(comm2plsMap.containsKey(comm)){
			return (ArrayList<Object>) comm2plsMap.get(comm);
		}
		return plsPair;
	}

	/**
	 * function 3.29: pltComm: Comm -> PLType
	 * create a mapping comm2pltMap[comm, plType]
	 * 
	 * @param {Comm}   comm            The communication((A,c),(b,d))
	 * @param {String} partnerLinkType The partner link type
	 */
	protected void fpltComm(Comm comm, String plType){
		comm2pltMap.put(comm, plType);
	}

	/**
	 * function 3.29: pltComm: Comm -> PLType
	 * 
	 * @param {Comm}    comm            The communication((A,c),(b,d))
	 * @return {String} partnerLinkType The partner link type
	 */
	protected String fpltComm(Comm comm){
		if(comm2pltMap.containsKey(comm)){
			return comm2pltMap.get(comm);
		}
		return EMPTY;
	}
	
	/**
	 * function pltCommReverse: 
	 * according the input partnerLinkType of comm2pltMap to output the communication
	 * 
	 * @param {String} plt       The partner link type
	 * @return {Comm}  comm      The communication
	 */
	protected Comm fpltCommReverse(String plt){
		if(comm2pltMap.containsValue(plt)){
			Set<Comm> ks = comm2pltMap.keySet();
			Iterator<Comm> it = ks.iterator();
			while(it.hasNext()){
				Comm comm = (Comm)it.next();
				if(fpltComm(comm).equals(plt)){
					return comm;
				}
			}
		}
		return null;
	}

	/**
	 * function 3.30: myRolePL: PL -> Pa U {EMPTY}
	 * create a mapping pl2myRoleMap[pl.getName(), myRoleValue]
	 * 
	 * @param {PartnerLink} pl           The partner link
	 * @param {String}      myRoleValue  The value of myRole in partner link
	 */
	protected void fmyRolePL(PartnerLink pl, String myRoleValue){
		pl2myRoleMap.put(pl.getName(), myRoleValue);
		pl.setMyRole(myRoleValue);
	}

	/**
	 * function 3.30: myRolePL: PL -> Pa U {EMPTY}
	 * 
	 * @param {PartnerLink} pl           The partner link
	 * @return {String}     myRoleValue  The value of myRole in partner link
	 */
	protected String fmyRolePL(PartnerLink pl){
		if(pl2myRoleMap.containsKey(pl.getName())){
			return pl2myRoleMap.get(pl.getName());
		}
		return EMPTY;
	}

	/**
	 * function 3.31: partnerRolePL: PL -> Pa U {EMPTY}
	 * create a mapping pl2partnerRoleMap[pl.getName(), partnerRoleValue]
	 * 
	 * @param {PartnerLink} pl                The partner link
	 * @param {String}      partnerRoleValue  The value of partnerRole in partner link
	 */
	protected void fpartnerRolePL(PartnerLink pl, String partnerRoleValue){
		pl2partnerRoleMap.put(pl.getName(), partnerRoleValue);
		pl.setPartnerRole(partnerRoleValue);
	}

	/**
	 * function 3.31: partnerRolePL: PL -> Pa U {EMPTY}
	 * 
	 * @param {PartnerLink} pl                The partner link
	 * @return {String}     partnerRoleValue  The value of partnerRole in partner link
	 */
	protected String fpartnerRolePL(PartnerLink pl){
		if(pl2partnerRoleMap.containsKey(pl.getName())){
			return pl2partnerRoleMap.get(pl.getName());
		}
		return EMPTY;
	}

	/***********************Correlation Properties*********************/
	// 3.32: set of NCNames of correlation properties 
	protected Set<String> corrPropNameSet = new HashSet<String>();

	// 3.33: set of WSDL properties
	protected Set<String> propertySet = new HashSet<String>();

	// used by 3.34
	protected HashMap<String, String> corrPropName2propertyMap = new HashMap<String, String>();

	// used by 3.35
	protected HashMap<String, String> property2nsprefixOfPropMap = new HashMap<String, String>();

	// used by 3.36
	protected HashMap<String, String> forEach2setMap = new HashMap<String, String>();

	/**
	 * function 3.34: the function assigning a property to each property name. 
	 *                propertyCorrPropName: CorrPropName -> Property
	 * create a mapping corrPropName2propertyMap[propNameInput, propertyInput]
	 * 
	 * @param {String} propNameInput      The property name
	 * @param {String} propertyInput      The WSDLproperty value
	 */
	protected void fpropertyCorrPropName(String propNameInput, String propertyInput){
		corrPropName2propertyMap.put(propNameInput, propertyInput);
	}

	/**
	 * function 3.34: assigning a property to each property name. 
	 *                propertyCorrPropName: CorrPropName -> Property
	 * 
	 * @param  {String} propNameInput      The property name
	 * @return {String} property           The WSDLproperty value
	 */
	protected String fpropertyCorrPropName(String propNameInput){
		if(corrPropName2propertyMap.containsKey(propNameInput)){
			return corrPropName2propertyMap.get(propNameInput);
		}
		return EMPTY;
	}

	/**
	 * function 3.35: assigning a name space prefix to each WSDL property. 
	 *                nsprefixProperty: property -> nsprefix
	 * create a mapping property2nsprefixOfPropMap[propertyInput, nsprefixInput]
	 * 
	 * @param {String} propertyInput        The WSDLproperty value in grounding
	 * @param {String} nsprefixInput        The name space prefix of this value
	 */
	protected void fnsprefixProperty(String propertyInput, String nsprefixInput){
		property2nsprefixOfPropMap.put(propertyInput, nsprefixInput);
	}

	/**
	 * function 3.35: assigning a name space prefix to each WSDL property. 
	 *                nsprefixProperty: property -> nsprefix
	 * 
	 * @param  {String} propertyInput        The WSDLproperty value in grounding
	 * @return {String} nsprefix             The name space prefix of this value
	 */
	protected String fnsprefixProperty(String propertyInput){
		if(property2nsprefixOfPropMap.containsKey(propertyInput)){
			return property2nsprefixOfPropMap.get(propertyInput);
		}
		return EMPTY;
	}

	/**
	 * function 3.36: assigning a set of participant references to each <forEach> activity
	 * Attention: the following functions of 3.36 will be just used within the tag <participantSet> of XML files.
	 * create a mapping forEach2setMap[sc, paSetName]
	 * 
	 * @param {String} sc         The QName of <scope> or <forEach> activity
	 * @param {String} paSetName  The name of <participantSet>
	 */
	protected void fsetForEach(String sc, String paSetName){
			forEach2setMap.put(sc, paSetName);
	}

	/**
	 * function 3.36: assigning a set of participant references to each <forEach> activity
	 * Attention: the following functions of 3.36 will be just used within the tag <participantSet> of XML files.
	 * 
	 * @param  {String} sc         The QName of <scope> or <forEach> activity
	 * @return {String} paSetName  The name of <participantSet>
	 */
	protected String fsetForEach(String sc){
		if(forEach2setMap.containsKey(sc)){
			return forEach2setMap.get(sc);
		}
		return EMPTY;
	}
}
