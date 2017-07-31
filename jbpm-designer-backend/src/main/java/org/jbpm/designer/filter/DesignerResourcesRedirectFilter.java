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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class DesignerResourcesRedirectFilter implements Filter {

    public static final String DESIGNER_PREFIX = "/org.jbpm.designer.jBPMDesigner";

    private String redirectTo;

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        try {
            if (request instanceof HttpServletRequest) {
                String requestUrl = ((HttpServletRequest) request).getRequestURI();
                String contextPath = ((HttpServletRequest) request).getContextPath();
                String redirect = redirectTo + requestUrl.replaceFirst((contextPath + DESIGNER_PREFIX),
                                                                       "");
                request.getRequestDispatcher(redirect).forward(request,
                                                               response);
            } else {
                chain.doFilter(request,
                               response);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        redirectTo = config.getInitParameter("redirectTo");
    }
}
