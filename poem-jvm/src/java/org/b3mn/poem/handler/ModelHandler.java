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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.Representation;
import org.b3mn.poem.util.AccessRight;
import org.b3mn.poem.util.HandlerWithModelContext;
import org.b3mn.poem.util.RestrictAccess;

@HandlerWithModelContext(uri="/self", filterBrowser=true)
public class ModelHandler extends  HandlerBase {
	Properties props=null;
	@Override
	public void init() {
		//Load properties
		FileInputStream in;
		
		//initialize properties from backend.properties
		try {
			
			in = new FileInputStream(this.getBackendRootDirectory() + "/WEB-INF/backend.properties");
			props = new Properties();
			props.load(in);
			in.close();
		}catch (Exception e) {
			props=new Properties();
		}
	}
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		String profileName=null;
		try {
			Representation representation = object.read();
			String stencilSet=representation.getType();
			Pattern p = Pattern.compile("/([^/]+)#");
			Matcher matcher = p.matcher(stencilSet);
			if(matcher.find()){
				profileName=props.getProperty("org.b3mn.poem.handler.ModelHandler.profileFor."+matcher.group(1));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		if(profileName==null)
			profileName="default";
		
	    String queryString = request.getQueryString();   // d=789
	    if (queryString != null) {
			response.sendRedirect("/oryx/editor;"+profileName+"?"+queryString+"#"+object.getUri());

	        }
	    else{
			response.sendRedirect("/oryx/editor;"+profileName+"#"+object.getUri());

	    }
	}
	
	@Override
	@RestrictAccess(AccessRight.WRITE)
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		// TODO: add some error handling
		Representation.update(object.getId(), null, null, request.getParameter("data"), request.getParameter("svg"));
		response.setStatus(200);
	}

	@Override
    public void doPut(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		response.setStatus(200);
	}

	@Override
	@RestrictAccess(AccessRight.WRITE)
    public void doDelete(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws IOException {
		object.delete();
		response.setStatus(200);
	}

}
