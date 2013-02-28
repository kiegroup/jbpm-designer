package org.jbpm.designer.filter;

import org.apache.commons.httpclient.HttpClient;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DesignerInjectionFilter implements Filter {
    private FilterConfig fc = null;
    private ServletContext sc = null;
    private InjectionConfig cf;
    private InjectionRules rules;
    public static HttpClient client;

    public void init(FilterConfig filterConfig) throws ServletException {
        fc = filterConfig;
        sc = fc.getServletContext();
        cf = new InjectionConfig(sc);
        rules = cf.getRules();
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {


        try {
            PrintWriter out = response.getWriter();
            DesignerResponseWrapper wrapper = new DesignerResponseWrapper((HttpServletResponse) response);
            chain.doFilter(request, wrapper);
            if (!(((HttpServletResponse) response).containsHeader("Content-Encoding")) && response.getContentType() != null &&
                    response.getContentType().indexOf("text/html") >= 0) {
                StringBuffer buff = wrapper.getBuffer();
                // workaround for hosted gwt mode
                if(!(((HttpServletRequest) request).getRequestURI().endsWith("hosted.html"))) {
                    String modifiedResponse = processContent(buff, rules);
                    out.write(modifiedResponse);
                } else {
                    out.write(buff.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void destroy() {
        fc = null;
    }

    private String processContent(StringBuffer buffer, InjectionRules rulesToApply) {
        String strResponse = buffer.toString();

        String tagsToInsert = "";
        String modResponse = strResponse;
        String headBeginMarker = "<!-- mg#head#begin#marker -->";
        String bodyBeginMarker = "<!-- mg#body#begin#marker -->";
        boolean headBeginInserted = false;
        boolean bodyBeginInserted = false;

        Iterator<InjectionRule> rulesToApplyIter = rulesToApply.iterator();
        while (rulesToApplyIter.hasNext()) {
            InjectionRule ruleToApply = rulesToApplyIter.next();
            tagsToInsert = ruleToApply.toString();
            switch (ruleToApply.getInsertAt()) {
                case InsertAt.HEAD_BEGIN:
                    if (!headBeginInserted) {
                        if (tagsToInsert != "") {
                            tagsToInsert = "<head>\n" + tagsToInsert
                                    + headBeginMarker;
                            modResponse = findAndReplace(tagsToInsert, modResponse,
                                    "<head>");
                            headBeginInserted = true;
                        }
                    } else {
                        if (tagsToInsert != "") {
                            tagsToInsert = tagsToInsert + headBeginMarker;
                            modResponse = findAndReplace(tagsToInsert, modResponse,
                                    headBeginMarker);
                        }
                    }
                    break;

                case InsertAt.HEAD_END:
                    if (tagsToInsert != "") {
                        tagsToInsert += "</head>";
                        modResponse = findAndReplace(tagsToInsert, modResponse,
                                "</head>");
                    }
                    break;

                case InsertAt.BODY_BEGIN:
                    if (!bodyBeginInserted) {
                        if (tagsToInsert != "") {
                            Pattern p = Pattern.compile("<body[^>]*>",
                                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
                                            | Pattern.DOTALL);
                            Matcher m = p.matcher(modResponse);
                            StringBuffer sb = new StringBuffer();
                            do {
                                if (!m.find())
                                    break;
                                String origBody = m.group();
                                if (origBody == null)
                                    continue;
                                m.appendReplacement(sb, origBody.replaceAll("\\$",
                                        "\\\\\\$")
                                        + "\n" + tagsToInsert + bodyBeginMarker);
                                break;
                            } while (true);
                            m.appendTail(sb);
                            modResponse = sb.toString();
                            bodyBeginInserted = true;
                        }
                    } else {
                        if (tagsToInsert != "") {
                            tagsToInsert = tagsToInsert + bodyBeginMarker;
                            modResponse = findAndReplace(tagsToInsert, modResponse,
                                    bodyBeginMarker);
                        }
                    }
                    break;

                case InsertAt.BODY_END:
                    if (tagsToInsert != "") {
                        tagsToInsert += "\n</body>";
                        modResponse = findAndReplace(tagsToInsert, modResponse,
                                "</body>");
                    }
                    break;
            }
        }
        return modResponse;
    }

    private String findAndReplace(String tagsToInsert, String modResponse,
                                  String strPattern) {
        Pattern p = Pattern.compile(strPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(modResponse);
        modResponse = m.replaceFirst(tagsToInsert);
        return modResponse;
    }

}

