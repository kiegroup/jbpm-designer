package org.b3mn.poem.security.filter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.business.User;
import org.b3mn.poem.handler.HandlerBase;
import org.b3mn.poem.security.AuthenticationToken;

public class AuthenticationFilter implements Filter {

	private static final String USER_AUTHENTIFICATION_TOKENS = "user_auth_tokens";

	private Properties props;

	private long authenticationTokenExpirationTime = 30;

	private ServletContext servletContext;

	public void init(FilterConfig config) throws ServletException {
		servletContext = config.getServletContext();

		// load backend.properties
		FileInputStream in;
		try {
			in = new FileInputStream(servletContext
					.getRealPath("/WEB-INF/backend.properties"));
			props = new Properties();
			props.load(in);
			in.close();

			String expirationTime = props
					.getProperty("org.b3mn.poem.authenticationTokenExpirationTime");
			authenticationTokenExpirationTime = new Long(expirationTime);
		}
		catch (Exception e) {

		}
	}

	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		try {
			// make sure this is a HttpServlet.
			HttpServletRequest request = null;
			HttpServletResponse response = null;
			try {
				request = (HttpServletRequest) req;
				response = (HttpServletResponse) res;
			} catch (ClassCastException cce) {
				chain.doFilter(req, res);
				return;
			}

			// If you want to disable OpenID login, hard code the openId here.
			// You can use any name without spaces as openId
			// For example:
//			String openId = "OryxUser";
			String openId = 
				(String) request.getSession().getAttribute("openid"); // Retrieve open id from session
			
			User user = null;

			// if the user isn't logged in, check if an authentication token is
			// provided
			// in the servlet context and try to authenticate the user with that
			// token
			String authTokenParam = request.getParameter("authtoken");

			List<AuthenticationToken> authList = (List<AuthenticationToken>) servletContext
					.getAttribute(USER_AUTHENTIFICATION_TOKENS);

			if (authList == null) {
				authList = Collections
						.synchronizedList(new ArrayList<AuthenticationToken>());
				servletContext.setAttribute(USER_AUTHENTIFICATION_TOKENS,
						authList);
			}

			//call garbage collector for expired tokens
			this.removeExpiredTokens(authList);
			
			if (authTokenParam != null && !authTokenParam.equals("")) {

				// find authentication token
				Iterator<AuthenticationToken> iter = authList.iterator();

				while (iter.hasNext()) {
					AuthenticationToken token = iter.next();

					if (token.getAuthToken().equals(authTokenParam)) {
						// authentication token found. set openid
						openId = token.getUserUniqueId();
						token.setLastRequestDate(new Date());
						break;
					}
				}
			} else if(openId != null){
				//client-side request without authToken.
				//update all tokens for that openid
				Iterator<AuthenticationToken> iter = authList.iterator();

				while (iter.hasNext()) {
					AuthenticationToken token = iter.next();

					if (token.getUserUniqueId().equals(openId)) {
						// authentication token found. set openid
						token.setLastRequestDate(new Date());
					}
				}
			}

			//store authentication token list
			servletContext.setAttribute(USER_AUTHENTIFICATION_TOKENS,
					authList);
			
			// If the user isn't logged in, set the OpenID to public
			if (openId == null) {
				openId = HandlerBase.getPublicUser();
				request.getSession().setAttribute("openid", openId);
				user = new User(openId);
				//user.login(request, response);
			} else {
				Identity subject = Identity.ensureSubject(openId);
				user = new User(subject);
				//user.login(request, response);
			}
			request.setAttribute("user", user);

		} catch (Exception e) {

		} finally {
			chain.doFilter(req, res);
		}
	}
	
	/**
	 * The garbage collector for invalid authentication tokens.
	 * 
	 * @param authList The list of currently stored authentication tokens
	 */
	private void removeExpiredTokens(List<AuthenticationToken> authList) {
		Date curDate = new Date();
		Long thirtyMinutes = new Date(
				1000 * 60 * authenticationTokenExpirationTime)
				.getTime();

		int index = 0;
		while (true) {
			if (index < authList.size()) {
				AuthenticationToken token = authList.get(index);
				
				if ((curDate.getTime() - token.getLastRequestDate()
						.getTime()) > thirtyMinutes) {
					authList.remove(index);
				} else {
					index++;
				}
			} else {
				break;
			}
		}
	}
}