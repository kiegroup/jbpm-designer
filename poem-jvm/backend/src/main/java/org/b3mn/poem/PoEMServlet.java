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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.ClassCache;
import org.jruby.runtime.Block;

public class PoEMServlet extends HttpServlet {
	private static final long serialVersionUID = -9128262564769832181L;
	@SuppressWarnings("unchecked")
	private final ClassCache classCache = JavaEmbedUtils.createClassCache(Thread.currentThread().getContextClassLoader());
	private final List<String> loadPaths = new ArrayList<String>();
	private final Ruby ruby;
	
	public PoEMServlet() {
		loadPaths.add("jruby");
		ruby = JavaEmbedUtils.initialize(loadPaths, classCache);
	}

	protected void dispatch(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		ruby.getLoadService().require("dispatcher");
		RubyClass dispatcher = ruby.getClass("Dispatcher");

		IRubyObject[] args = {JavaEmbedUtils.javaToRuby(ruby, request), JavaEmbedUtils.javaToRuby(ruby, response)};
		IRubyObject[] damn_java_hack = {};
		IRubyObject instance = dispatcher.newInstance(damn_java_hack, Block.NULL_BLOCK);
		instance.callMethod(dispatcher.getRuntime().getCurrentContext(), "dispatch", args);
	
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		dispatch(request,response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		dispatch(request,response);
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		dispatch(request,response);
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		dispatch(request,response);
	}

}