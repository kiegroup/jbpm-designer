package org.jbpm.designer.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;

public class Worklist extends HttpServlet {

	private static Configuration config = null;
	private static final long serialVersionUID = -2313072133919578353L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		/*
		 try {
		 if (config == null) {
		 config = new PropertiesConfiguration("pnengine.properties");
		 }
		 } catch (ConfigurationException e1) {
		 e1.printStackTrace();
		 }
		 String engineURL = config.getString("pnengine.url") + "/cases";

		 String user = req.getHeader("Authorization");
		 if (user != null) {
		 java.util.StringTokenizer st = new java.util.StringTokenizer(user);
		 if (st.hasMoreTokens()) {
		 if (st.nextToken().equalsIgnoreCase("Basic")) {
		 BASE64Decoder decoder = // use Base64EncodingUtil
		 String userPass = new String(decoder.decodeBuffer(st.nextToken()));

		 user = userPass.split(":")[0];
		 }
		 }
		 }

		 if (user == null) {
		 resp.setHeader("WWW-Authenticate", "BASIC realm=\"Please type in your username here\"");
		 resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		 return;
		 }

		 URL url_engine = new URL(engineURL);
		 HttpURLConnection connection_engine = (HttpURLConnection) url_engine.openConnection();
		 connection_engine.setRequestMethod("GET");
		 BASE64Encoder encoder = // use Base64EncodingUtil
		 String encoding = encoder.encode((user + ":").getBytes());
		 connection_engine.setRequestProperty("Authorization", "Basic " + encoding);
		 connection_engine.setDoInput(true);

		 connection_engine.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		 */
		//connection_engine.setRequestProperty("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		/*
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
		 String action = "";
		 for (int j = 0; j < curCase.getChildNodes().getLength(); j++) {
		 if (curCase.getChildNodes().item(j).getNodeName().equals("case_id")) {
		 case_id = curCase.getChildNodes().item(j).getTextContent();
		 } else if (curCase.getChildNodes().item(j).getNodeName().equals("transition_id")) {
		 transition_id = curCase.getChildNodes().item(j).getTextContent();
		 } else if (curCase.getChildNodes().item(j).getNodeName().equals("transition_name")) {
		 transition_name = curCase.getChildNodes().item(j).getTextContent();
		 } else if (curCase.getChildNodes().item(j).getNodeName().equals("action")) {
		 action = curCase.getChildNodes().item(j).getTextContent();
		 }
		 }
		 jsData.add("['" + case_id + "','" + transition_id + "','" + transition_name
		 + "','<a target=\"form_frame\" href=\"" + config.getString("pnengine.url") + "/transitions/" + transition_id
		 + "\">" + (action.equals("") ? "default" : action) + "</a>']");
		 }

		 jsString = StringUtils.join(jsData, ", ");

		 } catch (Exception e) {
		 e.printStackTrace();
		 }
		 */
		PrintWriter out = resp.getWriter();

		// mysteriously, this doesn't work if using xhtml ...???!
		/*			out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		 out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:ext=\"http://b3mn.org/2007/ext\">");
		 */
		out.println("<html><head>");
		out.println("<title>Worklist</title>");
		out
				.println("<script type=\"text/javascript\" src=\"lib/ext-2.0.2/adapter/ext/ext-base.js\"></script>");
		out
				.println("<script type=\"text/javascript\" src=\"lib/ext-2.0.2/adapter/yui/yui-utilities.js\"></script>");
		out
				.println("<script type=\"text/javascript\" src=\"lib/ext-2.0.2/adapter/yui/ext-yui-adapter.js\"></script>");
		out
				.println("<script type=\"text/javascript\" src=\"lib/ext-2.0.2/ext-all-debug.js\"></script>");

		// define data for grid and load model code from external file
		//			out.println("<script type=\"text/javascript\">Ext.grid.dummyData = [" + jsString + " ]</script>");
		//			out.println("<script type=\"text/javascript\" src=\"Plugins/worklistGrid.js\"></script>");

		out
				.println("<script type=\"text/javascript\" src=\"Plugins/worklistlayout.js\"></script>");

		out
				.println("<style type=\"text/css\">\n"
						+ "@import url(\"lib/ext-2.0.2/resources/css/ext-all.css\");\n"
						+ "@import url(\"lib/ext-2.0.2/resources/css/xtheme-gray.css\");\n"
						+ "</style>\n");
		out.println("</head>");

		out.println("<body>" +
		/*					"<div id=\"test-container\">" +
		 "<div id=\"worklist-grid\"></div>" +
		 "<div id=\"center\"></div>" +
		 "</div>" +
		 */"" +
		/*					"<div id='west-div' style='border:1px solid black;'></div>" +
		 "<div id=\"north-grid\" style='border:1px solid black;'></div>" +
		 "<div id='worklist-div'>" +
		 "<iframe id='southeast_iframe' frameborder=1 scrolling='auto' style='border:0px none;' src='http://www.google.de'></iframe>" +
		 "</div>" +
		 */
		/*					"" +
		 "<table><tr colspan=2><td align=\"center\">" +
		 "<span style=\"font-size: 40px\">Universal Worklist</span>" +
		 "</td></tr>"
		 + "<tr>"
		 + "<td width=500 valign=\"top\"><div id=\"worklist-grid\"></div></td>"
		 + "<td><iframe name=\"form_frame\" width=500 height=600 src=\"about:blank\" /></td>"
		 + "</tr></table>" +
		 */"</body>");

		out.println("</html>");

	}
}
