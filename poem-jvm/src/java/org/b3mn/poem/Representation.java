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

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.b3mn.poem.util.JsonErdfTransformation;
import org.b3mn.poem.util.RdfJsonTransformation;
import org.hibernate.CacheMode;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.springframework.util.FileCopyUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
    
    /**
     * @deprecated Replaced by {@link #getErdf()}.
     */
    @Deprecated public String getContent() {
    	return getErdf();
	}
    
    protected String getPureContent(){
    	String content = (String)Persistance.getSession().
		createSQLQuery("SELECT content.erdf FROM content WHERE id=:id").
		setLong("id", id).uniqueResult();
		Persistance.commit();
		return content;
    }
    
    static protected boolean isJson(String content){
    	return !content.startsWith("<");
    }

    /**
     * @deprecated Try to avoid using this function because {@link #getJson()} is more future-proof
     */
    @Deprecated public String getErdf(){
    	String content = getPureContent();
    	if(isJson(content)){
    		return jsonToErdf(content);
    	} else {
    		return content;
    	}
    }
    
    public String getJson(){
    	return getJson("");
    }
    
    /**
     * 
     * @param serverUrl Used if json must be extracted from erdf and if a stencil set doesn't have any absolute URL
     * @return
     */
    public String getJson(String serverUrl){
//    	try {
//			test(serverUrl);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	String content = getPureContent();
    	if(isJson(content)){
    		return content;
    	} else {  			
    		return erdfToJson(content, serverUrl);
    	}
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
	
	public byte[] getPngLarge() {
    	byte[] pngData = (byte[]) Persistance.getSession().
		createSQLQuery("SELECT content.png_large FROM content WHERE id=:id").
		setLong("id", id).uniqueResult();
	Persistance.commit();
	return pngData;
	}
	
	public void setPngLarge(byte[] pngData) {
		// Create and execute UPDATE query
		Persistance.getSession().
		createSQLQuery("UPDATE content SET png_large=:data WHERE id=:id").
		setBinary("data", pngData).
		setLong("id", id).executeUpdate();
		Persistance.commit();
	}
	
	public byte[] getPngSmall() {
    	byte[] pngData = (byte[]) Persistance.getSession().
		createSQLQuery("SELECT content.png_small FROM content WHERE id=:id").
		setLong("id", id).uniqueResult();
	Persistance.commit();
	return pngData;
	}
	
	public void setPngSmall(byte[] pngData) {
		// Create and execute UPDATE query
		Persistance.getSession().
		createSQLQuery("UPDATE content SET png_small=:data WHERE id=:id").
		setBinary("data", pngData).
		setLong("id", id).executeUpdate();
		Persistance.commit();
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
	        if(svg != null) {
	        	rep.setSvg(svg);
	        	rep.setPngLarge(null); // Pngs has to be rerendered on the next request
	        	rep.setPngSmall(null);
	        }
	        
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
		String content = this.getPureContent();
		if(isJson(content)){
			return erdfToRdf(jsonToErdf(content));
		} else {
			return erdfToRdf(content);
		}
		
	}
	
	public String getErdf(javax.servlet.ServletContext context) throws TransformerException {
		String content = this.getPureContent();
		if(isJson(content)){
			return jsonToErdf(content);
		} else {
			return content;
		}
		
	}
	
	protected static String erdfToRdf(String erdf) throws TransformerException{
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
		"</head><body>" + erdf + "</body></html>";
        
		InputStream xsltStream = Dispatcher.servletContext.getResourceAsStream("/WEB-INF/lib/extract-rdf.xsl");
        Source xsltSource = new StreamSource(xsltStream);
        Source erdfSource = new StreamSource(new StringReader(serializedDOM));

        TransformerFactory transFact =
                TransformerFactory.newInstance();
        Transformer trans = transFact.newTransformer(xsltSource);
        StringWriter output = new StringWriter();
        trans.transform(erdfSource, new StreamResult(output));
		return output.toString();
	}
	
	protected static String erdfToJson(String erdf, String serverUrl){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document rdfDoc = builder.parse(new ByteArrayInputStream(erdfToRdf(erdf).getBytes()));
			return RdfJsonTransformation.toJson(rdfDoc, serverUrl).toString();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected static String jsonToErdf(String json){
		return new JsonErdfTransformation(json).toString();
	}
	public static void test(String serverUrl) throws IOException {
    	ScrollableResults contents = Persistance.getSession().createSQLQuery("SELECT content.erdf FROM content")
        .setCacheMode(CacheMode.IGNORE)
        .scroll(ScrollMode.FORWARD_ONLY);

    	int count=0;
    	while ( contents.next() ) {
    	    String content = (String) contents.get(0);
    	    Writer writer=new FileWriter("C:/Program Files/Apache Software Foundation/Tomcat 6.0/webapps/json/"+count+".json");

        	if(isJson(content)){
        		//content;
        	} else {  		
        		content=erdfToJson(content, serverUrl);

        	}
    	    if(content==null || content.indexOf("BPMNDiagram")==-1)
    	    	continue;
    	    String[] labels=new String[]{"name", "documentation", "title", "description", "pooldocumentation", "conditionexpression", "text", "state"};
    	    for(String prop:labels)
    	    	content=content.replaceAll("\""+prop+"\":\"([\\w\\W^\"]*?)\"", "\""+prop+"\":\"\"");

    	    
    	    FileCopyUtils.copy(content,writer);
        	writer.close();
        	
    	    if ( ++count % 10 == 0 ) {
    	        //flush a batch of updates and release memory:
    	    	Persistance.getSession().flush();
    	    	Persistance.getSession().clear();
    	    }
    	    System.gc();
    	}
    	Persistance.commit();

	}
}