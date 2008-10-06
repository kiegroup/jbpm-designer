/***************************************
 * Copyright (c) 2008
 * Ole Eckermann
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

package org.b3mn.poem;

import java.io.Serializable;

public class Access implements Serializable {
	
	private static final long serialVersionUID = 2216329517850461299L;

    private long context_id;
    private String context_name;
    private long subject_id;
    private String subject_name;
    private long object_id;
    private String object_name;
    private String access_scheme;	
    private String access_term;
    private long access_id;


	public String getAccess_scheme() {
		return access_scheme;
	}
	public String getAccess_term() {
		return access_term;
	}
	public long getContext_id() {
		return context_id;
	}
	public String getContext_name() {
		return context_name;
	}
	public long getObject_id() {
		return object_id;
	}
	public String getObject_name() {
		return object_name;
	}
	public long getSubject_id() {
		return subject_id;
	}
	public long getAccess_id() {
		return access_id;
	}
	public String getSubject_name() {
		return subject_name;
	}
	public void setSubject_id(long subject_id) {
		this.subject_id = subject_id;
	}
	public void setSubject_name(String subject_name) {
		this.subject_name = subject_name;
	}
	public void setObject_id(long object_id) {
		this.object_id = object_id;
	}
	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}
	public void setContext_id(long context_id) {
		this.context_id = context_id;
	}
	public void setContext_name(String context_name) {
		this.context_name = context_name;
	}
	public void setAccess_scheme(String access_scheme) {
		this.access_scheme = access_scheme;
	}
	public void setAccess_term(String access_term) {
		this.access_term = access_term;
	}
	public void setAccess_id(long access_id) {
		this.access_id = access_id;
	}
	public String getUri() {
		return this.getObject_name() + "/access?id=" +  this.getAccess_id();
	}
	public String getSubject() {
		return (this.context_name.equals("ownership")) ? this.getSubject_name() : this.getContext_name();
	}
	public String getPredicate() {
		return this.getAccess_term();
	}
	
	public int hashCode() {
		int result = 0;
		result+= access_id;
		result+= context_id;
		result+= context_name.hashCode();
		result+= subject_id;
		result+= subject_name.hashCode();
		result+= object_id;
		result+= object_name.hashCode();
		result+= access_scheme.hashCode();	
		result+= access_term.hashCode();
        return result;
	}
	
	public boolean equals(Object other) {
		return false;
	}
}
