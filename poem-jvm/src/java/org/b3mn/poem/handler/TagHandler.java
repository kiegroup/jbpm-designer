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

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.b3mn.poem.Identity;
import org.b3mn.poem.Persistance;
import org.b3mn.poem.business.Model;
import org.b3mn.poem.business.User;
import org.b3mn.poem.util.FilterMethod;
import org.b3mn.poem.util.HandlerWithModelContext;
import org.json.JSONArray;
import org.json.JSONObject;

@HandlerWithModelContext(uri="/tags")
public class TagHandler extends HandlerBase {
	
	protected void tagsToJson(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		Model model = new Model(object.getId());
		Collection<String> tags = null;
		User user = new User(subject.getId());
		JSONArray jsonPublicTags = new JSONArray(model.getPublicTags(user));
		JSONArray jsonUserTags = new JSONArray(model.getUserTags(user));
		JSONObject jsonAllTags = new JSONObject();
		jsonAllTags.put("publicTags", jsonPublicTags);
		jsonAllTags.put("userTags", jsonUserTags);	
		jsonAllTags.write(response.getWriter());
	}
	
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		
		if (object != null) {
			tagsToJson(request, response, subject, object);
		}
	}
	
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		
		String tags = request.getParameter("tag_name");
		if (tags != null) {
			Model model = new Model(object.getId());
			User user = new User(subject.getId());
			// Separate tags by comma
			for (String tag : tags.split(",")) {
				tag = this.removeSpaces(tag);
				model.addTag(user, tag);
			}
		}
		// Return all tags of the model
		this.tagsToJson(request, response, subject, object);
	}	

	@Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		
		String tags = request.getParameter("tag_name");
		if (tags != null) {
			Model model = new Model(object.getId());
			User user = new User(subject.getId());
			// Separate tags by comma
			for (String tag : tags.split(",")) {
				tag = this.removeSpaces(tag);
				model.removeTag(user, tag);
			}
		}
		// Return all tags of the model
		this.tagsToJson(request, response, subject, object);
	}		
	
	/* Returns all modelIds of the input which are tagged with the tags passed in the params 
	 * parameter. params must be an JSON array of tags
	 */
	@SuppressWarnings("unchecked")
	@FilterMethod(FilterName="tags")
	public static Collection<String> tagFilter(Identity subject, String params) throws Exception {
		
		List<String> finalUris = Persistance.getSession()
				.createSQLQuery("SELECT DISTINCT access.object_name "
						+ "FROM  access "
						+ "WHERE (access.subject_name='public' OR access.subject_id=:subject_id) ")
						.setInteger("subject_id", subject.getId())
						.list();
		
		for (String tag : params.split(",")) {
			tag = StringEscapeUtils.unescapeHtml(tag);
			tag = removeSpaces(tag);
			
			List<String> tagUris = Persistance.getSession()
			.createSQLQuery("SELECT access.object_name "
					+ "FROM tag_relation, tag_definition, access "
					+ "WHERE tag_relation.tag_id=tag_definition.id "
					+ "AND (access.subject_name='public' OR access.subject_id=:subject_id) "
					+ "AND tag_relation.object_id=access.object_id " 
					+ "AND tag_definition.name=:tag_name")
					.setInteger("subject_id", subject.getId())
					.setString("tag_name", tag)
					.list();
			
			Persistance.commit();

			finalUris.retainAll(tagUris);
		}
		return finalUris;
	}

}
