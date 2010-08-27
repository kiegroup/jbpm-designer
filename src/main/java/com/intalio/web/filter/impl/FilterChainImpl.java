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
            Filter filter = _filters.pop();
            FilterChain chain = this;
            //when reaching the last filter, pass the original chain.
            filter.doFilter(request, response, chain);
        }
    }

}
