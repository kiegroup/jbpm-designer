package org.jbpm.designer.stencilset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;

public class BeautifierTest {

    private static final String FILE_NAME = "src/test/resources/designer.configuration";
    private static final String FILE_CONTENT = "application.context=/";

    @Test
    public void testgetScriptFromFileNullParameter() throws IOException {
        String result = Beautifier.getScriptFromFile(null);
        assertNull(result);
    }

    @Test
    public void testgetScriptFromFile() throws IOException {
        String result = Beautifier.getScriptFromFile(FILE_NAME);
        assertEquals(FILE_CONTENT, result);
    }
}
