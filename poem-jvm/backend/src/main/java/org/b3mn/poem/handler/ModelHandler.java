package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Representation;
import org.json.JSONException;

public class ModelHandler extends  HandlerBase {

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		Representation representation = object.read();
		response.getWriter().println(this.getOryxModel(representation.getTitle(), 
				representation.getContent(), this.getLanguageCode(request), 
				this.getCountryCode(request)));
		response.setStatus(200);
	}
	
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		// TODO: add some error handling
		Representation.update(object.getId(), null, null, request.getParameter("content"), request.getParameter("svg"));
		response.setStatus(200);
	}

	@Override
    public void doPut(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		response.setStatus(200);
	}

	@Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		object.delete();
		response.setStatus(200);
	}

}
