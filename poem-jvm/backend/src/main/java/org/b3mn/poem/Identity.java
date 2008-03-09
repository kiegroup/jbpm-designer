package org.b3mn.poem;

import javax.persistence.*;
import java.util.List;
import java.util.Iterator;

@Entity
public class Identity {

	@Id
	private long id;
	private String uri;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
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
			createSQLQuery("select {identity.*} FROM identity(?)")
			.addEntity("identity", Identity.class)
			.setString(0, uri)
			.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public static List<Access> subject(String openid) {
		return Persistance.getSession().
		createSQLQuery("select {access.*} from {access} where subject_name=?")
		.addEntity("access", Access.class)
	    .setString(0, openid).list();
	}
	
	@SuppressWarnings("unchecked")
	public static List<Access> object(String uri) {
		return (List<Access>) Persistance.getSession().
		createSQLQuery("select {access.*} from {access} where object_name=?")
		.addEntity("access", Access.class)
	    .setString(0, uri).list();
	}
	
	@SuppressWarnings("unchecked")
	public Access access(String openId, String rel) {
		List<Access> access = Persistance.getSession().
		createSQLQuery("select {access.*} from {access} where subject_name = :subject and object_name = :object and plugin_relation = :relation")
		.addEntity("access", Access.class)
		.setString("subject", openId)
	    .setString("object", this.getUri())
	    .setString("relation", rel).list();
		
		Iterator<Access> rights = access.iterator();
		Access writer = new Access(); 
		Access reader = new Access();
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
	
	public Representation read() {
		return (Representation)Persistance.getSession().
		createSQLQuery("select {representation.*} from {representation} where ident_id = :ident_id")
		.addEntity("representation", Representation.class)
	    .setLong("ident_id", this.id).uniqueResult();
	}
	
	/*public static Representation create() {
		// insert tralala stored procedure by hagen
	}

	
	public Representation update(Representation rep) {
		// update representation values rep where rep.ident=i.id and i.id=this.id
	}
	
	public void delete() {
		// delete Identity on cascade
	}*/
}
