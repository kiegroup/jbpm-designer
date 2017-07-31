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

package org.jbpm.designer.web.preprocessing.impl;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.*;

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
        assertEquals(FILE_CONTENT + System.lineSeparator(),
                     result);
    }

    @Test
    public void testGetBytesFromFileNullParameter() throws IOException {
        byte[] result = JbpmPreprocessingUnit.getBytesFromFile(null);
        assertNull(result);
    }

    @Test
    public void testGetBytesFromFile() throws IOException {
        byte[] result = JbpmPreprocessingUnit.getBytesFromFile(new File(FILE_NAME));
        assertEquals(FILE_CONTENT,
                     new String(result));
    }
}
