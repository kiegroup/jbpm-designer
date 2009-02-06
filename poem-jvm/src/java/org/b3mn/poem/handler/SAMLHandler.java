/***************************************
 * Copyright (c) 2008
 * Bjoern Wagner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ****************************************/

package org.b3mn.poem.handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.b3mn.poem.Identity;
import org.b3mn.poem.business.User;
import org.b3mn.poem.util.HandlerWithoutModelContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.fraunhofer.fokus.jic.identity.Claim;
import de.fraunhofer.fokus.jic.identity.ClaimIdentity;

@HandlerWithoutModelContext(uri = "/saml")
public class SAMLHandler extends HandlerBase {

	/**
	 * This handler provides authentification by SAML token. This handler is
	 * part of the seminar Identitymanagement in SOA in winter term 08/09
	 * 
	 * @author Nicolas Peters
	 */

	private static final String USER_SESSION_IDENTIFIER = "openid";
	
	private String uniqueIdentifierClaimUri;
	
	private boolean onlyUseTrustedIssuers;
	
	private ArrayList<String> trustedIssuers;
	
	private ArrayList<String> certificates;

	@Override
	public void init() {
		//Load properties
		FileInputStream in;
		
		//initialize properties from backend.properties
		try {
			trustedIssuers = new ArrayList<String>();
			certificates = new ArrayList<String>();
			
			in = new FileInputStream(this.getBackendRootDirectory() + "/WEB-INF/backend.properties");
			Properties props = new Properties();
			props.load(in);
			in.close();
			
			//get unique identifier claim uri
			uniqueIdentifierClaimUri = props.getProperty("org.b3mn.poem.handler.SAMLHandler.uniqueIdentifier");
			System.out.println("ID: " + uniqueIdentifierClaimUri);
			try {
				URL uIdUrl = new URL(uniqueIdentifierClaimUri);
			} catch (MalformedURLException mue) {
				uniqueIdentifierClaimUri = null;
				System.out.println("ID malformed");
			}
			
			//get flag, if only trusted issuers should be used
			String onlyUseTrusted = props.getProperty("org.b3mn.poem.handler.SAMLHandler.onlyUseTrustedIssuers");
			if("false".equals(onlyUseTrusted)) {
				onlyUseTrustedIssuers = false;
			} else {
				onlyUseTrustedIssuers = true;
				
				//get list of trusted issuers
				String trustedIssuersString = props.getProperty("org.b3mn.poem.handler.SAMLHandler.trustedIssuers");
				String[] trustedIssuersArray = trustedIssuersString.split(";");
				for(int i = 0; i < trustedIssuersArray.length; i++) {
					String issuer = trustedIssuersArray[i];
					try {
						System.out.println(issuer);
						URL issuerUrl = new URL(issuer);
						trustedIssuers.add(issuer);
					} catch (MalformedURLException mue) {
						throw new Exception("Issuer " + issuer + " is not a valid URL.");
					}
				}
				
				//get list of certificates
				String certificatesString = props.getProperty("org.b3mn.poem.handler.SAMLHandler.trustedIssuerCertificates");
				String[] certificatesArray = certificatesString.split(";");
				for(int i = 0; i < certificatesArray.length; i++) {
					String cert = certificatesArray[i];
					File certFile = new File(this.getBackendRootDirectory() + "/WEB-INF/" + cert);
					if(certFile.exists() && certFile.isFile()) {
						certificates.add(certFile.getCanonicalPath());
					} else {
						throw new Exception("Certicate " + cert + " does not exist!");
					}
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * SAML Authentication via GET
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res,
			Identity subject, Identity object) throws Exception {
		doPost(req, res, subject, object);

	}
	
	/**
	 * SAML Authentification via POST
	 * 
	 * Expects two request parameters:
	 * 	returnto: the url that is return to after login/logout is processed
	 *  logout: If this parameter has the value "true", a logout will be performed
	 *  xmltoken: the SAML token
	 *  
	 * In case of a logout, no SAML token has to be passed.
	 *  
	 * The SAMLHandler redirects to the url specified by the returnto parameter and adds
	 * the following parameters:
	 *  login: "true" or "false"
	 *  logout: "true" or "false"
	 *  
	 *  In case a login was successful, login is "true" and logout is "false".
	 *  If a logout has been performed, login is "false" and logout is "true".
	 *  In case of an error, e.g. no SAML token passed for login, both parameters
	 *  are "false".
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res,
			Identity subject, Identity object) throws Exception {
		
		//get current user
		String userId = (String) req.getSession().getAttribute(USER_SESSION_IDENTIFIER);
		
		User curUser = null;
		if(userId != null && !userId.equals("") && !userId.equals(getPublicUser())) {
			curUser = new User(userId);
		}
		
		//logout current user so that future requests to Oryx
		//will fail, in case the SAML authentication fails
		//req.getSession().removeAttribute(USER_SESSION_IDENTIFIER);
		if(curUser != null) {
			curUser.removeAuthenticationAttributes(this.getServletContext(), req, res);
		}
		
		//get returnto url
		String retUrl = req.getParameter("returnto");
		
		//throw an exception if no returnto url is passed
		if(retUrl == null) {
			throw new Exception("SAMLHandler: No 'returnto' parameter passed!");
		}
		
		//get the claim uri that has to be used as the user's unique identifier
		if(this.uniqueIdentifierClaimUri == null) {
			throw new Exception("SAMLHandler: No unique identifier claim URI in configuration defined!");
		}
		
		try {
			// If logout is true, just return
			if ("true".equals(req.getParameter("logout"))) {
				
				User u = new User(getPublicUser());
				u.login(req, res);
				
				res.setStatus(200);
				retUrl = this.getReturnToUrl(retUrl, false, true, null);
			} else { //login
				//Get ClaimIdentity that contains all claims
				ClaimIdentity _id = (ClaimIdentity) req
						.getAttribute("userdata");

				// get unique user id
				Claim userUniqueIdClaim = _id.get(uniqueIdentifierClaimUri).get(0);
				
				if(onlyUseTrustedIssuers) {
					//get issuer
					String _issuerType = "urn:oasis:names:tc:SAML:2.0:assertion:NameIDType";
					String issuer = userUniqueIdClaim.getIsuuer().get(_issuerType).get(0).getValue();
					
					//check, if issuer is trusted
					if(!trustedIssuers.contains(issuer)) {
						throw new Exception("SAMLHandler: Issuer of SAML token is not a trusted issuer!");
					}
					
					//validate signature (if it is not a self-issued token)
					if(!issuer.equals("http://schemas.xmlsoap.org/ws/2005/05/identity/issuer/self")) {
						int issuerIndex = trustedIssuers.indexOf(issuer);
						try {
							String certFileName = certificates.get(issuerIndex);
							RSAPublicKey pubKey = getPubKeyFromFile(certFileName);
							String token = req.getParameter("xmltoken");
							if(!validateSignature(token, pubKey)) {
								throw new Exception("SAMLHandler: Validating signature of issuer " + issuer + " failed.");
							}
						} catch (IndexOutOfBoundsException e){
							
						}
					}
				}
				
				//get user's unique id
				String userUniqueId = userUniqueIdClaim.getValue();

				// identify user
				Identity subj = Identity.instance(userUniqueId);

				// create new user, if the user does not exist
				if (subj == null) {
					// Create new user with open id attributes
					User.CreateNewUser(userUniqueId);
				}
				
				//get user
				User user = new User(userUniqueId);
				user.login(req, res);
				
				// create authentification token
				UUID authToken = UUID.randomUUID();

				user.addAuthenticationAttributes(this.getServletContext(), req, res, authToken);
				
				// redirect to return to page
				res.setStatus(200);
				retUrl = this.getReturnToUrl(retUrl, true, false, authToken.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			res.setStatus(200);
			retUrl = this.getReturnToUrl(retUrl, false, false, null);
		}

		//Redirect the request
		res.sendRedirect(retUrl);
	}
	
	
	/**
	 * Creates the URL the request will be redirected.
	 * Adds two query parameters login and logout.
	 * 
	 * @param url
	 * @param login
	 * @param logout
	 * @return a string that represents the redirect URL
	 */
	private String getReturnToUrl(String url, boolean login, boolean logout, String authToken) {
		String res = url;
		String lin = null, lout = null;
		if(login) {
			lin = "true";
		} else {
			lin = "false";
		}
		if(logout) {
			lout = "true";
		} else {
			lout = "false";
		}
		
		if(res.contains("?")) {
			res += "&";
		} else {
			res += "?";
		}
		
		res += "login=" + lin + "&logout=" + lout;

		if(authToken != null) {
			res += "&authtoken=" + authToken;
		}
		
		return res;
	}
	
	/**
	 * Checks the signature against the issuer's public key.
	 * The JInfoCard Framework only validates the signature with the
	 * key specified in the token. To be more secure, we check it again
	 * with a public key that is stored on our server for that issuer.
	 * 
	 * @param token The SAML token
	 * @param pubKey The issuer's public key
	 * @return validation result
	 */
	protected Boolean validateSignature( String token, RSAPublicKey pubKey ){

		try {
			if(pubKey == null){
				return false;
			}
			
			// Get the DOM-Structure from token
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
			// be namespace aware
			dbf.setNamespaceAware(true); 
			DocumentBuilder builder = dbf.newDocumentBuilder();  
			Document doc = builder.parse(new ByteArrayInputStream(token.getBytes())); 
	 
			// Get the signature node
			NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			if (nl.getLength() == 0) {
				throw new Exception("Cannot find Signature element");
			} 
	
			// Creating a validation context
			DOMValidateContext valContext = new DOMValidateContext(pubKey, nl.item(0)); 
	
			// Unmarshal the xml signature
			XMLSignatureFactory factory = 
				  XMLSignatureFactory.getInstance("DOM"); 
			 
			XMLSignature signature = 
				  factory.unmarshalXMLSignature(valContext); 
	
			// Validate
			return signature.validate(valContext); 
		
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param filename of certificate
	 * @return The certificate's public key
	 */
	protected RSAPublicKey getPubKeyFromFile( String filename ) {


		try {
			
			InputStream inStream = new FileInputStream(filename);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert =(X509Certificate)cf.generateCertificate(inStream);
			inStream.close();

			RSAPublicKey pubKey = (RSAPublicKey) cert.getPublicKey(); 
			
			return pubKey;

		} catch (Exception e) {
			
		}

		return null;

	}
}
