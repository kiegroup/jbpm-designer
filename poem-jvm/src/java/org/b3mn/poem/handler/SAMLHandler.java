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

import java.io.FileInputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.business.User;
import org.b3mn.poem.util.HandlerWithoutModelContext;
import org.opensaml.Configuration;
import org.opensaml.saml2.encryption.Decrypter;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.encryption.InlineEncryptedKeyResolver;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.credential.BasicCredential;
import org.opensaml.xml.security.keyinfo.StaticKeyInfoCredentialResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

	@Override
	public void init() {
		//Load properties
		FileInputStream in;
		
		//initialize properties from backend.properties
		try {
			trustedIssuers = new ArrayList<String>();
			
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
						System.out.println("issuer malformed");
					}
				}
			}
			
		} catch (Exception e) {
			
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
		
		//logout current user so that future requests to Oryx
		//will fail, in case the SAML authentication fails
		req.getSession().removeAttribute(USER_SESSION_IDENTIFIER);
		
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
				
				res.setStatus(200);
				retUrl = this.getReturnToUrl(retUrl, false, true);
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

				// login
				user.login(req, res);

				// bind session to authenticated user
				req.getSession().setAttribute(USER_SESSION_IDENTIFIER,
						user.getOpenId());

				// set identifier
				req.setAttribute("identifier", user.getOpenId());

				// redirect to return to page
				res.setStatus(200);
				retUrl = this.getReturnToUrl(retUrl, true, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			res.setStatus(200);
			retUrl = this.getReturnToUrl(retUrl, false, false);
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
	private String getReturnToUrl(String url, boolean login, boolean logout) {
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

		return res;
	}
}
