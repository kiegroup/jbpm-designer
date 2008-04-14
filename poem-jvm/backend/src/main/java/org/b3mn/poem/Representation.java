package org.b3mn.poem;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Representation {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
	private long ident_id;
    private String mime_type;
    private String language;
    private String title;
    private String type;
    private String summary;
    private Date created;
    private Date updated;
    private String content;
 
    
    public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getIdent_id() {
		return ident_id;
	}
	public void setIdent_id(long ident_id) {
		this.ident_id = ident_id;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getMime_type() {
		return mime_type;
	}
	public void setMime_type(String mime_type) {
		this.mime_type = mime_type;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getUpdated() {
		return updated;
	}
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	public void update() {

		Date date = new Date(System.currentTimeMillis());
		this.setUpdated(date);
		Persistance.getSession().flush();
		Persistance.commit();

	}
	
	public static Representation instance(Identity model) {
		Representation representation = new Representation();
		representation.setIdent_id(model.getId());
		representation.setCreated(new Date(System.currentTimeMillis()));
		representation.setUpdated(new Date(System.currentTimeMillis()));
		return representation;
		
	}
	
}