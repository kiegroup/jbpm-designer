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
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TaskFormsEditorServletTest  extends RepositoryBaseTest {

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
    public void testSaveFormAsset() throws Exception {
        Repository repository = new VFSRepository(profile);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String uniqueId = repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uniqueId);
        params.put("action", "save");
        params.put("profile", "jbpm");
        params.put("taskname", "evaluate");
        params.put("tfvalue", "this is simple task content");

        TaskFormsEditorServlet taskFormsEditorServlet = new TaskFormsEditorServlet();
        taskFormsEditorServlet.setProfile(profile);

        taskFormsEditorServlet.init(new TestServletConfig(new TestServletContext(repository)));

        taskFormsEditorServlet.doPost(new TestHttpServletRequest(params), new TestHttpServletResponse());

        Collection<Asset> forms = repository.listAssets("/defaultPackage", new FilterByExtension("flt"));
        assertNotNull(forms);
        assertEquals(1, forms.size());
        Iterator<Asset> assets = forms.iterator();

        Asset asset1 = assets.next();
        assertEquals("evaluate-taskform", asset1.getName());
        assertEquals("/defaultPackage", asset1.getAssetLocation());

        Asset<String> form1 = repository.loadAsset(asset1.getUniqueId());
        assertNotNull(form1.getAssetContent());
        assertEquals("this is simple task content\n", form1.getAssetContent());

    }

    @Test
    public void testLoadFormAsset() throws Exception {
        Repository repository = new VFSRepository(profile);

        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("bpmn2 content")
                .type("bpmn2")
                .name("testprocess")
                .location("/defaultPackage");
        String uniqueId = repository.createAsset(builder.getAsset());

        AssetBuilder builderForm = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builderForm.content("this is simple task content")
                .type("flt")
                .name("evaluate-taskform")
                .location("/defaultPackage");
        String uniqueIdForm = repository.createAsset(builderForm.getAsset());

        // setup parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("uuid", uniqueId);
        params.put("action", "load");
        params.put("profile", "jbpm");
        params.put("taskname", "evaluate");
        params.put("tfvalue", "this is simple task content");

        TaskFormsEditorServlet taskFormsEditorServlet = new TaskFormsEditorServlet();
        taskFormsEditorServlet.setProfile(profile);

        taskFormsEditorServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new TestHttpServletResponse();
        taskFormsEditorServlet.doPost(new TestHttpServletRequest(params), response);

        String formData =      new String(response.getContent());
        System.out.println(formData);
        assertEquals("this is simple task content\n", formData);
    }
}
