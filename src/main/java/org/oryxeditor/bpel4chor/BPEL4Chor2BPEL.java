/**
 * Copyright (c) 2009-2010 Changhua Li
 * 				 2010 Oliver Kopp
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

package org.oryxeditor.bpel4chor;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BPEL4Chor2BPEL {
	
	/**
	 * Adds WSDL specific elements to the given BPEL4Chor choreography
	 * 
	 * FIXED: add WSDLs to result set (see BPEL4Chor2BPELExport)
	 * 
	 * @param docPBD the BPEL process definitions to modify - These documents are MODIFIED!
	 * @return docPBD with WSDL specific elements 
	 * @throws Exception 
	 */
	public List<Document> convert(Element elGround, Element elTopo, List<Document> docPBD) throws Exception {
		BPEL4Chor2BPELTopologyAnalyze topoAnaly = new BPEL4Chor2BPELTopologyAnalyze();
		BPEL4Chor2BPELGroundingAnalyze grouAnaly = new BPEL4Chor2BPELGroundingAnalyze();
		BPEL4Chor2BPELPBDConversion pbdCon = new BPEL4Chor2BPELPBDConversion();
		BPEL4Chor2BPELWSDLCreate wsdlCre = new BPEL4Chor2BPELWSDLCreate();

		//topology analyze
		topoAnaly.nsAnalyze(elTopo);
		topoAnaly.paTypeAnalyze(elTopo);
		topoAnaly.paAnalyze(elTopo);
		topoAnaly.mlAnalyze(elTopo);
		topoAnaly.getMl2BindSenderToMap(elTopo);
			
		grouAnaly.namespacePrefixSet = topoAnaly.namespacePrefixSet;    
		grouAnaly.namespaceSet = topoAnaly.namespaceSet;				
		grouAnaly.ns2prefixMap = topoAnaly.ns2prefixMap;				
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
		grouAnaly.nsAnalyze(elGround); // add more namespace prefixes to ns2prefixMap
		grouAnaly.mlAnalyze(elGround);
		grouAnaly.propertyAnalyze(elGround);
			
		pbdCon.scopeSet = topoAnaly.scopeSet;							
		pbdCon.processSet = topoAnaly.processSet;						
		pbdCon.topologyNS = topoAnaly.topologyNS;						
		pbdCon.forEach2setMap = topoAnaly.forEach2setMap;				
		pbdCon.paSet = topoAnaly.paSet;									
		pbdCon.pa2scopeMap = topoAnaly.pa2scopeMap; 					
		pbdCon.ns2prefixMap = grouAnaly.ns2prefixMap;					
		pbdCon.namespacePrefixSet = grouAnaly.namespacePrefixSet;		
		pbdCon.plSet = grouAnaly.plSet;									
		pbdCon.sc2plMap = grouAnaly.sc2plMap;							
		pbdCon.pl2plTypeMap = grouAnaly.pl2plTypeMap;					
		pbdCon.pl2myRoleMap = grouAnaly.pl2myRoleMap;					
		pbdCon.pl2partnerRoleMap = grouAnaly.pl2partnerRoleMap;			
		pbdCon.messageConstructsSet = grouAnaly.messageConstructsSet;	
		pbdCon.mc2plMap = grouAnaly.mc2plMap;							
		pbdCon.ml2mcMap = grouAnaly.ml2mcMap;							
		pbdCon.messageLinkSet = grouAnaly.messageLinkSet;				
		pbdCon.ml2ptMap = grouAnaly.ml2ptMap;							
		pbdCon.ml2opMap = grouAnaly.ml2opMap;							
		pbdCon.corrPropName2propertyMap = grouAnaly.corrPropName2propertyMap;  
		pbdCon.property2nsprefixOfPropMap = grouAnaly.property2nsprefixOfPropMap; 
		
		List<Document> newDocumentList = new ArrayList<Document>();
		List<String> processList = new ArrayList<String>();			// a List to store the process names 

		// PBD conversion
		for (Document currentDoc: docPBD) {
			pbdCon.convertPBD(currentDoc);
			processList.add(pbdCon.processName);
			newDocumentList.add(currentDoc);
		}
		
		// WSDL files creation
		for (int i = 0; i < docPBD.size(); i++){
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document newDoc = builder.newDocument();

			//WSDL creation
			wsdlCre.currentDocument = newDoc;
			wsdlCre.topologyNS = topoAnaly.topologyNS;
			wsdlCre.plTypeSet = grouAnaly.plTypeSet;
			wsdlCre.comm2pltMap = grouAnaly.comm2pltMap;
			wsdlCre.ptSet = grouAnaly.ptSet;						
			wsdlCre.ns2prefixMap = grouAnaly.ns2prefixMap;
			wsdlCre.processName = processList.get(i);
			wsdlCre.declarePartnerLinkTypes((Element)newDoc.getFirstChild());
			newDocumentList.add(newDoc);
		}
		// return the newDocumentList (the first half is converted processes, the last half is created wsdl)
		return newDocumentList;
	}
}
