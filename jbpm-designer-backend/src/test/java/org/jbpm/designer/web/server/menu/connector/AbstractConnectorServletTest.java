package org.jbpm.designer.web.server.menu.connector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class AbstractConnectorServletTest {

    private static final String FILE_NAME = "src/test/resources/designer.configuration";
    private static final String FILE_CONTENT = "application.context=/";

    @Test
    public void testGetBytesFromFileNullParameter() throws IOException {
        byte[] result = AbstractConnectorServlet.getBytesFromFile(null);
        assertNull(result);
    }

    @Test
    public void testGetBytesFromFile() throws  IOException {
        byte[] result = AbstractConnectorServlet.getBytesFromFile(new File(FILE_NAME));
        assertEquals(FILE_CONTENT, new String(result));
    }
}
