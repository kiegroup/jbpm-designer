package org.b3mn.poem.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.business.Model;
import org.b3mn.poem.util.AccessRight;
import org.b3mn.poem.util.HandlerWithModelContext;
import org.b3mn.poem.util.RestrictAccess;
import org.json.JSONException;
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
}
