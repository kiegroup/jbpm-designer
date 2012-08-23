package org.jbpm.designer.server;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Copyright (c) 2007 Martin Czuchra.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class SimpleStorageServlet extends HttpServlet {

    // serialization id.
    private static final long serialVersionUID = -5801302483240001557L;
    private static final boolean DEBUG = false;

    private static Configuration config = null;
    private Connection database = null;
    private String currentResource = null;

    private void prepare(HttpServletRequest req, HttpServletResponse res)
	    throws InstantiationException, IllegalAccessException,
	    ClassNotFoundException, SQLException, ConfigurationException {

	if (SimpleStorageServlet.config == null)
	    SimpleStorageServlet.config = new PropertiesConfiguration(
		    "database.properties");

	String connector = SimpleStorageServlet.config
		.getString("db.connector");
	String url = SimpleStorageServlet.config.getString("db.url");
	String username = SimpleStorageServlet.config.getString("db.username");
	String password = SimpleStorageServlet.config.getString("db.password");

	Class.forName(connector).newInstance();
	this.database = DriverManager.getConnection(url, username, password);

	this.currentResource = req.getParameter("resource");
    }

    private void process(boolean isPost, HttpServletRequest req,
	    HttpServletResponse res) {

	try {

	    prepare(req, res);

	    if (isPost) {

		this.storeResource(req, res);

	    } else if (this.currentResource == null)

		this.showProcessList(req, res);

	    else {

		String stencilsetURL = req.getParameter("stencilset");
		this.showResource(req, res, stencilsetURL);

	    }

	} catch (Exception e) {

	    this.showError(e, res);

	} finally {

	    if (database != null) {
		try {
		    database.close();
		} catch (Exception e) { /* ignore close errors */
		}
	    }
	}
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {

	this.process(false, req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
	    throws ServletException, IOException {

	this.process(true, req, res);
    }

    protected void storeResource(HttpServletRequest req, HttpServletResponse res)
	    throws SQLException {

	String data = req.getParameter("data");

	PreparedStatement stmt = database
		.prepareStatement("SELECT ID FROM sites WHERE Name = ?");
	stmt.setString(1, this.currentResource);

	if (stmt.executeQuery().next()) {

	    PreparedStatement store = database
		    .prepareStatement("UPDATE sites SET Site = ? WHERE Name = ?");
	    store.setString(1, data);
	    store.setString(2, this.currentResource);
	    store.execute();

	} else {

	    PreparedStatement store = database
		    .prepareStatement("INSERT INTO sites (Name, Site) VALUES (?, ?)");
	    store.setString(2, data);
	    store.setString(1, this.currentResource);
	    store.execute();
	}
    }

    public void showResource(HttpServletRequest req, HttpServletResponse res,
	    String stencilsetURL) throws SQLException {

	res.setContentType("application/xhtml+xml");

	PreparedStatement stmt = database
		.prepareStatement("SELECT ID, Site FROM sites WHERE Name = ?");
	stmt.setString(1, this.currentResource);

	stmt.execute();

	ResultSet rs = stmt.getResultSet();
	String result;

	if (rs.next())

	    result = rs.getString(2);

	else {

	    result = "<div class=\"-oryx-canvas\" id=\"oryx-canvas123\" style=\"width:1200px; height:600px;\">";
	    result += "<a href=\"" + stencilsetURL
		    + "\" rel=\"oryx-stencilset\"></a>";
	    result += "<span class=\"oryx-mode\">writeable</span>";
	    result += "<span class=\"oryx-mode\">fullscreen</span>";
	    result += "</div>";
	}

	this.template(req, res, result);

    }

    public void template(HttpServletRequest req, HttpServletResponse res,
	    String resource) throws SQLException {

	PrintWriter out;
	try {
	    out = res.getWriter();
	} catch (IOException e) {
	    e.printStackTrace();
	    return;
	}

	out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
	out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\"");
	out.println("xmlns:b3mn=\"http://b3mn.org/2007/b3mn\"");
	out.println("xmlns:ext=\"http://b3mn.org/2007/ext\"");
	out
		.println("xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");
	out.println("xmlns:atom=\"http://b3mn.org/2007/atom+xhtml\">");

	out.println("<head profile=\"http://purl.org/NET/erdf/profile\">");

	out.println("<title>" + this.currentResource + " - Oryx</title>");

	out.println("<!-- libraries -->");
	out
		.println("<script src=\"lib/prototype-1.5.1.js\" type=\"text/javascript\" />");
	out
		.println("<script src=\"lib/path_parser.js\" type=\"text/javascript\" />");
	out
		.println("<script src=\"lib/ext-2.0.2/adapter/yui/yui-utilities.js\" type=\"text/javascript\" />");
	out
		.println("<script src=\"lib/ext-2.0.2/adapter/yui/ext-yui-adapter.js\" type=\"text/javascript\" />");
	out
		.println("<script src=\"lib/ext-2.0.2/ext-all.js\" type=\"text/javascript\" />");
	out
		.println("<script src=\"lib/ext-2.0.2/color-field.js\" type=\"text/javascript\" />");
	out.println("<style media=\"screen\" type=\"text/css\">");
	out.println("@import url(\"lib/ext-2.0.2/resources/css/ext-all.css\");");
	out
		.println("@import url(\"lib/ext-2.0.2/resources/css/xtheme-gray.css\");");
	out.println("</style>");

	out
		.println("<script src=\"shared/kickstart.js\" type=\"text/javascript\" />");
	out
		.println("<script src=\"shared/erdfparser.js\" type=\"text/javascript\" />");
	out
		.println("<script src=\"shared/datamanager.js\" type=\"text/javascript\" />");

	out.println("<!-- oryx editor -->");
	out.println("<script src=\""
		+ (SimpleStorageServlet.DEBUG ? "oryx.debug.js" : "oryx.js")
		+ "\" type=\"text/javascript\" />");
	out
		.println("<link rel=\"Stylesheet\" media=\"screen\" href=\"css/theme_norm.css\" type=\"text/css\" />");

	out.println("<!-- erdf schemas -->");
	out
		.println("<link rel=\"schema.dc\" href=\"http://purl.org/dc/elements/1.1/\" />");
	out
		.println("<link rel=\"schema.dcTerms\" href=\"http://purl.org/dc/terms/\" />");
	out.println("<link rel=\"schema.b3mn\" href=\"http://b3mn.org\" />");
	out
		.println("<link rel=\"schema.oryx\" href=\"http://oryx-editor.org/\" />");
	out
		.println("<link rel=\"schema.raziel\" href=\"http://raziel.org/\" />");

	// providing a base is essential for the layouter plugin, as the
	// underlying rdf parser does not allow relative urls, and the
	// extract-rdf xsl script by Ian Davis doesn't respect xml:base but the
	// html base element. However, ehen providing a base, fragments refering
	// to fetched svg files break, since they now all refer to the base
	// element's href attribute. Latter makes the arrowheads disappear. To
	// fix this, the layouter plugin currently adds a base element before
	// sending the current data back to the server.
	// out.println("<base href=\"" + req.getRequestURL() + "\" />");

	out.println("</head>");

	out.println("<body style=\"overflow:hidden;\"><div class='processdata' style='display:none'>");

	out.println(resource);

	out.println("</div>");
	out.println("<div class='processdata'></div>");
	out.println("</body>");
	out.println("</html>");
    }

    /**
     * Returns a map of all available stencil sets, which means, all currently
     * installed stencil sets. It therefore traverses all subfolders of the
     * stencil sets folder and regards each JSON file within them as a valid
     * stencil set. All files ending with ".json" are considered JSON files.
     * 
     * @return a map of all available stencilsets (name => url).
     */
    private Map<String, URL> getAvailableStencilsets(URL base) {

	// TODO make stencilsets folder configurable.
	// TODO make webapp name be found dynamically.
	String webappName = "/oryx";
	String stencilsetPath = this.getServletContext().getRealPath("/")
		+ File.separator + "stencilsets" + File.separator;
	Map<String, URL> stencilsets = new HashMap<String, URL>();
	File dir = new File(stencilsetPath);

	// if dir is not a directory
	if (!(dir.isDirectory()))
	    return stencilsets;

	// for each folder within...
	for (File contained : dir.listFiles()) {
	    if (!(contained.isDirectory()))
		continue;

	    // find the included json files.
	    for (File jsonFile : contained.listFiles(new FilenameFilter() {
		public boolean accept(File dir, String name) {
		    return name.endsWith(".json");
		}
	    })) {

		try {

		    // construct url for the stencilset
		    URL location = new URL(base.getProtocol(), base.getHost(),
			    base.getPort(), webappName + "/stencilsets/"
				    + contained.getName() + "/"
				    + jsonFile.getName());

		    String name = this.lookupStencilsetName(jsonFile.getName(),
			    location);

		    // put it in the map
		    stencilsets.put(name, location);

		} catch (MalformedURLException e) {

		    // if there is a problem, ignore this stencil set.
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}

	return stencilsets;
    }

    private String lookupStencilsetName(String name, URL location) {
	return name.substring(0, name.length() - 5);
    }

    public void showProcessList(HttpServletRequest req, HttpServletResponse res)
	    throws SQLException, MalformedURLException {

	PreparedStatement stmt = database
		.prepareStatement("SELECT ID, Name FROM sites");

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
	out.println("<title>Oryx - Process Overview</title>");
	out.println("</head>");

	out.println("<body>");
	out.println("<div style=\"text-align: center;\">");

	out.println("<p>");
	out.println("<form action='" + req.getRequestURL() + "' method='get'>");
	out.println("Create a new process:<br/>");
	out
		.println("<img src='./images/crystal/empty.png' style='float: clear;' width='128' height='128'/><br/>");
	out.println("<input type='text' name='resource' value='' />");

	// get available stencil sets.
	URL base = new URL(req.getRequestURL().toString());
	Map<String, URL> stencilsets = getAvailableStencilsets(base);

	out.println("<select name=\"stencilset\" size=\"1\">");

	for (String name : stencilsets.keySet()) {
	    URL url = stencilsets.get(name);
	    out.println("<option value=\"" + url.toString() + "\">");
	    out.println(name);
	    out.println("</option>");
	}
	out.println("</select>");
	out.println("<input type='submit' value='Add' />");

	out.println("</form>");
	out.println("</p>");

	if (stmt.execute()) {

	    ResultSet rs = stmt.getResultSet();
	    out
		    .println("<div style='text-align:left; float: clear;'>Or review an existing one:<br/></div>");

	    while (rs.next()) {

		out
			.println("<div style='padding: 16px; display: inline; float: left;'>");
		out.println("<div>");
		out.println("<a href='" + req.getRequestURL() + "?resource="
			+ rs.getString(2) + "' style='text-decoration: none'>");
		out
			.println("<img src='./images/crystal/misc.png' border='0' width='128' height='128'/><br/>");
		out.println(rs.getString(2));
		out.println("</a>");
		out.println("</div>");
		out.println("</div>");
	    }

	} else {
	    out.println("There currently are no saved processes.<br/>");
	}

	out.println("</div>");
	out.println("</body>");
	out.println("</html>");
    }

    public void showError(Exception exception, HttpServletResponse res) {

	PrintWriter out;
	try {
	    out = res.getWriter();
	} catch (IOException e) {
	    e.printStackTrace();
	    return;
	}

	//System.out.println(exception);
	exception.printStackTrace();

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
