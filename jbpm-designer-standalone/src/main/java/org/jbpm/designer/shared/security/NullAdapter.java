package org.jbpm.designer.shared.security;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.server.security.auth.AuthenticationAdapter;

public class NullAdapter implements AuthenticationAdapter {

	@Override
	public void challenge(Message message) {

	}
	
	public void process(Message message) {
		
	}

	@Override
	public boolean endSession(Message message) {
		return false;
	}

	@Override
	public boolean isAuthenticated(Message message) {
		return true;
	}

	public boolean requiresAuthorization(Message message) {
	    return false;
	}

}
