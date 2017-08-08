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
package org.jbpm.designer.web.filter.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jbpm.designer.web.filter.ConfigurableFilterConfig;
import org.jbpm.designer.web.filter.IFilterFactory;

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

    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
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
        new FilterChainImpl(_filters,
                            chain).doFilter(request,
                                            response);
        if (!response.isCommitted()) {
            chain.doFilter(request,
                           response);
        }
    }

    public void destroy() {
        for (Filter filter : _filters) {
            filter.destroy();
        }
        _filters.clear();
    }
}
