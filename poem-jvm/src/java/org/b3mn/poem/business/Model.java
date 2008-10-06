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
import java.util.Date;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Persistance;
import org.b3mn.poem.Representation;
import org.b3mn.poem.TagDefinition;
import org.b3mn.poem.TagRelation;
import org.hibernate.classic.Session;

public class Model extends BusinessObject {
	
	protected Representation representation;
	
	protected TagDefinition getTagDefintion(User user, String name) {
		TagDefinition tagDefinition = (TagDefinition) Persistance.getSession()
		.createSQLQuery("SELECT {tag_definition.*} FROM {tag_definition} WHERE subject_id=:subject_id AND name=:name")
		.addEntity("tag_definition", TagDefinition.class)
		.setInteger("subject_id", user.getId())
		.setString("name", name)
		.uniqueResult();
		
		Persistance.commit();
		return tagDefinition;
	}
	
	protected TagRelation getTagRelation(User user, String name) {
		TagRelation tagRelation = (TagRelation) Persistance.getSession()
		.createSQLQuery("SELECT {tag_relation.*} FROM {tag_relation}, tag_definition WHERE tag_definition.subject_id=:subject_id AND tag_definition.name=:name AND tag_definition.id=tag_relation.tag_id AND tag_relation.object_id=:object_id")
		.addEntity("tag_relation", TagRelation.class)
		.setInteger("subject_id", user.getId())
		.setInteger("object_id", this.getId())
		.setString("name", name)
		.uniqueResult();
		
		Persistance.commit();
		return tagRelation;
	}
	
	public Model(int id) {
		identity = Identity.instance(id);
		representation = identity.read();
	}
	
	public Model(String uri) {
		identity = Identity.instance(uri);
		representation = identity.read();
	}
	
	public int getId() {
		return this.identity.getId();
	}

	public String getUri() {
		return this.identity.getUri();
	}
	
	public String getTitle() {
		return this.representation.getTitle();
	}
	
	public void setTitle(String title) {
		this.representation.setTitle(title);
	}
	
	public String getSummary() {
		return this.representation.getSummary();
	}
	
	public void setSummary(String summary) {
		this.representation.setSummary(summary);
	}
	
	public String getType() {
		return this.representation.getType();
	}
	
	public void setType(String type) {
		this.representation.setType(type);
	}
	
	public Date getCreationDate() {
		return this.representation.getCreated();
	}
	
	public void setCreationDate(Date creationDate) {
		this.representation.setCreated(creationDate);
	}
	
	public Date getLastUpdate() {
		return this.representation.getUpdated();
	}
	
	public void setLastUpdate(Date lastUpdate) {
		this.representation.setCreated(lastUpdate);
	}
	
	// TODO: implement erdf and svg db access here instead of the representation class
	public String geteRdf() {
		return representation.getContent();
	}
	
	public void seteRdf(String eRdf) {
		representation.setContent(eRdf);
	}
	
	public String getSvg() {
		return representation.getSvg();
	}
	
	public void setSvg(String svg) {
		representation.setSvg(svg);
	}
	
	public Collection<String> getPublicTags(User user) {
		Collection<?> tags = Persistance.getSession().
			createSQLQuery("SELECT DISTINCT ON(tag_definition.name) tag_definition.name " 
			+ "FROM tag_definition, tag_relation " 
			+ "WHERE tag_definition.id=tag_relation.tag_id AND "
			+ "tag_relation.object_id=:object_id AND "
			+ "NOT tag_definition.subject_id=:subject_id")
			.setInteger("object_id", this.identity.getId())
			.setInteger("subject_id", user.getId())
			.list();
		
		Persistance.commit();
		return toStringCollection(tags);
	}
	
	public Collection<String> getUserTags(User user) {
		Collection<?> tags = Persistance.getSession().
			createSQLQuery("SELECT DISTINCT ON(tag_definition.name) tag_definition.name " 
			+ "FROM tag_definition, tag_relation " 
			+ "WHERE tag_definition.id=tag_relation.tag_id AND "
			+ "tag_relation.object_id=:object_id AND "
			+ "tag_definition.subject_id=:subject_id")
			.setInteger("object_id", this.identity.getId())
			.setInteger("subject_id", user.getId())
			.list();
		
		Persistance.commit();
		return toStringCollection(tags);
	}
	
	public void addTag(User user, String tag) {
		// TODO check access right of the user
		// If the user hasn't already tagged the model with this tag
		if (!this.getUserTags(user).contains(tag)) {
			
			TagDefinition tagDefinition = this.getTagDefintion(user, tag);
			Session session = Persistance.getSession();
			// User uses this tag for the first time
			if (tagDefinition == null) {
				// Create definition
				tagDefinition = new TagDefinition();
				tagDefinition.setName(tag);
				tagDefinition.setSubject_id(user.getId());
				session.save(tagDefinition);
			}
			// Create tag relation
			TagRelation tagRelation = new TagRelation();
			tagRelation.setObject_id(this.getId());
			tagRelation.setTag_id(tagDefinition.getId());
			session.save(tagRelation);
			Persistance.commit();
		}
	}
	
	public void removeTag(User user, String tag) {
		TagRelation tagRel = this.getTagRelation(user, tag);	
		if (tagRel != null) {
			Persistance.getSession().delete(tagRel);
			Persistance.commit();
			// TODO: remove tag_defintion if no other models are tagged with this tag anymore
		}
	}
	
	
}
