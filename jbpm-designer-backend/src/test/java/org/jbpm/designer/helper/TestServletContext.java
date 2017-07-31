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

package org.jbpm.designer.helper;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
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

import org.jbpm.designer.repository.Repository;

public class TestServletContext implements ServletContext {

    private Repository repository;

    private String basePath = null;

    public TestServletContext() {

    }

    Map<String, String> initParameters = new HashMap<String, String>();

    public TestServletContext(Repository repository) {
        this.repository = repository;
    }

    public TestServletContext(Repository repository,
                              String basePath) {
        this.repository = repository;
        this.basePath = basePath;
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
        return 0;
    }

    @Override
    public int getEffectiveMinorVersion() {
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

    public void log(Exception exception,
                    String msg) {

    }

    public void log(String message,
                    Throwable throwable) {

    }

    public String getRealPath(String path) {
        if (basePath != null) {
            return basePath + path;
        } else {
            return "src/test/resources/org/jbpm/designer/public" + path;
        }
    }

    public String getServerInfo() {
        return null;
    }

    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    public Enumeration getInitParameterNames() {
        return null;
    }

    @Override
    public boolean setInitParameter(String name,
                                    String value) {
        initParameters.put(name,
                           value);
        return true;
    }

    public Object getAttribute(String name) {
        return repository;
    }

    public Enumeration getAttributeNames() {
        return null;
    }

    public void setAttribute(String name,
                             Object object) {

    }

    public void removeAttribute(String name) {

    }

    public String getServletContextName() {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName,
                                                  String className) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName,
                                                  Servlet servlet) {
        return null;
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String servletName,
                                                  Class<? extends Servlet> servletClass) {
        return null;
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public ServletRegistration getServletRegistration(String servletName) {
        return null;
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName,
                                                String className) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName,
                                                Filter filter) {
        return null;
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String filterName,
                                                Class<? extends Filter> filterClass) {
        return null;
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException {
        return null;
    }

    @Override
    public FilterRegistration getFilterRegistration(String filterName) {
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
    public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes) {

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
    public void addListener(String className) {

    }

    @Override
    public <T extends EventListener> void addListener(T t) {

    }

    @Override
    public void addListener(Class<? extends EventListener> listenerClass) {

    }

    @Override
    public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException {
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
    public void declareRoles(String... roleNames) {

    }

    @Override
    public String getVirtualServerName() {
        return null;
    }
}