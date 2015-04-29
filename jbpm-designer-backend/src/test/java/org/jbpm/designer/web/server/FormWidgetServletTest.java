package org.jbpm.designer.web.server;

import org.jbpm.designer.repository.RepositoryBaseTest;

public class FormWidgetServletTest  extends RepositoryBaseTest {

//    @Before
//    public void setup() {
//        new File(REPOSITORY_ROOT).mkdir();
//        profile = new JbpmProfileImpl();
//        producer = new VFSFileSystemProducer();
//        HashMap<String, String> env = new HashMap<String, String>();
//        env.put("repository.root", VFS_REPOSITORY_ROOT);
//        env.put("repository.globaldir", "/global");
//        descriptor = producer.produceFileSystem(env);
//    }
//
//    @After
//    public void teardown() {
//        File repo = new File(REPOSITORY_ROOT);
//        if(repo.exists()) {
//            deleteFiles(repo);
//        }
//        repo.delete();
//    }
//
//    @Test
//    public void testGetFormWidgets() throws Exception {
//        Repository repository = new VFSRepository(producer.getIoService());
//        ((VFSRepository)repository).setDescriptor(descriptor);
//        profile.setRepository(repository);
//        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
//        builder.content("form widget content")
//                .type("fw")
//                .name("testformwidget")
//                .location("/global");
//        repository.createAsset(builder.getAsset());
//        // setup parameters
//        Map<String, String> params = new HashMap<String, String>();
//
//        params.put("action", "getwidgets");
//        params.put("profile", "jbpm");
//
//
//        FormWidgetServlet formWidgetServlet = new FormWidgetServlet();
//        formWidgetServlet.setProfile(profile);
//
//        formWidgetServlet.init(new TestServletConfig(new TestServletContext(repository)));
//        TestHttpServletResponse response = new  TestHttpServletResponse();
//        formWidgetServlet.doPost(new TestHttpServletRequest(params), response);
//
//        String widgets = new String(response.getContent());
//        assertNotNull(widgets);
//        assertEquals("{\"testformwidget\":\"testformwidget\"}", widgets);
//    }
//
//    @Test
//    public void testGetFormWidgetSource() throws Exception {
//        Repository repository = new VFSRepository(producer.getIoService());
//        ((VFSRepository)repository).setDescriptor(descriptor);
//        profile.setRepository(repository);
//        AssetBuilder builder = AssetBuilderFactory.getAssetBuilder(Asset.AssetType.Text);
//        builder.content("form widget content")
//                .type("fw")
//                .name("testformwidget")
//                .location("/global");
//        repository.createAsset(builder.getAsset());
//        // setup parameters
//        Map<String, String> params = new HashMap<String, String>();
//
//        params.put("action", "getwidgetsource");
//        params.put("profile", "jbpm");
//        params.put("widgetname", "testformwidget");
//
//
//        FormWidgetServlet formWidgetServlet = new FormWidgetServlet();
//        formWidgetServlet.setProfile(profile);
//
//        formWidgetServlet.init(new TestServletConfig(new TestServletContext(repository)));
//        TestHttpServletResponse response = new  TestHttpServletResponse();
//        formWidgetServlet.doPost(new TestHttpServletRequest(params), response);
//
//        String widgets = new String(response.getContent());
//        assertNotNull(widgets);
//        assertEquals("form widget content", widgets);
//    }
}
