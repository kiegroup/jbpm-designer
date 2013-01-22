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

public class DictionaryServletTest  extends RepositoryBaseTest {

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
    public void testStoreDictionary() throws Exception {
        Repository repository = new VFSRepository(profile);
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("action", "save");
        params.put("profile", "jbpm");
        params.put("dvalue", "this is dictionary");


        DictionaryServlet dictionaryServlet = new DictionaryServlet();
        dictionaryServlet.setProfile(profile);

        dictionaryServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        dictionaryServlet.doPost(new TestHttpServletRequest(params), response);

        String responseText = new String(response.getContent());
        assertNotNull(responseText);
        assertEquals("saved", responseText);

        Collection<Asset> dictionary = repository.listAssets("/global", new FilterByExtension("json"));
        assertNotNull(dictionary);
        assertEquals(1, dictionary.size());

        Asset<String> dictionaryAsset = repository.loadAsset(dictionary.iterator().next().getUniqueId());
        assertNotNull(dictionaryAsset);
        assertEquals("this is dictionary\n", dictionaryAsset.getAssetContent());
    }

    @Test
    public void testLoadDictionary() throws Exception {
        Repository repository = new VFSRepository(profile);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("test dictionary content")
                .type("json")
                .name("processdictionary")
                .location("/global");
        repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("action", "load");
        params.put("profile", "jbpm");


        DictionaryServlet dictionaryServlet = new DictionaryServlet();
        dictionaryServlet.setProfile(profile);

        dictionaryServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        dictionaryServlet.doPost(new TestHttpServletRequest(params), response);

        String dictionaryContent = new String(response.getContent());
        assertNotNull(dictionaryContent);
        assertEquals("test dictionary content\n", dictionaryContent);
    }
}
