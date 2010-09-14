/***************************************
 * Copyright (c) Intalio, Inc 2010
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/
package com.intalio.web.filter.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.intalio.web.filter.ConfigurableFilterConfig;
import com.intalio.web.filter.IFilterFactory;

/**
 * @author Antoine Toulme
 * 
 * a filter that can delegate to other filters detected via an OSGi declarative service.
 *
 */
public class PluggableFilter implements Filter {
    
    private static List<IFilterFactory> _registeredFilters = new ArrayList<IFilterFactory>();
    private List<Filter> _filters = new ArrayList<Filter>();
    private FilterConfig _filterConfig;
    
    
    public static void registerFilter(IFilterFactory filter) {
        _registeredFilters.add(filter);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        _filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (_filters.size() != _registeredFilters.size()) {
            for (Filter f : _filters) {
                f.destroy();
            }
            _filters.clear();
            for (IFilterFactory f : _registeredFilters) {
                Filter filter = f.createFilter();
                ConfigurableFilterConfig config = new ConfigurableFilterConfig(_filterConfig);
                f.configureFilterConfig(config);
                filter.init(config);
                _filters.add(filter);
            }
        }
        new FilterChainImpl(_filters, chain).doFilter(request, response);
        if (!response.isCommitted()) {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
        for (Filter filter : _filters) {
            filter.destroy();
        }
        _filters.clear();
    }

}
