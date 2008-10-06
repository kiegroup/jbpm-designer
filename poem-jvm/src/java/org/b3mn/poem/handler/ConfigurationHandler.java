package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.manager.ConfigurationManager;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfigurationHandler extends  HandlerBase {
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		ConfigurationManager cm = ConfigurationManager.getInstance();
		JSONObject jsonConfig = new JSONObject();
		try {
			Collection<String> userSettingKeys = cm.getUserSettingKeys(subject);
			// Check weather there are setting in the database
			if (userSettingKeys != null) {
				// Add key value pairs to the json object
				for (String key : userSettingKeys) {
					jsonConfig.put(key, cm.getUserSetting(subject, key));
				}
			}
			jsonConfig.write(response.getWriter()); // Write json to http response
			response.setStatus(200);
		}
		catch (JSONException e) {
			// TODO: Remove debug code
			response.setStatus(500);
			response.getWriter().println(e.getMessage());
		}
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		String jsonConfigStr = request.getParameter("json_config");
		if (jsonConfigStr != null) {
			try {
				JSONObject jsonConfig = new JSONObject(jsonConfigStr);
				ConfigurationManager cm = ConfigurationManager.getInstance();
				Iterator iter = jsonConfig.keys(); 
				// for each key in the json post
				while (iter.hasNext()) {
					String key = (String) iter.next();
					cm.setUserSetting(subject, key, jsonConfig.getString(key)); // Store key value pair in the database 
				}
				response.setStatus(200);
			} catch (JSONException e) {
				// TODO: Remove Debug code
				response.setStatus(500);
				return;
			}
			
			
		}
	}
}
