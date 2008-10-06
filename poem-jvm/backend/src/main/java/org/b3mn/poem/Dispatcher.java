/***************************************
 * Copyright (c) 2008
 * Bjoern Wagner, Ole Eckermann
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

package org.b3mn.poem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 

import org.b3mn.poem.handler.HandlerBase;
import org.b3mn.poem.manager.UserManager;


public class Dispatcher extends HttpServlet {
	private static final long serialVersionUID = -9128262564769832181L;
	
	private static String publicUser = "public";  
	private static String backendRootPath = "/backend/"; // Root path of the backend war file
	private static String oryxRootPath = "/oryx/"; // Root path of the oryx war file
	private static String handlerRootPath = backendRootPath + "poem/"; // Root url of all server handlers
	
	protected Map<String, HandlerBase> loadedHandlers = new Hashtable<String, HandlerBase>();
	
	public static String getPublicUser() {
		return publicUser;
	}

	public static String getBackendRootPath() {
		return backendRootPath;
	}

	public static String getOryxRootPath() {
		return oryxRootPath;
	}

	public static String getHandlerRootPath() {
		return handlerRootPath;
	}

	public Dispatcher() {
		HandlerBase.setDispatcher(this);
	}
	
	protected String getErrorPage(String stacktrace) {
		String page = "<html><head><title>ORYX: Error</title><body><h1>We're sorry, but an server error occurred.</h1>" + stacktrace +"</body></head></html>";
		return page;
	}
	
	public Collection<String> getHandlerClassNames() {
		// Get class names of the handlers from the database
		Collection<String> result = Persistance.getSession()
			.createSQLQuery("SELECT java_class FROM plugin")
			.list();
		Persistance.commit();
		return result;
	}
	
	// Returns the actual instance of the requested handler
	// TODO: insert better exception handling
	protected HandlerBase getHandlerInstance(String className) {
		if (className != null) {
			try {
				// Create new handler instance with Java reflection
				Class handlerClass = Class.forName(className);
				// TODO: Check if handlerClass is derived from HandlerBase and use 
				// java.lang.reflect.Constructor.newInstance() to create the instance
				HandlerBase handler = (HandlerBase) handlerClass.newInstance();
				handler.setServletContext(this.getServletContext()); // Initialize handler with ServletContext
				handler.init(); // Initialize the handler
				
				return handler;
			} catch(Exception e) {
				return null;
			}
		} else return null;
	}
	
	// Returns the identity of the model that is referenced in the request URL or null if 
	// the request doesn't contain an id
	protected Identity getObjectIdentity(String path) {
		try {
			// Extract id from the request URL 
			Pattern pattern = Pattern.compile("(\\/([0-9]+))?(\\/[^\\/]+\\/?)$");
			Matcher matcher = pattern.matcher(new StringBuffer(path));
			matcher.find();			
			String id = matcher.group(2);
			// If the request doesn't contain an id
			if (id == null) {
				return null;
			}
			else {
				// TODO: Seems to be quick and dirty
				return Identity.instance(Integer.parseInt(id));
			}
		} catch (Exception e) { return null; }
	}
	
	// Returns an initialized instance of the requested handler  
	protected HandlerBase getHandler(String path) {
		try {
			// Extract handler name from the request URL 
			Pattern pattern = Pattern.compile("(\\/([0-9]+))?(\\/[^\\/]+\\/?)$");
			Matcher matcher = pattern.matcher(new StringBuffer(path));
			matcher.find();
			String name = matcher.group(3);
			// If the request doesn't contain an id
			if (name == null) {
				return null;
			}
			else {
				// Has the handler already been loaded?
				if (this.loadedHandlers.get(name) != null) {
					return this.loadedHandlers.get(name);
				} else {
					// Get class name of the handler from the database
					String className = (String) Persistance.getSession().
					createSQLQuery("SELECT java_class FROM plugin WHERE rel= :rel")
					.setString("rel", name)
					.uniqueResult();
					Persistance.commit();
					HandlerBase handler = this.getHandlerInstance(className);
					this.loadedHandlers.put(name, handler);
					return handler;
				}
			}
		} catch (Exception e) { return null; }	
	}

	// The dispatching magic goes here. Each exception is caught and the tomcat stackstrace page 
	// is replaced by a custom oryx error page.
	protected void dispatch(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		try {
			String openId =  (String) request.getSession().getAttribute("openid"); 
			// If the user isn't logged in, set the OpenID to public
			if (openId == null) {
				openId = HandlerBase.getPublicUser();
				request.getSession().setAttribute("openid", openId);
				UserManager.getInstance().login(openId, request, response); // Login public user to handle language selection
			}
			Identity subject = Identity.ensureSubject(openId);
			Identity object = this.getObjectIdentity(request.getPathInfo());
			HandlerBase handler = this.getHandler(request.getPathInfo()); 
			if (request.getMethod().equals("GET")) {
				handler.doGet(request, response, subject, object);
			}
			if (request.getMethod().equals("POST")) {
				handler.doPost(request, response, subject, object);
			}
			if (request.getMethod().equals("PUT")) {
				handler.doPut(request, response, subject, object);
			}
			if (request.getMethod().equals("DELETE")) {
				handler.doDelete(request, response, subject, object);
			}
		} catch (Exception e) {
			//response.reset(); // Undo all changes --> this may cause some trouble because of a SUN bug
			// response.getWriter().print(this.getErrorPage(e.getStackTrace().toString()));
			throw new ServletException(e);
		}
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		dispatch(request,response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		dispatch(request,response);
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		dispatch(request,response);
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		dispatch(request,response);
	}

}