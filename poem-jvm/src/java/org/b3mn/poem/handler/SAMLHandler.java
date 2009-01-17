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
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.business.User;
import org.b3mn.poem.util.HandlerWithoutModelContext;

import de.fraunhofer.fokus.jic.identity.ClaimIdentity;
import de.fraunhofer.fokus.jic.identity.ClaimUris;

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

	@Override
	public void init() {
		//Load properties
		FileInputStream in;
		
		try {
			in = new FileInputStream(this.getBackendRootDirectory() + "/WEB-INF/backend.properties");
			Properties props = new Properties();
			props.load(in);
			in.close();
			uniqueIdentifierClaimUri = props.getProperty("org.b3mn.poem.handler.SAMLHandler.UniqueIdentifier");
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
		
		//get returnto url
		String retUrl = req.getParameter("returnto");
		
		//throw an exception if no returnto url is passed
		if(retUrl == null) {
			throw new Exception("SAMLHandler: No 'returnto' parameter passed!");
		}
		
		if(this.uniqueIdentifierClaimUri == null) {
			throw new Exception("SAMLHandler: No unique identifier claim URI in configuration defined!");
		}
		
		try {
			// If logout is true remove session attribute
			if ("true".equals(req.getParameter("logout"))) {
				req.getSession().removeAttribute(USER_SESSION_IDENTIFIER);

				res.setStatus(200); // redirect status???
				res.getWriter().write(this.getReturnToPage(retUrl, false, true));
				return;
			} else {
				ClaimIdentity _id = (ClaimIdentity) req
						.getAttribute("userdata");

				// get email address
				String emailAddress = _id.get(ClaimUris.EMAIL_ADDRESS).get(0)
						.toString();

				// identify user
				Identity subj = Identity.instance(emailAddress);

				// create new user if the user does not exist
				if (subj == null) {
					// Create new user with open id attributes
					User.CreateNewUser(emailAddress);
				}
				
				//get user
				User user = new User(emailAddress);

				// login
				user.login(req, res);

				// bind session to authenticated user
				req.getSession().setAttribute(USER_SESSION_IDENTIFIER,
						user.getOpenId());

				// set identifier
				req.setAttribute("identifier", user.getOpenId());

				// redirect to return to page
				res.setStatus(200);
				res.getWriter().write(this.getReturnToPage(retUrl, true, false));
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			res.setStatus(200);
			res.getWriter().write(this.getReturnToPage(retUrl, false, false));
			return;
		}

	}
	
	
	/**
	 * Creates an HTML with a form that automatically redirects to 
	 * the resource via GET specified by url.
	 * Parameters login and logout are passed.
	 * 
	 * @param url
	 * @param login
	 * @param logout
	 * @return a string that represents a html document
	 */
	private String getReturnToPage(String url, boolean login, boolean logout) {
		
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
		
		String page = "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
				+ "<head>"
				+ "<title>SAML Redirection</title>"
				+ "</head>"
				+ "<body onload=\"document.forms['saml-form-redirection'].submit();\">"
				+ "<form name=\"saml-form-redirection\" action=\"" + url
				+ "\" method=\"get\" accept-charset=\"utf-8\" >"
				+ "<input type=\"hidden\" name=\"login\" value=\"" + lin + "\"/>"
				+ "<input type=\"hidden\" name=\"logout\" value=\"" + lout + "\"/>"
				+ "<button type=\"submit\">Continue...</button>" + "</form>"
				+ "</body>" + "</html>";

		return page;
	}
}
