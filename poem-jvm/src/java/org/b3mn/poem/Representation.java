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
import org.hibernate.HibernateException;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

@Entity
public class Representation {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
	private int ident_id;
    private String mime_type;
    private String language;
    private String title;
    private String type;
    private String summary;
    private Date created;
    private Date updated;

    // The following 5 methods access the "content" table instead of "representation". 
    // This isn't nice style but it was necessary to separate meta and content data, in order 
    // to provide adequate performance. 
    
    // Check whether the content already exists
    private boolean contentExists() {
    	boolean result = Persistance.getSession().
    		createSQLQuery("SELECT id FROM content WHERE id=:id").
    		setLong("id", this.id).list().size() != 0;
    	Persistance.commit();
    	return result;
    }
    
    public String getContent() {
    	String content = (String)Persistance.getSession().
    		createSQLQuery("SELECT content.erdf FROM content WHERE id=:id").
    		setLong("id", id).uniqueResult();
    	Persistance.commit();
    	return content;
	}
    
	public void setContent(String erdf) {
		// Check whether the content already exists
		if (contentExists()) {
			// Create and execute UPDATE query
			Persistance.getSession().
			createSQLQuery("UPDATE content SET erdf=:erdf WHERE id=:id").
			setString("erdf", erdf).
			setLong("id", id).executeUpdate();
			Persistance.commit();
		}
		else {
			// Create and execute INSERT query
			Persistance.getSession().
			createSQLQuery("INSERT INTO content (id, erdf, svg) VALUES (:id, :erdf, '')").
			setLong("id", id).
			setString("erdf", erdf).executeUpdate();
			Persistance.commit();
		}
	}
	
    public String getSvg() {
    	String svg = (String)Persistance.getSession().
			createSQLQuery("SELECT content.svg FROM content WHERE id=:id").
			setLong("id", id).uniqueResult();
    	Persistance.commit();
    	return svg;
	}
    
	public void setSvg(String svg) {
		// Check whether the content already exists
		if (contentExists()) {
			// Create and execute UPDATE query
			Persistance.getSession().
			createSQLQuery("UPDATE content SET svg=:svg WHERE id=:id").
			setString("svg", svg).
			setLong("id", id).executeUpdate();
			Persistance.commit();
		}
		else {
			// Create and execute INSERT query
			Persistance.getSession().
			createSQLQuery("INSERT INTO content (id, svg, erdf) VALUES (:id, :svg, '')").
			setLong("id", id).
			setString("svg", svg).executeUpdate();
			Persistance.commit();
		}
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
	public int getIdent_id() {
		return ident_id;
	}
	public void setIdent_id(int ident_id) {
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
	
    public static void update(int id, String title, String summary, String content, String svg) {
    	
    	Representation rep = (Representation) Persistance.getSession()
    	.createSQLQuery("select {representation.*} from {representation} where ident_id = :ident_id")
		.addEntity("representation", Representation.class)
	    .setInteger("ident_id", id).uniqueResult();

        try {
	        Date date = new Date(System.currentTimeMillis());
	        
	        if(title != null) rep.setTitle(title);
	        if(summary != null) rep.setSummary(summary);
	        
	        rep.setUpdated(date);
	        Persistance.getSession().flush();
	        Persistance.commit();
	        
	        if(content != null) rep.setContent(content);
	        if(svg != null) rep.setSvg(svg);
	    }
        catch(HibernateException ex) {
            System.err.println(ex.getMessage());
        }
    } 
	
	public static Representation instance(Identity model) {
		Representation representation = new Representation();
		representation.setIdent_id(model.getId());
		representation.setCreated(new Date(System.currentTimeMillis()));
		representation.setUpdated(new Date(System.currentTimeMillis()));
		return representation;
	}
	
	public String getRdf(javax.servlet.ServletContext context) throws TransformerException {
		String serializedDOM = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<html xmlns=\"http://www.w3.org/1999/xhtml\" " +
		"xmlns:b3mn=\"http://b3mn.org/2007/b3mn\" " +
		"xmlns:ext=\"http://b3mn.org/2007/ext\" " +
		"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "  +
		"xmlns:atom=\"http://b3mn.org/2007/atom+xhtml\">" +
		"<head profile=\"http://purl.org/NET/erdf/profile\">" +
		"<link rel=\"schema.dc\" href=\"http://purl.org/dc/elements/1.1/\" />" +
		"<link rel=\"schema.dcTerms\" href=\"http://purl.org/dc/terms/ \" />" +
		"<link rel=\"schema.b3mn\" href=\"http://b3mn.org\" />" +
		"<link rel=\"schema.oryx\" href=\"http://oryx-editor.org/\" />" +
		"<link rel=\"schema.raziel\" href=\"http://raziel.org/\" />" +
		"</head><body>" + this.getContent() + "</body></html>";
        

		InputStream xsltStream = context.getResourceAsStream("/extract-rdf.xsl");
        Source xsltSource = new StreamSource(xsltStream);
        Source erdfSource = new StreamSource(new StringReader(serializedDOM));

        TransformerFactory transFact =
                TransformerFactory.newInstance();
        Transformer trans = transFact.newTransformer(xsltSource);
        StringWriter output = new StringWriter();
        trans.transform(erdfSource, new StreamResult(output));
		return output.toString();
		
	}
	
}