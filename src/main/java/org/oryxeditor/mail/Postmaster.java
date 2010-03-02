package org.oryxeditor.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Postmaster {
	// Modify web.xml to set defaults.
	private static String SMTP_HOST_NAME;
	private static String SMTP_EMAIL;
	private static String SMTP_AUTH_USER;
	private static String SMTP_AUTH_PWD;
	private static int SMTP_PORT;

	private static boolean SMTP_DEBUG = true;
	
	public final static String defaultTemplatePath = "/WEB-INF/classes/org/oryxeditor/mail"; 

	public static void main(String[] args) {
		Message mes = new Message();
		mes.setMessage("Hi!");
		mes.setRecipients(SMTP_EMAIL);
		mes.setSubject("Test");
		mes.setFrom("Test");
		Postmaster.deliver(mes);
	}

	static public void deliver(Message message) {
		Properties props = new Properties();

		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.port", SMTP_PORT);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.quitwait", "false");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(SMTP_AUTH_USER,
								SMTP_AUTH_PWD);
					}
				});

		if (SMTP_DEBUG)
			session.setDebug(true);

		MimeMessage msg = new MimeMessage(session);
		try {
			msg.setText(message.getMessage());
			msg.setSubject(message.getSubject());
			msg.setFrom(new InternetAddress(SMTP_EMAIL, message.getFrom()));
			msg.addRecipient(javax.mail.Message.RecipientType.TO,
					new InternetAddress(message.getRecipients()));
			Transport.send(msg);
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	static public void configure(javax.servlet.ServletContext c){
		SMTP_HOST_NAME = c.getInitParameter("SMTP_HOST_NAME");
		SMTP_EMAIL = c.getInitParameter("SMTP_EMAIL");
		SMTP_AUTH_USER = c.getInitParameter("SMTP_AUTH_USER");
		SMTP_AUTH_PWD = c.getInitParameter("SMTP_AUTH_PWD");
		SMTP_PORT = Integer.valueOf(c.getInitParameter("SMTP_PORT"));
		SMTP_DEBUG = Boolean.valueOf(c.getInitParameter("SMTP_DEBUG"));
	}
}