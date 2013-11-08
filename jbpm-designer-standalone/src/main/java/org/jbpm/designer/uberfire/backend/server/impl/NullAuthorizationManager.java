package org.jbpm.designer.uberfire.backend.server.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.uberfire.security.Resource;
import org.uberfire.security.Subject;
import org.uberfire.security.authz.AuthorizationException;
import org.uberfire.security.authz.AuthorizationManager;

@ApplicationScoped
@Alternative
public class NullAuthorizationManager implements AuthorizationManager {

	@Override
	public boolean supports(Resource resource) {
		return true;
	}

	@Override
	public boolean authorize(Resource resource, Subject subject) throws AuthorizationException {
		return true;
	}

}
