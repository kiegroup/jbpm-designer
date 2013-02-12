package org.jbpm.designer.server.service;

import org.jbpm.designer.util.ConfigurationProvider;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * [manstis] Mock Servlet Context to allow *resources* to be added to classpath without re-writing lots and lots...
 */
public class MockServletContext implements ServletContext {

    @Override
    public String getRealPath( String s ) {
        //FIXME just for now so it works in embedded mode-gwt hosted mode
        return "target/jbpm-designer-standalone-6.0.0-SNAPSHOT" + ConfigurationProvider.getInstance().getDesignerContext();
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
}
