package org.jbpm.designer.repository.servlet;

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestHttpServletResponse;
import org.jbpm.designer.helper.TestServletConfig;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.*;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.VFSFileSystemProducer;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AssetServiceServletTest extends RepositoryBaseTest {

    @Before
    public void setup() {
        new File(REPOSITORY_ROOT).mkdir();
        profile = new JbpmProfileImpl();
        profile.setRepositoryId("vfs");
        profile.setRepositoryRoot(VFS_REPOSITORY_ROOT);
        profile.setRepositoryGlobalDir("/global");
        producer = new VFSFileSystemProducer();
        fileSystem = producer.produceFileSystem(profile, new HashMap<String, String>());
    }

    @After
    public void teardown() {
        File repo = new File(REPOSITORY_ROOT);
        if(repo.exists()) {
            deleteFiles(repo);
        }
        repo.delete();
    }
    
    @Test
    public void testCreateAsset() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "createasset");
        params.put("assettype", "bpmn2");
        params.put("assetname", "testprocess");
        params.put("assetlocation", "/defaultPackage");
        params.put("", "");

        boolean processAssetExists = repository.assetExists("/defaultPackage/testprocess.bpmn2");
        assertFalse(processAssetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);

        processAssetExists = repository.assetExists("/defaultPackage/testprocess.bpmn2");
        assertTrue(processAssetExists);
    }

    @Test
    public void testUpdateAsset() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "updateasset");
        params.put("assetid", id);
        params.put("assetcontent", "testprocess");
        params.put("", "");

        boolean processAssetExists = repository.assetExists("/defaultPackage/testprocess.bpmn2");
        assertTrue(processAssetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);

        processAssetExists = repository.assetExists(id);
        assertTrue(processAssetExists);

        Asset<String> processAsset = repository.loadAsset(id);
        assertEquals("testprocess\n", processAsset.getAssetContent());
    }

    @Test
    public void testDeleteAsset() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "deleteasset");
        params.put("assetid", id);
        params.put("", "");

        boolean processAssetExists = repository.assetExists("/defaultPackage/testprocess.bpmn2");
        assertTrue(processAssetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);

        processAssetExists = repository.assetExists(id);
        assertFalse(processAssetExists);
    }

    @Test
    public void testAssetExists() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "existsasset");
        params.put("assetid", id);
        params.put("", "");

        boolean processAssetExists = repository.assetExists("/defaultPackage/testprocess.bpmn2");
        assertTrue(processAssetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        assertTrue(jsonResponse.indexOf("\"answer\":\"true\"") != -1);
    }

    @Test
    public void testAssetDoesnotExists() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "existsasset");
        params.put("assetid", "/defaultPackage/nonexistingprocess.bpmn2");
        params.put("", "");


        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        assertTrue(jsonResponse.indexOf("\"answer\":\"false\"") != -1);
    }

    @Test
    public void testCreateDirectory() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "createdir");
        params.put("assetlocation", "/defaultPackage");
        params.put("", "");
        boolean directoryExits = repository.directoryExists("/defaultPackage");
        assertFalse(directoryExits);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);

        directoryExits = repository.directoryExists("/defaultPackage");
        assertTrue(directoryExits);
    }

    @Test
    public void testDeleteDirectory() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        repository.createDirectory("/defaultPackage");
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "deletedir");
        params.put("assetlocation", "/defaultPackage");
        params.put("", "");
        boolean directoryExits = repository.directoryExists("/defaultPackage");
        assertTrue(directoryExits);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);

        directoryExits = repository.directoryExists("/defaultPackage");
        assertFalse(directoryExits);
    }

    @Test
    public void testDirectoryExists() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        repository.createDirectory("/defaultPackage");
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "existsdir");
        params.put("assetlocation", "/defaultPackage");
        params.put("", "");
        boolean directoryExits = repository.directoryExists("/defaultPackage");
        assertTrue(directoryExits);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        assertTrue(jsonResponse.indexOf("\"answer\":\"true\"") != -1);
    }

    @Test
    public void testDirectoryDoesNotExist() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "existsdir");
        params.put("assetlocation", "/defaultPackage");
        params.put("", "");
        boolean directoryExits = repository.directoryExists("/defaultPackage");
        assertFalse(directoryExits);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        assertTrue(jsonResponse.indexOf("\"answer\":\"false\"") != -1);
    }

    @Test
    public void testListDirectories() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        repository.createDirectory("/defaultPackage");
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "listdirs");
        params.put("assetlocation", "/");
        params.put("", "");

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        System.out.println(jsonResponse);
        assertTrue(jsonResponse.indexOf("\"answer\":[{\"name\":\"defaultPackage\"}]") != -1);
    }

    @Test
    public void testListAssets() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "listassets");
        params.put("assetlocation", "/defaultPackage");
        params.put("", "");
        boolean directoryExits = repository.directoryExists("/defaultPackage");
        assertTrue(directoryExits);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        assertTrue(jsonResponse.indexOf("\"location\":\"/defaultPackage\",\"description\":\"\",\"name\":\"testprocess\",\"owner\":\"\",\"type\":\"bpmn2\",\"fullname\":\"testprocess.bpmn2\"") != -1);
    }


    @Test
    public void testGetAssetSourceById() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "getassetsource");
        params.put("assetid", id);
        params.put("loadoption", "optionbyid");
        boolean assetExists = repository.assetExists(id);
        assertTrue(assetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        assertEquals(jsonResponse, "custom editors content\n");
    }

    @Test
    public void testGetAssetSourceByPath() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "getassetsource");
        params.put("assetlocation", "/defaultPackage/testprocess.bpmn2");
        params.put("loadoption", "optionbypath");
        boolean assetExists = repository.assetExists(id);
        assertTrue(assetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        assertEquals(jsonResponse, "custom editors content\n");
    }

    @Test
    public void testGetAssetInfoById() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "getassetinfo");
        params.put("assetid", id);
        params.put("loadoption", "optionbyid");
        boolean assetExists = repository.assetExists(id);
        assertTrue(assetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        assertTrue(jsonResponse.indexOf("\"location\":\"/defaultPackage\",\"description\":\"\",\"name\":\"testprocess\",\"owner\":\"\",\"type\":\"bpmn2\",\"fullname\":\"testprocess.bpmn2\"") != -1);
    }

    @Test
    public void testGetAssetInfoByPath() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String id = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");
        params.put("action", "getassetinfo");
        params.put("assetlocation", "/defaultPackage/testprocess.bpmn2");
        params.put("loadoption", "optionbypath");
        boolean assetExists = repository.assetExists(id);
        assertTrue(assetExists);

        AssetServiceServlet assetServiceServlet = new AssetServiceServlet();
        assetServiceServlet.setProfile(profile);

        assetServiceServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        assetServiceServlet.doPost(new TestHttpServletRequest(params), response);

        String jsonResponse = new String(response.getContent());
        assertNotNull(jsonResponse);
        assertTrue(jsonResponse.indexOf("\"location\":\"/defaultPackage\",\"description\":\"\",\"name\":\"testprocess\",\"owner\":\"\",\"type\":\"bpmn2\",\"fullname\":\"testprocess.bpmn2\"") != -1);
    }
}
