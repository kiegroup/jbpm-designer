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

package org.b3mn.poem.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.b3mn.poem.Identity;
import org.b3mn.poem.handler.HandlerBase;

public class HandlerInfo {
	protected String uri;
	protected boolean needsModelContext;
	protected boolean permitPublicUserAccess;
	protected boolean filterBrowser;
	protected Map<String, AccessRight> accessRights = new HashMap<String, AccessRight>();
	
	protected Class<? extends HandlerBase> handlerClass = null;
	protected HandlerBase handlerInstance = null;

	public String getUri() {
		return uri;
	}

	public boolean isNeedsModelContext() {
		return needsModelContext;
	}

	public boolean isPermitPublicUserAccess() {
		return permitPublicUserAccess;
	}

	public boolean isFilterBrowser() {
		return filterBrowser;
	}

	public HandlerBase getHandlerInstance() {
		return handlerInstance;
	}
	
	public void setHandlerInstance(HandlerBase handler) {
		this.handlerInstance = handler;
	}
	
	public Class<? extends HandlerBase> getHandlerClass() {
		return handlerClass;
	}
	
	public HandlerInfo(String className) throws ClassNotFoundException {
		Class<? extends HandlerBase> handlerClass = (Class<? extends HandlerBase>) Class.forName(className);
		init(handlerClass);
	}
	
	public HandlerInfo(Class<? extends HandlerBase> handlerClass) {
		init(handlerClass);
	}
	
	private void init(Class<? extends HandlerBase> handlerClass) {
		if (handlerClass.getAnnotation(HandlerWithoutModelContext.class) != null) {
			HandlerWithoutModelContext annotation = handlerClass.getAnnotation(HandlerWithoutModelContext.class);
			this.uri = annotation.uri();
			this.needsModelContext = false;
			this.permitPublicUserAccess = annotation.permitPublicUserAccess();
			this.filterBrowser = annotation.filterBrowser();
			this.handlerClass = handlerClass;
		}
		if (handlerClass.getAnnotation(HandlerWithModelContext.class) != null) {
			HandlerWithModelContext annotation = handlerClass.getAnnotation(HandlerWithModelContext.class);
			this.uri = annotation.uri();
			this.needsModelContext = false;
			this.permitPublicUserAccess = annotation.permitPublicUserAccess();
			this.filterBrowser = annotation.filterBrowser();
			this.handlerClass = handlerClass;
			this.accessRights.put(null, annotation.accessRestriction());
			
			// Assign individual operation access restriction from method annotations
			for (Method method : handlerClass.getMethods()) {
				// If the method is one of the 4 REST methods an takes 4 parameters
				if ((method.getName().equals("doGet") || method.getName().equals("doPut") || 
						method.getName().equals("doPost") || method.getName().equals("doDelete")) && 
						(method.getParameterTypes().length == 4)) {
					
					Class<?>[] parameters = method.getParameterTypes();
					// Validate parameter types
					if (parameters[0].equals(HttpServletRequest.class) && parameters[1].equals(HttpServletResponse.class) && 
							parameters[2].equals(Identity.class) && parameters[3].equals(Identity.class)) {
						
						String operation = method.getName().substring(2).toLowerCase();
						if (method.getAnnotation(RestrictAccess.class) != null) {
							this.accessRights.put(operation, method.getAnnotation(RestrictAccess.class).value());
						}
					}				
				}
			}
		}
		if (handlerClass.getAnnotation(ExportHandler.class) != null) {
			ExportHandler annotation = handlerClass.getAnnotation(ExportHandler.class);
			this.uri = annotation.uri();
			this.needsModelContext = false;
			this.permitPublicUserAccess = annotation.permitPublicUserAccess();
			this.filterBrowser = annotation.filterBrowser();
			this.handlerClass = handlerClass;
			this.accessRights.put(null, annotation.accessRestriction());
		}
	}

	public AccessRight getAccessRestriction(String operation) {
		operation = operation.toLowerCase();
		AccessRight right = this.accessRights.get(operation);
		// if a specific access right for a method doesn't exist, 
		// try to get the access right from the class scope
		if ((right == null) && operation != null) {
			right = this.accessRights.get(null);
		}
		return this.accessRights.get(operation);
	}
}
