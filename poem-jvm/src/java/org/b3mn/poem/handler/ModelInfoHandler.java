package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Persistance;
import org.b3mn.poem.Representation;
import org.b3mn.poem.business.Model;
import org.b3mn.poem.util.FilterMethod;
import org.b3mn.poem.util.JavaBeanJsonTransformation;
import org.b3mn.poem.util.SortMethod;
import org.json.JSONException;
import org.json.JSONObject;

public class ModelInfoHandler extends  HandlerBase {

	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		if (object != null) {
			Model model = new Model(object.getId());
			Collection<String> attributes = new ArrayList<String>();
			attributes.add("title");			
			attributes.add("summary");
			attributes.add("type");
			attributes.add("creationDate");
			attributes.add("lastUpdate");
			attributes.add("author");
			JSONObject data = JavaBeanJsonTransformation.toJsonObject(model, attributes);
			data.put("thumbnailUri", this.getServerPath(request) + model.getUri() + "/png");
			// Create an envelop to be able to return results for more than one model later
			JSONObject envelop = new JSONObject(); 
			envelop.put(String.valueOf(model.getId()), data);	
			envelop.write(response.getWriter());
			response.setStatus(200);
		}
	}
	
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {

	}

	@SuppressWarnings({ "unchecked", "static-access" })
	@FilterMethod(FilterName="type")
	public static Collection<Integer> filterByModelType(Identity subject, String params) {
		String typeQuery = "";
		for (String type : params.split(",")) {
			type = removeSpaces(type);
			typeQuery+="representation.type='"+type+"' OR ";
		}
		// Remove last OR
		if (typeQuery.length() > 4) {
			typeQuery = typeQuery.substring(0, typeQuery.length() - 4);
		}
		
		List<Integer> results = Persistance.getSession()
			.createSQLQuery("SELECT identity.id FROM identity, access, representation "
			+ "WHERE access.subject_id=:subject_id AND access.object_id=identity.id AND representation.ident_id=access.object_id "
			+ "AND (" + typeQuery + ")")
			.setInteger("subject_id", subject.getId())
			.list();
		
		Persistance.commit();
		
		return results;
	}
	
	@SuppressWarnings("unchecked")
	@SortMethod(SortName="lastChange")	
	public static List<Integer> sortByLastChange(Identity subject) {
		List<Integer> results = Persistance.getSession()
		.createSQLQuery("SELECT identity.id FROM identity, access, representation "
		+ "WHERE access.subject_id=:subject_id AND access.object_id=identity.id AND representation.ident_id=access.object_id " 
		+ "ORDER BY representation.updated")
		.setInteger("subject_id", subject.getId())
		.list();
	
		Persistance.commit();
		return results;
	}
	
	
}
