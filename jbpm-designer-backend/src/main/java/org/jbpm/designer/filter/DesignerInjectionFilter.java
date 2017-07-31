/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DesignerInjectionFilter implements Filter {

    private FilterConfig fc = null;
    private ServletContext sc = null;
    private InjectionConfig cf;
    private InjectionRules rules;

    public void init(FilterConfig filterConfig) throws ServletException {
        fc = filterConfig;
        sc = fc.getServletContext();
        cf = new InjectionConfig(sc);
        rules = cf.getRules();
    }

    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        try {
            DesignerResponseWrapper wrapper = new DesignerResponseWrapper((HttpServletResponse) response);
            chain.doFilter(request,
                           wrapper);
            if (!(((HttpServletResponse) response).containsHeader("Content-Encoding")) && response.getContentType() != null &&
                    response.getContentType().indexOf("text/html") >= 0) {
                StringBuffer buff = wrapper.getBuffer();
                PrintWriter out = response.getWriter();
                // workaround for hosted gwt mode
                if (!(((HttpServletRequest) request).getRequestURI().endsWith("hosted.html"))) {
                    String modifiedResponse = processContent(buff,
                                                             rules);
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

    protected static String processContent(StringBuffer buffer,
                                           InjectionRules rulesToApply) {
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
            if (tagsToInsert.equals("")) {
                continue;
            }
            switch (ruleToApply.getInsertAt()) {
                case InsertAt.HEAD_BEGIN:
                    if (!headBeginInserted) {
                        tagsToInsert = "<head>\n" + tagsToInsert
                                + headBeginMarker;
                        modResponse = findAndReplace(tagsToInsert,
                                                     modResponse,
                                                     "<head>");
                        headBeginInserted = true;
                    } else {
                        tagsToInsert = tagsToInsert + headBeginMarker;
                        modResponse = findAndReplace(tagsToInsert,
                                                     modResponse,
                                                     headBeginMarker);
                    }
                    break;

                case InsertAt.HEAD_END:
                    tagsToInsert += "</head>";
                    modResponse = findAndReplace(tagsToInsert,
                                                 modResponse,
                                                 "</head>");
                    break;

                case InsertAt.BODY_BEGIN:
                    if (!bodyBeginInserted) {
                        Pattern p = Pattern.compile("<body[^>]*>",
                                                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
                                                            | Pattern.DOTALL);
                        Matcher m = p.matcher(modResponse);
                        StringBuffer sb = new StringBuffer();
                        do {
                            if (!m.find()) {
                                break;
                            }
                            String origBody = m.group();
                            if (origBody == null) {
                                continue;
                            }
                            m.appendReplacement(sb,
                                                origBody.replaceAll("\\$",
                                                                    "\\\\\\$")
                                                        + "\n" + tagsToInsert + bodyBeginMarker);
                            break;
                        } while (true);
                        m.appendTail(sb);
                        modResponse = sb.toString();
                        bodyBeginInserted = true;
                    } else {
                        tagsToInsert = tagsToInsert + bodyBeginMarker;
                        modResponse = findAndReplace(tagsToInsert,
                                                     modResponse,
                                                     bodyBeginMarker);
                    }
                    break;

                case InsertAt.BODY_END:
                    tagsToInsert += "\n</body>";
                    modResponse = findAndReplace(tagsToInsert,
                                                 modResponse,
                                                 "</body>");
                    break;
            }
        }
        return modResponse;
    }

    protected static String findAndReplace(String tagsToInsert,
                                           String modResponse,
                                           String strPattern) {
        Pattern p = Pattern.compile(strPattern,
                                    Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(modResponse);
        modResponse = m.replaceFirst(tagsToInsert);
        return modResponse;
    }
}

