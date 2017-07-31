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

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class DesignerResponseWrapper extends HttpServletResponseWrapper {

    private StringWriter stringWriter;
    private InjectionRules rules;

    public DesignerResponseWrapper(HttpServletResponse response) throws Exception {
        super(response);
        stringWriter = new StringWriter();
    }

    public PrintWriter getWriter() {
        return (new PrintWriter(stringWriter));
    }

    public ServletOutputStream getOutputStream() {
        return (new DesignerStream(stringWriter));
    }

    public String toString() {
        return (stringWriter.toString());
    }

    public StringBuffer getBuffer() {
        return (stringWriter.getBuffer());
    }
}
