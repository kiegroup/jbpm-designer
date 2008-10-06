package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.json.JSONException;

public class NewModelHandler extends HandlerBase {

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		String stencilset = "/stencilsets/bpmn/bpmn.json";
		if (request.getParameter("stencilset") != null) {
			stencilset = request.getParameter("stencilset");
		}

		String content = "<div id=\"oryx-canvas123\" class=\"-oryx-canvas\">"
			+ "<span class=\"oryx-mode\">writeable</span>"
			+ "<span class=\"oryx-mode\">fullscreen</span>"
			+ "<a href=\"http://" + request.getServerName() + ':' + String.valueOf(request.getServerPort()) + "/oryx" + stencilset + "\" rel=\"oryx-stencilset\"></a>\n"
			+ "</div>\n";
		response.getWriter().print(this.getOryxModel("New Process Model", content,
				this.getLanguageCode(request), this.getCountryCode(request)));

		response.setStatus(200);
		response.setContentType("application/xhtml+xml");
	}
	
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		// Check whether the user is public
		if (subject.getUri().equals(getPublicUser())) {
			response.getWriter().println("The public user is not allowed to create new models. Please login first.");
			response.setStatus(403);
			return;
		}
		// Check whether the request contains at least the data and svg parameters
		if ((request.getParameter("data") != null) && (request.getParameter("svg") != null)) {
			String title = request.getParameter("title");
			if (title == null) title = "New Process";
			String type = request.getParameter("type");
			if (type == null) type = "/stencilsets/bpmn/bpmn.json";
			String summary = request.getParameter("summary");
			if (summary == null) summary = "This is a new process.";
			
			Identity identity = Identity.newModel(subject, title, type, summary, 
					request.getParameter("svg"), request.getParameter("data"));
			response.setHeader("location", this.getServerPath(request) + identity.getUri() + "/self");
			response.setStatus(201);
		}
		else {
			response.setStatus(400);
			response.getWriter().println("Data and/or SVG missing");
		}
			
	}
}
