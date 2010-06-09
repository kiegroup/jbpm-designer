package org.oryxeditor.bpel4chor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Copyright (c) 2009-2010 Changhua Li
 *               2010      Oliver Kopp
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
 * It will be used for the Transformation of the BPEL4Chor to BPEL.
 * 
 * It was designed for the Diplom Arbeit of Changhua Li(student of uni. stuttgart), 
 * It is the procedure of analyze of Grounding, which was designed in the Studien Arbeit
 * of Peter Reimann(2008)
 */

	

/**
 * This class is for relation Communication 3.27 of SA 
 * the form of it should be like ((A,c),(b,d)) with
 * A: subset of participant
 * c: port type of A
 * b: another participant
 * d: port type of b
 * 
 * As the relation that assigns a subset of participant references
 * and another participant reference to a pair of port types which 
 * they use to communicate.
 */
class Comm {
	private Set<Object> pa1 = new HashSet<Object>();
	private String pa2;
	private QName pt1;
	private QName pt2;
	private ArrayList<Object> element;
	
	public Comm (Set<Object> pa1, String pa2, QName pt1, QName pt2){
		this.pa1 = pa1;
		this.pa2 = pa2;
		this.pt1 = pt1;
		this.pt2 = pt2;
		this.element = init(pa1, pa2, pt1, pt2);
	}

	// create an comm with the following ordering: comm=((A,c),(b,d))
	public ArrayList<Object> init(Set<Object> pa1, String pa2, QName pt1, QName pt2){
		ArrayList<Object> commElement = new ArrayList<Object>(2);
		ArrayList<Object> firstElement = new ArrayList<Object>(2);
		ArrayList<Object> secondElement = new ArrayList<Object>(2);
		firstElement.add(pa1);
		firstElement.add(pt1);
		secondElement.add(pa2);
		secondElement.add(pt2);
		commElement.add(firstElement);
		commElement.add(secondElement);
		return commElement;
	}
	
	public Set<Object> getPa1(){
		return this.pa1;
	}
	
	public String getPa2(){
		return this.pa2;
	}
	
	public QName getPt1(){
		return this.pt1;
	}
	
	public QName getPt2(){
		return this.pt2;
	}
	
	public ArrayList<Object> getElement(){
		return this.element;
	}
	
	/**
	 * change the first portType of comm into the specified portType
	 * 
	 * @param {String} portType     The first port type (i.e. c) of Comm 
	 * @return {Comm} comm          The new Comm with new first port type
	 */
	public Comm changeFirstPortType(QName portType){
		this.pt1 = portType;
		return new Comm(pa1, pa2, pt1, pt2);
	}
}

/**
 * The class for partner link structure of BPEL4Chor
 */
class PartnerLink {
	private String name;					//NCName
	private String myRole;
	private String partnerRole;
	private QName  partnerLinkType;			//QName
	private String initializePartnerRole;   //Yes or No, and MUST NOT be used if partnerLink has not a partnerRole
	
	public PartnerLink(String name, String myRole, String partnerRole, QName partnerLinkType, String initializePartnerRole){
		this.name = name;
		this.myRole = myRole;
		this.partnerRole = partnerRole;
		this.partnerLinkType = partnerLinkType;
		this.initializePartnerRole = initializePartnerRole;
	}
	
	public PartnerLink(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getMyRole(){
		return this.myRole;
	}
	
	public String getPartnerRole(){
		return this.partnerRole;
	}
	
	public QName getPartnerLinkType(){
		return this.partnerLinkType;
	}
	
	public String getInitializePartnerRole(){
		return this.initializePartnerRole;
	}
	
	public void setMyRole(String myRole){
		this.myRole = myRole;
	}
	
	public void setPartnerRole(String partnerRole){
		this.partnerRole = partnerRole;
	}
	
	public void setPartnerLinkType(QName plt){
		this.partnerLinkType = plt;
	}
	
	public void setInitializePartnerRole(String initPartnerRole){
		this.initializePartnerRole = initPartnerRole;
	}
}

class MessageLink_Ab {
	public Set<Object> senders; // A
	public String receiver;     // b
}


/**
 * 
 * TODO: refactor: message links should be an object and not strings
 *                 the whole access functions of Peter can be converted to getting properties of an object 
 *
 */
public class BPEL4Chor2BPELGroundingAnalyze {
	private Logger log = Logger.getLogger("org.oryxeditor.bpel4chor.BPEL4Chor2BPELGroundingAnalyze");

	final static String EMPTY = "";
	private final static String WSU_Namespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
	private final static String BPEL_Namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/abstract";
	
	public Set<String> messageLinkSet = new HashSet<String>();
	
	// 3.4: participant types set
	public Set<String> paTypeSet = new HashSet<String>();

	// for the function 3.6 fprocessPaType
	public HashMap<String,QName> paType2processMap = new HashMap<String, QName>();

	// for function 3.9 ftypePa
	public HashMap<String, String> pa2paTypeMap = new HashMap<String, String>();

	public HashMap<String, Object> ml2mcMap = new HashMap<String, Object>();

	public HashMap<String, Object> ml2paMap = new HashMap<String, Object>();

	//in fbindSenderToML defined and for grounding Analyze
	public HashMap<String, String> ml2bindSenderToMap = new HashMap<String, String>();
	public Set<QName> messageConstructsSet; 
	
	// 3.20: fportTypeMC()
	public HashMap<String, QName> ml2ptMap = new HashMap<String, QName>();

	// 3.21: foperationMC()
	public HashMap<String, QName> ml2opMap = new HashMap<String, QName>();

	// 3.22: partnerLink Set
	public Set<String> plSet = new HashSet<String>();

	// 3.23: messageConstruct --> partnerLink Mapping
	public HashMap<QName, PartnerLink> mc2plMap = new HashMap<QName, PartnerLink>();

	// 3.24: scope --> partnerLinkSet Mapping
	public HashMap<QName, Set<PartnerLink>> sc2plMap = new HashMap<QName, Set<PartnerLink>>();

	// 3.25: partnerLinkType Set
	public Set<String> plTypeSet = new HashSet<String>();

	// 3.26: partnerLink --> partnerLinkType
	public HashMap<String, String> pl2plTypeMap = new HashMap<String, String>();

	// 3.29: communication --> partnerLinkType
	public HashMap<Comm, String> comm2pltMap = new HashMap<Comm, String>();

	// 3.30: partnerLink --> myRole 
	public HashMap<String, String> pl2myRoleMap = new HashMap<String, String>();

	// 3.31: partnerLink --> partnerRole
	public HashMap<String, String> pl2partnerRoleMap = new HashMap<String, String>();

	// for function 3.11 fscopePa
	public HashMap<String, Object> pa2scopeMap = new HashMap<String, Object>();

	// used by 3.34
	public HashMap<String, String> corrPropName2propertyMap = new HashMap<String, String>();

	// used by 3.35
	public HashMap<String, String> property2nsprefixOfPropMap = new HashMap<String, String>();

	// used by 3.36
	public Set<String> namespaceSet = new HashSet<String>();

	// 3.2: record all name space prefixes of QName
	public Set<String> namespacePrefixSet = new HashSet<String>();
	public HashMap<String, String> ns2prefixMap = new HashMap<String, String>();
	private String topologyNS;					// it will be used in conversion of PBD

	// 3.17: to save the portType of messageLink of grounding
	public Set<QName> ptSet = new HashSet<QName>();
	
	// 3.28: communication --> partnerLink X partnerLink
	private HashMap<Comm, Object> comm2plsMap = new HashMap<Comm, Object>();
	// relation COMM ((A,c),(b,d))
	private Set<Object> commSet = new HashSet<Object>();
	
	// 3.32: set of NCNames of correlation properties 
	private Set<String> corrPropNameSet = new HashSet<String>();

	// 3.33: set of WSDL properties
	private Set<String> propertySet = new HashSet<String>();

	/************************Name space of Grounding********************/
	/**
	 * To analyze the name spaces of <grounding> of grounding.bpel 
	 *  
	 * @param {Element} grounding      The <grounding> element of grounding.bpel 
	 */
	public void nsAnalyze(Element grounding){
		getNamespaceSet(grounding, "grounding");
	}
	
	private int currentPtNum = 0;
	private HashMap<QName,Integer> ptToCurrentOpCount = new HashMap<QName,Integer>();
	
	private void generateNewPtOpForUngroundedMLs(Set<String> ungroundedMLs) {
		for (String currentMl: ungroundedMLs) {
			MessageLink_Ab mAb = getAbFromMl(currentMl);

			ArrayList<QName> mcList = fconstructsML(currentMl);
			QName mc1 = mcList.get(0);
			QName mc2 = mcList.get(1);
			
			String senderIds = getSenderIds(mAb.senders);
			String ptName = senderIds.concat("_missingOps_pt");
			Integer currentOpNum;
			if (ptToCurrentOpCount.containsKey(ptName)) {
				currentOpNum = ptToCurrentOpCount.get(ptName);
				currentOpNum = currentOpNum + 1;
			} else {
				currentOpNum = new Integer(0);
			}
			ptToCurrentOpCount.put(QName.valueOf(ptName), currentOpNum);
			
			String operationName = "op".concat(currentOpNum.toString());
			// store for later use in PBDConversion.modifyMessageConstruct -> foperationMC
			ml2opMap.put(currentMl, QName.valueOf(operationName));

			traverseComm(mAb.senders, mAb.receiver, QName.valueOf(ptName), mc1, mc2, currentMl);
		}
	}
	
	private MessageLink_Ab getAbFromMl(String ml) {
		ArrayList<Object> parefsML = fparefsML(ml);
		Set<Object> A = new HashSet<Object>();
		if (parefsML.get(0).getClass().getSimpleName().equals("ArrayList")){
			ArrayList<String> strList = (ArrayList<String>)parefsML.get(0);
			for(int j = 0; j<strList.size(); j++){
				String str = strList.get(j);
				A.add(str);
			}
		}
		else 
		{
			A.add(parefsML.get(0).toString());
		}
		String b = (String)parefsML.get(1);
		
		if(ml2bindSenderToMap.containsKey(ml) && (!(ml2bindSenderToMap.get(ml).equals(EMPTY)))){
			A.clear();
			A.add((String)ml2bindSenderToMap.get(ml));
		}
		
		MessageLink_Ab res = new MessageLink_Ab();
		res.senders = A;
		res.receiver = b;
		return res;
	}
	
	/************************Message Links of Grounding********************/
	/**
	 * To analyze the part <messageLinks> of grounding.bpel, Algorithm 3.2.
	 * Analysis of each one <messageLink> declaration of the participant grounding.
	 * executes the derivation of the sets, functions and the relation defined in 
	 * definitions 3.17 to 3.31 for one message link. 
	 *  
	 * @param {Element} currentDocument      The <grounding> element of grounding.bpel 
	 */
	public void mlAnalyze(Element groundingElement){
		String ml;			// member of messageLinkSet and inherits of NCName
		QName  mc1, mc2;	// member of messageConstructsSet and inherits of QName
							// mc1 will be the sendActivity and mc2 the receiveActivity of the message link
		QName  ptName;		// member of ptSet(portTypeSet), inherit of QName
		QName  opName;		// member of oSet(operationSet), inherit of QName
		String pt_nsprefix;	// name space prefix of the port type pt, inherits of NCName

		Set<String> notConvertedMessageLinks = new HashSet<String>();
		notConvertedMessageLinks.addAll(messageLinkSet);
				
		NodeList childNodes = groundingElement.getElementsByTagName("messageLink");
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			ml = getId((Element)child);
			// instead of adding a removal has to be done. The set is later handled to generate "fake" groundings
			notConvertedMessageLinks.remove(ml);

			// add ml and pt into ptSet
			String ptStr = ((Element)child).getAttribute("portType");
			ptName = QName.valueOf(ptStr);
			ptSet.add(ptName);
			// * function 3.20: portTypeMC: MC -> PT
			ml2ptMap.put(ml, ptName);					// make a mapping of ml and "portType" for PBDConvertion

			// get name space prefix of pt
			pt_nsprefix = fnsprefixPT(QName.valueOf(((Element)child).getAttribute("portType")));
			opName = buildQName(pt_nsprefix, ((Element)child).getAttribute("operation"));
			// * function 3.21: operationMC: MC -> O
			ml2opMap.put(ml, opName);					// make a mapping of ml and "operation" for PBDConvertion
			// assign message constructs of ml to port type pt and operation o
			ArrayList<QName> mcList = fconstructsML(ml);
			mc1 = mcList.get(0);
			mc2 = mcList.get(1);

			// Generate A and b for traverseComm
			MessageLink_Ab mAb = getAbFromMl(ml);
			//TRAVERSEComm procedure
			traverseComm(mAb.senders, mAb.receiver, ptName, mc1, mc2, ml);
		}
		generateNewPtOpForUngroundedMLs(notConvertedMessageLinks);
	}
	
	/**
	 * Algorithm 3.3 Procedure traverseComm
	 * According the Page 38 to 41 of SA to create the partner link declaration
	 * 
	 * @param {Set}    A     The Set of participant reference in Comm construct
	 * @param {String} b     The another participant reference in Comm construct
	 * @param {QName} pt     The port type of Comm construct
	 * @param {QName} mc1    The first element of message construct (send activity)
	 * @param {QName} mc2    The second element of message construct (receive activity)
	 * @param {String} ml    The message link
	 */
	private void traverseComm(Set<Object> A, String b, QName pt, QName mc1, QName mc2, String ml){
		Comm comm, commNew;
		PartnerLink pl1, pl2;                       	// inherits of NCName
		String plt;                         			// inherits of NCName
		String a = A.iterator().next().toString();
		Set<String> bSet = new HashSet<String>();
		bSet.add(b);
		if(!commSet.isEmpty()){
			Iterator<Object> it = commSet.iterator();
			while(it.hasNext()){
				comm = (Comm)it.next();
				Set<Object> pa1 = comm.getPa1();
				String pa2 = comm.getPa2();
				QName pt1 = comm.getPt1();
				QName pt2 = comm.getPt2();
				final boolean CONDITION1 = pa1.equals(A) && pa2.equals(b) && pt2.getLocalPart().equals(pt.getLocalPart());
				final boolean CONDITION2 = !CONDITION1 && pa1.equals(bSet) && pa2.equals(a) && pt1.equals(pt);
				final boolean CONDITION3 = !CONDITION1 && !CONDITION2 && pa1.equals(bSet) && pa2.equals(a) && pt1.getLocalPart().equals(EMPTY);
				if (CONDITION1){
					ArrayList<Object> plsPair = new ArrayList<Object>();
					//partnerLinksPair is (pl1, pl2)
					plsPair = fpartnerLinksComm(comm);
					pl1 = new PartnerLink(plsPair.get(0).toString());
					pl2 = new PartnerLink(plsPair.get(1).toString());
					fpartnerLinkMC(mc1, pl1); 
					fpartnerLinkMC(mc2, pl2);
				}
				else if(CONDITION2){
					ArrayList<Object> plsPair = new ArrayList<Object>();
					//partnerLinksPair is (pl1, pl2)
					plsPair = fpartnerLinksComm(comm);
					pl1 = new PartnerLink(plsPair.get(0).toString());
					pl2 = new PartnerLink(plsPair.get(1).toString());
					fpartnerLinkMC(mc1, pl2); 
					fpartnerLinkMC(mc2, pl1);
				}
				else if(CONDITION3){
					ArrayList<Object> plsPair = new ArrayList<Object>();
					//partnerLinksPair is (pl1, pl2)
					plsPair = fpartnerLinksComm(comm);
					pl1 = new PartnerLink(plsPair.get(0).toString());
					pl2 = new PartnerLink(plsPair.get(1).toString());
					fpartnerLinkMC(mc1, pl2); 
					fpartnerLinkMC(mc2, pl1);
					//change communication into req/res communication and replace the communication in relation mapping.
					Comm commChanged = comm.changeFirstPortType(pt);
					if(commSet.contains(comm)){
						commSet.remove(comm);
						commSet.add(commChanged);
					}
					
					if(comm2plsMap.containsKey(comm)){
						comm2plsMap.remove(comm);
						fpartnerLinksComm(commChanged, plsPair);
					}
					
					plt = fpltComm(comm);
					if(comm2pltMap.containsKey(comm)){
						comm2pltMap.remove(comm);
						fpltComm(commChanged, plt);
					}
					fmyRolePL(pl1, b);
					fpartnerRolePL(pl2, b);
				}
				else{
					//one of the CONDITION 4,5,6 holds
					//new partner link declarations, a new partner link type and a new element of Comm
					//need to be created
					commNew = new Comm(A, b, QName.valueOf(EMPTY), pt);
					it.remove();
					commSet.add(commNew);													
					createPartnerLinkDeclarations(commNew, A, b, pt, mc1, mc2, ml);
				}
			}
		}
		else{
			//one of the CONDITION 4,5,6 holds
			commNew = new Comm(A, b, QName.valueOf(EMPTY), pt);
			commSet.add(commNew);												
			createPartnerLinkDeclarations(commNew, A, b, pt, mc1, mc2, ml);
		}
	}
	
	/**
	 * Generate a string of senders for the given set A
	 * 
	 * @param 	{Set}		The A set 
	 * @return 	{String}	The string of all senders connected with "_"
	 */
	private String getSenderIds(Set<Object> A) {
		Iterator<Object> it = A.iterator();
		String res = EMPTY;
		do {
			String current = (String) it.next();
			res = res.concat(current);
			if (it.hasNext()) {
				res = res.concat("_");
			}
		} while (it.hasNext());
		return res;
	}
	
	/**
	 * Algorithm 3.4 Procedure createPartnerLinkDeclarations
	 * 
	 * @param {Comm}   commNewInput     The input of new Comm construct
	 * @param {Set}    AInput           The input of set A
	 * @param {String} bInput           The input of b
	 * @param {QName}  ptInput          The input of port type
	 * @param {QName}  mc1Input         The input of first element of message construct
	 * @param {QName}  mc2Input         The input of second element of message construct
	 * @param {String} ml               The input of message link
	 */
	private void createPartnerLinkDeclarations(Comm commNewInput, Set<Object> AInput, String bInput, QName ptInput, 
												QName mc1Input, QName mc2Input, String ml){
		PartnerLink pl1, pl2;                     			//inherits of NCName
		String plt;                                         //PartnerLinkType, inherits of NCName
		String senders_ids = getSenderIds(AInput);   
		String firstSender = (String)AInput.iterator().next();
		
		QName sc;                                           //QName, sc will be used for elements of (Scope U Process)

		// create partner link declarations
		String pl1Name = senders_ids + "-" + bInput + "_isRealizedBy_" + replaceColons(ptInput.toString());
		pl1 = new PartnerLink(pl1Name);
		String pl2Name = bInput + "_isRealizedBy_" + replaceColons(ptInput.toString()) + "-" + senders_ids;
		pl2 = new PartnerLink(pl2Name);
		plSet.add(pl1.getName());
		plSet.add(pl2.getName());
		
		//create partner link type
		plt = senders_ids + "-" + bInput + "_isRealizedBy_" + replaceColons(ptInput.toString()) + "-plt";
		plTypeSet.add(plt);
		
		//assign the message constructs of ml to their partner link declarations
		fpartnerLinkMC(mc1Input, pl1);
		fpartnerLinkMC(mc2Input, pl2);
		
		//assign partner link declarations to their scopes
		String strScope = fscopePa(bInput).toString();
		if (strScope.equals(EMPTY)){
			sc = fprocessPaType(ftypePa(firstSender));
		}
		else
		{
			strScope = fscopePa(bInput).toString();
			sc = QName.valueOf(strScope);
		}
		Set<PartnerLink> partnerLinkSet1 = new HashSet<PartnerLink>();
		if(sc2plMap.containsKey(sc)){
			partnerLinkSet1 = sc2plMap.get(sc);
		}
		partnerLinkSet1.add(pl1);
		fpartnerLinksScope(sc, partnerLinkSet1);					
		if (ml2bindSenderToMap.get(ml).equals(EMPTY) || 
				(fscopePa(ml2bindSenderToMap.get(ml))).equals(EMPTY) ||
				fscopePa(bInput).equals(EMPTY)){
			if(!AInput.isEmpty()){
				sc = fprocessPaType(ftypePa(bInput));
			}
		}
		else
		{
			sc = QName.valueOf((String)fscopePa(ml2bindSenderToMap.get(ml)));
		}
		Set<PartnerLink> partnerLinkSet2 = new HashSet<PartnerLink>();
		if(sc2plMap.containsKey(sc)){
			partnerLinkSet2 = sc2plMap.get(sc);
		}
		partnerLinkSet2.add(pl2);
		if(!sc.equals(EMPTY)){
			fpartnerLinksScope(sc, partnerLinkSet2);						
		}
		// modify the remaining functions
		ftypePL(pl1, plt);
		ftypePL(pl2, plt);
		ArrayList<Object> plsPair = new ArrayList<Object>();
		plsPair.add(pl1.getName());
		plsPair.add(pl2.getName());
		fpartnerLinksComm(commNewInput, plsPair);
		fpltComm(commNewInput, plt);
		fmyRolePL(pl1, EMPTY);
		fmyRolePL(pl2, bInput);
		fpartnerRolePL(pl1, bInput);
		fpartnerRolePL(pl2, EMPTY);
	}
	
	/**
	 * To analyze the part <property> of grounding.bpel 
	 *  
	 * @param {Document} currentDocument      The element <grounding> of grounding.bpel 
	 */
	public void propertyAnalyze(Element currentDocument){
		NodeList childNodes = currentDocument.getElementsByTagName("property");
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if (child instanceof Element){
				analyzePropertyGrounding((Element)child);
			}
		}
	}
	
	/**
	 * Algorithm 3.11 Analysis of one <property> declaration of the participant groundings
	 * 
	 * @param {Element} construct     The tag <property>
	 */
	private void analyzePropertyGrounding(Element construct){
		// the input construct points on the current <property> tag
		String propName;									// element of corrPropNameSet, inherits of NCName
		String property;									// element of propertySet, inherits of QName
		
		// get property name and WSDL property
		propName = getId(construct);
		property = construct.getAttribute("WSDLproperty");
		// add them to the sets corrPropNameSet and propertySet
		corrPropNameSet.add(propName);
		propertySet.add(property);
		// assign property name to its WSDL property
		fpropertyCorrPropName(propName, property);
		// assign WSDL property to its name space prefix
		fnsprefixProperty(property, getAttributeNamespacePrefix(construct, "WSDLproperty"));
	}
	
	/**
	 * 
	 * @param construct
	 * @return returns the id of the construct, null if the element has no id
	 */
	private String getId(Element construct) {
		if (construct.hasAttributeNS(WSU_Namespace, "Id")){
			return construct.getAttributeNS(WSU_Namespace, "Id");
		} else if (construct.hasAttribute("name")){
			return construct.getAttribute("name");
		} else if (construct.hasAttribute("wsu:id")) {
			// hack - if wsu is not declared in the namespaces, we just use the string used in papers
			return construct.getAttribute("wsu:id");
		} else if (construct.hasAttribute("wsu:Id")) {
			// hack - same as above, but with correct casing
			return construct.getAttribute("wsu:Id");
		} else if (construct.hasAttributeNS(BPEL_Namespace, "name")){
			// fallback - maybe the name attribute is prefixed with the BPEL namespace prefix
			// This seems to be an awkard design by DOM, isn't it?
			// normally, hasAttributeNS(BPEL_Namespace, "name") should return the same as hasAttribute("name") if the element
			// itself is in the BPEL namespace
			return construct.getAttributeNS(BPEL_Namespace, "name");
		} else {
			return null;
		}
	}
	
	/**
	 * function 3.18: nsprefixPT: PT -> NSPrefix
	 * according to "portType" output the prefix of it. 
	 * 
	 * @param 	{QName} 	portType     The port type
	 * @return 	{String} 	nsprefix   	 The name space prefix
	 */
	private String fnsprefixPT(QName portType){
		if(!portType.toString().equals(EMPTY)){
			return portType.getLocalPart().toString().split(":")[0];
		}
		return QName.valueOf(EMPTY).toString();
	}
	
	/**
	 * function 3.23: partnerLinkMC: MC -> PL
	 * add a mapping into mc2plMap [messageConstruct, partnerLink]
	 * assumption: mc2plMap is existed.
	 * 
	 * @param {String}      mc        The message construct
	 * @param {PartnerLink} pl        The partner link
	 */
	private void fpartnerLinkMC(QName mc, PartnerLink pl){
		mc2plMap.put(mc, pl);
	}
	
	/**
	 * function 3.24: partnerLinksScope: (Scope U Process) -> 2^PL
	 * add a mapping into sc2plMap [sc, partnerLinkSet]
	 * assumption: sc2plMap is existed.
	 * 
	 * @param {String} sc             The element of scopeSet and processSet
	 * @param {Set}    partnerLinkSet The partner link set
	 */
	private void fpartnerLinksScope(QName sc, Set<PartnerLink> partnerLinkSet){
		sc2plMap.put(sc, partnerLinkSet);
	}

	/**
	 * function 3.26: typePL: PL -> PLType
	 * add a mapping into pl2plTypeMap [pl.getName(), plType]
	 * assumption: pl2plTypeMap is existed.
	 * 
	 * @param {PartnerLink} pl       The partner link
	 * @param {String}      plType   The partner link type
	 */
	private void ftypePL(PartnerLink pl, String plType){
		pl2plTypeMap.put(pl.getName(), plType);
		pl.setPartnerLinkType(QName.valueOf(plType));
	}

	/**
	 * function 3.28: partnerLinksComm: Comm -> PL x PL
	 * add a mapping into comm2plsMap [comm, plsPair]
	 * assumption: comm2plsMap is existed.
	 * 
	 * @param {Comm}      comm     The communication((A,c),(b,d))
	 * @param {ArrayList} plsPair  The pair of partner link(pl1, pl2)
	 */
	private void fpartnerLinksComm(Comm comm, ArrayList<Object> plsPair){
		comm2plsMap.put(comm, plsPair);
	}

	/**
	 * function 3.28: partnerLinksComm: Comm -> PL x PL
	 * according to pair of "comm" output the pair of partnerLinks  
	 * assumption: comm2plsMap is not empty.
	 * 
	 * @param {Comm}       comm     The communication((A,c),(b,d))
	 * @return {ArrayList} plsPair  The pair of partner link(pl1, pl2)
	 */
	private ArrayList<Object> fpartnerLinksComm(Comm comm){
		ArrayList<Object> plsPair = new ArrayList<Object>();
		if(comm2plsMap.containsKey(comm)){
			return (ArrayList<Object>) comm2plsMap.get(comm);
		}
		return plsPair;
	}

	/**
	 * function 3.29: pltComm: Comm -> PLType
	 * add a mapping into comm2pltMap [comm, plType]
	 * assumption: comm2pltMap is existed.
	 * 
	 * @param {Comm}   comm            The communication((A,c),(b,d))
	 * @param {String} partnerLinkType The partner link type
	 */
	private void fpltComm(Comm comm, String plType){
		comm2pltMap.put(comm, plType);
	}

	/**
	 * function 3.29: pltComm: Comm -> PLType
	 * according to "comm" output the partnerLinkType
	 * assumption: comm2pltMap is not empty.
	 * 
	 * @param {Comm}    comm            The communication((A,c),(b,d))
	 * @return {String} partnerLinkType The partner link type
	 */
	private String fpltComm(Comm comm){
		if(comm2pltMap.containsKey(comm)){
			return comm2pltMap.get(comm);
		}
		return EMPTY;
	}
	
	/**
	 * function 3.30: myRolePL: PL -> Pa U {EMPTY}
	 * add a mapping into pl2myRoleMap [pl.getName(), myRoleValue]
	 * assumption: pl2myRoleMap is existed.
	 * 
	 * @param {PartnerLink} pl           The partner link
	 * @param {String}      myRoleValue  The value of myRole in partner link
	 */
	private void fmyRolePL(PartnerLink pl, String myRoleValue){
		pl2myRoleMap.put(pl.getName(), myRoleValue);
		pl.setMyRole(myRoleValue);
	}

	/**
	 * function 3.31: partnerRolePL: PL -> Pa U {EMPTY}
	 * add a mapping into pl2partnerRoleMap [pl.getName(), partnerRoleValue]
	 * assumption: pl2partnerRoleMap is existed.
	 * 
	 * @param {PartnerLink} pl                The partner link
	 * @param {String}      partnerRoleValue  The value of partnerRole in partner link
	 */
	private void fpartnerRolePL(PartnerLink pl, String partnerRoleValue){
		pl2partnerRoleMap.put(pl.getName(), partnerRoleValue);
		pl.setPartnerRole(partnerRoleValue);
	}

	/**
	 * function 3.34: the function assigning a property to each property name. 
	 *                propertyCorrPropName: CorrPropName -> Property
	 * add a mapping into corrPropName2propertyMap [propNameInput, propertyInput]
	 * assumption: corrPropName2propertyMap is existed.
	 * 
	 * @param {String} propNameInput      The property name
	 * @param {String} propertyInput      The WSDLproperty value
	 */
	private void fpropertyCorrPropName(String propNameInput, String propertyInput){
		corrPropName2propertyMap.put(propNameInput, propertyInput);
	}

	/**
	 * function 3.35: assigning a name space prefix to each WSDL property. 
	 *                nsprefixProperty: property -> nsprefix
	 * add a mapping into property2nsprefixOfPropMap [propertyInput, nsprefixInput]
	 * assumption: property2nsprefixOfPropMap is existed.
	 * 
	 * @param {String} propertyInput        The WSDLproperty value in grounding
	 * @param {String} nsprefixInput        The name space prefix of this value
	 */
	private void fnsprefixProperty(String propertyInput, String nsprefixInput){
		property2nsprefixOfPropMap.put(propertyInput, nsprefixInput);
	}


	/**
	 * to create the Sets: namespaceSet, namespaceprefixSet and Mapping: ns2prefixMap
	 * 
	 * @param {Element} currentNode     The current element of the XML file
	 * @param {String}  nodeName        The name of the Node
	 */
	private void getNamespaceSet(Element currentNode, String nodeName){
		String str;
		String[] strSplit, prefixSplit;

		if(currentNode.getNodeName().equals(nodeName)){
			for(int i=0; i<currentNode.getAttributes().getLength(); i++){
				str = currentNode.getAttributes().item(i).toString();
				strSplit = str.split("=");
				if(strSplit[0].contains("xmlns") || (strSplit[0].equals("targetNamespace")) 
						|| (strSplit[0].equals("topology"))){
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
					if(strSplit[0].equals("topology")){
						ns2prefixMap.put(strSplit[0], strSplit[1].replaceAll("\"", ""));
						String valueOfTopologyInGrounding = strSplit[1].replaceAll("\"", "");
						namespacePrefixSet.add(strSplit[0]);
						namespaceSet.add(valueOfTopologyInGrounding);
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
	
	/**
	 * function 3.14: constructsML: ML -> MC x MC
	 * for each <messageLink> specify a "sendActivity" and a "receiveActivity".
	 * 
	 * @param {String} mlName               The message link
	 * @return {ArrayList} mcSenderReceiver The ArrayList [senderNS:senderActivity, receiverNS:receiverActivity]
	 */
	private ArrayList<QName> fconstructsML(String ml){
		ArrayList<QName> mcSenderReceiver = new ArrayList<QName>();
		if(!ml2mcMap.isEmpty()){
			mcSenderReceiver = (ArrayList<QName>)ml2mcMap.get(ml);
		} else {
			log.severe("foncstructsML: ml2mcMap is empty for " + ml);
		}
		if (mcSenderReceiver == null) {
			log.severe("ml2mcMap found empty element for " + ml);
			throw new IllegalStateException("ml2mcMap found empty element for " + ml);
		}
		if (mcSenderReceiver.size() != 2) {
			log.severe("foncstructsML: mcSenderReceiver.size() is != 2 " + ml);
		}
		return mcSenderReceiver;
	}
	
	/**
	 * function: To build QName for function 3.12 
	 * to build a QName
	 * 
	 * @param {String} prefix     The prefix
	 * @param {String} NCName     The NCName
	 * @return {QName} QName      The QName
	 */
	private static QName buildQName(String prefix, String NCName){
		String strName = prefix + ":" + NCName;
		QName qName = QName.valueOf(strName);
		return qName;
	}

	/**
	 * function 3.15: parefsML: ML -> 2^Pa x Pa
	 * according to messageLink output pair of senderArrayList of participant and receiver participant
	 * assumption: ml2paMap is not empty
	 * 
	 * @param 	{String} 	ml                          The message link
	 * @return 	{ArrayList} outputSenderReceiverPaList 	The ArrayList [[senderArrayListPa], receiverPa]
	 */
	private ArrayList<Object> fparefsML(String ml){
		ArrayList<Object> outputSenderReceiverPaList = new ArrayList<Object>();
		if(!(ml2paMap.isEmpty())){
			outputSenderReceiverPaList = (ArrayList<Object>)ml2paMap.get(ml);
		}
		return outputSenderReceiverPaList;
	}
	/**
	 * function 3.11: scopePa: Pa -> Scope U {EMPTY}
	 * according to participant output the <scope> or <forEach> activity.
	 * assumption: pa2scopeMap is not empty.
	 * 
	 * @param	{String} participant     The participant
	 * @return	{Object} EMPTY or ArrayList for <forEach> or <scope> activity
	 */
	private Object fscopePa(String participant){
		try{
			if(pa2scopeMap.containsKey(participant)){
				return pa2scopeMap.get(participant);
			}
			else
				return QName.valueOf(EMPTY);	
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return QName.valueOf(EMPTY);
	}
	
	/**
	 * function 3.9: typePa: Pa -> paType
	 * according to participant output the participantType
	 * assumption: pa2paTypeMap is not empty.
	 * 
	 * @param {String} participant       The participant
	 * @return {String} participantType  The participantType
	 */
	private String ftypePa (String participant){
		if(pa2paTypeMap.containsKey(participant)){
			return pa2paTypeMap.get(participant);
		}
		return EMPTY;
	}

	/**
	 * function 3.6: processPaType: PaType -> Process
	 * according to participantType output the pbd
	 * assumption: paType2processMap is not empty.
	 * 
	 * @param	{String}	paType    The participant type
	 * @return	{QName}		process   The process 
	 */
	private QName fprocessPaType(String paType){
		if(!paType.isEmpty() && paType2processMap.containsKey(paType)){
			return paType2processMap.get(paType);
		}
		return QName.valueOf(EMPTY);
	}
	
	/**
	 * replace the ":" with "_" in the inputStr. 
	 * 
	 * @param 	{String} inputStr     The input string
	 * @return 	{String} inputStr     The replaced output string
	 */
	private String replaceColons(String inputStr){
		if (inputStr.contains(":")){
			return inputStr.replaceAll(":", "_");
		}
		return inputStr;
	}
	

	/**
	 * return the name space prefix of the attribute which having the "name", it
	 * will return the first NCName of its value if this is a QName, otherwise it
	 * will return EMPTY
	 * 
	 * @param {Element} currentElement     The current element
	 * @param {String}  name			   The name of desired
	 * @return {String}                    The name space prefix of the name
	 */
	private String getAttributeNamespacePrefix(Element currentElement, String name){
		if(currentElement.hasAttribute(name) && 
				currentElement.getAttribute(name).contains(":")){
			String value = currentElement.getAttribute(name);
			String[] valueSplit = value.split(":");
			return valueSplit[0];
		}
		return EMPTY;
	}
}
