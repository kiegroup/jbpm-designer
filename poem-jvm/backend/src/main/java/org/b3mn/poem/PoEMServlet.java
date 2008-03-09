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

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ruby.getLoadService().require("dispatcher");
		RubyClass dispatcher = ruby.getClass("Dispatcher");

		System.out.println("rufe Ruby::Dispatcher");
		IRubyObject[] args = {JavaEmbedUtils.javaToRuby(ruby, request), JavaEmbedUtils.javaToRuby(ruby, response)};
		IRubyObject[] damn_java_hack = {};
		IRubyObject instance = dispatcher.newInstance(damn_java_hack, Block.NULL_BLOCK);
		instance.callMethod(dispatcher.getRuntime().getCurrentContext(), "dispatch", args);
		System.out.println("Ruby::Dispatcher fertig");
	
	}

}