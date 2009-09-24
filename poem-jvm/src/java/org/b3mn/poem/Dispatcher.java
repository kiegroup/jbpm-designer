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
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.business.Model;
import org.b3mn.poem.business.User;
import org.b3mn.poem.handler.HandlerBase;
import org.b3mn.poem.util.AccessRight;
import org.b3mn.poem.util.ExportInfo;
import org.b3mn.poem.util.HandlerInfo;
 
public class Dispatcher extends HttpServlet {
	private static final long serialVersionUID = -9128262564769832181L;
	
	private static String publicUser = "public";  
	private static String backendRootPath = "/backend/"; // Root path of the backend war file
	private static String oryxRootPath = "/oryx/"; // Root path of the oryx war file
	private static String handlerRootPath = backendRootPath + "poem/"; // Root url of all server handlers
	private static String filterBrowserRedirectUrl = handlerRootPath + "repository";
	private static String filterModelRedirectErrorUrl = "/error";
	private static String filterBrowserRegexPattern = "MSIE \\d+\\.\\d+;";
	public static ServletContext servletContext;
	
	protected Map<String, HandlerInfo> knownHandlers = new Hashtable<String, HandlerInfo>();
	
	protected Collection<ExportInfo> exportInfos = new ArrayList<ExportInfo>();
	
//	private static final String USER_AUTHENTIFICATION_TOKENS = "user_auth_tokens";
//	
//	private Properties props;
//	
//	private long authenticationTokenExpirationTime = 30;
	
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
	
	@Override
	public void init() throws ServletException {
		super.init();
		loadHandlerInfo();
		servletContext = this.getServletContext();
		//reloadFriendTable(); // ToDo: implement a bootloader. this operation isn't necessary at each start
		
		// load backend.properties
//		try {
//			FileInputStream in;
//			in = new FileInputStream(this.getServletContext().getRealPath("/WEB-INF/backend.properties"));
//			props = new Properties();
//			props.load(in);
//			in.close();
//			
//			String expirationTime = props.getProperty("org.b3mn.poem.authenticationTokenExpirationTime");
//			authenticationTokenExpirationTime = new Long(expirationTime);
//		} catch (Exception e) {
//			
//		}
	}
	
	protected void reloadFriendTable()  {
		Persistance.getSession().createSQLQuery("SELECT * FROM friend_init()").list();
		Persistance.commit();

	}

	protected String getErrorPage(String stacktrace) {
		String page = "<html><head><title>ORYX: Error</title><body><h1>We're sorry, but an server error occurred.</h1>" + stacktrace +"</body></head></html>";
		return page;
	}
	
	public Collection<ExportInfo> getExportInfos() {
		return this.exportInfos;
	}
	
	public Method getFilterMethod(String filterName) {
		if (HandlerInfo.getFilterMapping() != null) {
			return HandlerInfo.getFilterMapping().get(filterName);
		} else return null;
	}
	
	public Method getSortMethod(String sortName) {
		if (HandlerInfo.getSortMapping() != null) {
			return HandlerInfo.getSortMapping().get(sortName);
		} else return null;
	}
	
	public Collection<String> getHandlerClassNames() {
		File handlerDir = new File(this.getServletContext().
				getRealPath("/WEB-INF/classes/org/b3mn/poem/handler"));
		
		Collection<String> classNames = new ArrayList<String>();
		
		for (File source : handlerDir.listFiles()) {
			if (source.getName().endsWith(".class")) {
				classNames.add("org.b3mn.poem.handler." + 
						source.getName().substring(0, source.getName().lastIndexOf(".")));
			}
		}
		return classNames;
	}
	
	
	// Read all annotation data of all handler, but do not initialize them
	public void loadHandlerInfo() {
		for (String className : this.getHandlerClassNames()) {
			try {
				HandlerInfo info = new HandlerInfo(className);
				if (info != null) {
					// The reflection logic is implemented in the HandlerInfo.load() method
					this.knownHandlers.put(info.getUri(), info);
					if  (info.getExportInfo() != null)
						this.exportInfos.add(info.getExportInfo());
				}
			} catch (Exception e) {}	// Ignore handler if an exception occurs
		}
	}
	
	// Returns the identity of the model that is referenced in the request URL or null if 
	// the request doesn't contain an id
	protected Identity getObjectIdentity(String modelUri) {

		if (modelUri != null) return Identity.instance(Integer.parseInt(modelUri));
		else return null;
	}

	private String getModelUri(String path) {
		// Extract id from the request URL 
		Pattern pattern = Pattern.compile("(\\/model\\/([0-9]+))?(\\/[^\\/]+\\/?)$");
		Matcher matcher = pattern.matcher(new StringBuffer(path));
		matcher.find();			
		String modelUri = matcher.group(1);
		return modelUri;
	}
	
	private String getHandlerUri(String path) {
		// Extract handler uri from the request URL 
		Pattern pattern = Pattern.compile("(\\/model\\/([0-9]+))?(\\/[^\\/]+\\/?)$");
		Matcher matcher = pattern.matcher(new StringBuffer(path));
		matcher.find();
		String uri = matcher.group(3);
		return uri;
	}
	
	// Returns an initialized instance of the requested handler  
	protected HandlerBase getHandler(String uri) {
		try {
			// Is the handler instance already initalized?
			if (this.knownHandlers.get(uri).getHandlerInstance() != null) {
				return this.knownHandlers.get(uri).getHandlerInstance();
			} else {
				Constructor<? extends HandlerBase> constructor = this.knownHandlers.get(uri).getHandlerClass().getConstructor((Class[])null);
				HandlerBase handler = (HandlerBase)  constructor.newInstance();
				handler.setServletContext(this.getServletContext()); // Initialize handler with ServletContext
				handler.init(); // Initialize the handler
				this.knownHandlers.get(uri).setHandlerInstance(handler); // Store the handler instance in ModelInfo
				return handler;
			}
		} catch (Exception e) { return null; }	
	}

	protected boolean checkAccess(HandlerInfo handlerInfo, Identity subject, Model model, String requestMethod) {
		try {
			// Explicitly check right of the public user
			AccessRight publicRight = model.getAccessRight(publicUser);
			// Read access right for the user from the requested model
			AccessRight userRight = model.getAccessRight(subject.getUri());
			// Read required access right from the handler
			AccessRight modelRestriction = handlerInfo.getAccessRestriction(requestMethod);
			// User needs the same or a higher privilege then required by the handler
			return (userRight.compareTo(modelRestriction) >= 0 || publicRight.compareTo(modelRestriction) >= 0);
		} catch(Exception e) { return false; }
	}
	
	
	protected boolean checkBrowser(HandlerInfo handlerInfo, HttpServletRequest request, HttpServletResponse response) {
		// Validate Browser
		if (handlerInfo.isFilterBrowser()) {
			// Extract handler uri from the request URL 
			Pattern pattern = Pattern.compile(filterBrowserRegexPattern); 
			return !pattern.matcher(new StringBuffer((String)request.getHeader("user-agent"))).find();
		} return true;
	}

	// The dispatching magic goes here. Each exception is caught and the tomcat stackstrace page 
	// is replaced by a custom oryx error page.
	@SuppressWarnings("unchecked")
	protected void dispatch(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException {
		try { 
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			// Parse request uri to extract handler uri and model uri
			String modelUri = this.getModelUri(request.getPathInfo());
			String handlerUri  = this.getHandlerUri(request.getPathInfo());
			
			// Validate request: handler uri has to be different from null
			if (handlerUri == null) throw new Exception("Dispatching failed: Handler uri is missing!");
			
			Model model = null;

			// Try to get the model if an handler uri is provided by the request
			if (modelUri != null) {
				try {
					model = new Model(modelUri);
				} catch (Exception e) {
					throw new Exception("Dispatching failed: Invalid model uri", e);
				}
			}
			
			// If you want to disable OpenID login, hard code the openId here. 
			// You can use any name without spaces as openId   
			// For example: 
			// String openId = "OryxUser";
//			String openId =  (String) request.getSession().getAttribute("openid"); // Retrieve open id from session
//			
//			User user = null;
//			
//			// if the user isn't logged in, check if an authentication token is provided
//			// in the servlet context and try to authenticate the user with that token
//			String authTokenParam = request.getParameter("authtoken");
//			
//			List<AuthenticationToken> authList = (List<AuthenticationToken>) this.getServletContext().getAttribute(USER_AUTHENTIFICATION_TOKENS);
//			
//			if(authList == null) {
//				authList = Collections.synchronizedList(new ArrayList<AuthenticationToken>());
//				this.getServletContext().setAttribute(USER_AUTHENTIFICATION_TOKENS, authList);
//			}
//			
//			if(authTokenParam != null && !authTokenParam.equals("")) {
//				
//				//remove all expired tokens
//				//GARBAGE COLLECTOR
//				Date curDate = new Date();
//				Long thirtyMinutes = new Date(1000*60*authenticationTokenExpirationTime).getTime();
//				
//				int index = 0;
//				while(true) {
//					if(index < authList.size()) {
//						AuthenticationToken token = authList.get(index);
//						System.out.println("expires: " + (curDate.getTime() - token.getLastRequestDate().getTime()));
//						if((curDate.getTime() - token.getLastRequestDate().getTime()) > thirtyMinutes ) {
//							authList.remove(index);
//						} else {
//							index++;
//						}
//					} else {
//						break;
//					}
//				}
//				
//				this.getServletContext().setAttribute(USER_AUTHENTIFICATION_TOKENS, authList);
//				// END GARBAGE COLLECTOR
//				
//				//find authentication token
//				Iterator<AuthenticationToken> iter = authList.iterator();
//				
//				while(iter.hasNext()) {
//					AuthenticationToken token = iter.next();
//					
//					if(token.getAuthToken().equals(authTokenParam)) {
//						//authentication token found. set openid
//						openId = token.getUserUniqueId();
//						token.setLastRequestDate(new Date());
//						break;
//					}
//				}
//			}
//			
//			// If the user isn't logged in, set the OpenID to public
//			if (openId == null) {
//				openId = HandlerBase.getPublicUser();
//				request.getSession().setAttribute("openid", openId);
//				user = new User(openId);
//				user.login(request, response);
//			} else {
//				Identity subject = Identity.ensureSubject(openId);
//				user = new User(subject);
//			}
			
			// Retrieve open id from session
			String openId =  (String) request.getSession().getAttribute("openid"); 
			
			User user = (User) request.getAttribute("user");
			
			String requestMethod = request.getMethod();
			
			HandlerInfo handlerInfo = this.knownHandlers.get(handlerUri); // Get meta information of the requested handler

			if (handlerInfo == null) throw new Exception("Dispatching failed: The requested handler doesn't exist.");

			// If the requesting browser cannot handle the response (IE)
			if (!this.checkBrowser(handlerInfo, request, response)) {
				if( model == null ){
					response.sendRedirect(filterBrowserRedirectUrl);
				} else {
					response.sendRedirect(handlerRootPath + model.getUri() + filterModelRedirectErrorUrl);
				}
				return; 
			}
			
			// Verify that the handler is only executed if the context is valid
			if ((model == null) && (handlerInfo.isNeedsModelContext())) {
				throw new Exception("Dispatching failed: The handler requires a model context.");
			}
			if ((model != null) && (!handlerInfo.isNeedsModelContext())) {
				throw new Exception("Dispatching failed: The handler cannot be called in a model context.");
			}
			
			// Check if the user is allowed to do this operation on this model with the requested handler
			if ((model != null) && (!checkAccess(handlerInfo, user.getIdentity(), model, requestMethod)) || 
					(handlerInfo.isPermitPublicUserAccess() && openId.equals(publicUser))) {
				response.setStatus(403); // Access forbidden
				return;
			}
			
			HandlerBase handler = this.getHandler(handlerUri); // Retrieve handler instance
			
			Identity object = null;
			if (model != null) object =  model.getIdentity();
			
			if (requestMethod.equals("GET")) {
				handler.doGet(request, response, user.getIdentity(), object);
			}
			if (requestMethod.equals("POST")) {
				handler.doPost(request, response, user.getIdentity(), object);
			}
			if (requestMethod.equals("PUT")) {
				handler.doPut(request, response, user.getIdentity(), object);
			}
			if (requestMethod.equals("DELETE")) {
				handler.doDelete(request, response, user.getIdentity(), object);
			}
		} catch (Exception e) {
			// response.reset(); // Undo all changes --> this may cause some trouble because of a SUN bug
			e.printStackTrace();
			/*
			try {
				response.sendRedirect("http://bpt.hpi.uni-potsdam.de/Oryx/503");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			*/
			throw new ServletException(e);
		} finally {
			// This might be a hibernate design crime but should fix some problems 
			Persistance.getSession().close();
		}
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException {
		dispatch(request,response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException {
		dispatch(request,response);
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
		throws ServletException {
		dispatch(request,response);
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
		throws ServletException {
		dispatch(request,response);
	}

}