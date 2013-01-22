package org.jbpm.designer.web.server;

import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * Deals with stencil patterns data.
 *
 * @author Tihomir Surdilovic
 */
public class StencilPatternsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger _logger = Logger.getLogger(StencilPatternsServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // for now just return the patterns data json
        try {
            String patternsDataPath = getServletContext().getRealPath("/defaults/patterns.json");

            PrintWriter pw = resp.getWriter();
            resp.setContentType("text/json");
            resp.setCharacterEncoding("UTF-8");
            pw.write(readFile(patternsDataPath));
        } catch (IOException e) {
            _logger.error(e.getMessage());
            PrintWriter pw = resp.getWriter();
            resp.setContentType("text/json");
            resp.setCharacterEncoding("UTF-8");
            pw.write("");
        }
    }

    private String readFile(String pathname) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new File(pathname), "UTF-8");
        String lineSeparator = System.getProperty("line.separator");
        try {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }
}
