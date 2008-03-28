package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Worklist extends HttpServlet {

	private static final long serialVersionUID = -2313072133919578353L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String engineURL = "http://localhost:3000/cases";
		
		URL url_engine = new URL(engineURL);
		HttpURLConnection connection_engine = (HttpURLConnection)url_engine.openConnection();
		connection_engine.setRequestMethod("GET");

		connection_engine.setDoInput(true);

		connection_engine.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection_engine.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");

		connection_engine.connect();
		
		if (connection_engine.getResponseCode() == 200) {	
			DataInputStream in = new DataInputStream(connection_engine.getInputStream());
			String str;
			String xmlDoc = "";
			while ((str = in.readLine()) != null) {
				xmlDoc += str + " ";
			}
			
			String jsString = "";

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			Vector<String> jsData = new Vector<String>();
			try {
				builder = factory.newDocumentBuilder();
				Document document = builder.parse(new ByteArrayInputStream(xmlDoc.getBytes("utf-8")));
				NodeList cases = document.getDocumentElement().getChildNodes();
				for (int i = 0; i < cases.getLength(); i++) {
					//jsData.add(cases.item(i).getNodeName());
					if (!cases.item(i).getNodeName().equals("task")) 
						continue;
					Node curCase = cases.item(i);
					String case_id = "";
					String transition_id = "";
					String transition_name = "";
					for (int j=0; j < curCase.getChildNodes().getLength(); j++) {
						if (curCase.getChildNodes().item(j).getNodeName().equals("case_id")) {
							case_id = curCase.getChildNodes().item(j).getTextContent();
						} else if (curCase.getChildNodes().item(j).getNodeName().equals("transition_id")) {
							transition_id = "<a target=\"form_frame\" href=\"http://localhost:3000/transition/" + curCase.getChildNodes().item(j).getTextContent() + "\">open task</a>";
						} else if (curCase.getChildNodes().item(j).getNodeName().equals("transition_name")) {
							transition_name = curCase.getChildNodes().item(j).getTextContent();
						}
					}
					jsData.add("['"+case_id+"','"+transition_id+"','"+transition_name+"']");
				}
				
				jsString = StringUtils.join(jsData, ", ");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String bla = 
			 "Ext.onReady(function(){\n" + 
			 "    var myData = [ " + jsString + " ];\n" + 

			 "    // create the data store\n" + 
			 "    var store = new Ext.data.SimpleStore({\n" + 
			 "        fields: [ {name: 'case_id'}, {name: 'transition_id'}, {name: 'transition_name'} ]\n" + 
			 "    });\n" + 
			 "    store.loadData(myData);\n" + 

			 "    // create the Grid\n" + 
			 "    var grid = new Ext.grid.GridPanel({\n" + 
			 "        store: store,\n" + 
			 "        columns: [ {id:'case_id', header: \"case_id\", sortable: true, dataIndex: 'case_id'},\n" +
			 "{ header: \"Link to form\", sortable: true, dataIndex: 'transition_id'},\n" +
			 "{ header: \"transition_name\", sortable: true, dataIndex: 'transition_name'},\n" +
			 " ],\n" + 
			 "        height:350,\n" + 
			 "        width:380,\n" + 
			 "        title:'Worklist'\n" + 
			 "    });\n" + 

			 "    grid.render('worklist-grid');\n" + 

			 "    grid.getSelectionModel().selectFirstRow();\n" + 
			 "});";

			
			
			PrintWriter out = resp.getWriter();
			
/*			out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:ext=\"http://b3mn.org/2007/ext\">");
*/
			out.println("<html><head>");
			out.println("<title>Worklist</title>");
			out.println("<script src=\"lib/ext-2.0.2/adapter/ext/ext-base.js\" type=\"text/javascript\" ></script>");			
			out.println("<script src=\"lib/ext-2.0.2/ext-all-debug.js\" type=\"text/javascript\" ></script>");			

			out.println("<script type=\"text/javascript\">" + bla + "</script>");

			out.println("<style type=\"text/css\">\n" +
					"@import url(\"lib/ext-2.0.2/resources/css/ext-all.css\");\n" +
					"@import url(\"lib/ext-2.0.2/resources/css/xtheme-gray.css\");\n" +
					"</style>\n");

			out.println("</head>");

			out.println("<body><table><tr colspan=2><td align=\"center\"><h3>Extreme Universal Worklist <span style=\"font-size: 40px\">2.0</span></h3></td></tr>" +
					"<tr>" +
					"<td width=500 valign=\"top\"><div id=\"worklist-grid\"></div></td>" +
					"<td><iframe name=\"form_frame\" width=500 height=600 src=\"about:blank\" /></td>" +
					"</tr></table></body>");

			out.println("</html>");
			
		}
		
		
	}
}
