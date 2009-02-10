package org.oryxeditor.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class StencilSetJSONPServlet extends HttpServlet {
	
	private static final long serialVersionUID = 6084194342174761093L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("application/json");
		
		String resource = req.getParameter("resource");
		String jsonp = req.getParameter("jsonp");
		String embedSvg = req.getParameter("embedsvg");

		try {
			PrintWriter out = res.getWriter();
			
			resource.replace("..", ""); // don't allow to leave stencilset dir
			
			if(resource.startsWith("/"))
				resource = resource.substring(1);
			
			if(resource.startsWith("stencilsets/")) // ignore stencilset/ at beginning of path reference
				resource = resource.substring(12);

			File jsonFile;
			if(embedSvg!=null && embedSvg.equals("true")) { // SVG embedding
				jsonFile = new File(getServletContext().getRealPath("/stencilsets/" + resource));
			} else { // no SVG embedding (default)
				// try to find stencilset nosvg representation
				int pIdx = resource.lastIndexOf('.');
				jsonFile = new File(getServletContext().getRealPath("/stencilsets/" + 
						resource.substring(0, pIdx) + "-nosvg" + resource.substring(pIdx)));
				if(!jsonFile.exists())
					jsonFile = new File(getServletContext().getRealPath("/stencilsets/" + resource));
			}
			
			if(!jsonFile.exists()) {
				if(jsonp!=null) out.write(jsonp + "({ \"error\":\"resource not found\" })");
				else out.write("{ \"error\":\"resource not found\" }");
			}
				
			if(jsonp!=null) out.append(jsonp + "(");
			
			BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
			String line = null;
			while (( line = reader.readLine()) != null){
		          out.append(line);
		          out.append(System.getProperty("line.separator"));
		    }
			if(jsonp!=null) out.append(");");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
