package org.oryxeditor.bpel4chor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
	private String pt1 = "";
	private String pt2 = "";
	private ArrayList<Object> element;
	
	public Comm (Set<Object> pa1, String pa2, String pt1, String pt2){
		this.pa1 = pa1;
		this.pa2 = pa2;
		this.pt1 = pt1;
		this.pt2 = pt2;
		this.element = init(pa1, pa2, pt1, pt2);
	}

	// create an comm with the following ordering: comm=((A,c),(b,d))
	public ArrayList<Object> init(Set<Object> pa1, String pa2, String pt1, String pt2){
		ArrayList<Object> commElement = new ArrayList<Object>(2);
		ArrayList<Object> firstElement = new ArrayList<Object>(2);
		ArrayList<String> secondElement = new ArrayList<String>(2);
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
	
	public String getPt1(){
		return this.pt1;
	}
	
	public String getPt2(){
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
	public Comm changeFirstPortType(String portType){
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
	private String partnerLinkType;			//QName
	private String initializePartnerRole;   //Yes or No, and MUST NOT be used if partnerLink has not a partnerRole
	
	public PartnerLink(String name, String myRole, String partnerRole, String partnerLinkType, String initializePartnerRole){
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
	
	public String getPartnerLinkType(){
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
	
	public void setPartnerLinkType(String plt){
		this.partnerLinkType = plt;
	}
	
	public void setInitializePartnerRole(String initPartnerRole){
		this.initializePartnerRole = initPartnerRole;
	}
}

public class BPEL4Chor2BPELGroundingAnalyze extends FunctionsOfBPEL4Chor2BPEL{
	/************************Name space of Grounding********************/
	/**
	 * To analyze the name spaces of <grounding> of grounding.bpel 
	 *  
	 * @param {Document} currentDocument      The document of grounding.bpel 
	 */
	public void nsAnalyze(Document currentDocument){
		//namespaceSet = new HashSet<String>();
		//namespacePrefixSet = new HashSet<String>();
		//ns2prefixMap = new HashMap<String, String>();
		
		//System.out.println(messageConstructsSet);
		//System.out.println(ml2mcMap);
		//System.out.println(messageLinkSet);
	
		getNamespaceSet(currentDocument, "grounding");
		//System.out.println("ns2prefixMap of grounding is: " + ns2prefixMap);
		//System.out.println("namespaces prefix Set of grounding is: " + namespacePrefixSet);
		//System.out.println("namespaces Set of grounding is: " + namespaceSet);
	}
	
	/************************Message Links of Grounding********************/
	/**
	 * To analyze the part <messageLinks> of grounding.bpel, it is Algorithm 3.2 also.
	 * Analysis of each one <messageLink> declaration of the participant groundings.
	 * executes the derivation of the sets, functions and the relation defined in 
	 * definitions 3.17 to 3.31 for one message link. 
	 *  
	 * @param {Document} currentDocument      The document of grounding.bpel 
	 */
	public void mlAnalyze(Document currentDocument){
		ptSet = new HashSet<String>();
		oSet  = new HashSet<String>();
		ml2opMap = new HashMap<String, String>();
		ml2ptMap = new HashMap<String, String>();
		commMap = new HashMap<Object, Object>();
		mc2plMap = new HashMap<String, PartnerLink>();		// used by 3.23
		sc2plMap = new HashMap<String, Set<PartnerLink>>();	// used by 3.24
		pl2plTypeMap = new HashMap<String, String>();		// used by 3.26
		comm2plsMap = new HashMap<Comm, Object>();			// used by 3.28
		comm2pltMap = new HashMap<Comm, String>();      	// used by 3.29
		pl2myRoleMap = new HashMap<String, String>();		// used by 3.30
		pl2partnerRoleMap = new HashMap<String, String>();	// used by 3.31
		commSet = new HashSet<Object>();					// commSet to store the comms
		
		// 3.17 ptSet output
		//ptSet = getPtSet((Element)currentDocument.getFirstChild());
		//System.out.println("3.17: " + ptSet);
		
		// 3.18 fnsprefixPT() output
		//System.out.println("3.18: " + fnsprefixPT(ptSet.iterator().next().toString()));
		
		// 3.19 oSet output
		//oSet = getOSet((Element)currentDocument.getFirstChild());
		//System.out.println("3.19: " + oSet);
		
		// 3.20 fportTypeMC output
		//System.out.println("3.20: " + fportTypeMC((Element)currentDocument.getFirstChild(), "seller:SendPI"));
		//System.out.println("3.20: " + fportTypeMC((Element)currentDocument.getFirstChild(), "buyer:ReceivePI"));
		//System.out.println("3.20: " + fportTypeMC((Element)currentDocument.getFirstChild(), "buyer:SendPO"));
		//System.out.println("3.20: " + fportTypeMC((Element)currentDocument.getFirstChild(), "buyer:ReceivePO"));
		
		// 3.21 foperationMC output
		//System.out.println("3.21: " + foperationMC((Element)currentDocument.getFirstChild(), "seller:SendPI"));
		//System.out.println("3.21: " + foperationMC((Element)currentDocument.getFirstChild(), "buyer:ReceivePI"));
		//System.out.println("3.21: " + foperationMC((Element)currentDocument.getFirstChild(), "buyer:SendPO"));
		
		/*
		 * ml :             member of messageLinkSet and inherits of NCName
		 * mc1, mc2 :       member of messageConstructsSet and inherits of QName
		 *                  mc1 will be the send activity and mc2 the receive activity of the message link
		 * pt :             member of ptSet(portTypeSet), inherit of QName
		 * o :              member of oSet(operationSet), inherit of QName
		 * pt_nsprefix :    name space prefix of the port type pt, inherits of NCName
		 * b :              member of paSet(participant)
		 * ASet :           a List of participant references
		*/
		String ml = "";
		String mc1 = "", mc2 = "";
		String pt = "";
		String o;
		String pt_nsprefix;
		String b = "";
		Set<Object> ASet = new HashSet<Object>();
		
		NodeList childNodes = ((Element)currentDocument.getFirstChild()).getElementsByTagName("messageLink");
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if (child instanceof Element){
				// add ml and pt into messageLinkSet and ptSet
				ml = ((Element)child).getAttribute("name");
				messageLinkSet.add(ml);
				pt = ((Element)child).getAttribute("portType");
				ptSet.add(pt);
				ml2ptMap.put(ml, pt);									// make a mapping of ml and portType for PBDConvertion
				
				// get name space prefix of pt
				pt_nsprefix = fnsprefixPT(((Element)child).getAttribute("portType"));
				o = buildQName(pt_nsprefix, ((Element)child).getAttribute("operation"));
				oSet.add(o);
				ml2opMap.put(ml, o);									// make a mapping of ml and operatiosn for PBDConvertion
				
				// assign message constructs of ml to port type pt and operation o
				ArrayList<String> mcList = fconstructsML(ml);
				//System.out.println("mcList is: " + mcList);
				mc1 = mcList.get(0);
				mc2 = mcList.get(1);
				//System.out.println("pt is: " + pt);
				//System.out.println("portTypeMC(mc1) is: " + fportTypeMC((Element)child, mc1));
				//System.out.println("portTypeMC(mc2) is: " + fportTypeMC((Element)child, mc2));
				//System.out.println("o is: " + o);
				//System.out.println("operationMC(mc1) is: " + foperationMC((Element)child, mc1));
				//System.out.println("operationMC(mc2) is: " + foperationMC((Element)child, mc2));
				 
				ArrayList<Object> parefsML = fparefsML(ml);
				//System.out.println("parefsML is: " + parefsML);
				Set<Object> A = new HashSet<Object>();
				A.clear();
				if (parefsML.get(0).getClass().getSimpleName().equals("ArrayList")){
					String str = parefsML.get(0).toString();
					A.add(str);
				}
				else 
				{
					A.add(parefsML.get(0).toString());
				}
				b = (String)parefsML.get(1);
				//System.out.println("A is: " + A);
				//System.out.println("b is: " + b);
				//System.out.println("ml2bindSenderToMap is: " + ml2bindSenderToMap);
				
				if(ml2bindSenderToMap.containsKey(ml) && (!ml2bindSenderToMap.get(ml).equals(EMPTY))){
					A.clear();
					A.add((String)ml2bindSenderToMap.get(ml));
				}
				//System.out.println("A after ml2bindSenderToMap is: " + A);
				ASet = A;
			}
			//TRAVERSEComm procedure
			//System.out.println("*****A is: " + ASet);
			//System.out.println("*****b is: " + b);
			//System.out.println("*****pt is: " + pt);
			//System.out.println("*****mc1 is: " + mc1);
			//System.out.println("*****mc2 is: " + mc2);
			//System.out.println("*****ml is: " + ml);
			traverseComm(ASet, b, pt, mc1, mc2, ml);
		}
		//System.out.println("messageLinkSet of grounding: " + messageLinkSet);
		//System.out.println("portTypeSet of grounding: " + ptSet);
		//System.out.println("operationSet of grounding: " + oSet);
		
		//TRAVERSEComm procedure
		//traverseComm(A, b, pt, mc1, mc2, ml);
	}
	
	/**
	 * Algorithm 3.3 Procedure traverseComm
	 * According the Page 38 to 41 of SA to create the partner link declaration
	 * 
	 * @param {Set}    A     The Set of participant reference in Comm construct
	 * @param {String} b     The another participant reference in Comm construct
	 * @param {String} pt    The port type of Comm construct
	 * @param {String} mc1   The first element of message construct (send activity)
	 * @param {String} mc2   The second element of message construct (receive activity)
	 * @param {String} ml    The message link
	 */
	private void traverseComm(Set<Object> A, String b, String pt, String mc1, String mc2, String ml){
		Comm comm = new Comm(null, null, null, null);
		Comm commNew = new Comm(null, null, null, null);
		PartnerLink pl1 = null, pl2 = null;                       	//DT(PL)inherits of NCName
		String plt;                            						//DT(PLType)inherits of NCName
		String a = A.iterator().next().toString();
		Set<String> bSet = new HashSet<String>();
		bSet.add(b);

		if(!commSet.isEmpty()){
			Iterator<Object> it = commSet.iterator();
			while(it.hasNext()){
				comm = (Comm)it.next();
				//System.out.println("!!!!comm from commSet is: " + comm.getElement());
				Set<Object> pa1 = comm.getPa1();
				//System.out.println("!!!" + pa1);
				String pa2 = comm.getPa2();
				//System.out.println("!!!" + pa2);
				String pt1 = comm.getPt1();
				//System.out.println("!!!" + pt1);
				String pt2 = comm.getPt2();
				//System.out.println("!!!" + pt2);
				final boolean CONDITION1 = pa1.equals(A) && pa2.equals(b) && pt2.equals(pt);
				final boolean CONDITION2 = !CONDITION1 && pa1.equals(bSet) && pa2.equals(a) && pt1.equals(pt);
				final boolean CONDITION3 = !CONDITION1 && !CONDITION2 && pa1.equals(bSet) && pa2.equals(a) && pt1.equals(EMPTY);
				if (CONDITION1){
					//System.out.println("!!!!!!!!!!!!!!!CONDITION1!!!!!!!!!!!!!!!!!!!!!!!!");
					ArrayList<Object> plsPair = new ArrayList<Object>();
					//partnerLinksPair is (pl1, pl2)
					plsPair = fpartnerLinksComm(comm);
					//System.out.println("~~~~~plsPair is: " + plsPair);
					pl1 = new PartnerLink(plsPair.get(0).toString());
					pl2 = new PartnerLink(plsPair.get(1).toString());
					fpartnerLinkMC(mc1, pl1); 
					fpartnerLinkMC(mc2, pl2);
					//System.out.println("mc2plMap is changed to: " + mc2plMap);
				}
				else if(CONDITION2){
					//System.out.println("!!!!!!!!!!!!!!!CONDITION2!!!!!!!!!!!!!!!!!!!!!!!!");
					ArrayList<Object> plsPair = new ArrayList<Object>();
					//partnerLinksPair is (pl1, pl2)
					plsPair = fpartnerLinksComm(comm);
					//System.out.println("~~~~~plsPair is: " + plsPair);
					pl1 = new PartnerLink(plsPair.get(0).toString());
					pl2 = new PartnerLink(plsPair.get(1).toString());
					fpartnerLinkMC(mc1, pl2); 
					fpartnerLinkMC(mc2, pl1);
					//System.out.println("mc2plMap is changed to: " + mc2plMap);
				}
				else if(CONDITION3){
					//System.out.println("!!!!!!!!!!!!!!!CONDITION3!!!!!!!!!!!!!!!!!!!!!!!!");
					ArrayList<Object> plsPair = new ArrayList<Object>();
					//partnerLinksPair is (pl1, pl2)
					plsPair = fpartnerLinksComm(comm);
					//System.out.println("~~~~~plsPair is: " + plsPair);
					pl1 = new PartnerLink(plsPair.get(0).toString());
					pl2 = new PartnerLink(plsPair.get(1).toString());
					fpartnerLinkMC(mc1, pl2); 
					fpartnerLinkMC(mc2, pl1);
					//System.out.println("mc2plMap is changed to: " + mc2plMap);
					//change communication into req/res communication and replace the communication in relation mapping.
					Comm commChanged = comm.changeFirstPortType(pt);
					//System.out.println("comm is changed to: " + commChanged.getElement());
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
					//System.out.println("pl2myRoleMap is changed to: " + pl2myRoleMap);
					fpartnerRolePL(pl2, b);
					//System.out.println("pl2partnerRoleMap is changed to: " + pl2partnerRoleMap);
					//System.out.println("===========================================================");
				}
				else{
					//one of the CONDITION 4,5,6 holds
					//new partner link declarations, a new partner link type and a new element of Comm
					//need to be created
					commNew = new Comm(A, b, EMPTY, pt);
					//System.out.println("???????comm_new is: " + comm_new.getElement());
					//commSet.add(comm_new.getElement());			
					commSet.add(commNew);													
					//System.out.println("???????commSet is: " + commSet);
					createPartnerLinkDeclarations(commNew, A, b, pt, mc1, mc2, ml);
				}
			}
		}
		else{
			//one of the CONDITION 4,5,6 holds
			commNew = new Comm(A, b, EMPTY, pt);
			//System.out.println("???????comm_new is: " + comm_new.getElement());
			//commSet.add(comm_new.getElement());			
			commSet.add(commNew);												
			//System.out.println("???????commSet is: " + commSet);
			createPartnerLinkDeclarations(commNew, A, b, pt, mc1, mc2, ml);
		}
	}
	
	/**
	 * Algorithm 3.4 Procedure createPartnerLinkDeclarations
	 * 
	 * @param {Comm}   commNewInput     The input of new Comm construct
	 * @param {Set}    AInput           The input of set A
	 * @param {String} bInput           The input of b
	 * @param {String} ptInput          The input of port type
	 * @param {String} mc1Input         The input of first element of message construct
	 * @param {String} mc2Input         The input of second element of message construct
	 * @param {String} ml               The input of message link
	 */
	private void createPartnerLinkDeclarations(Comm commNewInput, Set<Object> AInput, String bInput, String ptInput, 
												String mc1Input, String mc2Input, String ml){
		PartnerLink pl1 = null, pl2 = null;                     //inherits of NCName
		String plt;                                             //PartnerLinkType, inherits of NCName
		String senders_ids = (String)AInput.iterator().next();  //initially the identifier of the first participant reference of A
		String firstSender = senders_ids;
		//String a;												//element of Participant, inherits of NCName
		String sc;                                              //QName, sc will be used for elements of (Scope U Process)
		if (AInput.remove(senders_ids) && !AInput.isEmpty()){
			Iterator<Object> it = AInput.iterator();
			while(it.hasNext()){
				//adds an underline and the identifier of the participant reference 'next' at the end of the string senders_ids,
				//such as "a1_a2_..._an"
				senders_ids = senders_ids + '_' + (String)it.next();
			}
		}

		AInput.add(firstSender);                                 //recover 'removed element' to ensure the completely of Set A
		//create partner link declarations
		String pl1Name = senders_ids + "-" + bInput + "_isRealizedBy_" + replaceColons(ptInput);
		pl1 = new PartnerLink(pl1Name);
		String pl2Name = bInput + "_isRealizedBy_" + replaceColons(ptInput) + "-" + senders_ids;
		pl2 = new PartnerLink(pl2Name);
		plSet.add(pl1.getName());
		plSet.add(pl2.getName());
		//System.out.println("plSet is: " + plSet);
		
		//create partner link type
		plt = senders_ids + "-" + bInput + "_isRealizedBy_" + replaceColons(ptInput) + "-plt";
		//results in a1_a2_...-an-b_isRealizedBy_pt' -plt
		plTypeSet.add(plt);
		//System.out.println("plTypeSet is: " + plTypeSet);
		
		//assign the message constructs of ml to their partner link declarations
		fpartnerLinkMC(mc1Input, pl1);
		fpartnerLinkMC(mc2Input, pl2);
		
		//assign partner link declarations to their scopes
		//TODO: which one is right? buyer:buyer as in SA or seller:seller as in fact.
		//System.out.println("##################bInput is: " + bInput);
		String scopeResult = (String)fscopePa(bInput);
		//System.out.println("##################scopeResult is: " + scopeResult);		
		if (scopeResult.equals(EMPTY)){
			//sc = fprocessPaType(ftypePa(firstSender));			//?????
			sc = fprocessPaType(ftypePa(bInput));
		}
		else
		{
			sc = (String)fscopePa(bInput);
		}
		//System.out.println("####################pa2scopeMap is: " + pa2scopeMap);
		//System.out.println("####sc is: " + sc);
		Set<PartnerLink> partnerLinkSet1 = new HashSet<PartnerLink>();
		partnerLinkSet1.add(pl1);
		fpartnerLinksScope(sc, partnerLinkSet1);
		//System.out.println("####sc2plMap is: " + sc2plMap);
		//System.out.println("#############################" + ml2bindSenderToMap);
		if (ml2bindSenderToMap.get(ml).equals(EMPTY) || fscopePa(ml2bindSenderToMap.get(ml)).equals(EMPTY)){
			sc = fprocessPaType(ftypePa(bInput));
		}
		else
		{
			sc = (String)fscopePa(ml2bindSenderToMap.get(ml));
		}
		//System.out.println("####pa2scopeMap is: " + pa2scopeMap);
		//System.out.println("####sc is: " + sc);
		Set<PartnerLink> partnerLinkSet2 = new HashSet<PartnerLink>();
		partnerLinkSet2.add(pl2);
		//System.out.println(pl2.getName());
		fpartnerLinksScope(sc, partnerLinkSet2);
		//System.out.println("####sc2plMap is: " + sc2plMap);
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
		//System.out.println("+++++mc2plMap is: " + mc2plMap);
		//System.out.println("+++++sc2plMap is: " + sc2plMap);
		//System.out.println("+++++pl2plTypeMap is: " + pl2plTypeMap);
		//System.out.println("+++++comm2plsMap is: " + comm2plsMap);
		//System.out.println("+++++comm2pltMap is: " + comm2pltMap);
		//System.out.println("+++++pl2myRoleMap is: " + pl2myRoleMap);
		//System.out.println("+++++pl2partnerRoleMap is: " + pl2partnerRoleMap);
		//System.out.println("+++++myRole of pl1 is: " + pl1.getMyRole());
		//System.out.println("+++++myRole of pl2 is: " + pl2.getMyRole());
		//System.out.println("+++++partnerRole of pl1 is: " + pl1.getPartnerRole());
		//System.out.println("+++++partnerRole of pl2 is: " + pl2.getPartnerRole());
		//System.out.println("+++++commSet is: " + commSet);
		//System.out.println("====================================================");
		//Iterator it = partnerLinkSet1.iterator();
		//while (it.hasNext()){
		//	String partnerlinkName = ((PartnerLink)it.next()).getName();
		//	System.out.println("####partnerLinkSet is: " + partnerlinkName);
		//}
		//Iterator it2 = partnerLinkSet2.iterator();
		//while (it2.hasNext()){
		//	String partnerlinkName = ((PartnerLink)it2.next()).getName();
		//	System.out.println("####partnerLinkSet is: " + partnerlinkName);
		//}
	}
	
	/**
	 * To analyze the part <property> of grounding.bpel 
	 *  
	 * @param {Document} currentDocument      The document of grounding.bpel 
	 */
	public void propertyAnalyze(Document currentDocument){
		NodeList childNodes = ((Element)currentDocument.getFirstChild()).getElementsByTagName("property");
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if (child instanceof Element){
				analyzePropertyGrounding((Element)child);
			}
		}
		
		//System.out.println("propName is: " + propName);
		//System.out.println("property is: " + property);
		//System.out.println("corrPropNameSet is: " + corrPropNameSet);
		//System.out.println("propertySet is: " + propertySet);
		//System.out.println("corrPropName2propertyMap is: " + corrPropName2propertyMap);
		//System.out.println("property2nsprefixOfPropMap is: " + property2nsprefixOfPropMap);
	}
	
	/**
	 * Algorithm 3.11 Analysis of one <property> declaration of the participant groundings
	 * 
	 * @param {Element} construct     The tag <property>
	 */
	private void analyzePropertyGrounding(Element construct){
		// the input construct points on the current <property> tag
		String propName = EMPTY;											// element of corrPropNameSet, inherits of NCName
		String property = EMPTY;											// element of propertySet, inherits of QName
		
		// get property name and WSDL property
		propName = construct.getAttribute("name");
		property = construct.getAttribute("WSDLproperty");
		// add them to the sets corrPropNameSet and propertySet
		corrPropNameSet.add(propName);
		propertySet.add(property);
		// assign property name to its WSDL property
		fpropertyCorrPropName(propName, property);
		// assign WSDL property to its name space prefix
		fnsprefixProperty(property, getAttributeNamespacePrefix(construct, "WSDLproperty"));
	}
	
	/*****************Other Functions****************/
	/**
	 * replace the ":" in the inputStr with "_"
	 * 
	 * @param {String} inputStr     The input string
	 * @param {String} inputStr     The replaced output string
	 */
	private String replaceColons(String inputStr){
		if (inputStr.contains(":")){
			return inputStr.replaceAll(":", "_");
		}
		return inputStr;
	}
	
	/**
	 * return the class type of the input object
	 * 
	 * @param {Object} inputObj     The input object
	 * @return {String}             The output of the class for the input object
	 */
	private String dt(Object inputObj){
		return inputObj.getClass().getSimpleName();
	}

	/**
	 * return the name space prefix of the attribute which having the "name", it
	 * will return the first NCName of its value if this is a QName, otherwise it
	 * will return EMPTY
	// input: currentElement type-Element, name type-String
	// output: prefix type-String
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
		
	/**************************main*******************************/
	/*public static void main(String argv[]) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		if(docBuilder.isNamespaceAware()){
			Document docGround = docBuilder.parse("/home/eysler/work/DiplomArbeit/oryx-editor/editor/server/src/org/oryxeditor/bpel4chor/testFiles/groundingSA.bpel");
			Document docTopo = docBuilder.parse("/home/eysler/work/DiplomArbeit/oryx-editor/editor/server/src/org/oryxeditor/bpel4chor/testFiles/topologySA.xml");

			BPEL4ChorTopologyAnalyze topoAnaly = new BPEL4ChorTopologyAnalyze();
			BPEL4ChorGroundingAnalyze grouAnaly = new BPEL4ChorGroundingAnalyze();

			//prepare the MC and ML Set of topology for the grounding analyze
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
			
			// Name space analysis
			grouAnaly.nsAnalyze(docGround);
			
			// MessageLink analysis
			grouAnaly.mlAnalyze(docGround);
			
			// Properties analysis
			grouAnaly.propertyAnalyze(docGround);
		}
	}*/
}
