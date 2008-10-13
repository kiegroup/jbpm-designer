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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Interaction;
import org.b3mn.poem.business.Model;
import org.b3mn.poem.util.AccessRight;
import org.b3mn.poem.util.HandlerWithModelContext;
import org.b3mn.poem.util.RestrictAccess;
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
			for (String openId : openIds.split(",")) {
				Model model = new Model(object.getId());
				model.addAccessRight(openId, term);
			}
			response.setStatus(200);
			this.writeAccessRights(response, object);
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
	
	
}
