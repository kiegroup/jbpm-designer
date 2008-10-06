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

import java.util.Collection;
import java.util.List;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Persistance;
import org.b3mn.poem.Setting;
import org.hibernate.classic.Session;


public class ConfigurationManager {

	// Hide the constructor to prevent unauthorized instantiation of the singleton class
	protected ConfigurationManager() {}
	
	// This class handles the singleton instantiation. According to en.wikipedia.org this is the best way
	// considering performance and thread-safety. 
	private static class SingletonHolder {
		private static final ConfigurationManager INSTANCE = new ConfigurationManager();
	}
	
	// Returns the singleton instance of the class
	public static ConfigurationManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public String getUserSetting(Identity subject, String key) {
		String value = (String) Persistance.getSession()
			.createSQLQuery("SELECT value FROM setting WHERE subject_id=:subject_id AND key=:key")
			.setString("key", key)
			.setInteger("subject_id", subject.getId())
			.uniqueResult();
		
		Persistance.commit();
		return value;
	}
	
	
	public void setUserSetting(Identity subject, String key, String value) {
		Session session = Persistance.getSession();
		try {
			Setting setting = (Setting) session 
				.createSQLQuery("SELECT {setting.*} FROM {setting} WHERE subject_id=:subject_id AND key=:key")
				.addEntity("setting", Setting.class)
				.setString("key", key)
				.setInteger("subject_id", subject.getId())
				.uniqueResult();
			
			// Check if the setting already exists for this user
			if (setting != null) {
				// Set the new value 
				setting.setValue(value);
			} else {
				// Create a new setting 
				setting = new Setting();
				setting.setSubject_id(subject.getId());
				setting.setKey(key);
				setting.setValue(value);
			}
			// Save changes to database
			session.save(setting);
			Persistance.commit();
		}
		catch (Exception e) {
			session.close();
		}
	}
	
	public Collection<String> getUserSettingKeys(Identity subject) {
		List result = (List) Persistance.getSession()
			.createSQLQuery("SELECT key FROM setting WHERE subject_id=:subject_id")
			.setInteger("subject_id", subject.getId())
			.list();
		
		Persistance.commit();
		if (result == null) {
			return null;
		} else {
			Collection<String> keys = new java.util.ArrayList<String>();
			for (Object key : result) {
				if (key.getClass().equals(String.class)) {
					String strKey = (String) key;
					keys.add(strKey);
				}
			}
			return keys;
		}		
	}
	
	public String getServerSetting(String key) {
		String value = (String) Persistance.getSession()
			.createSQLQuery("SELECT value FROM setting WHERE subject_id=0 AND key=:key")
			.setString("key", key)
			.uniqueResult();
		
		Persistance.commit();
		return value;
	}
	
	
	public void setServerSetting(String key, String value) {
		Session session = Persistance.getSession();
		try {
			Setting setting = (Setting) session 
				.createSQLQuery("SELECT {setting.*} FROM {setting} WHERE subject_id=0 AND key=:key")
				.addEntity("setting", Setting.class)
				.setString("key", key)
				.uniqueResult();
			
			// Check if the setting already exists for this user
			if (setting != null) {
				// Set the new value 
				setting.setValue(value);
			} else {
				// Create a new setting 
				setting = new Setting();
				setting.setSubject_id(0);
				setting.setKey(key);
				setting.setValue(value);
			}
			// Save changes to database
			session.save(setting);
			Persistance.commit();
		}
		catch (Exception e) {
			session.close();
		}
	}
	
	public Collection<String> getServerSettingKeys(Identity subject) {
		List result = (List) Persistance.getSession()
			.createSQLQuery("SELECT key FROM setting WHERE subject_id=0")
			.list();
		
		Persistance.commit();
		if (result == null) {
			return null;
		} else {
			Collection<String> keys = new java.util.ArrayList<String>();
			for (Object key : result) {
				if (key.getClass().equals(String.class)) {
					String strKey = (String) key;
					keys.add(strKey);
				}
			}
			return keys;
		}		
	}
	
	
}
