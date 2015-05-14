package org.jbpm.designer.web.server;

import org.jbpm.designer.repository.RepositoryBaseTest;

/**
 * Note this test relies on external service repository: http://people.redhat.com/tsurdilo/repository/
 * so it will fail when service repository is not accessible
 */
public class JbpmServiceRepositoryServletTest  extends RepositoryBaseTest {

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
    //@Test
    // jenkins seems to no longer can connect to people.redhat.com
//    public void testJbpmServiceRepositoryServlet() throws Exception {
//
//        Repository repository = new VFSRepository(producer.getIoService());
//        ((VFSRepository)repository).setDescriptor(descriptor);
//        profile.setRepository(repository);
//        // setup parameters
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("repourl", "http://people.redhat.com/tsurdilo/repository/");
//        params.put("asset", "Rewardsystem");
//        params.put("profile", "jbpm");
//        params.put("category", "Rewards");
//        params.put("action", "install");
//
//        JbpmServiceRepositoryServlet jbpmServiceRepositoryServlet = new JbpmServiceRepositoryServlet();
//        jbpmServiceRepositoryServlet.setProfile(profile);
//
//        jbpmServiceRepositoryServlet.init(new TestServletConfig(new TestServletContext(repository)));
//
//        jbpmServiceRepositoryServlet.doPost(new TestHttpServletRequest(params), new TestHttpServletResponse());
//
//        Collection<Asset> serviceAssets = repository.listAssets("/global");
//        assertNotNull(serviceAssets);
//        assertEquals(2, serviceAssets.size());
//
//        Asset<String> form = repository.loadAsset(serviceAssets.iterator().next().getUniqueId());
//        assertNotNull(form.getAssetContent());
//    }
}
