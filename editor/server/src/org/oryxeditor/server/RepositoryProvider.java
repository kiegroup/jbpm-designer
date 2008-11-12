package org.oryxeditor.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oryxeditor.server.auth.OpenIDAuthenticationServlet;

/**
 * Provides a repository view to the client. This repository needs to know about
 * the currently logged-in user and his openID. The main repository code is
 * located in the jsp file repository.jsp. The openID is generated directly into
 * the javascript code.
 */
public class RepositoryProvider extends HttpServlet {

    private static final long serialVersionUID = 3110537531840029014L;

    private static final String REPOSITORY_PAGE = "/repository.jsp";

    /**
     * Provides a repository with an included openid that is either the real
     * openid of the user or an empty string inside the resulting javascript of
     * the repository.
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {

	// create a new openid identifier string and get the openid identifier
	// from the session.
	String open_id = new String("");
	Object identifier_from_session = req.getSession().getAttribute(
		OpenIDAuthenticationServlet.OPENID_SESSION_IDENTIFIER);

	System.out.println("OpenID got from session is " + identifier_from_session);
	
	// if the identifier from the session is indeed a string, cast it into
	// the openid identifier initialized earlier.
	if (identifier_from_session instanceof String) {
	    open_id = (String) identifier_from_session;
	}

	System.out.println("OpenID sent to repository.jsp is " + open_id);
	
	// set the request attribute. if there was no string identifier in the
	// session, the resulting openid identifier is an empty string.
	req.setAttribute("identifier", open_id);

	// redirect to the actual repository jsp using the RequestDispatcher.
	// this is not involving redirects to the browser, but is server-only.
	RequestDispatcher dispatcher = getServletContext()
		.getRequestDispatcher(REPOSITORY_PAGE);
	dispatcher.forward(req, res);
    }
}
