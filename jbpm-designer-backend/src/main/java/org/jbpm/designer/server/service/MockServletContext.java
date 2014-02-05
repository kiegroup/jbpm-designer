package org.jbpm.designer.server.service;

import org.jbpm.designer.util.ConfigurationProvider;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

/**
 * [manstis] Mock Servlet Context to allow *resources* to be added to classpath without re-writing lots and lots...
 */
public class MockServletContext implements ServletContext {

    @Override
    public String getRealPath( String s ) {
        //FIXME just for now so it works in embedded mode-gwt hosted mode
        return "target/jbpm-designer-standalone-6.0.0.Alpha9" + ConfigurationProvider.getInstance().getDesignerContext();
    }

    @Override
    public ServletContext getContext( String s ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContextPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMajorVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMinorVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getEffectiveMajorVersion() {
        return 0;
    }

    @Override
    public int getEffectiveMinorVersion() {
        return 0;
    }

    @Override
    public String getMimeType( String s ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set getResourcePaths( String s ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getResource( String s ) throws MalformedURLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getResourceAsStream( String s ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getRequestDispatcher( String s ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestDispatcher getNamedDispatcher( String s ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Servlet getServlet( String s ) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration getServlets() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration getServletNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void log( String s ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void log( Exception e,
                     String s ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void log( String s,
                     Throwable throwable ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServerInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getInitParameter( String s ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration getInitParameterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setInitParameter( String s,
                                     String s2 ) {
        return false;
    }

    @Override
    public Object getAttribute( String s ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration getAttributeNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute( String s,
                              Object o ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute( String s ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServletContextName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletRegistration.Dynamic addServlet( String s,
                                                   String s2 ) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet( String s,
                                                   Servlet servlet ) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet( String s,
                                                   Class<? extends Servlet> aClass ) {
        return null;
    }

    @Override
    public <T extends Servlet> T createServlet( Class<T> tClass ) throws ServletException {
        return null;
    }

    @Override
    public ServletRegistration getServletRegistration( String s ) {
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter( String s,
                                                 String s2 ) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter( String s,
                                                 Filter filter ) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter( String s,
                                                 Class<? extends Filter> aClass ) {
        return null;
    }

    @Override
    public <T extends Filter> T createFilter( Class<T> tClass ) throws ServletException {
        return null;
    }

    @Override
    public FilterRegistration getFilterRegistration( String s ) {
        return null;
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return null;
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return null;
    }

    @Override
    public void setSessionTrackingModes( Set<SessionTrackingMode> sessionTrackingModes ) {

    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return null;
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return null;
    }

    @Override
    public void addListener( String s ) {

    }

    @Override
    public <T extends EventListener> void addListener( T t ) {

    }

    @Override
    public void addListener( Class<? extends EventListener> aClass ) {

    }

    @Override
    public <T extends EventListener> T createListener( Class<T> tClass ) throws ServletException {
        return null;
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void declareRoles( String... strings ) {

    }
}
