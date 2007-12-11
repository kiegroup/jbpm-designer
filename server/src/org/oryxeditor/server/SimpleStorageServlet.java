package org.oryxeditor.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SimpleStorageServlet extends HttpServlet {

	// serialization id.
	private static final long serialVersionUID = -5801302483240001557L;

	// database connection configuration
	// TODO put this into a configuration file.
	private static final String username = "oryx";
	private static final String password = "";
	private static final String url = "jdbc:mysql://localhost/test";
	private static final String connector = "com.mysql.jdbc.Driver";

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		Connection conn = null;

		try {

			Class.forName(connector).newInstance();
			conn = DriverManager.getConnection(url, username, password);

		} catch (Exception e) {

			this.showError(e, res);

		} finally {

			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) { /* ignore close errors */
				}
			}
		}
	}

	private void showError(Exception exception, HttpServletResponse res) {

		PrintWriter out;
		try {
			out = res.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		res.setContentType("text/html");

		out
				.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");

		out
				.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
		out.println("<head>");

		out
				.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>");
		out.println("<title>Oryx - Error</title>");
		out.println("</head>");

		out.println("<body>");
		out.println("<div style=\"text-align: center;\">");
		out
				.println("<img src='./images/crystal/error.png' style='padding-top: 200px;' width='128' height='128'/>");
		out.println("<p>" + exception.toString() + "</p>");
		out.println("<!-- ACTUAL ERROR: ");
		exception.printStackTrace(out);
		out.println(" -->");
		out.println("</div>");
		out.println("</body>");
		out.println("</html>");

		out.close();

	}
}
