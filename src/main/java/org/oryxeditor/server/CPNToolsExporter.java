package org.oryxeditor.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.cpn.converter.CPNConverter;


public class CPNToolsExporter extends HttpServlet
{
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{		
			PrintWriter out = null;		
			out = response.getWriter();
			
			String json = request.getParameter("data");		
		
			String cpnfileString = CPNConverter.convertToCPNFile(json);
			
			if (cpnfileString.startsWith("error:"))
			{
				out.write(cpnfileString);
			}
			else
			{
				out.write(cpnfileString);
			}
			
		} catch (Exception e) 
		{
			e.printStackTrace();
			
			PrintWriter out = null;
			out = response.getWriter();
			out.write("error," + e.getMessage());
		}		
	}
}