package org.jbpm.designer.web.server;

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
import org.jbpm.designer.repository.VFSFileSystemProducer;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TaskFormsServletTest  extends RepositoryBaseTest {

    @Before
    public void setup() {
        new File(REPOSITORY_ROOT).mkdir();
        profile = new JbpmProfileImpl();
        producer = new VFSFileSystemProducer();
        HashMap<String, String> env = new HashMap<String, String>();
        env.put("repository.root", VFS_REPOSITORY_ROOT);
        env.put("repository.globaldir", "/global");
        fileSystem = producer.produceFileSystem(env);
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
    public void testTaskFormServlet() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("hello")
                .location("/defaultPackage");
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uniqueId);
        params.put("json", readFile("src/test/resources/BPMN2-DefaultProcess.json"));
        params.put("profile", "jbpm");
        params.put("ppdata", null);

        TaskFormsServlet taskFormsServlet = new TaskFormsServlet();
        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository)));

        taskFormsServlet.doPost(new TestHttpServletRequest(params), new TestHttpServletResponse());

        Collection<Asset> forms = repository.listAssets("/defaultPackage", new FilterByExtension("ftl"));
        assertNotNull(forms);
        assertEquals(1, forms.size());
        assertEquals("hello-taskform", forms.iterator().next().getName());
        assertEquals("/defaultPackage", forms.iterator().next().getAssetLocation());

        Asset<String> form = repository.loadAsset(forms.iterator().next().getUniqueId());
        assertNotNull(form.getAssetContent());
    }

    @Test
    public void testTaskFormServletWithUserTask() throws Exception {

        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("userTask")
                .location("/defaultPackage");
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uniqueId);
        params.put("json", readFile("src/test/resources/BPMN2-UserTask.json"));
        params.put("profile", "jbpm");
        params.put("ppdata", null);

        TaskFormsServlet taskFormsServlet = new TaskFormsServlet();
        taskFormsServlet.setProfile(profile);

        taskFormsServlet.init(new TestServletConfig(new TestServletContext(repository)));

        taskFormsServlet.doPost(new TestHttpServletRequest(params), new TestHttpServletResponse());

        Collection<Asset> forms = repository.listAssets("/defaultPackage", new FilterByExtension("ftl"));
        assertNotNull(forms);
        assertEquals(2, forms.size());
        Iterator<Asset> assets = forms.iterator();
        Asset asset1 = assets.next();
        assertEquals("evaluate-taskform", asset1.getName());
        assertEquals("/defaultPackage", asset1.getAssetLocation());

        Asset asset2 = assets.next();
        assertEquals("testprocess-taskform", asset2.getName());
        assertEquals("/defaultPackage", asset2.getAssetLocation());

        Asset<String> form1 = repository.loadAsset(asset1.getUniqueId());
        assertNotNull(form1.getAssetContent());
        Asset<String> form2 = repository.loadAsset(asset2.getUniqueId());
        assertNotNull(form2.getAssetContent());
    }

    private String readFile(String pathname) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        Scanner scanner = new Scanner(new File(pathname), "UTF-8");
        String lineSeparator = System.getProperty("line.separator");
        try {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }
}
