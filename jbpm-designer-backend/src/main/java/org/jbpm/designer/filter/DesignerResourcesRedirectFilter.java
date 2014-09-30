package org.jbpm.designer.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DesignerResourcesRedirectFilter implements Filter {

    public static final String DESIGNER_PREFIX = "/org.jbpm.designer.jBPMDesigner";
    
    private String redirectTo;
    
    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            String requestUrl = ((HttpServletRequest) request).getRequestURI();
            String contextPath = ((HttpServletRequest) request).getContextPath();
            String redirect = contextPath + redirectTo + requestUrl.replaceFirst((contextPath+DESIGNER_PREFIX), "");
            ((HttpServletResponse) response).sendRedirect(redirect);

        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        redirectTo = config.getInitParameter("redirectTo");
    }

}
