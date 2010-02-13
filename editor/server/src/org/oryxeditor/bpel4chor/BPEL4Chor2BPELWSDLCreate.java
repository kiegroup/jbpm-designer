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
 * It is the creation of WSDL file of BPEL, which was designed in the Studien Arbeit
 * of Peter Reimann(2008)
 */

public class BPEL4Chor2BPELWSDLCreate extends BPEL4Chor2BPELPBDConversion{
	
	protected Document currentDocument;
	protected Set<String> nsprefixSet;
	/**************************create a matched WSDL file to current PBD***************************/
	/** 
	 * Algorithm 3.19 Procedure declarePartnerLinkTypes
	 * 
	 * @param {Element} definitions  The Tag <wsdl:definitions>
	 */
	private void declarePartnerLinkTypes(Element definitions){
		// the input definitions points on the <wsdl:definitions> tag of the newly created WSDL file
		// remove the first Child of currentDocument
		currentDocument.removeChild(definitions);
		
		String plt;												// partnerLinkType, inherits of NCName
		Element partnerLinkType, role;							// single BPEL constructs
		Set<Object> aSet = new HashSet<Object>();				// a list of participant references
		String b;												// a participant, inherits of NCName
		String c,d;												// portType, inherits of QName
		Set<String> nsprefixSet = this.nsprefixSet;				// a list of name space prefixes referencing to the name
																// spaces of the WSDL definitions of port types used by
		// create a new child node of currentDocument
		definitions = currentDocument.createElement("wsdl:definitions");
		currentDocument.appendChild(definitions);
		
		// add a name attribute to the <definitions> tag
		definitions.setAttribute("name", "partnerLinkTypes");
		// declare the target name space for the WSDL definitions
		String targetNS = topologyNS + "/partnerLinkTypes";
		// topologyNS is the target name space of the participant topology
		definitions.setAttribute("targetNamespace", targetNS);	// algorithm 3.5
		// add the name space declarations for the name space prefixes wsdl and plnk
		definitions.setAttribute("xmlns:wsdl", "http://schemas.xmlsoap.org/wsdl/");
		String plnkNS = "http://docs.oasis-open.org/wsbpel/2.0/plnktype";
		definitions.setAttribute("xmlns:plnk", plnkNS);
		
		// add a partner link type declaration for each element of PLType
		System.out.println("plTypeSet is: " + plTypeSet);
		if(!plTypeSet.isEmpty()){
			Iterator<String> it = plTypeSet.iterator();
			while(it.hasNext()){
				// create a new <plnk:partnerLinkType> construct
				plt = (String)it.next();
				partnerLinkType = currentDocument.createElement("plnk:partnerLinkType");
				// add a name attribute to the new partner link type declaration
				partnerLinkType.setAttribute("name", plt);
				// get the element of the relation Comm of the current partner link type
				Comm comm = fpltCommReverse(plt);
				aSet = comm.getPa1();
				c 	 = comm.getPt1();
				b	 = comm.getPa2();
				d	 = comm.getPt2();
				System.out.println(comm.getElement());
				// create the first role element and add it to the partner link type declaration
				role = declareNewRole(b,d);											// algorithm 3.20
				partnerLinkType.appendChild(role);
				// the second role must not be declared if we have c = EMPTY
				if(!c.equals(EMPTY)){
					role = declareNewRole(aSet.iterator().next().toString(), c);	// algorithm 3.20
					// in this case, A includes only one participant reference
					partnerLinkType.appendChild(role);
				}
				// add the partner link type declaration to the <definitions> construct
				definitions.appendChild(partnerLinkType);
			}
		}
		// add the declarations of the name spaces of the port type definitions
		declareNameSpaces(definitions, nsprefixSet);								// algorithm 3.8
	}
	
	/**
	 * Algorithm 3.20 Function declareNewRole
	 * 
	 * @param  {String} pa           The participant of this role
	 * @param  {String} pt           The portType of this role
	 * @return {Element} role        The tag <plnk:role>
	 */
	private Element declareNewRole(String pa, String pt){
		// the input pa is the participant reference which may be interpreted as the new role, the input pt is the port
		// type of this role
		// this function returns the new <plnk:role> construct
		Element role = currentDocument.createElement("plnk:role");		// a new <plnk:role> construct
		String pt_nsprefix = fnsprefixPT(pt);							// the name space prefix of the port type pt
		
		// add the name attribute to the role
		role.setAttribute("name", pa);									// pa is an NCName
		// add the portType attribute to the role
		role.setAttribute("portType", pt);								// pt is a QName
		
		// add the name space prefix of pt to the global list nsprefixList if it has not been added before
		if(!nsprefixSet.contains(pt_nsprefix) && !pt_nsprefix.equals(EMPTY)){
			nsprefixSet.add(pt_nsprefix);
		}
		
		// return the new <plnk:role> construct
		return role;
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
			BPEL4Chor2BPELWSDLCreate wsdlCreate = new BPEL4Chor2BPELWSDLCreate();

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
			
			//WSDL creation
			wsdlCreate.currentDocument = docPBD;
			wsdlCreate.topologyNS = topoAnaly.topologyNS;
			wsdlCreate.plTypeSet = grouAnaly.plTypeSet;
			wsdlCreate.comm2pltMap = grouAnaly.comm2pltMap;
			wsdlCreate.nsprefixSet = pbdCon.namespacePrefixSet;
			wsdlCreate.declarePartnerLinkTypes((Element)docPBD.getFirstChild());

			/**************************output of the converted PBD******************************/
			Source sourceWSDL = new DOMSource(docPBD);
			File wsdlFile = new File("/home/eysler/work/DiplomArbeit/oryx-editor/editor/server/src/org/oryxeditor/bpel4chor/testFiles/PBDConvertion.wsdl");
			Result resultWSDL = new StreamResult(wsdlFile);
			 
			// Write the converted docPBD to the file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(sourceWSDL, resultWSDL);
		}
	}
}
