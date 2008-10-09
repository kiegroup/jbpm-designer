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

package org.b3mn.poem.manager;

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Persistance;
import org.b3mn.poem.Subject;
import org.b3mn.poem.handler.HandlerBase;



public class UserManager {

	// Hide the constructor to prevent unauthorized instantiation of the singleton class
	protected UserManager() {}
	
	// This class handles the singleton instantiation. According to en.wikipedia.org this is the best way
	// considering performance and thread-safety. 
	private static class SingletonHolder {
		private static final UserManager INSTANCE = new UserManager();
	}
	
	// Returns the singleton instance of the class
	public static UserManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public Subject CreateNewUser(String openid, String nickname, String fullname, String email, Date dob, String gender, 
			String postcode, String country, String language, String languageCode, String countryCode, 
			String password, String visibility) {
		
		// Create identity and put it at the right place in the tree
		Identity identity =  Identity.ensureSubject(openid);
		
		// Initialize user object
		Subject user = new Subject();
		user.setIdent_id(identity.getId());
		user.setNickname(nickname);
		user.setFullname(fullname);
		user.setEmail(email);
		user.setDob(dob);
		user.setGender(gender);
		user.setPostcode(postcode);
		user.setLanguageCode(languageCode);
		user.setCountryCode(countryCode);
		user.setPassword(password);
		user.setVisibility(visibility);
		
		Date loginDate = new Date();
		user.setFirstLogin(loginDate);
		user.setLastLogin(loginDate);
		user.setLoginCount(0);
		
		Persistance.getSession().save(user);
		Persistance.commit();
		return user;
	}

	public Subject getUser(String openid) {
		Subject user = (Subject) Persistance.getSession()
			.createSQLQuery("SELECT {subject.*} FROM {subject}, identity WHERE subject.ident_id=identity.id AND identity.uri=:openid")
			.addEntity("subject", Subject.class)
			.setString("openid", openid)
			.uniqueResult();
		
		Persistance.commit();
		return user;
	}
	
	public Subject getUser(int id) {
		Subject user = (Subject) Persistance.getSession()
			.createSQLQuery("SELECT {subject.*} FROM {subject} WHERE ident_id=:id")
			.addEntity("subject", Subject.class)
			.setInteger("id", id)
			.uniqueResult();
		
		Persistance.commit();
		return user;
	}

	public Subject getUser(Identity identity) {
		Subject user = (Subject) Persistance.getSession()
			.createSQLQuery("SELECT {subject.*} FROM {subject} WHERE ident_id=:id")
			.addEntity("subject", Subject.class)
			.setInteger("id", identity.getId())
			.uniqueResult();
		
		Persistance.commit();
		return user;
	}
	
	public void updateUser(Subject user) {
		Persistance.getSession().update(user);
		Persistance.commit();
	}

	public void deleteUser(Subject user) {
		// Delete identity row. user is deleted by database on delete cascade
		Persistance.getSession().delete(Identity.instance(user.getIdent_id()));
		Persistance.commit();
	}
	
	public void deleteUser(int id) {
		// Delete identity row. user is deleted by database on delete cascade
		Persistance.getSession().delete(Identity.instance(id));
		Persistance.commit();
	}
	
	public Subject login(String openid, HttpServletRequest request, HttpServletResponse response) {
		Subject user = null;
		String languagecode = null;
		String countrycode = null;
		// Try to get data from the database first if the user isn't public		
		if (!openid.equals(HandlerBase.getPublicUser())) {	 
			// Update login statistics 
			user = this.getUser(openid);
			user.setLastLogin(new Date());
			user.setLoginCount(user.getLoginCount() + 1);
			this.updateUser(user);
			languagecode = user.getLanguageCode();
			countrycode = user.getCountryCode();
		} 
		// Try to get data from a cookie
		if ((languagecode == null) && (request.getCookies() != null)){
			for (Cookie cookie : request.getCookies()) {
				if (cookie.getName().equals("languagecode")) {
					languagecode = cookie.getValue();
				}
				if (cookie.getName().equals("countrycode")) {
					countrycode = cookie.getValue();
				}
			}
		}
			
		// If the langugae isn't set try to get default from server config
		if (languagecode == null) {
			languagecode =  getDefaultLanguageCode();
			countrycode = getDefaultCountryCode();
		}

		// Save data in the session and cookie
		request.getSession().setAttribute("languagecode", languagecode);
		request.getSession().setAttribute("countrycode", countrycode);
		response.addCookie(new Cookie("languagecode", languagecode));
		response.addCookie(new Cookie("countrycode", countrycode));
		
		return user;
	}
	
	public boolean userExists(String openid) {
		return this.getUser(openid) != null;
	}
	
	public String getDefaultLanguageCode() {
		ConfigurationManager cm = ConfigurationManager.getInstance();
		// Try to get code from the server configuration
		String defaultLanguageCode = cm.getServerSetting("UserManager.DefaultLanguageCode");
		if (defaultLanguageCode != null) {
			return defaultLanguageCode;
		} else {
			return "en"; // Hard code default language English if no other is configured
		}
	}
	
	public String getDefaultCountryCode() {
		ConfigurationManager cm = ConfigurationManager.getInstance();
		// Try to get code from the server configuration
		String defaultCountryCode = cm.getServerSetting("UserManager.DefaultCountryCode");
		if (defaultCountryCode != null) {
			return defaultCountryCode;
		} else {
			return "us"; // Hard code default country us if no other is configured
		}
	}
}
