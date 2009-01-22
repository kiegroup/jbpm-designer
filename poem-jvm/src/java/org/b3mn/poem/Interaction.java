/***************************************
 * Copyright (c) 2008
 * Ole Eckermann
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

import javax.persistence.*;

@Entity
public class Interaction {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String subject;
	private boolean subject_descend;
   	private String object;
   	private boolean object_self;
   	private boolean object_descend;
   	private boolean object_restrict_to_parent;
   	private String scheme;
   	private String term;

   	public long getId() {
	   	return id;
   	}
   	public String getObject() {
	   	return object;
   	}
   	public void setObject(String object) {
	   	this.object = object;
   	}
	public boolean isObject_descend() {
		return object_descend;
	}
	public void setObject_descend(boolean object_descend) {
		this.object_descend = object_descend;
	}
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public boolean isSubject_descend() {
		return subject_descend;
	}
	public void setSubject_descend(boolean subject_descend) {
		this.subject_descend = subject_descend;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getPredicate() {
		return term;
	}
	public boolean isObject_self() {
		return object_self;
	}
	public void setObject_self(boolean object_self) {
		this.object_self = object_self;
	}
	public boolean isObject_restrict_to_parent() {
		return object_restrict_to_parent;
	}
	public void setObject_restrict_to_parent(boolean object_restrict_to_parent) {
		this.object_restrict_to_parent = object_restrict_to_parent;
	}
	public void delete() {
		Persistance.getSession().delete(this);
		Persistance.commit();
	}
	public long save() {
		Persistance.getSession().saveOrUpdate(this);
		Persistance.commit();
		return this.getId();
	}
	public String getUri() {
		return "/access?id=" +  this.getId();
	}
	public static Interaction getInteraction(long id) {
		Interaction inter = (Interaction) Persistance.getSession().
		createSQLQuery("select {interaction.*} from {interaction} where id = :id").
		addEntity("interaction", Interaction.class).
		setLong("id", id).uniqueResult();
		Persistance.commit();
		return inter;
	}
	public static Interaction exist(String subject, String object, String term) {
		Interaction right = (Interaction)Persistance.getSession().
		   createSQLQuery("select {interaction.*} from {interaction} where subject = :subject " +
		   "and object = :object and term = :term").
		   addEntity("interaction", Interaction.class).
		   setString("subject", subject).
		   setString("object", object).
		   setString("term", term).uniqueResult();
		Persistance.commit();
		if(right == null)
			return null;
		else return right;
	}
	public static Interaction exist(long id) {
		
		Interaction right = (Interaction)Persistance.getSession().
		   createSQLQuery("select {interaction.*} from {interaction} where id = :id").
		   addEntity("interaction", Interaction.class).
		   setLong("id", id).uniqueResult();
		Persistance.commit();
		if(right == null) return null;
		else return right;
	}
}
