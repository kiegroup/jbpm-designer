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

public class PoEMServlet extends HttpServlet {
	private static final long serialVersionUID = -9128262564769832181L;
	@SuppressWarnings("unchecked")
	private final ClassCache classCache = JavaEmbedUtils.createClassCache(Thread.currentThread().getContextClassLoader());
	private final List<String> loadPaths = new ArrayList<String>();
	private final Ruby ruby;
	
	public PoEMServlet() {
		//loadPaths.add(".");
		loadPaths.add("/jruby");
		//loadPaths.add("/Users/sixtus/src/new-poem/webapps/ROOT/WEB-INF/classes");
		ruby = JavaEmbedUtils.initialize(loadPaths, classCache);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//HttpOutput httpOutput = new HttpOutput(response);
		//RubyIO stdin = new RubyIO(ruby, request.getInputStream());
		//RubyIO output = new RubyIO(ruby, httpOutput);
		
		IRubyObject[] args = {JavaEmbedUtils.javaToRuby(ruby, request), JavaEmbedUtils.javaToRuby(ruby, response)};
		//ruby.getLoadService().require("poem/dispatcher");
		//RubyClass dispatcher = (RubyClass) ruby.getClass("Dispatcher");
		//dispatcher.callMethod(ruby.getCurrentContext(), "dispatch", args);
	}

}
