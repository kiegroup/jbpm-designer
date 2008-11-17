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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Interaction;
import org.b3mn.poem.Persistance;
import org.b3mn.poem.business.Model;
import org.b3mn.poem.business.User;
import org.b3mn.poem.util.AccessRight;
import org.b3mn.poem.util.FilterMethod;
import org.b3mn.poem.util.HandlerWithModelContext;
import org.b3mn.poem.util.RestrictAccess;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@HandlerWithModelContext(uri="/access")
public class AccessHandler extends  HandlerBase {
	
	private void writeAccessRights(HttpServletResponse response, Identity object)
			throws Exception {
		Model model = new Model(object.getId());
		
		Map<String, String> accessRights = model.getAccessRights();
		
		JSONObject json = new JSONObject(accessRights);
		json.write(response.getWriter());
	}
	
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		writeAccessRights(response, object);
	}
	
	@Override
	@RestrictAccess(AccessRight.WRITE)
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {

		String openIds = request.getParameter("subject");
		String term = request.getParameter("predicate");
		// It's only allowed to set read and write rights and the public user cannot get a write right
		if ((openIds != null) && (term.equals("read") || (term.equals("write") || !subject.getUri().equals(getPublicUser())))) {
			JSONArray invalidOpenIds = new JSONArray();
			Model model = new Model(object.getId());
			for (String openId : openIds.split(",")) {
				if (!model.addAccessRight(openId, term)) {
					invalidOpenIds.put(openId);
				}
			}
			if (invalidOpenIds.length() == 0) {
				response.setStatus(200);
				this.writeAccessRights(response, object);
			} else {
				response.setStatus(404);
				response.getWriter().println(invalidOpenIds.toString());
			}
		} else {
			response.setStatus(409);
			response.getWriter().println("AccessHandler : Invalid Parameters!");
		}
	}
	
	@Override
	@RestrictAccess(AccessRight.WRITE)
    public void doDelete(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		String openId = request.getParameter("subject");
		if (openId != null) {
			Model model = new Model(object.getId());
			model.removeAccessRight(openId);
			response.setStatus(201);
			this.writeAccessRights(response, object);
			return;
		}
		response.setStatus(409);
		response.getWriter().println("AccessHandler : Invalid Parameters!");
	}
	
	@SuppressWarnings("unchecked")
	@FilterMethod(FilterName="friend")
	public static Collection<String> filterByFriends(Identity subject, String params) throws Exception {
		String typeQuery = "";
		User user = new User(subject);
		Collection<String> modelUris = user.getModelUris(); // get all models of the user
		for (String friend : params.split(",")) {
			friend = removeSpaces(friend);
			Collection<String> friendModelUris =  Persistance.getSession()
			.createSQLQuery("SELECT access.object_name FROM access "
					+ "WHERE access.subject_name=:friend_openId ")
					.setString("friend_openId", friend)
					.list();
			Persistance.commit();
			
			modelUris.retainAll(friendModelUris);
			if (modelUris.size() == 0) break;
		}
		
		return modelUris;
	}
	
	
	@SuppressWarnings("unchecked")
	@FilterMethod(FilterName="access")
	public static Collection<String> filterByAccessRight(Identity subject, String params) throws Exception {

		Collection<String> result =  Persistance.getSession()
		.createSQLQuery("SELECT access.object_name FROM access "
				+ "WHERE access.subject_id=:subject_id  AND :params LIKE '%' || access.term  || '%' "
				+ "OR access.subject_name='public' AND :params LIKE '%public%'")
				.setInteger("subject_id", subject.getId())
				.setString("params", params)
				.list();
		Persistance.commit();
		return result;
	}
}
