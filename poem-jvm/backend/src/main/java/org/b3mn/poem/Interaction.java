package org.b3mn.poem;

import javax.persistence.*;

@Entity
public class Interaction {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String subject;
	private boolean subject_descend;
   	private String object;
   	private boolean object_descend;
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
	public void delete() {
		Persistance.getSession().delete(this);
	}
	public long save() {
		long id = 0;
		Persistance.getSession().save(this);
		return id;
	}
	public static Interaction getInteraction(long id) {
		return (Interaction) Persistance.getSession().
		createSQLQuery("select {interaction.*} from {interaction} where id = :id").
		addEntity("interaction", Interaction.class).
		setLong("id", id).uniqueResult();
	}
	public static boolean exist(String subject, String object, String term) {
		if(Persistance.getSession().
		   createSQLQuery("select * from interaction where subject = :subject " +
		   "and object = :object and term = :term").
		   setString("subject", subject).
		   setString("object", object).
		   setString("term", term) == null)
			return false;
		else return true;
	}


}
