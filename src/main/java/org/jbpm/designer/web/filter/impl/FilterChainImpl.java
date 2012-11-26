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
package org.jbpm.designer.web.filter.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 
 * @author Antoine Toulme
 * An implementation of filter chain to delegate to a series of stored filters.
 *
 */
public class FilterChainImpl implements FilterChain {
    
    private LinkedList<Filter> _filters = new LinkedList<Filter>();
    private FilterChain _chain;
    
    public FilterChainImpl(Collection<Filter> filters, FilterChain chain) {
        _filters.addAll(filters);
        _chain = chain;
    }

    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (!_filters.isEmpty()) {
            // remove the first element from the chain and pass itself to the filter.
            Filter filter = _filters.removeFirst();
            FilterChain chain = this;
            //when reaching the last filter, pass the original chain.
            filter.doFilter(request, response, chain);
        }
    }

}
