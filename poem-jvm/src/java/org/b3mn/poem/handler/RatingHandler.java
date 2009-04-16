package org.b3mn.poem.handler;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Persistance;
import org.b3mn.poem.business.Model;
import org.b3mn.poem.util.AccessRight;
import org.b3mn.poem.util.FilterMethod;
import org.b3mn.poem.util.HandlerWithModelContext;
import org.b3mn.poem.util.RestrictAccess;
import org.b3mn.poem.util.SortMethod;
import org.json.JSONObject;


@HandlerWithModelContext(uri="/rating")
public class RatingHandler extends HandlerBase {
	
	public void writeRating(Model model, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		
		JSONObject rating = new JSONObject();
		rating.put("userScore", model.getUserScore(subject));
		rating.put("totalScore", model.getTotalScore());
		rating.put("totalVotes", model.getTotalVotes());
		response.getWriter().println(rating.toString());
	}
	
	@Override
	@RestrictAccess(AccessRight.READ)
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		Model model = new Model(object);
		writeRating(model, response, subject, object);
	}
	
	@Override
	@RestrictAccess(AccessRight.READ)
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		Model model = new Model(object);
		String userScore = request.getParameter("userScore");
		if (userScore != null) model.setUserScore(subject, Integer.parseInt(userScore));
		writeRating(model, response, subject, object);
	}
	
	@SuppressWarnings("unchecked")
	@SortMethod(SortName="rating")	
	public static List<String> sortByRating(Identity subject) {
		List<String> results = Persistance.getSession()
		.createSQLQuery("SELECT access.object_name FROM " 	
		+ "access LEFT JOIN model_rating ON access.object_id=model_rating.object_id "
		+ "WHERE (access.subject_name='public' OR access.subject_id=:subject_id)  " 
		+ "GROUP BY access.object_name "
		+ "ORDER BY avg(model_rating.score) DESC NULLS LAST")
		.setInteger("subject_id", subject.getId())
		.list();
	
		Persistance.commit();
		return results;
	}
	
	@SuppressWarnings({ "unchecked" })
	@FilterMethod(FilterName="rating")
	public static Collection<String> filterByRating(Identity subject, String params) {
		float score = Float.parseFloat(params);
		
		List<String> results = Persistance.getSession()
			.createSQLQuery("SELECT access.object_name FROM access, model_rating " 	
			+ "WHERE (access.subject_name='public' OR access.subject_id=:subject_id)  AND access.object_id=model_rating.object_id " 
			+ "GROUP BY access.object_name "
			+ "HAVING avg(model_rating.score) >= :score")
			.setFloat("score", score)
			.setInteger("subject_id", subject.getId())
			.list();
		
		Persistance.commit();
		
		return results;
	}
}
