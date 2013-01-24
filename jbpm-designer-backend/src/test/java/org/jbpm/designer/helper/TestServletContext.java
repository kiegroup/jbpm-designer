package org.jbpm.designer.helper;

import org.jbpm.designer.repository.Repository;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

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
}