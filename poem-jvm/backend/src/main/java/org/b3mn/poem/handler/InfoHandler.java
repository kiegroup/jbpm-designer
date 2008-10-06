package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Representation;
import org.json.JSONException;

public class InfoHandler extends  HandlerBase {

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		try {
			this.getModelInfo(object, request.getServerName()).write(response.getWriter());
		} catch (JSONException e) {e.printStackTrace();}
	}
	
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		try {
			Representation.update(object.getId(), request.getParameter("title"), request.getParameter("summary"), null, null);
			this.getModelMetaData(subject, object, request).write(response.getWriter());
		} catch (JSONException e) {e.printStackTrace();}
	}

}
