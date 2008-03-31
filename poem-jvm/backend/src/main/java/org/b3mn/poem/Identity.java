package org.b3mn.poem;

import javax.persistence.*;

import java.util.List;
import java.util.Iterator;
import java.util.Date;

@Entity
public class Identity {
        
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String uri;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public static Identity instance(String uri) {
		return (Identity) Persistance.getSession().
			createSQLQuery("select {identity.*} FROM {identity} where uri=?")
			.addEntity("identity", Identity.class)
			.setString(0, uri)
			.uniqueResult();
	}
	
	public static Identity instance(int id) {
		return (Identity) Persistance.getSession().
			createSQLQuery("select {identity.*} FROM {identity} where id=:id")
			.addEntity("identity", Identity.class)
			.setInteger("id", id)
			.uniqueResult();
	}
	
	public static Identity newModel(Identity owner, String title, String type, String mime_type, String language, String summary, String content) {
			
			Identity identity = (Identity) Persistance.getSession().
			createSQLQuery("select {identity.*} from identity(?)")
			.addEntity("identity", Identity.class).setString(0, "/data/model/").uniqueResult();
			identity.setUri("/data/model/" + identity.getId());
			Persistance.getSession().save(identity);
			
			Structure.instance(identity.getId(), owner.getUserHierarchy());
			
			Representation representation = Representation.instance(identity);
			representation.setType(type);
			representation.setTitle(title);
			representation.setSummary(summary);
			representation.setLanguage(language);
			representation.setMime_type(mime_type);
			representation.setContent(content);
			Persistance.getSession().save(representation);
			return identity;
			
	}
	
	public static Identity ensureSubject(String openid) {
		
		Identity userroot = instance("ownership");
		Identity identity = (Identity) Persistance.getSession().
		createSQLQuery("select {identity.*} from identity(?)")
		.addEntity("identity", Identity.class).
		setString(0, openid).uniqueResult();
		
		Structure.instance(identity.getId(), userroot.getUserHierarchy());
		
		return identity;	
	}
	
	public static Identity newUser(String openid, String hierarchy) {

		Identity identity = (Identity) Persistance.getSession().
		createSQLQuery("select {identity.*} from identity(?)")
		.addEntity("identity", Identity.class).
		setString(0, openid).uniqueResult();
		
		Structure.instance(identity.getId(), hierarchy);
		
		return identity;	
	}
	
	@SuppressWarnings("unchecked")
	public List<Representation> getModels(String type, Date from, Date to) {
		return (List<Representation>) Persistance.getSession().
		createSQLQuery("select DISTINCT ON(i.id) r.* from access as a, identity as i, representation as r" +
					" where (a.subject_name=:subject or a.subject_name='public')" +
					" and r.type like :type and r.updated >= :from and r.updated <= :to" +
					" and i.id=a.object_id and i.id=r.ident_id")
		.addEntity("representation", Representation.class)
	    .setString("subject", this.getUri())
	    .setString("type", type)
	    .setDate("from", from)
	    .setDate("to", to)
	    .list();
	}
	
	
	
	@SuppressWarnings("unchecked")
	public List<Access> getAccess() {
		return (List<Access>) Persistance.getSession().
		createSQLQuery("select DISTINCT ON(context_name) {access.*} from {access} where object_name=?")
		.addEntity("access", Access.class)
	    .setString(0, this.getUri()).list();
	}
	
	@SuppressWarnings("unchecked")
	public Access access(String openId, String rel) {
		List<Access> access = Persistance.getSession().
		createSQLQuery("select {access.*} from {access} where (subject_name = :subject or subject_name = 'public') and object_name = :object and plugin_relation = :relation")
		.addEntity("access", Access.class)
		.setString("subject", openId)
	    .setString("object", this.getUri())
	    .setString("relation", rel).list();
		Persistance.commit();
		Iterator<Access> rights = access.iterator();
		Access writer = null; 
		Access reader = null;
		if(rights.hasNext()) {
			while(rights.hasNext()) {
				Access item = rights.next();
				String term = item.getAccess_term();
				if(term.equalsIgnoreCase("owner"))
					return item;
				else if(term.equalsIgnoreCase("write"))
					writer = item;
				else if(term.equalsIgnoreCase("read"))
					reader = item;
			}
			
			if(writer != null) return writer;
			else if(reader != null) return reader;
			else return access.get(0);
		}
		return null;

	}
	
	@SuppressWarnings("unchecked")
	public List<Plugin> getServlets() {
		return (List<Plugin>)Persistance.getSession().
		createSQLQuery("select {plugin.*} from {plugin}")
		.addEntity("plugin", Plugin.class)
		.list();
	}
	
	public Representation read() {
		return (Representation)Persistance.getSession().
		createSQLQuery("select {representation.*} from {representation} where ident_id = :ident_id")
		.addEntity("representation", Representation.class)
	    .setInteger("ident_id", this.id).uniqueResult();
	}
	
	public String getModelHierarchy() {
		return Persistance.getSession().
		createSQLQuery("select structure.hierarchy from identity, structure " +
						"where identity.id = :id and identity.id = structure.ident_id").
		setInteger("id", this.id).uniqueResult().toString();
	}
	public String getUserHierarchy() {
		return Persistance.getSession().
		createSQLQuery("select structure.hierarchy from identity, structure " +
						"where identity.id = :id and identity.id = structure.ident_id " +
						"and structure.hierarchy like 'U2_%'").
		setInteger("id", this.id).uniqueResult().toString();
	}
	
	public void delete() {
		Persistance.getSession().delete(this);
	}
}
