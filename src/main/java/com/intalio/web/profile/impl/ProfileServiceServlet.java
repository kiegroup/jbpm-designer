/***************************************
 * Copyright (c) Intalio, Inc 2010
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
package com.intalio.web.profile.impl;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.intalio.web.plugin.Plugin;
import com.intalio.web.profile.Profile;

/**
 * The profile service servlet, to make profiles available to the editor.
 * 
 * @author Antoine Toulme
 */
public class ProfileServiceServlet extends HttpServlet {
    
    private static final long serialVersionUID = 8234640848580375543L;
    
    private ProfileServiceImpl _profileService;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        _profileService = new ProfileServiceImpl(config.getServletContext());
    }
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        if (name == null) {
            throw new IllegalArgumentException("Name parameter required");
        }
        
        Profile p = _profileService.findProfile(name);
        
        try {
            JSONObject profile = new JSONObject();
            profile.put("name", p.getName());
            profile.put("title", p.getTitle());
            JSONArray plugins = new JSONArray();
            for (Plugin plugin : p.getPlugins()) {
                JSONObject obj = new JSONObject();
                obj.put("name", plugin.getName());
                obj.put("core", plugin.isCore());
                obj.put("properties", plugin.getProperties());
                plugins.put(obj);
            }
            profile.put("plugins", plugins);
            profile.put("stencilset", p.getStencilSet());
            profile.put("ssexts", p.getStencilSetExtensions());
            resp.getWriter().append(profile.toString());
        } catch (JSONException e) {
            throw new ServletException(e);
        }
        
        
        
    }
}
