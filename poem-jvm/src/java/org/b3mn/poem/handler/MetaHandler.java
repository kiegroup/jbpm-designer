package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.json.JSONException;

/* This class is deprecated and should be removed if the old repository isn't used anymore
 * 
 * 
 * */

public class MetaHandler extends  HandlerBase {

	
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		try {
			this.getModelMetaData(subject, object, request).write(response.getWriter());
		} catch (JSONException e) {e.printStackTrace();}
	}
}
