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

package org.jbpm.designer.repository.servlet;

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
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AssetServiceServletTest extends RepositoryBaseTest {

    @Before
    public void setup() {
        super.setup();
    }

    @After
    public void teardown() {
        super.teardown();
    }

    @Test
    public void testCreateAsset() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "createasset");
        params.put("assettype",
                   "bpmn2");
        params.put("assetname",
                   "testprocess");
        params.put("assetlocation",
                   "/defaultPackage");
        params.put("",
                   "");

        boolean processAssetExists = repository.assetExists("/defaultPackage/testprocess.bpmn2");
        assertFalse(processAssetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);

        processAssetExists = repository.assetExists("/defaultPackage/testprocess.bpmn2");
        assertTrue(processAssetExists);
    }

    @Test
    public void testMultiByteCommitMessageOnAssetUpdate() throws Exception {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "updateasset");
        params.put("assetid",
                   id);
        params.put("assetcontent",
                   "testprocess");
        params.put("commitmessage",
                   "こんにちは世界");

        boolean processAssetExists = repository.assetExists("/defaultPackage/testprocess.bpmn2");
        assertTrue(processAssetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        JSONObject jsonObject = new JSONObject(jsonResponse);
        assertNotNull(jsonObject);
        assertEquals("こんにちは世界",
                     jsonObject.getString("commitMessage"));
    }

    @Test
    public void testUpdateAsset() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "updateasset");
        params.put("assetid",
                   id);
        params.put("assetcontent",
                   "testprocess");
        params.put("",
                   "");

        boolean processAssetExists = repository.assetExists("/defaultPackage/testprocess.bpmn2");
        assertTrue(processAssetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);

        processAssetExists = repository.assetExists(id);
        assertTrue(processAssetExists);
    }

    @Test
    public void testDeleteAsset() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "deleteasset");
        params.put("assetid",
                   id);
        params.put("",
                   "");

        boolean processAssetExists = repository.assetExists("/defaultPackage/testprocess.bpmn2");
        assertTrue(processAssetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);

        processAssetExists = repository.assetExists(id);
        assertFalse(processAssetExists);
    }

    @Test
    public void testAssetExists() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "existsasset");
        params.put("assetid",
                   id);
        params.put("",
                   "");

        boolean processAssetExists = repository.assetExists("/defaultPackage/testprocess.bpmn2");
        assertTrue(processAssetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertJsonContains(jsonResponse,
                           "\"answer\":\"true\"");
    }

    @Test
    public void testAssetDoesnotExists() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "existsasset");
        params.put("assetid",
                   "/defaultPackage/nonexistingprocess.bpmn2");
        params.put("",
                   "");

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertJsonContains(jsonResponse,
                           "\"answer\":\"false\"");
    }

    @Test
    public void testCreateDirectory() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "createdir");
        params.put("assetlocation",
                   "/defaultPackage");
        params.put("",
                   "");
        boolean directoryExits = repository.directoryExists("/defaultPackage");
        assertFalse(directoryExits);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);

        directoryExits = repository.directoryExists("/defaultPackage");
        assertTrue(directoryExits);
    }

    @Test
    public void testDeleteDirectory() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        repository.createDirectory("/defaultPackage");
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "deletedir");
        params.put("assetlocation",
                   "/defaultPackage");
        params.put("",
                   "");
        boolean directoryExits = repository.directoryExists("/defaultPackage");
        assertTrue(directoryExits);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);

        directoryExits = repository.directoryExists("/defaultPackage");
        assertFalse(directoryExits);
    }

    @Test
    public void testDirectoryExists() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        repository.createDirectory("/defaultPackage");
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "existsdir");
        params.put("assetlocation",
                   "/defaultPackage");
        params.put("",
                   "");
        boolean directoryExits = repository.directoryExists("/defaultPackage");
        assertTrue(directoryExits);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertJsonContains(jsonResponse,
                           "\"answer\":\"true\"");
    }

    @Test
    public void testDirectoryDoesNotExist() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "existsdir");
        params.put("assetlocation",
                   "/defaultPackage");
        params.put("",
                   "");
        boolean directoryExits = repository.directoryExists("/defaultPackage");
        assertFalse(directoryExits);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());

        assertJsonContains(jsonResponse,
                           "\"answer\":\"false\"");
    }

    @Test
    public void testListDirectories() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        repository.createDirectory("/defaultPackage");
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "listdirs");
        params.put("assetlocation",
                   "/");
        params.put("",
                   "");

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertJsonContains(jsonResponse,
                           "\"answer\":[{\"name\":\"defaultPackage\"}]");
    }

    @Test
    public void testListAssets() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "listassets");
        params.put("assetlocation",
                   "/defaultPackage");
        params.put("",
                   "");
        boolean directoryExits = repository.directoryExists("/defaultPackage");
        assertTrue(directoryExits);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        assertJsonContains(jsonResponse,
                           "\"location\":\"/defaultPackage\"");
        assertJsonContains(jsonResponse,
                           "\"description\":\"\"");
        assertJsonContains(jsonResponse,
                           "\"name\":\"testprocess\"");
        assertJsonContains(jsonResponse,
                           "\"owner\":\"\"");
        assertJsonContains(jsonResponse,
                           "\"type\":\"bpmn2\"");
        assertJsonContains(jsonResponse,
                           "\"fullname\":\"testprocess.bpmn2\"");
    }

    @Test
    public void testGetAssetSourceById() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "getassetsource");
        params.put("assetid",
                   id);
        params.put("loadoption",
                   "optionbyid");
        boolean assetExists = repository.assetExists(id);
        assertTrue(assetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        assertEquals(jsonResponse,
                     "custom editors content");
    }

    @Test
    public void testGetAssetSourceByPath() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "getassetsource");
        params.put("assetlocation",
                   "/defaultPackage/testprocess.bpmn2");
        params.put("loadoption",
                   "optionbypath");
        boolean assetExists = repository.assetExists(id);
        assertTrue(assetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertEquals(jsonResponse,
                     "custom editors content");
    }

    @Test
    public void testGetAssetInfoById() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "getassetinfo");
        params.put("assetid",
                   id);
        params.put("loadoption",
                   "optionbyid");
        boolean assetExists = repository.assetExists(id);
        assertTrue(assetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertJsonContains(jsonResponse,
                           "\"location\":\"/defaultPackage\"");
        assertJsonContains(jsonResponse,
                           "\"description\":\"\"");
        assertJsonContains(jsonResponse,
                           "\"name\":\"testprocess\"");
        assertJsonContains(jsonResponse,
                           "\"owner\":\"\"");
        assertJsonContains(jsonResponse,
                           "\"type\":\"bpmn2\"");
        assertJsonContains(jsonResponse,
                           "\"fullname\":\"testprocess.bpmn2\"");
    }

    @Test
    public void testGetAssetInfoByPath() throws Exception {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile",
                   "jbpm");
        params.put("action",
                   "getassetinfo");
        params.put("assetlocation",
                   "/defaultPackage/testprocess.bpmn2");
        params.put("loadoption",
                   "optionbypath");
        boolean assetExists = repository.assetExists(id);
        assertTrue(assetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params),
                                   response);

        String jsonResponse = new String(response.getContent());
        assertJsonContains(jsonResponse,
                           "\"location\":\"/defaultPackage\"");
        assertJsonContains(jsonResponse,
                           "\"description\":\"\"");
        assertJsonContains(jsonResponse,
                           "\"name\":\"testprocess\"");
        assertJsonContains(jsonResponse,
                           "\"owner\":\"\"");
        assertJsonContains(jsonResponse,
                           "\"type\":\"bpmn2\"");
        assertJsonContains(jsonResponse,
                           "\"fullname\":\"testprocess.bpmn2\"");
    }

    private void assertJsonContains(String json,
                                    String expected) {
        assertNotNull("No JSON string specified!",
                      json);
        assertTrue("Expected substring '" + expected + "' not found in JSON string '" + json + "'!",
                   json.contains(expected));
    }
}
