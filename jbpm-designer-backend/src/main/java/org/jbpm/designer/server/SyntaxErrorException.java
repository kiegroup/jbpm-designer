/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.designer.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Gero.Decker
 */
public class SyntaxErrorException extends Exception {
	
	private static final long serialVersionUID = 4122105347895713305L;
	private Map<String,String> errors = new HashMap<String,String>();
	
	/**
	 * 
	 * @param errors key = resourceID, value = error text
	 */
	public SyntaxErrorException(Map<String,String> errors) {
		this.errors = errors;
	}

	public Map<String,String> getErrors() {
		return errors;
	}
	
	@Override
	public String getMessage() {
		String message = "";
		boolean isFirstEntry = true;
		for (Entry<String,String> error: errors.entrySet()) {
			if (isFirstEntry) isFirstEntry = false;
			else message = message+", ";
			
			message = message+error.getValue()+" ("+error.getKey()+")";
		}
		return message;
	}

}


