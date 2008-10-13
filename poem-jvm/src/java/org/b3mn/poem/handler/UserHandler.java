/***************************************
 * Copyright (c) 2008
 * Bjoern Wagner
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

package org.b3mn.poem.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Subject;
import org.b3mn.poem.manager.UserManager;
import org.b3mn.poem.util.HandlerWithoutModelContext;
import org.b3mn.poem.util.JavaBeanJsonTransformation;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.emory.mathcs.backport.java.util.Arrays;

@HandlerWithoutModelContext(uri="/user")
public class UserHandler extends  HandlerBase {

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {

		
		JSONObject userObject = new JSONObject();
		JSONObject currentLanguage = new JSONObject();
		currentLanguage.put("languagecode", request.getSession().getAttribute("languagecode"));
		currentLanguage.put("countrycode", request.getSession().getAttribute("countrycode"));

		userObject.put("currentLanguage", currentLanguage);
		
		// if the user is public read  data from session
		if (subject.getUri().equals(getPublicUser())) {
			userObject.put("fullname", getPublicUser());
		} else {
			UserManager um = UserManager.getInstance();
			userObject.put("fullname", um.getUser(subject).getFullname());	
		}
		response.getWriter().print(userObject.toString());
		response.setStatus(200);
	}
	
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		String languageCode = request.getParameter("languagecode");
		String contrycode = request.getParameter("countrycode");
		
		if (languageCode != null) {	
			
			if (languageCode != null) {
				request.getSession().setAttribute("languagecode", languageCode);
				request.getSession().setAttribute("countrycode", contrycode);
				// If the user isn't public update database too
				if (!subject.getUri().equals(getPublicUser())) {
					UserManager um = UserManager.getInstance();
					Subject user =  um.getUser(subject);
					user.setLanguageCode(languageCode);
					user.setCountryCode(contrycode);
					um.updateUser(user);
					response.setStatus(200);
				}
			}
		}
	}
}
