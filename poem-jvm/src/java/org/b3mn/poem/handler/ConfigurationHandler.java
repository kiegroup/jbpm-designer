/***************************************
 * Copyright (c) 2008
 * Bjoern Wagner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/

package org.b3mn.poem.handler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.manager.ConfigurationManager;
import org.b3mn.poem.util.HandlerWithoutModelContext;
import org.json.JSONException;
import org.json.JSONObject;

@HandlerWithoutModelContext(uri="/config", permitPublicUserAccess=true)
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
