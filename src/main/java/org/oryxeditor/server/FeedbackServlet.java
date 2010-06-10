package org.oryxeditor.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.oryxeditor.mail.Message;
import org.oryxeditor.mail.Postmaster;

public class FeedbackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException  {
  		res.setStatus(200);	
  		
  		try {  			
  			Message message = new Message();
  			message.setSubject(req.getParameter("subject"));
  			message.setFrom("Feedback");
  			message.setRecipients(getServletContext().getInitParameter("FEEDBACK_RECIPIENT_EMAIL"));
  			
  			VelocityContext context = new VelocityContext();
  			context.put("environment", req.getParameter("environment"));
  			context.put("model", req.getParameter("model"));
  			context.put("description", req.getParameter("description"));
  			context.put("email", req.getParameter("email"));
  			context.put("name", req.getParameter("name"));
  			
  			message.setMessageFromTemplate(Postmaster.defaultTemplatePath, "feedback.mail.vm", context, getServletContext());
  			
  			Postmaster.configure(getServletContext());
  			Postmaster.deliver(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
