package org.jbpm.designer.filter;

import javax.servlet.ServletOutputStream;
import java.io.*;

public class DesignerStream extends ServletOutputStream {
    private StringWriter stringWriter;

    public DesignerStream(StringWriter stringWriter) {
        this.stringWriter = stringWriter;
    }

    public void write(int c) {
        stringWriter.write(c);
    }
}
