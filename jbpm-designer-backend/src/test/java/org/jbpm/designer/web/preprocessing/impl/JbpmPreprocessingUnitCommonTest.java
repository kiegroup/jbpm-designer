package org.jbpm.designer.web.preprocessing.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class JbpmPreprocessingUnitCommonTest {

    private static final String FILE_NAME = "src/test/resources/designer.configuration";
    private static final String FILE_CONTENT = "application.context=/";

    @Test
    public void testReadFileNullParameter() throws IOException {
        String result = JbpmPreprocessingUnit.readFile(null);
        assertNull(result);
    }

    @Test
    public void testReadFile() throws IOException {
        String result = JbpmPreprocessingUnit.readFile(FILE_NAME);
        assertEquals(FILE_CONTENT + System.getProperty("line.separator"), result);
    }

    @Test
    public void testGetBytesFromFileNullParameter() throws IOException {
        byte[] result = JbpmPreprocessingUnit.getBytesFromFile(null);
        assertNull(result);
    }

    @Test
    public void testGetBytesFromFile() throws  IOException {
        byte[] result = JbpmPreprocessingUnit.getBytesFromFile(new File(FILE_NAME));
        assertEquals(FILE_CONTENT, new String(result));
    }
}
