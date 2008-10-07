package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Interaction;
import org.b3mn.poem.business.Model;
import org.json.JSONException;
import org.json.JSONObject;

public class AccessHandler extends  HandlerBase {
	
	private void writeAccessRights(HttpServletResponse response, Identity object)
			throws JSONException, IOException {
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
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {

		String openId = request.getParameter("subject");
		String term = request.getParameter("predicate");
		if ((openId != null) && (term.equals("read") || term.equals("write"))) {
			Model model = new Model(object.getId());
			if (model.addAccessRight(openId, term)) {
				response.setStatus(201);
			} else {
				response.setStatus(200);
			}
			this.writeAccessRights(response, object);
		} else {
			response.setStatus(409);
			response.getWriter().println("AccessHandler : Invalid Parameters!");
		}
	}
	
	@Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		String openId = request.getParameter("subject");
		if (openId != null) {
			Model model = new Model(object.getId());
			if (model.removeAccessRight(openId)) {
				response.setStatus(200);
				this.writeAccessRights(response, object);
			}
		}
		response.setStatus(409);
		response.getWriter().println("AccessHandler : Invalid Parameters!");
	}
	
	
}
