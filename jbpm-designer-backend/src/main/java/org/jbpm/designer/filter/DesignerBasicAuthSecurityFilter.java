/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.designer.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.FailedAuthenticationException;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.ext.security.server.BasicAuthSecurityFilter;

public class DesignerBasicAuthSecurityFilter extends BasicAuthSecurityFilter implements Filter {

    @Inject
    AuthenticationService authenticationService;

    @Override
    public void doFilter(ServletRequest _request,
                         ServletResponse _response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) _request;
        HttpServletResponse response = (HttpServletResponse) _response;
        HttpSession session = request.getSession(false);
        User user = this.authenticationService.getUser();

        // For HTTP OPTIONS verb/method reply with ACCEPTED status code -- per CORS handshake
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }

        try {
            if (user == null) {
                if (this.authenticate(request)) {
                    chain.doFilter(request,
                                   response);
                    if (response.isCommitted()) {
                        this.authenticationService.logout();
                    }
                } else {
                    this.challengeClient(request,
                                         response);
                }
            } else {
                chain.doFilter(request,
                               response);
            }
        } finally {
            if (session == null) {
                session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
            }
        }
    }

    @Override
    public void challengeClient(HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        // allow OPTIONS preflight requests
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        } else {
            super.challengeClient(request,
                                  response);
        }
    }

    public boolean authenticate(HttpServletRequest req) {

        if (req.getMethod().equals("OPTIONS")) {
            return true;
        }

        String authHead = req.getHeader("Authorization");
        if (authHead != null) {
            int index = authHead.indexOf(32);
            String[] credentials = (new String(Base64.decodeBase64(authHead.substring(index)),
                                               Charsets.UTF_8)).split(":",
                                                                      -1);

            try {
                this.authenticationService.login(credentials[0],
                                                 credentials[1]);
                return true;
            } catch (FailedAuthenticationException var6) {
                return false;
            }
        } else {
            return false;
        }
    }
}
