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

import java.util.HashMap;
import java.util.Map;

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestHttpServletResponse;
import org.jbpm.designer.helper.TestServletConfig;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.filters.FilterByExtension;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JbpmServiceRepositoryServletTest extends RepositoryBaseTest {

    private Repository repository;

    private String uniqueId;

    private Map<String, String> params;

    @Before
    public void setup() {
        super.setup();

        repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("samplebpmn2process")
                .location("/defaultPackage");
        uniqueId = repository.createAsset(builder.getAsset());

        params = new HashMap<String, String>();
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testDisplayRepoContent() throws Exception {
        params.put("repourl",
                   getClass().getResource("servicerepo").toURI().toString());
        params.put("profile",
                   "jbpm");
        params.put("action",
                   "display");

        TestHttpServletResponse testResponse = new TestHttpServletResponse();

        JbpmServiceRepositoryServlet jbpmServiceRepositoryServlet = new JbpmServiceRepositoryServlet();
        jbpmServiceRepositoryServlet.setProfile(profile);
        jbpmServiceRepositoryServlet.init(new TestServletConfig(new TestServletContext(repository)));
        jbpmServiceRepositoryServlet.doPost(new TestHttpServletRequest(params),
                                            testResponse);

        String response = new String(testResponse.getContent(),
                                     "UTF-8");
        assertNotNull(response);
        JSONObject json = new JSONObject(response);
        assertNotNull(json);
        assertEquals(4,
                     json.length());
        JSONArray maArray = (JSONArray) json.get("MicrosoftAcademy");
        assertNotNull(maArray);
        assertEquals(9,
                     maArray.length());
        assertEquals("MicrosoftAcademy",
                     maArray.get(0));

        JSONArray syArray = (JSONArray) json.get("SwitchYardService");
        assertNotNull(syArray);
        assertEquals(9,
                     syArray.length());
        assertEquals("SwitchYardService",
                     syArray.get(0));

        JSONArray minimalisticArray = (JSONArray) json.get("Minimalistic");
        assertNotNull(minimalisticArray);
        assertEquals(9,
                     minimalisticArray.length());
        assertEquals("Minimalistic",
                     minimalisticArray.get(0));

        JSONArray rsArray = (JSONArray) json.get("Rewardsystem");
        assertNotNull(rsArray);
        assertEquals(9,
                     rsArray.length());
        assertEquals("Rewardsystem",
                     rsArray.get(0));
    }

    @Test
    public void testDisplayEmptyRepoContent() throws Exception {
        params.put("repourl",
                   getClass().getResource("emptyservicerepo").toURI().toString());
        params.put("profile",
                   "jbpm");
        params.put("action",
                   "display");

        TestHttpServletResponse testResponse = new TestHttpServletResponse();

        JbpmServiceRepositoryServlet jbpmServiceRepositoryServlet = new JbpmServiceRepositoryServlet();
        jbpmServiceRepositoryServlet.setProfile(profile);
        jbpmServiceRepositoryServlet.init(new TestServletConfig(new TestServletContext(repository)));
        jbpmServiceRepositoryServlet.doPost(new TestHttpServletRequest(params),
                                            testResponse);

        assertEquals("false",
                     new String(testResponse.getContent()));
    }

    @Test
    public void testInstallWid() throws Exception {

        params.put("repourl",
                   getClass().getResource("servicerepo").toURI().toString());
        params.put("asset",
                   "Rewardsystem");
        params.put("profile",
                   "jbpm");
        params.put("category",
                   "Rewards");
        params.put("action",
                   "install");
        params.put("uuid",
                   uniqueId);

        JbpmServiceRepositoryServlet jbpmServiceRepositoryServlet = new JbpmServiceRepositoryServlet();
        jbpmServiceRepositoryServlet.setProfile(profile);
        jbpmServiceRepositoryServlet.init(new TestServletConfig(new TestServletContext(repository)));
        jbpmServiceRepositoryServlet.doPost(new TestHttpServletRequest(params),
                                            new TestHttpServletResponse());

        assertEquals(1,
                     repository.listAssetsRecursively("/",
                                                      new FilterByExtension("bpmn2")).size());
        assertEquals(1,
                     repository.listAssetsRecursively("/",
                                                      new FilterByExtension("wid")).size());
        // rewards wid has no icon
        assertEquals(0,
                     repository.listAssetsRecursively("/",
                                                      new FilterByExtension("png")).size());
    }

    @Test
    public void testInstallInvalidWid() throws Exception {

        params.put("repourl",
                   getClass().getResource("servicerepo").toURI().toString());
        params.put("asset",
                   "InvalidService");
        params.put("profile",
                   "jbpm");
        params.put("category",
                   "InvalidServiceCategory");
        params.put("action",
                   "install");
        params.put("uuid",
                   uniqueId);

        JbpmServiceRepositoryServlet jbpmServiceRepositoryServlet = new JbpmServiceRepositoryServlet();
        jbpmServiceRepositoryServlet.setProfile(profile);
        jbpmServiceRepositoryServlet.init(new TestServletConfig(new TestServletContext(repository)));
        jbpmServiceRepositoryServlet.doPost(new TestHttpServletRequest(params),
                                            new TestHttpServletResponse());

        assertEquals(1,
                     repository.listAssetsRecursively("/",
                                                      new FilterByExtension("bpmn2")).size());
        assertEquals(0,
                     repository.listAssetsRecursively("/",
                                                      new FilterByExtension("wid")).size());
        assertEquals(0,
                     repository.listAssetsRecursively("/",
                                                      new FilterByExtension("png")).size());
    }

    @Test
    public void testInstallWidTwice() throws Exception {

        // setup parameters
        params.put("repourl",
                   getClass().getResource("servicerepo").toURI().toString());
        params.put("asset",
                   "MicrosoftAcademy");
        params.put("profile",
                   "jbpm");
        params.put("category",
                   "Search");
        params.put("action",
                   "install");
        params.put("uuid",
                   uniqueId);

        JbpmServiceRepositoryServlet jbpmServiceRepositoryServlet = new JbpmServiceRepositoryServlet();
        jbpmServiceRepositoryServlet.setProfile(profile);
        jbpmServiceRepositoryServlet.init(new TestServletConfig(new TestServletContext(repository)));

        jbpmServiceRepositoryServlet.doPost(new TestHttpServletRequest(params),
                                            new TestHttpServletResponse());
        jbpmServiceRepositoryServlet.doPost(new TestHttpServletRequest(params),
                                            new TestHttpServletResponse());

        assertEquals(1,
                     repository.listAssetsRecursively("/",
                                                      new FilterByExtension("bpmn2")).size());
        assertEquals(1,
                     repository.listAssetsRecursively("/",
                                                      new FilterByExtension("wid")).size());
        assertEquals(1,
                     repository.listAssetsRecursively("/",
                                                      new FilterByExtension("png")).size());
    }

    @Test
    public void testInstallWidEmptyRepository() throws Exception {

        // setup parameters
        params.put("repourl",
                   getClass().getResource("emptyservicerepo").toURI().toString());
        params.put("asset",
                   "MicrosoftAcademy");
        params.put("profile",
                   "jbpm");
        params.put("category",
                   "Search");
        params.put("action",
                   "install");
        params.put("uuid",
                   uniqueId);

        JbpmServiceRepositoryServlet jbpmServiceRepositoryServlet = new JbpmServiceRepositoryServlet();
        jbpmServiceRepositoryServlet.setProfile(profile);
        jbpmServiceRepositoryServlet.init(new TestServletConfig(new TestServletContext(repository)));

        TestHttpServletResponse response = new TestHttpServletResponse();
        jbpmServiceRepositoryServlet.doPost(new TestHttpServletRequest(params),
                                            response);
        assertEquals("false",
                     new String(response.getContent()));
    }
}
