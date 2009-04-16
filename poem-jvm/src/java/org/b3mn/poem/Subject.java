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

package org.b3mn.poem;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity @Table(name="subject")
public class Subject {
        
	@Id 
	private int ident_id;
	
	// OpenID Attributes
	private String nickname;
	private String email;
	private String fullname;
	private Date dob;
	private String gender;
	private String postcode;

	
	// Oryx Server Attributes
	@Column(name="first_login")
	private Date firstLogin;
	
	@Column(name="last_login")
	private Date lastLogin;
	
	@Column(name="login_count")
	private int loginCount;
	
	@Column(name="language_code")
	private String languageCode;
	
	@Column(name="country_code")
	private String countryCode;
	
	private String password;
	private String visibility;
	
	public Subject() {
		
	}
	
	public static Subject createNewSubject(int subjectId) {
		
		Subject subject = new Subject();

		subject.ident_id = subjectId;
		subject.firstLogin = new Date();
		subject.lastLogin = new Date();
		subject.loginCount = 0;

		Persistance.getSession().save(subject);
		Persistance.commit();
		return subject;
	}
	
	public static Subject createNewSubject(int subjectId, String countryCode, Date dob, String email, 
			String fullname, String gender, String languageCode,
			String nickname, String password, String postcode, String visibility) {
		
		Subject subject = new Subject();
		
		subject.ident_id = subjectId;
		subject.countryCode = countryCode;
		subject.dob = dob;
		subject.email = email;
		subject.firstLogin = new Date();
		subject.lastLogin = new Date();
		subject.loginCount = 0;
		subject.fullname = fullname;
		subject.gender = gender;
		subject.languageCode = languageCode;
		subject.nickname = nickname;
		subject.password = password;
		subject.postcode = postcode;
		subject.visibility = visibility;
		Persistance.getSession().save(subject);
		return subject;
	}

	public void update() {
		Persistance.getSession().update(this);
		Persistance.commit();
	}

	public int getIdent_id() {
		return ident_id;
	}
	public void setIdent_id(int ident_id) {
		this.ident_id = ident_id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public Date getFirstLogin() {
		return firstLogin;
	}
	public void setFirstLogin(Date firstLogin) {
		this.firstLogin = firstLogin;
	}
	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	public int getLoginCount() {
		return loginCount;
	}
	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}
	public String getLanguageCode() {
		return languageCode;
	}
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visisbility) {
		this.visibility = visisbility;
	}

	// Returns the open id of the user from the identity table
	public String getOpenId() {
		return Identity.instance(this.getIdent_id()).getUri();
	}
}
