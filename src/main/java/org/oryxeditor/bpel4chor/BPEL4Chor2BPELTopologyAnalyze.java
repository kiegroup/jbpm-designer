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
 * It is the analyze of Topology, which was designed in the Studien Arbeit
 * of Peter Reimann(2008)
 */

public class BPEL4Chor2BPELTopologyAnalyze extends FunctionsOfBPEL4Chor2BPEL{
	/**
	 * To analyze the name spaces of <topology> of topology.xml with the node name "topology"
	 * 
	 * @param {Document} currentDocument      The document of topology.xml 
	 */
	public void nsAnalyze (Document currentDocument){
		//namespaceSet = new HashSet<String>();
		//namespacePrefixSet = new HashSet<String>();
		//ns2prefixMap = new HashMap<String, String>();
		
		getNamespaceSet(currentDocument, "topology");
		//System.out.println("ns2prefixMap of topology is: " + ns2prefixMap);
		//System.out.println("namespaces prefix Set of topology is: " + namespacePrefixSet);
		//System.out.println("namespaces Set of topology is: " + namespaceSet);			
		
		/*if(!namespacePrefixSet.isEmpty()){
			Iterator<String> it = namespacePrefixSet.iterator();
			while(it.hasNext()){
				String namespace = (String)it.next();
				System.out.println("prefixNS=" + fprefixNS(namespace));
			}
		}*/
	}
	
	/**
	 * To analyze the part <participantTypes> of topology.xml
	 * 
	 * @param {Document} currentDocument     The document of topology.xml 
	 */
	public void paTypeAnalyze (Document currentDocument){
		
		paTypeSet = new HashSet<String>();
		processSet = new HashSet<String>();
		paType2processMap = new HashMap<String, String>();

		paTypeSet = getPaTypeSet((Element)currentDocument.getFirstChild());
		processSet = getProcessSet((Element)currentDocument.getFirstChild());
		paType2processMap = getPaType2ProcessMap((Element)currentDocument.getFirstChild());
		//System.out.println("paTypeSet" + paTypeSet);
		//System.out.println("processSet" + processSet);
		
		
		/*if (!paTypeSet.isEmpty()){
			Iterator<String> it = paTypeSet.iterator();
			while (it.hasNext()){
				String paType = (String)it.next();
			    fprocessPaType(paType);
			}
		}		

		// namespace prefix of process
		if (!processSet.isEmpty()){
			Iterator<String> it = processSet.iterator();
			while (it.hasNext()){
				String process = (String)it.next();
				if(process.contains(":")){
					System.out.println("namespace prefix of process " + process + " is: " 
							+ fnsprefixProcess(process));
				}
			}
		}*/
	}
	
	/**
	 * To analyze the part <participants> of topology.xml
	 * 
	 * @param {Document} currentDocument     The document of topology.xml
	 */
	public void paAnalyze (Document currentDocument){
		
		paSet = new HashSet<String>();
		scopeSet = new HashSet<String>();
		pa2paTypeMap = new HashMap<String, String>();
		pa2scopeMap = new HashMap<String, Object>();
		pa2foreachInScopeMap = new HashMap<String, String>();
		
		paSet = getPaSet((Element)currentDocument.getFirstChild());
		//System.out.println("paSet is: " + paSet);
		
		paTypeSet = getPaTypeSet((Element)currentDocument.getFirstChild());
		//System.out.println("paTypeSet is: " + paTypeSet);
		
		getPa2PaTypeMap((Element)currentDocument.getFirstChild());
		//System.out.println("pa2paTypeMap is:" + pa2paTypeMap);
		
		scopeSet = getScopeSet((Element)currentDocument.getFirstChild());
		//System.out.println("scopeSet is:" + scopeSet);
		
		getPa2ScopeMap((Element)currentDocument.getFirstChild());
		//System.out.println("pa2scopeMap is: " + pa2scopeMap);		
		
		//System.out.println(ftypePa("buyerref"));
		//System.out.println(ftypePa("sellerref"));
		//System.out.println(ftypePa("sellers"));
		
		//System.out.println("scopePa: buyerref--> " + fscopePa("buyerref"));
		//System.out.println("scopePa: sellerref--> " + fscopePa("sellerref"));
		//System.out.println("scopePa: sellers--> " + fscopePa("sellers"));
	}
	
	/**
	 * To analyze the part <messageLinks> of topology.xml
	 * 
	 * @param {Document} currentDocument     The document of topology.xml
	 */
	public void mlAnalyze(Document currentDocument){
		
		messageLinkSet = new HashSet<String>();		  // ML Set
		messageConstructsSet = new HashSet<String>(); // MC Set
		String ml;                                    // ml is an Element of messageLinkSet
		String receiver, sender1; 					  // they are the Element of PaSet
		ArrayList<String> sendersList = new ArrayList<String>();  // to store Elements of Attribute "senders"
		String receiveActivity, sendActivity;         // NCName
		String receiverns, senderns;                  // name space prefixes receiver-ns and sender-ns
		String mc1, mc2;							  // QName, mc1 will be the sned activity and mc2 the receive activity of the message link
		
		// during the analyze of messageLink of topology to get the messageLinkSet, 
		// messageConstructsSet, ml2mcMap, ml2paMap, 
		NodeList childNodes = ((Element)currentDocument.getFirstChild()).getElementsByTagName("messageLink");
		Node child;
		for (int i=0; i<childNodes.getLength(); i++){
			child = childNodes.item(i);
			if (child instanceof Element){
				ml = ((Element)child).getAttribute("name");
				messageLinkSet.add(ml);
				receiver = ((Element)child).getAttribute("receiver");
				sender1 = "none";
				if (((Element) child).hasAttribute("sender")){
					sender1 = ((Element)child).getAttribute("sender");
				}
				else {
					sendersList.clear();
					String senders = ((Element)child).getAttribute("senders");
					String[] sendersSplit = senders.split(" ");
					for(int j=0; j<sendersSplit.length; j++){
						sendersList.add(sendersSplit[j]);
						sender1 = (String)sendersList.get(0);
					}
				}
				receiveActivity = ((Element)child).getAttribute("receiveActivity");
				sendActivity = ((Element)child).getAttribute("sendActivity");
				receiverns = fnsprefixProcess(fprocessPaType(ftypePa(receiver)));
				//TODO: sendersList[], just sender1 is done.
				senderns = fnsprefixProcess(fprocessPaType(ftypePa(sender1)));
				mc2 = buildQName(receiverns, receiveActivity);
				mc1 = buildQName(senderns, sendActivity);
				messageConstructsSet.add(mc2);
				messageConstructsSet.add(mc1);
				// create ml2mcMap for function fconstructsML
				ArrayList<String> mcSenderReceiverList = new ArrayList<String>();
				if (!sendActivity.isEmpty() && !receiveActivity.isEmpty()){
					mcSenderReceiverList.clear();
					mcSenderReceiverList.add(mc1);
					mcSenderReceiverList.add(mc2);
					ml2mcMap.put(ml, mcSenderReceiverList);
				}
				// create ml2paMap for function fparefsML
				ArrayList<Object> senderReceiverList = new ArrayList<Object>();
				//senderReceiverList.clear();
				if (((Element) child).hasAttribute("senders")){
					senderReceiverList.add(sendersList);
					senderReceiverList.add(receiver);
				}
				else {
					senderReceiverList.add(sender1);
					senderReceiverList.add(receiver);
				}
				ml2paMap.put(ml, senderReceiverList);
			}
		}

		//System.out.println("MessageLinkSet is: " + messageLinkSet);
		//System.out.println("MessageConstructsSet is: " + messageConstructsSet);
		//System.out.println(ml2mcMap);
		//System.out.println("!!!!!" + ml2paMap);

		//System.out.println("3.14 function for ProductInformation is: " + 
		//		fconstructsML("ProductInformation"));
		//System.out.println("3.14 function for PurchaseOrder is: " + 
		//		fconstructsML("PurchaseOrder"));
		//System.out.println("3.14 function for POConfirmation is: " + 
		//		fconstructsML("POConfirmation"));

		//System.out.println("3.15 function for ProductInformation is: " + 
		//		fparefsML("ProductInformation"));
		
		//System.out.println("3.16 function for ProductInformation is: " + 
		//		fbindSenderToML((Element)document.getFirstChild(), "ProductInformation"));
		//System.out.println("3.16 function for PurchaseOrder is: " + 
		//		fbindSenderToML((Element)document.getFirstChild(), "PurchaseOrder"));
		//System.out.println("3.16 function for POConfirmation is: " + 
		//		fbindSenderToML((Element)document.getFirstChild(), "POConfirmation"));
		
	}
	
	/*************************Main**********************************/
	/*public static void main (String[] args) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		if(docBuilder.isNamespaceAware()){
			Document docTopo = docBuilder.parse("/home/eysler/work/DiplomArbeit/oryx-editor/editor/server/src/org/oryxeditor/bpel4chor/testFiles/topologySA.xml");

			BPEL4ChorTopologyAnalyze topoAnaly = new BPEL4ChorTopologyAnalyze();

			//for Name space
			topoAnaly.nsAnalyze(docTopo);

			//for Participant Type
			topoAnaly.paTypeAnalyze(docTopo);

			//for Participant
			topoAnaly.paAnalyze(docTopo);

			//for MessageLink
			topoAnaly.mlAnalyze(docTopo);
		}
	}*/
}
