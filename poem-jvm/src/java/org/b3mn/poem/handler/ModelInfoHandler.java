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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Persistance;
import org.b3mn.poem.business.Model;
import org.b3mn.poem.util.FilterMethod;
import org.b3mn.poem.util.HandlerWithModelContext;
import org.b3mn.poem.util.JavaBeanJsonTransformation;
import org.b3mn.poem.util.SortMethod;
import org.json.JSONException;
import org.json.JSONObject;

@HandlerWithModelContext(uri="/meta")
public class ModelInfoHandler extends  HandlerBase {

	private void writeResponse(HttpServletRequest request,
			HttpServletResponse response, Identity object, Identity subject) throws Exception,
			JSONException, IOException {
		
		Model model = new Model(object.getId());

		
		Collection<String> attributes = new ArrayList<String>();
		attributes.add("title");			
		attributes.add("summary");
		attributes.add("type");
		attributes.add("creationDate");
		attributes.add("lastUpdate");
		attributes.add("author");
		JSONObject data = JavaBeanJsonTransformation.toJsonObject(model, attributes);
		data.put("thumbnailUri", this.getServerPath(request) + model.getUri() + "/thumbnail");
		data.put("pngUri", this.getServerPath(request) + model.getUri() + "/png");
		data.write(response.getWriter());
		response.setStatus(200);
	}
	
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		if (object != null) {
			writeResponse(request, response, object, subject);
		}
	}
	
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {

		String title = request.getParameter("title");
		String summary = request.getParameter("summary");
		Model model = new Model(object);
		if (title != null) model.setTitle(title);
		if (summary != null) model.setSummary(summary);
		this.writeResponse(request, response, object, subject);
	}

	@SuppressWarnings({ "unchecked" })
	@FilterMethod(FilterName="type")
	public static Collection<String> filterByModelType(Identity subject, String params) {
		String typeQuery = "";
		for (String type : params.split(",")) {
			type = removeSpaces(type);
			typeQuery+="representation.type='"+type+"' OR ";
		}
		// Remove last OR
		if (typeQuery.length() > 4) {
			typeQuery = typeQuery.substring(0, typeQuery.length() - 4);
		}
		
		List<String> results = Persistance.getSession()
			.createSQLQuery("SELECT identity.uri FROM identity, access, representation "
			+ "WHERE (access.subject_name='public' OR access.subject_id=:subject_id) AND access.object_id=identity.id AND representation.ident_id=access.object_id "
			+ "AND (" + typeQuery + ")")
			.setInteger("subject_id", subject.getId())
			.list();
		
		Persistance.commit();
		
		return results;
	}
	
	@SuppressWarnings("unchecked")
	@SortMethod(SortName="lastChange")	
	public static List<String> sortByLastChange(Identity subject) {
		List<String> results = Persistance.getSession()
		.createSQLQuery("SELECT identity.uri FROM identity, access, representation "
		+ "WHERE (access.subject_name='public' OR access.subject_id=:subject_id) AND access.object_id=identity.id AND representation.ident_id=access.object_id " 
		+ "ORDER BY representation.updated DESC")
		.setInteger("subject_id", subject.getId())
		.list();
	
		Persistance.commit();
		return results;
	}
	
	@SuppressWarnings("unchecked")
	@SortMethod(SortName="title")	
	public static List<String> sortByTitle(Identity subject) {
		List<String> results = Persistance.getSession()
		.createSQLQuery("SELECT identity.uri FROM identity, access, representation "
		+ "WHERE (access.subject_name='public' OR access.subject_id=:subject_id) AND access.object_id=identity.id AND representation.ident_id=access.object_id " 
		+ "ORDER BY representation.title")
		.setInteger("subject_id", subject.getId())
		.list();
	
		Persistance.commit();
		return results;
	}
	
}
