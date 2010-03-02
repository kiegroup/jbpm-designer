package org.oryxeditor.mail;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletContext;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class Message {
	private String recipients;
	
	private String subject;
	
	private String message;
	
	private String from;
	
	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	public void setMessageFromTemplate(String path, String tpl, VelocityContext context, ServletContext servletContext){
		VelocityEngine ve = new VelocityEngine();
		ve.setApplicationAttribute("javax.servlet.ServletContext", servletContext);
        ve.setProperty("resource.loader", "webapp");
        ve.setProperty("webapp.resource.loader.class", "org.apache.velocity.tools.view.servlet.WebappLoader");
        ve.setProperty("webapp.resource.loader.path", path);
        
		try {
			Template t = ve.getTemplate( tpl );
			
	        /* now render the template into a StringWriter */
	        StringWriter writer = new StringWriter();
	        t.merge( context, writer );
	        
	        this.message = writer.toString();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		} catch (MethodInvocationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseErrorException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}


	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}
}
