package org.jbpm.designer.web.server.menu;

import org.apache.log4j.Logger;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.web.profile.IDiagramProfile;
import org.jbpm.designer.web.profile.IDiagramProfileService;
import org.jbpm.designer.web.profile.impl.ProfileServiceImpl;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

public class MenuServlet extends HttpServlet {
    private Document _doc = null;
    @Inject
    private IDiagramProfileService _profileService = null;
    private static final Logger _logger = Logger.getLogger(MenuServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        _profileService.init(config.getServletContext());
        String menufile = config.getServletContext().getRealPath("/menu.html");
        try {
            _doc = readDocument(menufile);
        } catch (Exception e) {
            throw new ServletException(
                    "Error while parsing menu.html", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Document doc = (Document) _doc.clone();
        String profileName = request.getParameter("profile");
        if(profileName == null || profileName.length() < 1) {
            // default to jbpm
            profileName = "jbpm";
        }
        IDiagramProfile profile = _profileService.findProfile(
                request, profileName);
        if (profile == null) {
            _logger.error("No profile with the name " + profileName
                    + " was registered");
            throw new IllegalArgumentException(
                    "No profile with the name " + profileName +
                            " was registered");
        }
        Repository repo = profile.getRepository();
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setExpandEmptyElements(true);
        outputter.setFormat(format);
        String html = outputter.outputString(doc);
        StringTokenizer tokenizer = new StringTokenizer(html, "@", true);
        StringBuilder resultHtml = new StringBuilder();
        boolean tokenFound = false;
        boolean replacementMade = false;

        while(tokenizer.hasMoreTokens()) {
            String elt = tokenizer.nextToken();
            if ("rootid".equals(elt)) {
                resultHtml.append("vfs");
                replacementMade = true;
            } else if ("rootname".equals(elt)) {
                resultHtml.append("Designer Repository");
                replacementMade = true;
            } else if ("language".equals(elt)) {
                resultHtml.append("en");
                replacementMade = true;
            } else if ("@".equals(elt)) {
                if (replacementMade) {
                    tokenFound = false;
                    replacementMade = false;
                } else {
                    tokenFound = true;
                }
            } else {
                if (tokenFound) {
                    tokenFound = false;
                    resultHtml.append("@");
                }
                resultHtml.append(elt);
            }
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(resultHtml.toString());
    }

    private static Document readDocument(String path) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder(false);
        builder.setValidation(false);
        builder.setFeature("http://xml.org/sax/features/validation", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        Document anotherDocument = builder.build(new File(path));
        return anotherDocument;
    }
}