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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CustomEditorsServletTest  extends RepositoryBaseTest {

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
    public void testLoadCustomEditors() throws Exception {
        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("custom editors content")
                .type("json")
                .name("customeditors")
                .location("/global");
        repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("profile", "jbpm");


        CustomEditorsServlet customEditorsServlet = new CustomEditorsServlet();
        customEditorsServlet.setProfile(profile);

        customEditorsServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        customEditorsServlet.doPost(new TestHttpServletRequest(params), response);

        String responseText = new String(response.getContent());
        assertNotNull(responseText);
        assertEquals("custom editors content\n", responseText);

        Collection<Asset> dictionary = repository.listAssets("/global", new FilterByExtension("json"));
        assertNotNull(dictionary);
        assertEquals(1, dictionary.size());

        Asset<String> dictionaryAsset = repository.loadAsset(dictionary.iterator().next().getUniqueId());
        assertNotNull(dictionaryAsset);
        assertEquals("custom editors content\n", dictionaryAsset.getAssetContent());
    }
}
