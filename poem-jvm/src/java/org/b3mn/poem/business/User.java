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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Persistance;
import org.b3mn.poem.Subject;
import org.b3mn.poem.manager.UserManager;


public class User extends BusinessObject {
	
	protected Subject subject;
	
	public User(int id) {
		this.identity = Identity.instance(id);
		// TODO: implement get user in this class
		this.subject = UserManager.getInstance().getUser(identity);
	}
	
	public User(String openId) {
		this.identity = Identity.instance(openId);
		// TODO: implement get user in this class
		this.subject = UserManager.getInstance().getUser(openId);
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
	
}
