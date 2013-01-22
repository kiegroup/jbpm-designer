package org.jbpm.designer.web.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * 
 * Used to store sources to local file system.
 * 
 * @author Tihomir Surdilovic
 */
public class FileStoreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
    	String fname = req.getParameter("fname");
    	String fext = req.getParameter("fext");
    	String data = req.getParameter("data");
    	if(fext != null && fext.equals("bpmn2")) {
    		try {
				resp.setContentType("application/xml; charset=UTF-8");
				resp.setHeader("Content-Disposition",
				        "attachment; filename=\"" + fname + "." + fext + "\"");
				resp.getWriter().write(data);
			} catch (Exception e) {
				resp.sendError(500, e.getMessage());
			}
    	}
    }
}
