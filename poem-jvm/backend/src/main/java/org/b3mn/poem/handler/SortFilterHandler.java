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

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.b3mn.poem.Identity;
import org.b3mn.poem.Persistance;
import org.b3mn.poem.TagRelation;
import org.b3mn.poem.business.Model;
import org.b3mn.poem.business.User;
import org.b3mn.poem.util.FilterMethod;
import org.b3mn.poem.util.SortMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.tools.javac.tree.Tree.Annotation;



public class SortFilterHandler extends HandlerBase {
	
	private Map<String, Method> filterMapping = new HashMap<String, Method>();
	private Map<String, Method> sortMapping = new HashMap<String, Method>();
	
	protected static boolean isSuperclass(Class<?> subClass, Class<?> parentClass) {
		if (subClass.equals(parentClass)) return true;
		if (subClass.getSuperclass() == null) return false;
		return isSuperclass(subClass.getSuperclass(), parentClass);
	}
	
	@Override
	public void init() {
		try {
			load();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	// Search all handlers for static methods that are annotated with FilterMethod or SortMethod  
	protected void load() throws Exception {
		for (String handlerName : getDispatcher().getHandlerClassNames()) {
			Class<?> handlerClass = Class.forName(handlerName);
			// Iterate over all public methods of the class
			for (Method method : handlerClass.getMethods()) {  
				// Find filtering methods ************************************************************
				// Check if the method is static, annotated with the FilterMethod annotation,
				// returns a collection and has the right parameters
				for (java.lang.annotation.Annotation annotation : method.getAnnotations()) {
					System.out.println("Oryx:"  + method.getDeclaringClass().getName() + "  -   "+ annotation.getClass().getName());
				}
				if ((method.getAnnotation(FilterMethod.class) != null) && 
						(isSuperclass(method.getReturnType(),Collection.class)) && 
						(method.getParameterTypes().length == 2) &&
						((method.getModifiers() & Modifier.STATIC) != 0)) {
					// Check: 1st parameter: Identity, 2nd String
					if ((method.getParameterTypes()[0].equals(Identity.class)) && 
							(method.getGenericParameterTypes()[1].equals(String.class))) {
						// If no filter name is supplied by the annotation, use the method name
						// Note: filter names are case-insensitive
						String filterName = method.getName().toLowerCase();
						
						if (!method.getAnnotation(FilterMethod.class).FilterName().equals("")) {
							filterName = method.getAnnotation(FilterMethod.class).FilterName().toLowerCase();
						}
						this.filterMapping.put(filterName, method);
					}
				}
				// Find sorting methods ************************************************************
				// Check if the method is static, annotated with the SortMethod annotation,
				// returns a list and has the right parameters  
				if ((method.getAnnotation(SortMethod.class) != null) && 
						(isSuperclass(method.getReturnType(),List.class)) && 
						(method.getParameterTypes().length == 1) &&
						((method.getModifiers() & Modifier.STATIC) != 0)) {
					// Check: 1st and only parameter: Identity
					if (method.getParameterTypes()[0].equals(Identity.class)) {			
						// If no filter name is supplied by the annotation, use the method name
						// Note: filter names are case-insensitive
						String sortName = method.getName().toLowerCase();

						if (!method.getAnnotation(SortMethod.class).SortName().equals("")) {
							sortName = method.getAnnotation(SortMethod.class).SortName().toLowerCase();
						}
						this.sortMapping.put(sortName, method);
					}
				}											
			}
		}
	}
	
	
	/* This handler returns a json encoded array of model ids that 
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		String sortName = request.getParameter("sort");
		String defaultSort = "lastchange";
		if (sortName == null) {
			sortName = defaultSort; // set default filter
		} 
		
		sortName = sortName.toLowerCase();
		Method sortMethod = this.sortMapping.get(sortName);
		
		if (sortMethod == null) {
			sortName = defaultSort; // set default filter
			sortMethod = this.sortMapping.get(sortName);
		}

		Object[] arg = { subject };
		List<Integer> orderedIds= (List<Integer>) sortMethod.invoke(null, arg);
		
		// Iterate over http parameters
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String filterName = (String) e.nextElement();
			if (!filterName.equals("sort")) {
				Method filterMethod = this.filterMapping.get(filterName.toLowerCase());
				// If the filter method exists
				if (filterMethod != null) {
					Object[] args = { subject, request.getParameter(filterName) };
					// Invoke the filter method an add the filtered ids to the result set
					orderedIds.retainAll((Collection<Integer>) filterMethod.invoke(null, args)); 
				}
			}
		}
		
		JSONArray jsonArray = new JSONArray(orderedIds); // Transform List to json
		jsonArray.write(response.getWriter()); // Write json to http response
		response.setStatus(200);
	}
	

	
	/* Returns all modelIds of the input which are tagged with the tags passed in the params 
	 * parameter. params must be an JSON array of tags
	 */
	@FilterMethod(FilterName="tags")
	public Collection<Integer> tagFilter(int subjectId, Collection<Integer> modelIds, String params) throws Exception {
		
		JSONArray jsonArray = new JSONArray(params);
		String sqlTagQuery = "";
		for (int i = 0; i < jsonArray.length() - 1; i++) {
			try {
				sqlTagQuery += " tag_definition.name='" + jsonArray.getString(i) + "' OR ";
			} catch (JSONException e) {}
		}
		try {
			sqlTagQuery += " tag_definition.name='" + jsonArray.getString(jsonArray.length() - 1) + "' ";
		} catch (JSONException e) {}
			
		// Access database directly to minimize performance impact
		List<?> databaseIds = Persistance.getSession()
				.createSQLQuery("SELECT tag_relation.object_id "
				+ "FROM tag_relation, tag_definition, access "
				+ "WHERE tag_relation.tag_id=tag_definition.id "
				+ "AND access.subject_id=:subject_id "
				+ "AND tag_relation.object_id=access.object_id " 
				+ "AND(" + sqlTagQuery + ")")
				//.addEntity("tag_relation", TagRelation.class)
				.setInteger("subject_id", subjectId) 
				.list();
		
		Persistance.commit();
		ArrayList<Integer> outputIds = new ArrayList<Integer>();
		for (Integer modelId : modelIds) {
			if (databaseIds.contains(modelId)) {
				outputIds.add(modelId);
			}
		}
		
		return outputIds;
	}
}
