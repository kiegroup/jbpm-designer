package org.b3mn.poem.handler;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Subject;
import org.b3mn.poem.manager.ConfigurationManager;
import org.b3mn.poem.manager.UserManager;
import org.b3mn.poem.util.JavaBeanJsonTransformation;
import org.json.JSONObject;

import edu.emory.mathcs.backport.java.util.Arrays;



public class UserHandler extends  HandlerBase {

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		// if the user is public read  data from session
		if (subject.getUri().equals(getPublicUser())) {
			JSONObject publicObject = new JSONObject();
			publicObject.put("languagecode", request.getSession().getAttribute("languagecode"));
			publicObject.put("countrycode", request.getSession().getAttribute("countrycode"));
			publicObject.put("fullname", "public");
			publicObject.write(response.getWriter());
		} else {
			UserManager um = UserManager.getInstance();
			String[] attributes = {"fullname", "email", "languagecode", "countrycode"};
			String jsonString = JavaBeanJsonTransformation.toJsonObject(
					um.getUser(subject), Arrays.asList(attributes)).toString();
			response.getWriter().print(jsonString);
		}
		response.setStatus(200);
	}
	
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		String jsonString = request.getParameter("json_data");
		if (jsonString != null) {	
			// Write language data to session			
			JSONObject jsonObject = new JSONObject(jsonString);
			request.getSession().setAttribute("languagecode", jsonObject.get("languagecode"));
			request.getSession().setAttribute("countrycode", jsonObject.get("countrycode"));
			// If the user isn't public update database too
			if (subject.getUri().equals(getPublicUser())) {
				UserManager um = UserManager.getInstance();
				Subject user = (Subject) JavaBeanJsonTransformation.
				updateJavaBean(jsonString, um.getUser(subject));
				if (user != null) {
					um.updateUser(user);
					response.setStatus(200);
				}
			}
		}
	}
}
