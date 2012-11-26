/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.designer.web.filter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * @author Antoine Toulme
 * A configurable filter config, so that users can set settings programmatically.
 *
 */
@SuppressWarnings("rawtypes")
public class ConfigurableFilterConfig implements FilterConfig {
    
    private Map<String, String> _parameters = new HashMap<String, String>();
    
    private String _name;

    private ServletContext _servletContext;
    
    public ConfigurableFilterConfig(FilterConfig config) {
        Enumeration parameters = config.getInitParameterNames();
        while(parameters.hasMoreElements()) {
            String name = (String) parameters.nextElement();
            _parameters.put(name, config.getInitParameter(name));
        }
        _name = config.getFilterName();
        _servletContext = config.getServletContext();
        
    }

    /** 
     * Returns the filter-name of this filter as defined in the deployment descriptor. 
     */
    public String getFilterName() {
        return _name;
    }

    /**
     * Returns a reference to the {@link ServletContext} in which the caller
     * is executing.
     *
     *
     * @return      a {@link ServletContext} object, used
     *          by the caller to interact with its servlet 
     *                  container
     * 
     * @see     ServletContext
     *
     */
    public ServletContext getServletContext() {
        return _servletContext;
    }

    /**
     * Returns a <code>String</code> containing the value of the 
     * named initialization parameter, or <code>null</code> if 
     * the parameter does not exist.
     *
     * @param name  a <code>String</code> specifying the name
     *          of the initialization parameter
     *
     * @return      a <code>String</code> containing the value 
     *          of the initialization parameter
     *
     */
    public String getInitParameter(String name) {
        return _parameters.get(name);
    }

    /**
     * Returns the names of the filter's initialization parameters
     * as an <code>Enumeration</code> of <code>String</code> objects, 
     * or an empty <code>Enumeration</code> if the filter has
     * no initialization parameters.
     *
     * @return      an <code>Enumeration</code> of <code>String</code> 
     *          objects containing the names of the filter's 
     *          initialization parameters
     *
     *
     *
     */
    public Enumeration getInitParameterNames() {
        final LinkedList<String> keys = new LinkedList<String>(_parameters.keySet());
        return new Enumeration() {

            public boolean hasMoreElements() {
                return keys.isEmpty();
            }

            public Object nextElement() {
                return keys.removeFirst();
            }};
    }
    
    /**
     * Sets the value of a parameter
     * @param name the name of the parameter
     * @param value the value of the parameter
     */
    public void setParameter(String name, String value) {
        _parameters.put(name, value);
    }
    
    /**
     * Sets the name of the filter.
     * @param name 
     */
    public void setFilterName(String name) {
        _name = name;
    }

}
