package org.oryxeditor.bpel4chor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;

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
 * 
 * It will be used for the Transformation of the BPEL4Chor to BPEL.
 * 
 * It was designed for the Diplom Arbeit of Changhua Li(student of uni. stuttgart), 
 * It is the analyze of Topology, which was designed in the Studien-Arbeit
 * of Peter Reimann(2008)
 */

public class BPEL4Chor2BPELTopologyAnalyze {
	private Logger log = Logger.getLogger("org.oryxeditor.bpel4chor.BPEL4Chor2BPELTopologyAnalyze");
	
	final static String EMPTY = "";
	public Set<String> namespaceSet = new HashSet<String>();

	// 3.2: record all name space prefixes of QName
	public Set<String> namespacePrefixSet = new HashSet<String>();
	public HashMap<String, String> ns2prefixMap = new HashMap<String, String>();
	public String topologyNS;					// it will be used in conversion of PBD
	public HashMap<QName, String> forEach2setMap = new HashMap<QName, String>();

	/*************************ParticipantType variables***********************/
	public Set<String> paTypeSet = new HashSet<String>();

	// 3.5: process set
	public Set<QName> processSet = new HashSet<QName>();
	
	// for the function 3.6 fprocessPaType
	public HashMap<String, QName> paType2processMap = new HashMap<String, QName>();

	/*************************Participants**************************/
	public Set<String> paSet = new HashSet<String>();

	// 3.10: scopes set
	public Set<QName> scopeSet = new HashSet<QName>();

	// for function 3.9 ftypePa
	public HashMap<String, String> pa2paTypeMap = new HashMap<String, String>();

	// for function 3.11 fscopePa
	public HashMap<String, Object> pa2scopeMap = new HashMap<String, Object>();
	private HashMap<QName, String> pa2foreachInScopeMap = new HashMap<QName, String>();


	/*************************MessageLink variables***************************/
	public Set<String> messageLinkSet = new HashSet<String>();

	public Set<QName> messageConstructsSet = new HashSet<QName>();

	public HashMap<String, Object> ml2mcMap = new HashMap<String, Object>();

	public HashMap<String, Object> ml2paMap = new HashMap<String, Object>();

	//in fbindSenderToML defined and for grounding Analyze
	public HashMap<String, String> ml2bindSenderToMap = new HashMap<String, String>(); 

	/**
	 * To analyze the name spaces of <topology> of topology.xml with the node name "topology"
	 * 
	 * @param {Element} elCurrent      The element <topology> of topology.xml 
	 */
	public void nsAnalyze (Element elTopology){
		
		getNamespaceSet(elTopology, "topology");
	}
	
	/**
	 * To analyze the part <participantTypes> of topology.xml
	 * 
	 * @param {Element} elCurrent     The element <topology> of topology.xml 
	 */
	public void paTypeAnalyze (Element elTopology){
		
		paTypeSet = getPaTypeSet(elTopology);
		processSet = getProcessSet(elTopology);
		paType2processMap = getPaType2ProcessMap(elTopology);
	}
	
	/**
	 * To analyze the part <participants> of topology.xml
	 * 
	 * @param {Element} elTopology     The element <topology> of topology.xml
	 */
	public void paAnalyze (Element elTopology){
				
		paSet = getPaSet(elTopology);
		paTypeSet = getPaTypeSet(elTopology);
		getPa2PaTypeMap(elTopology);
		scopeSet = getScopeSet(elTopology);
		getPa2ScopeMap(elTopology);
	}
	
	/**
	 * To analyze the part <messageLinks> of topology.xml
	 * 
	 * @param {Element} elTopology     The element <topology> of topology.xml
	 */
	public void mlAnalyze(Element elTopology){
		
		messageLinkSet = new HashSet<String>();		  // ML Set
		messageConstructsSet = new HashSet<QName>();  // MC Set
		String ml;                                    // ml is an Element of messageLinkSet
		String receiver, sender1 = EMPTY; 			  // they are the Element of PaSet
		ArrayList<String> sendersList = new ArrayList<String>();  // to store Elements of Attribute "senders"
		String receiveActivity, sendActivity;         // NCName
		String receiverNSPrefix, senderNSPrefix;      // Prefix of name space of "receiver" and "sender"
		QName mc1, mc2;							  	  // QName, mc1 will be the "sendActivity" and mc2 the
													  // "receiveActivity" of the message link
		
		// during the analyze of messageLink of topology to get the messageLinkSet, 
		// messageConstructsSet, ml2mcMap, ml2paMap 
		NodeList childNodes = elTopology.getElementsByTagName("messageLink");
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if (child instanceof Element){
				ml = ((Element)child).getAttribute("name");
				messageLinkSet.add(ml);
				receiver = ((Element)child).getAttribute("receiver");
				if (receiver == null) {
					log.severe("No receiver given");
				}
				if (((Element) child).hasAttribute("sender")){
					sender1 = ((Element)child).getAttribute("sender");
				}
				else {
					sendersList.clear();
					if (((Element) child).hasAttribute("senders")){
						String senders = ((Element)child).getAttribute("senders");
						String[] sendersSplit = senders.split(" ");
						for(int j=0; j<sendersSplit.length; j++){
							sendersList.add(sendersSplit[j]);
							sender1 = (String)sendersList.get(0);
						}
					}
				}
				receiveActivity = ((Element)child).getAttribute("receiveActivity");
				if (receiveActivity == null) {
					log.severe("No receiveActivity given");
				}
				sendActivity = ((Element)child).getAttribute("sendActivity");
				if (sendActivity == null) {
					log.severe("No sendActivity given");
				}
				receiverNSPrefix = fnsprefixProcess(fprocessPaType(ftypePa(receiver)));
				senderNSPrefix = fnsprefixProcess(fprocessPaType(ftypePa(sender1)));
				mc2 = buildQName(receiverNSPrefix, receiveActivity);
				mc1 = buildQName(senderNSPrefix, sendActivity);
				messageConstructsSet.add(mc2);
				messageConstructsSet.add(mc1);
				// deal with the "senders" attribute
				if(sendersList.size() >= 2){
					for(int k=1; k<sendersList.size(); k++){
						senderNSPrefix = fnsprefixProcess(fprocessPaType(ftypePa(sendersList.get(k))));
						mc2 = buildQName(receiverNSPrefix, receiveActivity);
						mc1 = buildQName(senderNSPrefix, sendActivity);
						messageConstructsSet.add(mc2);
						messageConstructsSet.add(mc1);
					}
				}
				// create ml2mcMap for function fconstructsML
				ArrayList<QName> mcSenderReceiverList = new ArrayList<QName>();
				if (!sendActivity.isEmpty() && !receiveActivity.isEmpty()){
					mcSenderReceiverList.clear();
					mcSenderReceiverList.add(mc1);
					mcSenderReceiverList.add(mc2);
					ml2mcMap.put(ml, mcSenderReceiverList);
				}
				// create ml2paMap for function fparefsML
				ArrayList<Object> sendersReceiverList = new ArrayList<Object>();
				if (((Element) child).hasAttribute("senders")){
					sendersReceiverList.add(sendersList);
					sendersReceiverList.add(receiver);
				}
				else {
					ArrayList<String> senderList = new ArrayList<String>();
					senderList.add(sender1);
					sendersReceiverList.add(senderList);
					sendersReceiverList.add(receiver);
				}
				ml2paMap.put(ml, sendersReceiverList);
			}
		}
	}
	

	/**********************Method of Name Space******************************/
	/**
	 * to create the Sets: namespaceSet, namespaceprefixSet and Mapping: ns2prefixMap
	 * 
	 * Difference to BPEL4Chor2BPELGroundingAnalyze.getNamespaceSet: no "topology" name space handling
	 * 
	 * @param {Node}   currentNode     The current node of the XML file
	 * @param {String} nodeName        The name of the Node
	 */
	private void getNamespaceSet(Element currentNode, String nodeName){
		String str;
		String[] strSplit, prefixSplit;

		if(currentNode.getLocalName().equals(nodeName)){
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
				getNamespaceSet((Element) child, nodeName);
			}	
		}
	}


	/***********************Method of ParticipantType*************************/
	/**
	 * function 3.4: To create the participantTypeSet 
	 * create set for "participantType"
	 * 
	 * @param {Element} elCurrent     The current Element
	 * @return {Set}    paTypeSet     The participantType set
	 */
	private Set<String> getPaTypeSet(Element elCurrent){
		if(!(elCurrent instanceof Node || elCurrent instanceof Document)){
			return null;
		}

		if(elCurrent.getLocalName().equals("participantType")){
			// analyze name space of participantType node
			String paType = elCurrent.getAttribute("name");
			paTypeSet.add(paType);
		}

		NodeList childNodes = elCurrent.getChildNodes();
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
	 * create set for "participantBehaviorDescription"
	 * 
	 * @param {Element} elCurrent     The current element
	 * @return {Set}    processSet    The process set
	 */
	private Set<QName> getProcessSet(Element elCurrent){
		if(!(elCurrent instanceof Node || elCurrent instanceof Document)){
			return null;
		}
		
		if(elCurrent.getLocalName().equals("participantType")){
			// analyze namespace of participantType node
			String pbd = elCurrent.getAttribute("participantBehaviorDescription");
			QName qNamePBD = QName.valueOf(pbd);
			processSet.add(qNamePBD);
		}
		
		NodeList childNodes = elCurrent.getChildNodes();
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
	 * create the mapping between "participantType" and "participantBehaviorDescription" 
	 * 
	 * @param {Element} elCurrent     The current element
	 * @return {HashMap} paType2processMap The mapping of paType and process
	 */
	private HashMap<String, QName> getPaType2ProcessMap(Element elCurrent){
		if(!(elCurrent instanceof Node || elCurrent instanceof Document)) {
			return null;
		}

		if(elCurrent.getLocalName().equals("participantType")){
			// analyze namespace of participantType node
			String paName = elCurrent.getAttribute("name");
			String pbd = elCurrent.getAttribute("participantBehaviorDescription");
			QName qNamePBD = QName.valueOf(pbd);
			paType2processMap.put(paName, qNamePBD);
		}
		
		NodeList childNodes = elCurrent.getChildNodes();
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
	 * according to the participantType output the pbd.
	 * assumption: paType2processMap is existed.
	 * 
	 * @param {String} participantType     The participant type
	 * @return {QName} process   		   The PBD 
	 */
	private QName fprocessPaType(String participantType){
		if(!participantType.isEmpty() && paType2processMap.containsKey(participantType)){
			return paType2processMap.get(participantType);
		}
		return QName.valueOf(EMPTY);
	}

	/**
	 * function 3.7: nsprefixProcess: Process -> NSPrefix
	 * according to the pbd output the prefix of pbd.
	 * assumption: process is QName.
	 * 
	 * @param {QName} process      The process
	 * @return {String} nsprefix   The name space prefix
	 */
	private String fnsprefixProcess(QName process){
		return process.toString().split(":")[0];
	}


	/********************Method of Participant*************************/
	/**
	 * function 3.8: To create a set of "participant" and call the function 3.36 
	 * to create mapping between "forEach" or "scope" with <participantSet> element
	 * or <participant> element
	 * 
	 * @param {Element} elCurrent     The current element
	 * @return {Set}    paSet         The participant set
	 */
	private Set<String> getPaSet(Element elCurrent){
		if(!(elCurrent instanceof Node || elCurrent instanceof Document)){
			return null;
		}

		if(elCurrent.getLocalName().equals("participant") || 
			elCurrent.getLocalName().equals("participantSet")){
				String pa = elCurrent.getAttribute("name");
				paSet.add(pa);
		}

		// make forEach2setMap for PBDConvertion (base for function 3.36)
		if(elCurrent.getLocalName().equals("participantSet")){
			String paSetName = elCurrent.getAttribute("name");
			if(elCurrent.hasAttribute("scope")){
				String scContent = elCurrent.getAttribute("scope");
				QName qNameScope = QName.valueOf(scContent);
				// it allows just one scope for scope attribute
				fsetForEach(qNameScope, EMPTY);
			}
		    if(elCurrent.hasAttribute("forEach")){
				String fEContent = elCurrent.getAttribute("forEach");
				// it allows many forEachs for forEach attribute
				if(fEContent.contains(" ")){
					String[] fEArray = fEContent.split(" ");
					for(int i=0;i<fEArray.length;i++){
						String strFE = fEArray[i].toString();
						QName qNameForEach = QName.valueOf(strFE);
						fsetForEach(qNameForEach, paSetName);
					}
				}
				else{
					QName qNameForEach = QName.valueOf(fEContent);
					fsetForEach(qNameForEach, paSetName);
				}
			}
		}

		NodeList childNodes = elCurrent.getChildNodes();
		Node child;
		for(int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				getPaSet((Element)child);
			}	
		}
		return paSet;
	}

	/**
	 * create pa2paTypeMap for function 3.9
	 * to create mapping between <participant> and <participantType>
	 * 
	 * @param {Element} elCurrent     The current element
	 */
	private void getPa2PaTypeMap(Element elCurrent){
		if(!(elCurrent instanceof Node || elCurrent instanceof Document)){
			return;
		}

		if(elCurrent.getLocalName().equals("participant")){
			try{
				if(!(elCurrent.getAttribute("name") == EMPTY) &&
						!(elCurrent.getAttribute("type") == EMPTY)){
					String pa = elCurrent.getAttribute("name");
					String paType = elCurrent.getAttribute("type");
					pa2paTypeMap.put(pa, paType);
				}
			}
			catch(Exception e){
				e.printStackTrace();
				log.severe("participant is not defined completely.");
			}
		}
		if(elCurrent.getLocalName().equals("participantSet")){
			try{
				String pa = elCurrent.getAttribute("name");
				String paType = elCurrent.getAttribute("type");
				pa2paTypeMap.put(pa, paType);
				if(elCurrent.hasChildNodes()){
					NodeList childNodes = elCurrent.getChildNodes();
					Node child;
					for(int i = 0; i < childNodes.getLength(); i++){
						child = childNodes.item(i);
						if(child instanceof Element){
							String childPa = child.getAttributes().getNamedItem("name").getNodeValue();
							String childPaType = paType;
							pa2paTypeMap.put(childPa, childPaType);
						}
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				log.severe("participantSet is not defined completely.");
			}
		}

		NodeList childNodes = elCurrent.getChildNodes();
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
	 * according to participant output the participantType.
	 * assumption: pa2paTypeMap is not empty.
	 * 
	 * @param {String} participant       The name of <participant>
	 * @return {String} participantType  The name of <participantType>
	 */
	private String ftypePa (String participant){
		return pa2paTypeMap.get(participant);
	}

	/**
	 * function 3.10: getScopeSet for "scope" and "forEach" attribute of <participant> or <participantSet>
	 * create set scopeSet to store "scope" and "forEach" of <participant> or <participantSet>
	 * 
	 * @param {Element} elCurrent     The current element
	 * @return {Set}    scopeSet      The scope set
	 */
	private Set<QName> getScopeSet(Element elCurrent){
		if(!(elCurrent instanceof Node || elCurrent instanceof Document)){
			return null;
		}

		if(elCurrent.getLocalName().equals("participant") || 
		   elCurrent.getLocalName().equals("participantSet")){
			try{
				// it allows many elements in forEach attribute
				if(!(elCurrent.getAttribute("forEach") == EMPTY)){
					String forEachAttribute = elCurrent.getAttribute("forEach");
					if(forEachAttribute.contains(" ")){
						String[] forEachArray = forEachAttribute.split(" ");
						for(int i=0;i<forEachArray.length;i++){
							QName qNameForEach = QName.valueOf(forEachArray[i].toString());
							scopeSet.add(qNameForEach);
						}
					}
					else{
						QName qNameForEach = QName.valueOf(forEachAttribute);
						scopeSet.add(qNameForEach);
					}
				}

				// it allows just one element in scope attribute 
				if(!(elCurrent.getAttribute("scope") == EMPTY)){
					String scopeAttribute = elCurrent.getAttribute("scope");
					QName qNameScope = QName.valueOf(scopeAttribute);
					scopeSet.add(qNameScope);
				}
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}
		}

		NodeList childNodes = elCurrent.getChildNodes();
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
	 * To create mapping between "participant" and "scope" or "forEach" for function 3.11
	 * create pa2scopeMap: ({String} participant, {QName} "scope" or "forEach")
	 * 
	 * @param {Element} elCurrent     The current element
	 */
	private void getPa2ScopeMap(Element elCurrent){
		if(!((elCurrent instanceof Node) || (elCurrent instanceof Document))){
			return;
		}

		if(elCurrent.getLocalName().equals("participant")){
			try{
				if((elCurrent.getAttribute("name") != EMPTY) &&
						(elCurrent.getAttribute("scope") != EMPTY)){
					String pa = elCurrent.getAttribute("name");
					String paScope = elCurrent.getAttribute("scope");
					QName qNameScope = QName.valueOf(paScope);
					pa2scopeMap.put(pa, qNameScope);
				}
				else if((elCurrent.getAttribute("name") != EMPTY) &&
						(elCurrent.getAttribute("forEach") != EMPTY)){
					String pa = elCurrent.getAttribute("name");
					String paForEach = elCurrent.getAttribute("forEach");
					if(paForEach.contains(" ")){
						String[] paForEachArray = paForEach.split(" ");
						for(int i=0;i<paForEachArray.length;i++){
							// mapping between "participant" and "forEachs" are stored here with
							// "<ForEach>" as id.
							QName qNameForEach = QName.valueOf(paForEachArray[i].toString());
							pa2foreachInScopeMap.put(qNameForEach, "<ForEach>");
							pa2scopeMap.put(pa, pa2foreachInScopeMap);
							scopeSet.add(qNameForEach);
						}
					}
					else{
						QName qNameForEach = QName.valueOf(paForEach);
						scopeSet.add(qNameForEach);
						pa2scopeMap.put(pa, qNameForEach);
					}
				}
				else{
					String pa = elCurrent.getAttribute("name");
					pa2scopeMap.put(pa, EMPTY);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(elCurrent.getLocalName().equals("participantSet")){
			try{
				if(elCurrent.getAttribute("name") != EMPTY){
					String pa = elCurrent.getAttribute("name");
					pa2scopeMap.put(pa, EMPTY);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	
		// recursive to search
		NodeList childNodes = elCurrent.getChildNodes();
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if(child instanceof Element){
				getPa2ScopeMap((Element)child);
			}	
		}
	}


	/***********************Method of MessageLink***********************/
	/**
	 * function: To build QName for function 3.12
	 * according to the prefix and NCName output the builded QName.
	 *  
	 * FIXME: throughout the code, javax.xml.namespace.QName should be used
	 * Fixed: all of the "qName" is generated with QName
	 * 
	 * @param {String} prefix     The prefix
	 * @param {String} NCName     The NCName
	 * @return {QName} qName      The QName
	 */
	private static QName buildQName(String prefix, String NCName){
		String strName = prefix + ":" + NCName;
		QName qName = QName.valueOf(strName);
		return qName;
	}

	/**
	 * To create mapping between <messageLink> and "bindSenderTo"
	 * will be used in fbindSenderToML in Grounding analyze
	 * 
	 * @param {Element} elCurrent      The current element
	 */
	public void getMl2BindSenderToMap(Element elCurrent){
		if(!(elCurrent instanceof Node || elCurrent instanceof Document)){
			return;
		}

		NodeList childNodes = elCurrent.getElementsByTagName("messageLink");
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
	
	/**
	 * function 3.36: assigning a set of participant references to each <forEach> activity
	 * attention: functions of 3.36 will be just used with the tag <participantSet>. 
	 * assumption: pa2scopeMap is not empty.
	 * create a mapping forEach2setMap[sc, paSetName]
	 * 
	 * @param {QName}  sc         The QName of <scope> or <forEach> activity
	 * @param {String} paSetName  The name of <participantSet>
	 */
	private void fsetForEach(QName sc, String paSetName){
			if(pa2scopeMap.containsValue(sc)){
				forEach2setMap.put(sc, EMPTY);
			}
			else{
				forEach2setMap.put(sc, paSetName);
			}
	}
}
