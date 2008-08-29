package org.b3mn.poem.servlets;

import javax.servlet.ServletContext;

public class PluginBase {
	private  ServletContext context;
	
	public ServletContext getServletContext() {
		return this.context;
	}
	
	public void setServletContext(ServletContext context) {
		this.context = context;
	}
}
