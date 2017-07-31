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

package org.jbpm.designer.web.server;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestHttpServletResponse;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProcessInfoServletTest {

    @Test
    public void testDefaultGatewayConnection() throws Exception {
        ProcessInfoServlet processInfoServlet = new ProcessInfoServlet();
        processInfoServlet.setProfile(new JbpmProfileImpl());

        String rawJson = readFile("defaultgateforgateway.json");
        String encodedJson = Base64.encodeBase64String(UriUtils.encode(rawJson.toString()).getBytes("UTF-8"));

        Map<String, String> params = new HashMap<String, String>();
        params.put("json",
                   encodedJson);
        params.put("profile",
                   "jbpm");
        params.put("ppdata",
                   "Email,Log,Rest,WebService");
        params.put("gatewayid",
                   "_FD098FB2-3615-4C26-A5F6-06D4B453E234");

        TestHttpServletResponse response = new TestHttpServletResponse();
        processInfoServlet.doPost(new TestHttpServletRequest(params),
                                  response);

        int responseStatus = response.getStatus();
        assertEquals(0,
                     responseStatus);

        String responseText = new String(response.getContent());
        assertNotNull(responseText);
        assertTrue(responseText.contains("_460FCE64-6E4C-4BB6-8E1F-8817D01CA8C5"));
        assertTrue(responseText.contains("_385AB844-FFE1-4DCA-9A59-E15CC280F288"));
    }

    private String readFile(String fileName) throws Exception {
        URL fileURL = ProcessInfoServletTest.class.getResource(fileName);
        return FileUtils.readFileToString(new File(fileURL.toURI()));
    }
}
