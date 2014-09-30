package org.jbpm.designer.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DesignerResponseWrapper extends HttpServletResponseWrapper {
    private StringWriter stringWriter;
    private InjectionRules rules;

    public DesignerResponseWrapper(HttpServletResponse response) throws Exception {
        super(response);
        stringWriter = new StringWriter();
    }

    public PrintWriter getWriter() {
        return(new PrintWriter(stringWriter));
    }

    public ServletOutputStream getOutputStream() {
        return(new DesignerStream(stringWriter));
    }

    public String toString() {
        return(stringWriter.toString());
    }

    public StringBuffer getBuffer() {
        return(stringWriter.getBuffer());
    }

}
