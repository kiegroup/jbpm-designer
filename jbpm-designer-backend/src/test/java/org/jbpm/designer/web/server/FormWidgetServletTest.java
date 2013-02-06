package org.jbpm.designer.web.server;

import org.jbpm.designer.helper.TestHttpServletRequest;
import org.jbpm.designer.helper.TestHttpServletResponse;
import org.jbpm.designer.helper.TestServletConfig;
import org.jbpm.designer.helper.TestServletContext;
import org.jbpm.designer.repository.Asset;
import org.jbpm.designer.repository.AssetBuilderFactory;
import org.jbpm.designer.repository.Repository;
import org.jbpm.designer.repository.RepositoryBaseTest;
import org.jbpm.designer.repository.impl.AssetBuilder;
import org.jbpm.designer.repository.VFSFileSystemProducer;
import org.jbpm.designer.repository.vfs.VFSRepository;
import org.jbpm.designer.web.profile.impl.JbpmProfileImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FormWidgetServletTest  extends RepositoryBaseTest {

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
    public void testGetFormWidgets() throws Exception {
        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("form widget content")
                .type("fw")
                .name("testformwidget")
                .location("/global");
        repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("action", "getwidgets");
        params.put("profile", "jbpm");


        FormWidgetServlet formWidgetServlet = new FormWidgetServlet();
        formWidgetServlet.setProfile(profile);

        formWidgetServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        formWidgetServlet.doPost(new TestHttpServletRequest(params), response);

        String widgets = new String(response.getContent());
        assertNotNull(widgets);
        assertEquals("{\"testformwidget\":\"testformwidget\"}", widgets);
    }

    @Test
    public void testGetFormWidgetSource() throws Exception {
        Repository repository = new VFSRepository(fileSystem, producer.getIoService(), producer.getActiveFileSystems());
        ((VFSRepository)repository).init();
        profile.setRepository(repository);
        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
        builder.content("form widget content")
                .type("fw")
                .name("testformwidget")
                .location("/global");
        repository.createAsset(builder.getAsset());
        // setup parameters
        Map<String, String> params = new HashMap<String, String>();

        params.put("action", "getwidgetsource");
        params.put("profile", "jbpm");
        params.put("widgetname", "testformwidget");


        FormWidgetServlet formWidgetServlet = new FormWidgetServlet();
        formWidgetServlet.setProfile(profile);

        formWidgetServlet.init(new TestServletConfig(new TestServletContext(repository)));
        TestHttpServletResponse response = new  TestHttpServletResponse();
        formWidgetServlet.doPost(new TestHttpServletRequest(params), response);

        String widgets = new String(response.getContent());
        assertNotNull(widgets);
        assertEquals("form widget content\n", widgets);
    }
}
