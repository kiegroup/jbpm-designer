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

package org.jbpm.designer.repository;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.jbpm.designer.repository.filters.FilterByExtension;
import org.jbpm.designer.repository.filters.FilterByFileName;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.RepositoryDescriptor;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.NoSuchFileException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VFSRepositoryGitFileSystemTest {

    Logger logger = LoggerFactory.getLogger(VFSRepositoryGitFileSystemTest.class);

    // TODO change it to generic independent path
    private static final String REPOSITORY_ROOT = "designer-playground";
    private static final String VFS_REPOSITORY_ROOT = "git://" + REPOSITORY_ROOT;
    private static final String USERNAME = "guvnorngtestuser1";
    private static final String PASSWORD = "test1234";
    private static final String ORIGIN_URL = "https://github.com/mswiderski/designer-playground.git";
    private static final String FETCH_COMMAND = "?fetch";
    private JbpmProfileImpl profile;

    private static String gitLocalClone = System.getProperty("java.io.tmpdir") + File.separator + "git-repo";
    private static Map<String, String> env = new HashMap<String, String>();

    private static int counter = 0;

    private RepositoryDescriptor descriptor;
    private VFSFileSystemProducer producer;

    @BeforeClass
    public static void prepare() {

        env.put("username",
                USERNAME);
        env.put("password",
                PASSWORD);
        env.put("origin",
                ORIGIN_URL);
        env.put("fetch.cmd",
                FETCH_COMMAND);
        System.setProperty("org.kie.nio.git.dir",
                           gitLocalClone);
    }

    @AfterClass
    public static void cleanup() {
        System.clearProperty("org.kie.nio.git.dir");
    }

    @Before
    public void setup() {
        profile = new JbpmProfileImpl();
        producer = new VFSFileSystemProducer();
        env.put("repository.root",
                VFS_REPOSITORY_ROOT + counter);
        env.put("repository.globaldir",
                "/global");
        descriptor = producer.produceFileSystem(env);
    }

    private void deleteFiles(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteFiles(file);
            }
            file.delete();
        }
    }

    @After
    public void teardown() {
        File repo = new File(gitLocalClone);
        if (repo.exists()) {
            deleteFiles(repo);
        }
        repo.delete();
        repo = new File(".niogit");
        if (repo.exists()) {
            deleteFiles(repo);
        }
        repo.delete();
        counter++;
    }

    @Test
    public void testListDirectories() {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        boolean rootFolderExists = repository.directoryExists("/processes");
        assertTrue(rootFolderExists);

        Collection<Directory> directories = repository.listDirectories("/");
        assertNotNull(directories);
        assertEquals(3,
                     directories.size());

        directories = repository.listDirectories("/processes");
        assertNotNull(directories);
        assertEquals(1,
                     directories.size());
    }

    @Test
    public void testCreateDirectory() {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        boolean rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);

        Directory directoryId = repository.createDirectory("/test");
        assertNotNull(directoryId);
        assertEquals("test",
                     directoryId.getName());
        assertEquals("/",
                     directoryId.getLocation());
        assertNotNull(directoryId.getUniqueId());

        rootFolderExists = repository.directoryExists("/test");
        assertTrue(rootFolderExists);
    }

    @Test
    public void testDirectoryExists() {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        boolean rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);

        Directory directoryId = repository.createDirectory("/test");
        assertNotNull(directoryId);
        assertEquals("test",
                     directoryId.getName());
        assertEquals("/",
                     directoryId.getLocation());
        assertNotNull(directoryId.getUniqueId());

        rootFolderExists = repository.directoryExists("/test");
        assertTrue(rootFolderExists);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder.content("simple content".getBytes())
                .type("png")
                .name("test")
                .location("/test");

        String id = repository.createAsset(builder.getAsset());

        assertNotNull(id);

        boolean assetPathShouldNotExists = repository.directoryExists("/test/test.png");
        assertFalse(assetPathShouldNotExists);
    }

    @Test
    public void testDeleteDirectory() {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        boolean rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);

        Directory directoryId = repository.createDirectory("/test");
        assertNotNull(directoryId);

        rootFolderExists = repository.directoryExists("/test");
        assertTrue(rootFolderExists);

        boolean deleted = repository.deleteDirectory("/test",
                                                     true);
        assertTrue(deleted);

        rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);
    }

    @Test
    public void testDeleteNonEmptyDirectory() {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        boolean rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);

        Directory directoryId = repository.createDirectory("/test/nested");
        assertNotNull(directoryId);

        rootFolderExists = repository.directoryExists("/test");
        assertTrue(rootFolderExists);

        rootFolderExists = repository.directoryExists("/test/nested");
        assertTrue(rootFolderExists);

        boolean deleted = repository.deleteDirectory("/test",
                                                     false);
        assertTrue(deleted);

        rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);
    }

    @Test
    public void testListAsset() {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        boolean rootFolderExists = repository.directoryExists("/processes");
        assertTrue(rootFolderExists);

        Collection<Asset> assets = repository.listAssets("/processes");
        assertNotNull(assets);
        assertEquals(2,
                     assets.size());
    }

    @Test
    public void testListSingleTextAsset() {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        boolean rootFolderExists = repository.directoryExists("/processes");
        assertTrue(rootFolderExists);

        Collection<Asset> assets = repository.listAssets("/processes");
        assertNotNull(assets);
        assertEquals(2,
                     assets.size());
        Asset<String> asset = assets.iterator().next();

        assertEquals("bpmn2",
                     asset.getAssetType());
        assertEquals("BPMN2-ScriptTask.bpmn2",
                     asset.getFullName());
        assertEquals("BPMN2-ScriptTask",
                     asset.getName());
        assertEquals("/processes",
                     asset.getAssetLocation());
    }

    @Test
    public void testListSingleBinaryAsset() {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        boolean rootFolderExists = repository.directoryExists("/images");
        assertTrue(rootFolderExists);

        Collection<Asset> assets = repository.listAssets("/images");
        assertNotNull(assets);
        assertEquals(1,
                     assets.size());
        Asset<String> asset = assets.iterator().next();

        assertEquals("png",
                     asset.getAssetType());
        assertEquals("release-process.png",
                     asset.getFullName());
        assertEquals("release-process",
                     asset.getName());
        assertEquals("/images",
                     asset.getAssetLocation());
    }

    @Test
    public void testListNestedSingleTextAsset() {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        boolean rootFolderExists = repository.directoryExists("/processes/nested");
        assertTrue(rootFolderExists);

        Collection<Asset> assets = repository.listAssets("/processes/nested");
        assertNotNull(assets);
        assertEquals(1,
                     assets.size());
        Asset<String> asset = assets.iterator().next();

        assertEquals("bpmn2",
                     asset.getAssetType());
        assertEquals("BPMN2-UserTask.bpmn2",
                     asset.getFullName());
        assertEquals("BPMN2-UserTask",
                     asset.getName());
        assertEquals("/processes/nested",
                     asset.getAssetLocation());
    }

    @Test
    public void testLoadAssetFromPath() throws NoSuchFileException {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        Asset<String> asset = repository.loadAssetFromPath("/processes/BPMN2-ScriptTask.bpmn2");

        assertEquals("bpmn2",
                     asset.getAssetType());
        assertEquals("BPMN2-ScriptTask",
                     asset.getName());
        assertEquals("BPMN2-ScriptTask.bpmn2",
                     asset.getFullName());
        assertEquals("/processes",
                     asset.getAssetLocation());
        assertNotNull(asset.getAssetContent());
        System.out.print(asset.getUniqueId());
    }

    @Test
    public void testStoreSingleBinaryAsset() throws NoSuchFileException {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        Collection<Asset> assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(0,
                     assets.size());

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder.content("simple content".getBytes())
                .type("png")
                .name("test")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        assertNotNull(id);

        Asset<byte[]> asset = repository.loadAsset(id);

        assertEquals("png",
                     asset.getAssetType());
        assertEquals("test",
                     asset.getName());
        assertEquals("test.png",
                     asset.getFullName());
        assertEquals("/",
                     asset.getAssetLocation());
        assertFalse(asset.getAssetContent().length == 0);
    }

    @Test
    public void testStoreSingleBinaryAssetSpaceInName() throws NoSuchFileException {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        Collection<Asset> assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(0,
                     assets.size());

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder.content("simple content".getBytes())
                .type("png")
                .name("test asset")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        assertNotNull(id);

        Asset<byte[]> asset = repository.loadAsset(id);

        assertEquals("png",
                     asset.getAssetType());
        assertEquals("test asset",
                     asset.getName());
        assertEquals("test asset.png",
                     asset.getFullName());
        assertEquals("/",
                     asset.getAssetLocation());
        assertFalse(asset.getAssetContent().length == 0);
    }

    @Test
    public void testStoreSingleTextAsset() throws NoSuchFileException {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        Collection<Asset> assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(0,
                     assets.size());

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("txt")
                .name("test")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        assertNotNull(id);

        Asset<String> asset = repository.loadAsset(id);

        assertEquals("txt",
                     asset.getAssetType());
        assertEquals("test",
                     asset.getName());
        assertEquals("test.txt",
                     asset.getFullName());
        assertEquals("/",
                     asset.getAssetLocation());
        assertEquals("simple content",
                     asset.getAssetContent());
    }

    @Test
    public void testStoreSingleUnicodeTextAsset() throws NoSuchFileException {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        Collection<Asset> assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(0,
                     assets.size());

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("精算 精算 精算")
                .type("txt")
                .name("test")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        assertNotNull(id);

        Asset<String> asset = repository.loadAsset(id);

        assertEquals("txt",
                     asset.getAssetType());
        assertEquals("test",
                     asset.getName());
        assertEquals("test.txt",
                     asset.getFullName());
        assertEquals("/",
                     asset.getAssetLocation());
        assertEquals("精算 精算 精算",
                     asset.getAssetContent());
    }

    @Test
    public void testAssetExists() throws NoSuchFileException {

        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        Collection<Asset> assets = repository.listAssets("/");
        assertNotNull(assets);
        for (Asset aset : assets) {
            logger.debug(aset.getAssetLocation() + " " + aset.getFullName());
        }
        assertEquals(0,
                     assets.size());

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("txt")
                .name("test")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        assertNotNull(id);

        boolean assetExists = repository.assetExists(id);
        assertTrue(assetExists);
    }

    @Test
    public void testListAssetsRecursively() {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/1/2/3/4/5/6");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssetsRecursively("/",
                                                                        new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(4,
                     foundAsset.size());
    }

    @Test
    public void testUpdateAsset() throws NoSuchFileException {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/",
                                                             new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        builder.content("updated content").uniqueId(id);

        id = repository.updateAsset(builder.getAsset(),
                                    "",
                                    "");

        foundAsset = repository.listAssetsRecursively("/",
                                                      new FilterByFileName("process.bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        String content = ((Asset<String>) repository.loadAsset(id)).getAssetContent();
        assertNotNull(content);
        assertEquals("updated content",
                     content);
    }

    @Test
    public void testUpdateUnicodeTextAsset() throws NoSuchFileException {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("精算 精算 精算")
                .type("txt")
                .name("test")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/",
                                                             new FilterByExtension("txt"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        builder.content("精算 精算 精算 精算 精算 精算").uniqueId(id);

        id = repository.updateAsset(builder.getAsset(),
                                    "",
                                    "");

        foundAsset = repository.listAssetsRecursively("/",
                                                      new FilterByFileName("test.txt"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        String content = ((Asset<String>) repository.loadAsset(id)).getAssetContent();
        assertNotNull(content);
        assertEquals("精算 精算 精算 精算 精算 精算",
                     content);
    }

    @Test
    public void testDeleteAsset() throws NoSuchFileException {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/",
                                                             new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean assetExistsBeforeDelete = repository.assetExists(id);
        assertTrue(assetExistsBeforeDelete);

        boolean deleted = repository.deleteAsset(id);
        assertTrue(deleted);

        boolean assetExists = repository.assetExists(id);
        assertFalse(assetExists);
    }

    @Test
    public void testDeleteAssetFromPath() throws NoSuchFileException {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/",
                                                             new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean assetExistsBeforeDelete = repository.assetExists(id);
        assertTrue(assetExistsBeforeDelete);

        boolean deleted = repository.deleteAssetFromPath("/process.bpmn2");
        assertTrue(deleted);

        boolean assetExists = repository.assetExists(id);
        assertFalse(assetExists);
    }

    @Test
    public void testCopyAsset() throws NoSuchFileException {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/source");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/source",
                                                             new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean assetExistsBeforeDelete = repository.assetExists(id);
        assertTrue(assetExistsBeforeDelete);

        boolean copied = repository.copyAsset(id,
                                              "/target");
        assertTrue(copied);

        foundAsset = repository.listAssets("/target",
                                           new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean assetExists = repository.assetExists("/target/process.bpmn2");
        assertTrue(assetExists);
    }

    @Test
    public void testMoveAsset() throws NoSuchFileException {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/source");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/source",
                                                             new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean sourceAssetExists = repository.assetExists(id);
        assertTrue(sourceAssetExists);

        boolean copied = repository.moveAsset(id,
                                              "/target",
                                              null);
        assertTrue(copied);

        foundAsset = repository.listAssets("/target",
                                           new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean assetExists = repository.assetExists("/target/process.bpmn2");
        assertTrue(assetExists);

        foundAsset = repository.listAssets("/source",
                                           new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(0,
                     foundAsset.size());

        sourceAssetExists = repository.assetExists(id);
        assertFalse(sourceAssetExists);
    }

    @Test
    public void testMoveAndRenameAsset() throws NoSuchFileException {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/source");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/source",
                                                             new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean sourceAssetExists = repository.assetExists(id);
        assertTrue(sourceAssetExists);

        boolean copied = repository.moveAsset(id,
                                              "/target",
                                              "renamed.bpmn2");
        assertTrue(copied);

        foundAsset = repository.listAssets("/target",
                                           new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean assetExists = repository.assetExists("/target/renamed.bpmn2");
        assertTrue(assetExists);

        foundAsset = repository.listAssets("/source",
                                           new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(0,
                     foundAsset.size());

        sourceAssetExists = repository.assetExists(id);
        assertFalse(sourceAssetExists);
    }

    @Test
    public void testRenameAsset() throws NoSuchFileException {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/source");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/source",
                                                             new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean sourceAssetExists = repository.assetExists(id);
        assertTrue(sourceAssetExists);

        boolean copied = repository.moveAsset(id,
                                              "/source",
                                              "renamed.bpmn2");
        assertTrue(copied);

        foundAsset = repository.listAssets("/source",
                                           new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean assetExists = repository.assetExists("/source/renamed.bpmn2");
        assertTrue(assetExists);

        foundAsset = repository.listAssets("/source",
                                           new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        sourceAssetExists = repository.assetExists(id);
        assertFalse(sourceAssetExists);
    }

    @Test
    public void testCopyDirectory() throws NoSuchFileException {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        Directory sourceDir = repository.createDirectory("/source");
        Directory targetDir = repository.createDirectory("/target");

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simpleContent")
                .type("bpmn2")
                .name("process")
                .location("/source");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/source",
                                                             new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean assetExistsBeforeDelete = repository.assetExists(id);
        assertTrue(assetExistsBeforeDelete);

        boolean copied = repository.copyDirectory("/source",
                                                  "/target");
        assertTrue(copied);

        foundAsset = repository.listAssets("/target/source",
                                           new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean assetExists = repository.assetExists("/target/source/process.bpmn2");
        assertTrue(assetExists);

        boolean copiedDirectoryExists = repository.directoryExists("/source");
        assertTrue(copiedDirectoryExists);
    }

    @Test
    public void testMoveDirectory() throws NoSuchFileException {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        Directory sourceDir = repository.createDirectory("/source");
        Directory targetDir = repository.createDirectory("/target");

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/source");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/source",
                                                             new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean assetExistsBeforeDelete = repository.assetExists(id);
        assertTrue(assetExistsBeforeDelete);

        boolean copied = repository.moveDirectory("/source",
                                                  "/target",
                                                  null);
        assertTrue(copied);

        foundAsset = repository.listAssets("/target/source",
                                           new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1,
                     foundAsset.size());

        boolean assetExists = repository.assetExists("/target/source/process.bpmn2");
        assertTrue(assetExists);

        boolean movedDirectoryExists = repository.directoryExists("/source");
        assertFalse(movedDirectoryExists);
    }

    @Test
    public void testMoveEmptyDirectory() throws NoSuchFileException {
        Repository repository = new VFSRepository(producer.getIoService());
        ((VFSRepository) repository).setDescriptor(descriptor);
        Directory sourceDir = repository.createDirectory("/source");
        Directory targetDir = repository.createDirectory("/target");

        boolean directoryExists = repository.directoryExists(sourceDir.getLocation() + sourceDir.getName());
        assertTrue(directoryExists);
        Collection<Asset> foundAsset = repository.listAssets("/source",
                                                             new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(0,
                     foundAsset.size());

        boolean copied = repository.moveDirectory("/source",
                                                  "/",
                                                  "target");
        assertTrue(copied);

        boolean movedDirectoryExists = repository.directoryExists("/source");
        assertFalse(movedDirectoryExists);
        movedDirectoryExists = repository.directoryExists("/target");
        assertTrue(movedDirectoryExists);

        foundAsset = repository.listAssets("/target",
                                           new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(0,
                     foundAsset.size());
    }

    @Test
    public void testCreateGlobalDirOnNewProject() throws FileAlreadyExistsException {
        VFSRepository repository = new VFSRepository(producer.getIoService());
        repository.setDescriptor(descriptor);

        Directory testProjectDir = repository.createDirectory("/mytestproject");

        final KieProject mockProject = mock(KieProject.class);
        when(mockProject.getRootPath()).thenReturn(Paths.convert(producer.getIoService().get(URI.create(decodeUniqueId(testProjectDir.getUniqueId())))));

        NewProjectEvent event = mock(NewProjectEvent.class);
        when(event.getProject()).thenReturn(mockProject);

        repository.createGlobalDirOnNewProject(event);

        boolean globalDirectoryExists = repository.directoryExists("/mytestproject/global");
        assertTrue(globalDirectoryExists);

        Collection<Asset> foundFormTemplates = repository.listAssets("/mytestproject/global",
                                                                     new FilterByExtension("fw"));
        assertNotNull(foundFormTemplates);
        assertEquals(0,
                     foundFormTemplates.size());

        Collection<Asset> foundJSONAssets = repository.listAssets("/mytestproject/global",
                                                                  new FilterByExtension("json"));
        assertNotNull(foundJSONAssets);
        assertEquals(3,
                     foundJSONAssets.size());

        Collection<Asset> foundWidAssets = repository.listAssets("/mytestproject",
                                                                 new FilterByExtension("wid"));
        assertNotNull(foundWidAssets);
        assertEquals(0,
                     foundWidAssets.size());

        // call again to try to trigger FileAlreadyExistsException
        repository.createGlobalDirOnNewProject(event);

        boolean globalDirectoryStillExists = repository.directoryExists("/mytestproject/global");
        assertTrue(globalDirectoryStillExists);

        // no new files or copies were added
        Collection<Asset> foundFormTemplatesAfterSecondCall = repository.listAssets("/mytestproject/global",
                                                                                    new FilterByExtension("fw"));
        assertNotNull(foundFormTemplatesAfterSecondCall);
        assertEquals(0,
                     foundFormTemplatesAfterSecondCall.size());

        Collection<Asset> foundJSONAssetsAfterSecondCall = repository.listAssets("/mytestproject/global",
                                                                                 new FilterByExtension("json"));
        assertNotNull(foundJSONAssetsAfterSecondCall);
        assertEquals(3,
                     foundJSONAssetsAfterSecondCall.size());

        Collection<Asset> foundWidAssetsAfterSecondCall = repository.listAssets("/mytestproject",
                                                                                new FilterByExtension("wid"));
        assertNotNull(foundWidAssetsAfterSecondCall);
        assertEquals(0,
                     foundWidAssetsAfterSecondCall.size());
    }

    private String decodeUniqueId(String uniqueId) {
        if (Base64.isBase64(uniqueId)) {
            byte[] decoded = Base64.decodeBase64(uniqueId);
            try {
                String uri = new String(decoded,
                                        "UTF-8");

                return UriUtils.encode(uri);
            } catch (UnsupportedEncodingException e) {

            }
        }

        return UriUtils.encode(uniqueId);
    }
}
