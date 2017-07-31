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

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestHttpServletResponse;
import org.jbpm.designer.repository.UriUtils;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimulationServletTest {

    private JbpmProfileImpl profile;

    @Before
    public void setup() {
        profile = new JbpmProfileImpl();
        profile.setBpsimDisplay("true");
    }

    @Test
    public void testRunSimulationLocalizedNames() throws Exception {

        SimulationServlet simulationServlet = new SimulationServlet();
        simulationServlet.setProfile(profile);

        // Request json is encoded
        String rawJson = readFile("BPSim_i18nNames.json");
        String encodedJson = Base64.encodeBase64String(UriUtils.encode(rawJson.toString()).getBytes("UTF-8"));

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("json",
                   encodedJson);
        params.put("action",
                   SimulationServlet.ACTION_RUNSIMULATION);
        params.put("ppdata",
                   "Email,Log,Rest,WebService");
        params.put("numinstances",
                   "10");
        params.put("interval",
                   "5");
        params.put("intervalUnit",
                   "minutes");

        TestHttpServletResponse response = new TestHttpServletResponse();
        simulationServlet.doPost(new TestHttpServletRequest(params),
                                 response);

        int responseStatus = response.getStatus();
        assertEquals(0,
                     responseStatus);
        // Response json is encoded
        String encodedResponseText = new String(response.getContent());
        assertNotNull(encodedResponseText);
        String responseText = UriUtils.decode(new String(Base64.decodeBase64(encodedResponseText),
                                                         "UTF-8"));
        assertTrue(responseText.contains("BPSim䧦し 脩"));
        assertTrue(responseText.contains("u1䧦し 脩"));
        assertTrue(responseText.contains("u1дэмокритум"));
    }

    @Test
    public void testGetPathInfoLocalizedNames() throws Exception {

        SimulationServlet simulationServlet = new SimulationServlet();
        simulationServlet.setProfile(profile);

        // Request json is encoded
        String rawJson = readFile("BPSim_i18nNames.json");
        String encodedJson = Base64.encodeBase64String(UriUtils.encode(rawJson.toString()).getBytes("UTF-8"));

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("json",
                   encodedJson);
        params.put("action",
                   SimulationServlet.ACTION_GETPATHINFO);
        params.put("ppdata",
                   "Email,Log,Rest,WebService");
        params.put("numinstances",
                   "10");
        params.put("interval",
                   "5");
        params.put("intervalUnit",
                   "minutes");

        TestHttpServletResponse response = new TestHttpServletResponse();
        simulationServlet.doPost(new TestHttpServletRequest(params),
                                 response);

        int responseStatus = response.getStatus();
        assertEquals(0,
                     responseStatus);

        // Response json is not encoded
        String responseText = new String(response.getContent());
        assertNotNull(responseText);
        assertTrue(responseText.contains("Path"));
        assertTrue(responseText.contains("paths"));
    }

    @Test
    public void testLocalizedStartEndTime() throws Exception {

        SimulationServlet simulationServlet = new SimulationServlet();
        simulationServlet.setProfile(profile);

        // Request json is encoded
        String rawJson = readFile("BPSim_i18nNames.json");
        String encodedJson = Base64.encodeBase64String(UriUtils.encode(rawJson.toString()).getBytes("UTF-8"));

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("json",
                   encodedJson);
        params.put("action",
                   SimulationServlet.ACTION_RUNSIMULATION);
        params.put("language",
                   "ru_RU");
        params.put("ppdata",
                   "Email,Log,Rest,WebService");
        params.put("numinstances",
                   "10");
        params.put("interval",
                   "5");
        params.put("intervalUnit",
                   "minutes");
        params.put("simteststarttime",
                   "1464083491796");
        params.put("simtestendtime",
                   "1465776165148");

        TestHttpServletResponse response = new TestHttpServletResponse();
        simulationServlet.doPost(new TestHttpServletRequest(params),
                                 response);

        int responseStatus = response.getStatus();
        assertEquals(0,
                     responseStatus);

        // Response json is encoded
        String encodedResponseText = new String(response.getContent());
        assertNotNull(encodedResponseText);
        String responseText = UriUtils.decode(new String(Base64.decodeBase64(encodedResponseText),
                                                         "UTF-8"));
        assertNotNull(responseText);
        assertTrue(responseText.contains("май") || responseText.contains("мая"));
        assertTrue(responseText.contains("июн"));
    }

    @Test
    public void testNotAbleToFindPathsInProcess() throws Exception {
        SimulationServlet simulationServlet = new SimulationServlet();
        simulationServlet.setProfile(profile);

        // Request json is encoded
        String rawJson = readFile("BPSim_pathfindererror.json");
        String encodedJson = Base64.encodeBase64String(UriUtils.encode(rawJson.toString()).getBytes("UTF-8"));

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("json",
                   encodedJson);
        params.put("action",
                   SimulationServlet.ACTION_GETPATHINFO);
        params.put("ppdata",
                   "Email,Log,Rest,WebService");
        params.put("numinstances",
                   "10");
        params.put("interval",
                   "5");
        params.put("intervalUnit",
                   "minutes");

        TestHttpServletResponse response = new TestHttpServletResponse();
        simulationServlet.doPost(new TestHttpServletRequest(params),
                                 response);

        int responseStatus = response.getStatus();
        assertEquals(500,
                     responseStatus);
    }

    @Test
    public void testRunSimulationOnSendTask() throws Exception {
        SimulationServlet simulationServlet = new SimulationServlet();
        simulationServlet.setProfile(profile);

        // Request json is encoded
        String rawJson = readFile("BPSim_sendtask.json");
        String encodedJson = Base64.encodeBase64String(UriUtils.encode(rawJson.toString()).getBytes("UTF-8"));

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("json",
                   encodedJson);
        params.put("action",
                   SimulationServlet.ACTION_RUNSIMULATION);
        params.put("language",
                   "en_USs");
        params.put("ppdata",
                   "Email,Log,Rest,WebService");
        params.put("numinstances",
                   "10");
        params.put("interval",
                   "5");
        params.put("intervalUnit",
                   "minutes");
        params.put("simteststarttime",
                   "1464083491796");
        params.put("simtestendtime",
                   "1465776165148");

        TestHttpServletResponse response = new TestHttpServletResponse();
        simulationServlet.doPost(new TestHttpServletRequest(params),
                                 response);

        int responseStatus = response.getStatus();
        assertEquals(0,
                     responseStatus);

        // Response json is encoded
        String encodedResponseText = new String(response.getContent());
        assertNotNull(encodedResponseText);
        String responseText = UriUtils.decode(new String(Base64.decodeBase64(encodedResponseText), "UTF-8"));
        assertNotNull(responseText);
        assertTrue(responseText.contains("\"id\":\"sendtasksim\""));
    }

    private String readFile(String fileName) throws Exception {
        URL fileURL = SimulationServletTest.class.getResource(fileName);
        return new String(Files.readAllBytes(Paths.get(fileURL.toURI())));
    }
}
