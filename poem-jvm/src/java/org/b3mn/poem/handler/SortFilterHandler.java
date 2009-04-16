/***************************************
 * Copyright (c) 2008
 * Bjoern Wagner
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

package org.b3mn.poem.handler;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.util.HandlerWithoutModelContext;
import org.json.JSONArray;

@HandlerWithoutModelContext(uri="/filter")
public class SortFilterHandler extends HandlerBase {
	
	@SuppressWarnings("unchecked")
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		String sortName = request.getParameter("sort");
		String defaultSort = "lastchange";
		if (sortName == null) {
			sortName = defaultSort; // set default filter
		} 
		
		sortName = sortName.toLowerCase();
		Method sortMethod = getDispatcher().getSortMethod(sortName);
		
		if (sortMethod == null) {
			sortName = defaultSort; // set default filter
			sortMethod = getDispatcher().getSortMethod(sortName);
		}

		Object[] arg = { subject };
		
		// Use the LinkedHashSet implementation to remain the order of the entries
		Set<String> orderedUris = new LinkedHashSet<String>( (List<String>) sortMethod.invoke(null, arg));
		
		// Iterate over http parameters
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String filterName = (String) e.nextElement();
			
			String params = request.getParameter(filterName);
			// Ignore Filters without parameters
			if (!filterName.equals("sort") && (params != null) && (params.length() > 0)) {
				Method filterMethod = getDispatcher().getFilterMethod(filterName.toLowerCase());
				// If the filter method exists
				if (filterMethod != null) {
					Object[] args = { subject,  params };
					// Invoke the filter method an add the filtered ids to the result set
					orderedUris.retainAll((Collection<String>) filterMethod.invoke(null, args)); 
				}
			}
		}
		
		JSONArray jsonArray = new JSONArray(orderedUris); // Transform List to json
		jsonArray.write(response.getWriter()); // Write json to http response
		response.setStatus(200);
	}	
}
