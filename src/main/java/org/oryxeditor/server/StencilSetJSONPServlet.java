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

import org.json.JSONArray;
import org.json.JSONObject;


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
		String type = req.getParameter("type");
		String jsonp = req.getParameter("jsonp");
		String embedSvg = req.getParameter("embedsvg");

		try {
			PrintWriter out = res.getWriter();
			
			
			// also allow to request stencil set extensions (specified by namespace)
			if(type!=null && type.equals("ssextension")) {
				
				File extensionsFile = new File(getServletContext().getRealPath("/stencilsets/extensions/extensions.json"));
				BufferedReader reader = new BufferedReader(new FileReader(extensionsFile));
				String line = null; String jsonString = "";
				while (( line = reader.readLine()) != null){
			          jsonString += line;
			    }
				JSONObject jsonObj = new JSONObject(jsonString);
				JSONArray extArr = jsonObj.getJSONArray("extensions");
				for(int i=0; i<extArr.length(); i++) {
					if(extArr.getJSONObject(i).getString("namespace").equals(resource)) {
						String definition = extArr.getJSONObject(i).getString("definition");
						File jsonFile = new File(getServletContext().getRealPath("/stencilsets/extensions/"+definition));
						if(jsonp!=null) out.append(jsonp + "(");
						BufferedReader reader2 = new BufferedReader(new FileReader(jsonFile));
						String line2 = null;
						while (( line2 = reader2.readLine()) != null){
					          out.append(line2);
					          out.append(System.getProperty("line.separator"));
					    }
						if(jsonp!=null) out.append(");");
					}
				}
				
			} else { // standard stencil set requested (specified by path)
				
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
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
