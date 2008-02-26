package org.b3mn.poem;

import javax.persistence.*;

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
	public static Identity instance(String openid) {
		return (Identity) Persistance.getSession().
			createSQLQuery("select {identity.*} FROM identity(?)")
			.addEntity("identity", Identity.class)
			.setString(0, openid)
			.uniqueResult();
	}

}
