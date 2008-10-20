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

package org.b3mn.poem.business;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Persistance;
import org.b3mn.poem.Subject;
import org.b3mn.poem.handler.HandlerBase;
import org.b3mn.poem.manager.ConfigurationManager;



public class User extends BusinessObject {
	
	protected Subject subject;
	
	public User(int id) throws Exception {
		super(Identity.instance(id));
		ensureSubject();
	}
	
	public User(String openId) throws Exception {
		super(Identity.instance(openId));
		ensureSubject();
	}
	
	public User(Identity identity) throws Exception {
		super(identity);
		ensureSubject();
	}
	
	private void ensureSubject() {
		this.subject = (Subject) Persistance.getSession()
		.createSQLQuery("SELECT {subject.*} FROM {subject} WHERE ident_id=:id")
		.addEntity("subject", Subject.class)
		.setInteger("id", this.getId())
		.uniqueResult();
	
		Persistance.commit();
		
		if (this.subject == null) {
			this.subject = Subject.createNewSubject(this.getId());
		}
	}
	
	public static boolean openIdExists(String openId) {
		/*try {
			URL url = new URL(openId.split("?")[0]); // Remove possible query parameters
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.connect();
			return true;
		} catch (Exception e) {};
		return false;*/
		return true;
	}
	
	public static User CreateNewUser(String openid, String nickname, String fullname, String email, Date dob, String gender, 
			String postcode, String country, String language, String languageCode, String countryCode, 
			String password, String visibility) throws Exception {
		
		// Create identity and put it at the right place in the tree
		Identity identity =  Identity.ensureSubject(openid);
		Subject.createNewSubject(identity.getId(), countryCode, dob, email, fullname, gender, languageCode, nickname, password, postcode, visibility);
		
		return new User(identity);
	}
	
	public static User CreateNewUser(String openid) throws Exception {
		
		// Create identity and put it at the right place in the tree
		Identity identity =  Identity.ensureSubject(openid);
		Subject.createNewSubject(identity.getId());
		
		return new User(identity);
	}
	
	public void login(HttpServletRequest request, HttpServletResponse response) {
		
		// Try to get data from the database first if the user isn't public		
		if (this.getOpenId().equals(HandlerBase.getPublicUser())) {	 
			// Update login statistics 
			this.subject.setLastLogin(new Date());
			this.subject.setLoginCount(this.subject.getLoginCount() + 1);
			this.subject.update();
		} 

			
		String languagecode = this.getLanguageCode(request);
		String countrycode = this.getCountryCode(request);

		// Save data in the session and cookie
		request.getSession().setAttribute("languagecode", languagecode);
		request.getSession().setAttribute("countrycode", countrycode);
		response.addCookie(new Cookie("languagecode", languagecode));
		response.addCookie(new Cookie("countrycode", countrycode));
	}
	
	public String getLanguageCode(HttpServletRequest request) {
		return this.getLanguageOrCountryCode(request, true);
	}
	
	public String getCountryCode(HttpServletRequest request) {
		return this.getLanguageOrCountryCode(request, false);
	}
	
	private String getLanguageOrCountryCode(HttpServletRequest request, boolean isLanguage) {
		String languagecode = (String) request.getSession().getAttribute("languagecode");
		String countrycode = (String) request.getSession().getAttribute("countrycode");
		
		if ((languagecode == null) && (this.getOpenId().equals(HandlerBase.getPublicUser()))) {	
			languagecode = this.subject.getLanguageCode();			
			countrycode = this.subject.getCountryCode();
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
		
		if (isLanguage) return languagecode;
		else return countrycode;
	}
	
	public void setLanguage(String languagecode, String countrycode, 
			HttpServletRequest request, HttpServletResponse response) {
		
		if (languagecode == null) return;
		
		if (this.getOpenId().equals(HandlerBase.getPublicUser())) {	
			this.subject.setLanguageCode(languagecode);
			this.subject.setCountryCode(countrycode);
			this.subject.update();
		}
			
		// Save data in the session and cookie
		if (request != null) {
			request.getSession().setAttribute("languagecode", languagecode);
			request.getSession().setAttribute("countrycode", countrycode);
		}
		if (response != null) {
			response.addCookie(new Cookie("languagecode", languagecode));
			response.addCookie(new Cookie("countrycode", countrycode));
		}
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
	
	public String getOpenId() {
		return this.identity.getUri();
	}
	
	public Collection<Model> getModels() {
		List<Model> models = new ArrayList<Model>();
		List<?> identities = Persistance.getSession()
			.createSQLQuery("SELECT DISTINCT ON(identity.id) identity.* FROM identity, access "
					+ "WHERE access.subject_id=:id AND access.object_id=identity.id") 
					.addEntity("identity", Identity.class)
					.setInteger("id", this.identity.getId())
					.list();
		
		Persistance.commit();
		for (Object o : identities) {
			if (o instanceof Identity) {
				try {
					models.add(new Model(((Identity) o).getId()));
				} catch (Exception e) {}
			}
		}
		return models;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> getModelUris() {
		Collection<String> modelUris = Persistance.getSession()
			.createSQLQuery("SELECT identity.uri FROM identity, access WHERE " +
					"identity.id=access.object_id AND access.subject_id=:subject_id")
			.setInteger("subject_id", getId())
			.list();
		Persistance.commit();
		return modelUris;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> getTags() {
		List<String> tags = Persistance.getSession()
				.createSQLQuery("SELECT DISTINCT tag_definition.name FROM tag_definition "
						+ "WHERE tag_definition.subject_id=:id") 
						.setInteger("id", getId())
						.list();
		Persistance.commit();
		return tags;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> getFriendOpenIds() {
		List<String> friends = Persistance.getSession()
		.createSQLQuery("SELECT DISTINCT identity.uri FROM friend, identity WHERE " +
				"(friend.subject_id=:subject_id AND friend.friend_id=identity.id) OR " +
				"(friend.subject_id=identity.id AND friend.friend_id=:subject_id)")
				.setInteger("subject_id", this.getId())
				.list();
		
		Persistance.commit();
		return friends;
	}
	
	public void addFriend(String openId) {
		if (this.getFriendOpenIds().contains(openId) || 
				openId.equals(HandlerBase.getPublicUser()) ||
				openId.equals("ownership") || 
				openId.equals(this.getOpenId()) ||
				this.getOpenId().equals(HandlerBase.getPublicUser()))
			return;
		
		Identity friend = Identity.instance(openId);
		Persistance.getSession()
		.createSQLQuery("INSERT INTO friend (subject_id, friend_id) VALUES (:subject_id, :friend_id)")
			.setInteger("subject_id", this.getId())
			.setInteger("friend_id", friend.getId())
			.executeUpdate();
		Persistance.commit();
	}
	
}
