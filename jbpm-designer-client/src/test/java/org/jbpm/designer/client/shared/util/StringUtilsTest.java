/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.designer.client.shared.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringUtilsTest {

    @Test
    public void testCreateQuotedConstantNull() throws Exception {
        assertNull(StringUtils.createQuotedConstant(null));
    }

    @Test
    public void testCreateQuotedConstantEmpty() throws Exception {
        assertEquals("",
                     StringUtils.createQuotedConstant(""));
    }

    @Test
    public void testCreateQuotedConstantNumber() throws Exception {
        assertEquals("-123",
                     StringUtils.createQuotedConstant("-123"));
    }

    @Test
    public void testCreateQuotedConstant() throws Exception {
        assertEquals("\" abc \"",
                     StringUtils.createQuotedConstant(" abc "));
    }

    @Test
    public void testCreateUnquotedConstantNull() throws Exception {
        assertNull(StringUtils.createUnquotedConstant(null));
    }

    @Test
    public void testCreateUnquotedConstantEmpty() throws Exception {
        assertEquals("",
                     StringUtils.createUnquotedConstant(""));
    }

    @Test
    public void testCreateUnquotedConstantNoAction() throws Exception {
        assertEquals("-123",
                     StringUtils.createUnquotedConstant("-123"));
    }

    @Test
    public void testCreateUnquotedConstant() throws Exception {
        assertEquals(" abc ",
                     StringUtils.createUnquotedConstant("\" abc \""));
    }
}
