package org.jbpm.designer.helper;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;
import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.jbpm.designer.repository.Repository;

public class TestServletContext implements ServletContext {

    private Repository repository;

    public TestServletContext() {

    }

    public TestServletContext(Repository repository) {
        this.repository = repository;
    }

    public String getContextPath() {
        return null;
    }

    public ServletContext getContext(String uripath) {
        return null;
    }

    public int getMajorVersion() {
        return 0;
    }

    public int getMinorVersion() {
        return 0;
    }

  @Override
  public int getEffectiveMajorVersion() {
    return 0;  //TODO: implement this method
  }

  @Override
  public int getEffectiveMinorVersion() {
    return 0;  //TODO: implement this method
  }

  public String getMimeType(String file) {
        return null;
    }

    public Set getResourcePaths(String path) {
        return null;
    }

    public URL getResource(String path) throws MalformedURLException {
        return null;
    }

    public InputStream getResourceAsStream(String path) {
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        return null;
    }

    public Servlet getServlet(String name) throws ServletException {
        return null;
    }

    public Enumeration getServlets() {
        return null;
    }

    public Enumeration getServletNames() {
        return null;
    }

    public void log(String msg) {

    }

    public void log(Exception exception, String msg) {

    }

    public void log(String message, Throwable throwable) {

    }

    public String getRealPath(String path) {
        return "src/test/resources/org/jbpm/designer/public" + path;
    }

    public String getServerInfo() {
        return null;
    }

    public String getInitParameter(String name) {
        return null;
    }

    public Enumeration getInitParameterNames() {
        return null;
    }

  @Override
  public boolean setInitParameter(String s, String s1) {
    return false;  //TODO: implement this method
  }

  public Object getAttribute(String name) {
        return repository;
    }

    public Enumeration getAttributeNames() {
        return null;
    }

    public void setAttribute(String name, Object object) {

    }

    public void removeAttribute(String name) {

    }

    public String getServletContextName() {
        return null;
    }

  @Override
  public ServletRegistration.Dynamic addServlet(String s, String s1) {
    return null;  //TODO: implement this method
  }

  @Override
  public ServletRegistration.Dynamic addServlet(String s, Servlet servlet) {
    return null;  //TODO: implement this method
  }

  @Override
  public ServletRegistration.Dynamic addServlet(String s, Class<? extends Servlet> aClass) {
    return null;  //TODO: implement this method
  }

  @Override
  public <T extends Servlet> T createServlet(Class<T> aClass) throws ServletException {
    return null;  //TODO: implement this method
  }

  @Override
  public ServletRegistration getServletRegistration(String s) {
    return null;  //TODO: implement this method
  }

  @Override
  public Map<String, ? extends ServletRegistration> getServletRegistrations() {
    return null;  //TODO: implement this method
  }

  @Override
  public FilterRegistration.Dynamic addFilter(String s, String s1) {
    return null;  //TODO: implement this method
  }

  @Override
  public FilterRegistration.Dynamic addFilter(String s, Filter filter) {
    return null;  //TODO: implement this method
  }

  @Override
  public FilterRegistration.Dynamic addFilter(String s, Class<? extends Filter> aClass) {
    return null;  //TODO: implement this method
  }

  @Override
  public <T extends Filter> T createFilter(Class<T> aClass) throws ServletException {
    return null;  //TODO: implement this method
  }

  @Override
  public FilterRegistration getFilterRegistration(String s) {
    return null;  //TODO: implement this method
  }

  @Override
  public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
    return null;  //TODO: implement this method
  }

  @Override
  public SessionCookieConfig getSessionCookieConfig() {
    return null;  //TODO: implement this method
  }

  @Override
  public void setSessionTrackingModes(Set<SessionTrackingMode> set) {
    //TODO: implement this method
  }

  @Override
  public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
    return null;  //TODO: implement this method
  }

  @Override
  public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
    return null;  //TODO: implement this method
  }

  @Override
  public void addListener(String s) {
    //TODO: implement this method
  }

  @Override
  public <T extends EventListener> void addListener(T t) {
    //TODO: implement this method
  }

  @Override
  public void addListener(Class<? extends EventListener> aClass) {
    //TODO: implement this method
  }

  @Override
  public <T extends EventListener> T createListener(Class<T> aClass) throws ServletException {
    return null;  //TODO: implement this method
  }

  @Override
  public JspConfigDescriptor getJspConfigDescriptor() {
    return null;  //TODO: implement this method
  }

  @Override
  public ClassLoader getClassLoader() {
    return null;  //TODO: implement this method
  }

  @Override
  public void declareRoles(String... strings) {
    //TODO: implement this method
  }
}