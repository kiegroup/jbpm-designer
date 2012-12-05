package org.jbpm.designer.repository;

import org.jbpm.designer.repository.filters.FilterByExtension;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class VFSRepositoryDefaultFileSystemTest extends RepositoryBaseTest {

    @Before
    public void setup() {
        new File(REPOSITORY_ROOT).mkdir();
        profile = new JbpmProfileImpl();
        profile.setRepositoryId("vfs");
        profile.setRepositoryRoot(VFS_REPOSITORY_ROOT);
        profile.setRepositoryGlobalDir("/global");
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
    public void testCreateDefaultVFSRepository() {


        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/");
        assertTrue(rootFolderExists);

        Collection<Directory> directories = repository.listDirectories("/");
        assertNotNull(directories);
        assertEquals(0, directories.size());
    }

    @Test
    public void testCreateDirectory() {
        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);

        Directory directoryId = repository.createDirectory("/test");
        assertNotNull(directoryId);
        assertEquals("test", directoryId.getName());
        assertEquals("/", directoryId.getLocation());
        assertNotNull(directoryId.getUniqueId());

        rootFolderExists = repository.directoryExists("/test");
        assertTrue(rootFolderExists);
    }

    @Test
    public void testDirectoryExists() {
        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);

        Directory directoryId = repository.createDirectory("/test");
        assertNotNull(directoryId);
        assertEquals("test", directoryId.getName());
        assertEquals("/", directoryId.getLocation());
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
    public void testListDirectories() {
        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);

        Directory directoryId = repository.createDirectory("/test");
        assertNotNull(directoryId);
        directoryId = repository.createDirectory("/test2");
        assertNotNull(directoryId);
        directoryId = repository.createDirectory("/test3/nested");
        assertNotNull(directoryId);

        rootFolderExists = repository.directoryExists("/test");
        assertTrue(rootFolderExists);

        rootFolderExists = repository.directoryExists("/test2");
        assertTrue(rootFolderExists);

        rootFolderExists = repository.directoryExists("/test3");
        assertTrue(rootFolderExists);

        Collection<Directory> directories = repository.listDirectories("/");
        assertNotNull(directories);
        assertEquals(3, directories.size());

        directories = repository.listDirectories("/test3");
        assertNotNull(directories);
        assertEquals(1, directories.size());
    }

    @Test
    public void testDeleteDirectory() {
        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);

        Directory directoryId = repository.createDirectory("/test");
        assertNotNull(directoryId);

        rootFolderExists = repository.directoryExists("/test");
        assertTrue(rootFolderExists);

        boolean deleted = repository.deleteDirectory("/test", true);
        assertTrue(deleted);

        rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);

    }

    @Test
    public void testDeleteNonEmptyDirectory() {
        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);

        Directory directoryId = repository.createDirectory("/test/nested");
        assertNotNull(directoryId);

        rootFolderExists = repository.directoryExists("/test");
        assertTrue(rootFolderExists);

        rootFolderExists = repository.directoryExists("/test/nested");
        assertTrue(rootFolderExists);

        boolean deleted = repository.deleteDirectory("/test", false);
        assertTrue(deleted);

        rootFolderExists = repository.directoryExists("/test");
        assertFalse(rootFolderExists);

    }

    @Test
    public void testListAsset() {

        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/");
        assertTrue(rootFolderExists);

        Collection<Asset> assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(0, assets.size());

        try {
            new File(REPOSITORY_ROOT + "/" + "test.txt").createNewFile();
            new File(REPOSITORY_ROOT + "/" + "test.png").createNewFile();
        } catch (Exception e) {

        }

        assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(2, assets.size());
    }

    @Test
    public void testListSingleTextAsset() {

        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/");
        assertTrue(rootFolderExists);

        Collection<Asset> assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(0, assets.size());

        try {
            new File(REPOSITORY_ROOT + "/" + "test.txt").createNewFile();
        } catch (Exception e) {

        }

        assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(1, assets.size());
        Asset<String> asset = assets.iterator().next();

        assertEquals("txt", asset.getAssetType());
        assertEquals("test.txt", asset.getFullName());
        assertEquals("test", asset.getName());
        assertEquals("/", asset.getAssetLocation());
    }

    @Test
    public void testListSingleBinaryAsset() {

        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/");
        assertTrue(rootFolderExists);

        Collection<Asset> assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(0, assets.size());

        try {
            new File(REPOSITORY_ROOT + "/" + "test.png").createNewFile();
        } catch (Exception e) {

        }

        assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(1, assets.size());

        Asset<byte[]> asset = assets.iterator().next();

        assertEquals("png", asset.getAssetType());
        assertEquals("test.png", asset.getFullName());
        assertEquals("test", asset.getName());
        assertEquals("/", asset.getAssetLocation());
    }

    @Test
    public void testListNestedSingleTextAsset() {

        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/");
        assertTrue(rootFolderExists);

        Directory directoryId = repository.createDirectory("/test/nested");
        assertNotNull(directoryId);

        Collection<Asset> assets = repository.listAssets("/test/nested");
        assertNotNull(assets);
        assertEquals(0, assets.size());

        try {
            new File(REPOSITORY_ROOT + "/test/nested/" + "test.txt").createNewFile();
        } catch (Exception e) {

        }

        assets = repository.listAssets("/test/nested");
        assertNotNull(assets);
        assertEquals(1, assets.size());
        Asset<String> asset = assets.iterator().next();

        assertEquals("txt", asset.getAssetType());
        assertEquals("test", asset.getName());
        assertEquals("test.txt", asset.getFullName());
        assertEquals("/test/nested", asset.getAssetLocation());
    }

    @Test
    public void testStoreSingleBinaryAsset() throws AssetNotFoundException{

        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/");
        assertTrue(rootFolderExists);

        Collection<Asset> assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(0, assets.size());

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Byte);
        builder.content("simple content".getBytes())
                .type("png")
                .name("test")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        assertNotNull(id);

        Asset<byte[]> asset = repository.loadAsset(id);

        assertEquals("png", asset.getAssetType());
        assertEquals("test", asset.getName());
        assertEquals("test.png", asset.getFullName());
        assertEquals("/", asset.getAssetLocation());
        assertFalse(asset.getAssetContent().length == 0);
    }

    @Test
    public void testStoreSingleTextAsset() throws AssetNotFoundException{

        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/");
        assertTrue(rootFolderExists);

        Collection<Asset> assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(0, assets.size());

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("txt")
                .name("test")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        assertNotNull(id);

        Asset<String> asset = repository.loadAsset(id);

        assertEquals("txt", asset.getAssetType());
        assertEquals("test", asset.getName());
        assertEquals("test.txt", asset.getFullName());
        assertEquals("/", asset.getAssetLocation());
        assertEquals("simple content\n", asset.getAssetContent());
    }

    @Test
    public void testAssetExists() throws AssetNotFoundException{

        Repository repository = new VFSRepository(profile);

        boolean rootFolderExists = repository.directoryExists("/");
        assertTrue(rootFolderExists);

        Collection<Asset> assets = repository.listAssets("/");
        assertNotNull(assets);
        assertEquals(0, assets.size());

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
        Repository repository = new VFSRepository(profile);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/1/2/3/4/5/6");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssetsRecursively("/", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());
    }

    @Test
    public void testUpdateAsset() throws AssetNotFoundException {
        Repository repository = new VFSRepository(profile);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        builder.content("updated content").uniqueId(id);

        id = repository.updateAsset(builder.getAsset());

        foundAsset = repository.listAssetsRecursively("/", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        String content = ((Asset<String>)repository.loadAsset(id)).getAssetContent();
        assertNotNull(content);
        assertEquals("updated content\n", content);
    }

    @Test
    public void testDeleteAsset() throws AssetNotFoundException {
        Repository repository = new VFSRepository(profile);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean assetExistsBeforeDelete = repository.assetExists(id);
        assertTrue(assetExistsBeforeDelete);

        boolean deleted = repository.deleteAsset(id);
        assertTrue(deleted);

        boolean assetExists = repository.assetExists(id);
        assertFalse(assetExists);

    }

    @Test
    public void testDeleteAssetFromPath() throws AssetNotFoundException {
        Repository repository = new VFSRepository(profile);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean assetExistsBeforeDelete = repository.assetExists(id);
        assertTrue(assetExistsBeforeDelete);

        boolean deleted = repository.deleteAssetFromPath("/process.bpmn2");
        assertTrue(deleted);

        boolean assetExists = repository.assetExists(id);
        assertFalse(assetExists);

    }

    @Test
    public void testCopyAsset() throws AssetNotFoundException {
        Repository repository = new VFSRepository(profile);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/source");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean assetExistsBeforeDelete = repository.assetExists(id);
        assertTrue(assetExistsBeforeDelete);

        boolean copied = repository.copyAsset(id, "/target");
        assertTrue(copied);

        foundAsset = repository.listAssets("/target", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean assetExists = repository.assetExists("/target/process.bpmn2");
        assertTrue(assetExists);

    }

    @Test
    public void testMoveAsset() throws AssetNotFoundException {
        Repository repository = new VFSRepository(profile);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/source");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean sourceAssetExists = repository.assetExists(id);
        assertTrue(sourceAssetExists);

        boolean copied = repository.moveAsset(id, "/target", null);
        assertTrue(copied);

        foundAsset = repository.listAssets("/target", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean assetExists = repository.assetExists("/target/process.bpmn2");
        assertTrue(assetExists);

        foundAsset = repository.listAssets("/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(0, foundAsset.size());

        sourceAssetExists = repository.assetExists(id);
        assertFalse(sourceAssetExists);
    }

    @Test
    public void testMoveAndRenameAsset() throws AssetNotFoundException {
        Repository repository = new VFSRepository(profile);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/source");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean sourceAssetExists = repository.assetExists(id);
        assertTrue(sourceAssetExists);

        boolean copied = repository.moveAsset(id, "/target", "renamed.bpmn2");
        assertTrue(copied);

        foundAsset = repository.listAssets("/target", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean assetExists = repository.assetExists("/target/renamed.bpmn2");
        assertTrue(assetExists);

        foundAsset = repository.listAssets("/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(0, foundAsset.size());

        sourceAssetExists = repository.assetExists(id);
        assertFalse(sourceAssetExists);
    }

    @Test
    public void testRenameAsset() throws AssetNotFoundException {
        Repository repository = new VFSRepository(profile);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/source");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean sourceAssetExists = repository.assetExists(id);
        assertTrue(sourceAssetExists);

        boolean copied = repository.moveAsset(id, "/source", "renamed.bpmn2");
        assertTrue(copied);

        foundAsset = repository.listAssets("/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean assetExists = repository.assetExists("/source/renamed.bpmn2");
        assertTrue(assetExists);

        foundAsset = repository.listAssets("/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        sourceAssetExists = repository.assetExists(id);
        assertFalse(sourceAssetExists);
    }

    @Test
    public void testCopyDirectory() throws AssetNotFoundException {
        Repository repository = new VFSRepository(profile);

        Directory sourceDir = repository.createDirectory("/source");

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/source");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean assetExistsBeforeDelete = repository.assetExists(id);
        assertTrue(assetExistsBeforeDelete);

        boolean copied = repository.copyDirectory("/source", "/target");
        assertTrue(copied);

        foundAsset = repository.listAssets("/target/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean assetExists = repository.assetExists("/target/source/process.bpmn2");
        assertTrue(assetExists);

        boolean copiedDirectoryExists = repository.directoryExists("/source");
        assertTrue(copiedDirectoryExists);

    }

    @Test
    public void testMoveDirectory() throws AssetNotFoundException {
        Repository repository = new VFSRepository(profile);

        Directory sourceDir = repository.createDirectory("/source");

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("simple content")
                .type("bpmn2")
                .name("process")
                .location("/source");

        String id = repository.createAsset(builder.getAsset());

        Collection<Asset> foundAsset = repository.listAssets("/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean assetExistsBeforeDelete = repository.assetExists(id);
        assertTrue(assetExistsBeforeDelete);

        boolean copied = repository.moveDirectory("/source", "/target", null);
        assertTrue(copied);

        foundAsset = repository.listAssets("/target/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(1, foundAsset.size());

        boolean assetExists = repository.assetExists("/target/source/process.bpmn2");
        assertTrue(assetExists);

        boolean movedDirectoryExists = repository.directoryExists("/source");
        assertFalse(movedDirectoryExists);

    }

    @Test
    public void testMoveEmptyDirectory() throws AssetNotFoundException {
        Repository repository = new VFSRepository(profile);

        Directory sourceDir = repository.createDirectory("/source");

        boolean directoryExists = repository.directoryExists(sourceDir.getLocation()+sourceDir.getName());
        assertTrue(directoryExists);
        Collection<Asset> foundAsset = repository.listAssets("/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(0, foundAsset.size());


        boolean copied = repository.moveDirectory("/source", "/", "target");
        assertTrue(copied);

        boolean movedDirectoryExists = repository.directoryExists("/source");
        assertFalse(movedDirectoryExists);
        movedDirectoryExists = repository.directoryExists("/target");
        assertTrue(movedDirectoryExists);

        foundAsset = repository.listAssets("/target", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(0, foundAsset.size());
    }

    @Test
    public void testCopyEmptyDirectory() throws AssetNotFoundException {
        Repository repository = new VFSRepository(profile);

        Directory sourceDir = repository.createDirectory("/source");

        boolean directoryExists = repository.directoryExists(sourceDir.getLocation()+sourceDir.getName());
        assertTrue(directoryExists);
        Collection<Asset> foundAsset = repository.listAssets("/source", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(0, foundAsset.size());


        boolean copied = repository.copyDirectory("/source", "/target");
        assertTrue(copied);

        boolean movedDirectoryExists = repository.directoryExists("/source");
        assertTrue(movedDirectoryExists);
        movedDirectoryExists = repository.directoryExists("/target");
        assertTrue(movedDirectoryExists);

        foundAsset = repository.listAssets("/target", new FilterByExtension("bpmn2"));

        assertNotNull(foundAsset);
        assertEquals(0, foundAsset.size());
    }
}
