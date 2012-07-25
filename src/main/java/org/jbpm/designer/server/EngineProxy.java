package org.jbpm.designer.server;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.codec.binary.Base64;

public class EngineProxy extends HttpServlet {
	private static final long serialVersionUID = -596209118625017987L;
	private static Configuration config = null;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			String engineURL = req.getParameter("url");

			String user = req.getHeader("Authorization");
			if (user != null) {
				java.util.StringTokenizer st = new java.util.StringTokenizer(
						user);
				if (st.hasMoreTokens()) {
					if (st.nextToken().equalsIgnoreCase("Basic")) {
						String userPass = new String(Base64.decodeBase64(st.nextToken()));
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
			String encoding = Base64.encodeBase64String((user + ":").getBytes());
			connection_engine.setRequestProperty("Authorization", "Basic " + encoding);
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
/*
				xmlDoc = xmlDoc.replaceAll("href=\"/", "href=\"/oryx/engineproxy?url="+url_engine.getProtocol()+"://"+url_engine.getHost()+":"+url_engine.getPort()+"/");
				xmlDoc = xmlDoc.replaceAll("src=\"/", "src=\"/oryx/engineproxy?url="+url_engine.getProtocol()+"://"+url_engine.getHost()+":"+url_engine.getPort()+"/");
				xmlDoc = xmlDoc.replaceAll("action=\"/", "action=\"/oryx/engineproxy?url="+url_engine.getProtocol()+"://"+url_engine.getHost()+":"+url_engine.getPort()+"/");
*/
				PrintWriter out = resp.getWriter();
				
				out.print(xmlDoc);

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
