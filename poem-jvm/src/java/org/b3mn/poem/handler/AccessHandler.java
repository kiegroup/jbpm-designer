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

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		Model model = new Model(object.getId());
		
		Map<String, String> accessRights = model.getAccessRights();
		
		JSONObject json = new JSONObject(accessRights);
		json.write(response.getWriter());
	}
	
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		Identity sub = Identity.ensureSubject(request.getParameter("subject"));
		String subject_hierarchy = sub.getUserHierarchy();
		String object_hierarchy = object.getModelHierarchy();
		String term = request.getParameter("predicate");
		Interaction right = Interaction.exist(subject_hierarchy, object_hierarchy, term);
		if (right == null) {
			right = new Interaction();
			right.setSubject(subject_hierarchy);
			right.setObject(object_hierarchy);
			right.setScheme("http://b3mn.org/http");
			right.setTerm(term);
			right.setObject_self(true);
			right.save();
			response.setStatus(201);
		}
		else {
			response.setStatus(200);
		}

		String location = this.getServerPath(request) +  object.getUri() + right.getUri();
		try {
			JSONObject output = new JSONObject();
			output.put("predicate", right.getPredicate());
			output.put("subject", sub.getUri());
			output.put("uri", location);
			response.addHeader("Location", location);
			output.write(response.getWriter());
	      } catch (JSONException e) {e.printStackTrace();}
	}
	
	@Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		Interaction right = Interaction.exist(Integer.parseInt(request.getParameter("id")));
		if (right != null) {
			right.delete();
			response.setStatus(200);
		}
		else {
			response.setStatus(400);
		}
	}
	
	
}
