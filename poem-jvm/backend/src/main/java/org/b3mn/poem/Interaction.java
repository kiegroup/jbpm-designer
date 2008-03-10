package org.b3mn.poem;

import javax.persistence.*;

@Entity
public class Interaction {
	
	@Id
	private int id;
	private String subject;
	private boolean subject_descend;
   	private String object;
   	private boolean object_descend;
   	private String scheme;
   	private String term;

   	public int getId() {
	   	return id;
   	}
   	public void setId(int id) {
	   	this.id = id;
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


}
