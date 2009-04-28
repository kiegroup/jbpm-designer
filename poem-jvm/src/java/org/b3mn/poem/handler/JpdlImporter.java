package org.b3mn.poem.handler;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.b3mn.poem.Identity;
import org.b3mn.poem.jbpm.JpdlToJson;
import org.b3mn.poem.util.HandlerWithoutModelContext;
import org.w3c.dom.Document;

@HandlerWithoutModelContext(uri="/new_jpdl")

public class JpdlImporter extends  HandlerBase {

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res, Identity subject, Identity object)  {
		
		res.setContentType("application/json");
			res.setStatus(200);	
			try {
				
				PrintWriter out = res.getWriter();
				String jpdlRepresentation = req.getParameter("data");
				
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document jpdlDoc = builder.parse(new ByteArrayInputStream(jpdlRepresentation.getBytes()));
				
				String result = "";
				result = JpdlToJson.transform(jpdlDoc);
				out.write(result);
				
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}