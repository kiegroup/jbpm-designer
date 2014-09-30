package org.jbpm.designer.helper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;

public class TestServletConfig implements ServletConfig {

    private ServletContext servletContext;

    public TestServletConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String getServletName() {
        return null;  
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public String getInitParameter(String name) {
        return null;  
    }

    public Enumeration getInitParameterNames() {
        return null;  
    }
}
