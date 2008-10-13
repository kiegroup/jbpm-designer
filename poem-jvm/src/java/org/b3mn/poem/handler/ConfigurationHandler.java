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
import org.b3mn.poem.util.HandlerInfo;
import org.b3mn.poem.util.HandlerWithoutModelContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@HandlerWithoutModelContext(uri="/config", permitPublicUserAccess=true)
public class ConfigurationHandler extends  HandlerBase {
	
	private JSONArray getAvailableLanguages() throws Exception {
		JSONArray availableLanguages = new JSONArray();
		for (String language : this.getLanguageFiles(this.getBackendRootDirectory() + "/i18n").keySet()) {
			String[] languageCountryCode = language.split("_"); // split e.g. en_us 
			JSONObject languageObj = new JSONObject();
			languageObj.put("languagecode", languageCountryCode[0]);
			// Country code exists
			if (languageCountryCode.length > 1){ 
				languageObj.put("countrycode", languageCountryCode[1]);
			} 
			availableLanguages.put(languageObj);
		}
		return availableLanguages;
	}
	
	private JSONArray getAvailableSorts() throws Exception {
		return new JSONArray(HandlerInfo.getSortMapping().keySet());
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		JSONObject envelope = new JSONObject();
		envelope.put("availableLanguages", getAvailableLanguages());
		envelope.put("availableSorts", getAvailableSorts());
		response.getWriter().println(envelope.toString());
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
	
	}
}
