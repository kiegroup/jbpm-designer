package org.oryxeditor.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hpi.cpn.converter.CPNConverter;

public class CPNToolsImporter extends HttpServlet 
{
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			PrintWriter out = null;		
			out = response.getWriter();
			
			String cpnToImport = request.getParameter("data");
			String[] pagesToImport = request.getParameter("pagesToImport").split(";;");
		
			String resultJSONDiagrams = CPNConverter.importPagesNamed(cpnToImport, pagesToImport);
			
			if (resultJSONDiagrams.startsWith("error:"))
			{
				out.write(resultJSONDiagrams);
			}
			else
			{
				out.write(resultJSONDiagrams);
			}			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			PrintWriter out = null;
			out = response.getWriter();
			out.write("error:" + e.getMessage());
		}		
	}	
}